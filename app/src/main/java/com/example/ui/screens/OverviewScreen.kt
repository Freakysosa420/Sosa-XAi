package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StatusPill
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.SosaXaiViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OverviewScreen(
    viewModel: SosaXaiViewModel,
    modifier: Modifier = Modifier
) {
    val endpoints by viewModel.endpoints.collectAsState()
    val containers by viewModel.containers.collectAsState()
    val frameworks by viewModel.promptFrameworks.collectAsState()
    val logs by viewModel.telemetryLogs.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()
    val videos by viewModel.videos.collectAsState()
    val images by viewModel.images.collectAsState()
    val searchReports by viewModel.searchReports.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SosaBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))

            // User Identity & Firebase Cloud Sync Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(
                        1.dp,
                        if (userAccount != null) ElegantLiveGreen.copy(alpha = 0.4f) else ElegantLavender.copy(alpha = 0.4f),
                        RoundedCornerShape(18.dp)
                    ),
                color = SosaSurfaceDark
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (userAccount != null) ElegantLiveGreen.copy(alpha = 0.2f) else ElegantLavender.copy(alpha = 0.2f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (userAccount != null) Icons.Default.CloudDone else Icons.Default.AccountCircle,
                                contentDescription = "User Profile",
                                tint = if (userAccount != null) ElegantLiveGreen else ElegantLavender,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = userAccount?.displayName ?: "Google Account Not Linked",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SosaTextPrimary
                            )
                            Text(
                                text = userAccount?.email ?: "Tap to authenticate with Firebase Auth",
                                fontSize = 11.sp,
                                color = SosaTextSecondary
                            )
                            if (userAccount != null) {
                                Text(
                                    text = "Firestore Synced: ${if (userAccount?.isFirestoreSynced == true) "READY" else "PENDING"} • Provider: ${userAccount?.provider}",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = ElegantLiveGreen
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (userAccount == null) {
                                viewModel.performGoogleSignIn()
                            } else {
                                viewModel.triggerFirestoreSync()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (userAccount != null) ElegantLiveGreen else ElegantLavender,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (userAccount != null) "Sync Now" else "Sign In",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            // Operational Overview Hero Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.dp, SosaBorder, RoundedCornerShape(24.dp)),
                color = SosaSurfaceDark,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "OPERATIONAL OVERVIEW",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElegantLavender,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "AI Systems Healthy",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Light,
                                color = SosaTextPrimary
                            )
                        }

                        // Elegant Live Status Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(SosaSurfaceInset)
                                .border(1.dp, ElegantLavender, RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .alpha(pulseAlpha)
                                        .clip(CircleShape)
                                        .background(ElegantLiveGreen)
                                )
                                Text(
                                    text = "LIVE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantLavender,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // 2x2 Nested Metric Insets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SosaSurfaceInset)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "LATENCY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SosaTextMuted,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "18.4ms",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SosaTextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SosaSurfaceInset)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "UPTIME",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SosaTextMuted,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "99.98%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SosaTextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SosaSurfaceInset)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "ACTIVE REPLICAS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SosaTextMuted,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${containers.sumOf { it.replicas }} Nodes",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SosaTextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(SosaSurfaceInset)
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "SECURITY AUDIT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SosaTextMuted,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "SHA-256 Pass",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SosaTextPrimary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // Multimodal AI Studio Suite Launchers
        item {
            Text(
                text = "MULTIMODAL AI & ENGINE STUDIOS",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = ElegantLavender,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Gemini Chat Card
                StudioLauncherCard(
                    title = "Gemini Multi-Turn Chat",
                    subtitle = "Model: gemini-3.1-pro-preview / gemini-3.5-flash with Thinking Mode & Roles",
                    icon = Icons.AutoMirrored.Filled.Chat,
                    accentColor = ElegantLavender,
                    badgeText = "CHAT",
                    onClick = { viewModel.selectTab(AppNavTab.AI_CHAT) }
                )

                // Veo 3 Video Studio Card
                StudioLauncherCard(
                    title = "Veo 3 Video Studio",
                    subtitle = "Generate video from text or animate photos into 16:9 / 9:16 streams (${videos.size} saved)",
                    icon = Icons.Default.Movie,
                    accentColor = ElegantLavender,
                    badgeText = "VEO 3",
                    onClick = { viewModel.selectTab(AppNavTab.VIDEO_STUDIO) }
                )

                // Google Search Grounding Card
                StudioLauncherCard(
                    title = "Google Search Grounding",
                    subtitle = "Query live real-world web intelligence with googleSearch tool & citations (${searchReports.size} reports)",
                    icon = Icons.Default.TravelExplore,
                    accentColor = ElegantIceBlue,
                    badgeText = "SEARCH",
                    onClick = { viewModel.selectTab(AppNavTab.SEARCH_GROUNDING) }
                )

                // Image Studio Card
                StudioLauncherCard(
                    title = "Image Studio (Create & Edit)",
                    subtitle = "Model: gemini-3.1-flash-image-preview text-to-image & photo modifications (${images.size} images)",
                    icon = Icons.Default.Image,
                    accentColor = ElegantLavender,
                    badgeText = "IMAGES",
                    onClick = { viewModel.selectTab(AppNavTab.IMAGE_STUDIO) }
                )

                // Live Voice Copilot Card
                StudioLauncherCard(
                    title = "Live Voice Copilot",
                    subtitle = "Real-time bidirectional speech conversation with gemini-3.1-flash-live-preview",
                    icon = Icons.Default.GraphicEq,
                    accentColor = ElegantLiveGreen,
                    badgeText = "VOICE LIVE",
                    onClick = { viewModel.selectTab(AppNavTab.VOICE_LIVE) }
                )
            }
        }

        // Infrastructure Matrix
        item {
            Text(
                text = "INFRASTRUCTURE & ORCHESTRATION",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = ElegantIceBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElegantGridCard(
                    title = "Endpoints",
                    subtitle = "${endpoints.size} Active Nodes",
                    icon = Icons.Default.Api,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.selectTab(AppNavTab.ENDPOINTS) }
                )
                ElegantGridCard(
                    title = "Containers",
                    subtitle = "Orchestration Stable",
                    icon = Icons.Default.Layers,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.selectTab(AppNavTab.CONTAINERS) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElegantGridCard(
                    title = "Verification",
                    subtitle = "Cryptographic AES",
                    icon = Icons.Default.Shield,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.selectTab(AppNavTab.SECURITY) }
                )
                ElegantGridCard(
                    title = "Telemetry Logs",
                    subtitle = "${logs.size} Audit Events",
                    icon = Icons.Default.Terminal,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.selectTab(AppNavTab.SECURITY) }
                )
            }
        }

        // Live Telemetry Stream
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE SYSTEM TELEMETRY",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElegantIceBlue,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "View All (${logs.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElegantLavender,
                    modifier = Modifier.clickable { viewModel.selectTab(AppNavTab.SECURITY) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (logs.isEmpty()) {
                Text(
                    text = "No telemetry events recorded yet.",
                    fontSize = 12.sp,
                    color = SosaTextMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    logs.take(4).forEach { log ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, SosaBorderSubtle, RoundedCornerShape(14.dp)),
                            color = SosaSurfaceCard
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = log.eventType,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ElegantIceBlue
                                        )
                                        Text(
                                            text = "• ${log.sourceModule}",
                                            fontSize = 11.sp,
                                            color = SosaTextSecondary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = log.details,
                                        fontSize = 11.sp,
                                        color = SosaTextPrimary,
                                        maxLines = 2
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.End) {
                                    StatusPill(log.status)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "${log.latencyMs}ms",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        color = ElegantLiveGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StudioLauncherCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    badgeText: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("studio_launcher_${title.replace(" ", "_").lowercase()}"),
        color = SosaSurfaceDark
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = SosaTextSecondary,
                        lineHeight = 15.sp,
                        maxLines = 2
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = badgeText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun ElegantGridCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SosaBorderSubtle, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("module_${title.replace(" ", "_").lowercase()}"),
        color = SosaSurfaceCard
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .height(100.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ElegantIceBlue,
                modifier = Modifier.size(24.dp)
            )
            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SosaTextSecondary
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = SosaTextMuted
                )
            }
        }
    }
}

@Composable
fun CapabilityItem(
    icon: ImageVector,
    title: String,
    desc: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
        }
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = SosaTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = SosaTextSecondary,
                lineHeight = 16.sp
            )
        }
    }
}
