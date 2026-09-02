package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_endpoints")
data class ApiEndpointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val path: String,
    val method: String, // GET, POST, PUT, DELETE
    val authType: String, // Bearer JWT, TLS Mutual, HMAC-SHA256, None
    val status: String, // ACTIVE, DEGRADED, OFFLINE
    val rateLimit: String, // e.g. "10,000 req/min"
    val headersJson: String,
    val samplePayload: String,
    val lastTestedTime: Long = 0,
    val latencyMs: Long = 0
)

@Entity(tableName = "container_configs")
data class ContainerConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val runtime: String, // Docker, Kubernetes, Podman
    val image: String,
    val replicas: Int,
    val cpuAllocation: String, // e.g. "4 vCPU"
    val memoryAllocation: String, // e.g. "16 GB"
    val gpuSupport: String, // "NVIDIA H100", "NVIDIA A100", "CUDA Local", "None"
    val portBindings: String, // e.g. "8080:8080, 9090:9090"
    val envVars: String,
    val manifestYaml: String,
    val status: String, // RUNNING, DEPLOYED, STOPPED, FAILED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "prompt_frameworks")
data class PromptFrameworkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // Architecture, Security, DevOps, Optimization, Data
    val systemInstruction: String,
    val userTemplate: String,
    val targetModel: String, // gemini-3.1-pro-preview, gemini-3.5-flash, gemini-3.1-flash-lite-preview
    val temperature: Float,
    val topP: Float,
    val isHighThinking: Boolean,
    val isDefault: Boolean = false
)

@Entity(tableName = "telemetry_logs")
data class TelemetryLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: String, // MODEL_EXECUTION, API_CALL, CONTAINER_ACTION, CRYPTO_VERIFY, PROMPT_OPTIMIZE
    val sourceModule: String,
    val status: String, // SUCCESS, WARNING, FAILURE, AUDIT_PASS
    val latencyMs: Long,
    val tokensUsed: Int,
    val details: String
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sender: String, // "user" or "ai"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val latencyMs: Long? = null,
    val modelUsed: String? = null
)

@Entity(tableName = "veo_videos")
data class VeoVideoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val prompt: String,
    val sourceImageUrl: String? = null, // null for text-to-video, uri/path for image-to-video
    val videoUrl: String,
    val aspectRatio: String = "16:9", // "16:9" or "9:16"
    val resolution: String = "1080p",
    val durationSeconds: Int = 5,
    val status: String = "COMPLETED", // PENDING, GENERATING, COMPLETED, FAILED
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String = "veo-3.1-fast-generate-preview"
)

@Entity(tableName = "generated_images")
data class GeneratedImageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val prompt: String,
    val sourceImageUrl: String? = null, // if editing existing image
    val outputImageUrl: String,
    val aspectRatio: String = "1:1", // "1:1", "16:9", "9:16", "4:3", "3:4"
    val resolution: String = "1K", // "512px", "1K", "2K", "4K"
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String = "gemini-3.1-flash-image-preview"
)

@Entity(tableName = "search_reports")
data class SearchReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
    val answer: String,
    val sourcesJson: String, // JSON array of sources [{title, url, snippet}]
    val searchQueriesJson: String, // JSON array of search queries executed
    val timestamp: Long = System.currentTimeMillis(),
    val latencyMs: Long = 0,
    val modelUsed: String = "gemini-3.5-flash"
)

@Entity(tableName = "voice_sessions")
data class VoiceSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userUtterance: String,
    val aiReply: String,
    val latencyMs: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val modelUsed: String = "gemini-3.1-flash-live-preview"
)

@Entity(tableName = "user_account")
data class UserAccountEntity(
    @PrimaryKey val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val provider: String = "google.com",
    val lastLoginTime: Long = System.currentTimeMillis(),
    val isFirestoreSynced: Boolean = true
)


