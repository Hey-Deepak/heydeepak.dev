package dev.heydeepak.site

// Single source of truth for every fact on the page.

object Profile {
    const val ROLE = "SDE 3 · Tech Lead, Client SDKs"
    const val COMPANY = "VideoSDK"
    const val EMAIL = "choudharydeepak990@gmail.com"
    const val RESUME = "/resume.pdf"
    const val LINKEDIN = "https://www.linkedin.com/in/hey-deepak"
    const val GITHUB = "https://github.com/Hey-Deepak"
    const val MEDIUM = "https://medium.com/@HeyDeepak"
}

enum class SiteSection(val id: String, val label: String, val inNav: Boolean = true) {
    ABOUT("about", "About"),
    WORK("work", "Work"),
    CAREER("career", "Career"),
    // Reachable from the palette and the scroll spy, but too minor for the top bar.
    STACK("stack", "Stack", inNav = false),
    BUILDING("building", "Building"),
    CONTACT("contact", "Contact"),
}

const val HERO_ID = "top"

const val HERO_TAG = "MOBILE · REAL-TIME VIDEO & VOICE · AI AGENTS"
const val HERO_PITCH = "6+ years in mobile (Android). I lead VideoSDK's 4 client SDKs — Android, iOS, Flutter and " +
    "React Native — on one shared core, and build AI-agent tools for the SDK team."

const val LAST_UPDATED = "2026-10-29"

enum class Tone { PLAIN, ACCENT, CHECK, DIM }

class Seg(val text: String, val tone: Tone = Tone.PLAIN)

/** One command in the hero terminal; [stagger] prints output segments one by one, like a build log. */
class TerminalStep(val command: String, val output: List<Seg>, val stagger: Boolean = false)

