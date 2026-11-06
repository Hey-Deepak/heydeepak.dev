package dev.heydeepak.site.components

import androidx.compose.runtime.*
import dev.heydeepak.site.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.*

private const val COPIED_LABEL_MS = 1600L
internal const val LS_PERMISSIONS = "drwxr-xr-x"

// The section outputs the shell prints; each command's heading and "next" line come from Shell.kt.

/** Source-code styling: [cls] picks the syntax colour. */
@Composable
private fun Tok(cls: String, text: String) = Span(attrs = { classes(cls) }) { Text(text) }

/** One source line; [indent] levels become a hanging indent, so long strings wrap instead of scrolling. */
@Composable
private fun CodeLine(indent: Int = 0, content: @Composable () -> Unit) {
    Div(attrs = { classes("kt-line", "kt-i$indent") }) { content() }
}

/** `cat about.kt` */
@Composable
fun AboutOutput() {
    Div(attrs = { classes("code") }) {
        CodeLine { Tok("tk-kw", "data class"); Text(" "); Tok("tk-type", "Deepak"); Tok("tk-punc", "(") }
        aboutFields.forEach { field ->
            CodeLine(1) {
                Tok("tk-kw", "val"); Text(" "); Tok("tk-prop", field.name); Tok("tk-punc", ": ")
                Tok("tk-type", "String"); Tok("tk-punc", " =")
            }
            CodeLine(2) { Tok("tk-str", "\"${field.text}\""); Tok("tk-punc", ",") }
        }
        CodeLine { Tok("tk-punc", ")") }
    }
}

/** `ls ./work`, then a `git show` per case study. */
@Composable
fun WorkOutput() {
    Div(attrs = { classes("cases") }) {
        caseStudies.forEach { CaseStudyEntry(it) }
    }
}

/** A case study as `git show`: a hunk header with the title, the tags, then the notes as diff lines. */
@Composable
private fun CaseStudyEntry(study: CaseStudy) {
    Article(attrs = { classes("case") }) {
        Prompt("git show ${study.path}", variant = "prompt-sub")
        Div(attrs = { classes("hunk") }) {
            Span(attrs = { classes("hunk-at"); attr("aria-hidden", "true") }) { Text("@@") }
            H3(attrs = { classes("case-title") }) { Text(study.title) }
            Span(attrs = { classes("hunk-at"); attr("aria-hidden", "true") }) { Text("@@") }
        }
        Div(attrs = { classes("diff-tags") }) {
            Span(attrs = { classes("ck"); attr("aria-hidden", "true") }) { Text("tags") }
            TechLine(study.chips)
        }
        Ul(attrs = { classes("diff") }) {
            study.notes.forEach { note ->
                val (sign, kind) = when (note.kind) {
                    DiffKind.CONTEXT -> " " to "ctx"
                    DiffKind.REMOVED -> "-" to "del"
                    DiffKind.ADDED -> "+" to "add"
                }
                Li(attrs = { classes("diff-line", "diff-$kind") }) {
                    Span(attrs = { classes("diff-sign"); attr("aria-hidden", "true") }) { Text(sign) }
                    Span(attrs = { classes("diff-text") }) {
                        Span(attrs = { classes("sr-only") }) { Text("${note.label}: ") }
                        Text(note.text)
                    }
                }
            }
        }
        study.proof?.let { proof ->
            ExternalLink(proof.href, "tlink", "case-proof") {
                Span(attrs = { classes("tlink-lead"); attr("aria-hidden", "true") }) { Text("→") }
                Text(proof.label)
                Span(attrs = { classes("tlink-arrow"); attr("aria-hidden", "true") }) { Text("↗") }
            }
        }
    }
}

/** `git log --graph --oneline career` */
@Composable
fun CareerOutput() {
    Ol(attrs = { classes("graph") }) {
        careerLog.forEach { job -> GraphEntry(job) }
    }
}

@Composable
private fun GraphEntry(job: Job) {
    Li(attrs = {
        classes("gl")
        if (job.isCurrent) classes("is-head")
    }) {
        Span(attrs = { classes("gl-star"); attr("aria-hidden", "true") }) { Text("*") }
        Div(attrs = { classes("gl-head") }) {
            Span(attrs = { classes("gl-since"); attr("aria-hidden", "true") }) { Text(job.since) }
            if (job.isCurrent) {
                Span(attrs = { classes("gl-ref"); attr("aria-hidden", "true") }) { Text("(HEAD → now)") }
            }
            H3(attrs = { classes("gl-org") }) {
                if (job.orgUrl != null) OutLink(job.orgUrl, job.org, "gl-org-link") else Text(job.org)
            }
            Span(attrs = { classes("gl-role") }) { Text(job.role) }
            Span(attrs = { classes("gl-when") }) { Text(job.period) }
        }
        if (job.points.isNotEmpty()) {
            Ul(attrs = { classes("gl-points") }) {
                job.points.forEach { point ->
                    Li {
                        Span(attrs = { classes("gl-plus"); attr("aria-hidden", "true") }) { Text("+") }
                        Text(point)
                    }
                }
            }
        }
    }
}

/** `tree ~/stack` */
@Composable
fun StackOutput() {
    Div(attrs = { classes("tree") }) {
        P(attrs = { classes("tree-root") }) { Text("~/stack") }
        Ul(attrs = { classes("tree-rows") }) {
            stackGroups.forEachIndexed { index, group ->
                Li(attrs = { classes("tree-row") }) {
                    Span(attrs = { classes("tree-branch"); attr("aria-hidden", "true") }) {
                        Text(if (index == stackGroups.lastIndex) "└── " else "├── ")
                    }
                    Span(attrs = { classes("tree-dir") }) { Text("${group.key}/") }
                    DotList(group.items, "tree-items")
                }
            }
        }
    }
}

