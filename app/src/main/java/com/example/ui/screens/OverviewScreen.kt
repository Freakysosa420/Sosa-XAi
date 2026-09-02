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
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
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
            // Operational Overview Hero Card (Elegant Dark Section)
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
                                text = "System Healthy",
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

        // 2x2 Grid of Protocol Modules
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                        title = "Telemetry",
                        subtitle = "Routing Secure",
                        icon = Icons.Default.Terminal,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.selectTab(AppNavTab.SECURITY) }
                    )
                }
            }
        }

        // Primary Action Button (Elegant Lavender Button)
        item {
            Button(
                onClick = {
                    val firstFw = frameworks.firstOrNull()
                    if (firstFw != null) {
                        viewModel.selectFramework(firstFw)
                        viewModel.executePrompt(
                            framework = firstFw,
                            customPrompt = firstFw.userTemplate,
                            inputs = emptyMap()
                        )
                    }
                    viewModel.selectTab(AppNavTab.GROK_LEO)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .testTag("deploy_module_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElegantLavender,
                    contentColor = ElegantLavenderOn
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = ElegantLavenderOn,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Deploy Module Update",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Core Capabilities Matrix
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, SosaBorderSubtle, RoundedCornerShape(20.dp)),
                color = SosaSurfaceDark
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "CORE ARCHITECTURE MATRIX",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantLavender,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    CapabilityItem(
                        icon = Icons.Default.Bolt,
                        title = "Grok Leo Prompt Framework",
                        desc = "Precision-tuned system prompts with High Thinking level for architectural breakdown, zero-day threat analysis, and automated code generation.",
                        color = ElegantLavender
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CapabilityItem(
                        icon = Icons.Default.Code,
                        title = "Enterprise API Integrations",
                        desc = "Custom TLS Mutual, Bearer JWT, and HMAC-SHA256 authenticated routes with sub-30ms routing telemetry.",
                        color = ElegantIceBlue
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CapabilityItem(
                        icon = Icons.Default.Memory,
                        title = "Scalable Container Deployments",
                        desc = "Pre-configured Kubernetes & Docker manifests with multi-GPU H100 SXM5 orchestration and dynamic scaling.",
                        color = ElegantLiveGreen
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CapabilityItem(
                        icon = Icons.Default.Security,
                        title = "Cryptographic Verification & Audit",
                        desc = "Real-time SHA-256 HMAC payload verification, zero-trust perimeter enforcement, and structured telemetry logs.",
                        color = SosaAmber
                    )
                }
            }
        }

        // Live Telemetry Event Stream
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
                    logs.take(3).forEach { log ->
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
