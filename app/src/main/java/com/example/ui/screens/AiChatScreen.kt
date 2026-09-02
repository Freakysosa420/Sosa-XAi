package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StatusPill
import com.example.ui.theme.ElegantActivePill
import com.example.ui.theme.ElegantIceBlue
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderOn
import com.example.ui.theme.ElegantLiveGreen
import com.example.ui.theme.SosaBackground
import com.example.ui.theme.SosaBorderSubtle
import com.example.ui.theme.SosaDarkBg
import com.example.ui.theme.SosaRose
import com.example.ui.theme.SosaSurfaceDark
import com.example.ui.theme.SosaSurfaceInset
import com.example.ui.theme.SosaSurfaceVariant
import com.example.ui.theme.SosaTextMuted
import com.example.ui.theme.SosaTextPrimary
import com.example.ui.theme.SosaTextSecondary
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.SosaXaiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiChatScreen(
    viewModel: SosaXaiViewModel,
    modifier: Modifier = Modifier
) {
    val chatState by viewModel.chatState.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    // Quick prompts suggestions
    val quickSuggestions = listOf(
        "Audit Kubernetes cluster security",
        "Explain Grok Leo reasoning structure",
        "Scale pod replicas for high load",
        "Generate HMAC verification code",
        "Optimize API gateway latency"
    )

    val supportedModels = listOf(
        "gemini-3.1-pro-preview" to "Gemini 3.1 Pro",
        "gemini-3.5-flash" to "Gemini 3.5 Flash",
        "gemini-3.1-flash-lite-preview" to "Flash Lite"
    )

    // Auto-scroll on new message
    LaunchedEffect(chatState.messages.size, chatState.isSending) {
        if (chatState.messages.isNotEmpty()) {
            listState.animateScrollToItem(chatState.messages.size)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SosaBackground)
            .testTag("ai_chat_screen")
    ) {
        // Top Control Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SosaBorderSubtle),
            color = SosaSurfaceDark
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(ElegantLavender.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = "AI Agent",
                                tint = ElegantLavender,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "SOSA X AI COPILOT",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElegantLavender,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Autonomous Enterprise AI Intelligence",
                                fontSize = 10.sp,
                                color = SosaTextSecondary
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        StatusPill(if (chatState.isSending) "THINKING" else "ONLINE")

                        IconButton(
                            onClick = { viewModel.clearChat() },
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("clear_chat_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CleaningServices,
                                contentDescription = "Clear Chat",
                                tint = SosaTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Model Selector & High Thinking Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    supportedModels.forEach { (modelId, label) ->
                        val isSelected = chatState.selectedModel == modelId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) ElegantActivePill else SosaSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) ElegantLavender else SosaBorderSubtle,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable { viewModel.setChatModel(modelId) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) ElegantLavender else SosaTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // High Thinking toggle badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (chatState.isHighThinking) ElegantLiveGreen.copy(alpha = 0.12f) else SosaSurfaceVariant)
                            .border(
                                1.dp,
                                if (chatState.isHighThinking) ElegantLiveGreen.copy(alpha = 0.4f) else SosaBorderSubtle,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.toggleChatHighThinking(!chatState.isHighThinking) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (chatState.isHighThinking) ElegantLiveGreen else SosaTextMuted)
                        )
                        Text(
                            text = if (chatState.isHighThinking) "High Thinking: ON" else "High Thinking: OFF",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (chatState.isHighThinking) ElegantLiveGreen else SosaTextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Error Banner if present
        if (chatState.errorMessage != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, SosaRose.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                color = SosaRose.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "System notice: ${chatState.errorMessage}",
                    color = SosaRose,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(chatState.messages, key = { it.id }) { message ->
                ChatMessageBubble(
                    message = message,
                    onCopy = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("AI Response", message.content))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (chatState.isSending) {
                item {
                    AiThinkingIndicator()
                }
            }
        }

        // Quick Suggestions Horizontal Chips
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickSuggestions.forEach { suggestion ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, SosaBorderSubtle, RoundedCornerShape(14.dp))
                        .clickable {
                            viewModel.sendChatMessage(suggestion)
                        },
                    color = SosaSurfaceDark
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = ElegantIceBlue,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = suggestion,
                            fontSize = 10.sp,
                            color = SosaTextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Bottom Input Area
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SosaBorderSubtle),
            color = SosaSurfaceDark
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = chatState.currentInput,
                    onValueChange = { viewModel.updateChatInput(it) },
                    placeholder = {
                        Text(
                            text = "Ask Sosa X AI anything (e.g. cloud, reasoning, code)...",
                            fontSize = 12.sp,
                            color = SosaTextMuted
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp, max = 120.dp)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElegantLavender,
                        unfocusedBorderColor = SosaBorderSubtle,
                        focusedTextColor = SosaTextPrimary,
                        unfocusedTextColor = SosaTextPrimary,
                        cursorColor = ElegantLavender,
                        focusedContainerColor = SosaSurfaceInset,
                        unfocusedContainerColor = SosaSurfaceInset
                    ),
                    maxLines = 4
                )

                IconButton(
                    onClick = { viewModel.sendChatMessage() },
                    enabled = chatState.currentInput.isNotBlank() && !chatState.isSending,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            if (chatState.currentInput.isNotBlank() && !chatState.isSending)
                                ElegantLavender
                            else
                                SosaSurfaceVariant
                        )
                        .testTag("send_chat_button")
                ) {
                    if (chatState.isSending) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = ElegantLavender,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (chatState.currentInput.isNotBlank()) ElegantLavenderOn else SosaTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    onCopy: () -> Unit
) {
    val isUser = message.sender == "user"
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }
    val formattedTime = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .padding(end = 8.dp, top = 2.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(ElegantLavender.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "AI",
                    tint = ElegantLavender,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .border(
                        1.dp,
                        if (isUser) ElegantLavender.copy(alpha = 0.3f) else SosaBorderSubtle,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    ),
                color = if (isUser) ElegantLavender.copy(alpha = 0.18f) else SosaSurfaceDark
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.content,
                        fontSize = 13.sp,
                        color = SosaTextPrimary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formattedTime,
                            fontSize = 9.sp,
                            color = SosaTextMuted,
                            fontFamily = FontFamily.Monospace
                        )

                        if (!isUser) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                if (message.latencyMs != null && message.latencyMs > 0) {
                                    Text(
                                        text = "${message.latencyMs}ms",
                                        fontSize = 9.sp,
                                        color = ElegantIceBlue,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy message",
                                    tint = SosaTextSecondary,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { onCopy() }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (isUser) {
            Box(
                modifier = Modifier
                    .padding(start = 8.dp, top = 2.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SosaSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = SosaTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun AiThinkingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(end = 8.dp)
                .size(28.dp)
                .clip(CircleShape)
                .background(ElegantLavender.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = "AI",
                tint = ElegantLavender,
                modifier = Modifier.size(16.dp)
            )
        }

        Surface(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, SosaBorderSubtle, RoundedCornerShape(16.dp)),
            color = SosaSurfaceDark
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = ElegantLavender,
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Sosa X AI is thinking...",
                    fontSize = 11.sp,
                    color = ElegantLavender,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
