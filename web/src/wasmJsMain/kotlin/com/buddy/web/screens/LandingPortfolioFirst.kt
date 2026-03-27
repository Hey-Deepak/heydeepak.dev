package com.buddy.web.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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

/**
 * OPTION A: Portfolio-first
 * Landing is about Deepak. Buddy is a featured project.
 */
@Composable
fun LandingPortfolioFirst(onNavigateToDashboard: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(BuddyColors.Background),
    ) {
        // Hero — About Deepak
        PortfolioHero()

        Spacer(Modifier.height(80.dp))

        // What I Do
        WhatIDoSection()

        Spacer(Modifier.height(80.dp))

        // Featured Project — Buddy
        FeaturedProjectSection(onNavigateToDashboard)

        Spacer(Modifier.height(80.dp))

        // Skills / Tech
        SkillsSection()

        Spacer(Modifier.height(80.dp))

        // Build in Public / Journey
        JourneySection()

        Spacer(Modifier.height(80.dp))

        // Connect
        ConnectSection()

        Spacer(Modifier.height(64.dp))

        // Footer
        PortfolioFooter()
    }
}

@Composable
private fun PortfolioHero() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Terminal prompt
        Text(
            text = "$ whoami",
            style = MaterialTheme.typography.bodyMedium,
            color = BuddyColors.Teal,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(BuddyColors.TealDim)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        )

        Spacer(Modifier.height(24.dp))

        // Avatar placeholder
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(BuddyColors.Amber.copy(alpha = 0.15f))
                .border(2.dp, BuddyColors.Amber.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "D",
                style = MaterialTheme.typography.displayMedium,
                color = BuddyColors.Amber,
            )
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Deepak",
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Senior Android Engineer",
            style = MaterialTheme.typography.headlineMedium,
            color = BuddyColors.Amber,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "5 years building Android apps. Now building a personal AI assistant " +
                    "with Kotlin Multiplatform that manages AI agents from your phone.",
            style = MaterialTheme.typography.bodyLarge,
            color = BuddyColors.TextMid,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 560.dp),
        )

        Spacer(Modifier.height(32.dp))

        // Links
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LinkButton("GitHub", BuddyColors.TextPrimary)
            LinkButton("LinkedIn", BuddyColors.Amber)
        }
    }
}

@Composable
private fun WhatIDoSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionTag("WHAT I DO")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Android + AI + Multiplatform",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TerminalCard(modifier = Modifier.weight(1f), borderColor = BuddyColors.Amber.copy(alpha = 0.3f)) {
                Text("Android", style = MaterialTheme.typography.titleLarge, color = BuddyColors.Amber)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Jetpack Compose, MVVM, Kotlin coroutines, " +
                            "custom views, performance optimization. " +
                            "5 years shipping production apps.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            TerminalCard(modifier = Modifier.weight(1f), borderColor = BuddyColors.Teal.copy(alpha = 0.3f)) {
                Text("AI Engineering", style = MaterialTheme.typography.titleLarge, color = BuddyColors.Teal)
                Spacer(Modifier.height(8.dp))
                Text(
                    "AI agent orchestration, MCP servers, A2A protocol, " +
                            "Claude + Gemini integration, multi-agent pipelines " +
                            "with quality gates.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            TerminalCard(modifier = Modifier.weight(1f), borderColor = BuddyColors.Purple.copy(alpha = 0.3f)) {
                Text("Multiplatform", style = MaterialTheme.typography.titleLarge, color = BuddyColors.Purple)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Kotlin Multiplatform from shared models to " +
                            "shared UI. Single codebase targeting " +
                            "Android, iOS, JVM, and Web (Wasm).",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun FeaturedProjectSection(onNavigateToDashboard: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionTag("FEATURED PROJECT")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Buddy",
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Your personal AI assistant — like Jarvis",
            style = MaterialTheme.typography.bodyLarge,
            color = BuddyColors.Amber,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Lives on your phone. Manages a fleet of specialist AI agents on your PC. " +
                    "Remote control mode or full autopilot. Agent Store where you hire and fire AI.",
            style = MaterialTheme.typography.bodyLarge,
            color = BuddyColors.TextMid,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 640.dp),
        )

        Spacer(Modifier.height(32.dp))

        // Architecture mini
        CodeBlock(
            modifier = Modifier.widthIn(max = 600.dp).fillMaxWidth(),
            code = "Phone (Compose)  <-- WebSocket -->  PC (Ktor Daemon)\n" +
                    "  Chat UI                            7-agent pipeline\n" +
                    "  Voice I/O                          Claude / Gemini CLI\n" +
                    "  Permissions                        MCP + A2A servers\n" +
                    "\n" +
                    "  :shared (KMP) -- wire protocol across all platforms",
        )

        Spacer(Modifier.height(32.dp))

        // Stats row
        Row(
            modifier = Modifier.widthIn(max = 700.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            MiniStat("7", "Gradle Modules", BuddyColors.Amber, Modifier.weight(1f))
            MiniStat("4", "Platform Targets", BuddyColors.Teal, Modifier.weight(1f))
            MiniStat("120+", "Kotlin Files", BuddyColors.Purple, Modifier.weight(1f))
            MiniStat("20+", "API Endpoints", BuddyColors.TextPrimary, Modifier.weight(1f))
        }

        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(BuddyColors.Amber)
                    .clickable { }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text("View Source", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = BuddyColors.Background)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, BuddyColors.Amber, RoundedCornerShape(8.dp))
                    .clickable { onNavigateToDashboard() }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text("Live Dashboard", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = BuddyColors.Amber)
            }
        }
    }
}

