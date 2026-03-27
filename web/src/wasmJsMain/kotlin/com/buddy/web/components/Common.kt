package com.buddy.web.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buddy.web.theme.BuddyColors

/** Terminal-style card with border glow. */
@Composable
fun TerminalCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BuddyColors.Border,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(BuddyColors.Surface)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(24.dp),
        content = content,
    )
}

/** Stat card for dashboard metrics. */
@Composable
fun StatCard(
    label: String,
    value: String,
    accent: Color = BuddyColors.Amber,
    modifier: Modifier = Modifier,
) {
    TerminalCard(
        modifier = modifier,
        borderColor = accent.copy(alpha = 0.2f),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displayMedium,
            color = accent,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = BuddyColors.TextDim,
        )
    }
}

/** Status badge (online/offline/idle). */
@Composable
fun StatusBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color),
        )
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            letterSpacing = 0.5.sp,
        )
    }
}

/** Top navigation bar. */
@Composable
fun NavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BuddyColors.Surface)
            .border(width = 1.dp, color = BuddyColors.Border)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Logo
        Text(
            text = "buddy",
            style = MaterialTheme.typography.titleLarge,
            color = BuddyColors.Amber,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onNavigate("/") },
        )
        Text(
            text = "_",
            style = MaterialTheme.typography.titleLarge,
            color = BuddyColors.Teal,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.width(48.dp))

        // Nav links
        val links = listOf(
            "/" to "Home",
            "/dashboard" to "Dashboard",
            "/agents" to "Agents",
            "/sessions" to "Sessions",
        )
        links.forEach { (path, label) ->
            val isActive = currentRoute == path
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isActive) BuddyColors.Amber else BuddyColors.TextDim,
                fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier
                    .clickable { onNavigate(path) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

/** Code-style monospace text block. */
@Composable
fun CodeBlock(
    code: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0A0C14))
            .border(1.dp, BuddyColors.Border, RoundedCornerShape(8.dp))
            .padding(16.dp),
    ) {
        Text(
            text = code,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            color = BuddyColors.TextMid,
        )
    }
}