/** `ls -l ~/projects`, then `cat recognition.txt`. The live `buddy ps` is in the `buddy` output. */
@Composable
fun ProjectsOutput() {
    Div(attrs = { classes("projects") }) {
        ProjectEntry(buddyProject, featured = true)
        sideProjects.forEach { ProjectEntry(it) }
    }
    Prompt("cat recognition.txt", variant = "prompt-sub")
    Ul(attrs = { classes("recog"); attr("aria-label", "Recognition") }) {
        recognition.forEach { item ->
            Li {
                Span(attrs = { classes("recog-star"); attr("aria-hidden", "true") }) { Text("★") }
                Text(item)
            }
        }
    }
}

/** A project as an `ls -l` row, its details indented under the name. */
@Composable
private fun ProjectEntry(project: Project, featured: Boolean = false) {
    Article(attrs = {
        classes("project")
        if (featured) classes("is-featured")
    }) {
        Div(attrs = { classes("ls-row") }) {
            Span(attrs = { classes("ls-perm"); attr("aria-hidden", "true") }) { Text(LS_PERMISSIONS) }
            H3(attrs = { classes("project-name") }) {
                Text(project.name)
                Span(attrs = { classes("ls-slash"); attr("aria-hidden", "true") }) { Text("/") }
            }
            P(attrs = {
                classes("project-meta")
                if (project.status == ProjectStatus.RUNNING) classes("is-running")
            }) {
                if (project.status == ProjectStatus.RUNNING) {
                    Span(attrs = { classes("status-dot"); attr("aria-hidden", "true") }) { Text("●") }
                }
                Text(project.meta)
            }
        }
        Div(attrs = { classes("project-body") }) {
            P(attrs = { classes("project-desc") }) { Text(project.description) }
            TechLine(project.chips)
            ExternalLink(project.url, "tlink", "project-link") {
                Span(attrs = { classes("tlink-lead"); attr("aria-hidden", "true") }) { Text("→") }
                Text(project.urlLabel)
                Span(attrs = { classes("tlink-arrow"); attr("aria-hidden", "true") }) { Text("↗") }
            }
        }
    }
}

/**
 * An htop-like table of Buddy's parts; hidden from screen readers, the text around it says it in words.
 * Once in view (motion allowed) the dots pulse and a selection bar steps down the rows like a refresh.
 */
@Composable
fun BuddyPs() {
    val motion = LocalMotion.current
    var live by remember { mutableStateOf(false) }
    Div(attrs = {
        classes("ps")
        if (live) classes("is-live")
        attr("aria-hidden", "true")
        if (motion) {
            ref { element ->
                val stop = observeOnce(element, rootMargin = "0px") { live = true }
                onDispose { stop() }
            }
        }
    }) {
        P(attrs = { classes("ps-title") }) { Text(BUDDY_PS_TITLE) }
        Div(attrs = { classes("ps-grid") }) {
            Span(attrs = { classes("ps-sel") })
            listOf("NAME", "WHERE", "STATUS").forEach { Span(attrs = { classes("ps-cell", "ps-head") }) { Text(it) } }
            buddyProcesses.forEach { process ->
                Span(attrs = { classes("ps-cell") }) { Text(process.name) }
                Span(attrs = { classes("ps-cell", "ps-where") }) { Text(process.where) }
                Span(attrs = { classes("ps-cell", "ps-status") }) {
                    Span(attrs = { classes("status-dot") }) { Text("●") }
                    Text(" ${process.status}")
                }
            }
        }
    }
}

/** `./contact.sh` */
@Composable
fun ContactOutput() {
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(COPIED_LABEL_MS)
            copied = false
        }
    }
    Div(attrs = { classes("contact") }) {
        Div(attrs = { classes("contact-main") }) {
            P(attrs = { classes("contact-lead") }) { Text("Email reaches me fastest.") }
            A(href = "mailto:${Profile.EMAIL}", attrs = { classes("contact-email") }) { Text(Profile.EMAIL) }
            Div(attrs = { classes("contact-actions") }) {
                Button(attrs = {
                    classes("tbtn", "copy-btn")
                    if (copied) classes("is-done")
                    attr("type", "button")
                    attr("aria-label", if (copied) "Email address copied" else "Copy email address")
                    onClick { copyToClipboard(Profile.EMAIL, onCopied = { copied = true }, onFailed = {}) }
                }) { TermButtonLabel(if (copied) "copied ✓" else "copy") }
                ResumeButton()
            }
        }
        Dl(attrs = { classes("kv") }) {
            ContactRow("resume", Profile.RESUME, "resume.pdf")
            ContactRow("linkedin", Profile.LINKEDIN, "linkedin.com/in/hey-deepak")
            ContactRow("github", Profile.GITHUB, "github.com/Hey-Deepak")
            ContactRow("medium", Profile.MEDIUM, "medium.com/@HeyDeepak")
        }
    }
    Span(attrs = { classes("sr-only"); attr("role", "status") }) { if (copied) Text("Email copied") }
}

@Composable
private fun ContactRow(key: String, href: String, label: String) {
    Div(attrs = { classes("kv-row") }) {
        Dt(attrs = { classes("kv-key") }) { Text(key) }
        Dd(attrs = { classes("kv-val") }) { OutLink(href, label, "kv-link") }
    }
}