@Composable
private fun SkillsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionTag("SKILLS")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "What I work with",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))

        val skills = listOf(
            "Languages" to "Kotlin, Java, TypeScript, SQL",
            "Android" to "Compose, Coroutines, Room, Ktor, Koin, Navigation",
            "Backend" to "Ktor, Exposed, WebSocket, SSE, REST, JSON-RPC",
            "AI / ML" to "Claude API, Gemini API, MCP, A2A, agent pipelines",
            "KMP" to "Shared models, Compose Multiplatform, expect/actual",
            "DevOps" to "Gradle, tmux, adb, Git, CI/CD",
        )

        Column(
            modifier = Modifier.widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            skills.forEach { (category, items) ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.titleMedium,
                        color = BuddyColors.Amber,
                        modifier = Modifier.width(120.dp),
                    )
                    Text(
                        text = items,
                        style = MaterialTheme.typography.bodyMedium,
                        color = BuddyColors.TextMid,
                    )
                }
            }
        }
    }
}

@Composable
private fun JourneySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionTag("BUILD IN PUBLIC")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "The journey, not just the destination",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "I share the real process — architecture decisions, bugs that took hours, " +
                    "tradeoffs, and breakthroughs. No sugarcoating.",
            style = MaterialTheme.typography.bodyLarge,
            color = BuddyColors.TextMid,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 560.dp),
        )

        Spacer(Modifier.height(32.dp))

        // Timeline highlights
        Column(
            modifier = Modifier.widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TimelineItem("Phase 1-3", "Core pipeline, voice, networking", "Foundation")
            TimelineItem("Phase 4-5", "AI providers, Koog pipeline, MCP server", "Intelligence")
            TimelineItem("Phase 6", "A2A protocol, ACP agent, worktree isolation", "Interop")
            TimelineItem("Now", "Web module (this site), Kotlin/Wasm", "Platform expansion")
        }
    }
}

@Composable
private fun TimelineItem(phase: String, desc: String, tag: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Dot
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(BuddyColors.Amber),
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .background(BuddyColors.Border),
            )
        }
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(phase, style = MaterialTheme.typography.titleMedium, color = BuddyColors.TextPrimary)
                Text(
                    text = tag,
                    fontSize = 10.sp,
                    color = BuddyColors.Teal,
                    modifier = Modifier
                        .clip(RoundedCornerShape(2.dp))
                        .background(BuddyColors.TealDim)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }
            Text(desc, style = MaterialTheme.typography.bodyMedium, color = BuddyColors.TextMid)
        }
    }
}

@Composable
private fun ConnectSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SectionTag("CONNECT")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Let's build something together",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LinkButton("GitHub", BuddyColors.TextPrimary)
            LinkButton("LinkedIn", BuddyColors.Amber)
            LinkButton("Twitter / X", BuddyColors.Teal)
        }
    }
}

// --- Shared helpers for this file ---

@Composable
private fun MiniStat(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    TerminalCard(modifier = modifier, borderColor = color.copy(alpha = 0.2f)) {
        Text(value, style = MaterialTheme.typography.headlineLarge, color = color)
        Spacer(Modifier.height(2.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = BuddyColors.TextDim)
    }
}

@Composable
private fun LinkButton(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .clickable { }
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = color)
    }
}

@Composable
private fun SectionTag(text: String) {
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

@Composable
private fun PortfolioFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BuddyColors.Surface)
            .border(width = 1.dp, color = BuddyColors.Border)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Deepak", style = MaterialTheme.typography.titleMedium, color = BuddyColors.Amber)
        Spacer(Modifier.height(4.dp))
        Text(
            "Senior Android Engineer // Building Buddy in public",
            style = MaterialTheme.typography.bodySmall, color = BuddyColors.TextDim,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Built with Kotlin/Wasm + Compose Multiplatform",
            style = MaterialTheme.typography.bodySmall, color = BuddyColors.TextDim,
        )
    }
}
