package dev.heydeepak.site.components

import androidx.compose.runtime.*
import dev.heydeepak.site.*
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.dom.*

// The site is one terminal: a transcript of commands and their output, a prompt, and a row of chips.

const val SHELL_INPUT_ID = "shell-input"
const val TRANSCRIPT_ID = "transcript"
private const val TRANSCRIPT_END_ID = "transcript-end"

private const val CMD_WHOAMI = "whoami"
private const val CMD_MOTD = "cat /etc/motd"
private const val CMD_HELP = "help"
private const val CMD_CLEAR = "clear"
private const val CMD_ALL = "all"

// whoami streams only if the app is up this early; otherwise its output shows at once (hero readable by ~1.2 s).
private const val WHOAMI_STREAM_DEADLINE_MS = 300.0
private const val WHOAMI_START_MS = 60L
// Keep equal to the CSS stream timelines (rows × stagger + fade).
private const val WHOAMI_STREAM_MS = 480L
private const val MOTD_STREAM_MS = 400L

/** A command that prints a section: its real command line, one line of help, and what to try next. */
class SectionCommand(
    val section: SiteSection,
    val line: String,
    val help: String,
    val next: List<String>,
    val aliases: List<String> = emptyList(),
) {
    /** What you type: the id, with spaces for its sub-commands ("buddy demo"). */
    val name get() = section.id.replace('-', ' ')
}

val sectionCommands = listOf(
    SectionCommand(SiteSection.SHIP, "./ship --all-sdks --core shared", "4 SDKs on one shared core: the map, releases, commits", listOf("agents", "buddy", "work"), listOf("sdk", "./ship")),
    SectionCommand(SiteSection.AGENTS, "cat agents.md", "rusty, the SDK team’s Claude Code plugin, and voice agents", listOf("buddy", "career", "work"), listOf("rusty")),
    SectionCommand(SiteSection.BUDDY, "man buddy", "what Buddy is and does, with its live buddy ps", listOf("buddy demo", "buddy screenshots", "buddy build"), listOf("buddy", "buddy --help", "buddy ps")),
    SectionCommand(SiteSection.BUDDY_DEMO, "buddy demo", "a sample Buddy session, replayed", listOf("buddy screenshots", "buddy build", "buddy")),
    SectionCommand(SiteSection.BUDDY_SCREENSHOTS, "buddy screenshots", "Buddy’s real screens, on sample data", listOf("buddy build", "buddy demo", "buddy"), listOf("screenshots", "buddy shots")),
    SectionCommand(SiteSection.BUDDY_BUILD, "buddy build", "how Buddy’s agents built this page, replayed", listOf("buddy", "buddy demo", "projects"), listOf("build")),
    SectionCommand(SiteSection.PROJECTS, "ls -l ~/projects", "Buddy, Murmur, FaangX and recognition", listOf("buddy", "work", "contact"), listOf("ls", "ls -l", "ls ~/projects")),
    SectionCommand(SiteSection.WORK, "ls ./work", "three case studies as git diffs", listOf("career", "projects", "contact"), listOf("git show")),
    SectionCommand(SiteSection.CAREER, "git log --graph --oneline career", "every role, as a git log", listOf("work", "stack", "contact"), listOf("git log")),
    SectionCommand(SiteSection.ABOUT, "cat about.kt", "engineer, educator, entrepreneur", listOf("stack", "career", "contact")),
    SectionCommand(SiteSection.STACK, "tree ~/stack", "the tools I use, as a tree", listOf("projects", "work", "contact"), listOf("tree")),
    SectionCommand(SiteSection.STATS, "neofetch", "the four proof numbers", listOf("ship", "work", "contact")),
    SectionCommand(SiteSection.CONTACT, "./contact.sh", "email, resume and links", listOf("resume", "linkedin", "help")),
)

/** A command that opens a link; [newTab] is false for mail. */
class LinkCommand(val name: String, val href: String, val label: String, val newTab: Boolean = true)

val linkCommands = listOf(
    LinkCommand("resume", Profile.RESUME, "resume.pdf"),
    LinkCommand("email", "mailto:${Profile.EMAIL}", Profile.EMAIL, newTab = false),
    LinkCommand("linkedin", Profile.LINKEDIN, "linkedin.com/in/hey-deepak"),
    LinkCommand("github", Profile.GITHUB, "github.com/Hey-Deepak"),
    LinkCommand("medium", Profile.MEDIUM, "medium.com/@HeyDeepak"),
)

