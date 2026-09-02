package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.data.local.entity.SearchReportEntity
import com.example.data.remote.WebCitationSource
import com.example.ui.components.StatusPill
import com.example.ui.theme.ElegantActivePill
import com.example.ui.theme.ElegantIceBlue
import com.example.ui.theme.ElegantLavender
import com.example.ui.theme.ElegantLavenderOn
import com.example.ui.theme.ElegantLiveGreen
import com.example.ui.theme.SosaBackground
import com.example.ui.theme.SosaBorderSubtle
import com.example.ui.theme.SosaDarkBg
import com.example.ui.theme.SosaSurfaceDark
import com.example.ui.theme.SosaSurfaceInset
import com.example.ui.theme.SosaSurfaceVariant
import com.example.ui.theme.SosaTextMuted
import com.example.ui.theme.SosaTextPrimary
import com.example.ui.theme.SosaTextSecondary
import com.example.ui.viewmodel.SosaXaiViewModel
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchGroundingScreen(
    viewModel: SosaXaiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val searchState by viewModel.searchState.collectAsState()
    val reports by viewModel.searchReports.collectAsState()

    val suggestedQueries = listOf(
        "Latest Kubernetes 1.30 scheduling features and GPU dynamic partitioning",
        "Frontier AI agentic architectures and Gemini 3.5 multi-modal benchmarks",
        "Zero-trust mTLS 1.3 enterprise best practices in 2026",
        "Real-time distributed database consensus protocols and Raft optimizations"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SosaBackground)
            .testTag("search_grounding_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
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
                                    .background(ElegantIceBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TravelExplore,
                                    contentDescription = "Search Grounding",
                                    tint = ElegantIceBlue,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Google Search Grounding",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SosaTextPrimary
                                )
                                Text(
                                    text = "Model: gemini-3.5-flash (googleSearch tool)",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = SosaTextSecondary
                                )
                            }
                        }
                        StatusPill(
                            text = "Live Grounding",
                            containerColor = ElegantActivePill,
                            contentColor = ElegantLavenderOn
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Query up-to-date and accurate real-world intelligence verified against Google Search. Returns grounded synthesis with direct web citations and grounding metadata.",
                        fontSize = 13.sp,
                        color = SosaTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Search Input Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SosaSurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SosaBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Search Query",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = searchState.query,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_query_input"),
                        placeholder = {
                            Text(
                                "Enter technical or real-time query to ground via Google Search...",
                                color = SosaTextMuted,
                                fontSize = 13.sp
                            )
                        },
                        trailingIcon = {
                            if (searchState.isSearching) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = ElegantIceBlue,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                IconButton(
                                    onClick = { viewModel.executeSearchGrounding() },
                                    modifier = Modifier.testTag("execute_search_icon_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search",
                                        tint = ElegantIceBlue
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SosaTextPrimary,
                            unfocusedTextColor = SosaTextPrimary,
                            focusedContainerColor = SosaSurfaceInset,
                            unfocusedContainerColor = SosaSurfaceInset,
                            focusedBorderColor = ElegantIceBlue,
                            unfocusedBorderColor = SosaBorderSubtle
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Suggested Intelligence Queries:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SosaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        suggestedQueries.forEach { query ->
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.updateSearchQuery(query)
                                        viewModel.executeSearchGrounding()
                                    },
                                color = SosaSurfaceInset,
                                border = androidx.compose.foundation.BorderStroke(1.dp, SosaBorderSubtle)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Query",
                                        tint = ElegantIceBlue,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = query,
                                        fontSize = 11.sp,
                                        color = SosaTextSecondary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.executeSearchGrounding() },
                        enabled = !searchState.isSearching && searchState.query.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("execute_search_grounding_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElegantIceBlue,
                            disabledContainerColor = SosaSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (searchState.isSearching) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Grounding with Google Search...",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.TravelExplore,
                                contentDescription = "Search",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Execute Grounded Search (gemini-3.5-flash)",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Active Grounded Result Card
        if (searchState.latestReport != null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SosaSurfaceDark),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElegantLiveGreen.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(ElegantLiveGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Google Search Grounded Report",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantLiveGreen
                                )
                            }
                            Text(
                                text = "${searchState.latestReport?.latencyMs}ms",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SosaTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Search Queries Executed
                        if (searchState.latestReport?.searchQueries?.isNotEmpty() == true) {
                            Text(
                                text = "Search Queries Executed by Google Search Tool:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = SosaTextMuted
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                searchState.latestReport?.searchQueries?.forEach { q ->
                                    StatusPill(
                                        text = q,
                                        containerColor = SosaSurfaceInset,
                                        contentColor = ElegantIceBlue
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Grounded Answer
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = SosaSurfaceInset,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = searchState.latestReport?.answer ?: "",
                                fontSize = 13.sp,
                                color = SosaTextPrimary,
                                modifier = Modifier.padding(12.dp),
                                lineHeight = 19.sp
                            )
                        }

                        // Web Citations & Sources
                        if (searchState.latestReport?.sources?.isNotEmpty() == true) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Verified Web Sources & Citations (${searchState.latestReport?.sources?.size}):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SosaTextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            searchState.latestReport?.sources?.forEach { source ->
                                WebSourceCitationCard(source = source)
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                }
            }
        }

        // Historical Reports Header
        item {
            Text(
                text = "Saved Search Reports in Room Database (${reports.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SosaTextPrimary
            )
        }

        if (reports.isEmpty()) {
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
                            imageVector = Icons.Default.TravelExplore,
                            contentDescription = "No reports",
                            tint = SosaTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No search reports in Room database yet.",
                            fontSize = 13.sp,
                            color = SosaTextSecondary
                        )
                    }
                }
            }
        } else {
            items(reports, key = { it.id }) { report ->
                SavedSearchReportCard(
                    report = report,
                    onDelete = { viewModel.deleteSearchReport(report) }
                )
            }
        }
    }
}

@Composable
fun WebSourceCitationCard(source: WebCitationSource) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.uri))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Opening link: ${source.uri}", Toast.LENGTH_SHORT).show()
                }
            },
        color = SosaSurfaceInset,
        border = androidx.compose.foundation.BorderStroke(1.dp, SosaBorderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Web",
                    tint = ElegantIceBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = source.title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SosaTextPrimary,
                        maxLines = 1
                    )
                    Text(
                        text = source.uri,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = SosaTextSecondary,
                        maxLines = 1
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = "Open",
                tint = ElegantIceBlue,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SavedSearchReportCard(
    report: SearchReportEntity,
    onDelete: () -> Unit
) {
    val dateStr = remember(report.timestamp) {
        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(report.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_search_card_${report.id}"),
        colors = CardDefaults.cardColors(containerColor = SosaSurfaceDark),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, SosaBorderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusPill(
                        text = "Google Search",
                        containerColor = ElegantIceBlue.copy(alpha = 0.2f),
                        contentColor = ElegantIceBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$dateStr • ${report.latencyMs}ms",
                        fontSize = 11.sp,
                        color = SosaTextMuted
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = SosaTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Query: ${report.query}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SosaTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = report.answer,
                fontSize = 12.sp,
                color = SosaTextSecondary,
                lineHeight = 16.sp,
                maxLines = 3
            )
        }
    }
}
