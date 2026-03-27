package com.buddy.web.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Client for Buddy daemon REST endpoints. */
class DaemonApiClient(
    private val baseUrl: String = "http://localhost:8080",
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getHealth(): HealthResponse =
        client.get("$baseUrl/health").body()

    suspend fun getStoreStats(): StoreStats =
        client.get("$baseUrl/store/stats").body()

    suspend fun getAgents(query: String? = null, role: String? = null, category: String? = null): StoreListResponse =
        client.get("$baseUrl/store/agents") {
            query?.let { parameter("q", it) }
            role?.let { parameter("role", it) }
            category?.let { parameter("category", it) }
        }.body()

    suspend fun getCategories(): CategoriesResponse =
        client.get("$baseUrl/store/categories").body()

    suspend fun getSessions(): SessionListResponse =
        client.get("$baseUrl/relay/sessions").body()
}

// --- Response models ---

@Serializable
data class HealthResponse(
    val status: String = "",
    val uptimeMs: Long = 0,
    val uptimeFormatted: String = "",
    val activeWorkspace: String? = null,
    val registeredAgents: Int = 0,
    val activeTasks: Int = 0,
    val totalTasks: Int = 0,
    val sessionCount: Int = 0,
)

@Serializable
data class StoreStats(
    val totalAgents: Int = 0,
    val categories: Int = 0,
    val roles: Int = 0,
    val totalHires: Int = 0,
    val avgRating: Double = 0.0,
    val eliteCount: Int = 0,
    val trustedCount: Int = 0,
    val verifiedCount: Int = 0,
    val newCount: Int = 0,
)

@Serializable
data class StoreAgentInfo(
    val id: String = "",
    val name: String = "",
    val role: String = "",
    val category: String = "",
    val description: String = "",
    val rating: Double = 0.0,
    val hireCount: Int = 0,
    val trustLevel: String = "NEW",
    val version: String = "1.0.0",
)

@Serializable
data class StoreListResponse(
    val agents: List<StoreAgentInfo> = emptyList(),
    val query: String? = null,
    val role: String? = null,
    val category: String? = null,
)

@Serializable
data class CategoriesResponse(
    val categories: List<String> = emptyList(),
)

@Serializable
data class DesktopSessionInfo(
    val id: String = "",
    val claudeSessionId: String? = null,
    val toolName: String = "",
    val projectPath: String = "",
    val title: String = "",
    val turnCount: Int = 0,
    val inputTokens: Long = 0,
    val outputTokens: Long = 0,
    val model: String = "",
    val isLive: Boolean = false,
    val state: String = "UNKNOWN",
    val pendingPermissions: Int = 0,
    val createdAt: String = "",
    val lastMessageAt: String = "",
    val remoteControlUrl: String? = null,
    val isRemoteControl: Boolean = false,
)

@Serializable
data class SessionListResponse(
    val sessions: List<DesktopSessionInfo> = emptyList(),
    val workspaceId: String? = null,
)
