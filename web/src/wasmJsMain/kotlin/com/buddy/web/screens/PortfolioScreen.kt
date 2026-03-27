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
import com.buddy.web.components.TerminalCard
import com.buddy.web.theme.BuddyColors

/**
 * Full portfolio page — matches heydeepak.dev design
 */
@Composable
fun PortfolioScreen() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(BuddyColors.Background),
    ) {
        // Hero
        HeroSection()

        Spacer(Modifier.height(80.dp))

        // About (cat about.kt)
        AboutSection()

        Spacer(Modifier.height(80.dp))

        // Projects (ls -la ./projects)
        ProjectsSection()

        Spacer(Modifier.height(80.dp))

        // Stack (cat stack.json)
        StackSection()

        Spacer(Modifier.height(80.dp))

        // Contact (open contact.sh)
        ContactSection()

        Spacer(Modifier.height(64.dp))

        // Footer
        PortfolioFooter()
    }
}

@Composable
private fun HeroSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .padding(top = 80.dp),
    ) {
        // Label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(20.dp)
                    .height(1.dp)
                    .background(BuddyColors.Teal)
            )
            Text(
                text = "Android Engineer & Builder",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp,
                color = BuddyColors.Teal,
            )
        }

        // Name
        Text(
            text = buildAnnotatedString {
                append("Hey, I'm ")
                withStyle(SpanStyle(color = BuddyColors.Amber)) {
                    append("Deepak")
                }
            },
            fontSize = 56.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 60.sp,
            letterSpacing = (-1).sp,
            color = BuddyColors.TextPrimary,
        )

        Spacer(Modifier.height(16.dp))

        // Subtitle (will be animated)
        Text(
            text = "Android Engineer @ VideoSDK.live",
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal,
            color = BuddyColors.TextMid,
        )

        Spacer(Modifier.height(40.dp))

        // Bio
        Text(
            text = "SDE-2 at VideoSDK.live · 6 years building Android SDKs, WebRTC pipelines, and KMP libraries. " +
                    "I ship products in public — from AI agents on mobile to social entertainment platforms. " +
                    "Based in India. Currently obsessed with MCP protocol & on-device AI.",
            fontSize = 13.5.sp,
            lineHeight = 24.sp,
            color = BuddyColors.TextMid,
            modifier = Modifier
                .widthIn(max = 540.dp)
                .border(2.dp, BuddyColors.Border, RoundedCornerShape(0.dp))
                .padding(start = 16.dp),
        )

        Spacer(Modifier.height(48.dp))

        // Links
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryButton("→ View Projects")
            SecondaryButton("⌥ GitHub", "https://github.com/Hey-Deepak")
            SecondaryButton("↗ LinkedIn", "https://linkedin.com/in/heydeepak")
        }
    }
}

@Composable
private fun AboutSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
    ) {
        // Section header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            Text(
                text = "$",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.Amber,
            )
            Text(
                text = "cat about.kt",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.TextPrimary,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(BuddyColors.Border)
            )
        }

        // Stats
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatCard("6", "years Android")
            StatCard("5+", "products shipped")
            StatCard("SDE-2", "VideoSDK.live")
            StatCard("15", "MCP tools built")
        }

        // Code block
        TerminalCard(
            modifier = Modifier.widthIn(max = 700.dp)
        ) {
            Text(
                "Deepak.kt — profile",
                fontSize = 11.sp,
                color = BuddyColors.TextDim,
                modifier = Modifier.padding(bottom = 16.dp),
            )
            CodeLine(1, """data class Deepak(""")
            CodeLine(2, """  role: String = "Android Engineer @ VideoSDK.live",""", indent = 1)
            CodeLine(3, """  experience: Int = 6, // years""", indent = 1)
            CodeLine(4, """  level: String = "SDE-2",""", indent = 1)
            CodeLine(5, """  core: List<String> = listOf(""", indent = 1)
            CodeLine(6, """"Kotlin/KMP", "Jetpack Compose", "WebRTC",""", indent = 2)
            CodeLine(7, """"BLE", "MCP Protocol", "On-device AI"""", indent = 2)
            CodeLine(8, """),""", indent = 1)
            CodeLine(9, """  currentlyBuilding: String = "Buddy — AI agent on mobile",""", indent = 1)
            CodeLine(10, """  github: String = "github.com/Hey-Deepak"""", indent = 1)
            CodeLine(11, """)""")
        }
    }
}

