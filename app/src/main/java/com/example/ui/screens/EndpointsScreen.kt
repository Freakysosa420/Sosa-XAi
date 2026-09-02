package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.local.entity.ApiEndpointEntity
import com.example.ui.components.CodeOutputBox
import com.example.ui.components.StatusPill
import com.example.ui.theme.*
import com.example.ui.viewmodel.SosaXaiViewModel

@Composable
fun EndpointsScreen(
    viewModel: SosaXaiViewModel,
    modifier: Modifier = Modifier
) {
    val endpoints by viewModel.endpoints.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SosaDarkBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "API INTEGRATION GATEWAY",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElegantLavender,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Enterprise Routes & Telemetry Ping",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaTextPrimary
                    )
                }

                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.testTag("add_endpoint_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Endpoint",
                        tint = ElegantLavender
                    )
                }
            }
        }

        if (endpoints.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    color = SosaSurfaceDark
                ) {
                    Text(
                        text = "No active API endpoints registered. Tap + to register a route.",
                        fontSize = 12.sp,
                        color = SosaTextMuted,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(endpoints, key = { it.id }) { endpoint ->
                EndpointCard(
                    endpoint = endpoint,
                    onTestPing = { viewModel.testEndpoint(endpoint) },
                    onDelete = { viewModel.deleteEndpoint(endpoint) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var path by remember { mutableStateOf("/v1/xai/service") }
        var method by remember { mutableStateOf("POST") }
        var authType by remember { mutableStateOf("Bearer JWT") }
        var rateLimit by remember { mutableStateOf("20,000 req/min") }
        var headers by remember { mutableStateOf("{\"Content-Type\": \"application/json\"}") }
        var payload by remember { mutableStateOf("{\"action\": \"sync\"}") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Register Enterprise API Route",
                    fontWeight = FontWeight.Bold,
                    color = ElegantLavender
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Route Name") },
                        placeholder = { Text("e.g. Ingest Webhook") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = path,
                        onValueChange = { path = it },
                        label = { Text("Endpoint Path") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = method,
                            onValueChange = { method = it },
                            label = { Text("HTTP Method") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = authType,
                            onValueChange = { authType = it },
                            label = { Text("Auth Protocol") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    OutlinedTextField(
                        value = rateLimit,
                        onValueChange = { rateLimit = it },
                        label = { Text("Rate Limit") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank() && path.isNotBlank()) {
                            viewModel.addEndpoint(
                                ApiEndpointEntity(
                                    name = name,
                                    path = path,
                                    method = method.uppercase(),
                                    authType = authType,
                                    status = "ACTIVE",
                                    rateLimit = rateLimit,
                                    headersJson = headers,
                                    samplePayload = payload
                                )
                            )
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElegantLavender, contentColor = ElegantLavenderOn),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Register Endpoint", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = SosaTextSecondary)
                }
            },
            containerColor = SosaSurfaceDark,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun EndpointCard(
    endpoint: ApiEndpointEntity,
    onTestPing: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val methodColor = when (endpoint.method.uppercase()) {
        "GET" -> ElegantLiveGreen
        "POST" -> ElegantLavender
        "PUT" -> SosaAmber
        "DELETE" -> SosaRose
        else -> ElegantIceBlue
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SosaBorderSubtle, RoundedCornerShape(16.dp)),
        color = SosaSurfaceDark
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
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
                            .clip(RoundedCornerShape(6.dp))
                            .background(methodColor.copy(alpha = 0.2f))
                            .border(1.dp, methodColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = endpoint.method,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = methodColor
                        )
                    }

                    Column {
                        Text(
                            text = endpoint.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SosaTextPrimary
                        )
                        Text(
                            text = endpoint.path,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = SosaTextSecondary
                        )
                    }
                }

                StatusPill(endpoint.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Auth: ${endpoint.authType}",
                        fontSize = 10.sp,
                        color = SosaTextMuted
                    )
                    Text(
                        text = "Limit: ${endpoint.rateLimit}",
                        fontSize = 10.sp,
                        color = SosaTextMuted
                    )
                }

                if (endpoint.latencyMs > 0) {
                    Text(
                        text = "${endpoint.latencyMs}ms",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaEmerald
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onTestPing,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SosaViolet.copy(alpha = 0.2f),
                            contentColor = SosaViolet
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp).testTag("ping_${endpoint.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Test Ping", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp).testTag("delete_endpoint_${endpoint.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = SosaTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand details",
                        tint = SosaTextSecondary
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "Headers Matrix:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CodeOutputBox(codeText = endpoint.headersJson, title = "HEADERS")

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Sample Ingest Payload:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CodeOutputBox(codeText = endpoint.samplePayload, title = "BODY")
                }
            }
        }
    }
}
