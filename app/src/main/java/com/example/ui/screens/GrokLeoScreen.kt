package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.PromptFrameworkEntity
import com.example.ui.components.CodeOutputBox
import com.example.ui.components.StatusPill
import com.example.ui.components.SystemMetricBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.SosaXaiViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GrokLeoScreen(
    viewModel: SosaXaiViewModel,
    modifier: Modifier = Modifier
) {
    val frameworks by viewModel.promptFrameworks.collectAsState()
    val execState by viewModel.executionState.collectAsState()
    val selectedFramework by viewModel.selectedFramework.collectAsState()

    val currentFramework = selectedFramework ?: frameworks.firstOrNull()

    var customPromptInput by remember { mutableStateOf("") }
    val templateInputs = remember { mutableStateMapOf<String, String>() }
    var showNewTemplateDialog by remember { mutableStateOf(false) }

    // Initialize inputs when framework changes
    remember(currentFramework?.id) {
        templateInputs.clear()
        if (currentFramework != null) {
            val regex = Regex("""\{\{([a-zA-Z0-9_]+)\}\}""")
            regex.findAll(currentFramework.userTemplate).forEach { match ->
                val varName = match.groupValues[1]
                if (!templateInputs.containsKey(varName)) {
                    val defaultVal = when (varName) {
                        "system_name" -> "Sosa X AI Microservice Grid"
                        "requirement" -> "Sub-20ms latency telemetry ingest with mTLS & H100 GPU pod orchestration"
                        "constraints" -> "Zero root privilege, AES-256-GCM encryption, auto-healing replicas"
                        "service_def" -> "grok-leo-inference-gateway"
                        "ingress_protocol" -> "gRPC / HTTP2 TLS"
                        "resources" -> "16 vCPU, 64GB RAM, 4x NVIDIA H100"
                        "payload" -> "{\"token\": \"eyJhbGciOi...\", \"scope\": \"xai:admin\"}"
                        "logs" -> "[WARN] Latency spike 42ms on cluster node gpu-west-02\n[INFO] Auto-scaler engaged"
                        else -> "Production parameters"
                    }
                    templateInputs[varName] = defaultVal
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SosaDarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GROK LEO TEMPLATE ENGINE",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantLavender,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "High-Thinking Architecture & Prompt Studio",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaTextPrimary
                    )
                }

                IconButton(
                    onClick = { showNewTemplateDialog = true },
                    modifier = Modifier.testTag("add_template_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Template",
                        tint = ElegantLavender
                    )
                }
            }
        }

        // Template Selection Chips
        item {
            Text(
                text = "SELECT PROTOCOL TEMPLATE",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SosaTextMuted
            )
            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                frameworks.forEach { fw ->
                    val isSelected = currentFramework?.id == fw.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectFramework(fw) },
                        label = {
                            Text(
                                text = fw.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElegantLavender.copy(alpha = 0.2f),
                            selectedLabelColor = ElegantLavender,
                            selectedLeadingIconColor = ElegantLavender,
                            containerColor = SosaSurfaceVariant,
                            labelColor = SosaTextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) ElegantLavender else SosaBorderSubtle,
                            selectedBorderColor = ElegantLavender,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // Framework Details & Model Engine Controls
        item {
            currentFramework?.let { fw ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, SosaBorderSubtle, RoundedCornerShape(16.dp)),
                    color = SosaSurfaceDark
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = fw.category.uppercase(),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantLiveGreen
                            )
                            if (fw.isDefault) {
                                StatusPill("DEFAULT")
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = fw.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = SosaTextPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "System Prompt Matrix:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SosaTextSecondary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            color = SosaSurfaceInset
                        ) {
                            Text(
                                text = fw.systemInstruction,
                                fontSize = 11.sp,
                                color = SosaTextSecondary,
                                modifier = Modifier.padding(10.dp),
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Model & High Thinking Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Target Model",
                                    fontSize = 11.sp,
                                    color = SosaTextMuted
                                )
                                Text(
                                    text = execState.selectedModel,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantLavender
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "High Thinking",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (execState.isHighThinking) ElegantLiveGreen else SosaTextMuted
                                    )
                                    Text(
                                        text = if (execState.isHighThinking) "ThinkingLevel.HIGH" else "Standard",
                                        fontSize = 9.sp,
                                        color = SosaTextMuted
                                    )
                                }
                                Switch(
                                    checked = execState.isHighThinking,
                                    onCheckedChange = { viewModel.toggleHighThinking(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = SosaDarkBg,
                                        checkedTrackColor = ElegantLiveGreen,
                                        uncheckedThumbColor = SosaTextMuted,
                                        uncheckedTrackColor = SosaSurfaceVariant
                                    ),
                                    modifier = Modifier.testTag("high_thinking_toggle")
                                )
                            }
                        }

                        // Model Quick Selectors
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "gemini-3.1-pro-preview" to "Pro (Deep Reasoning)",
                                "gemini-3.5-flash" to "Flash (High Speed)",
                                "gemini-3.1-flash-lite-preview" to "Flash Lite"
                            ).forEach { (mId, mLabel) ->
                                val isSelected = execState.selectedModel == mId
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ElegantLavender.copy(alpha = 0.2f) else SosaSurfaceVariant)
                                        .border(
                                            1.dp,
                                            if (isSelected) ElegantLavender else SosaBorderSubtle,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.setTargetModel(mId) }
                                        .padding(vertical = 8.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = mLabel,
                                        fontSize = 9.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) ElegantLavender else SosaTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Variable Inputs / Custom Prompt Runner
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, SosaBorderSubtle, RoundedCornerShape(16.dp)),
                color = SosaSurfaceDark
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DYNAMIC TEMPLATE PARAMETERS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantLavender,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (templateInputs.isEmpty()) {
                        OutlinedTextField(
                            value = customPromptInput,
                            onValueChange = { customPromptInput = it },
                            label = { Text("Prompt Query / Spec Instruction") },
                            placeholder = { Text("e.g. Design enterprise API gateway with mTLS") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_prompt_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElegantLavender,
                                unfocusedBorderColor = SosaBorderSubtle,
                                focusedTextColor = SosaTextPrimary,
                                unfocusedTextColor = SosaTextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        templateInputs.forEach { (varKey, varVal) ->
                            OutlinedTextField(
                                value = varVal,
                                onValueChange = { templateInputs[varKey] = it },
                                label = { Text(varKey.replace("_", " ").uppercase()) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .testTag("input_$varKey"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElegantLavender,
                                    unfocusedBorderColor = SosaBorderSubtle,
                                    focusedTextColor = SosaTextPrimary,
                                    unfocusedTextColor = SosaTextPrimary,
                                    focusedLabelColor = ElegantLavender,
                                    unfocusedLabelColor = SosaTextSecondary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            viewModel.executePrompt(
                                framework = currentFramework,
                                customPrompt = customPromptInput.ifBlank { currentFramework?.userTemplate ?: "" },
                                inputs = templateInputs
                            )
                        },
                        enabled = !execState.isExecuting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("execute_grok_leo_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElegantLavender,
                            contentColor = SosaDarkBg
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        if (execState.isExecuting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = SosaDarkBg,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Grok Leo Engine Thinking...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EXECUTE GROK LEO INFERENCE",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // Execution Output & Metrics
        if (execState.output.isNotBlank() || execState.errorMessage != null) {
            item {
                if (execState.errorMessage != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, SosaRose, RoundedCornerShape(14.dp)),
                        color = SosaRose.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "Execution Notice: ${execState.errorMessage}",
                            color = SosaRose,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (execState.output.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "GROK LEO SYNTHESIZED OUTPUT",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElegantLiveGreen,
                            letterSpacing = 0.8.sp
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${execState.executionTimeMs} ms",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = ElegantIceBlue
                            )
                            StatusPill("VERIFIED")
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    CodeOutputBox(
                        codeText = execState.output,
                        title = "GROK LEO DECOMPOSED SPECIFICATION"
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // New Template Creator Dialog
    if (showNewTemplateDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newCategory by remember { mutableStateOf("Grok Leo Protocol") }
        var newSystemPrompt by remember { mutableStateOf("") }
        var newUserTemplate by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewTemplateDialog = false },
            title = {
                Text(
                    text = "New Grok Leo Template",
                    fontWeight = FontWeight.Bold,
                    color = SosaCyan
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Template Title") },
                        placeholder = { Text("e.g. Grok Leo Autonomous Cluster Manager") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        label = { Text("Category") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSystemPrompt,
                        onValueChange = { newSystemPrompt = it },
                        label = { Text("System Instruction Matrix") },
                        placeholder = { Text("You are the Grok Leo...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                    OutlinedTextField(
                        value = newUserTemplate,
                        onValueChange = { newUserTemplate = it },
                        label = { Text("User Template (Use {{var}} tags)") },
                        placeholder = { Text("Target: {{target}}\nConfig: {{config}}") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.addFramework(
                                PromptFrameworkEntity(
                                    title = newTitle,
                                    category = newCategory,
                                    systemInstruction = newSystemPrompt.ifBlank { "You are Grok Leo." },
                                    userTemplate = newUserTemplate.ifBlank { "Execute architecture optimization for {{task}}" },
                                    targetModel = "gemini-3.1-pro-preview",
                                    temperature = 0.2f,
                                    topP = 0.95f,
                                    isHighThinking = true
                                )
                            )
                            showNewTemplateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SosaCyan, contentColor = SosaDarkBg)
                ) {
                    Text("Save Template", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewTemplateDialog = false }) {
                    Text("Cancel", color = SosaTextSecondary)
                }
            },
            containerColor = SosaSurfaceDark
        )
    }
}
