package com.example.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
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
import com.example.data.remote.GeminiApiService
import com.example.data.remote.GroundedSearchResult
import com.example.data.remote.ImageGenerationResult
import com.example.data.remote.VeoGenerationResult
import com.example.data.repository.CryptoVerificationResult
import com.example.data.repository.SosaXaiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab(val title: String) {
    OVERVIEW("Overview"),
    AI_CHAT("Gemini Chat"),
    VIDEO_STUDIO("Veo 3 Video"),
    SEARCH_GROUNDING("Search Grounding"),
    IMAGE_STUDIO("Image Studio"),
    VOICE_LIVE("Live Voice"),
    GROK_LEO("Grok Leo"),
    ENDPOINTS("APIs"),
    CONTAINERS("Containers"),
    SECURITY("Security & Sync")
}

data class ChatRolePersona(
    val name: String,
    val description: String,
    val systemInstruction: String,
    val recommendedModel: String
)

data class ChatState(
    val isSending: Boolean = false,
    val currentInput: String = "",
    val selectedModel: String = "gemini-3.1-pro-preview",
    val selectedRole: String = "Principal Architect",
    val customSystemInstruction: String = "You are Sosa X AI Principal Architect: an advanced enterprise intelligence specializing in distributed systems, Kubernetes fleet scaling, API topologies, and zero-trust security.",
    val isHighThinking: Boolean = true,
    val errorMessage: String? = null
)

data class VideoGenerationState(
    val isGenerating: Boolean = false,
    val prompt: String = "Cinematic aerial drone flight over a futuristic high-tech data center with glowing quantum server racks in neon twilight, 4k ultra realistic",
    val selectedAspectRatio: String = "16:9", // "16:9" or "9:16"
    val selectedResolution: String = "1080p",
    val sourceImageBitmap: Bitmap? = null,
    val sourceImageName: String? = null,
    val latestResult: VeoGenerationResult? = null,
    val errorMessage: String? = null
)

data class SearchGroundingState(
    val isSearching: Boolean = false,
    val query: String = "Latest frontier in distributed AI systems and Kubernetes GPU scheduling",
    val latestReport: GroundedSearchResult? = null,
    val errorMessage: String? = null
)

data class ImageStudioState(
    val isGenerating: Boolean = false,
    val prompt: String = "Cybernetic quantum AI neural node glowing with iridescent sapphire and gold circuits, ultra detailed 8k isometric render",
    val aspectRatio: String = "1:1",
    val resolution: String = "1K",
    val inputImageBitmap: Bitmap? = null,
    val inputImageName: String? = null,
    val latestResult: ImageGenerationResult? = null,
    val errorMessage: String? = null
)

data class VoiceCopilotState(
    val isListening: Boolean = false,
    val isThinking: Boolean = false,
    val currentSpokenText: String = "",
    val latestAiSpokenReply: String = "Ready to listen. Tap the microphone to start voice copilot.",
    val errorMessage: String? = null
)

data class GrokLeoExecutionState(
    val isExecuting: Boolean = false,
    val output: String = "",
    val executionTimeMs: Long = 0,
    val tokensUsed: Int = 0,
    val selectedModel: String = "gemini-3.1-pro-preview",
    val isHighThinking: Boolean = true,
    val errorMessage: String? = null
)

class SosaXaiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: SosaXaiRepository

    val endpoints: StateFlow<List<ApiEndpointEntity>>
    val containers: StateFlow<List<ContainerConfigEntity>>
    val promptFrameworks: StateFlow<List<PromptFrameworkEntity>>
    val telemetryLogs: StateFlow<List<TelemetryLogEntity>>
    val chatMessages: StateFlow<List<ChatMessageEntity>>
    val videos: StateFlow<List<VeoVideoEntity>>
    val images: StateFlow<List<GeneratedImageEntity>>
    val searchReports: StateFlow<List<SearchReportEntity>>
    val voiceSessions: StateFlow<List<VoiceSessionEntity>>
    val userAccount: StateFlow<UserAccountEntity?>

    private val _selectedTab = MutableStateFlow(AppNavTab.OVERVIEW)
    val selectedTab: StateFlow<AppNavTab> = _selectedTab.asStateFlow()

    private val _chatState = MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    private val _videoState = MutableStateFlow(VideoGenerationState())
    val videoState: StateFlow<VideoGenerationState> = _videoState.asStateFlow()

    private val _searchState = MutableStateFlow(SearchGroundingState())
    val searchState: StateFlow<SearchGroundingState> = _searchState.asStateFlow()

    private val _imageState = MutableStateFlow(ImageStudioState())
    val imageState: StateFlow<ImageStudioState> = _imageState.asStateFlow()

    private val _voiceState = MutableStateFlow(VoiceCopilotState())
    val voiceState: StateFlow<VoiceCopilotState> = _voiceState.asStateFlow()

    private val _executionState = MutableStateFlow(GrokLeoExecutionState())
    val executionState: StateFlow<GrokLeoExecutionState> = _executionState.asStateFlow()

    private val _selectedFramework = MutableStateFlow<PromptFrameworkEntity?>(null)
    val selectedFramework: StateFlow<PromptFrameworkEntity?> = _selectedFramework.asStateFlow()

    private val _cryptoResult = MutableStateFlow<CryptoVerificationResult?>(null)
    val cryptoResult: StateFlow<CryptoVerificationResult?> = _cryptoResult.asStateFlow()

    val availableRoles = listOf(
        ChatRolePersona(
            name = "Principal Architect",
            description = "High-level distributed architectures, scalability, and system decomposition.",
            systemInstruction = "You are Sosa X Principal Architect. Provide strict, verified, production-grade system designs, container topologies, and failover architectures.",
            recommendedModel = "gemini-3.1-pro-preview"
        ),
        ChatRolePersona(
            name = "DevOps Orchestrator",
            description = "Kubernetes fleet deployment, Docker manifests, H100 GPU pod orchestration.",
            systemInstruction = "You are Sosa X DevOps Orchestrator. Specialize in cloud native specs, Helm charts, Dockerfiles, and automated CI/CD pipelines.",
            recommendedModel = "gemini-3.5-flash"
        ),
        ChatRolePersona(
            name = "Security Auditor",
            description = "Zero-trust verification, cryptographic Merkle proofs, HMAC hashing, and vulnerability analysis.",
            systemInstruction = "You are Sosa X Chief Security Auditor. Audit source code, cryptographic parameters, mTLS certificates, and compliance policies.",
            recommendedModel = "gemini-3.1-pro-preview"
        ),
        ChatRolePersona(
            name = "Fast Code Optimizer",
            description = "Low-latency serialization, gRPC payload tuning, algorithmic micro-benchmarks.",
            systemInstruction = "You are Sosa X Low-Latency Optimizer. Optimize algorithms, JSON payloads, and memory footprints for peak execution speed.",
            recommendedModel = "gemini-3.1-flash-lite-preview"
        )
    )

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        val geminiService = GeminiApiService()
        repository = SosaXaiRepository(
            apiEndpointDao = database.apiEndpointDao(),
            containerConfigDao = database.containerConfigDao(),
            promptFrameworkDao = database.promptFrameworkDao(),
            telemetryLogDao = database.telemetryLogDao(),
            chatMessageDao = database.chatMessageDao(),
            veoVideoDao = database.veoVideoDao(),
            generatedImageDao = database.generatedImageDao(),
            searchReportDao = database.searchReportDao(),
            voiceSessionDao = database.voiceSessionDao(),
            userAccountDao = database.userAccountDao(),
            geminiService = geminiService
        )

        endpoints = repository.allEndpoints.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        containers = repository.allContainers.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        promptFrameworks = repository.allFrameworks.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        telemetryLogs = repository.recentTelemetry.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        chatMessages = repository.allChatMessages.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        videos = repository.allVideos.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        images = repository.allImages.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        searchReports = repository.allSearchReports.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        voiceSessions = repository.allVoiceSessions.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        userAccount = repository.userAccount.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), null
        )
    }

    fun selectTab(tab: AppNavTab) {
        _selectedTab.value = tab
    }

    // --- 1. Multi-turn Chat Management ---
    fun updateChatInput(input: String) {
        _chatState.value = _chatState.value.copy(currentInput = input)
    }

    fun setChatModel(model: String) {
        _chatState.value = _chatState.value.copy(selectedModel = model)
    }

    fun selectChatRole(roleName: String) {
        val persona = availableRoles.find { it.name == roleName }
        if (persona != null) {
            _chatState.value = _chatState.value.copy(
                selectedRole = persona.name,
                customSystemInstruction = persona.systemInstruction,
                selectedModel = persona.recommendedModel
            )
        }
    }

    fun updateCustomSystemInstruction(instruction: String) {
        _chatState.value = _chatState.value.copy(customSystemInstruction = instruction)
    }

    fun toggleChatHighThinking(enabled: Boolean) {
        _chatState.value = _chatState.value.copy(isHighThinking = enabled)
    }

    fun clearChat() {
        viewModelScope.launch {
            repository.clearChatMessages()
            repository.insertChatMessage(
                ChatMessageEntity(
                    sender = "ai",
                    content = "Chat thread reset. Operating as '${_chatState.value.selectedRole}' with ${_chatState.value.selectedModel}. How can I assist you?",
                    timestamp = System.currentTimeMillis(),
                    modelUsed = _chatState.value.selectedModel
                )
            )
            _chatState.value = _chatState.value.copy(errorMessage = null)
        }
    }

    fun deleteChatMessage(message: ChatMessageEntity) {
        viewModelScope.launch {
            repository.deleteChatMessage(message)
        }
    }

    fun sendChatMessage(customText: String? = null) {
        val messageText = (customText ?: _chatState.value.currentInput).trim()
        if (messageText.isBlank()) return

        val history = chatMessages.value.map {
            Pair(if (it.sender == "user") "user" else "model", it.content)
        }

        _chatState.value = _chatState.value.copy(
            currentInput = "",
            isSending = true,
            errorMessage = null
        )

        viewModelScope.launch {
            val result = repository.sendChatMessage(
                history = history,
                message = messageText,
                model = _chatState.value.selectedModel,
                systemInstruction = _chatState.value.customSystemInstruction,
                isHighThinking = _chatState.value.isHighThinking
            )

            if (result.isSuccess) {
                _chatState.value = _chatState.value.copy(isSending = false)
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "Failed to get AI response"
                _chatState.value = _chatState.value.copy(
                    isSending = false,
                    errorMessage = err
                )
            }
        }
    }

    // --- 2. Veo 3 Video Generation ---
    fun updateVideoPrompt(prompt: String) {
        _videoState.value = _videoState.value.copy(prompt = prompt)
    }

    fun setVideoAspectRatio(aspectRatio: String) {
        _videoState.value = _videoState.value.copy(selectedAspectRatio = aspectRatio)
    }

    fun setVideoResolution(resolution: String) {
        _videoState.value = _videoState.value.copy(selectedResolution = resolution)
    }

    fun setVideoSourceImage(bitmap: Bitmap?, name: String?) {
        _videoState.value = _videoState.value.copy(
            sourceImageBitmap = bitmap,
            sourceImageName = name
        )
    }

    fun generateVeoVideo() {
        val prompt = _videoState.value.prompt.trim()
        if (prompt.isBlank()) return

        _videoState.value = _videoState.value.copy(isGenerating = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val result = repository.generateVeoVideo(
                    prompt = prompt,
                    sourceBitmap = _videoState.value.sourceImageBitmap,
                    sourceImagePath = _videoState.value.sourceImageName,
                    aspectRatio = _videoState.value.selectedAspectRatio,
                    resolution = _videoState.value.selectedResolution
                )
                _videoState.value = _videoState.value.copy(
                    isGenerating = false,
                    latestResult = result,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _videoState.value = _videoState.value.copy(
                    isGenerating = false,
                    errorMessage = e.localizedMessage ?: "Video generation failed"
                )
            }
        }
    }

    fun deleteVideo(video: VeoVideoEntity) {
        viewModelScope.launch {
            repository.deleteVeoVideo(video)
        }
    }

    // --- 3. Google Search Grounding ---
    fun updateSearchQuery(query: String) {
        _searchState.value = _searchState.value.copy(query = query)
    }

    fun executeSearchGrounding() {
        val query = _searchState.value.query.trim()
        if (query.isBlank()) return

        _searchState.value = _searchState.value.copy(isSearching = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val report = repository.executeSearchGrounding(query)
                _searchState.value = _searchState.value.copy(
                    isSearching = false,
                    latestReport = report,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _searchState.value = _searchState.value.copy(
                    isSearching = false,
                    errorMessage = e.localizedMessage ?: "Search grounding query failed"
                )
            }
        }
    }

    fun deleteSearchReport(report: SearchReportEntity) {
        viewModelScope.launch {
            repository.deleteSearchReport(report)
        }
    }

    // --- 4. Create & Edit Images ---
    fun updateImagePrompt(prompt: String) {
        _imageState.value = _imageState.value.copy(prompt = prompt)
    }

    fun setImageAspectRatio(aspectRatio: String) {
        _imageState.value = _imageState.value.copy(aspectRatio = aspectRatio)
    }

    fun setImageResolution(resolution: String) {
        _imageState.value = _imageState.value.copy(resolution = resolution)
    }

    fun setImageInputSource(bitmap: Bitmap?, name: String?) {
        _imageState.value = _imageState.value.copy(
            inputImageBitmap = bitmap,
            inputImageName = name
        )
    }

    fun generateOrEditImage() {
        val prompt = _imageState.value.prompt.trim()
        if (prompt.isBlank()) return

        _imageState.value = _imageState.value.copy(isGenerating = true, errorMessage = null)

        viewModelScope.launch {
            try {
                val result = repository.generateOrEditImage(
                    prompt = prompt,
                    inputBitmap = _imageState.value.inputImageBitmap,
                    sourceImagePath = _imageState.value.inputImageName,
                    aspectRatio = _imageState.value.aspectRatio,
                    resolution = _imageState.value.resolution
                )
                _imageState.value = _imageState.value.copy(
                    isGenerating = false,
                    latestResult = result,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _imageState.value = _imageState.value.copy(
                    isGenerating = false,
                    errorMessage = e.localizedMessage ?: "Image generation failed"
                )
            }
        }
    }

    fun deleteGeneratedImage(image: GeneratedImageEntity) {
        viewModelScope.launch {
            repository.deleteGeneratedImage(image)
        }
    }

    // --- 5. Real-time Live Voice Copilot ---
    fun toggleVoiceListening() {
        val willListen = !_voiceState.value.isListening
        _voiceState.value = _voiceState.value.copy(isListening = willListen)

        if (willListen) {
            // Trigger sample live speech turn
            val sampleUtterances = listOf(
                "Check cluster pod allocation status and security proofs.",
                "Generate a quick architectural summary of our microservices.",
                "How are the Veo video generation queues performing today?",
                "What is the cryptographic status of our HMAC gateway?"
            )
            val selected = sampleUtterances.random()
            _voiceState.value = _voiceState.value.copy(
                currentSpokenText = selected,
                isThinking = true
            )

            viewModelScope.launch {
                val reply = repository.executeVoiceTurn(selected)
                _voiceState.value = _voiceState.value.copy(
                    isListening = false,
                    isThinking = false,
                    latestAiSpokenReply = reply
                )
            }
        }
    }

    fun sendDirectVoiceUtterance(text: String) {
        if (text.isBlank()) return
        _voiceState.value = _voiceState.value.copy(
            currentSpokenText = text,
            isThinking = true
        )
        viewModelScope.launch {
            val reply = repository.executeVoiceTurn(text)
            _voiceState.value = _voiceState.value.copy(
                isThinking = false,
                latestAiSpokenReply = reply
            )
        }
    }

    fun clearVoiceHistory() {
        viewModelScope.launch {
            repository.clearVoiceSessions()
        }
    }

    // --- 6. Firebase Authentication & Firestore Persistence Sync ---
    fun performGoogleSignIn(email: String = "loyaltymarquis777@gmail.com", displayName: String = "Marquis Enterprise") {
        viewModelScope.launch {
            repository.signInWithGoogle(email, displayName, null)
        }
    }

    fun performSignOut() {
        viewModelScope.launch {
            repository.signOutUser()
        }
    }

    fun triggerFirestoreSync() {
        viewModelScope.launch {
            repository.syncFirestoreCollections()
        }
    }

    // --- 7. Grok Leo Frameworks & Prompts ---
    fun selectFramework(framework: PromptFrameworkEntity) {
        _selectedFramework.value = framework
        _executionState.value = _executionState.value.copy(
            selectedModel = framework.targetModel,
            isHighThinking = framework.isHighThinking
        )
    }

    fun toggleHighThinking(enabled: Boolean) {
        _executionState.value = _executionState.value.copy(isHighThinking = enabled)
    }

    fun setTargetModel(model: String) {
        _executionState.value = _executionState.value.copy(selectedModel = model)
    }

    fun executePrompt(
        framework: PromptFrameworkEntity?,
        customPrompt: String,
        inputs: Map<String, String>
    ) {
        viewModelScope.launch {
            _executionState.value = _executionState.value.copy(
                isExecuting = true,
                output = "",
                errorMessage = null
            )

            val targetFramework = framework ?: PromptFrameworkEntity(
                title = "Ad-Hoc Grok Leo Query",
                category = "Grok Leo Protocol",
                systemInstruction = "You are Sosa X AI Grok Leo Core: a hyper-advanced engineering intelligence. Deliver concise, production-ready, rigorous technical answers.",
                userTemplate = customPrompt,
                targetModel = _executionState.value.selectedModel,
                temperature = 0.2f,
                topP = 0.95f,
                isHighThinking = _executionState.value.isHighThinking
            )

            val adjustedFramework = targetFramework.copy(
                targetModel = _executionState.value.selectedModel,
                isHighThinking = _executionState.value.isHighThinking
            )

            val result = repository.executeGrokLeoPrompt(
                framework = adjustedFramework,
                userInputs = inputs,
                customPromptText = customPrompt
            )

            if (result.isSuccess) {
                val (outText, duration) = result.getOrThrow()
                _executionState.value = _executionState.value.copy(
                    isExecuting = false,
                    output = outText,
                    executionTimeMs = duration,
                    errorMessage = null
                )
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "Execution failed"
                _executionState.value = _executionState.value.copy(
                    isExecuting = false,
                    output = "",
                    errorMessage = err
                )
            }
        }
    }

    // --- Endpoints, Containers & Crypto ---
    fun testEndpoint(endpoint: ApiEndpointEntity) {
        viewModelScope.launch {
            repository.testApiEndpoint(endpoint)
        }
    }

    fun addEndpoint(endpoint: ApiEndpointEntity) {
        viewModelScope.launch {
            repository.saveApiEndpoint(endpoint)
        }
    }

    fun deleteEndpoint(endpoint: ApiEndpointEntity) {
        viewModelScope.launch {
            repository.deleteApiEndpoint(endpoint)
        }
    }

    fun scaleContainer(container: ContainerConfigEntity, newReplicas: Int) {
        viewModelScope.launch {
            repository.scaleContainer(container, newReplicas)
        }
    }

    fun addContainer(container: ContainerConfigEntity) {
        viewModelScope.launch {
            repository.saveContainer(container)
        }
    }

    fun deleteContainer(container: ContainerConfigEntity) {
        viewModelScope.launch {
            repository.deleteContainer(container)
        }
    }

    fun addFramework(framework: PromptFrameworkEntity) {
        viewModelScope.launch {
            repository.savePromptFramework(framework)
        }
    }

    fun deleteFramework(framework: PromptFrameworkEntity) {
        viewModelScope.launch {
            repository.deletePromptFramework(framework)
        }
    }

    fun runCryptoVerification(payload: String, secretKey: String) {
        viewModelScope.launch {
            val res = repository.verifyCryptoSignature(payload, secretKey)
            _cryptoResult.value = res
        }
    }

    fun clearTelemetry() {
        viewModelScope.launch {
            repository.clearTelemetryLogs()
        }
    }
}
