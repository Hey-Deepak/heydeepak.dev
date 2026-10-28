#!/usr/bin/env python3
"""Site checks for the exported heydeepak.dev: forbidden/private text, required text, links, PDF."""
from __future__ import annotations

import argparse
import json
import re
import shutil
import subprocess
import sys
import urllib.error
import urllib.request
from html.parser import HTMLParser
from pathlib import Path
from urllib.parse import urlparse

HERE = Path(__file__).resolve().parent
PRIVATE_PATTERNS = Path.home() / ".buddy" / "career" / ".private-patterns"
SAME_SITE_HOSTS = {"heydeepak.dev", "www.heydeepak.dev", "localhost", "127.0.0.1"}
BOT_BLOCKED_HOSTS = ("linkedin.com", "medium.com", "twitter.com", "x.com", "instagram.com",
                     "facebook.com", "naukri.com", "indeed.com")
USER_AGENT = ("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 "
              "(KHTML, like Gecko) Chrome/124.0 Safari/537.36")
LINK_TIMEOUT_SECONDS = 15
BLOCK_TAGS = {"p", "div", "li", "ul", "ol", "br", "section", "article", "header", "footer",
              "nav", "main", "h1", "h2", "h3", "h4", "h5", "h6", "tr", "td", "th", "table"}


def _compile(patterns: list[str]) -> list[re.Pattern]:
    return [re.compile(p, re.IGNORECASE) for p in patterns]


PHONE = _compile([r"(?<![\d.])(?:\+91[\s-]?)?[6-9](?:[\s-]?\d){9}(?!\d)"])
PAY = _compile([r"\b(LPA|lakhs?|CTC|salary|per annum)\b"])
JOB_SEEKING = _compile([
    r"open to\b[^.]{0,30}\b(work|roles?|opportunit\w*|new)\b",
    r"looking for (a )?(new )?(role|job|opportunit)",
    r"available for hire",
    r"actively (looking|seeking)",
    r"notice period",
])
BUILT_IN = PHONE + PAY + JOB_SEEKING


def load_patterns(path: Path) -> list[re.Pattern]:
    if not path.is_file():
        return []
    lines = (line.strip() for line in path.read_text(encoding="utf-8").splitlines())
    return _compile([line for line in lines if line and not line.startswith("#")])


