package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.ApiEndpointDao
import com.example.data.local.dao.ContainerConfigDao
import com.example.data.local.dao.PromptFrameworkDao
import com.example.data.local.dao.TelemetryLogDao
import com.example.data.local.entity.ApiEndpointEntity
import com.example.data.local.entity.ContainerConfigEntity
import com.example.data.local.entity.PromptFrameworkEntity
import com.example.data.local.entity.TelemetryLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ApiEndpointEntity::class,
        ContainerConfigEntity::class,
        PromptFrameworkEntity::class,
        TelemetryLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SosaXAiDatabase : RoomDatabase() {
    abstract fun apiEndpointDao(): ApiEndpointDao
    abstract fun containerConfigDao(): ContainerConfigDao
    abstract fun promptFrameworkDao(): PromptFrameworkDao
    abstract fun telemetryLogDao(): TelemetryLogDao

    companion object {
        @Volatile
        private var INSTANCE: SosaXAiDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SosaXAiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SosaXAiDatabase::class.java,
                    "sosa_xai_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
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

        suspend fun populateInitialData(db: SosaXAiDatabase) {
            // Seed API Endpoints
            if (db.apiEndpointDao().getCount() == 0) {
                db.apiEndpointDao().insertAll(
                    listOf(
                        ApiEndpointEntity(
                            name = "Sosa Core Model Orchestration",
                            path = "/v1/models/sosa-xai/orchestrate",
                            method = "POST",
                            authType = "Bearer JWT",
                            status = "ACTIVE",
                            rateLimit = "10,000 req/min",
                            headersJson = """{"Authorization": "Bearer sosa_sec_live_99a8b", "X-Sosa-Protocol": "4.8-Enterprise", "Content-Type": "application/json"}""",
                            samplePayload = """{"target_pipeline": "deep_reasoning_flow", "priority": "CRITICAL", "security_tier": "LEVEL_4"}""",
                            lastTestedTime = System.currentTimeMillis() - 120000,
                            latencyMs = 24
                        ),
                        ApiEndpointEntity(
                            name = "Enterprise Telemetry Ingestion",
                            path = "/v1/telemetry/audit-stream",
                            method = "POST",
                            authType = "HMAC-SHA256",
                            status = "ACTIVE",
                            rateLimit = "50,000 req/min",
                            headersJson = """{"X-HMAC-Signature": "sha256=9f83ac...", "X-Node-ID": "sosa-cluster-us-west"}""",
                            samplePayload = """{"event_type": "KERNEL_HEARTBEAT", "cluster_load": 0.38, "cryptographic_state": "VERIFIED"}""",
                            lastTestedTime = System.currentTimeMillis() - 45000,
                            latencyMs = 12
                        ),
                        ApiEndpointEntity(
                            name = "Container Fleet Control Plane",
                            path = "/v1/containers/fleet/sync",
                            method = "PUT",
                            authType = "TLS Mutual",
                            status = "ACTIVE",
                            rateLimit = "2,000 req/min",
                            headersJson = """{"X-Client-Cert-Thumbprint": "e3b0c44298fc1c149afbf4c8996fb924", "X-Auth-Realm": "Enterprise-K8s"}""",
                            samplePayload = """{"nodes": ["sosa-node-01", "sosa-node-02"], "action": "MAINTAIN_REPLICAS"}""",
                            lastTestedTime = System.currentTimeMillis() - 300000,
                            latencyMs = 38
                        ),
                        ApiEndpointEntity(
                            name = "Cryptographic Verification & Proofs",
                            path = "/v1/security/crypto/verify-merkle",
                            method = "POST",
                            authType = "HMAC-SHA256",
                            status = "ACTIVE",
                            rateLimit = "5,000 req/min",
                            headersJson = """{"X-Proof-Schema": "ZKP-ZK-SNARK-v2", "Content-Type": "application/json"}""",
                            samplePayload = """{"block_height": 1892044, "merkle_root": "0x77fa2b9843c9...", "signature": "0xfe31..."}""",
                            lastTestedTime = System.currentTimeMillis() - 10000,
                            latencyMs = 18
                        )
                    )
                )
            }

            // Seed Container Deployments
            if (db.containerConfigDao().getCount() == 0) {
                db.containerConfigDao().insertAll(
                    listOf(
                        ContainerConfigEntity(
                            name = "sosa-inference-core",
                            runtime = "Kubernetes",
                            image = "registry.sosa.ai/xai-core/inference-engine:v4.8-cuda",
                            replicas = 8,
                            cpuAllocation = "16 vCPU",
                            memoryAllocation = "64 GB",
                            gpuSupport = "NVIDIA H100 (8x SXM5)",
                            portBindings = "8000:8000, 9000:9000",
                            envVars = "SOSA_ENV=production\nLOG_LEVEL=info\nMODEL_CACHE_DIR=/models\nNUM_WORKERS=16",
                            manifestYaml = """
apiVersion: apps/v1
kind: Deployment
metadata:
  name: sosa-inference-core
  namespace: sosa-prod
spec:
  replicas: 8
  selector:
    matchLabels:
      app: sosa-inference
  template:
    metadata:
      labels:
        app: sosa-inference
    spec:
      containers:
      - name: inference-engine
        image: registry.sosa.ai/xai-core/inference-engine:v4.8-cuda
        resources:
          limits:
            nvidia.com/gpu: 8
            memory: "64Gi"
            cpu: "16000m"
        ports:
        - containerPort: 8000
        - containerPort: 9000
                            """.trimIndent(),
                            status = "RUNNING"
                        ),
                        ContainerConfigEntity(
                            name = "sosa-telemetry-collector",
                            runtime = "Docker",
                            image = "registry.sosa.ai/telemetry/collector:v2.1",
                            replicas = 4,
                            cpuAllocation = "4 vCPU",
                            memoryAllocation = "16 GB",
                            gpuSupport = "None",
                            portBindings = "4317:4317, 4318:4318",
                            envVars = "EXPORTER_ENDPOINT=https://audit.sosa.ai/v1/traces\nBUFFER_SIZE=65536",
                            manifestYaml = """
version: '3.8'
services:
  sosa-telemetry:
    image: registry.sosa.ai/telemetry/collector:v2.1
    restart: always
    deploy:
      replicas: 4
    ports:
      - "4317:4317"
      - "4318:4318"
    environment:
      - EXPORTER_ENDPOINT=https://audit.sosa.ai/v1/traces
      - BUFFER_SIZE=65536
                            """.trimIndent(),
                            status = "RUNNING"
                        ),
                        ContainerConfigEntity(
                            name = "sosa-crypto-gateway",
                            runtime = "Podman",
                            image = "registry.sosa.ai/security/crypto-vault:v1.9",
                            replicas = 2,
                            cpuAllocation = "8 vCPU",
                            memoryAllocation = "32 GB",
                            gpuSupport = "CUDA Local",
                            portBindings = "8443:8443",
                            envVars = "SECURITY_LEVEL=ENTERPRISE_L4\nHSM_MODULE_ENABLED=true",
                            manifestYaml = """
podman run -d --name sosa-crypto-gateway \
  --restart always \
  -p 8443:8443 \
  -e SECURITY_LEVEL=ENTERPRISE_L4 \
  -e HSM_MODULE_ENABLED=true \
  --memory 32g \
  --cpus 8 \
  registry.sosa.ai/security/crypto-vault:v1.9
                            """.trimIndent(),
                            status = "RUNNING"
                        )
                    )
                )
            }

            // Seed Prompt Frameworks
            if (db.promptFrameworkDao().getCount() == 0) {
                db.promptFrameworkDao().insertAll(
                    listOf(
                        PromptFrameworkEntity(
                            title = "Enterprise Architecture Synthesizer",
                            category = "Architecture",
                            systemInstruction = "You are the Sosa XAI Principal Architect. Provide strict, production-ready system architecture breakdowns, component topologies, container specs, and high-throughput data flows.",
                            userTemplate = "Analyze and design the system blueprint for the following enterprise workload: [INSERT_REQUIREMENTS]. Emphasize security, sub-50ms latency, and high-availability failover.",
                            targetModel = "gemini-3.1-pro-preview",
                            temperature = 0.2f,
                            topP = 0.85f,
                            isHighThinking = true,
                            isDefault = true
                        ),
                        PromptFrameworkEntity(
                            title = "Cryptographic Security & Audit Protocol",
                            category = "Security",
                            systemInstruction = "You are the Sosa XAI Chief Security Auditor. Audit source code, API contracts, TLS configurations, and cryptographic primitives for vulnerabilities, zero-day vectors, and compliance standards (SOC2, ISO27001, HIPAA).",
                            userTemplate = "Review this system module configuration for security vulnerabilities and cryptographic integrity: [INSERT_CODE_OR_CONFIG].",
                            targetModel = "gemini-3.1-pro-preview",
                            temperature = 0.1f,
                            topP = 0.8f,
                            isHighThinking = true,
                            isDefault = false
                        ),
                        PromptFrameworkEntity(
                            title = "Fast API Payload & Telemetry Optimizer",
                            category = "Optimization",
                            systemInstruction = "You are the Sosa XAI Low-Latency Optimizer. Optimize JSON payloads, gRPC schemas, rate limits, and network routing configurations for microsecond serialization.",
                            userTemplate = "Streamline the following API payload format and optimize network throughput: [INSERT_PAYLOAD]",
                            targetModel = "gemini-3.1-flash-lite-preview",
                            temperature = 0.3f,
                            topP = 0.9f,
                            isHighThinking = false,
                            isDefault = false
                        ),
                        PromptFrameworkEntity(
                            title = "DevOps Container & K8s Manifest Generator",
                            category = "DevOps",
                            systemInstruction = "You are the Sosa XAI Infrastructure Automation Specialist. Generate optimized Kubernetes manifests, Dockerfiles, and Helm charts configured for multi-GPU inference and zero-downtime rolling updates.",
                            userTemplate = "Generate Kubernetes deployment and service manifests for container image [IMAGE] with GPU resource limits and ingress configuration.",
                            targetModel = "gemini-3.5-flash",
                            temperature = 0.2f,
                            topP = 0.95f,
                            isHighThinking = false,
                            isDefault = false
                        )
                    )
                )
            }

            // Seed Telemetry Logs
            if (db.telemetryLogDao().getCount() == 0) {
                val now = System.currentTimeMillis()
                db.telemetryLogDao().insertLog(
                    TelemetryLogEntity(
                        timestamp = now - 5000,
                        eventType = "CRYPTO_VERIFY",
                        sourceModule = "sosa-crypto-gateway",
                        status = "AUDIT_PASS",
                        latencyMs = 14,
                        tokensUsed = 0,
                        details = "Zero-Knowledge Merkle Root 0x77fa2b9843 verified with enterprise HSM token."
                    )
                )
                db.telemetryLogDao().insertLog(
                    TelemetryLogEntity(
                        timestamp = now - 18000,
                        eventType = "MODEL_EXECUTION",
                        sourceModule = "sosa-inference-core",
                        status = "SUCCESS",
                        latencyMs = 340,
                        tokensUsed = 1420,
                        details = "Orchestrated complex reasoning query via gemini-3.1-pro-preview [High Thinking Mode]."
                    )
                )
                db.telemetryLogDao().insertLog(
                    TelemetryLogEntity(
                        timestamp = now - 45000,
                        eventType = "API_CALL",
                        sourceModule = "sosa-api-gateway",
                        status = "SUCCESS",
                        latencyMs = 22,
                        tokensUsed = 0,
                        details = "POST /v1/models/sosa-xai/orchestrate returned HTTP 200 OK (24ms latency)."
                    )
                )
                db.telemetryLogDao().insertLog(
                    TelemetryLogEntity(
                        timestamp = now - 80000,
                        eventType = "CONTAINER_ACTION",
                        sourceModule = "k8s-cluster-ctrl",
                        status = "SUCCESS",
                        latencyMs = 85,
                        tokensUsed = 0,
                        details = "Scaled deployment sosa-inference-core to 8 active pods with H100 GPU binding."
                    )
                )
            }
        }
    }
}
