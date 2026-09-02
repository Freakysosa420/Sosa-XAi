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
