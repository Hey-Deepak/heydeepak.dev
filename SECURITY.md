# Security

This repo is public. The site is static: no backend and no API keys. CI uses only the built-in `GITHUB_TOKEN`.

## Ignored on purpose (`.gitignore`)

- Local config and secrets: `local.properties`, `gradle.properties.local`, `.env`, `.env.local`, `.env.*.local`,
  `.mcp.json`, `.claude/`, and key or credential files (`*.key`, `*.pem`, `*.p12`, `*.jks`, `credentials.json`,
  `firebase-key.json`). They hold machine paths, tokens or tool settings.
- `*.pdf`: anything committed stays in public history, so the resume is never committed (see below).
- Build output and tool state: `build/`, `out/`, `.gradle/`, `.kotlin/`, `.idea/`, `.vercel/`, `__pycache__/`.

`kotlin-js-store/yarn.lock` is committed on purpose: it pins the npm dependencies CI installs.

## The resume PDF

It lives only as the `resume.pdf` asset of the `resume` GitHub Release. The deploy workflow downloads it into
the export at build time. An old version can be deleted from the release; a committed one could not.

## If a secret is ever committed

1. Rotate or revoke it first. Assume it has already been copied.
2. With the owner's OK, purge it from history with `git filter-repo` (`--invert-paths --path <file>`, or
   `--replace-text` for a string), then force-push every affected branch and tag.
3. `git rm --cached` alone is not enough: it stops tracking the file, but the secret stays in public history.