@Composable
private fun ProjectsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
    ) {
        // Section header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            Text(
                text = "$",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.Amber,
            )
            Text(
                text = "ls -la ./projects",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.TextPrimary,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(BuddyColors.Border)
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ProjectCard(
                name = "🤖 Buddy",
                status = "Building",
                description = "AI agent on your phone that controls your dev machine. Remote Control your PC from Android — run Claude, manage Android Studio, execute code — from anywhere. Dual-mode: Remote Control + Personal Assistant.",
                tags = listOf("Kotlin/KMP", "Ktor", "MCP", "Tailscale", "Whisper", "Koog", "Claude API"),
                accentColor = BuddyColors.Amber,
            )

            ProjectCard(
                name = "🎙 Murmur",
                status = "Building",
                description = "Privacy-first, always-listening audio recorder & analyzer for Android. On-device Whisper-tiny transcription via TFLite. MobileBERT sentiment analysis. Hinglish-aware. No cloud. Your voice stays local.",
                tags = listOf("Jetpack Compose", "TFLite", "Whisper-tiny", "MobileBERT", "Koin", "Room"),
                accentColor = BuddyColors.Teal,
            )

            ProjectCard(
                name = "🎬 Timepass",
                status = "Building",
                description = "Social entertainment discovery where friends recommend what to watch — not algorithms. Movies, TV shows, curated by people you trust. Built for India. Because your friends know your taste better than Netflix.",
                tags = listOf("Android", "Kotlin", "Jetpack Compose", "Social", "India"),
                accentColor = Color(0xFFF472B6), // Pink
            )

            ProjectCard(
                name = "📡 VideoSDK.live",
                status = "Active · SDE-2",
                description = "Real-time video & audio SDK powering thousands of apps. Core contributor to Android SDK — WebRTC internals, HardwareVideoEncoder debugging, Java-to-Kotlin migrations, BLE + WebRTC hybrid architecture.",
                tags = listOf("Android SDK", "WebRTC", "BLE", "Kotlin", "C++"),
                accentColor = Color(0xFF60A5FA), // Blue
            )

            ProjectCard(
                name = "🚀 FaangX.com",
                status = "Launched",
                description = "Platform to help developers crack FAANG interviews. Resources, roadmaps, and community — curated by someone who's been through the grind. For engineers serious about levelling up.",
                tags = listOf("Product", "Dev Tools", "Career", "Community"),
                accentColor = BuddyColors.Purple,
            )
        }
    }
}

@Composable
private fun StackSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
    ) {
        // Section header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            Text(
                text = "$",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.Amber,
            )
            Text(
                text = "cat stack.json",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.TextPrimary,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(BuddyColors.Border)
            )
        }

        // Stack grid
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StackItem("🟣", "Kotlin / KMP", "Primary language", Modifier.weight(1f))
                StackItem("🖼", "Jetpack Compose", "UI framework", Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StackItem("📡", "WebRTC", "Real-time comms", Modifier.weight(1f))
                StackItem("🔵", "BLE / Bluetooth", "Hardware", Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StackItem("🔌", "MCP Protocol", "AI tooling", Modifier.weight(1f))
                StackItem("⚡", "Ktor", "Backend / server", Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StackItem("🤝", "Koin", "DI framework", Modifier.weight(1f))
                StackItem("🗃", "Room", "Local DB", Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StackItem("🎙", "Whisper / ONNX", "On-device STT", Modifier.weight(1f))
                StackItem("🧠", "Claude + Gemini", "LLM backends", Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StackItem("🌐", "Tailscale", "Secure networking", Modifier.weight(1f))
                StackItem("🤖", "Koog", "Agent orchestrator", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ContactSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
    ) {
        // Section header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 48.dp)
        ) {
            Text(
                text = "$",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.Amber,
            )
            Text(
                text = "open contact.sh",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.TextPrimary,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(BuddyColors.Border)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ContactCard("⌥", "github.com/Hey-Deepak", "https://github.com/Hey-Deepak", Modifier.weight(1f))
            ContactCard("↗", "LinkedIn", "https://linkedin.com/in/heydeepak", Modifier.weight(1f))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ContactCard("📡", "VideoSDK.live", "https://videosdk.live", Modifier.weight(1f))
            ContactCard("🚀", "FaangX.com", "https://faangx.com", Modifier.weight(1f))
        }
    }
}

@Composable
private fun PortfolioFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BuddyColors.Surface)
            .border(1.dp, BuddyColors.Border, RoundedCornerShape(0.dp))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Deepak",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = BuddyColors.Amber,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Senior Android Engineer // Building Buddy in public",
            fontSize = 12.sp,
            color = BuddyColors.TextDim,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Built with Kotlin/Wasm + Compose Multiplatform",
            fontSize = 12.sp,
            color = BuddyColors.TextDim,
        )
    }
}