/** The chips under the prompt: the site's nav. */
val chipCommands = listOf("ship", "agents", "buddy", "projects", "work", "career", "contact", "help", CMD_ALL)

private const val ALL_HELP = "run every section, in order"
private val chipHelp = mapOf(CMD_HELP to "every command", CMD_ALL to ALL_HELP)
private val WHITESPACE = Regex("\\s+")

fun commandFor(section: SiteSection) = sectionCommands.first { it.section == section }

/** What one transcript entry prints. */
sealed interface Output
object LastLoginOut : Output
object WhoamiOut : Output
object MotdOut : Output
object HelpOut : Output
class SectionOut(val command: SectionCommand) : Output
class TextOut(val text: String, val muted: Boolean = false) : Output

/**
 * One `$ command` and its output; [live] output was printed by the app and may animate, [built] output
 * waits for the agents to build it. [anchor] is the export's deep-link id.
 */
class Entry(
    val key: Int,
    val echo: String?,
    val output: Output,
    val live: Boolean,
    val boot: Boolean = false,
    val anchor: String? = null,
    val built: Boolean = false,
) {
    val elementId get() = anchor ?: "e-$key"
}

private sealed interface Action
private class RunSection(val command: SectionCommand) : Action
private class OpenLink(val link: LinkCommand) : Action
private object Help : Action
private object Clear : Action
private object All : Action
private object Whoami : Action
private object Motd : Action

private val actions: Map<String, Action> = buildMap {
    sectionCommands.forEach { command ->
        (listOf(command.section.id, command.line) + command.aliases).forEach { put(it, RunSection(command)) }
    }
    linkCommands.forEach { put(it.name, OpenLink(it)) }
    put(CMD_HELP, Help); put("?", Help)
    put(CMD_CLEAR, Clear)
    put(CMD_ALL, All)
    put(CMD_WHOAMI, Whoami)
    put(CMD_MOTD, Motd); put("motd", Motd)
}

/** Names Tab can complete, shortest forms first. */
private val completions = (chipCommands + sectionCommands.map { it.name } + sectionCommands.map { it.line } +
    linkCommands.map { it.name } + listOf(CMD_CLEAR, CMD_WHOAMI)).distinct()

/**
 * The shell's state: the transcript, the prompt's text and history. [motion] allows typing and
 * streaming; [full] starts with every section printed (the export, or a page whose baked transcript is already on screen);
 * the [swarm], if any, builds each command's output.
 */
class ShellState(private val scope: CoroutineScope, val motion: Boolean, full: Boolean, val swarm: Swarm? = null) {
    val entries = mutableStateListOf<Entry>()
    var input by mutableStateOf("")
    var busy by mutableStateOf(false)
        private set
    /** The entry to bring to the top of the transcript after the next render. */
    var scrollTarget by mutableStateOf<Int?>(null)
        private set
    var announcement by mutableStateOf("")
        private set

    private val history = mutableListOf<String>()
    private var recalled = -1
    private var nextKey = 0
    private var typing: Job? = null

    init {
        val exporting = isExporting()
        add(null, LastLoginOut, boot = true)
        add(CMD_WHOAMI, WhoamiOut, boot = true, anchor = if (exporting) HERO_ID else null)
        add(CMD_MOTD, MotdOut, boot = true)
        if (full) sectionCommands.forEach { command ->
            add(command.line, SectionOut(command), anchor = if (exporting) command.section.id else null)
        }
    }

    private fun add(echo: String?, output: Output, boot: Boolean = false, anchor: String? = null): Entry {
        val live = motion && !boot
        val entry = Entry(nextKey++, echo, output, live, boot, anchor, built = live && swarm?.buildsOutput == true)
        entries += entry
        return entry
    }

    /** Hands the entries printed since [fromKey] to the agents. */
    private fun printedSince(fromKey: Int) {
        val printed = entries.filter { it.key >= fromKey && it.built }
        swarm?.printed(printed.map { PrintedEntry(it.elementId, leadFor(it.output)) })
    }

    /** Enter in the prompt. */
    fun submit() {
        if (busy) return
        val text = input
        input = ""
        if (text.isNotBlank()) execute(text.trim())
    }

    /** A chip, motd or help entry: the command is typed into the prompt for you, then run. */
    fun runTyped(name: String) {
        val line = (actions[name] as? RunSection)?.command?.line ?: name
        typing?.cancel()
        if (!motion) {
            execute(line)
            return
        }
        typing = scope.launch {
            busy = true
            input = ""
            typeCommand(line, FAST_PACE) { input = line.take(it) }
            input = ""
            busy = false
            execute(line)
        }
    }

