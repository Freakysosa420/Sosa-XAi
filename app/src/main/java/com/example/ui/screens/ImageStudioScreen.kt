package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.entity.GeneratedImageEntity
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
fun ImageStudioScreen(
    viewModel: SosaXaiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageState by viewModel.imageState.collectAsState()
    val images by viewModel.images.collectAsState()

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                viewModel.setImageInputSource(bitmap, uri.lastPathSegment ?: "Selected Image")
                Toast.makeText(context, "Input image loaded for editing", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Could not load image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SosaBackground)
            .testTag("image_studio_screen"),
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
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Image Studio",
                                    tint = ElegantLavender,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Create & Edit Images",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SosaTextPrimary
                                )
                                Text(
                                    text = "Model: gemini-3.1-flash-image-preview",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = SosaTextSecondary
                                )
                            }
                        }
                        StatusPill(
                            text = "${images.size} Images",
                            containerColor = ElegantActivePill,
                            contentColor = ElegantLavenderOn
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Generate high-fidelity AI imagery from text prompts, or upload existing photos and apply precise text-based visual modifications using gemini-3.1-flash-image-preview.",
                        fontSize = 13.sp,
                        color = SosaTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Generator Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SosaSurfaceDark),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SosaBorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Image Prompt & Parameters",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SosaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = imageState.prompt,
                        onValueChange = { viewModel.updateImagePrompt(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("image_prompt_input"),
                        placeholder = {
                            Text(
                                "Describe the subject, artistic style, lighting, composition...",
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

                    // Input Image for Editing
                    if (imageState.inputImageBitmap != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = SosaSurfaceInset,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElegantIceBlue.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Mode",
                                        tint = ElegantIceBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Image-to-Image Edit Mode",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ElegantIceBlue
                                        )
                                        Text(
                                            text = imageState.inputImageName ?: "Custom Photo Attached",
                                            fontSize = 11.sp,
                                            color = SosaTextSecondary
                                        )
                                    }
                                }
                                IconButton(
                                    onClick = { viewModel.setImageInputSource(null, null) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Remove",
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
                                .testTag("upload_image_to_edit_button"),
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
                                text = "Attach Photo to Edit (Image-to-Image)",
                                color = ElegantIceBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Aspect Ratio Selector
                    Text(
                        text = "Aspect Ratio:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SosaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("1:1", "16:9", "9:16", "4:3", "3:4").forEach { ratio ->
                            val isSelected = imageState.aspectRatio == ratio
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setImageAspectRatio(ratio) },
                                label = { Text(ratio, fontSize = 11.sp) },
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
                        text = "Image Resolution:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SosaTextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("512px", "1K", "2K", "4K").forEach { res ->
                            val isSelected = imageState.resolution == res
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setImageResolution(res) },
                                label = { Text(res, fontSize = 11.sp) },
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

                    Button(
                        onClick = { viewModel.generateOrEditImage() },
                        enabled = !imageState.isGenerating && imageState.prompt.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("generate_image_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElegantLavender,
                            disabledContainerColor = SosaSurfaceVariant
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (imageState.isGenerating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Synthesizing Image...",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Generate",
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (imageState.inputImageBitmap != null) "Apply Edit with gemini-3.1-flash-image-preview" else "Generate Image with gemini-3.1-flash-image-preview",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Active Output Preview Card
        if (imageState.latestResult != null) {
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
                                    text = "Generated Image Output",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElegantLiveGreen
                                )
                            }
                            Text(
                                text = "${imageState.latestResult?.latencyMs}ms",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SosaTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Render Coil Image with graceful fallback
                        AsyncImage(
                            model = imageState.latestResult?.imageUrl,
                            contentDescription = "Generated Image",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(SosaSurfaceInset),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = imageState.latestResult?.prompt ?: "",
                            fontSize = 12.sp,
                            color = SosaTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Gallery of Saved Images Header
        item {
            Text(
                text = "Room Database Image Gallery (${images.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SosaTextPrimary
            )
        }

        if (images.isEmpty()) {
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
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "No images",
                            tint = SosaTextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No generated images saved in Room yet.",
                            fontSize = 13.sp,
                            color = SosaTextSecondary
                        )
                    }
                }
            }
        } else {
            items(images, key = { it.id }) { img ->
                SavedImageCard(
                    image = img,
                    onDelete = { viewModel.deleteGeneratedImage(img) }
                )
            }
        }
    }
}

@Composable
fun SavedImageCard(
    image: GeneratedImageEntity,
    onDelete: () -> Unit
) {
    val dateStr = remember(image.timestamp) {
        SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(image.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("saved_image_card_${image.id}"),
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
                        text = "${image.aspectRatio} • ${image.resolution}",
                        containerColor = ElegantLavender.copy(alpha = 0.2f),
                        contentColor = ElegantLavender
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateStr,
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

            AsyncImage(
                model = image.outputImageUrl,
                contentDescription = image.prompt,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SosaSurfaceInset),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = image.prompt,
                fontSize = 12.sp,
                color = SosaTextPrimary,
                maxLines = 2,
                lineHeight = 16.sp
            )
        }
    }
}
