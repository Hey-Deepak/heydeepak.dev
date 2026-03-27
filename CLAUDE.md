# CLAUDE.md — heydeepak.dev Portfolio Guide

This file provides guidance to Claude Code when working on the heydeepak.dev portfolio website.

## What is heydeepak.dev?

A **personal portfolio website** showcasing 6 years of Android engineering experience. Built with **Kotlin/WASM + Compose Multiplatform** for the web.

- **Tech**: Kotlin, WASM, Compose for Web
- **Design**: Terminal Warmth v1.0 (dark, amber, teal)
- **Status**: Public portfolio, static content
- **Deployment**: Vercel → heydeepak.dev domain
- **Repo**: https://github.com/Hey-Deepak/heydeepak.dev

## 🔒 SECURITY FIRST — Never Commit API Keys

**GOLDEN RULE:** If it authenticates or authorizes → it's a secret → it NEVER goes in git.

### Protected Files (Automatically ignored by .gitignore)

```
.env                    # Environment variables (local only)
.env.local              # Personal secrets
.env.*.local            # Environment-specific secrets
local.properties        # Local gradle config
credentials.json        # Service credentials
firebase-key.json       # Firebase keys
*.key, *.pem, *.p12     # Cryptographic keys
```

### How to Handle API Keys (Safe Pattern)

**If we add API integrations:**

1. **Local Development:**
   ```bash
   # Create .env.local (NOT in git)
   API_KEY=your_actual_key_here
   ```

2. **Code (Kotlin):**
   ```kotlin
   // ✅ Safe: Load from environment
   val apiKey = System.getenv("API_KEY") ?: ""

   // ❌ NEVER: Hardcode in code
   val apiKey = "sk_live_abc123..."
   ```

3. **Documentation:**
   - Update `.env.example` with placeholder
   - Document in SECURITY.md
   - NO actual values in git-tracked files

4. **Production (Vercel):**
   - Add to Vercel Dashboard → Environment Variables
   - Vercel injects at build/runtime

### If You Accidentally Commit a Secret

1. **Revoke the key immediately** in service dashboard
2. **Remove from history:** `git rm --cached file && git commit`
3. **Create a new key** with restricted scopes
4. **Report** if it was a shared/team secret

### Current Status

- Portfolio does NOT currently use any API keys
- Static website, no backend or authentication
- All content is hardcoded/immutable

## Build & Deploy

### Build Locally

```bash
./gradlew :web:build
```

### Preview

```bash
cd web/build/dist/wasmJs/productionExecutable
python3 -m http.server 8000
# Visit: http://localhost:8000
```

### Deploy to Vercel

- Push to `main` branch
- Vercel auto-builds with `./gradlew :web:build`
- Auto-deploys to https://heydeepak.dev

## Project Structure

```
heydeepak.dev/
├── web/                           # Kotlin/WASM + Compose
│   └── src/wasmJsMain/
│       ├── kotlin/com/buddy/web/
│       │   ├── screens/
│       │   │   ├── PortfolioScreen.kt    # Main portfolio page
│       │   │   └── ...
│       │   ├── theme/
│       │   │   └── BuddyWebTheme.kt      # Terminal Warmth design
│       │   └── components/
│       │       └── Common.kt             # Reusable UI
│       └── resources/
│           └── index.html                # Entry point
├── build.gradle.kts                # Root build config
├── settings.gradle.kts             # Gradle settings + WASM repos
├── gradle.properties               # Versions
├── vercel.json                     # Deployment config
├── SECURITY.md                     # Security policy
├── .env.example                    # Secret template
├── README.md                       # Project docs
└── CLAUDE.md                       # This file

```

## Design System — Terminal Warmth v1.0

- **Background:** `#080A10` (deep dark)
- **Surface:** `#0E1018` (lighter dark)
- **Primary:** `#F5A623` (amber accent)
- **Secondary:** `#00D4B4` (teal accent)
- **Font:** JetBrains Mono (monospaced, terminal aesthetic)
- **Style:** Sharp edges (0px radius), minimal borders, dark-only

## Key Files

- **SECURITY.md** — Comprehensive guide for handling secrets
- **.env.example** — Template for environment variables (placeholders only)
- **README.md** — User-facing project documentation
- **vercel.json** — Vercel deployment configuration

## Deployment

### Vercel Setup (One-time)

1. Go to https://vercel.com/new
2. Import `Hey-Deepak/heydeepak.dev`
3. Vercel auto-detects `vercel.json`
4. Add custom domain: `heydeepak.dev`

### Auto-Deploy

- Push to `main` → Vercel builds & deploys automatically
- Build command: `./gradlew :web:build`
- Output dir: `web/build/dist/wasmJs/productionExecutable`

## Development Rules

### Do's

✅ Commit feature code, UI changes, new pages
✅ Keep design consistent with Terminal Warmth v1.0
✅ Document new components in comments
✅ Add secrets to `.env.example` (with placeholders)
✅ Update SECURITY.md for new patterns

### Don'ts

❌ NEVER commit API keys, tokens, credentials
❌ NEVER hardcode secrets in Kotlin code
❌ NEVER commit `.env`, `.env.local`, or private keys
❌ NEVER use colors outside Terminal Warmth palette
❌ NEVER ignore warnings from .gitignore

## Troubleshooting

### Build Issues

- **WASM binary download fails:** Check internet connection, binaryen repository is available
- **Port 8000 already in use:** `python3 -m http.server 9000` or kill the process

### Deployment Issues

- **Vercel build fails:** Check `vercel.json` output directory matches actual build path
- **Domain not resolving:** DNS can take 24-48 hours to propagate

## Key Tech Notes

- **Kotlin/WASM:** Kotlin compiles to WASM bytecode that runs in browser
- **Compose for Web:** Same Compose library as Android/Desktop, targeting wasmJs
- **No JavaScript:** Pure Kotlin implementation, no JS framework needed
- **Browser Runtime:** WASM runs in all modern browsers (Chrome, Firefox, Safari, Edge)

## Contact & Support

- **Repo Issues:** https://github.com/Hey-Deepak/heydeepak.dev/issues
- **Live Site:** https://heydeepak.dev
- **Author:** Deepak (Hey-Deepak on GitHub)

---

**Remember:** This is an **open, public portfolio**. Assume everything in this repo will be visible to the world. Keep secrets safe, keep code clean.
