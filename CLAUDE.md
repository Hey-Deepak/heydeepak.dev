# CLAUDE.md

Personal site heydeepak.dev. Public repo: never commit secrets (see SECURITY.md).

## Stack

Kotlin 2.4.10, Kobweb 0.25.1 (Compose HTML), static export. Design: Buddy "Terminal Warmth" (dark, amber, teal).

## Commands

- Export: `./scripts/export.sh` (output in `site/.kobweb/site`)
- Preview: `python3 -m http.server 8100 --bind 127.0.0.1 -d site/.kobweb/site`
- Dev server: `./gradlew :site:kobwebStart -t`, stop with `./gradlew :site:kobwebStop`
- Checks: `python3 scripts/check_site.py site/.kobweb/site` (script lands in the next task)

Kotlin root: `site/src/jsMain/kotlin/dev/heydeepak/site/`. Static files: `site/src/jsMain/resources/public/`.

## Content rules

- Page text says only what the resume says.
- Never put on the page: a phone number, pay/CTC, job-seeking wording, unreleased work, or an address beyond "India".
- No invented hashes, metrics or dates.
- Every number on the page must appear in the resume text.
- No employer detail beyond the resume.
- Wording follows resume v9: "one shared core"; Rust never goes in a title or the headline.
- Unreleased work means things like the 4.x API.
- The resume PDF is never committed (`*.pdf` is gitignored); it is served from a GitHub Release asset.

## Git

- Never commit without the user's OK.
- Never commit on `main`; work on `v2-kobweb`.
- No AI or Claude attribution in commits or files.
