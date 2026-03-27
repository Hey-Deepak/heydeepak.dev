package com.buddy.web.navigation

/** Navigation routes for the web app. */
enum class WebRoute(val path: String, val label: String) {
    Landing("/", "Home"),
    Dashboard("/dashboard", "Dashboard"),
    Agents("/agents", "Agent Store"),
    Sessions("/sessions", "Sessions"),
    Traces("/traces", "Traces"),
}