    fun execute(line: String) {
        val fromKey = nextKey
        history.remove(line)
        history += line
        recalled = -1
        val action = actions[line.lowercase().replace(WHITESPACE, " ")]
        val first: Entry
        when (action) {
            is RunSection -> {
                first = add(line, SectionOut(action.command))
                setHash(action.command.section.id)
                announcement = "Printed: ${action.command.section.title}"
            }
            is OpenLink -> {
                first = add(line, TextOut("opening ${action.link.label} ↗"))
                if (action.link.newTab) openInNewTab(action.link.href) else window.location.href = action.link.href
                announcement = "Opening ${action.link.label}"
            }
            Help -> {
                first = add(line, HelpOut)
                announcement = "Printed: commands"
            }
            Clear -> {
                clear()
                return
            }
            All -> {
                first = add(line, TextOut("every section, in order:", muted = true))
                sectionCommands.forEach { add(it.line, SectionOut(it)) }
                setHash(null)
                announcement = "Printed: every section"
            }
            Whoami -> {
                first = add(line, WhoamiOut)
                announcement = "Printed: whoami"
            }
            Motd -> {
                first = add(line, MotdOut)
                announcement = "Printed: message of the day"
            }
            null -> {
                first = add(line, TextOut("command not found: $line${DOT_SEP}try help", muted = true))
                announcement = "Command not found: $line"
            }
        }
        scrollTarget = first.key
        printedSince(fromKey)
    }

    /** `clear` / Ctrl+L: an empty scrollback, then the motd again, so nobody is left on a blank screen. */
    fun clear() {
        typing?.cancel()
        busy = false
        entries.clear()
        val motd = add(CMD_MOTD, MotdOut)
        setHash(null)
        announcement = "Cleared"
        scrollTarget = motd.key
        printedSince(motd.key)
    }

    fun historyUp() {
        if (history.isEmpty()) return
        recalled = if (recalled < 0) history.lastIndex else (recalled - 1).coerceAtLeast(0)
        input = history[recalled]
    }

    fun historyDown() {
        if (recalled < 0) return
        recalled = if (recalled < history.lastIndex) recalled + 1 else -1
        input = if (recalled < 0) "" else history[recalled]
    }

    /** Tab: completes a unique prefix, or lists the candidates; false when there is nothing to complete. */
    fun complete(): Boolean {
        val prefix = input.trimStart().lowercase()
        if (prefix.isEmpty()) return false
        val matches = completions.filter { it.startsWith(prefix) && it != prefix }
        when {
            matches.isEmpty() -> return false
            matches.size == 1 -> input = matches.first() + " "
            else -> {
                val common = matches.reduce { acc, name -> acc.commonPrefixWith(name) }
                if (common.length > prefix.length) input = common
                else {
                    val listed = add(input, TextOut(matches.joinToString("   "), muted = true))
                    scrollTarget = listed.key
                    printedSince(listed.key)
                }
            }
        }
        return true
    }

    /** After a deep link (#work): runs that section once the boot is done. */
    fun runHash() {
        val id = currentHash() ?: return
        sectionCommands.firstOrNull { it.section.id == id }?.let { runTyped(it.section.id) }
    }
}

/** The agent who leads an output's build: a Claude Code session named for the section, or Buddy itself. */
private fun leadFor(output: Output): String = when (output) {
    is SectionOut -> SwarmNames.claude(output.command.section.id)
    WhoamiOut -> SwarmNames.claude(CMD_WHOAMI)
    MotdOut -> SwarmNames.claude("motd")
    else -> SwarmNames.BUDDY
}

/**
 * The terminal: transcript, prompt and chips. Boots with whoami and the motd: typed and streamed, or,
 * when the shell's swarm builds the first screen, shown as is for the agents to build.
 */
