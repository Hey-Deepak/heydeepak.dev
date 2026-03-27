package com.buddy.web.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buddy.web.components.CodeBlock
import com.buddy.web.components.TerminalCard
import com.buddy.web.theme.BuddyColors

@Composable
fun LandingScreen(
    onNavigateToDashboard: () -> Unit,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(BuddyColors.Background),
    ) {
        // Hero Section
        HeroSection(onNavigateToDashboard)

        Spacer(Modifier.height(80.dp))

        // Two Modes Section
        TwoModesSection()

        Spacer(Modifier.height(80.dp))

        // Architecture Section
        ArchitectureSection()

        Spacer(Modifier.height(80.dp))

        // Agent Store Section
        AgentStoreSection()

        Spacer(Modifier.height(80.dp))

        // Tech Stack Section
        TechStackSection()

        Spacer(Modifier.height(64.dp))

        // Footer
        FooterSection()
    }
}

@Composable
private fun HeroSection(onNavigateToDashboard: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Terminal prompt style tag
        Text(
            text = "$ ./buddy --mode personal-ai",
            style = MaterialTheme.typography.bodyMedium,
            color = BuddyColors.Teal,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(BuddyColors.TealDim)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        )

        Spacer(Modifier.height(24.dp))

        // Main headline
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = BuddyColors.TextPrimary)) {
                    append("Your personal AI.\n")
                }
                withStyle(SpanStyle(color = BuddyColors.Amber)) {
                    append("On your phone.\n")
                }
                withStyle(SpanStyle(color = BuddyColors.TextPrimary)) {
                    append("Managing agents on your PC.")
                }
            },
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Buddy is a personal AI assistant \u2014 like Jarvis \u2014 that lives on your phone " +
                    "and orchestrates specialist AI agents running on your machine.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = BuddyColors.TextMid,
            modifier = Modifier.widthIn(max = 640.dp),
        )

        Spacer(Modifier.height(40.dp))

        // CTA buttons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Primary CTA
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(BuddyColors.Amber)
                    .clickable { /* TODO: GitHub link */ }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "View on GitHub",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = BuddyColors.Background,
                )
            }

            // Secondary CTA
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, BuddyColors.Amber, RoundedCornerShape(8.dp))
                    .clickable { onNavigateToDashboard() }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text(
                    text = "Open Dashboard",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = BuddyColors.Amber,
                )
            }
        }
    }
}

@Composable
private fun TwoModesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionLabel("TWO MODES")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Remote control or full autopilot",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(40.dp))

        Row(
            modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // PC Sessions card
            TerminalCard(
                modifier = Modifier.weight(1f),
                borderColor = BuddyColors.Amber.copy(alpha = 0.3f),
            ) {
                Text(
                    text = "PC Sessions",
                    style = MaterialTheme.typography.titleLarge,
                    color = BuddyColors.Amber,
                )
                Text(
                    text = "Remote Control",
                    style = MaterialTheme.typography.labelMedium,
                    color = BuddyColors.TextDim,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "You drive. Phone remotely controls Claude Code and Gemini CLI " +
                            "running in tmux sessions on your PC. Full visibility, real-time streaming, " +
                            "permission approvals from your pocket.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(16.dp))
                CodeBlock(
                    code = "Phone \u2192 WebSocket \u2192 Daemon \u2192 tmux \u2192 Claude CLI\n" +
                            "Phone \u2190 stream \u2190 Daemon \u2190 relay \u2190 hooks",
                )
            }

            // Mobile Sessions card
            TerminalCard(
                modifier = Modifier.weight(1f),
                borderColor = BuddyColors.Teal.copy(alpha = 0.3f),
            ) {
                Text(
                    text = "Mobile Sessions",
                    style = MaterialTheme.typography.titleLarge,
                    color = BuddyColors.Teal,
                )
                Text(
                    text = "Personal Assistant",
                    style = MaterialTheme.typography.labelMedium,
                    color = BuddyColors.TextDim,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Buddy drives. Talk to Buddy about anything \u2014 work, life, ideas. " +
                            "Buddy delegates to specialist agents hired from the Agent Store. " +
                            "Invisible tmux, seamless orchestration.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(16.dp))
                CodeBlock(
                    code = "You \u2192 Buddy \u2192 7-agent pipeline\n" +
                            "       TPM \u2192 TechLead \u2192 Coder \u2192 Reviewer\n" +
                            "       \u2192 Builder \u2192 Deployer \u2192 Debugger",
                )
            }
        }
    }
}

