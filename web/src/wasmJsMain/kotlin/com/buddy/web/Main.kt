package com.buddy.web

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import com.buddy.web.api.DaemonApiClient
import com.buddy.web.screens.*
import com.buddy.web.theme.BuddyColors
import com.buddy.web.theme.BuddyWebTheme
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val apiClient = DaemonApiClient()
    val body = document.body ?: return

    ComposeViewport(body) {
        BuddyWebTheme {
            BuddyWebApp(apiClient)
        }
    }
}

@Composable
fun BuddyWebApp(apiClient: DaemonApiClient) {
    var currentRoute by remember { mutableStateOf("/") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BuddyColors.Background),
    ) {
        when (currentRoute) {
            "/" -> PortfolioScreen()
            "/dashboard" -> DashboardScreen(apiClient)
            "/agents" -> AgentsScreen(apiClient)
            "/sessions" -> SessionsScreen(apiClient)
        }
    }
}
