# heydeepak.dev

Personal site of Deepak, built with Kotlin and Compose HTML via [Kobweb](https://github.com/varabyte/kobweb) 0.25.1. The static export puts real HTML in the page source.

## Commands

```bash
# Static export -> site/.kobweb/site (uses the installed Chrome for snapshots)
./scripts/export.sh

# Preview the export at http://127.0.0.1:8100
python3 -m http.server 8100 --bind 127.0.0.1 -d site/.kobweb/site

# Live-reload dev server on :8101
./gradlew :site:kobwebStart -t
./gradlew :site:kobwebStop
```

## Layout

- `site/src/jsMain/kotlin/dev/heydeepak/site/` - pages, components and `Content.kt` (every fact on the page)
- `site/src/jsMain/resources/public/` - static files and CSS
- `site/build.gradle.kts` - `<head>` tags (title, description, Open Graph)

See [SECURITY.md](SECURITY.md) before committing.
