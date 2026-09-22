package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BeatVisionViewModel
import com.example.ui.components.ChipTag
import com.example.ui.components.CinematicTopBar
import com.example.ui.components.GlowingButton
import com.example.ui.components.OutlinedCinematicButton
import com.example.ui.components.SceneVisualCard
import com.example.ui.components.SectionCard
import com.example.ui.components.StageProgressBar
import com.example.ui.navigation.Stage
import com.example.ui.theme.AmberHalogen
import com.example.ui.theme.BorderAccent
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CinematicBlack
import com.example.ui.theme.CinematicCard
import com.example.ui.theme.CinematicCardElevated
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun SceneDetailScreen(
    sceneIndex: Int,
    viewModel: BeatVisionViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.activeProject.collectAsState()
    val scenes = project?.storyboard ?: emptyList()
    val isRegenerating by viewModel.isRegeneratingScene.collectAsState()

    val currentScene = scenes.getOrNull(sceneIndex)

    Scaffold(
        topBar = {
            Column {
                CinematicTopBar(
                    title = currentScene?.let { "Scene ${it.sceneNumber}: ${it.sceneTitle}" } ?: "Scene Detail",
                    subtitle = currentScene?.storyRole ?: "",
                    isDemo = project?.isDemo == true,
                    onBack = { viewModel.navigateBack() },
                    actions = {
                        IconButton(
                            onClick = { viewModel.regenerateScene(sceneIndex) },
                            enabled = !isRegenerating,
                            modifier = Modifier.testTag("regenerate_scene_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Regenerate Scene",
                                tint = ElectricCyan
                            )
                        }
                    }
                )
                StageProgressBar(
                    currentStage = Stage.SCENES,
                    onStageClick = { stage -> viewModel.navigateToStage(stage) }
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CinematicBlack)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Scene button
                    OutlinedCinematicButton(
                        text = "PREV",
                        onClick = {
                            if (sceneIndex > 0) {
                                viewModel.openSceneDetail(sceneIndex - 1)
                            }
                        },
                        enabled = sceneIndex > 0,
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        modifier = Modifier.weight(0.8f),
                        testTag = "prev_scene_button"
                    )

                    // Launch Preview
                    GlowingButton(
                        text = "PREVIEW",
                        onClick = { viewModel.openCinematicPreview() },
                        icon = Icons.Default.PlayArrow,
                        modifier = Modifier.weight(1.4f),
                        testTag = "launch_preview_from_scene_button"
                    )

                    // Next Scene button
                    OutlinedCinematicButton(
                        text = "NEXT",
                        onClick = {
                            if (sceneIndex < scenes.size - 1) {
                                viewModel.openSceneDetail(sceneIndex + 1)
                            }
                        },
                        enabled = sceneIndex < scenes.size - 1,
                        icon = Icons.AutoMirrored.Filled.ArrowForward,
                        modifier = Modifier.weight(0.8f),
                        testTag = "next_scene_button"
                    )
                }
            }
        },
        containerColor = CinematicBlack,
        modifier = modifier.testTag("scene_detail_screen")
    ) { innerPadding ->
        if (currentScene == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Scene not found.", color = TextSecondary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // 1. SCENE CONCEPT VISUAL CARD
                item {
                    SceneVisualCard(
                        scene = currentScene,
                        showDetailsOverlay = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp)
                    )
                }

                // 2. STORY & LYRICS
                item {
                    SectionCard(
                        title = "STORY PURPOSE",
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Text(
                            text = currentScene.storyPurpose,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )

                        if (currentScene.lyricsSection.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CinematicCardElevated)
                                    .border(1.dp, AmberHalogen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "LYRICS IN THIS SCENE",
                                        color = AmberHalogen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"${currentScene.lyricsSection}\"",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. PERFORMANCE DIRECTION
                item {
                    SectionCard(
                        title = "PERFORMANCE DIRECTION",
                        icon = Icons.Default.Face,
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Text(
                            text = currentScene.performanceDirection,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        SpecRow("Character Actions", currentScene.characterActions)
                        SpecRow("Interactions", currentScene.characterInteraction)
                    }
                }

                // 4. MOTION PLAN (CRITICAL SPECIFICATION)
                item {
                    SectionCard(
                        title = "MOTION PLAN",
                        icon = Icons.AutoMirrored.Filled.DirectionsRun,
                        badge = currentScene.motionPlan.timing,
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Text(
                            text = currentScene.motionPlan.sceneSummary,
                            color = ElectricCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        MotionSubSection(
                            title = "CHARACTER MOTION",
                            items = currentScene.motionPlan.characterMotion
                        )

                        MotionSubSection(
                            title = "FACIAL PERFORMANCE",
                            items = currentScene.motionPlan.facialPerformance
                        )

                        MotionSubSection(
                            title = "INTERACTIONS",
                            items = currentScene.motionPlan.interaction
                        )

                        MotionSubSection(
                            title = "ENVIRONMENT MOTION",
                            items = currentScene.motionPlan.environmentMotion
                        )

                        MotionSubSection(
                            title = "CAMERA MOTION",
                            items = currentScene.motionPlan.cameraMotion
                        )
                    }
                }

                // 5. CAMERA & LIGHTING
                item {
                    SectionCard(
                        title = "CAMERA & LIGHTING",
                        icon = Icons.Default.CameraAlt,
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        SpecRow("Camera Direction", currentScene.cameraDirection)
                        SpecRow("Lighting Setup", currentScene.lighting)
                        SpecRow("Atmosphere", currentScene.atmosphere)
                        SpecRow("Environment", currentScene.environment)
                    }
                }

                // 6. VISUAL PROMPT & CONTINUITY
                item {
                    SectionCard(
                        title = "PROMPT & CONTINUITY",
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Text(
                            text = "Visual Prompt for Generation:",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(CinematicCardElevated)
                                .padding(10.dp)
                        ) {
                            Text(
                                text = currentScene.visualPrompt,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 16.sp
                            )
                        }

                        if (currentScene.continuityRequirements.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Continuity Requirements:",
                                color = AmberHalogen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentScene.continuityRequirements,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun MotionSubSection(title: String, items: List<String>) {
    if (items.isNotEmpty()) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(ElectricCyan)
                            .padding(top = 5.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
