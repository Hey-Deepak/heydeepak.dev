import contextlib
import io
import tempfile
import unittest
import urllib.error
from pathlib import Path
from unittest import mock

from check_site import (BUILT_IN, attribute_texts, canonical_hrefs, check_link, forbidden_patterns, main,
                        find_forbidden, json_ld_person, link_ok, load_patterns, missing_required, missing_seo,
                        page_problems, page_text)


class CheckSiteTest(unittest.TestCase):
    def test_phone_number_is_forbidden(self):
        self.assertTrue(find_forbidden("call +91 9876543210", BUILT_IN))

    def test_spaced_and_dashed_phone_numbers_are_forbidden(self):
        for s in ["+91 98765 43210", "+91-98765-43210", "98765-43210", "9876543210"]:
            self.assertTrue(find_forbidden(s, BUILT_IN), s)

    def test_trunk_prefixed_phone_numbers_are_forbidden(self):
        for s in ["09876543210", "0 98765 43210", "0-98765-43210"]:
            self.assertTrue(find_forbidden(s, BUILT_IN), s)

    def test_no_phone_false_positives(self):
        for s in ["2020.09", "1.0 → 3.0", "13.6k", "100,000+", "2017–2021", "1234567890123",
                  "id 7123456789012", "22 releases in 11 months"]:
            self.assertEqual(find_forbidden(s, BUILT_IN), [], s)

    def _run_on_page(self, body):
        with tempfile.TemporaryDirectory() as d:
            (Path(d) / "index.html").write_text(body, encoding="utf-8")
            out = io.StringIO()
            with contextlib.redirect_stdout(out):
                code = main([d])
            return code, out.getvalue()

    def test_tel_href_is_forbidden(self):
        code, out = self._run_on_page('<p>Deepak</p><a href="tel:+919876543210">call</a>')
        self.assertEqual(code, 1)
        self.assertIn("FAIL forbidden: index.html attribute", out)

    def test_json_ld_telephone_is_forbidden(self):
        code, out = self._run_on_page(
            '<p>Deepak</p><script type="application/ld+json">{"@type":"Person","telephone":"+91 98765 43210"}</script>')
        self.assertEqual(code, 1)
        self.assertIn("FAIL forbidden: index.html attribute", out)

    def test_phone_hits_never_print_the_number(self):
        for body in ['<p>Deepak, call 98765 43210</p>', '<p>Deepak</p><a href="tel:+919876543210">call</a>']:
            code, out = self._run_on_page(body)
            self.assertEqual(code, 1)
            self.assertRegex(out, r"FAIL forbidden: index\.html( attribute)?: phone number")
            for digits in ("98765", "43210", "9876543210"):
                self.assertNotIn(digits, out)

    def test_head_text_is_checked_for_forbidden_wording(self):
        cases = {
            '<meta name="description" content="Deepak, open to SDE-3 roles">': "open to SDE-3 roles",
            '<meta property="og:title" content="Deepak - Rust core lead">': "Rust core",
            '<script type="application/ld+json">{"@type":"Person","description":"ships the 4.x API"}</script>': "4.x",
        }
        for head, snippet in cases.items():
            code, out = self._run_on_page(f"<head>{head}</head><p>Deepak</p>")
            self.assertEqual(code, 1, head)
            self.assertIn(f"FAIL forbidden: index.html attribute: {snippet}", out)

    def test_normal_head_and_urls_pass(self):
        html = ('<head><title>Deepak Choudhary - SDE 3</title>'
                '<meta name="description" content="Tech lead for 4 SDKs on one shared core.">'
                '<meta property="og:title" content="Deepak Choudhary - SDE 3">'
                '<script type="application/ld+json">{"@type":"Person","name":"Deepak"}</script></head>'
                '<p>Deepak</p><a class="open-to-work rust-core" href="https://example.com/open-to-work/4.x">x</a>'
                '<img alt="Deepak Choudhary" src="/photo.jpg">')
        self.assertEqual(page_problems("index.html", html, forbidden_patterns(), []), [])

    def test_script_type_without_value_does_not_crash(self):
        attribute_texts("<script type>var a = 1</script>")

    def test_resume_numbers_are_allowed(self):
        self.assertEqual(find_forbidden("22 releases · 3 million+ shoppers · 100,000+ users · 2017–2021 · 13.6k lines", BUILT_IN), [])

    def test_job_seeking_wording_is_forbidden(self):
        for s in ["Open to SDE-3 roles", "looking for a new role", "available for hire"]:
            self.assertTrue(find_forbidden(s, BUILT_IN), s)

    def test_more_job_seeking_wording_is_forbidden(self):
        for s in ["Seeking new opportunities", "#OpenToWork", "open-to-work", "Open To Work", "OPEN TO WORK"]:
            self.assertTrue(find_forbidden(s, BUILT_IN), s)

    def test_pay_words_are_forbidden(self):
        self.assertTrue(find_forbidden("expecting 30 LPA", BUILT_IN))

    def test_page_text_ignores_scripts_and_joins_spans(self):
        self.assertEqual(page_text("<script>var LPA=1</script><h1>Hey, I'm <span>Deepak</span></h1>"), "Hey, I'm Deepak")

    def test_missing_required(self):
        self.assertEqual(missing_required("Hey, I'm Deepak", ["Hey, I'm Deepak", "Murmur"]), ["Murmur"])

    def test_bot_blocked_hosts_are_unverified_not_broken(self):
        self.assertIsNone(link_ok("https://www.linkedin.com/in/hey-deepak", 999))
        self.assertIsNone(link_ok("https://medium.com/@HeyDeepak", 403))
        self.assertFalse(link_ok("https://github.com/Hey-Deepak/nope", 404))
        self.assertFalse(link_ok("https://github.com/Hey-Deepak", 403))
        self.assertTrue(link_ok("https://github.com/Hey-Deepak", 200))

    def test_network_errors_on_bot_blocked_hosts_are_unverified(self):
        self.assertIsNone(link_ok("https://medium.com/@HeyDeepak", None))
        self.assertIsNone(link_ok("https://www.linkedin.com/in/hey-deepak", None))
        self.assertFalse(link_ok("https://github.com/Hey-Deepak", None))

    def test_rate_limited_links_are_unverified(self):
        self.assertIsNone(link_ok("https://github.com/Hey-Deepak", 429))
        self.assertIsNone(link_ok("https://www.linkedin.com/in/hey-deepak", 429))

    def test_check_link_classifies_without_network(self):
        for error in (urllib.error.URLError("reset"), ConnectionResetError(), TimeoutError()):
            with mock.patch("check_site._fetch_status", side_effect=error):
                self.assertEqual(check_link("https://medium.com/@HeyDeepak"), (None, type(error).__name__))
                self.assertEqual(check_link("https://github.com/Hey-Deepak"), (False, type(error).__name__))
        with mock.patch("check_site._fetch_status", return_value=429):
            self.assertEqual(check_link("https://github.com/Hey-Deepak"), (None, "429"))

    def test_json_ld_person(self):
        self.assertEqual(json_ld_person('<script type="application/ld+json">{"@type":"Person","name":"A"}</script>')["name"], "A")
        self.assertIsNone(json_ld_person("<p>none</p>"))
        self.assertIsNone(json_ld_person('<script type="application/ld+json">{bad</script>'))

    def test_load_patterns_missing_file_is_empty(self):
        self.assertEqual(load_patterns(Path("/nonexistent")), [])

    def _seo_site(self, d, *, robots=None, sitemap_lastmod="2026-10-28", footer_date="2026-10-28",
                  og_title="Deepak &amp; co — SDE 3"):
        site = Path(d)
        head = ('<title>Deepak &amp; co — SDE 3</title>'
                f'<meta property="og:title" content="{og_title}">'
                '<link rel="canonical" href="https://heydeepak.dev/">'
                '<script type="application/ld+json">{"@type":"Person","name":"A"}</script>')
        (site / "index.html").write_text(f"<head>{head}</head><footer>Last updated {footer_date}</footer>", encoding="utf-8")
        (site / "404.html").write_text("<p>no such page</p>", encoding="utf-8")
        for name in ("og.png", "photo.jpg"):
            (site / name).write_bytes(b"x")
        (site / "robots.txt").write_text(robots or "User-agent: *\nDisallow: /resume.pdf\nSitemap: https://heydeepak.dev/sitemap.xml\n")
        (site / "sitemap.xml").write_text(
            f"<urlset><url><loc>https://heydeepak.dev/</loc><lastmod>{sitemap_lastmod}</lastmod></url></urlset>")
        return site

    def test_missing_seo_lists_absent_files(self):
        with tempfile.TemporaryDirectory() as d:
            (Path(d) / "index.html").write_text("<p>Deepak</p>", encoding="utf-8")
            result = " | ".join(missing_seo(Path(d)))
        for expected in ["robots.txt", "sitemap.xml", "og.png", "404.html", "photo.jpg", "json-ld Person", "canonical"]:
            self.assertIn(expected, result)

    def test_missing_seo_passes_for_complete_site(self):
        with tempfile.TemporaryDirectory() as d:
            self.assertEqual(missing_seo(self._seo_site(d)), [])

    def test_canonical_is_found_whatever_the_attribute_order(self):
        for tag in ['<link href="https://heydeepak.dev/" rel="canonical">', '<link rel="canonical" href="https://heydeepak.dev/">']:
            self.assertEqual(canonical_hrefs(tag), ["https://heydeepak.dev/"])

    def test_robots_must_disallow_resume(self):
        with tempfile.TemporaryDirectory() as d:
            site = self._seo_site(d, robots="User-agent: *\nSitemap: https://heydeepak.dev/sitemap.xml\n")
            self.assertIn("robots: resume.pdf not disallowed", missing_seo(site))

    def test_sitemap_lastmod_must_match_footer_date(self):
        with tempfile.TemporaryDirectory() as d:
            site = self._seo_site(d, sitemap_lastmod="2026-01-01")
            self.assertIn("sitemap: lastmod != footer date", missing_seo(site))

    def test_title_must_equal_og_title(self):
        with tempfile.TemporaryDirectory() as d:
            site = self._seo_site(d, og_title="Deepak — an older title")
            self.assertIn("title != og:title", missing_seo(site))

    def test_svg_title_is_not_the_page_title(self):
        with tempfile.TemporaryDirectory() as d:
            site = self._seo_site(d)
            index = site / "index.html"
            index.write_text(index.read_text() + "<svg><title>icon</title></svg>", encoding="utf-8")
            self.assertNotIn("title != og:title", missing_seo(site))


if __name__ == "__main__":
    unittest.main()