// --- Components ---

@Composable
private fun StatCard(value: String, label: String) {
    TerminalCard(
        borderColor = BuddyColors.Amber.copy(alpha = 0.2f),
        modifier = Modifier.widthIn(min = 140.dp)
    ) {
        Text(
            value,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = BuddyColors.Amber,
            lineHeight = 36.sp,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            fontSize = 11.sp,
            color = BuddyColors.TextDim,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
private fun CodeLine(num: Int, code: String, indent: Int = 0) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            "$num",
            fontSize = 13.sp,
            color = BuddyColors.TextDim,
            modifier = Modifier.widthIn(min = 24.dp),
        )
        Text(
            " ".repeat(indent * 2) + code,
            fontSize = 13.sp,
            color = BuddyColors.TextMid,
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
        )
    }
}

@Composable
private fun ProjectCard(
    name: String,
    status: String,
    description: String,
    tags: List<String>,
    accentColor: Color,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = 500.dp)
            .background(BuddyColors.Surface, RoundedCornerShape(10.dp))
            .border(1.dp, BuddyColors.Border, RoundedCornerShape(10.dp))
            .padding(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BuddyColors.TextPrimary,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.12f))
                    .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            ) {
                Text(
                    status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor,
                    letterSpacing = 0.5.sp,
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            description,
            fontSize = 12.5.sp,
            color = BuddyColors.TextMid,
            lineHeight = 18.sp,
        )

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            tags.forEach { tag ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(BuddyColors.Surface)
                        .border(1.dp, BuddyColors.Border, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BuddyColors.TextDim,
                    )
                }
            }
        }
    }
}

@Composable
private fun StackItem(icon: String, name: String, type: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(BuddyColors.Surface, RoundedCornerShape(8.dp))
            .border(1.dp, BuddyColors.Border, RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(icon, fontSize = 18.sp)
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                name,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = BuddyColors.TextPrimary,
            )
            Text(
                type,
                fontSize = 10.sp,
                color = BuddyColors.TextDim,
            )
        }
    }
}

@Composable
private fun ContactCard(icon: String, label: String, url: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(BuddyColors.Surface)
            .border(1.dp, BuddyColors.Border, RoundedCornerShape(8.dp))
            .clickable { /* TODO: open URL */ }
            .padding(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(icon, fontSize = 18.sp)
            Text(
                label,
                fontSize = 13.sp,
                color = BuddyColors.TextMid,
            )
        }
    }
}

@Composable
private fun PrimaryButton(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(BuddyColors.Amber.copy(alpha = 0.15f))
            .border(1.dp, BuddyColors.Amber.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .clickable { }
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = BuddyColors.Amber,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
private fun SecondaryButton(label: String, url: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Transparent)
            .border(1.dp, BuddyColors.Border, RoundedCornerShape(6.dp))
            .clickable { }
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = BuddyColors.TextMid,
            letterSpacing = 0.5.sp,
        )
    }
}