class _TextParser(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.parts: list[str] = []
        self._skip = 0

    def handle_starttag(self, tag, attrs):
        if tag in ("script", "style"):
            self._skip += 1
        elif tag in BLOCK_TAGS:
            self.parts.append(" ")

    def handle_endtag(self, tag):
        if tag in ("script", "style"):
            self._skip = max(0, self._skip - 1)
        elif tag in BLOCK_TAGS:
            self.parts.append(" ")

    def handle_data(self, data):
        if not self._skip:
            self.parts.append(data)


def page_text(html: str) -> str:
    parser = _TextParser()
    parser.feed(html)
    parser.close()
    return re.sub(r"\s+", " ", "".join(parser.parts)).strip()


def find_forbidden(text: str, patterns: list[re.Pattern]) -> list[str]:
    return [m.group(0) for p in patterns for m in p.finditer(text)]


def missing_required(text: str, required: list[str]) -> list[str]:
    return [r for r in required if r not in text]


def link_ok(url: str, status: int) -> bool | None:
    if status == 404 or status == 410:
        return False
    host = (urlparse(url).hostname or "").lower()
    blocked = any(host == h or host.endswith("." + h) for h in BOT_BLOCKED_HOSTS)
    if status == 999 or (blocked and status >= 400):
        return None
    return status < 400


def json_ld_person(html: str) -> dict | None:
    for m in re.finditer(r'<script[^>]*type=["\']application/ld\+json["\'][^>]*>(.*?)</script>',
                         html, re.DOTALL | re.IGNORECASE):
        try:
            data = json.loads(m.group(1))
        except ValueError:
            continue
        nodes = data if isinstance(data, list) else data.get("@graph", [data]) if isinstance(data, dict) else []
        for node in nodes:
            if isinstance(node, dict) and node.get("@type") == "Person":
                return node
    return None


class _AttrParser(HTMLParser):
    def __init__(self):
        super().__init__(convert_charrefs=True)
        self.values: list[str] = []
        self._ld = False

    def handle_starttag(self, tag, attrs):
        self.values += [v for _, v in attrs if v]
        self._ld = tag == "script" and dict(attrs).get("type", "").lower() == "application/ld+json"

    def handle_endtag(self, tag):
        self._ld = False

    def handle_data(self, data):
        if self._ld:
            self.values.append(data)


def attribute_text(html: str) -> str:
    parser = _AttrParser()
    parser.feed(html)
    parser.close()
    return "\n".join(parser.values)


class _LinkParser(HTMLParser):
    def __init__(self):
        super().__init__()
        self.hrefs: list[str] = []

    def handle_starttag(self, tag, attrs):
        href = dict(attrs).get("href")
        if tag == "a" and href:
            self.hrefs.append(href)


def external_links(html: str) -> list[str]:
    parser = _LinkParser()
    parser.feed(html)
    return [h for h in parser.hrefs
            if h.startswith(("http://", "https://")) and (urlparse(h).hostname or "").lower() not in SAME_SITE_HOSTS]


def _fetch_status(url: str, method: str) -> int:
    req = urllib.request.Request(url, method=method, headers={"User-Agent": USER_AGENT})
    try:
        with urllib.request.urlopen(req, timeout=LINK_TIMEOUT_SECONDS) as resp:
            return resp.status
    except urllib.error.HTTPError as e:
        return e.code


def check_link(url: str) -> tuple[bool | None, str]:
    try:
        status = _fetch_status(url, "HEAD")
        if status >= 400:
            status = _fetch_status(url, "GET")
    except Exception as e:  # network/DNS/timeout
        try:
            status = _fetch_status(url, "GET")
        except Exception as e2:
            return False, type(e2).__name__ or type(e).__name__
    return link_ok(url, status), str(status)


def pdf_text(pdf: Path) -> tuple[str | None, str]:
    exe = shutil.which("pdftotext") or ("/opt/homebrew/bin/pdftotext" if Path("/opt/homebrew/bin/pdftotext").exists() else None)
    if not exe:
        return None, "pdftotext not found"
    if not pdf.is_file():
        return None, f"{pdf} does not exist"
    proc = subprocess.run([exe, str(pdf), "-"], capture_output=True, text=True)
    if proc.returncode != 0:
        return None, f"pdftotext failed on {pdf}"
    return re.sub(r"\s+", " ", proc.stdout), ""


SEO_FILES = ("robots.txt", "sitemap.xml", "og.png", "404.html", "photo.jpg")
CANONICAL_HREF = "https://heydeepak.dev/"
FOOTER_DATE = re.compile(r"Last updated (\d{4}-\d{2}-\d{2})")
SITEMAP_LASTMOD = re.compile(r"<lastmod>\s*([^<\s]+)\s*</lastmod>")


class _CanonicalParser(HTMLParser):
    def __init__(self):
        super().__init__()
        self.hrefs: list[str] = []

    def handle_starttag(self, tag, attrs):
        a = dict(attrs)
        if tag == "link" and a.get("rel") == "canonical" and a.get("href"):
            self.hrefs.append(a["href"])


def canonical_hrefs(html: str) -> list[str]:
    parser = _CanonicalParser()
    parser.feed(html)
    return parser.hrefs


def missing_seo(site_dir: Path) -> list[str]:
    """Problems with the sharing/search surface: files, canonical, Person JSON-LD, robots, sitemap."""
    problems = [f"missing {name}" for name in SEO_FILES if not (site_dir / name).is_file()]
    index = site_dir / "index.html"
    html = index.read_text(encoding="utf-8", errors="replace") if index.is_file() else ""
    if CANONICAL_HREF not in canonical_hrefs(html):
        problems.append("missing canonical")
    if json_ld_person(html) is None:
        problems.append("missing json-ld Person")
    robots = site_dir / "robots.txt"
    if robots.is_file() and "Disallow: /resume.pdf" not in robots.read_text(encoding="utf-8").splitlines():
        problems.append("robots: resume.pdf not disallowed")
    sitemap = site_dir / "sitemap.xml"
    if sitemap.is_file():
        lastmod = SITEMAP_LASTMOD.search(sitemap.read_text(encoding="utf-8"))
        footer = FOOTER_DATE.search(page_text(html))
        # The sitemap is a static file, so this keeps its date from drifting from the footer.
        if not lastmod or not footer or lastmod.group(1) != footer.group(1):
            problems.append("sitemap: lastmod != footer date")
    return problems


def _read_lines(path: Path) -> list[str]:
    if not path.is_file():
        return []
    return [l.strip() for l in path.read_text(encoding="utf-8").splitlines() if l.strip()]


def main(argv: list[str]) -> int:
    ap = argparse.ArgumentParser(description=__doc__)
    ap.add_argument("site_dir", type=Path)
    ap.add_argument("--links", action="store_true")
    ap.add_argument("--pdf", type=Path)
    ap.add_argument("--require-file", action="append", default=[])
    ap.add_argument("--extra-patterns", type=Path, action="append", default=[])
    args = ap.parse_args(argv)

    fails: list[str] = []
    site = args.site_dir
    pages = sorted(site.rglob("*.html")) if site.is_dir() else []
    if not pages:
        fails.append(f"FAIL file: no .html pages in {site}")

    forbidden = BUILT_IN + load_patterns(HERE / "forbidden-text.txt")
    private = load_patterns(PRIVATE_PATTERNS)
    for extra in args.extra_patterns:
        private += load_patterns(extra)

    links: set[str] = set()
    for page in pages:
        name = str(page.relative_to(site))
        html = page.read_text(encoding="utf-8", errors="replace")
        text = page_text(html)
        for snippet in sorted(set(find_forbidden(text, forbidden))):
            fails.append(f"FAIL forbidden: {name}: {snippet}")
        if find_forbidden(text, private):
            fails.append(f"FAIL private: {name}")
        attrs = attribute_text(html)
        for snippet in sorted(set(find_forbidden(attrs, PHONE + PAY))):
            fails.append(f"FAIL forbidden: {name} attribute: {snippet}")
        if find_forbidden(attrs, private):
            fails.append(f"FAIL private: {name} attribute")
        links.update(external_links(html))

    index = site / "index.html"
    if index.is_file():
        text = page_text(index.read_text(encoding="utf-8", errors="replace"))
        for req in missing_required(text, _read_lines(HERE / "required-text.txt")):
            fails.append(f"FAIL required: index.html is missing: {req}")
    elif pages:
        fails.append("FAIL file: index.html")

    fails += [f"FAIL seo: {problem}" for problem in missing_seo(site)]

    for name in args.require_file:
        if not (site / name).is_file():
            fails.append(f"FAIL file: {name}")

    if args.pdf:
        text, err = pdf_text(args.pdf)
        if text is None:
            fails.append(f"FAIL pdf: {err}")
        else:
            for snippet in sorted(set(find_forbidden(text, PAY))):
                fails.append(f"FAIL pdf: {args.pdf.name}: {snippet}")
            if find_forbidden(text, private):
                fails.append(f"FAIL private: {args.pdf.name}")

    if args.links:
        for url in sorted(links):
            ok, status = check_link(url)
            if ok is None:
                print(f"UNVERIFIED link: {url} ({status})")
            elif not ok:
                fails.append(f"FAIL link: {url} ({status})")

    for line in fails:
        print(line)
    return 1 if fails else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv[1:]))
