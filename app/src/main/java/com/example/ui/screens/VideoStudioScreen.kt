package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.VeoVideoEntity
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
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VideoStudioScreen(
    viewModel: SosaXaiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val videoState by viewModel.videoState.collectAsState()
    val videos by viewModel.videos.collectAsState()

    // Photo picker for Veo image-to-video animation
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel.setVideoSourceImage(bitmap, "Selected Photo (${uri.lastPathSegment ?: "image"})")
                Toast.makeText(context, "Photo loaded for Veo animation", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Could not load photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SosaBackground)
            .testTag("video_studio_screen"),
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
                                    .background(ElegantLavender.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Movie,
                                    contentDescription = "Veo 3",
                                    tint = ElegantLavender,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Veo 3 Video Studio",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SosaTextPrimary
                                )
                                Text(
                                    text = "Model: veo-3.1-fast-generate-preview",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = SosaTextSecondary
                                )
                            }
                        }
                        StatusPill(
                            text = "${videos.size} Videos",
                            containerColor = ElegantActivePill,
                            contentColor = ElegantLavenderOn
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Generate cinematic videos from text prompts or animate your uploaded photos into 16:9 landscape and 9:16 portrait video streams.",
                        fontSize = 13.sp,
                        color = SosaTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Generator Studio Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SosaSurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SosaBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Prompt & Generation Parameters",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Text Prompt Field
                    OutlinedTextField(
                        value = videoState.prompt,
                        onValueChange = { viewModel.updateVideoPrompt(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("video_prompt_input"),
                        placeholder = {
                            Text(
                                "Describe the motion, camera movements, lighting, and scene details...",
                                color = SosaTextMuted,
                                fontSize = 13.sp
                            )
                        },
                        minLines = 3,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SosaTextPrimary,
                            unfocusedTextColor = SosaTextPrimary,
                            focusedContainerColor = SosaSurfaceInset,
                            unfocusedContainerColor = SosaSurfaceInset,
                            focusedBorderColor = ElegantLavender,
                            unfocusedBorderColor = SosaBorderSubtle
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Image upload / animation slot
                    if (videoState.sourceImageBitmap != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = SosaSurfaceInset,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantLavender.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Source Photo",
                                        tint = ElegantIceBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Animate Photo Mode",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ElegantIceBlue
                                        )
                                        Text(
                                            text = videoState.sourceImageName ?: "Custom Image Attached",
                                            fontSize = 11.sp,
                                            color = SosaTextSecondary
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.setVideoSourceImage(null, null) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove photo",
                                        tint = SosaRose,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    } else {
                        Button(
                            onClick = { photoPickerLauncher.launch("image/*") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("upload_photo_to_video_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = SosaSurfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddPhotoAlternate,
                                contentDescription = "Upload",
                                tint = ElegantIceBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Upload Photo to Animate (Image-to-Video)",
                                color = ElegantIceBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Aspect Ratio Selector (Mandatory 16:9 or 9:16)
                    Text(
                        text = "Aspect Ratio (Mandatory 16:9 or 9:16):",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SosaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf(
                            Pair("16:9", "16:9 Landscape"),
                            Pair("9:16", "9:16 Portrait")
                        ).forEach { (ratio, label) ->
                            val isSelected = videoState.selectedAspectRatio == ratio
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setVideoAspectRatio(ratio) },
                                label = { Text(label, fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElegantLavender,
                                    selectedLabelColor = Color.Black,
                                    containerColor = SosaSurfaceInset,
                                    labelColor = SosaTextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Resolution selector
                    Text(
                        text = "Target Output Resolution:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SosaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("1080p", "720p", "4K").forEach { res ->
                            val isSelected = videoState.selectedResolution == res
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setVideoResolution(res) },
                                label = { Text(res, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElegantIceBlue,
                                    selectedLabelColor = Color.Black,
                                    containerColor = SosaSurfaceInset,
                                    labelColor = SosaTextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Generate Button
                    Button(
                        onClick = { viewModel.generateVeoVideo() },
                        enabled = !videoState.isGenerating && videoState.prompt.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("generate_veo_video_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElegantLavender,
                            disabledContainerColor = SosaSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (videoState.isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Veo 3 Synthesizing Video...",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Generate",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (videoState.sourceImageBitmap != null) "Animate Photo with Veo 3" else "Generate Video with Veo 3",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Active / Recent Video Preview
        if (videoState.latestResult != null) {
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
                                    text = "Veo 3 Video Generated Successfully",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantLiveGreen
                                )
                            }
                            Text(
                                text = "${videoState.latestResult?.latencyMs}ms",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SosaTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Video Player Simulation Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(if (videoState.latestResult?.aspectRatio == "9:16") 9f / 16f else 16f / 9f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))
                                    )
                                )
                                .border(1.dp, SosaBorderSubtle, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(ElegantLavender),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Play",
                                        tint = Color.Black,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Veo 3 Stream Ready (${videoState.latestResult?.resolution})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SosaTextPrimary
                                )
                                Text(
                                    text = "Ratio: ${videoState.latestResult?.aspectRatio} | Model: ${videoState.latestResult?.modelUsed}",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = SosaTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = videoState.latestResult?.prompt ?: "",
                            fontSize = 12.sp,
                            color = SosaTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Saved Video Gallery Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Room Database Video Gallery (${videos.size})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SosaTextPrimary
                )
            }
        }

        if (videos.isEmpty()) {
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
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "No videos",
                            tint = SosaTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No Veo videos generated yet.",
                            fontSize = 13.sp,
                            color = SosaTextSecondary
                        )
                        Text(
                            text = "Enter a prompt or upload an image above to create your first video.",
                            fontSize = 11.sp,
                            color = SosaTextMuted
                        )
                    }
                }
            }
        } else {
            items(videos, key = { it.id }) { video ->
                VeoVideoItemCard(
                    video = video,
                    onDelete = { viewModel.deleteVideo(video) }
                )
            }
        }
    }
}

@Composable
fun VeoVideoItemCard(
    video: VeoVideoEntity,
    onDelete: () -> Unit
) {
    val dateStr = remember(video.timestamp) {
        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(video.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("veo_video_card_${video.id}"),
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
                        text = video.aspectRatio,
                        containerColor = if (video.aspectRatio == "16:9") ElegantLavender.copy(alpha = 0.2f) else ElegantIceBlue.copy(alpha = 0.2f),
                        contentColor = if (video.aspectRatio == "16:9") ElegantLavender else ElegantIceBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = video.resolution,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = SosaTextSecondary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• $dateStr",
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
                text = video.prompt,
                fontSize = 13.sp,
                color = SosaTextPrimary,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )

            if (video.sourceImageUrl != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Source: ${video.sourceImageUrl}",
                    fontSize = 11.sp,
                    color = ElegantIceBlue
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Mini Video preview container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SosaSurfaceInset),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = ElegantLavender,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Preview ${video.aspectRatio} Stream (${video.durationSeconds}s)",
                        fontSize = 12.sp,
                        color = SosaTextSecondary
                    )
                }
            }
        }
    }
}
