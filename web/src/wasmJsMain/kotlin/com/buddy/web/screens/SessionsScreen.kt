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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buddy.web.api.DaemonApiClient
import com.buddy.web.api.DesktopSessionInfo
import com.buddy.web.components.StatusBadge
import com.buddy.web.components.TerminalCard
import com.buddy.web.theme.BuddyColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun SessionsScreen(apiClient: DaemonApiClient) {
    val scrollState = rememberScrollState()
    var sessions by remember { mutableStateOf<List<DesktopSessionInfo>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (isActive) {
            try {
                sessions = apiClient.getSessions().sessions
                error = null
            } catch (e: Exception) {
                error = "Cannot reach daemon: ${e.message}"
            }
            isLoading = false
            delay(3000) // Refresh every 3s for live session data
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(BuddyColors.Background)
            .padding(32.dp),
    ) {
        Text(
            text = "Desktop Sessions",
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Active Claude Code and Gemini CLI sessions",
            style = MaterialTheme.typography.bodyLarge,
            color = BuddyColors.TextDim,
        )

        Spacer(Modifier.height(24.dp))

        if (error != null && sessions.isEmpty()) {
            TerminalCard(borderColor = BuddyColors.Red.copy(alpha = 0.3f)) {
                Text(error ?: "", color = BuddyColors.Red, style = MaterialTheme.typography.bodyMedium)
            }
        } else if (isLoading) {
            Text("Loading sessions...", color = BuddyColors.TextDim, style = MaterialTheme.typography.bodyMedium)
        } else if (sessions.isEmpty()) {
            TerminalCard {
                Text(
                    text = "No active sessions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BuddyColors.TextDim,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Sessions appear when Claude Code or Gemini CLI connect to the daemon via hooks or MCP.",
                    style = MaterialTheme.typography.bodySmall,
                    color = BuddyColors.TextDim,
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                sessions.forEach { session ->
                    SessionCard(session)
                }
            }
        }
    }
}

@Composable
private fun SessionCard(session: DesktopSessionInfo) {
    val stateColor = when (session.state) {
        "ACTIVE" -> BuddyColors.Teal
        "IDLE" -> BuddyColors.Amber
        "CLOSED" -> BuddyColors.TextDim
        else -> BuddyColors.TextDim
    }

    TerminalCard(
        borderColor = if (session.isLive) BuddyColors.Teal.copy(alpha = 0.3f) else BuddyColors.Border,
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = session.toolName.ifEmpty { "Unknown" },
                    style = MaterialTheme.typography.titleMedium,
                    color = BuddyColors.TextPrimary,
                )
                StatusBadge(text = session.state, color = stateColor)
                if (session.isRemoteControl) {
                    StatusBadge(text = "REMOTE", color = BuddyColors.Purple)
                }
            }
            if (session.pendingPermissions > 0) {
                StatusBadge(
                    text = "${session.pendingPermissions} PENDING",
                    color = BuddyColors.Amber,
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Title & path
        if (session.title.isNotEmpty()) {
            Text(
                text = session.title,
                style = MaterialTheme.typography.bodyMedium,
                color = BuddyColors.TextPrimary,
            )
        }
        Text(
            text = session.projectPath,
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.TextDim,
        )

        Spacer(Modifier.height(12.dp))

        // Metrics row
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            SessionMetric("Turns", "${session.turnCount}")
            SessionMetric("In", formatTokens(session.inputTokens))
            SessionMetric("Out", formatTokens(session.outputTokens))
            if (session.model.isNotEmpty()) {
                SessionMetric("Model", session.model)
            }
        }
    }
}

@Composable
private fun SessionMetric(label: String, value: String) {
    Column {
        Text(text = value, fontSize = 13.sp, color = BuddyColors.TextPrimary)
        Text(text = label, fontSize = 11.sp, color = BuddyColors.TextDim)
    }
}

private fun formatTokens(tokens: Long): String {
    fun fmt(value: Double): String {
        val intPart = value.toLong()
        val fracPart = ((value - intPart) * 10).toLong()
        return "$intPart.$fracPart"
    }
    return when {
        tokens >= 1_000_000 -> "${fmt(tokens / 1_000_000.0)}M"
        tokens >= 1_000 -> "${fmt(tokens / 1_000.0)}K"
        else -> "$tokens"
    }
}
