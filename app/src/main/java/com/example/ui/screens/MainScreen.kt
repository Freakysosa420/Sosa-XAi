package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = SosaDarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(ElegantLavender),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = "Security Shield",
                                    tint = ElegantLavenderOn,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Sosa XAI",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = SosaTextPrimary
                                )
                                Text(
                                    text = "ENTERPRISE PROTOCOL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantIceBlue,
                                    letterSpacing = 1.5.sp
                                )
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(SosaSurfaceDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = SosaTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(SosaSurfaceDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SettingsInputComponent,
                                    contentDescription = "Settings",
                                    tint = SosaTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SosaBackground
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .border(1.dp, SosaBorderSubtle, RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
                color = SosaSurfaceNav,
                shadowElevation = 16.dp
            ) {
                NavigationBar(
                    containerColor = SosaSurfaceNav,
                    contentColor = SosaTextSecondary,
                    tonalElevation = 0.dp,
                    modifier = Modifier.height(76.dp)
                ) {
                    AppNavTab.values().forEach { tab ->
                        val isSelected = selectedTab == tab
                        val icon = when (tab) {
                            AppNavTab.OVERVIEW -> Icons.Default.Dashboard
                            AppNavTab.GROK_LEO -> Icons.Default.Psychology
                            AppNavTab.ENDPOINTS -> Icons.Default.Api
                            AppNavTab.CONTAINERS -> Icons.Default.Layers
                            AppNavTab.SECURITY -> Icons.Default.Shield
                        }

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(tab) },
                            icon = {
                                Icon(
                                    icon,
                                    contentDescription = tab.title,
                                    tint = if (isSelected) ElegantLavender else SosaTextSecondary
                                )
                            },
                            label = {
                                Text(
                                    text = when (tab) {
                                        AppNavTab.OVERVIEW -> "Status"
                                        AppNavTab.GROK_LEO -> "Grok Leo"
                                        AppNavTab.ENDPOINTS -> "APIs"
                                        AppNavTab.CONTAINERS -> "Containers"
                                        AppNavTab.SECURITY -> "Vault"
                                    },
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
                AppNavTab.GROK_LEO -> GrokLeoScreen(viewModel = viewModel)
                AppNavTab.ENDPOINTS -> EndpointsScreen(viewModel = viewModel)
                AppNavTab.CONTAINERS -> ContainersScreen(viewModel = viewModel)
                AppNavTab.SECURITY -> SecurityLogsScreen(viewModel = viewModel)
            }
        }
    }
}
