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

const val HERO_ID = "top"

/** What the shell can print, in `all` order: [id] is the command name and the URL hash, [title] the output's heading. */
enum class SiteSection(val id: String, val title: String) {
    SHIP("ship", SHARED_CORE_TITLE),
    AGENTS("agents", "AI-agent tools"),
    BUDDY("buddy", "Buddy"),
    BUDDY_DEMO("buddy-demo", "Buddy: a sample session"),
    BUDDY_SCREENSHOTS("buddy-screenshots", "Buddy: screenshots"),
    BUDDY_BUILD("buddy-build", "Buddy: how this page was built"),
    PROJECTS("projects", "Building in public"),
    WORK("work", "Selected work"),
    CAREER("career", "Career"),
    ABOUT("about", "About"),
    STACK("stack", "Stack"),
    STATS("stats", "Highlights"),
    CONTACT("contact", "Contact"),
}

// No date or time digits: the honest-numbers rule.
const val LAST_LOGIN = "Last login: today on heydeepak.dev"

const val HERO_NAME = "Deepak Choudhary"
const val HERO_TAG = "MOBILE · REAL-TIME VIDEO & VOICE · AI AGENTS"
// Two parts so a wrapped headline breaks after "SDKs" (resume: leads the 4 client SDKs; built rusty and Buddy).
const val HERO_HEADLINE_LEAD = "I lead mobile SDKs"
const val HERO_HEADLINE_TAIL = "and build AI-agent tools."
const val HERO_PITCH = "6+ years in mobile (Android). I lead VideoSDK’s 4 client SDKs — Android, iOS, Flutter and " +
    "React Native — on one shared core, and build AI-agent tools for the SDK team."

const val LAST_UPDATED = "2026-11-07"

const val PHOTO_SRC = "/photo.jpg"
const val PHOTO_ALT = "Deepak Choudhary"
const val PHOTO_SIZE_PX = 52

/** "DEEPAK" in the figlet ANSI Shadow font; every row is the same width so the art stays aligned. */
const val HERO_ART_COLUMNS = 48
val heroArt = listOf(
    "██████╗ ███████╗███████╗██████╗  █████╗ ██╗  ██╗",
    "██╔══██╗██╔════╝██╔════╝██╔══██╗██╔══██╗██║ ██╔╝",
    "██║  ██║█████╗  █████╗  ██████╔╝███████║█████╔╝ ",
    "██║  ██║██╔══╝  ██╔══╝  ██╔═══╝ ██╔══██║██╔═██╗ ",
    "██████╔╝███████╗███████╗██║     ██║  ██║██║  ██╗",
    "╚═════╝ ╚══════╝╚══════╝╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝",
)

class SdkRelease(val name: String, val version: String)

/** The 4 SDKs on the shared core, with the majors they shipped the same day. */
val sharedCoreReleases = listOf(
    SdkRelease("Android", "3.0"),
    SdkRelease("iOS", "4.0"),
    SdkRelease("Flutter", "5.0"),
    SdkRelease("React Native", "2.0"),
)
const val SHARED_CORE_LABEL = "one shared core"
const val SHARED_CORE_WHEN = "(same day, Oct 2026)"
const val SHARED_CORE_SHIPPED = "all 4 shipped"
/** The `sdk` pane's title line. */
const val SHARED_CORE_TITLE = "Four SDKs, one shared core."
/** The `sdk` pane's 3.x line, from the Android 3.x case study. */
const val KOTLIN_3X_LINE = "Kotlin-only 3.x API: 13.6k lines of Java interop removed"

/** rusty, the SDK team's Claude Code plugin (resume, ledger L-011); the career point is built from these. */
object Rusty {
    const val NAME = "rusty"
    const val WHAT = "the SDK team’s Claude Code plugin"
    val counts = listOf("13" to "agents", "18" to "commands", "7" to "hooks")
    const val WIKI = "a 70-page wiki"
    const val ADOPTION_LEAD = "4 engineers merged "
    const val ADOPTION_PRS = "103"
    const val ADOPTION_TAIL = " PRs into it in its first 4 weeks."
}

