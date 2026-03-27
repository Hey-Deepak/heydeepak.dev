# heydeepak.dev

Personal portfolio website built with **Kotlin/WASM + Compose Multiplatform**.

**Live:** https://heydeepak.dev

## What is This?

A developer portfolio showcasing:
- 6 years of Android engineering experience
- Projects: Buddy, Murmur, Timepass, VideoSDK, FaangX
- Tech stack: Kotlin, KMP, WebRTC, MCP, AI integration
- Built in public philosophy

## Tech Stack

- **Language:** Kotlin
- **Platform:** Kotlin/WASM (compiles to WebAssembly)
- **UI Framework:** Compose Multiplatform
- **Styling:** Terminal Warmth v1.0 design system
- **Build:** Gradle 8.14+
- **Deployment:** Vercel + heydeepak.dev domain

## Build & Run

### Prerequisites
- JDK 17+
- Gradle 8.14+

### Local Development

```bash
# Build the WASM project
./gradlew :web:build

# Run locally (preview)
cd web/build/dist/wasmJs/productionExecutable
python3 -m http.server 8000
# Visit: http://localhost:8000
```

### Project Structure

```
heydeepak.dev/
├── web/                                    # Kotlin/WASM + Compose
│   ├── src/wasmJsMain/
│   │   ├── kotlin/com/buddy/web/
│   │   │   ├── screens/PortfolioScreen.kt  # Main portfolio page
│   │   │   ├── theme/BuddyWebTheme.kt      # Design system
│   │   │   └── components/                 # Reusable UI
│   │   └── resources/index.html            # Entry point
│   └── build.gradle.kts
├── build.gradle.kts
├── settings.gradle.kts
├── vercel.json                             # Vercel deployment config
└── README.md
```

## Deployment

### Automatic (Recommended)

1. **Connect to Vercel:**
   ```bash
   npm install -g vercel
   vercel link  # Link to heydeepak.dev project
   ```

2. **Deploy on push:**
   - Push to `main` branch
   - Vercel auto-builds with `./gradlew :web:build`
   - Live at https://heydeepak.dev

### Manual Deploy

```bash
vercel --prod
```

## Design System

**Terminal Warmth v1.0:**
- Background: `#080A10` (deep dark)
- Primary: `#F5A623` (amber)
- Secondary: `#00D4B4` (teal)
- Font: JetBrains Mono (monospaced)
- Style: Sharp edges, terminal aesthetic, minimal borders

## Pages

- **Home (`/`)** — Full portfolio with hero, projects, stack, contact
- **Dashboard (`/dashboard`)** — Buddy daemon status (optional)
- **Agents (`/agents`)** — Agent registry (optional)
- **Sessions (`/sessions`)** — Active sessions (optional)

## Features

✅ Kotlin/WASM compilation (runs in browser)
✅ Compose Multiplatform UI
✅ Responsive desktop design
✅ Terminal-style aesthetics
✅ Fast WASM runtime
✅ No JavaScript framework needed

## Contributing

This is a personal portfolio. For contributing to Buddy (the AI agent platform), see [Buddy repo](https://github.com/Hey-Deepak/Buddy).

## License

Private — personal portfolio.

---

**Built with ♥ by Deepak**
Android Engineer · Builder · #BuildInPublic