val heroSteps = listOf(
    TerminalStep("whoami", listOf(Seg("deepak · ${Profile.ROLE} @ ${Profile.COMPANY}"))),
    TerminalStep(
        "./releases --sdk android",
        listOf(
            Seg("22 releases", Tone.ACCENT),
            Seg(" · 1.0 → 3.0 · "),
            Seg("91%", Tone.ACCENT),
            Seg(" of the commits are mine"),
        ),
    ),
    TerminalStep(
        "./ship --all-sdks --core shared",
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
    TerminalStep(
        "cat now.txt",
        listOf(Seg("Building Buddy: talk to it from your phone, and a daemon on your PC spawns and supervises Claude Code agents.")),
    ),
)

const val PHOTO_SRC = "/photo.jpg"
const val PHOTO_ALT = "Deepak Choudhary"
const val PHOTO_SIZE_PX = 72

class Stat(val value: Int, val suffix: String, val label: String)

val proofStats = listOf(
    Stat(22, "", "Android SDK releases in 11 months"),
    Stat(4, "", "SDKs on one shared core"),
    Stat(3, "M+", "shoppers reached (Ubuy)"),
    Stat(6, "+", "years in mobile (Android)"),
)

class Note(val label: String, val text: String, val isResult: Boolean = false)

class ProofLink(val label: String, val href: String)

class CaseStudy(
    val path: String,
    val title: String,
    val notes: List<Note>,
    val chips: List<String>,
    val proof: ProofLink? = null,
)

val caseStudies = listOf(
    CaseStudy(
        path = "work/shared-core",
        title = "One shared core for four SDKs",
        notes = listOf(
            Note("context", "VideoSDK ships 4 client SDKs: Android, iOS, Flutter and React Native."),
            Note(
                "what I did",
                "Led the move of all 4 onto one shared core.",
            ),
            Note(
                "result",
                "Android 3.0, iOS 4.0, Flutter 5.0 and React Native 2.0 shipped the same day (Oct 2026).",
                isResult = true,
            ),
        ),
        chips = listOf("Kotlin", "JNI/FFI", "Rust", "libwebrtc"),
    ),
    CaseStudy(
        path = "work/android-3x",
        title = "Android SDK 3.x, Kotlin-first",
        notes = listOf(
            Note("context", "The 1.x–2.x production SDK was Java."),
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
        proof = ProofLink(
            "versions on Maven Central",
            "https://central.sonatype.com/artifact/live.videosdk/rtc-android-sdk",
        ),
    ),
    CaseStudy(
        path = "work/camera-effects",
        title = "GPU camera effects, end to end",
        notes = listOf(
            Note(
                "what I did",
                "Built auto-framing, low-light and style effects end to end on OpenGL ES + Metal; " +
                    "sole author, shipped in Android 3.0.",
            ),
            Note(
                "also",
                "Rebranded libwebrtc's JNI layer so VideoSDK runs beside another WebRTC SDK in one app. " +
                    "Fixed a renderer deadlock that froze all remote video.",
            ),
        ),
        chips = listOf("OpenGL ES", "Metal", "Kotlin", "JNI"),
    ),
)

class Job(
    // `since` fills the commit-hash slot so the section still reads like `git log --oneline`.
    val since: String,
    val org: String,
    val orgUrl: String?,
    val role: String,
    val period: String,
    val points: List<String> = emptyList(),
    val isCurrent: Boolean = false,
)

private const val STREAMLINERS_URL = "https://thestreamliners.in/"

val careerLog = listOf(
    Job(
        since = "2025.05",
        org = Profile.COMPANY,
        orgUrl = "https://www.videosdk.live/",
        role = Profile.ROLE,
        period = "May 2025 – present",
        isCurrent = true,
        points = listOf(
            "Lead the Android, iOS, Flutter and React Native SDKs: plan work, review and merge PRs, run releases, " +
                "own enterprise escalations.",
            "Added AI voice-agent support to the shared core and Android SDK.",
            "Built rusty, the SDK team's Claude Code plugin (13 agents, 18 commands, 7 hooks, a 70-page wiki). " +
                "4 engineers merged 103 PRs into it in its first 4 weeks.",
        ),
    ),
    Job("2024.05", "The Streamliners", STREAMLINERS_URL, "Sr Android Engineer (SDE-2)", "May 2024 – Feb 2025"),
    Job(
        since = "2022.08",
        org = "Ubuy.com",
        orgUrl = "https://ubuy.com/",
        role = "Sr Android Engineer (SDE-2)",
        period = "Aug 2022 – Apr 2024",
        points = listOf(
            "Shipped features to 3 million+ shoppers across 180+ countries.",
            "RTL + LTR for 80+ languages and 5 payment gateways.",
            "Mentored 3 junior Android developers.",
        ),
    ),
    Job(
        since = "2020.09",
        org = "The Streamliners",
        orgUrl = STREAMLINERS_URL,
        role = "Sr Android Engineer (SDE-2)",
        period = "Sep 2020 – Jul 2022",
        points = listOf(
            "Led 10+ Android apps with 100,000+ users.",
            "Moved MVP → MVVM + Clean Architecture, cutting bug reports 25–30%.",
            "~30% less duplicate code via shared DroidLibs modules.",
        ),
    ),
    Job("2017", "JECRC University", null, "B.Tech CSE", "2017 – 2021"),
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
        meta = "co-founder",
        description = "Co-founded FaangX; trained developers through its Kotlin Training Program.",
        chips = emptyList(),
        url = "https://faangx.com",
        urlLabel = "faangx.com",
    ),
)

val recognition = listOf(
    "Incubated under MeitY Startup Hub",
    "JU Rythum 2019 Winner",
    "TIDE 2.0 Finalist",
    "Google BFDI 2020 Finalist",
    "SIH 2019 Finalist",
)

/** One `val` of the `data class Deepak` shown by `cat about.kt`. */
class AboutField(val name: String, val text: String)

val aboutFields = listOf(
    AboutField("engineer", "6+ years in mobile (Android); tech lead for VideoSDK's 4 client SDKs on one shared core"),
    AboutField("educator", "co-founded FaangX; trained developers through its Kotlin Training Program"),
    AboutField("entrepreneur", "incubated under MeitY Startup Hub; now building Buddy, an AI-agent assistant"),
)

/** One key of the `stack.json` shown by `cat stack.json`. */
class StackGroup(val key: String, val items: List<String>)

val stackGroups = listOf(
    StackGroup(
        "mobile",
        listOf("Kotlin", "Java", "Jetpack Compose", "Compose Multiplatform", "Coroutines", "MVVM", "Clean Arch", "Multi-Module"),
    ),
    StackGroup("media", listOf("WebRTC", "libwebrtc", "mediasoup", "OpenGL ES", "Metal", "Codecs", "Bluetooth")),
    StackGroup("sdk", listOf("JNI/FFI", "Rust", "C++ (libwebrtc)", "Gradle", "Maven Central", "Dokka")),
    StackGroup(
        "agents",
        listOf("Claude Code plugins", "Multi-Agent Orchestration", "MCP", "A2A", "ACP", "Voice Agents (VAD · STT · TTS)"),
    ),
    StackGroup("net", listOf("Ktor", "Retrofit & OkHttp", "WebSocket", "Socket.IO", "Protobuf")),
    StackGroup("di", listOf("Koin", "Dagger/Hilt")),
)
