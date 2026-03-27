# heydeepak.dev Architecture

## Overview

**heydeepak.dev** is a **personal portfolio website** — completely independent from other projects like Buddy, Murmur, Timepass, etc.

The portfolio showcases:
- 6+ years of Android engineering experience
- Key projects and tech stack
- Professional background and skills

## Domain Structure

```
heydeepak.dev                    ← Main domain (Personal Portfolio — this project)
├── buddy.heydeepak.dev          ← Subdomain (Buddy project — separate repo)
├── murmur.heydeepak.dev         ← Subdomain (Murmur project — future)
├── timepass.heydeepak.dev       ← Subdomain (Timepass project — future)
└── ...other-project.heydeepak.dev
```

## Project Independence

**heydeepak.dev has ZERO dependencies on:**
- ❌ Buddy codebase
- ❌ Buddy daemon / API
- ❌ Any backend service
- ❌ Any other project

**What this means:**
- Pure static portfolio website
- Can be deployed independently
- Builds without Buddy project
- No coupling or shared code

## Tech Stack

- **Language:** Kotlin
- **Platform:** Kotlin/WASM (compiles to WebAssembly)
- **UI:** Compose Multiplatform
- **Styling:** Terminal Warmth v1.0 design system
- **Deployment:** Vercel (static host)

## File Structure

```
heydeepak.dev/
├── web/                           # Main portfolio website
│   └── src/wasmJsMain/
│       ├── kotlin/com/buddy/web/
│       │   ├── screens/
│       │   │   └── PortfolioScreen.kt    # Only screen (no routing)
│       │   ├── theme/
│       │   │   └── BuddyWebTheme.kt      # Design system
│       │   └── components/
│       │       └── Common.kt             # Reusable UI
│       └── resources/
│           └── index.html                # Entry point
├── build.gradle.kts
├── settings.gradle.kts
├── vercel.json                   # Vercel config
├── CLAUDE.md                     # Dev guide
├── SECURITY.md                   # Secret management
├── README.md                     # User-facing docs
└── ARCHITECTURE.md               # This file
```

## Pages

Only one page (single-page portfolio):

- **`/`** — Full portfolio with hero, projects, tech stack, contact

No routing, navigation, or subpages. Pure content.

## Future: Buddy Subdomain

When we build `buddy.heydeepak.dev`, it will be a **separate repository** with:
- ✅ Own build system
- ✅ Own CI/CD (separate Vercel project)
- ✅ Own design system (if different)
- ✅ Own domain/subdomain
- ✅ Complete independence from portfolio

Example structure:
```
hey-deepak/buddy-website     (separate GitHub repo)
├── web/                     # Buddy showcase website
├── build.gradle.kts
├── vercel.json
└── CLAUDE.md
```

Deployed to: `buddy.heydeepak.dev`

## Deployment

**Vercel Configuration:**
- Build command: `./gradlew :web:build`
- Output directory: `web/build/dist/wasmJs/productionExecutable`
- Domain: `heydeepak.dev`
- Auto-deploy on push to `main` branch

**Future subdomains:**
- Each gets its own Vercel project
- Each has own DNS CNAME
- All point to heydeepak.dev "root"

## Key Principles

1. **Complete Independence** — No shared code, no dependencies between projects
2. **Single Responsibility** — Portfolio only; other projects get own sites
3. **Scalability** — Easy to add new subdomains for future projects
4. **Ownership** — Each project owns its site, design, deployment

---

## Related Projects

- **Buddy** (separate) — AI assistant for your phone
- **Murmur** (separate) — Privacy-first audio recorder
- **Timepass** (separate) — Social entertainment discovery
- **VideoSDK** (external) — Real-time communication SDK
- **FaangX** (external) — Interview prep platform

All have their own sites/marketing. This portfolio is just about **you** (Deepak).
