package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.*

@Composable
fun StatusPill(
    text: String,
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (status.uppercase()) {
        "ACTIVE", "RUNNING", "HEALTHY", "SUCCESS", "OPERATIONAL", "ONLINE", "VALID" -> Triple(
            ElegantLiveGreen.copy(alpha = 0.15f),
            ElegantLiveGreen,
            ElegantLiveGreen.copy(alpha = 0.35f)
        )
        "WARNING", "PENDING", "INITIALIZING", "DEGRADED" -> Triple(
            SosaAmber.copy(alpha = 0.15f),
            SosaAmber,
            SosaAmber.copy(alpha = 0.35f)
        )
        "ERROR", "STOPPED", "FAILED", "OFFLINE", "INVALID" -> Triple(
            SosaRose.copy(alpha = 0.15f),
            SosaRose,
            SosaRose.copy(alpha = 0.35f)
        )
        else -> Triple(
            ElegantLavender.copy(alpha = 0.15f),
            ElegantLavender,
            ElegantLavender.copy(alpha = 0.35f)
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag("status_pill_${status.lowercase()}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Text(
                text = text.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    status: String,
    modifier: Modifier = Modifier
) {
    StatusPill(text = text, status = status, modifier = modifier)
}

@Composable
fun SystemMetricBadge(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, SosaBorderSubtle, RoundedCornerShape(16.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        color = SosaSurfaceDark,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SosaTextMuted,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SosaTextPrimary,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun TechMetricCard(
    title: String,
    value: String,
    subValue: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    SystemMetricBadge(
        label = title,
        value = "$value ($subValue)",
        icon = icon,
        color = accentColor,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
fun CodeOutputBox(
    text: String,
    modifier: Modifier = Modifier,
    label: String? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SosaSurfaceInset)
            .border(1.dp, SosaBorderSubtle, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        if (label != null) {
            Text(
                text = label.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = ElegantLavender,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Text(
            text = text,
            fontSize = 12.sp,
            color = SosaTextSecondary,
            fontFamily = FontFamily.Monospace,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionButton: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SosaTextPrimary
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = SosaTextSecondary
                )
            }
        }
        if (actionButton != null) {
            actionButton()
        }
    }
}

