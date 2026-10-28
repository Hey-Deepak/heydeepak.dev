package dev.heydeepak.site.components

import androidx.compose.runtime.*
import dev.heydeepak.site.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.*

private const val COPIED_LABEL_MS = 1600L

/** Source-code line styling: [cls] picks the syntax colour. */
@Composable
private fun Tok(cls: String, text: String) = Span(attrs = { classes(cls) }) { Text(text) }

/** A named, focusable region so keyboard users can scroll a listing wider than the screen. */
@Composable
private fun CodeBlock(fileName: String, content: @Composable () -> Unit) {
    Pre(attrs = {
        classes("code")
        attr("tabindex", "0")
        attr("role", "region")
        attr("aria-label", fileName)
    }) {
        Code { content() }
    }
}

@Composable
fun AboutSection() {
    TermSection(SiteSection.ABOUT.id, "cat about.kt", "About") {
        CodeBlock("about.kt") {
            Tok("tk-kw", "data class"); Text(" "); Tok("tk-type", "Deepak"); Tok("tk-punc", "(")
            aboutFields.forEach { field ->
                Text("\n    ")
                Tok("tk-kw", "val"); Text(" "); Tok("tk-prop", field.name); Tok("tk-punc", ": ")
                Tok("tk-type", "String"); Tok("tk-punc", " = ")
                Tok("tk-str", "\"${field.text}\""); Tok("tk-punc", ",")
            }
            Text("\n"); Tok("tk-punc", ")")
        }
    }
}

@Composable
fun StackSection() {
    val keyWidth = stackGroups.maxOf { it.key.length }
    TermSection(SiteSection.STACK.id, "cat stack.json", "Stack") {
        CodeBlock("stack.json") {
            Tok("tk-punc", "{")
            stackGroups.forEachIndexed { index, group ->
                Text("\n  ")
                Tok("tk-key", "\"${group.key}\""); Tok("tk-punc", ":")
                // Pads values into one column, like the hand-aligned file.
                Text(" ".repeat(keyWidth - group.key.length + 1))
                Tok("tk-punc", "[")
                group.items.forEachIndexed { itemIndex, item ->
                    if (itemIndex > 0) Tok("tk-punc", ", ")
                    Tok("tk-jstr", "\"$item\"")
                }
                Tok("tk-punc", if (index < stackGroups.lastIndex) "]," else "]")
            }
            Text("\n"); Tok("tk-punc", "}")
        }
    }
}

@Composable
fun WorkSection() {
    TermSection(SiteSection.WORK.id, "ls ./work", "Selected work") {
        Div(attrs = { classes("cases") }) {
            caseStudies.forEach { CaseCard(it) }
        }
    }
}

@Composable
private fun CaseCard(study: CaseStudy) {
    Article(attrs = { classes("case") }) {
        Div(attrs = { classes("case-head") }) {
            H3(attrs = { classes("case-title") }) { Text(study.title) }
            Span(attrs = { classes("case-path"); attr("aria-hidden", "true") }) { Text(study.path) }
        }
        Div(attrs = {
            classes("case-grid")
            if (study.notes.size == 2) classes("cols-2")
        }) {
            study.notes.forEach { note ->
                Div(attrs = {
                    classes("note")
                    if (note.isResult) classes("is-result")
                }) {
                    Div(attrs = { classes("note-label") }) { Text("// ${note.label}") }
                    P(attrs = { classes("note-text") }) { Text(note.text) }
                }
            }
        }
        Chips(study.chips)
        study.proof?.let { proof -> ExternalLink(proof.href, "case-proof") { Text(proof.label) } }
    }
}

@Composable
fun CareerSection() {
    TermSection(SiteSection.CAREER.id, "git log --oneline career", "Career") {
        Ol(attrs = { classes("log") }) {
            careerLog.forEach { job -> Commit(job) }
        }
    }
}

@Composable
private fun Commit(job: Job) {
    Li(attrs = {
        classes("commit")
        if (job.isCurrent) classes("is-head")
    }) {
        Span(attrs = { classes("commit-node"); attr("aria-hidden", "true") })
        Div(attrs = { classes("commit-line") }) {
            Span(attrs = { classes("commit-hash"); attr("aria-hidden", "true") }) { Text(job.since) }
            if (job.isCurrent) {
                Span(attrs = { classes("commit-ref"); attr("aria-hidden", "true") }) { Text("(HEAD → now)") }
            }
            H3(attrs = { classes("commit-org") }) {
                if (job.orgUrl != null) ExternalLink(job.orgUrl, "commit-org-link") { Text(job.org) } else Text(job.org)
            }
            Span(attrs = { classes("commit-role") }) { Text(job.role) }
            Span(attrs = { classes("commit-when") }) { Text(job.period) }
        }
        if (job.points.isNotEmpty()) {
            Ul(attrs = { classes("commit-points") }) {
                job.points.forEach { point -> Li { Text(point) } }
            }
        }
    }
}