@Composable
fun Shell(shell: ShellState) {
    val swarm = shell.swarm
    val motion = LocalMotion.current
    // The agents build the first screen themselves; otherwise it is typed and streamed.
    val swarmBoot = remember { swarm?.building == true }
    val bootMotion = motion && !swarmBoot
    val textMotion = LocalTextMotion.current && !swarmBoot
    val streamWhoami = remember { textMotion && msSinceLoad() <= WHOAMI_STREAM_DEADLINE_MS }
    var whoamiTyped by remember { mutableStateOf(if (bootMotion && streamWhoami) 0 else CMD_WHOAMI.length) }
    var motdTyped by remember { mutableStateOf(if (bootMotion && textMotion) 0 else CMD_MOTD.length) }
    var whoami by remember { mutableStateOf(if (streamWhoami) Reveal.WAITING else Reveal.DONE) }
    var motd by remember { mutableStateOf(if (bootMotion && textMotion) Reveal.WAITING else Reveal.DONE) }

    LaunchedEffect(Unit) {
        if (bootMotion) {
            if (streamWhoami) {
                delay(WHOAMI_START_MS)
                typeCommand(CMD_WHOAMI, QUICK_PACE) { whoamiTyped = it }
                whoami = Reveal.PLAYING
                delay(WHOAMI_STREAM_MS)
            }
            if (motd == Reveal.WAITING) {
                typeCommand(CMD_MOTD, FAST_PACE) { motdTyped = it }
                motd = Reveal.PLAYING
                delay(MOTD_STREAM_MS)
            }
        }
        shell.runHash()
    }

    LaunchedEffect(shell.scrollTarget) {
        val key = shell.scrollTarget ?: return@LaunchedEffect
        window.requestAnimationFrame { scrollEntryToTop(TRANSCRIPT_ID, "e-$key", TRANSCRIPT_END_ID, smooth = motion) }
    }

    Div(attrs = {
        id(TRANSCRIPT_ID)
        classes("transcript")
        attr("role", "log")
        // New output is announced as a one-line summary (below), not read out whole.
        attr("aria-live", "off")
        attr("aria-label", "Terminal output")
        attr("tabindex", "0")
    }) {
        shell.entries.forEach { entry ->
            key(entry.key) {
                EntryView(entry, shell, whoamiTyped = whoamiTyped, whoami = whoami, motdTyped = motdTyped, motd = motd)
            }
        }
        Div(attrs = { id(TRANSCRIPT_END_ID); attr("aria-hidden", "true") })
    }
    swarm?.let { SwarmBar(it) }
    Div(attrs = { classes("sr-only"); attr("role", "status"); attr("aria-live", "polite") }) { Text(shell.announcement) }
    PromptLine(shell)
    Chips(shell)
}

@Composable
private fun EntryView(entry: Entry, shell: ShellState, whoamiTyped: Int, whoami: Reveal, motdTyped: Int, motd: Reveal) {
    val bootWhoami = entry.boot && entry.output == WhoamiOut
    val bootMotd = entry.boot && entry.output == MotdOut
    Div(attrs = {
        id(entry.elementId)
        classes("entry")
        if (entry.boot) classes("is-boot")
        if (isExporting()) attr("data-static", "")
    }) {
        entry.echo?.let { echo ->
            when {
                bootWhoami -> Prompt(echo, whoamiTyped, cursor = whoami == Reveal.WAITING)
                bootMotd -> Prompt(echo, motdTyped, cursor = motd == Reveal.WAITING && motdTyped < echo.length)
                else -> Prompt(echo)
            }
        }
        Div(attrs = {
            classes("out")
            if (entry.live) classes("is-playing")
            if (entry.built) classes("ag-fast")
        }) {
            when (val output = entry.output) {
                LastLoginOut -> P(attrs = { classes("last-login") }) { Text(LAST_LOGIN) }
                // Only the boot copy carries the page's h1.
                WhoamiOut -> WhoAmI(if (bootWhoami) whoami else Reveal.DONE, withTitle = bootWhoami, run = shell::runTyped)
                MotdOut -> MotdOutput(shell, if (bootMotd) motd else Reveal.DONE)
                HelpOut -> HelpOutput(shell)
                is SectionOut -> SectionOutput(output.command, entry.live, shell)
                is TextOut -> P(attrs = { classes("say"); if (output.muted) classes("is-muted") }) { Text(output.text) }
            }
        }
    }
}

