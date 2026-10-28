package dev.heydeepak.site

// Single source of truth for every fact on the page.

object Profile {
    const val EMAIL = "choudharydeepak990@gmail.com"
    const val RESUME = "/resume.pdf"
    const val LINKEDIN = "https://www.linkedin.com/in/hey-deepak"
    const val GITHUB = "https://github.com/Hey-Deepak"
    const val MEDIUM = "https://medium.com/@HeyDeepak"
}

enum class SiteSection(val id: String, val label: String) {
    WORK("work", "Work"),
    CAREER("career", "Career"),
    BUILDING("building", "Building"),
    CONTACT("contact", "Contact"),
}

const val HERO_ID = "top"

enum class Tone { PLAIN, ACCENT, CHECK, DIM }

class Seg(val text: String, val tone: Tone = Tone.PLAIN)

/** One command in the hero terminal; [stagger] prints output segments one by one, like a build log. */
class TerminalStep(val command: String, val output: List<Seg>, val stagger: Boolean = false)

val heroSteps = listOf(
    TerminalStep("whoami", listOf(Seg("deepak · SDE 3 · Tech Lead, Client SDKs @ VideoSDK"))),
    TerminalStep(
        "./releases --sdk android --since 2025-05",
        listOf(
            Seg("22 releases", Tone.ACCENT),
            Seg(" · 1.0 → 3.0 · "),
            Seg("91%", Tone.ACCENT),
            Seg(" of the commits are mine"),
        ),
    ),
    TerminalStep(
        "./ship --all-sdks --core rust",
        listOf(
            // Non-breaking spaces keep each SDK on one line when the output wraps.
            Seg("✓\u00A0Android\u00A03.0  ", Tone.CHECK),
            Seg("✓\u00A0iOS\u00A04.0  ", Tone.CHECK),
            Seg("✓\u00A0Flutter\u00A05.0  ", Tone.CHECK),
            Seg("✓\u00A0React\u00A0Native\u00A02.0  ", Tone.CHECK),
            Seg("(same day, Oct 2026)", Tone.DIM),
        ),
        stagger = true,
    ),
    TerminalStep("cat now.txt", listOf(Seg("Building Buddy: a phone app that runs fleets of Claude Code agents."))),
)

class Stat(val value: Int, val suffix: String, val label: String)

val proofStats = listOf(
    Stat(22, "", "Android SDK releases in 11 months"),
    Stat(4, "", "SDKs on one shared Rust core"),
    Stat(3, "M+", "shoppers reached (Ubuy)"),
    Stat(6, "+", "years on Android"),
)

class Note(val label: String, val text: String, val isResult: Boolean = false)

class CaseStudy(val path: String, val title: String, val notes: List<Note>, val chips: List<String>)

val caseStudies = listOf(
    CaseStudy(
        path = "work/rust-core",
        title = "One Rust core for four SDKs",
        notes = listOf(
            Note("context", "VideoSDK ships 4 client SDKs: Android, iOS, Flutter and React Native."),
            Note(
                "what I did",
                "Led the move of all 4 onto one shared Rust core. Top contributor to the core: 319 of 678 commits.",
            ),
            Note(
                "result",
                "Android 3.0, iOS 4.0, Flutter 5.0 and React Native 2.0 shipped the same day (Oct 2026).",
                isResult = true,
            ),
        ),
        chips = listOf("Rust", "Kotlin", "Swift", "JNI/FFI", "libwebrtc"),
    ),
    CaseStudy(
        path = "work/android-3x",
        title = "Android SDK 3.x, Kotlin-first",
        notes = listOf(
            Note("context", "The production SDK was Java with heavy interop."),
            Note(
                "what I did",
                "Owned the 3.x major: a Kotlin-only API (227 files, 13.6k lines of Java interop removed), " +
                    "API checks, Dokka docs and the Maven Central release flow.",
            ),
            Note(
                "result",
                "22 Android SDK releases in 11 months (1.0 → 3.0), 91% of their commits mine.",
                isResult = true,
            ),
        ),
        chips = listOf("Kotlin", "Gradle", "Dokka", "Maven Central"),
    ),
    CaseStudy(
        path = "work/camera-effects",
        title = "GPU camera effects in Rust",
        notes = listOf(
            Note(
                "what I did",
                "Built auto-framing, low-light and style effects end to end on a Rust graph, rendering with " +
                    "OpenGL ES (Android) and Metal (iOS). Sole author of ~9.4k Rust lines.",
            ),
            Note(
                "also",
                "Rebranded libwebrtc's JNI layer so VideoSDK runs beside another WebRTC SDK in one app. " +
                    "Fixed a renderer deadlock that froze all remote video.",
            ),
        ),
        chips = listOf("Rust", "OpenGL ES", "Metal", "JNI"),
    ),
)

