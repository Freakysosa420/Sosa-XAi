package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ApiEndpointEntity
import com.example.data.local.entity.ContainerConfigEntity
import com.example.data.local.entity.PromptFrameworkEntity
import com.example.data.local.entity.TelemetryLogEntity
import com.example.data.remote.GeminiApiService
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
    AI_CHAT("AI Assistant"),
    GROK_LEO("Grok Leo Engine"),
    ENDPOINTS("API Endpoints"),
    CONTAINERS("Containers"),
    SECURITY("Security & Logs")
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user" or "ai"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val latencyMs: Long? = null,
    val isThinking: Boolean = false
)

data class ChatState(
    val messages: List<ChatMessage> = listOf(
        ChatMessage(
            sender = "ai",
            content = "Greetings. I am Sosa X AI Assistant. How can I assist you with architecture design, cloud orchestration, Grok Leo reasoning, or security auditing today?"
        )
    ),
    val isSending: Boolean = false,
    val currentInput: String = "",
    val selectedModel: String = "gemini-3.1-pro-preview",
    val isHighThinking: Boolean = true,
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

    private val _selectedTab = MutableStateFlow(AppNavTab.OVERVIEW)
    val selectedTab: StateFlow<AppNavTab> = _selectedTab.asStateFlow()

    private val _selectedFramework = MutableStateFlow<PromptFrameworkEntity?>(null)
    val selectedFramework: StateFlow<PromptFrameworkEntity?> = _selectedFramework.asStateFlow()

    private val _executionState = MutableStateFlow(GrokLeoExecutionState())
    val executionState: StateFlow<GrokLeoExecutionState> = _executionState.asStateFlow()

    private val _cryptoResult = MutableStateFlow<CryptoVerificationResult?>(null)
    val cryptoResult: StateFlow<CryptoVerificationResult?> = _cryptoResult.asStateFlow()

    private val _chatState = MutableStateFlow(ChatState())
    val chatState: StateFlow<ChatState> = _chatState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        val geminiService = GeminiApiService()
        repository = SosaXaiRepository(
            apiEndpointDao = database.apiEndpointDao(),
            containerConfigDao = database.containerConfigDao(),
            promptFrameworkDao = database.promptFrameworkDao(),
            telemetryLogDao = database.telemetryLogDao(),
            geminiService = geminiService
        )

        endpoints = repository.allEndpoints.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        containers = repository.allContainers.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        promptFrameworks = repository.allFrameworks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        telemetryLogs = repository.recentTelemetry.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    fun selectTab(tab: AppNavTab) {
        _selectedTab.value = tab
    }

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

    // AI Chat Management
    fun updateChatInput(input: String) {
        _chatState.value = _chatState.value.copy(currentInput = input)
    }

    fun setChatModel(model: String) {
        _chatState.value = _chatState.value.copy(selectedModel = model)
    }

    fun toggleChatHighThinking(enabled: Boolean) {
        _chatState.value = _chatState.value.copy(isHighThinking = enabled)
    }

    fun clearChat() {
        _chatState.value = _chatState.value.copy(
            messages = listOf(
                ChatMessage(
                    sender = "ai",
                    content = "Chat cleared. Ready for your instructions on Sosa X architecture, APIs, container topologies, or reasoning."
                )
            ),
            errorMessage = null
        )
    }

    fun sendChatMessage(customText: String? = null) {
        val messageText = (customText ?: _chatState.value.currentInput).trim()
        if (messageText.isBlank()) return

        val userMessage = ChatMessage(
            sender = "user",
            content = messageText
        )

        val updatedMessages = _chatState.value.messages + userMessage
        _chatState.value = _chatState.value.copy(
            messages = updatedMessages,
            currentInput = "",
            isSending = true,
            errorMessage = null
        )

        viewModelScope.launch {
            val history = updatedMessages
                .dropLast(1)
                .map { Pair(if (it.sender == "user") "user" else "model", it.content) }

            val result = repository.sendChatMessage(
                history = history,
                message = messageText,
                model = _chatState.value.selectedModel,
                isHighThinking = _chatState.value.isHighThinking
            )

            if (result.isSuccess) {
                val (reply, duration) = result.getOrThrow()
                val aiMessage = ChatMessage(
                    sender = "ai",
                    content = reply,
                    latencyMs = duration
                )
                _chatState.value = _chatState.value.copy(
                    messages = _chatState.value.messages + aiMessage,
                    isSending = false
                )
            } else {
                val err = result.exceptionOrNull()?.localizedMessage ?: "Failed to get AI response"
                _chatState.value = _chatState.value.copy(
                    isSending = false,
                    errorMessage = err
                )
            }
        }
    }
}
