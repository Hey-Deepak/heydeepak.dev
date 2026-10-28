# heydeepak.dev

Personal site of Deepak, built with Kotlin and Compose HTML via [Kobweb](https://github.com/varabyte/kobweb) 0.25.1. The static export puts real HTML in the page source.

## Commands

```bash
# Static export -> site/.kobweb/site (uses the installed Chrome; override with KOBWEB_EXPORT_BROWSER_PATH)
./scripts/export.sh

# Preview the export at http://127.0.0.1:8100
python3 -m http.server 8100 --bind 127.0.0.1 -d site/.kobweb/site

# Content, SEO and (with --links) link checks, then the checker's unit tests
python3 scripts/check_site.py site/.kobweb/site --links
python3 -m unittest discover -s scripts

# Live-reload dev server on :8101
./gradlew :site:kobwebStart -t
./gradlew :site:kobwebStop
```

## Updating the resume

The PDF is a GitHub Release asset, never committed. Uploading doesn't redeploy, so run the workflow after:

```bash
gh release upload resume resume.pdf --clobber
gh workflow run "Deploy site"
```

## Layout

- `site/src/jsMain/kotlin/dev/heydeepak/site/` - pages, components and `Content.kt` (every fact on the page)
- `site/src/jsMain/resources/public/` - static files and CSS
- `site/build.gradle.kts` - `<head>` tags (description, Open Graph, JSON-LD) in `SiteMeta`
- `site/.kobweb/conf.yaml` - the `<title>`, which must equal `SiteMeta.TITLE`

See [SECURITY.md](SECURITY.md) before committing.