@Composable
fun BuildingSection() {
    TermSection(SiteSection.BUILDING.id, "ls ~/projects", "Building in public") {
        Div(attrs = { classes("projects") }) {
            ProjectCard(buddyProject, featured = true) { BuddyDiagram() }
            sideProjects.forEach { ProjectCard(it) }
        }
        Div(attrs = { classes("recog") }) {
            Span(attrs = { classes("recog-label") }) { Text("recognition") }
            Ul(attrs = { classes("badges") }) {
                recognition.forEach { item -> Li(attrs = { classes("badge") }) { Text(item) } }
            }
        }
    }
}

@Composable
private fun ProjectCard(project: Project, featured: Boolean = false, extra: @Composable () -> Unit = {}) {
    Article(attrs = {
        classes("project")
        if (featured) classes("is-featured")
    }) {
        Div(attrs = {
            classes("project-meta")
            if (project.status == ProjectStatus.RUNNING) classes("is-running")
        }) {
            if (project.status == ProjectStatus.RUNNING) {
                Span(attrs = { classes("status-dot"); attr("aria-hidden", "true") })
            }
            Text(project.meta)
        }
        H3(attrs = { classes("project-name") }) { Text(project.name) }
        P(attrs = { classes("project-desc") }) { Text(project.description) }
        extra()
        Chips(project.chips)
        ExternalLink(project.url, "project-link") { Text(project.urlLabel) }
    }
}

/** How Buddy is wired, drawn as terminal box-art. */
@Composable
private fun BuddyDiagram() {
    Pre(attrs = { classes("diagram"); attr("aria-hidden", "true") }) {
        Span(attrs = { classes("d-node") }) { Text("phone") }
        Text(" ──▶ ")
        Span(attrs = { classes("d-node") }) { Text("daemon on your PC") }
        Text("\n")
        listOf("├", "├", "└").forEachIndexed { index, branch ->
            Text("            $branch──▶ claude code ")
            Span(attrs = { classes("d-dim") }) { Text("agent ${index + 1}  ") }
            Span(attrs = { classes("d-run") }) { Text("●") }
            Text("\n")
        }
    }
}

@Composable
fun ContactSection() {
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            delay(COPIED_LABEL_MS)
            copied = false
        }
    }
    TermSection(SiteSection.CONTACT.id, "./contact.sh", "Contact") {
        Div(attrs = { classes("contact") }) {
            Div(attrs = { classes("contact-main") }) {
                P(attrs = { classes("contact-lead") }) { Text("Email reaches me fastest.") }
                Div(attrs = { classes("contact-email-row") }) {
                    A(href = "mailto:${Profile.EMAIL}", attrs = { classes("contact-email") }) { Text(Profile.EMAIL) }
                    Button(attrs = {
                        classes("kv-action")
                        if (copied) classes("is-done")
                        attr("type", "button")
                        attr("aria-label", "Copy email address")
                        onClick { copyToClipboard(Profile.EMAIL, onCopied = { copied = true }, onFailed = {}) }
                    }) { Text(if (copied) "copied ✓" else "copy") }
                }
                ExternalLink(Profile.RESUME, "btn", "btn-primary") {
                    Span(attrs = { classes("btn-glyph"); attr("aria-hidden", "true") }) { Text("↓") }
                    Text("Resume")
                }
            }
            Div(attrs = { classes("kv") }) {
                ContactRow("resume", Profile.RESUME, "resume.pdf")
                ContactRow("linkedin", Profile.LINKEDIN, "linkedin.com/in/hey-deepak")
                ContactRow("github", Profile.GITHUB, "github.com/Hey-Deepak")
                ContactRow("medium", Profile.MEDIUM, "medium.com/@HeyDeepak")
            }
        }
        Span(attrs = { classes("sr-only"); attr("role", "status") }) { if (copied) Text("Email copied") }
    }
}

@Composable
private fun ContactRow(key: String, href: String, label: String) {
    Div(attrs = { classes("kv-row") }) {
        Span(attrs = { classes("kv-key") }) { Text(key) }
        ExternalLink(href, "kv-val") { Text(label) }
        Span(attrs = { classes("kv-glyph"); attr("aria-hidden", "true") }) { Text("↗") }
    }
}

@Composable
fun SiteFooter() {
    Footer(attrs = { classes("footer") }) {
        Div(attrs = { classes("wrap", "footer-inner") }) {
            Span { Text("Built with Kotlin + Compose (Kobweb) · heydeepak.dev · Last updated $LAST_UPDATED") }
            Span(attrs = { classes("footer-hint") }) {
                Span(attrs = { classes("kbd") }) { Text(if (isMacLike()) "⌘K" else "Ctrl K") }
                Text(" or ")
                Span(attrs = { classes("kbd") }) { Text("/") }
                Text(" to jump anywhere")
            }
        }
    }
}
