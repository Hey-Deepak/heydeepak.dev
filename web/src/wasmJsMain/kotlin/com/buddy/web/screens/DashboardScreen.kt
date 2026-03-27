package com.buddy.web.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buddy.web.api.DaemonApiClient
import com.buddy.web.api.HealthResponse
import com.buddy.web.api.StoreStats
import com.buddy.web.components.StatCard
import com.buddy.web.components.StatusBadge
import com.buddy.web.components.TerminalCard
import com.buddy.web.theme.BuddyColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun DashboardScreen(apiClient: DaemonApiClient) {
    val scrollState = rememberScrollState()

    var health by remember { mutableStateOf<HealthResponse?>(null) }
    var storeStats by remember { mutableStateOf<StoreStats?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch data and auto-refresh every 5s
    LaunchedEffect(Unit) {
        while (isActive) {
            try {
                health = apiClient.getHealth()
                storeStats = apiClient.getStoreStats()
                error = null
            } catch (e: Exception) {
                error = "Cannot reach daemon: ${e.message}"
            }
            isLoading = false
            delay(5000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(BuddyColors.Background)
            .padding(32.dp),
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineLarge,
            )
            if (health != null) {
                StatusBadge(
                    text = "DAEMON ONLINE",
                    color = BuddyColors.Teal,
                )
            } else if (error != null) {
                StatusBadge(
                    text = "OFFLINE",
                    color = BuddyColors.Red,
                )
            }
        }

        if (error != null && health == null) {
            Spacer(Modifier.height(24.dp))
            TerminalCard(borderColor = BuddyColors.Red.copy(alpha = 0.3f)) {
                Text(
                    text = error ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BuddyColors.Red,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Start the daemon with: ./gradlew :daemon:run",
                    style = MaterialTheme.typography.bodySmall,
                    color = BuddyColors.TextDim,
                )
            }
        }

        if (isLoading) {
            Spacer(Modifier.height(24.dp))
            Text(
                text = "Connecting to daemon...",
                style = MaterialTheme.typography.bodyMedium,
                color = BuddyColors.TextDim,
            )
            return@Column
        }

        // Health metrics
        health?.let { h ->
            Spacer(Modifier.height(32.dp))
            SectionHeader("System Health")
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                StatCard(
                    label = "Uptime",
                    value = h.uptimeFormatted.ifEmpty { "${h.uptimeMs / 1000}s" },
                    accent = BuddyColors.Teal,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Active Tasks",
                    value = "${h.activeTasks}",
                    accent = BuddyColors.Amber,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Total Tasks",
                    value = "${h.totalTasks}",
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Sessions",
                    value = "${h.sessionCount}",
                    accent = BuddyColors.Purple,
                    modifier = Modifier.weight(1f),
                )
            }

            // Workspace info
            h.activeWorkspace?.let { ws ->
                Spacer(Modifier.height(16.dp))
                TerminalCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "Active Workspace",
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Text(
                            text = ws,
                            style = MaterialTheme.typography.bodyMedium,
                            color = BuddyColors.TextPrimary,
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            TerminalCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Registered Agents",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = "${h.registeredAgents}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BuddyColors.Amber,
                    )
                }
            }
        }

        // Agent Store stats
        storeStats?.let { stats ->
            Spacer(Modifier.height(40.dp))
            SectionHeader("Agent Store")
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                StatCard(
                    label = "Total Agents",
                    value = "${stats.totalAgents}",
                    accent = BuddyColors.Amber,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Categories",
                    value = "${stats.categories}",
                    accent = BuddyColors.Teal,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Total Hires",
                    value = "${stats.totalHires}",
                    accent = BuddyColors.Purple,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    label = "Avg Rating",
                    value = if (stats.avgRating > 0) formatDouble(stats.avgRating) else "-",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(16.dp))

            // Trust level breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                TrustLevelCard("Elite", stats.eliteCount, BuddyColors.Amber, Modifier.weight(1f))
                TrustLevelCard("Trusted", stats.trustedCount, BuddyColors.Teal, Modifier.weight(1f))
                TrustLevelCard("Verified", stats.verifiedCount, BuddyColors.Purple, Modifier.weight(1f))
                TrustLevelCard("New", stats.newCount, BuddyColors.TextDim, Modifier.weight(1f))
            }
        }

        // Endpoints reference
        Spacer(Modifier.height(40.dp))
        SectionHeader("API Endpoints")
        Spacer(Modifier.height(16.dp))
        EndpointsTable()
    }
}

@Composable
private fun TrustLevelCard(
    label: String,
    count: Int,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    TerminalCard(
        modifier = modifier,
        borderColor = color.copy(alpha = 0.2f),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = color,
            )
        }
        Text(
            text = "$count",
            style = MaterialTheme.typography.headlineMedium,
            color = BuddyColors.TextPrimary,
        )
    }
}

@Composable
private fun EndpointsTable() {
    val endpoints = listOf(
        Triple("/buddy", "WebSocket", "Phone \u2194 daemon communication"),
        Triple("/health", "GET", "System health + workspace metrics"),
        Triple("/store/agents", "GET", "Browse agent store"),
        Triple("/store/stats", "GET", "Agent store statistics"),
        Triple("/relay/sessions", "GET", "Active desktop sessions"),
        Triple("/traces", "GET", "Execution span inspector"),
        Triple("/mcp", "WS + SSE", "MCP server for external clients"),
        Triple("/a2a", "POST", "Agent-to-Agent JSON-RPC"),
        Triple("/.well-known/agent.json", "GET", "A2A agent card"),
        Triple("/relay/hook", "POST", "Claude Code hook events"),
    )

    TerminalCard {
        // Header row
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        ) {
            Text("Endpoint", style = MaterialTheme.typography.labelMedium, color = BuddyColors.Amber, modifier = Modifier.weight(2f))
            Text("Protocol", style = MaterialTheme.typography.labelMedium, color = BuddyColors.Amber, modifier = Modifier.weight(1f))
            Text("Purpose", style = MaterialTheme.typography.labelMedium, color = BuddyColors.Amber, modifier = Modifier.weight(3f))
        }

        // Divider
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BuddyColors.Border),
        )

        Spacer(Modifier.height(8.dp))

        endpoints.forEach { (endpoint, protocol, purpose) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            ) {
                Text(
                    text = endpoint,
                    style = MaterialTheme.typography.bodySmall,
                    color = BuddyColors.Teal,
                    modifier = Modifier.weight(2f),
                )
                Text(
                    text = protocol,
                    style = MaterialTheme.typography.bodySmall,
                    color = BuddyColors.TextMid,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = purpose,
                    style = MaterialTheme.typography.bodySmall,
                    color = BuddyColors.TextMid,
                    modifier = Modifier.weight(3f),
                )
            }
        }
    }
}

private fun formatDouble(value: Double): String {
    val intPart = value.toLong()
    val fracPart = ((value - intPart) * 10).toLong()
    return "$intPart.$fracPart"
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        color = BuddyColors.TextPrimary,
    )
}
