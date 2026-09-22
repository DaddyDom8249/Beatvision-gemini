package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BeatVisionViewModel
import com.example.ui.components.SceneVisualCard
import com.example.ui.components.StageProgressBar
import com.example.ui.components.formatTime
import com.example.ui.navigation.Stage
import com.example.ui.theme.AmberHalogen
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CinematicBlack
import com.example.ui.theme.CinematicCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun CinematicPreviewScreen(
    viewModel: BeatVisionViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.activeProject.collectAsState()
    val scenes = project?.storyboard ?: emptyList()
    val currentSceneIndex by viewModel.previewSceneIndex.collectAsState()
    val isPlaying by viewModel.isPreviewPlaying.collectAsState()

    val isAudioPlaying by viewModel.isAudioPlaying.collectAsState()
    val audioPosMs by viewModel.audioPositionMs.collectAsState()
    val audioDurationMs by viewModel.audioDurationMs.collectAsState()

    var showControlsOverlay by remember { mutableStateOf(true) }

    val currentScene = scenes.getOrNull(currentSceneIndex)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CinematicBlack)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                showControlsOverlay = !showControlsOverlay
            }
            .testTag("cinematic_preview_screen")
    ) {
        // Main Visual Canvas with Ken Burns slow pan and zoom
        if (currentScene != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                SceneVisualCard(
                    scene = currentScene,
                    isCinematicPreviewMode = true,
                    showDetailsOverlay = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Overlay Controls & Stage Navigation
        AnimatedVisibility(
            visible = showControlsOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                CinematicBlack.copy(alpha = 0.85f),
                                Color.Transparent,
                                CinematicBlack.copy(alpha = 0.92f)
                            )
                        )
                    )
            ) {
                // Top App Bar in preview
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("close_preview_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close preview",
                            tint = TextPrimary
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "CINEMATIC PREVIEW",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = project?.songTitle ?: "BeatVision Studio",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AmberHalogen.copy(alpha = 0.2f))
                            .border(1.dp, AmberHalogen.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "PREVIEW",
                            color = AmberHalogen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Stage Progress Bar (allows jumping back)
                StageProgressBar(
                    currentStage = Stage.PREVIEW,
                    onStageClick = { stage -> viewModel.navigateToStage(stage) }
                )

                Spacer(modifier = Modifier.weight(1f))

                // Current Scene Info Card
                if (currentScene != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CinematicCard.copy(alpha = 0.9f))
                            .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "SCENE ${currentScene.sceneNumber} OF ${scenes.size} • ${currentScene.storyRole.uppercase()}",
                                    color = ElectricCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                Text(
                                    text = currentScene.environment,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = currentScene.sceneTitle,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = currentScene.storyPurpose,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                maxLines = 2
                            )
                        }
                    }
                }

                // Audio Timeline & Progress Scrubber
                val effectiveDuration = if (audioDurationMs > 0) audioDurationMs else 90000L
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTime(audioPosMs),
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        Text(
                            text = formatTime(effectiveDuration),
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }

                    val frac = audioPosMs.toFloat() / effectiveDuration.toFloat()
                    Slider(
                        value = frac.coerceIn(0f, 1f),
                        onValueChange = { newFrac ->
                            viewModel.seekAudio((newFrac * effectiveDuration).toLong())
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricCyan,
                            activeTrackColor = ElectricCyan,
                            inactiveTrackColor = BorderSubtle
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("preview_timeline_slider")
                    )
                }

                // Playback Control Buttons & Scene Indicators
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Scene
                    IconButton(
                        onClick = { viewModel.prevPreviewScene() },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("preview_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous scene",
                            tint = TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Scene Step Indicators (1 to 6)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        scenes.forEachIndexed { i, _ ->
                            Box(
                                modifier = Modifier
                                    .size(if (i == currentSceneIndex) 10.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (i == currentSceneIndex) ElectricCyan else TextTertiary.copy(alpha = 0.4f))
                                    .clickable { viewModel.selectPreviewScene(i) }
                            )
                        }
                    }

                    // Play/Pause
                    IconButton(
                        onClick = { viewModel.togglePreviewPlayPause() },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan)
                            .testTag("preview_play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = CinematicBlack,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // Next Scene
                    IconButton(
                        onClick = { viewModel.nextPreviewScene() },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("preview_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next scene",
                            tint = TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
