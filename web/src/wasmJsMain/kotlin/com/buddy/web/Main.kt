package com.buddy.web

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import com.buddy.web.screens.PortfolioScreen
import com.buddy.web.theme.BuddyColors
import com.buddy.web.theme.BuddyWebTheme
import kotlinx.browser.document

/**
 * heydeepak.dev — Personal portfolio website
 *
 * Pure portfolio, no backend or Buddy integration.
 * Buddy dashboard lives at buddy.heydeepak.dev (separate project).
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val body = document.body ?: return

    ComposeViewport(body) {
        BuddyWebTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BuddyColors.Background),
            ) {
                PortfolioScreen()
            }
        }
    }
}
