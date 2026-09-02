package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: SosaXaiViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val userAccount by viewModel.userAccount.collectAsState()
    val tabScrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SosaDarkBg,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SosaBackground)
            ) {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(ElegantLavender),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Security Shield",
                                        tint = ElegantLavenderOn,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Sosa XAI Enterprise",
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SosaTextPrimary
                                    )
                                    Text(
                                        text = if (userAccount != null) "FIRESTORE SYNCED • ${userAccount?.displayName}" else "GOOGLE AUTH READY",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (userAccount != null) ElegantLiveGreen else ElegantIceBlue,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }

                            // Google Sign-In & Sync Action
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        if (userAccount == null) {
                                            viewModel.performGoogleSignIn()
                                        } else {
                                            viewModel.triggerFirestoreSync()
                                        }
                                    }
                                    .testTag("google_auth_top_button"),
                                color = if (userAccount != null) ElegantLiveGreen.copy(alpha = 0.15f) else ElegantActivePill,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (userAccount != null) ElegantLiveGreen.copy(alpha = 0.4f) else ElegantLavender.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (userAccount != null) Icons.Default.CloudDone else Icons.Default.AccountCircle,
                                        contentDescription = "User",
                                        tint = if (userAccount != null) ElegantLiveGreen else ElegantLavender,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (userAccount != null) "Synced" else "Sign In",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (userAccount != null) ElegantLiveGreen else ElegantLavender
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SosaBackground
                    )
                )

                // Horizontal Studio Category Navigation Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(tabScrollState)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppNavTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        val (icon, label) = when (tab) {
                            AppNavTab.OVERVIEW -> Pair(Icons.Default.Dashboard, "Overview")
                            AppNavTab.AI_CHAT -> Pair(Icons.AutoMirrored.Filled.Chat, "Gemini Chat")
                            AppNavTab.VIDEO_STUDIO -> Pair(Icons.Default.Movie, "Veo 3 Video")
                            AppNavTab.SEARCH_GROUNDING -> Pair(Icons.Default.TravelExplore, "Search Grounding")
                            AppNavTab.IMAGE_STUDIO -> Pair(Icons.Default.Image, "Image Studio")
                            AppNavTab.VOICE_LIVE -> Pair(Icons.Default.GraphicEq, "Live Voice")
                            AppNavTab.GROK_LEO -> Pair(Icons.Default.Psychology, "Grok Leo")
                            AppNavTab.ENDPOINTS -> Pair(Icons.Default.Api, "APIs")
                            AppNavTab.CONTAINERS -> Pair(Icons.Default.Layers, "Containers")
                            AppNavTab.SECURITY -> Pair(Icons.Default.Shield, "Security & Sync")
                        }

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.selectTab(tab) }
                                .testTag("nav_chip_${tab.name.lowercase()}"),
                            color = if (isSelected) ElegantLavender else SosaSurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) ElegantLavender else SosaBorderSubtle
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) Color.Black else SosaTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.Black else SosaTextPrimary
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .border(1.dp, SosaBorderSubtle, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                color = SosaSurfaceNav,
                shadowElevation = 16.dp
            ) {
                NavigationBar(
                    containerColor = SosaSurfaceNav,
                    contentColor = SosaTextSecondary,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(68.dp)
                ) {
                    val primaryTabs = listOf(
                        AppNavTab.OVERVIEW,
                        AppNavTab.AI_CHAT,
                        AppNavTab.VIDEO_STUDIO,
                        AppNavTab.SEARCH_GROUNDING,
                        AppNavTab.IMAGE_STUDIO,
                        AppNavTab.VOICE_LIVE
                    )

                    primaryTabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        val icon = when (tab) {
                            AppNavTab.OVERVIEW -> Icons.Default.Dashboard
                            AppNavTab.AI_CHAT -> Icons.AutoMirrored.Filled.Chat
                            AppNavTab.VIDEO_STUDIO -> Icons.Default.Movie
                            AppNavTab.SEARCH_GROUNDING -> Icons.Default.TravelExplore
                            AppNavTab.IMAGE_STUDIO -> Icons.Default.Image
                            AppNavTab.VOICE_LIVE -> Icons.Default.GraphicEq
                            else -> Icons.Default.Hub
                        }

                        val shortTitle = when (tab) {
                            AppNavTab.OVERVIEW -> "Home"
                            AppNavTab.AI_CHAT -> "Chat"
                            AppNavTab.VIDEO_STUDIO -> "Veo 3"
                            AppNavTab.SEARCH_GROUNDING -> "Search"
                            AppNavTab.IMAGE_STUDIO -> "Images"
                            AppNavTab.VOICE_LIVE -> "Voice"
                            else -> tab.title
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    icon,
                                    contentDescription = shortTitle,
                                    tint = if (isSelected) ElegantLavender else SosaTextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = shortTitle,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isSelected) ElegantLavender else SosaTextMuted
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ElegantLavender,
                                selectedTextColor = ElegantLavender,
                                indicatorColor = ElegantActivePill,
                                unselectedIconColor = SosaTextSecondary,
                                unselectedTextColor = SosaTextMuted
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AppNavTab.OVERVIEW -> OverviewScreen(viewModel = viewModel)
                AppNavTab.AI_CHAT -> ChatScreen(viewModel = viewModel)
                AppNavTab.VIDEO_STUDIO -> VideoStudioScreen(viewModel = viewModel)
                AppNavTab.SEARCH_GROUNDING -> SearchGroundingScreen(viewModel = viewModel)
                AppNavTab.IMAGE_STUDIO -> ImageStudioScreen(viewModel = viewModel)
                AppNavTab.VOICE_LIVE -> LiveVoiceScreen(viewModel = viewModel)
                AppNavTab.GROK_LEO -> GrokLeoScreen(viewModel = viewModel)
                AppNavTab.ENDPOINTS -> EndpointsScreen(viewModel = viewModel)
                AppNavTab.CONTAINERS -> ContainersScreen(viewModel = viewModel)
                AppNavTab.SECURITY -> SecurityLogsScreen(viewModel = viewModel)
            }
        }
    }
}
