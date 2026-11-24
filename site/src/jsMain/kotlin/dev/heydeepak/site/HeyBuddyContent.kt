package dev.heydeepak.site

// heydeepak.dev/heybuddy: every fact on that page, from Deepak's Hey Buddy brief (2026-11-25).
// Early beta: no store badges, no public download, no store claims. No digits, like the other Buddy page.
// Text in backticks is drawn as code.

/** One setup step; its number is drawn by CSS. */
class SetupStep(val title: String, val text: String)

/** A line with a drawn key, like the home page's `man buddy` features. */
class KeyedLine(val key: String, val text: String)

/** A build: [accessSubject] set means "ask for access" by email, null means not out yet. */
class HeyBuddyBuild(val key: String, val name: String, val status: String, val accessSubject: String? = null)

class Faq(val question: String, val answer: String)

/** A link to a page on this site, with a comment after it. */
class SiteLink(val href: String, val label: String, val note: String)

/** A line of the sample phone screen; [kind] picks its colour, and a TOOL line gets a drawn dot. */
class MockLine(val text: String, val kind: MockKind)

enum class MockKind { INPUT, TOOL, ASK, CHOICE }

object HeyBuddy {
    const val PATH = "/heybuddy/"
    val head = PageHead(
        title = "Hey Buddy: Claude Code on your phone · Deepak Choudhary",
        description = "Type claude in any terminal on your Mac and the same live terminal shows on your phone: " +
            "read it and type into it. A Mac app plus an Android app, in early beta.",
        url = "https://heydeepak.dev/heybuddy/",
        image = "https://heydeepak.dev/og-heybuddy.png",
    )
    const val WINDOW_TITLE = "deepak@heydeepak.dev: ~/heybuddy"
    const val SETUP_ID = "setup"
    const val DOWNLOAD_ID = "download"

    const val TAG = "HEY BUDDY · EARLY BETA"
    const val HEADLINE_LEAD = "Your Claude Code terminal,"
    const val HEADLINE_TAIL = "live on your phone."
    const val PITCH = "Hey Buddy is a Mac app plus a phone app. Type `claude` in any terminal on your Mac and the " +
        "same live terminal shows on your phone. Read it and type into it, with keys for Esc, Tab, Shift+Tab, " +
        "Ctrl, Ctrl+C, the arrows and Enter."
    const val TERMINALS_LABEL = "works in"
    val terminals = listOf("VS Code", "Android Studio", "RustRover/IntelliJ", "Terminal", "iTerm")
    const val PLATFORMS_LABEL = "runs on"
    val platforms = listOf("Mac + Android: early beta", "iPhone: next", "Windows: planned")
    const val GET_BETA = "get the beta"
    const val HOW_IT_WORKS = "how it works"

    const val MOCK_TITLE = "claude · my-app"
    const val MOCK_LIVE = "live"
    val mockLines = listOf(
        MockLine("> add a test for login", MockKind.INPUT),
        MockLine("Read LoginScreen.kt", MockKind.TOOL),
        MockLine("Write LoginScreenTest.kt", MockKind.TOOL),
        MockLine("Allow this edit?", MockKind.ASK),
        MockLine("▸ Yes    No", MockKind.CHOICE),
    )
    // The phone app's key row (TerminalScreen.kt): two rows of five.
    val mockKeys = listOf(listOf("Esc", "Tab", "⇧Tab", "Ctrl", "^C"), listOf("←", "↓", "↑", "→", "⏎"))
    const val MOCK_ALT = "A phone showing the Claude Code session from a terminal on the Mac, live, with a key row: " +
        "Esc, Tab, Shift+Tab, Ctrl, Ctrl+C, the arrow keys and Enter."
    const val MOCK_NOTE = "# sample screen"

    const val SETUP_TITLE = "Set up in three steps"
    val setup = listOf(
        SetupStep(
            "Set up the Mac",
            "Install the Mac app and click “Set up this Mac”. It installs or checks Tailscale, finds Claude Code, " +
                "and sets up tmux and the terminal link. macOS only asks you for a password or an Allow.",
        ),
        SetupStep(
            "Pair your phone",
            "Install the phone app and scan the QR code on the Mac, or type the twelve-character code.",
        ),
        SetupStep("Type `claude`", "Type `claude` in any terminal on your Mac. It shows up on your phone."),
    )

    const val LINKS_TITLE = "Three ways to reach your Mac"
    val links = listOf(
        KeyedLine("tailscale", "From anywhere."),
        KeyedLine("hotspot", "Your phone’s own hotspot. No internet needed."),
        KeyedLine("usb", "A USB cable (Android)."),
    )

    const val SECURITY_TITLE = "Security"
    val security = listOf(
        KeyedLine("paired", "Each phone gets its own key when it pairs, kept in the phone’s secure storage."),
        KeyedLine("no key", "The Mac refuses any device without a key, even on the same Tailscale network."),
        KeyedLine("remove", "Click Remove on the Mac to revoke a phone."),
        KeyedLine(
            "no cloud",
            "Nothing goes through a Hey Buddy cloud server. The Mac only accepts connections from itself, " +
                "Tailscale or the phone’s hotspot.",
        ),
    )

    const val DOWNLOAD_TITLE = "Download"
    const val BETA_NOTE = "Early beta: there is no public download yet. Ask for access by email."
    const val ASK_FOR_ACCESS = "ask for access"
    val builds = listOf(
        HeyBuddyBuild("mac", "Mac app (DMG)", "beta", accessSubject = "Hey Buddy Mac beta"),
        HeyBuddyBuild("android", "Android app", "beta", accessSubject = "Hey Buddy Android beta"),
        HeyBuddyBuild("iphone", "iPhone app", "coming soon"),
        HeyBuddyBuild("windows", "Windows", "not yet · planned"),
    )

    const val FAQ_TITLE = "FAQ"
    val faq = listOf(
        Faq(
            "Does my code leave my Mac?",
            "Hey Buddy sends your terminal only to your own paired phone, over Tailscale, your phone’s hotspot or a " +
                "USB cable. There is no Hey Buddy cloud server in between. Claude Code itself still talks to its " +
                "model as usual; Hey Buddy doesn’t change that.",
        ),
        Faq(
            "Which IDEs?",
            "Any terminal on your Mac: VS Code, Android Studio, RustRover and IntelliJ, Terminal, iTerm. " +
                "Type `claude` there as usual.",
        ),
        Faq(
            "Do I need Tailscale?",
            "Only to reach your Mac from anywhere, and “Set up this Mac” installs or checks it for you. Without it, " +
                "connect over your phone’s hotspot (no internet needed) or, on Android, a USB cable.",
        ),
        Faq("Windows?", "Not yet. Hey Buddy runs on the Mac for now; Windows is planned."),
    )

    const val HOME_NOTE = "  # the rest of my site"

    const val DISCLAIMER = "Hey Buddy is an independent project, not made by or affiliated with Anthropic. " +
        "Claude and Claude Code are trademarks of Anthropic."

    /** The link from the home page's Buddy project. */
    // The comment fits beside the link on a 375px phone.
    val homeLink = SiteLink(PATH, "heydeepak.dev/heybuddy", "# early beta")

    fun accessHref(subject: String) = "mailto:${Profile.EMAIL}?subject=${subject.replace(" ", "%20")}"
}
