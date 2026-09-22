package com.example.ui.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StoryboardScene
import com.example.ui.BeatVisionViewModel
import com.example.ui.components.ChipTag
import com.example.ui.components.CinematicTopBar
import com.example.ui.components.GlowingButton
import com.example.ui.components.SceneVisualCard
import com.example.ui.components.StageProgressBar
import com.example.ui.navigation.Stage
import com.example.ui.theme.AmberHalogen
import com.example.ui.theme.BorderAccent
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.CinematicBlack
import com.example.ui.theme.CinematicCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoryboardScreen(
    viewModel: BeatVisionViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.activeProject.collectAsState()
    val scenes = project?.storyboard ?: emptyList()

    Scaffold(
        topBar = {
            Column {
                CinematicTopBar(
                    title = "Storyboard",
                    subtitle = "${scenes.size} Directed Scenes • ${project?.songTitle ?: ""}",
                    isDemo = project?.isDemo == true,
                    onBack = { viewModel.navigateBack() }
                )
                StageProgressBar(
                    currentStage = Stage.STORYBOARD,
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
                GlowingButton(
                    text = "OPEN CINEMATIC PREVIEW",
                    onClick = { viewModel.openCinematicPreview() },
                    icon = Icons.Default.PlayArrow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    testTag = "open_cinematic_preview_button"
                )
            }
        },
        containerColor = CinematicBlack,
        modifier = modifier.testTag("storyboard_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            item {
                Text(
                    text = "A complete 6-scene cinematic progression mapped to the emotional rhythm of the song. Tap any scene to view concept specifications and motion plans.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            itemsIndexed(scenes, key = { _, s -> s.sceneNumber }) { index, scene ->
                StoryboardSceneCard(
                    scene = scene,
                    onClick = { viewModel.openSceneDetail(index) },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StoryboardSceneCard(
    scene: StoryboardScene,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CinematicCard)
            .border(1.dp, BorderSubtle, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("storyboard_scene_card_${scene.sceneNumber}")
    ) {
        Column {
            // Header: Scene number, Title, Role chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "SCENE ${scene.sceneNumber}",
                        color = ElectricCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = scene.sceneTitle,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when (scene.storyRole.lowercase()) {
                                "emotional peak", "transformation / climax" -> AmberHalogen.copy(alpha = 0.2f)
                                else -> ElectricCyan.copy(alpha = 0.15f)
                            }
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = scene.storyRole.uppercase(),
                        color = when (scene.storyRole.lowercase()) {
                            "emotional peak", "transformation / climax" -> AmberHalogen
                            else -> ElectricCyan
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Procedural Canvas Scene Concept representation
            SceneVisualCard(
                scene = scene,
                showDetailsOverlay = false,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Narrative Story Purpose
            Text(
                text = scene.storyPurpose,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Characters & Environment row
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                scene.characters.forEach { charName ->
                    ChipTag(label = charName, color = ElectricCyan)
                }
                ChipTag(label = scene.environment, color = AmberHalogen)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Motion summary banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CinematicBlack)
                    .border(1.dp, BorderAccent, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                        contentDescription = null,
                        tint = AmberHalogen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = scene.motionPlan.sceneSummary,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View detail",
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
