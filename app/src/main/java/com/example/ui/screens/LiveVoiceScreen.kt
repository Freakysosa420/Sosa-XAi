package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.VoiceSessionEntity
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
import com.example.ui.viewmodel.SosaXaiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LiveVoiceScreen(
    viewModel: SosaXaiViewModel,
    modifier: Modifier = Modifier
) {
    val voiceState by viewModel.voiceState.collectAsState()
    val voiceSessions by viewModel.voiceSessions.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (voiceState.isListening) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SosaBackground)
            .testTag("live_voice_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SosaSurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SosaBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ElegantLiveGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Live Voice",
                                    tint = ElegantLiveGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Live Voice Copilot",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SosaTextPrimary
                                )
                                Text(
                                    text = "Model: gemini-3.1-flash-live-preview",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = SosaTextSecondary
                                )
                            }
                        }
                        StatusPill(
                            text = if (voiceState.isListening) "LISTENING" else "STANDBY",
                            containerColor = if (voiceState.isListening) ElegantLiveGreen else SosaSurfaceInset,
                            contentColor = if (voiceState.isListening) Color.Black else SosaTextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Real-time, bidirectional voice dialogue powered by Gemini 3.1 Flash Live API. Low-latency spoken copilot for hands-free systems control and cloud inspection.",
                        fontSize = 13.sp,
                        color = SosaTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Live Interactive Voice Stage
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SosaSurfaceDark),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SosaBorderSubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (voiceState.isListening) "Listening to Voice Input..." else if (voiceState.isThinking) "Processing Voice Stream..." else "Tap Microphone to Speak",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (voiceState.isListening) ElegantLiveGreen else SosaTextPrimary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Pulsating Mic Button
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        if (voiceState.isListening) ElegantLiveGreen else ElegantLavender,
                                        if (voiceState.isListening) ElegantLiveGreen.copy(alpha = 0.3f) else ElegantLavender.copy(alpha = 0.2f)
                                    )
                                )
                            )
                            .clickable { viewModel.toggleVoiceListening() }
                            .testTag("voice_mic_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (voiceState.isThinking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(40.dp),
                                color = Color.Black,
                                strokeWidth = 3.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (voiceState.isListening) Icons.Default.Mic else Icons.Default.MicOff,
                                contentDescription = "Mic",
                                tint = Color.Black,
                                modifier = Modifier.size(42.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // User speech transcript bubble
                    if (voiceState.currentSpokenText.isNotBlank()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = SosaSurfaceInset,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = "User Speech",
                                    tint = ElegantIceBlue,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "User Spoken Input",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ElegantIceBlue
                                    )
                                    Text(
                                        text = "\"${voiceState.currentSpokenText}\"",
                                        fontSize = 13.sp,
                                        color = SosaTextPrimary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Live AI Voice Response Bubble
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = ElegantActivePill,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ElegantLavender.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "AI Speech",
                                tint = ElegantLavenderOn,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Gemini Live Voice Audio Synthesis",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantLavenderOn
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = voiceState.latestAiSpokenReply,
                                    fontSize = 13.sp,
                                    color = SosaTextPrimary,
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Quick voice test triggers
                    Text(
                        text = "Or trigger instant voice prompts:",
                        fontSize = 11.sp,
                        color = SosaTextMuted
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "Status audit",
                            "Scale replicas",
                            "Crypto verify"
                        ).forEach { prompt ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.sendDirectVoiceUtterance(prompt) },
                                color = SosaSurfaceInset,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SosaBorderSubtle)
                            ) {
                                Text(
                                    text = prompt,
                                    fontSize = 11.sp,
                                    color = ElegantIceBlue,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        // Voice Session Log Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Room Stored Voice Sessions (${voiceSessions.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SosaTextPrimary
                )
                if (voiceSessions.isNotEmpty()) {
                    IconButton(onClick = { viewModel.clearVoiceHistory() }) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear",
                            tint = SosaTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        if (voiceSessions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SosaSurfaceDark),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = "No sessions",
                            tint = SosaTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No voice sessions recorded yet.",
                            fontSize = 13.sp,
                            color = SosaTextSecondary
                        )
                    }
                }
            }
        } else {
            items(voiceSessions, key = { it.id }) { session ->
                VoiceSessionItemCard(session = session)
            }
        }
    }
}

@Composable
fun VoiceSessionItemCard(session: VoiceSessionEntity) {
    val dateStr = remember(session.timestamp) {
        SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(session.timestamp))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SosaSurfaceDark),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SosaBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Voice Turn • $dateStr",
                    fontSize = 11.sp,
                    color = SosaTextMuted
                )
                Text(
                    text = "${session.latencyMs}ms",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = ElegantLiveGreen
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "User: \"${session.userUtterance}\"",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ElegantIceBlue
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "AI: \"${session.aiReply}\"",
                fontSize = 12.sp,
                color = SosaTextPrimary,
                lineHeight = 16.sp
            )
        }
    }
}
