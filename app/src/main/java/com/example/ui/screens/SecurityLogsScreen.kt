package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.components.CodeOutputBox
import com.example.ui.components.StatusPill
import com.example.ui.theme.SosaAmber
import com.example.ui.theme.SosaBorder
import com.example.ui.theme.SosaCyan
import com.example.ui.theme.SosaDarkBg
import com.example.ui.theme.SosaEmerald
import com.example.ui.theme.SosaRose
import com.example.ui.theme.SosaSurfaceDark
import com.example.ui.theme.SosaSurfaceVariant
import com.example.ui.theme.SosaTextMuted
import com.example.ui.theme.SosaTextPrimary
import com.example.ui.theme.SosaTextSecondary
import com.example.ui.theme.SosaViolet
import com.example.ui.viewmodel.SosaXaiViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SecurityLogsScreen(
    viewModel: SosaXaiViewModel,
    modifier: Modifier = Modifier
) {
    val logs by viewModel.telemetryLogs.collectAsState()
    val cryptoResult by viewModel.cryptoResult.collectAsState()

    var payloadToVerify by remember { mutableStateOf("{\"module\": \"grok-leo\", \"action\": \"audit\", \"nonce\": \"9481a8c\"}") }
    var secretKey by remember { mutableStateOf("sosa-xai-master-enterprise-secret-key-2026") }

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
                        text = "ENTERPRISE SECURITY & TELEMETRY",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaAmber,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Cryptographic Verification & Audit Logs",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaTextPrimary
                    )
                }

                IconButton(
                    onClick = { viewModel.clearTelemetry() },
                    modifier = Modifier.testTag("clear_logs_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear Logs",
                        tint = SosaTextMuted
                    )
                }
            }
        }

        // Cryptographic Verification Tool Box
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, SosaBorder, RoundedCornerShape(14.dp)),
                color = SosaSurfaceDark
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = SosaAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "HMAC-SHA256 SIGNATURE VERIFICATION",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SosaAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = payloadToVerify,
                        onValueChange = { payloadToVerify = it },
                        label = { Text("Payload String to Hash") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("crypto_payload_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SosaAmber,
                            unfocusedBorderColor = SosaBorder,
                            focusedTextColor = SosaTextPrimary,
                            unfocusedTextColor = SosaTextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = secretKey,
                        onValueChange = { secretKey = it },
                        label = { Text("Secret HMAC Key") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("crypto_secret_key_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SosaAmber,
                            unfocusedBorderColor = SosaBorder,
                            focusedTextColor = SosaTextPrimary,
                            unfocusedTextColor = SosaTextPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.runCryptoVerification(payloadToVerify, secretKey) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SosaAmber,
                            contentColor = SosaDarkBg
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("verify_crypto_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "VERIFY CRYPTOGRAPHIC HASH",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    cryptoResult?.let { res ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SosaDarkBg)
                                .border(1.dp, SosaAmber.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "STATUS: CRYPTO_VERIFIED",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SosaEmerald
                                )
                                Text(
                                    text = "${res.durationMs}ms",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = SosaCyan
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "SHA-256: ${res.sha256Hash}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = SosaTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "HMAC: ${res.hmacSignature}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = SosaAmber
                            )
                        }
                    }
                }
            }
        }

        // Live Telemetry Stream
        item {
            Text(
                text = "STREAMING AUDIT LOGS (${logs.size})",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SosaCyan,
                letterSpacing = 0.8.sp
            )
        }

        if (logs.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    color = SosaSurfaceDark
                ) {
                    Text(
                        text = "No audit log entries recorded.",
                        fontSize = 12.sp,
                        color = SosaTextMuted,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }
        } else {
            items(logs, key = { it.id }) { log ->
                val dateStr = remember(log.timestamp) {
                    SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(log.timestamp))
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, SosaBorder, RoundedCornerShape(10.dp)),
                    color = SosaSurfaceDark
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = dateStr,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = SosaTextMuted
                                )
                                Text(
                                    text = "•",
                                    fontSize = 10.sp,
                                    color = SosaTextMuted
                                )
                                Text(
                                    text = log.eventType,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SosaCyan
                                )
                            }
                            StatusPill(log.status)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = log.details,
                            fontSize = 12.sp,
                            color = SosaTextPrimary,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Source: ${log.sourceModule}",
                                fontSize = 10.sp,
                                color = SosaTextSecondary
                            )
                            Text(
                                text = "Latency: ${log.latencyMs}ms  |  Tokens: ${log.tokensUsed}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = SosaEmerald
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