const val VOICE_AGENTS_LINE = "Added AI voice-agent support to the shared core and Android SDK."

/** One proof number; [key] is its neofetch label (a word, drawn by CSS so the value + label read as one line). */
class Stat(val key: String, val value: String, val label: String)

val proofStats = listOf(
    Stat("releases", "22", "Android SDK releases in 11 months"),
    Stat("sdks", "4", "SDKs on one shared core"),
    Stat("reach", "3M+", "shoppers reached (Ubuy)"),
    Stat("uptime", "6+", "years in mobile (Android)"),
)

// Share of the commits in those 22 Android releases (resume: "wrote 91% of their commits").
const val COMMIT_SHARE_PERCENT = 91
const val COMMIT_SHARE_LABEL = "of the Android SDK release commits are mine"

/** How a note reads in the `git show` diff: unchanged context, a real "before" the resume states, or added work. */
enum class DiffKind { CONTEXT, REMOVED, ADDED }

class Note(val label: String, val text: String, val kind: DiffKind)

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
            Note("context", "VideoSDK ships 4 client SDKs: Android, iOS, Flutter and React Native.", DiffKind.CONTEXT),
            Note("what I did", "Led the move of all 4 onto one shared core.", DiffKind.ADDED),
            Note(
                "result",
                "Android 3.0, iOS 4.0, Flutter 5.0 and React Native 2.0 shipped the same day (Oct 2026).",
                DiffKind.ADDED,
            ),
        ),
        chips = listOf("Kotlin", "JNI/FFI", "Rust", "libwebrtc"),
    ),
    CaseStudy(
        path = "work/android-3x",
        title = "Android SDK 3.x, Kotlin-first",
        notes = listOf(
            // The one real "before" on the resume: 3.x replaced the Java 1.x–2.x SDK.
            Note("before", "The 1.x–2.x production SDK was Java.", DiffKind.REMOVED),
            Note(
                "what I did",
                "Owned the 3.x major: a Kotlin-only API (227 files, 13.6k lines of Java interop removed), " +
                    "API checks, Dokka docs and the Maven Central release flow.",
                DiffKind.ADDED,
            ),
            Note(
                "result",
                "22 Android SDK releases in 11 months (1.0 → 3.0), 91% of their commits mine.",
                DiffKind.ADDED,
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
                DiffKind.ADDED,
            ),
            Note(
                "also",
                "Rebranded libwebrtc’s JNI layer so VideoSDK runs beside another WebRTC SDK in one app. " +
                    "Fixed a renderer deadlock that froze all remote video.",
                DiffKind.ADDED,
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
            VOICE_AGENTS_LINE,
            "Built ${Rusty.NAME}, ${Rusty.WHAT} (" + Rusty.counts.joinToString(", ") { "${it.first} ${it.second}" } +
                ", ${Rusty.WIKI}). ${Rusty.ADOPTION_LEAD}${Rusty.ADOPTION_PRS}${Rusty.ADOPTION_TAIL}",
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
    /** One line for the hero's `projects` pane, cut from [description]. */
    val teaser: String,
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
    teaser = "Talk to it from your phone; a daemon on your PC spawns and supervises Claude Code agents.",
)

/** A status card's tag; [RUNNING] is drawn amber, like Buddy's running dot. */
enum class CardStatus(val label: String) { SHIPPED("shipped"), IN_USE("in use"), RUNNING("running") }

/** A numbered status card beside whoami; [command] is what a click runs. Words only from the facts in this file. */
class HeroCard(val title: String, val line: String, val status: CardStatus, val command: String)

val heroCards = listOf(
    // Resume: Android 3.0, iOS 4.0, Flutter 5.0 and RN 2.0 shipped the same day.
    HeroCard(SHARED_CORE_TITLE.removeSuffix("."), "$SHARED_CORE_SHIPPED the same day", CardStatus.SHIPPED, "ship"),
    // In use: 4 engineers merged 103 PRs into it in its first 4 weeks.
    HeroCard(Rusty.NAME, Rusty.WHAT, CardStatus.IN_USE, "agents"),
    // Cut from BuddyInfo.ONE_LINE; "running" is its project status.
    HeroCard(buddyProject.name, "runs AI agents on your PC", CardStatus.RUNNING, "buddy"),
)

const val BUDDY_PS_TITLE = "buddy — supervising Claude Code agents"

/**
 * What Buddy is and does, in plain words from the public Hey-Buddy README (the user's own description).
 * No counts, no connection list, no agent names.
 */
object BuddyInfo {
    const val ONE_LINE = "a personal AI assistant on your phone that runs a team of AI agents on your PC"
    const val DESCRIPTION = "You talk to Buddy. It answers simple things itself and hands real tasks to specialist " +
        "agents, each a Claude Code session on your own machine."
    val features = listOf(
        "in control" to "Risky actions wait for a tap on your phone; Claude Code’s permission prompts go to your phone.",
        "teamwork" to "Sessions talk to each other through MCP tools.",
        "memory" to "It remembers: an LLM wiki keeps decisions and learnings across sessions.",
        "plugs in" to "MCP server and client, A2A, and ACP for IDEs.",
        "built with" to "Kotlin everywhere: Kotlin Multiplatform, Compose Multiplatform (Android, iOS, desktop, web), Ktor.",
    )
    const val LINKEDIN_LABEL = "building it in public on LinkedIn"
}

/** Who speaks a `buddy demo` line; it picks the colour. */
enum class DemoSpeaker { PHONE, BUDDY, AGENT }

class DemoLine(val speaker: DemoSpeaker, val name: String, val text: String)

/** `buddy demo`: a labelled replay, from the user's intro post; no real times, numbers or file names. */
val buddyDemo = listOf(
    DemoLine(DemoSpeaker.PHONE, "phone", "“Fix the crash in the login screen.”"),
    DemoLine(DemoSpeaker.BUDDY, "buddy", "that’s a task → starting a Claude Code session: login-crash"),
    DemoLine(DemoSpeaker.AGENT, "login-crash", "reading the stack trace …"),
    DemoLine(DemoSpeaker.AGENT, "login-crash", "wants to edit a file → asking your phone"),
    DemoLine(DemoSpeaker.PHONE, "phone", "✓ approved"),
    DemoLine(DemoSpeaker.AGENT, "login-crash", "fixed · tests pass"),
    DemoLine(DemoSpeaker.BUDDY, "buddy", "“Done: the crash is fixed and the tests pass.”"),
)

/**
 * The swarm's cursors: real Buddy roles and tools, and the Claude Code sessions Buddy launches.
 * Never a career or job agent. Labels are words only: no counts, sizes or times.
 */
object SwarmNames {
    const val BUDDY = "buddy"
    const val BUILDER = "website-builder"
    const val DESIGNER = "designer"
    const val CHECKS = "checks"
    const val SIMPLIFY = "simplify"
    const val CLAUDE = "claude"
    fun claude(section: String) = "$CLAUDE · $section"
}

const val SWARM_BUILDING = "agents are building this page"
const val SWARM_BUILDING_SHORT = "building this page"
const val SWARM_BUILT = "✓ built by Buddy’s agents"

/** One tmux pane of the `buddy build` replay: who typed in it ([phone] picks the colour) and what, in order. */
class BuildPane(val name: String, val lines: List<String>, val phone: Boolean = false)

const val BUILD_REPLAY_NOTE = "# replay: how Buddy’s agents built this page"
const val BUILD_DONE = "✓ shipped to heydeepak.dev"

// How this site is made (CLAUDE.md's update loop); words only: no counts, scores or times. Lines fit 30 columns (a 320px pane).
// Agent panes are named for their cursors, so each cursor follows its own pane.
val buildPanes = listOf(
    BuildPane(
        SwarmNames.BUILDER,
        listOf("reading the resume …", "writing whoami", "writing the ship map", "writing work, career, about …", "✓ facts match the resume"),
    ),
    BuildPane(
        SwarmNames.DESIGNER,
        listOf("applying the Hallmark rules", "Terminal Warmth tokens", "one font: JetBrains Mono", "✓ still one terminal"),
    ),
    BuildPane(SwarmNames.CHECKS, listOf("export … ok", "content checks … ok", "link checks … ok", "✓ all pass")),
    BuildPane("you · phone", listOf("buddy: ready to ship?", "[ allow ] [ deny ]", "✓ approved from the phone"), phone = true),
)

/** A real Buddy screen in `public/buddy/`, captioned "[area] · [caption]"; adding one is a file plus an entry here. */
class BuddyShot(val file: String, val area: String, val caption: String, val alt: String, val width: Int, val height: Int)

const val BUDDY_SHOTS_NOTE = "# sample data on a test setup"

// User-approved (2026-11-06), one per area. Captions stay within 27 characters (one line in the frame) and carry no numbers.
val buddyShots = listOf(
    BuddyShot(
        "chat.webp", "work", "ask Buddy for a fix",
        "Buddy chat: you ask it to fix the crash on the login screen, and Buddy says it will launch a session to fix it.",
        540, 1200,
    ),
    BuddyShot(
        "approval.webp", "work", "approve on the phone",
        "Buddy’s sessions screen: under Needs you, a session asks to edit a file, with Allow and Deny buttons.",
        540, 1200,
    ),
    BuddyShot(
        "fitness.webp", "personal", "today’s workout",
        "Buddy’s Fitness today screen: a push-day workout, each exercise with its weight and sets, some sets ticked off.",
        540, 715,
    ),
    BuddyShot(
        "style.webp", "personal", "outfit ideas",
        "Buddy’s Style chat: a request for a smart-casual office outfit, answered with outfit cards, a denim jacket marked Liked, then shoes.",
        540, 1200,
    ),
    BuddyShot(
        "content.webp", "content", "from idea to post",
        "Buddy’s Content board: posts grouped under Needs you, Up next and Posted, each with its next step.",
        540, 1200,
    ),
    BuddyShot(
        "wiki.webp", "wiki", "what it remembers",
        "Buddy’s Memory screen for its wiki: pages by type, tags and the most recent pages.",
        540, 1200,
    ),
    BuddyShot(
        "store.webp", "agent store", "pick an agent",
        "Buddy’s Agent Store: a marketplace of specialist agents, such as a tester and a code reviewer, that Buddy can put into a session.",
        540, 1200,
    ),
)

/** One row of the `buddy ps` table; the Buddy description above says the same in words. */
class BuddyProcess(val name: String, val where: String, val status: String)

private val agentProcess = BuddyProcess("claude code", "your PC", "running")

val buddyProcesses = listOf(
    BuddyProcess("phone", "your phone", "talking"),
    BuddyProcess("daemon", "your PC", "supervising"),
    agentProcess,
    agentProcess,
    agentProcess,
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
        teaser = "Rewrote it in Rust: decode 73.5× faster than Kotlin.",
    ),
    Project(
        name = "FaangX",
        meta = "co-founder",
        description = "Co-founded FaangX; trained developers through its Kotlin Training Program.",
        chips = emptyList(),
        url = "https://faangx.com",
        urlLabel = "faangx.com",
        teaser = "Trained developers through its Kotlin Training Program.",
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
    AboutField("engineer", "6+ years in mobile (Android); tech lead for VideoSDK’s 4 client SDKs on one shared core"),
    AboutField("educator", "co-founded FaangX; trained developers through its Kotlin Training Program"),
    AboutField("entrepreneur", "incubated under MeitY Startup Hub; now building Buddy, an AI-agent assistant"),
)

/** One directory of the `tree ~/stack` output. */
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
