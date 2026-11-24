package dev.heydeepak.site

import dev.heydeepak.site.components.Icon

// heydeepak.dev/heybuddy: every fact on that page, from Deepak's Hey Buddy brief (2026-11-25).
// Early beta: no store badges, no public download, no store claims. The only digits are in the sample pairing code.
// Text in backticks is drawn as code.

/** One setup step; its number is drawn by CSS. */
class SetupStep(val title: String, val text: String)

/** A titled point with an icon (bento tiles, ways to connect, security points). */
class IconPoint(val icon: Icon, val title: String, val text: String)

/** A build: [accessSubject] set means "ask for access" by email, null means not out yet. */
class HeyBuddyBuild(
    val icon: Icon,
    val name: String,
    val status: String,
    val detail: String,
    val accessSubject: String? = null,
)

class Faq(val question: String, val answer: String)

/** A link to a page on this site, with a comment after it. */
class SiteLink(val href: String, val label: String, val note: String)

/** A link in the top bar. */
class NavLink(val anchor: String, val label: String)

/** A line of the sample session; [kind] picks its colour, and a TOOL line gets a drawn dot. */
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
    const val NAME = "Hey Buddy"
    const val HOW_ID = "how"
    const val FEATURES_ID = "features"
    const val SECURITY_ID = "security"
    const val DOWNLOAD_ID = "download"
    const val FAQ_ID = "faq"
    const val MAIN_ID = "main"
    const val SKIP = "Skip to content"
    val nav = listOf(NavLink(HOW_ID, "How it works"), NavLink(SECURITY_ID, "Security"), NavLink(FAQ_ID, "FAQ"))
    const val GET_BETA = "Get the beta"
    const val SEE_HOW = "See how it works"

    const val TAG = "Early beta · Mac + Android"
    const val HEADLINE_LEAD = "Your Claude Code terminal,"
    const val HEADLINE_TAIL = "live on your phone."
    const val PITCH = "Type `claude` in any terminal on your Mac and the same live terminal shows on your phone, " +
        "ready to read and type into."
    const val TERMINALS_LABEL = "Works in"
    val terminals = listOf("VS Code", "Android Studio", "IntelliJ & RustRover", "Terminal", "iTerm")

    // The product shot: one Claude Code session, on the Mac and on the phone.
    const val MAC_TITLE = "my-app — claude"
    const val MAC_PATH = "~/my-app"
    const val MAC_COMMAND = "claude"
    const val PHONE_TITLE = "claude · my-app"
    const val LIVE = "live"
    const val SAME_SESSION = "same session"
    val mockLines = listOf(
        MockLine("> add a test for login", MockKind.INPUT),
        MockLine("Read LoginScreen.kt", MockKind.TOOL),
        MockLine("Write LoginScreenTest.kt", MockKind.TOOL),
        MockLine("Allow this edit?", MockKind.ASK),
        MockLine("▸ Yes    No", MockKind.CHOICE),
    )
    // The phone app's key row (TerminalScreen.kt): two rows of five.
    val mockKeys = listOf(listOf("Esc", "Tab", "⇧Tab", "Ctrl", "^C"), listOf("←", "↓", "↑", "→", "⏎"))
    const val SHOT_ALT = "A Claude Code session in a Mac terminal, and the same session live on a phone, with a key " +
        "row: Esc, Tab, Shift+Tab, Ctrl, Ctrl+C, the arrow keys and Enter."

    const val HOW_EYEBROW = "How it works"
    const val SETUP_TITLE = "Set up in three steps"
    val setup = listOf(
        SetupStep(
            "Set up your Mac",
            "Install the Mac app and click “Set up this Mac”. It installs or checks Tailscale, finds Claude Code, " +
                "and sets up tmux and the terminal link. macOS only asks you for a password or an Allow.",
        ),
        SetupStep(
            "Pair your phone",
            "Install the phone app and scan the QR code on the Mac, or type the twelve-character code.",
        ),
        SetupStep("Type `claude`", "Type `claude` in any terminal on your Mac. It shows up on your phone."),
    )
    val setupChecks = listOf("Tailscale", "Claude Code", "tmux", "Terminal link")
    const val SETUP_BUTTON = "Set up this Mac"
    const val PAIR_CODE = "K7QM-2XAB-CD9F"
    const val PAIR_HINT = "scan, or type the code"
    const val ON_PHONE = "on your phone"

    const val FEATURES_EYEBROW = "Features"
    const val FEATURES_TITLE = "Made for the terminal you already use"
    val realTerminal = IconPoint(
        Icon.TERMINAL,
        "Real terminal, not a chat",
        "The same live terminal, on your phone. Read it and type into it, with keys for Esc, Tab, Shift+Tab, " +
            "Ctrl, Ctrl+C, the arrows and Enter.",
    )
    val anyTerminal = IconPoint(Icon.WINDOWS, "Any terminal, any IDE", "Type `claude` there as usual.")
    val reach = IconPoint(Icon.ARROW_LEFT_RIGHT, "Three ways to reach your Mac", "")
    val links = listOf(
        IconPoint(Icon.GLOBE, "Tailscale", "From anywhere."),
        IconPoint(Icon.HOTSPOT, "Your phone’s hotspot", "No internet needed."),
        IconPoint(Icon.USB, "USB cable", "On Android."),
    )
    val phoneKey = IconPoint(
        Icon.KEY,
        "A key for every phone",
        "Each phone gets its own key when it pairs, kept in the phone’s secure storage. Remove it on the Mac to revoke it.",
    )
    val noCloud = IconPoint(
        Icon.CLOUD_OFF,
        "No cloud in between",
        "There is no Hey Buddy server. The Mac only accepts connections from itself, Tailscale or your phone’s hotspot.",
    )
    val platformsTile = IconPoint(
        Icon.PHONE,
        "Android now, iPhone next",
        "The Mac and Android apps are in early beta. The iPhone app is next; Windows is planned.",
    )

    const val SECURITY_EYEBROW = "Security"
    const val SECURITY_TITLE = "Your Mac, your phone, nothing in between."
    const val SECURITY_LEAD = "Hey Buddy sends your terminal only to your own paired phone. Claude Code itself still " +
        "talks to its model as usual."
    const val DIAGRAM_PHONE = "Your phone"
    const val DIAGRAM_MAC = "Your Mac"
    const val DIAGRAM_LINK = "Tailscale · hotspot · USB"
    const val DIAGRAM_CLOUD = "cloud server"
    const val DIAGRAM_ALT = "Your phone connects straight to your Mac over Tailscale, your phone’s hotspot or USB; " +
        "there is no cloud server in between."
    val security = listOf(
        IconPoint(Icon.KEY, "Paired phones only", "Each phone gets its own key when it pairs, kept in the phone’s secure storage."),
        IconPoint(Icon.SHIELD, "No key, no connection", "The Mac refuses any device without a key, even on the same Tailscale network."),
        IconPoint(Icon.UNLINK, "Revoke from the Mac", "Click Remove on the Mac to revoke a phone."),
        IconPoint(
            Icon.CLOUD_OFF,
            "No Hey Buddy cloud",
            "Nothing goes through a Hey Buddy cloud server. The Mac only accepts connections from itself, " +
                "Tailscale or the phone’s hotspot.",
        ),
    )

    const val DOWNLOAD_EYEBROW = "Download"
    const val DOWNLOAD_TITLE = "Get the beta"
    const val BETA_NOTE = "Early beta: there is no public download yet. Ask for access by email."
    const val ASK_FOR_ACCESS = "Ask for access"
    const val NOT_YET = "Not available yet"
    val builds = listOf(
        HeyBuddyBuild(Icon.LAPTOP, "Mac", "Beta", "The Mac app, as a DMG.", accessSubject = "Hey Buddy Mac beta"),
        HeyBuddyBuild(Icon.PHONE, "Android", "Beta", "The phone app.", accessSubject = "Hey Buddy Android beta"),
        HeyBuddyBuild(Icon.PHONE, "iPhone", "Coming soon", "Next after Android."),
        HeyBuddyBuild(Icon.WINDOWS, "Windows", "Planned", "Not yet: Hey Buddy runs on the Mac for now."),
    )

    const val FAQ_EYEBROW = "FAQ"
    const val FAQ_TITLE = "Questions"
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

    const val CTA_TITLE = "Try it on your Mac and phone"
    const val CTA_TEXT = "Mac + Android, in early beta. Ask for access by email."

    const val FOOTER_OWNER = "© Deepak Choudhary"
    const val FOOTER_HOME = "heydeepak.dev"
    // Microsoft Clarity project "Hey Buddy Website"; tag IDs are public by design.
    const val CLARITY_TAG = "https://www.clarity.ms/tag/ziw22nei8d"
    const val PRIVACY_NOTE = "This page uses Microsoft Clarity to see how visitors use it (clicks and scrolling, " +
        "no personal details). The Hey Buddy apps send anonymous usage and crash reports so we can fix problems."

    const val DISCLAIMER = "Hey Buddy is an independent project, not made by or affiliated with Anthropic. " +
        "Claude and Claude Code are trademarks of Anthropic."

    /** The link from the home page's Buddy project. */
    // The comment fits beside the link on a 375px phone.
    val homeLink = SiteLink(PATH, "heydeepak.dev/heybuddy", "# early beta")

    fun accessHref(subject: String) = "mailto:${Profile.EMAIL}?subject=${subject.replace(" ", "%20")}"
}