@Composable
private fun SectionOutput(command: SectionCommand, live: Boolean, shell: ShellState) {
    val section = command.section
    H2(attrs = { classes("h2") }) {
        Span(attrs = { classes("h2-mark"); attr("aria-hidden", "true") }) { Text("▸") }
        Text(section.title)
    }
    when (section) {
        SiteSection.SHIP -> ShipOutput(if (live) Reveal.PLAYING else Reveal.DONE)
        SiteSection.AGENTS -> AgentsOutput()
        SiteSection.BUDDY -> BuddyOutput()
        SiteSection.BUDDY_DEMO -> BuddyDemoOutput(playing = live)
        SiteSection.PROJECTS -> ProjectsOutput()
        SiteSection.WORK -> WorkOutput()
        SiteSection.CAREER -> CareerOutput()
        SiteSection.ABOUT -> AboutOutput()
        SiteSection.STACK -> StackOutput()
        SiteSection.STATS -> StatsOutput()
        SiteSection.CONTACT -> ContactOutput()
        SiteSection.BUDDY_SCREENSHOTS -> BuddyScreenshotsOutput()
        SiteSection.BUDDY_BUILD -> BuddyBuildOutput(playing = live)
    }
    // A replay's next line waits for its last line.
    val replayMs = when (section) {
        SiteSection.BUDDY_DEMO -> DEMO_REPLAY_MS
        SiteSection.BUDDY_BUILD -> buildReplayMs
        else -> null
    }
    NextLine(command.next, shell, delayMs = if (live) replayMs else null)
}

/** A clickable command inside output: a link to its deep-link anchor (works without JS), run in the shell with it. */
@Composable
private fun CmdLink(name: String, shell: ShellState, label: String = name) {
    val link = linkCommands.firstOrNull { it.name == name }
    if (link != null) {
        // A real link opens it natively (new tab, or the mail app); the shell just notes it.
        A(href = link.href, attrs = {
            classes("cmd")
            if (link.newTab) {
                attr("target", "_blank")
                attr("rel", if (link.href == Profile.RESUME) "noopener noreferrer nofollow" else "noopener noreferrer")
            }
        }) { Text(label) }
        return
    }
    val anchor = (actions[name] as? RunSection)?.command?.section?.id
    A(href = "#${anchor ?: name}", attrs = {
        classes("cmd")
        onClick { event ->
            event.preventDefault()
            shell.runTyped(name)
        }
    }) { Text(label) }
}

@Composable
private fun NextLine(next: List<String>, shell: ShellState, delayMs: Int? = null) {
    P(attrs = {
        classes("next")
        delayMs?.let {
            style { property("animation-delay", "${it}ms") }
            attr(NEXT_WAIT_ATTR, it.toString())
        }
    }) {
        Span(attrs = { classes("next-lead"); attr("aria-hidden", "true") }) { Text("→ ") }
        Span(attrs = { classes("sr-only") }) { Text("Next: ") }
        CmdList(next.map(::CmdItem), shell)
    }
}

/** A clickable command in a list: the [command] it runs, shown as [label]. */
private class CmdItem(val command: String, val label: String = command)

/** Commands joined by [DOT_SEP]; the dots are drawn, not read. */
@Composable
private fun CmdList(items: List<CmdItem>, shell: ShellState) {
    items.forEachIndexed { index, item ->
        if (index > 0) Span(attrs = { classes("next-sep"); attr("aria-hidden", "true") }) { Text(DOT_SEP) }
        CmdLink(item.command, shell, item.label)
    }
}

private class MotdRow(val key: String, val text: String, val go: List<CmdItem>)

/** `cat /etc/motd`: what whoami's cards don't already show, each one click from its full output. */
@Composable
private fun MotdOutput(shell: ShellState, reveal: Reveal) {
    val rows = listOf(
        // whoami's Buddy card opens man buddy; its sub-commands sit here.
        MotdRow(
            "buddy",
            "a replay of how its agents built this page",
            listOf(CmdItem("buddy build", "build"), CmdItem("buddy demo", "demo"), CmdItem("buddy screenshots", "screenshots")),
        ),
        MotdRow("projects", (listOf(buddyProject) + sideProjects).joinToString(DOT_SEP) { it.name }, listOf(CmdItem("projects"))),
    )
    Div(attrs = { classes("motd"); revealState(reveal) }) {
        rows.forEachIndexed { index, row ->
            Div(attrs = { classes("motd-row"); streamRow(index) }) {
                Span(attrs = { classes("motd-key") }) { Text(row.key) }
                Span(attrs = { classes("motd-text") }) { Text(row.text) }
                Span(attrs = { classes("motd-go") }) {
                    Span(attrs = { classes("next-lead"); attr("aria-hidden", "true") }) { Text("→ ") }
                    CmdList(row.go, shell)
                }
            }
        }
        Div(attrs = { classes("motd-row", "motd-more"); streamRow(rows.size) }) {
            Span(attrs = { classes("motd-key") }) { Text("more") }
            Span(attrs = { classes("motd-text") }) {
                CmdList(listOf("work", "career", "about", "stack", "stats", "contact", CMD_ALL, CMD_HELP).map(::CmdItem), shell)
            }
        }
        P(attrs = { classes("motd-foot"); streamRow(rows.size + 1) }) {
            Text("Built with Kotlin + Compose (Kobweb)$DOT_SEP")
            Span(attrs = { classes("nowrap") }) { Text("Last updated $LAST_UPDATED") }
            Text(DOT_SEP)
            Span(attrs = { classes("nowrap") }) { Text("${if (isMacLike()) "⌘K" else "Ctrl K"} or / opens the menu") }
        }
    }
}

