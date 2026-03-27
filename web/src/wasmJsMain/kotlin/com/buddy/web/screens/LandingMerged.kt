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
 * OPTION C: Merged
 * Deepak's story and Buddy's story interleaved — one narrative.
 * "I'm Deepak. I'm building Buddy. Here's the journey."
 */
@Composable
fun LandingMerged(onNavigateToDashboard: () -> Unit) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(BuddyColors.Background),
    ) {
        // Hero — The hook
        MergedHero()

        Spacer(Modifier.height(80.dp))

        // The Story
        TheStorySection()

        Spacer(Modifier.height(80.dp))

        // What Buddy Does
        WhatBuddyDoesSection()

        Spacer(Modifier.height(80.dp))

        // How It's Built
        HowItsBuiltSection()

        Spacer(Modifier.height(80.dp))

        // The Numbers
        TheNumbersSection()

        Spacer(Modifier.height(80.dp))

        // What's Next
        WhatsNextSection(onNavigateToDashboard)

        Spacer(Modifier.height(80.dp))

        // Follow the journey
        FollowSection()

        Spacer(Modifier.height(64.dp))

        // Footer
        MergedFooter()
    }
}

@Composable
private fun MergedHero() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Terminal prompt
        Text(
            text = "$ cat ~/story.md",
            style = MaterialTheme.typography.bodyMedium,
            color = BuddyColors.Teal,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(BuddyColors.TealDim)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        )

        Spacer(Modifier.height(32.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = BuddyColors.TextPrimary)) {
                    append("I'm building my own ")
                }
                withStyle(SpanStyle(color = BuddyColors.Amber)) {
                    append("Jarvis")
                }
                withStyle(SpanStyle(color = BuddyColors.TextPrimary)) {
                    append(".")
                }
            },
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = BuddyColors.TextMid)) {
                    append("An Android engineer with 5 years of shipping apps,\n")
                    append("building a personal AI assistant that lives on my phone\n")
                    append("and manages AI agents on my PC. ")
                }
                withStyle(SpanStyle(color = BuddyColors.Amber)) {
                    append("In public.")
                }
            },
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 600.dp),
        )

        Spacer(Modifier.height(32.dp))

        // Identity row
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IdentityChip("Deepak", BuddyColors.TextPrimary)
            Text("/", color = BuddyColors.TextDim, fontSize = 18.sp)
            IdentityChip("Senior Android Engineer", BuddyColors.Amber)
            Text("/", color = BuddyColors.TextDim, fontSize = 18.sp)
            IdentityChip("#BuildInPublic", BuddyColors.Teal)
        }
    }
}

@Composable
private fun IdentityChip(text: String, color: Color) {
    Text(
        text = text,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = color,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}

@Composable
private fun TheStorySection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MergedTag("THE STORY")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Why I'm building this",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier.widthIn(max = 640.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StoryBlock(
                "The problem",
                "AI tools are powerful but they're trapped in terminals. " +
                        "Claude Code runs on my laptop. Gemini CLI runs on my laptop. " +
                        "But I'm not always at my laptop.",
            )
            StoryBlock(
                "The idea",
                "What if my phone could be the remote control? Walk away from the desk, " +
                        "keep approving permissions, keep reviewing output, keep the AI working. " +
                        "Better yet — what if it could drive on its own?",
            )
            StoryBlock(
                "The result",
                "Buddy. A personal AI that lives on your phone and orchestrates specialist agents " +
                        "on your machine. Two modes: you drive (remote control) or Buddy drives (personal assistant). " +
                        "An Agent Store where AI agents have profiles, ratings, and trust levels.",
            )
        }
    }
}

@Composable
private fun StoryBlock(title: String, body: String) {
    TerminalCard(modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = BuddyColors.Amber)
        Spacer(Modifier.height(8.dp))
        Text(body, style = MaterialTheme.typography.bodyMedium, color = BuddyColors.TextMid)
    }
}

@Composable
private fun WhatBuddyDoesSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MergedTag("WHAT BUDDY DOES")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Two modes, one assistant",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.widthIn(max = 900.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            // Remote Control
            TerminalCard(
                modifier = Modifier.weight(1f),
                borderColor = BuddyColors.Amber.copy(alpha = 0.3f),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        Modifier.size(8.dp).clip(CircleShape).background(BuddyColors.Amber),
                    )
                    Text("You drive", style = MaterialTheme.typography.titleLarge, color = BuddyColors.Amber)
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "Phone remotely controls Claude Code + Gemini CLI in tmux. " +
                            "Real-time streaming. Permission approvals from your pocket. " +
                            "Walk away from the desk, keep the AI working.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(12.dp))
                CodeBlock(
                    code = "Phone --> WebSocket --> Daemon --> tmux\n" +
                            "                                   |\n" +
                            "                              Claude CLI",
                )
            }

            // Personal Assistant
            TerminalCard(
                modifier = Modifier.weight(1f),
                borderColor = BuddyColors.Teal.copy(alpha = 0.3f),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Box(
                        Modifier.size(8.dp).clip(CircleShape).background(BuddyColors.Teal),
                    )
                    Text("Buddy drives", style = MaterialTheme.typography.titleLarge, color = BuddyColors.Teal)
                }
                Spacer(Modifier.height(12.dp))
                Text(
                    "Talk to Buddy about anything. Buddy creates an invisible tmux session, " +
                            "delegates to a pipeline of specialist agents, manages quality, " +
                            "and reports back with results.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(12.dp))
                CodeBlock(
                    code = "You --> Buddy --> TPM --> TechLead\n" +
                            "   --> Coder --> Reviewer --> Builder\n" +
                            "   --> Deployer --> Debugger",
                )
            }
        }
    }
}

