import contextlib
import io
import tempfile
import unittest
from pathlib import Path

from check_site import (BUILT_IN, canonical_hrefs, main, find_forbidden, json_ld_person, link_ok,
                        load_patterns, missing_required, missing_seo, page_text)


class CheckSiteTest(unittest.TestCase):
    def test_phone_number_is_forbidden(self):
        self.assertTrue(find_forbidden("call +91 9876543210", BUILT_IN))

    def test_spaced_and_dashed_phone_numbers_are_forbidden(self):
        for s in ["+91 98765 43210", "+91-98765-43210", "98765-43210", "9876543210"]:
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

    def test_resume_numbers_are_allowed(self):
        self.assertEqual(find_forbidden("22 releases · 3 million+ shoppers · 100,000+ users · 2017–2021 · 13.6k lines", BUILT_IN), [])

    def test_job_seeking_wording_is_forbidden(self):
        for s in ["Open to SDE-3 roles", "looking for a new role", "available for hire"]:
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

    def test_json_ld_person(self):
        self.assertEqual(json_ld_person('<script type="application/ld+json">{"@type":"Person","name":"A"}</script>')["name"], "A")
        self.assertIsNone(json_ld_person("<p>none</p>"))
        self.assertIsNone(json_ld_person('<script type="application/ld+json">{bad</script>'))

    def test_load_patterns_missing_file_is_empty(self):
        self.assertEqual(load_patterns(Path("/nonexistent")), [])

    def _seo_site(self, d, *, robots=None, sitemap_lastmod="2026-10-28", footer_date="2026-10-28"):
        site = Path(d)
        head = ('<link rel="canonical" href="https://heydeepak.dev/">'
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


if __name__ == "__main__":
    unittest.main()