class Job(
    val hash: String,
    val org: String,
    val role: String,
    val period: String,
    val points: List<String> = emptyList(),
    val isCurrent: Boolean = false,
)

// Hashes are decorative: the section reads like `git log --oneline`.
val careerLog = listOf(
    Job(
        hash = "e3a91f2",
        org = "VideoSDK",
        role = "SDE 3 · Tech Lead, Client SDKs",
        period = "May 2025 – present",
        isCurrent = true,
        points = listOf(
            "Lead the Android, iOS, Flutter and React Native SDKs: plan work, review and merge PRs, run releases, " +
                "own enterprise escalations.",
            "Added AI voice-agent support to the Rust core and Android SDK.",
            "Built rusty, the SDK team's Claude Code plugin (13 agents, 18 commands, 7 hooks, a 70-page wiki). " +
                "4 engineers merged 103 PRs into it in its first 4 weeks.",
        ),
    ),
    Job("7c4d0b8", "The Streamliners", "Sr Android Engineer (SDE-2)", "May 2024 – Feb 2025"),
    Job(
        hash = "b51e6a3",
        org = "Ubuy.com",
        role = "Sr Android Engineer (SDE-2)",
        period = "Aug 2022 – Apr 2024",
        points = listOf(
            "Shipped features to 3 million+ shoppers across 180+ countries.",
            "RTL + LTR for 80+ languages and 5 payment gateways.",
            "Mentored 3 junior Android developers.",
        ),
    ),
    Job(
        hash = "2f8c7d1",
        org = "The Streamliners",
        role = "Sr Android Engineer (SDE-2)",
        period = "Sep 2020 – Jul 2022",
        points = listOf(
            "Led 10+ Android apps with 100,000+ users.",
            "Moved MVP → MVVM + Clean Architecture, cutting bug reports 25–30%.",
            "~30% less duplicate code via shared DroidLibs modules.",
        ),
    ),
    Job("9a0e5c4", "JECRC University", "B.Tech CSE", "2017 – 2021"),
)

enum class ProjectStatus { RUNNING, NONE }

class Project(
    val name: String,
    val meta: String,
    val description: String,
    val chips: List<String>,
    val url: String,
    val urlLabel: String,
    val status: ProjectStatus = ProjectStatus.NONE,
)

val buddyProject = Project(
    name = "Buddy",
    meta = "running · 2026",
    status = ProjectStatus.RUNNING,
    description = "A personal assistant you talk to from your phone: a daemon on your PC spawns and supervises " +
        "fleets of Claude Code agents. One Kotlin codebase across Android, iOS, desktop and web.",
    chips = listOf("Kotlin Multiplatform", "Compose", "Ktor", "MCP"),
    url = "https://github.com/Hey-Deepak/Hey-Buddy",
    urlLabel = "github.com/Hey-Deepak/Hey-Buddy",
)

val sideProjects = listOf(
    Project(
        name = "Murmur",
        meta = "on-device AI",
        description = "5-stage on-device pipeline: decode, diarization, Whisper speech-to-text, LLM analysis. " +
            "Rewrote it in Rust: decode 73.5× and diarization 4.3× faster than Kotlin.",
        chips = listOf("Rust", "whisper-rs", "ONNX", "JNI"),
        url = "https://github.com/Hey-Deepak/Murmur-rs",
        urlLabel = "github.com/Hey-Deepak/Murmur-rs",
    ),
    Project(
        name = "FaangX",
        meta = "co-founder · EdTech",
        description = "Co-founded EdTech. Kotlin Training Program.",
        chips = emptyList(),
        url = "https://faangx.com",
        urlLabel = "faangx.com",
    ),
)

val recognition = listOf(
    "Incubated under MeitY Startup Hub",
    "TIDE 2.0 Finalist",
    "Google BFDI 2020 Finalist",
    "SIH 2019 Finalist",
)