/** `help`: every command with one line each, all clickable, then links and keys. */
@Composable
private fun HelpOutput(shell: ShellState) {
    H2(attrs = { classes("sr-only") }) { Text("Commands") }
    Dl(attrs = { classes("help") }) {
        sectionCommands.forEach { command ->
            Div(attrs = { classes("help-row") }) {
                Dt { CmdLink(command.name, shell) }
                Dd { Text(command.help) }
            }
        }
        Div(attrs = { classes("help-row") }) {
            Dt { CmdLink(CMD_ALL, shell) }
            Dd { Text(ALL_HELP) }
        }
        Div(attrs = { classes("help-row") }) {
            Dt { CmdLink(CMD_CLEAR, shell) }
            Dd { Text("clear the screen (Ctrl+L)") }
        }
    }
    P(attrs = { classes("help-links") }) {
        Text("links  ")
        CmdList(linkCommands.map { CmdItem(it.name) }, shell)
    }
    P(attrs = { classes("help-keys") }) {
        Text("keys   " + listOf("↑/↓ history", "Tab completes", "Ctrl+L clears", "⌘K or / opens the menu").joinToString(DOT_SEP))
    }
}

/** The pinned prompt: a real input, labelled; a block cursor shows while it's empty and unfocused. */
@Composable
private fun PromptLine(shell: ShellState) {
    Form(attrs = {
        classes("prompt-line")
        onSubmit { event ->
            event.preventDefault()
            shell.submit()
        }
    }) {
        Label(forId = SHELL_INPUT_ID, attrs = { classes("sr-only") }) { Text("Type a command, for example help") }
        Span(attrs = { classes("ps1"); attr("aria-hidden", "true") }) {
            Span(attrs = { classes("ps1-user") }) { Text("deepak") }
            Span(attrs = { classes("ps1-at") }) { Text("@") }
            Span(attrs = { classes("ps1-host") }) { Text("heydeepak.dev") }
            Span(attrs = { classes("ps1-path") }) {
                // Its own span, so phones can drop it and show a short `~$`.
                Span(attrs = { classes("ps1-colon") }) { Text(":") }
                Text("~$")
            }
        }
        Div(attrs = { classes("tty-field") }) {
            Input(type = InputType.Text) {
                id(SHELL_INPUT_ID)
                classes("tty-input")
                value(shell.input)
                // A blank placeholder lets CSS tell an empty field (:placeholder-shown) and show the block cursor.
                attr("placeholder", " ")
                attr("autocomplete", "off")
                attr("autocapitalize", "off")
                attr("spellcheck", "false")
                attr("enterkeyhint", "go")
                onInput { shell.input = it.value }
                onKeyDown { event ->
                    when {
                        event.key == "ArrowUp" -> { event.preventDefault(); shell.historyUp() }
                        event.key == "ArrowDown" -> { event.preventDefault(); shell.historyDown() }
                        // Tab completes when there is something to complete; otherwise focus moves on as usual.
                        event.key == "Tab" && !event.shiftKey -> if (shell.complete()) event.preventDefault()
                        event.ctrlKey && event.key.equals("l", ignoreCase = true) -> { event.preventDefault(); shell.clear() }
                    }
                }
            }
            Span(attrs = { classes("tty-caret"); attr("aria-hidden", "true") })
        }
    }
}

/** The always-visible command chips: the site's nav. */
@Composable
private fun Chips(shell: ShellState) {
    Nav(attrs = { classes("chips"); attr("aria-label", "Commands") }) {
        chipCommands.forEach { name ->
            val help = chipHelp[name] ?: sectionCommands.first { it.section.id == name }.help
            Button(attrs = {
                classes("chip")
                attr("type", "button")
                attr("aria-label", "$name: $help")
                onClick { shell.runTyped(name) }
            }) { Text(name) }
        }
    }
}
