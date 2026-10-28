package dev.heydeepak.site.components

import androidx.compose.runtime.*
import dev.heydeepak.site.*
import kotlinx.coroutines.delay
import org.jetbrains.compose.web.dom.*

private const val COPIED_LABEL_MS = 1600L

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
            Span(attrs = { classes("commit-hash"); attr("aria-hidden", "true") }) { Text(job.hash) }
            if (job.isCurrent) {
                Span(attrs = { classes("commit-ref"); attr("aria-hidden", "true") }) { Text("(HEAD → now)") }
            }
            H3(attrs = { classes("commit-org") }) { Text(job.org) }
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
            Span { Text("Built with Kotlin + Compose (Kobweb) · heydeepak.dev") }
            Span(attrs = { classes("footer-hint") }) {
                Span(attrs = { classes("kbd") }) { Text(if (isMacLike()) "⌘K" else "Ctrl K") }
                Text(" or ")
                Span(attrs = { classes("kbd") }) { Text("/") }
                Text(" to jump anywhere")
            }
        }
    }
}
