package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ContainerConfigEntity
import com.example.ui.components.CodeOutputBox
import com.example.ui.components.StatusPill
import com.example.ui.theme.SosaBorder
import com.example.ui.theme.SosaCyan
import com.example.ui.theme.SosaDarkBg
import com.example.ui.theme.SosaEmerald
import com.example.ui.theme.SosaSurfaceDark
import com.example.ui.theme.SosaSurfaceVariant
import com.example.ui.theme.SosaTextMuted
import com.example.ui.theme.SosaTextPrimary
import com.example.ui.theme.SosaTextSecondary
import com.example.ui.viewmodel.SosaXaiViewModel

@Composable
fun ContainersScreen(
    viewModel: SosaXaiViewModel,
    modifier: Modifier = Modifier
) {
    val containers by viewModel.containers.collectAsState()
    var showDeployDialog by remember { mutableStateOf(false) }

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
                        text = "CONTAINER & CLUSTER ORCHESTRATION",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaEmerald,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Kubernetes & H100 GPU Pod Clusters",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaTextPrimary
                    )
                }

                IconButton(
                    onClick = { showDeployDialog = true },
                    modifier = Modifier.testTag("deploy_container_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Deploy Container",
                        tint = SosaEmerald
                    )
                }
            }
        }

        if (containers.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    color = SosaSurfaceDark
                ) {
                    Text(
                        text = "No container clusters deployed. Tap + to spin up a deployment.",
                        fontSize = 12.sp,
                        color = SosaTextMuted,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(containers, key = { it.id }) { container ->
                ContainerCard(
                    container = container,
                    onScale = { newReplicas -> viewModel.scaleContainer(container, newReplicas) },
                    onDelete = { viewModel.deleteContainer(container) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showDeployDialog) {
        var name by remember { mutableStateOf("") }
        var runtime by remember { mutableStateOf("Kubernetes") }
        var image by remember { mutableStateOf("registry.sosa.xai/engine/grok-leo:v4") }
        var replicas by remember { mutableStateOf("4") }
        var cpu by remember { mutableStateOf("16 vCPU") }
        var mem by remember { mutableStateOf("64 GB") }
        var gpu by remember { mutableStateOf("4x NVIDIA H100 SXM5") }

        AlertDialog(
            onDismissRequest = { showDeployDialog = false },
            title = {
                Text(
                    text = "Deploy Microservice Container",
                    fontWeight = FontWeight.Bold,
                    color = SosaEmerald
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Service Name") },
                        placeholder = { Text("e.g. sosa-grok-worker") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = image,
                        onValueChange = { image = it },
                        label = { Text("Docker / OCI Image") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = runtime,
                            onValueChange = { runtime = it },
                            label = { Text("Runtime") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = replicas,
                            onValueChange = { replicas = it },
                            label = { Text("Replicas") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedTextField(
                        value = gpu,
                        onValueChange = { gpu = it },
                        label = { Text("GPU Acceleration") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            viewModel.addContainer(
                                ContainerConfigEntity(
                                    name = name,
                                    runtime = runtime,
                                    image = image,
                                    replicas = replicas.toIntOrNull() ?: 1,
                                    cpuAllocation = cpu,
                                    memoryAllocation = mem,
                                    gpuSupport = gpu,
                                    portBindings = "8080:8080",
                                    envVars = "SOSA_ENV=production\nPROTOCOL=GROK_LEO",
                                    manifestYaml = "apiVersion: apps/v1\nkind: Deployment\nmetadata:\n  name: $name",
                                    status = "RUNNING"
                                )
                            )
                            showDeployDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SosaEmerald, contentColor = SosaDarkBg)
                ) {
                    Text("Deploy Container", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeployDialog = false }) {
                    Text("Cancel", color = SosaTextSecondary)
                }
            },
            containerColor = SosaSurfaceDark
        )
    }
}

@Composable
fun ContainerCard(
    container: ContainerConfigEntity,
    onScale: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, SosaBorder, RoundedCornerShape(12.dp)),
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
                            .background(SosaEmerald.copy(alpha = 0.2f))
                            .border(1.dp, SosaEmerald, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = container.runtime.uppercase(),
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SosaEmerald
                        )
                    }

                    Text(
                        text = container.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaTextPrimary
                    )
                }

                StatusPill(container.status)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Image: ${container.image}",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = SosaTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GPU: ${container.gpuSupport}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SosaCyan
                    )
                    Text(
                        text = "Compute: ${container.cpuAllocation} • ${container.memoryAllocation}",
                        fontSize = 10.sp,
                        color = SosaTextMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(SosaSurfaceVariant)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { if (container.replicas > 0) onScale(container.replicas - 1) },
                        modifier = Modifier.size(24.dp).testTag("scale_down_${container.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Scale down",
                            tint = SosaTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Text(
                        text = "${container.replicas} Pods",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaEmerald,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )

                    IconButton(
                        onClick = { onScale(container.replicas + 1) },
                        modifier = Modifier.size(24.dp).testTag("scale_up_${container.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Scale up",
                            tint = SosaTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(30.dp).testTag("delete_container_${container.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = SosaTextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Expand Manifest",
                        tint = SosaTextSecondary
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        text = "Deployment Manifest Spec:",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaTextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CodeOutputBox(codeText = container.manifestYaml, title = "YAML MANIFEST")
                }
            }
        }
    }
}
