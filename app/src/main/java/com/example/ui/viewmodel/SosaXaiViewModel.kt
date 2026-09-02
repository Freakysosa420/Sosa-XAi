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
    GROK_LEO("Grok Leo Engine"),
    ENDPOINTS("API Endpoints"),
    CONTAINERS("Containers"),
    SECURITY("Security & Logs")
}

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
}
