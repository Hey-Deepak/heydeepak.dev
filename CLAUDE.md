# CLAUDE.md

Personal site heydeepak.dev. Public repo: never commit secrets (see SECURITY.md).

## Stack

Kotlin 2.4.10, Kobweb 0.25.1 (Compose HTML), static export. Design: Buddy "Terminal Warmth" (dark, amber, teal).

## Commands

- Export: `./scripts/export.sh` (output in `site/.kobweb/site`)
- Preview: `python3 -m http.server 8100 --bind 127.0.0.1 -d site/.kobweb/site`
- Dev server: `./gradlew :site:kobwebStart -t`, stop with `./gradlew :site:kobwebStop`
- Checks: `python3 scripts/check_site.py site/.kobweb/site` (add `--links` to check external links)
- Unit tests: `python3 -m unittest discover -s scripts`

Kotlin root: `site/src/jsMain/kotlin/dev/heydeepak/site/`. Static files: `site/src/jsMain/resources/public/`.
`<title>` comes from `site.title` in `site/.kobweb/conf.yaml`, which must equal `SiteMeta.TITLE` in
`site/build.gradle.kts` (the check fails otherwise).

## Content rules

- Page text says only what the resume says.
- Never put on the page: a phone number, pay/CTC, job-seeking wording, unreleased work, or an address beyond "India".
- No invented hashes, metrics or dates.
- Every number on the page must appear in the resume text.
- No employer detail beyond the resume.
- Wording follows resume v9: "one shared core"; Rust never goes in a title or the headline.
- Unreleased work means things like the 4.x API.
- The resume PDF is never committed (`*.pdf` is gitignored); it is served from a GitHub Release asset.

## Updating the site (every change follows this loop)

The request may come as "update my website", `/website ...` or a Buddy-launched session.

1. **Edit.** Dispatch the `website-builder` subagent with the job: Audit, Sync, Add a case study, or the specific edit.
   - It edits only the working tree and returns its changes, the check output and any decisions.
   - Put each decision to the user, one at a time.
2. **Preview.** Run `./scripts/export.sh`, serve the site on 8100, and screenshot it at 1440 and 375 px wide. Show the user.
3. **Ship, on the user's OK.**
   1. Branch from an up-to-date `main` (`site/<topic>`).
   2. Show the diffstat and the commit message, then commit.
   3. Push and open a PR.
4. **Wait for CI's `build` job** (export, unit tests, checks with the resume PDF). Fix any failure on the branch.
5. **Merge, on the user's OK.** The `deploy` job publishes the site; watch it with `gh run watch`.
6. **Live check.**
   1. Fetch `/`, `404.html`, `robots.txt`, `sitemap.xml`, `og.png`, `photo.jpg`, `apple-touch-icon.png` and `resume.pdf` into a temp dir.
   2. Run `check_site.py <dir> --require-file resume.pdf --pdf <dir>/resume.pdf`.
   3. Tell the user the result and the link.

Also:
- Keep `LAST_UPDATED` and the sitemap's `<lastmod>` equal.
- After a role or proof-number change, run `./scripts/og/render.sh`.

## Updating the resume

`gh release upload resume resume.pdf --clobber`, then `gh workflow run "Deploy site"`. Uploading alone doesn't
redeploy.

## Hosting

- **GitHub Pages:** published by Actions, custom domain `heydeepak.dev`, repo `Hey-Deepak/heydeepak.dev`.
- **GitHub account:** run every `gh` call and push as Hey-Deepak, with a per-command token, and don't switch the machine's active `gh` account:
  - `export GH_TOKEN=$(gh auth token --user Hey-Deepak)`
  - `git -c credential.helper= -c 'credential.helper=!gh auth git-credential' push ...`
- **Cloudflare DNS:**
  - Records are Proxied, with SSL/TLS mode Full. Cloudflare's edge certificate serves HTTPS, because GitHub's own certificate never issued.
  - Keep "Email Address Obfuscation" off: it replaces the contact email with "[email protected]".
  - Dashboard changes need the user signed in.
- **Old address:** `heydeepakdev.vercel.app` redirects here via `vercel.json`. The Vercel project still builds `main`, so keep that file.
- **Stale DNS after a change:** a Mac may hold old DNS for 5 minutes. Test Cloudflare's edge directly with `curl --resolve heydeepak.dev:443:<ip>`.
- **Speed:** judge Lighthouse performance on the live URL. The local preview server sends no gzip and scores about 15 points lower (2026-10-28: local 80, live 95).

## Git

- Never commit without the user's OK.
- Work on a feature branch; merge to `main` through a PR. A push to `main` deploys.
- No AI or Claude attribution in commits or files.