@Composable
private fun HowItsBuiltSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MergedTag("HOW IT'S BUILT")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Pure Kotlin. All the way down.",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Even this website is Kotlin — compiled to WebAssembly.",
            style = MaterialTheme.typography.bodyLarge,
            color = BuddyColors.Teal,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(32.dp))

        // Architecture
        CodeBlock(
            modifier = Modifier.widthIn(max = 700.dp).fillMaxWidth(),
            code = "Phone (Compose)           PC (Ktor)\n" +
                    "+------------------+      +--------------------+\n" +
                    "|  :app            | <--> |  :daemon           |\n" +
                    "|  :composeApp     | WS   |  7-agent pipeline  |\n" +
                    "+------------------+      |  MCP + A2A servers |\n" +
                    "                          +--------------------+\n" +
                    "       :shared (KMP)              |\n" +
                    "    Wire protocol, models    +----+------+\n" +
                    "    Android/iOS/JVM/Web    :cli  :acp  :web",
        )

        Spacer(Modifier.height(32.dp))

        // Tech pills
        Row(
            modifier = Modifier.widthIn(max = 800.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val techs = listOf(
                "Kotlin 2.3" to BuddyColors.Amber,
                "Compose MP" to BuddyColors.Teal,
                "Ktor 3.4" to BuddyColors.Purple,
                "Koin 4.0" to BuddyColors.TextPrimary,
                "Koog 0.6" to BuddyColors.Amber,
                "MCP" to BuddyColors.Teal,
                "A2A" to BuddyColors.Purple,
                "Wasm" to BuddyColors.Amber,
            )
            techs.forEach { (name, color) ->
                Text(
                    text = name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(color.copy(alpha = 0.1f))
                        .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun TheNumbersSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MergedTag("THE NUMBERS")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "What 6 phases of building looks like",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))

        Row(
            modifier = Modifier.widthIn(max = 800.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NumberCard("7", "Modules", BuddyColors.Amber, Modifier.weight(1f))
            NumberCard("4", "Platforms", BuddyColors.Teal, Modifier.weight(1f))
            NumberCard("120+", "Source Files", BuddyColors.Purple, Modifier.weight(1f))
            NumberCard("20+", "API Endpoints", BuddyColors.TextPrimary, Modifier.weight(1f))
            NumberCard("6", "Phases Done", BuddyColors.Amber, Modifier.weight(1f))
        }
    }
}

@Composable
private fun NumberCard(value: String, label: String, color: Color, modifier: Modifier) {
    TerminalCard(modifier = modifier, borderColor = color.copy(alpha = 0.2f)) {
        Text(value, style = MaterialTheme.typography.displayMedium, color = color)
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.bodySmall, color = BuddyColors.TextDim)
    }
}

@Composable
private fun WhatsNextSection(onNavigateToDashboard: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MergedTag("WHAT'S NEXT")
        Spacer(Modifier.height(12.dp))
        Text(
            text = "This is a live project",
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))

        Column(
            modifier = Modifier.widthIn(max = 600.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            NextItem("Observer Agent", "Gemini Live API watching screen + listening to voice", BuddyColors.Amber)
            NextItem("Agent Store v2", "Community agents, marketplace, hot-swapping at runtime", BuddyColors.Teal)
            NextItem("iOS Client", "Compose Multiplatform — :composeApp already targets iOS", BuddyColors.Purple)
            NextItem("Open Source", "Documentation, contributor guide, public repo", BuddyColors.TextPrimary)
        }

        Spacer(Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(BuddyColors.Amber)
                    .clickable { }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text("View Source on GitHub", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = BuddyColors.Background)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, BuddyColors.Teal, RoundedCornerShape(8.dp))
                    .clickable { onNavigateToDashboard() }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
            ) {
                Text("See Live Dashboard", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = BuddyColors.Teal)
            }
        }
    }
}

@Composable
private fun NextItem(title: String, desc: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(Modifier.size(6.dp).clip(CircleShape).background(color))
        Text(title, style = MaterialTheme.typography.titleMedium, color = color, modifier = Modifier.width(160.dp))
        Text(desc, style = MaterialTheme.typography.bodyMedium, color = BuddyColors.TextMid)
    }
}

@Composable
private fun FollowSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MergedTag("FOLLOW THE JOURNEY")
        Spacer(Modifier.height(16.dp))
        Text(
            text = "I share every decision, bug, and breakthrough.",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            SocialButton("GitHub", BuddyColors.TextPrimary)
            SocialButton("LinkedIn", BuddyColors.Amber)
            SocialButton("Twitter / X", BuddyColors.Teal)
        }
    }
}

@Composable
private fun SocialButton(label: String, color: Color) {
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
private fun MergedTag(text: String) {
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
private fun MergedFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BuddyColors.Surface)
            .border(width = 1.dp, color = BuddyColors.Border)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = BuddyColors.TextDim)) { append("Built by ") }
                withStyle(SpanStyle(color = BuddyColors.Amber)) { append("Deepak") }
                withStyle(SpanStyle(color = BuddyColors.TextDim)) { append(" with ") }
                withStyle(SpanStyle(color = BuddyColors.Teal)) { append("Kotlin/Wasm") }
            },
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Compose Multiplatform // Ktor // This entire site is Kotlin compiled to WebAssembly",
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.TextDim,
        )
    }
}
