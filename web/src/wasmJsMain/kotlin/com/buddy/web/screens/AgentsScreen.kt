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
import com.buddy.web.api.StoreAgentInfo
import com.buddy.web.components.StatusBadge
import com.buddy.web.components.TerminalCard
import com.buddy.web.theme.BuddyColors

@Composable
fun AgentsScreen(apiClient: DaemonApiClient) {
    val scrollState = rememberScrollState()
    var agents by remember { mutableStateOf<List<StoreAgentInfo>>(emptyList()) }
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(selectedCategory) {
        isLoading = true
        try {
            agents = apiClient.getAgents(category = selectedCategory).agents
            if (categories.isEmpty()) {
                categories = apiClient.getCategories().categories
            }
            error = null
        } catch (e: Exception) {
            error = "Cannot reach daemon: ${e.message}"
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(BuddyColors.Background)
            .padding(32.dp),
    ) {
        Text(
            text = "Agent Store",
            style = MaterialTheme.typography.headlineLarge,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Browse and manage your AI agents",
            style = MaterialTheme.typography.bodyLarge,
            color = BuddyColors.TextDim,
        )

        // Category filter
        if (categories.isNotEmpty()) {
            Spacer(Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                CategoryChip("All", selectedCategory == null) { selectedCategory = null }
                categories.forEach { cat ->
                    CategoryChip(cat, selectedCategory == cat) { selectedCategory = cat }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        if (error != null) {
            TerminalCard(borderColor = BuddyColors.Red.copy(alpha = 0.3f)) {
                Text(error ?: "", color = BuddyColors.Red, style = MaterialTheme.typography.bodyMedium)
            }
        } else if (isLoading) {
            Text("Loading agents...", color = BuddyColors.TextDim, style = MaterialTheme.typography.bodyMedium)
        } else if (agents.isEmpty()) {
            Text("No agents found.", color = BuddyColors.TextDim, style = MaterialTheme.typography.bodyMedium)
        } else {
            // Agent grid
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                agents.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        row.forEach { agent ->
                            AgentCard(agent, Modifier.weight(1f))
                        }
                        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgentCard(agent: StoreAgentInfo, modifier: Modifier = Modifier) {
    val trustColor = when (agent.trustLevel.uppercase()) {
        "ELITE" -> BuddyColors.Amber
        "TRUSTED" -> BuddyColors.Teal
        "VERIFIED" -> BuddyColors.Purple
        else -> BuddyColors.TextDim
    }

    TerminalCard(
        modifier = modifier,
        borderColor = trustColor.copy(alpha = 0.2f),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = agent.name,
                style = MaterialTheme.typography.titleMedium,
                color = BuddyColors.TextPrimary,
            )
            StatusBadge(text = agent.trustLevel, color = trustColor)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = agent.role,
            style = MaterialTheme.typography.labelMedium,
            color = BuddyColors.Amber,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = agent.description,
            style = MaterialTheme.typography.bodySmall,
            color = BuddyColors.TextMid,
            maxLines = 3,
        )
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            MetricLabel("Rating", if (agent.rating > 0) formatDouble(agent.rating) else "-")
            MetricLabel("Hires", "${agent.hireCount}")
            MetricLabel("v${agent.version}", "")
        }
    }
}

@Composable
private fun MetricLabel(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (value.isNotEmpty()) {
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BuddyColors.TextPrimary)
        }
        Text(text = label, fontSize = 12.sp, color = BuddyColors.TextDim)
    }
}

private fun formatDouble(value: Double): String {
    val intPart = value.toLong()
    val fracPart = ((value - intPart) * 10).toLong()
    return "$intPart.$fracPart"
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
        color = if (isSelected) BuddyColors.Amber else BuddyColors.TextDim,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .then(
                if (isSelected) Modifier.background(BuddyColors.AmberDim)
                else Modifier.background(BuddyColors.Surface)
            )
            .border(1.dp, if (isSelected) BuddyColors.Amber.copy(alpha = 0.3f) else BuddyColors.Border, RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
