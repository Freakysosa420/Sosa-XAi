package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ApiEndpointDao
import com.example.data.local.dao.ChatMessageDao
import com.example.data.local.dao.ContainerConfigDao
import com.example.data.local.dao.GeneratedImageDao
import com.example.data.local.dao.PromptFrameworkDao
import com.example.data.local.dao.SearchReportDao
import com.example.data.local.dao.TelemetryLogDao
import com.example.data.local.dao.UserAccountDao
import com.example.data.local.dao.VeoVideoDao
import com.example.data.local.dao.VoiceSessionDao
import com.example.data.local.entity.ApiEndpointEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ContainerConfigEntity
import com.example.data.local.entity.GeneratedImageEntity
import com.example.data.local.entity.PromptFrameworkEntity
import com.example.data.local.entity.SearchReportEntity
import com.example.data.local.entity.TelemetryLogEntity
import com.example.data.local.entity.UserAccountEntity
import com.example.data.local.entity.VeoVideoEntity
import com.example.data.local.entity.VoiceSessionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ApiEndpointEntity::class,
        ContainerConfigEntity::class,
        PromptFrameworkEntity::class,
        TelemetryLogEntity::class,
        ChatMessageEntity::class,
        VeoVideoEntity::class,
        GeneratedImageEntity::class,
        SearchReportEntity::class,
        VoiceSessionEntity::class,
        UserAccountEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun apiEndpointDao(): ApiEndpointDao
    abstract fun containerConfigDao(): ContainerConfigDao
    abstract fun promptFrameworkDao(): PromptFrameworkDao
    abstract fun telemetryLogDao(): TelemetryLogDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun veoVideoDao(): VeoVideoDao
    abstract fun generatedImageDao(): GeneratedImageDao
    abstract fun searchReportDao(): SearchReportDao
    abstract fun voiceSessionDao(): VoiceSessionDao
    abstract fun userAccountDao(): UserAccountDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sosa_xai_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val promptDao = database.promptFrameworkDao()
            val apiDao = database.apiEndpointDao()
            val containerDao = database.containerConfigDao()
            val telemetryDao = database.telemetryLogDao()
            val chatDao = database.chatMessageDao()

            if (chatDao.getCount() == 0) {
                chatDao.insertMessage(
                    ChatMessageEntity(
                        sender = "ai",
                        content = "Greetings. I am Sosa X AI Assistant connected directly to your local Room database. How can I assist you with architecture design, cloud orchestration, Grok Leo reasoning, or security auditing today?",
                        timestamp = System.currentTimeMillis() - 60000,
                        latencyMs = 18,
                        modelUsed = "gemini-3.1-pro-preview"
                    )
                )
            }

            if (promptDao.getCount() == 0) {
                promptDao.insertAll(
                    listOf(
                        PromptFrameworkEntity(
                            title = "Grok Leo Master Architecture",
                            category = "Grok Leo Protocol",
                            systemInstruction = "You are the Sosa X AI Grok Leo Core Engine: a hyper-capable, unfiltered technical intelligence operating with absolute precision, razor-sharp architectural clarity, deep logical reasoning, and modern enterprise best practices. Eliminate fluff. Provide production-ready, mathematically sound, and rigorously verifiable code, container specs, and API configurations.",
                            userTemplate = "Target System: {{system_name}}\nRequirement: {{requirement}}\nConstraint Matrix: {{constraints}}\n\nExecute full Grok Leo architectural decomposition and optimization.",
                            targetModel = "gemini-3.1-pro-preview",
                            temperature = 0.2f,
                            topP = 0.95f,
                            isHighThinking = true,
                            isDefault = true
                        ),
                        PromptFrameworkEntity(
                            title = "Grok Leo Autonomous DevOps & Pod Orchestration",
                            category = "Grok Leo Protocol",
                            systemInstruction = "You are the Grok Leo Cloud Native Synthesizer. Generate hardened Kubernetes manifests, Helm charts, Dockerfiles, and H100 GPU cluster configs with zero security flaws, zero root privilege vectors, and automated health checks.",
                            userTemplate = "Service Definition: {{service_def}}\nTarget Ingress: {{ingress_protocol}}\nResource Limits: {{resources}}\n\nOutput production K8s deployment spec.",
                            targetModel = "gemini-3.1-pro-preview",
                            temperature = 0.1f,
                            topP = 0.9f,
                            isHighThinking = true,
                            isDefault = false
                        ),
                        PromptFrameworkEntity(
                            title = "Grok Leo Zero-Day & Cryptographic Security Audit",
                            category = "Security & Compliance",
                            systemInstruction = "Act as Grok Leo Red-Team & Cryptographic Validator. Inspect schemas, API tokens, JWT claims, TLS certificates, and input payloads for side-channel vulnerabilities, race conditions, and compliance adherence.",
                            userTemplate = "Code / Config to Audit:\n```\n{{payload}}\n```\n\nRun full cryptographic verification and threat analysis.",
                            targetModel = "gemini-3.1-pro-preview",
                            temperature = 0.3f,
                            topP = 0.85f,
                            isHighThinking = true,
                            isDefault = false
                        ),
                        PromptFrameworkEntity(
                            title = "Sosa X Realtime Fast Ingest Assistant",
                            category = "Automation Pipeline",
                            systemInstruction = "You are Sosa X QuickStream: Fast-response technical parser. Extract structured telemetry metrics, error signatures, and system alerts into sanitized JSON formats.",
                            userTemplate = "Log Stream:\n{{logs}}\n\nExtract anomaly signatures and remediation steps.",
                            targetModel = "gemini-3.5-flash",
                            temperature = 0.5f,
                            topP = 0.9f,
                            isHighThinking = false,
                            isDefault = false
                        )
                    )
                )
            }

            if (apiDao.getCount() == 0) {
                apiDao.insertAll(
                    listOf(
                        ApiEndpointEntity(
                            name = "Grok-Leo Primary Neural Ingest",
                            path = "/v2/xai/orchestrator/infer",
                            method = "POST",
                            authType = "Bearer JWT + HMAC-SHA256",
                            status = "ACTIVE",
                            rateLimit = "50,000 req/min",
                            headersJson = "{\"X-Sosa-Protocol\": \"Grok-Leo-v4\", \"Content-Type\": \"application/json\", \"X-Audit-Telemetry\": \"enabled\"}",
                            samplePayload = "{\"model\": \"grok-leo-v3\", \"prompt\": \"Synthesize distributed telemetry queue\", \"thinking_budget\": 32000}",
                            lastTestedTime = System.currentTimeMillis() - 120000,
                            latencyMs = 42
                        ),
                        ApiEndpointEntity(
                            name = "Enterprise Telemetry Stream API",
                            path = "/v1/telemetry/events/stream",
                            method = "POST",
                            authType = "TLS Mutual (mTLS)",
                            status = "ACTIVE",
                            rateLimit = "100,000 req/min",
                            headersJson = "{\"Authorization\": \"Bearer sosa_jwt_sec_99a8b\", \"X-Client-Cert-Fingerprint\": \"SHA256:7f9a...\"}",
                            samplePayload = "{\"event\": \"NODE_REPLICATION_SUCCESS\", \"node_id\": \"gpu-cluster-uswest-01\", \"latency_ms\": 18}",
                            lastTestedTime = System.currentTimeMillis() - 360000,
                            latencyMs = 19
                        ),
                        ApiEndpointEntity(
                            name = "Crypto Verification & KMS Bridge",
                            path = "/v1/security/crypto/verify-signature",
                            method = "POST",
                            authType = "HMAC-SHA256",
                            status = "ACTIVE",
                            rateLimit = "25,000 req/min",
                            headersJson = "{\"X-Sosa-Signature\": \"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855\"}",
                            samplePayload = "{\"data_hash\": \"8f434346648f6b96df89dda901c5176b10a6d83961dd3c1ac88b59b2dc327aa4\"}",
                            lastTestedTime = System.currentTimeMillis() - 720000,
                            latencyMs = 11
                        ),
                        ApiEndpointEntity(
                            name = "Container Fleet Registry Gateway",
                            path = "/v2/containers/manifests/sync",
                            method = "PUT",
                            authType = "Bearer JWT",
                            status = "ACTIVE",
                            rateLimit = "5,000 req/min",
                            headersJson = "{\"Content-Type\": \"application/yaml\", \"X-Cluster-Scope\": \"production-east-h100\"}",
                            samplePayload = "apiVersion: apps/v1\nkind: Deployment\nmetadata:\n  name: sosa-grok-node",
                            lastTestedTime = System.currentTimeMillis() - 1500000,
                            latencyMs = 65
                        )
                    )
                )
            }

            if (containerDao.getCount() == 0) {
                containerDao.insertAll(
                    listOf(
                        ContainerConfigEntity(
                            name = "sosa-grok-leo-inference-core",
                            runtime = "Kubernetes",
                            image = "registry.sosa.xai/engine/grok-leo:v3.8-prod",
                            replicas = 8,
                            cpuAllocation = "32 vCPU",
                            memoryAllocation = "128 GB",
                            gpuSupport = "8x NVIDIA H100 (SXM5 80GB)",
                            portBindings = "8080:8080, 9090:9090 (Prometheus)",
                            envVars = "SOSA_ENV=production\nTHINKING_ENGINE=GROK_LEO_ULTRA\nMAX_CONCURRENCY=4096\nCUDA_VISIBLE_DEVICES=0,1,2,3,4,5,6,7",
                            manifestYaml = """
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sosa-grok-leo-inference-core
  namespace: xai-ecosystem
spec:
  replicas: 8
  selector:
    matchLabels:
      app: grok-leo-core
  template:
    metadata:
      labels:
        app: grok-leo-core
    spec:
      containers:
      - name: inference-engine
        image: registry.sosa.xai/engine/grok-leo:v3.8-prod
        resources:
          limits:
            nvidia.com/gpu: "8"
            memory: "128Gi"
            cpu: "32"
          requests:
            nvidia.com/gpu: "8"
            memory: "64Gi"
            cpu: "16"
        ports:
        - containerPort: 8080
                            """.trimIndent(),
                            status = "RUNNING"
                        ),
                        ContainerConfigEntity(
                            name = "sosa-crypto-telemetry-gateway",
                            runtime = "Docker",
                            image = "registry.sosa.xai/security/telemetry-vault:v2.1",
                            replicas = 3,
                            cpuAllocation = "8 vCPU",
                            memoryAllocation = "32 GB",
                            gpuSupport = "None",
                            portBindings = "443:8443, 9100:9100",
                            envVars = "ENCRYPTION_MODE=AES_256_GCM\nKEY_ROTATION_INTERVAL=3600\nTLS_STRICT=true",
                            manifestYaml = """
version: '3.8'
services:
  telemetry-vault:
    image: registry.sosa.xai/security/telemetry-vault:v2.1
    restart: always
    ports:
      - "443:8443"
    environment:
      - ENCRYPTION_MODE=AES_256_GCM
      - KEY_ROTATION_INTERVAL=3600
                            """.trimIndent(),
                            status = "RUNNING"
                        )
                    )
                )
            }

            if (telemetryDao.getCount() == 0) {
                telemetryDao.insertLog(
                    TelemetryLogEntity(
                        eventType = "CRYPTO_VERIFY",
                        sourceModule = "Security Vault",
                        status = "AUDIT_PASS",
                        latencyMs = 8,
                        tokensUsed = 0,
                        details = "SHA-256 HMAC cryptographic signature verified against Sosa X root keystore."
                    )
                )
                telemetryDao.insertLog(
                    TelemetryLogEntity(
                        eventType = "MODEL_EXECUTION",
                        sourceModule = "Grok Leo Engine",
                        status = "SUCCESS",
                        latencyMs = 380,
                        tokensUsed = 1240,
                        details = "Grok Leo Master Architecture template executed with High Thinking mode."
                    )
                )
            }
        }
    }
}