@Composable
private fun ArchitectureSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionLabel("ARCHITECTURE")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Built with Kotlin Multiplatform",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(40.dp))

        CodeBlock(
            modifier = Modifier.widthIn(max = 700.dp).fillMaxWidth(),
            code = """
Phone (Android/iOS)              PC (JVM)
\u250c\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510    WebSocket    \u250c\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510
\u2502  :app            \u2502\u25c4\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u25ba\u2502  :daemon          \u2502
\u2502  :composeApp     \u2502   BuddyMessage \u2502                  \u2502
\u2502  (Compose UI)    \u2502                \u2502  7-agent pipeline \u2502
\u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518                \u2502  Ktor server      \u2502
                                   \u2502  MCP / A2A / ACP  \u2502
        :shared (KMP)              \u2514\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2518
    Wire protocol, models               \u2502
    Android, iOS, JVM, Web              \u2502
                                   \u250c\u2500\u2500\u2500\u2500\u2534\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2510
                               :buddy-cli    :acp-agent
                               (tmux mgmt)   (IDE agent)
            """.trimIndent(),
        )

        Spacer(Modifier.height(32.dp))

        // Module cards
        val modules = listOf(
            Triple(":shared", "Wire protocol, models", "KMP \u2014 Android, iOS, JVM, Web"),
            Triple(":composeApp", "Compose Multiplatform UI", "Screens, ViewModels, navigation"),
            Triple(":daemon", "Ktor Netty server", "Agent pipeline, MCP, A2A, executors"),
            Triple(":app", "Android wrapper", "MainActivity, services, notifications"),
            Triple(":buddy-cli", "Terminal manager", "tmux session management"),
            Triple(":acp-agent", "IDE agent", "Android Studio \u2194 daemon bridge"),
            Triple(":web", "Web dashboard", "Landing page, daemon visualization"),
        )

        FlowRow(
            modifier = Modifier.widthIn(max = 900.dp),
            modules = modules,
        )
    }
}

@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    modules: List<Triple<String, String, String>>,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        modules.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                row.forEach { (name, desc, detail) ->
                    TerminalCard(modifier = Modifier.weight(1f)) {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            color = BuddyColors.Amber,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(text = desc, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(2.dp))
                        Text(text = detail, style = MaterialTheme.typography.bodySmall)
                    }
                }
                // Pad incomplete rows
                repeat(3 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun AgentStoreSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionLabel("AGENT STORE")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "LinkedIn for AI agents",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Agents have profiles, ratings, and trust levels. " +
                    "Hire specialists for your tasks. Fire the ones that don't perform.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = BuddyColors.TextMid,
            modifier = Modifier.widthIn(max = 600.dp),
        )
        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.widthIn(max = 700.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            listOf(
                Triple("ELITE", "\u2b50 4.8+", BuddyColors.Amber),
                Triple("TRUSTED", "\u2705 4.5+", BuddyColors.Teal),
                Triple("VERIFIED", "\u2714 3.0+", BuddyColors.Purple),
                Triple("NEW", "\ud83c\udd95 Unrated", BuddyColors.TextDim),
            ).forEach { (level, criteria, color) ->
                TerminalCard(
                    modifier = Modifier.weight(1f),
                    borderColor = color.copy(alpha = 0.3f),
                ) {
                    Text(
                        text = level,
                        style = MaterialTheme.typography.labelLarge,
                        color = color,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = criteria,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
private fun TechStackSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionLabel("TECH STACK")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Pure Kotlin. All the way down.",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))

        val techs = listOf(
            "Kotlin 2.3" to "Multiplatform \u2014 Android, iOS, JVM, Web",
            "Compose Multiplatform 1.10" to "Shared UI across all platforms",
            "Ktor 3.4" to "Server (daemon) + Client (phone, web)",
            "Koin 4.0" to "Dependency injection everywhere",
            "Koog 0.6" to "AI agent framework + A2A protocol",
            "MCP SDK 0.8" to "Model Context Protocol server + client",
        )

        Column(
            modifier = Modifier.widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            techs.forEach { (name, desc) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        color = BuddyColors.Amber,
                        modifier = Modifier.width(240.dp),
                    )
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BuddyColors.TextMid,
                    )
                }
            }
        }
    }
}

@Composable
private fun FooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BuddyColors.Surface)
            .border(width = 1.dp, color = BuddyColors.Border)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Built in public by Deepak",
            style = MaterialTheme.typography.bodyMedium,
            color = BuddyColors.TextDim,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Kotlin \u2022 Compose Multiplatform \u2022 Ktor \u2022 Wasm",
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.TextDim,
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 2.sp,
        color = BuddyColors.Amber,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(BuddyColors.AmberDim)
            .padding(horizontal = 12.dp, vertical = 4.dp),
    )
}
