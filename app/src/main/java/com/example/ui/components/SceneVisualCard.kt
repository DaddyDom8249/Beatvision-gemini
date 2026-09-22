package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoryboardScene
import com.example.ui.theme.AmberHalogen
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CinematicBlack
import com.example.ui.theme.CinematicCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun SceneVisualCard(
    scene: StoryboardScene,
    modifier: Modifier = Modifier,
    isCinematicPreviewMode: Boolean = false,
    showDetailsOverlay: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cinematic_motion")

    // Slow cinematic pan & zoom animation (Ken Burns effect)
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isCinematicPreviewMode) 1.12f else 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val panAnim by infiniteTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pan"
    )

    val lightPulseAnim by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(if (isCinematicPreviewMode) 0.dp else 14.dp))
            .background(CinematicBlack)
            .border(
                width = if (isCinematicPreviewMode) 0.dp else 1.dp,
                color = if (isCinematicPreviewMode) Color.Transparent else BorderSubtle,
                shape = RoundedCornerShape(14.dp)
            )
            .testTag("scene_visual_card_${scene.sceneNumber}")
    ) {
        // Procedural Cinematic Canvas rendering
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val centerX = w / 2f + panAnim
            val centerY = h / 2f

            // Dynamic color selection based on scene role
            val primaryTint = when (scene.sceneNumber) {
                1 -> ElectricCyan
                2 -> ElectricCyan.copy(alpha = 0.8f)
                3 -> NeonViolet
                4 -> AmberHalogen
                5 -> AmberHalogen
                else -> Color(0xFF00E676)
            }

            val secondaryTint = when (scene.sceneNumber) {
                1 -> Color(0xFF0F1E36)
                2 -> Color(0xFF1F1E38)
                3 -> Color(0xFF28183E)
                4 -> Color(0xFF33200B)
                5 -> Color(0xFF281E12)
                else -> Color(0xFF0F2624)
            }

            // Background gradient with atmospheric depth
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        secondaryTint.copy(alpha = lightPulseAnim),
                        Color(0xFF07090E),
                        CinematicBlack
                    ),
                    center = Offset(centerX, centerY * 0.8f),
                    radius = w * scaleAnim * 0.9f
                ),
                size = size
            )

            // Volumetric light beam / spotlight cone
            val conePath = Path().apply {
                moveTo(centerX, 0f)
                lineTo(centerX - w * 0.45f * scaleAnim, h)
                lineTo(centerX + w * 0.45f * scaleAnim, h)
                close()
            }
            drawPath(
                path = conePath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryTint.copy(alpha = 0.35f * lightPulseAnim),
                        primaryTint.copy(alpha = 0.08f),
                        Color.Transparent
                    )
                )
            )

            // Anamorphic horizontal lens flare streak
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        primaryTint.copy(alpha = 0.15f),
                        primaryTint.copy(alpha = 0.6f * lightPulseAnim),
                        Color.White.copy(alpha = 0.7f * lightPulseAnim),
                        primaryTint.copy(alpha = 0.6f * lightPulseAnim),
                        primaryTint.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    startX = centerX - w * 0.6f,
                    endX = centerX + w * 0.6f
                ),
                start = Offset(centerX - w * 0.6f, centerY * 0.85f),
                end = Offset(centerX + w * 0.6f, centerY * 0.85f),
                strokeWidth = 3.dp.toPx()
            )

            // Horizon & ground reflection plane
            val groundY = h * 0.72f
            drawLine(
                color = primaryTint.copy(alpha = 0.25f),
                start = Offset(0f, groundY),
                end = Offset(w, groundY),
                strokeWidth = 1.dp.toPx()
            )

            // Atmospheric rain / particle streaks
            for (i in 0..24) {
                val seedX = ((i * 47) % w.toInt()).toFloat()
                val seedY = ((i * 31 + (panAnim * 2)) % (groundY.toInt())).toFloat()
                val length = 18.dp.toPx()
                drawLine(
                    color = primaryTint.copy(alpha = 0.2f),
                    start = Offset(seedX, seedY),
                    end = Offset(seedX - 3.dp.toPx(), seedY + length),
                    strokeWidth = 1.2.dp.toPx()
                )
            }

            // Cinematic composition lines (rule of thirds subtle guides)
            drawLine(
                color = Color.White.copy(alpha = 0.04f),
                start = Offset(w / 3f, 0f),
                end = Offset(w / 3f, h),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = Color.White.copy(alpha = 0.04f),
                start = Offset(2 * w / 3f, 0f),
                end = Offset(2 * w / 3f, h),
                strokeWidth = 1.dp.toPx()
            )

            // Film frame corner crosshairs
            val crossSize = 10.dp.toPx()
            val inset = 12.dp.toPx()
            // Top-left
            drawLine(Color.White.copy(alpha = 0.2f), Offset(inset, inset), Offset(inset + crossSize, inset), 1.dp.toPx())
            drawLine(Color.White.copy(alpha = 0.2f), Offset(inset, inset), Offset(inset, inset + crossSize), 1.dp.toPx())
            // Top-right
            drawLine(Color.White.copy(alpha = 0.2f), Offset(w - inset, inset), Offset(w - inset - crossSize, inset), 1.dp.toPx())
            drawLine(Color.White.copy(alpha = 0.2f), Offset(w - inset, inset), Offset(w - inset, inset + crossSize), 1.dp.toPx())
            // Bottom-left
            drawLine(Color.White.copy(alpha = 0.2f), Offset(inset, h - inset), Offset(inset + crossSize, h - inset), 1.dp.toPx())
            drawLine(Color.White.copy(alpha = 0.2f), Offset(inset, h - inset), Offset(inset, h - inset - crossSize), 1.dp.toPx())
            // Bottom-right
            drawLine(Color.White.copy(alpha = 0.2f), Offset(w - inset, h - inset), Offset(w - inset - crossSize, h - inset), 1.dp.toPx())
            drawLine(Color.White.copy(alpha = 0.2f), Offset(w - inset, h - inset), Offset(w - inset, h - inset - crossSize), 1.dp.toPx())

            // Vignette effect around edges
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)),
                    center = Offset(w / 2f, h / 2f),
                    radius = w * 0.7f
                ),
                size = size
            )
        }

        // Top Header Overlay: "SCENE CONCEPT" label and Scene number badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mandatory label per instructions: "SCENE CONCEPT"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CinematicBlack.copy(alpha = 0.75f))
                    .border(1.dp, ElectricCyan.copy(alpha = 0.8f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(ElectricCyan)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "SCENE CONCEPT",
                        color = ElectricCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Scene Number & Role
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(CinematicCard.copy(alpha = 0.85f))
                    .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "SCENE ${scene.sceneNumber} • ${scene.storyRole.uppercase()}",
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Bottom Details Overlay: Title, Location, and Camera Framing
        if (showDetailsOverlay) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, CinematicBlack.copy(alpha = 0.92f))
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Column {
                    Text(
                        text = scene.sceneTitle,
                        color = TextPrimary,
                        fontSize = if (isCinematicPreviewMode) 16.sp else 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = AmberHalogen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${scene.environment} • ${scene.cameraDirection}",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
