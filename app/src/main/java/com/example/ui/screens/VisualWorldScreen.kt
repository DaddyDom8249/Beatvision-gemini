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
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.Rule
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterProfile
import com.example.data.model.EnvironmentProfile
import com.example.ui.BeatVisionViewModel
import com.example.ui.components.CinematicErrorBanner
import com.example.ui.components.CinematicTopBar
import com.example.ui.components.GlowingButton
import com.example.ui.components.OutlinedCinematicButton
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
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun VisualWorldScreen(
    viewModel: BeatVisionViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.activeProject.collectAsState()
    val world = project?.worldReport
    val isRefining by viewModel.isRefiningWorld.collectAsState()
    val isGeneratingStoryboard by viewModel.isGeneratingStoryboard.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var showRefineDialog by remember { mutableStateOf(false) }
    var refineFeedback by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Column {
                CinematicTopBar(
                    title = "Visual World Report",
                    subtitle = project?.songTitle ?: "Cinematic Concept",
                    isDemo = project?.isDemo == true,
                    onBack = { viewModel.navigateBack() }
                )
                StageProgressBar(
                    currentStage = Stage.WORLD,
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Refine My World
                    OutlinedCinematicButton(
                        text = "Refine World",
                        onClick = { showRefineDialog = true },
                        icon = Icons.Default.Edit,
                        accentColor = AmberHalogen,
                        modifier = Modifier.weight(1f),
                        testTag = "refine_world_button"
                    )

                    // That's My World (Approve & proceed to Storyboard)
                    GlowingButton(
                        text = if (project?.storyboard?.isNotEmpty() == true) "View Storyboard" else "That's My World",
                        onClick = { viewModel.approveWorld() },
                        icon = Icons.Default.Check,
                        isLoading = isGeneratingStoryboard,
                        modifier = Modifier.weight(1.3f),
                        testTag = "approve_world_button"
                    )
                }
            }
        },
        containerColor = CinematicBlack,
        modifier = modifier.testTag("visual_world_screen")
    ) { innerPadding ->
        if (world == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No visual world has been revealed yet.\nReturn to Song screen to generate.",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                if (errorMessage != null) {
                    item {
                        CinematicErrorBanner(
                            errorMessage = errorMessage ?: "",
                            onDismiss = { viewModel.clearError() },
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }

                // 1. THE WORLD
                item {
                    SectionCard(
                        title = "THE WORLD",
                        icon = Icons.Default.Public,
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Text(
                            text = world.theWorld,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }

                // 2. EMOTIONAL CORE
                item {
                    SectionCard(
                        title = "EMOTIONAL CORE",
                        icon = Icons.Default.AutoAwesome,
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        Text(
                            text = world.emotionalCore,
                            color = ElectricCyan,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // 3. STORY ARC
                item {
                    SectionCard(
                        title = "STORY ARC",
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        StoryArcItem(label = "BEGINNING", text = world.story.beginning)
                        Spacer(modifier = Modifier.height(8.dp))
                        StoryArcItem(label = "MIDDLE", text = world.story.middle)
                        Spacer(modifier = Modifier.height(8.dp))
                        StoryArcItem(label = "ENDING", text = world.story.ending)
                    }
                }

                // 4. CHARACTERS
                item {
                    SectionCard(
                        title = "CHARACTERS (${world.characters.size})",
                        icon = Icons.Default.Face,
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        world.characters.forEachIndexed { idx, char ->
                            CharacterProfileItem(char = char)
                            if (idx < world.characters.size - 1) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }

                // 5. ENVIRONMENTS
                item {
                    SectionCard(
                        title = "ENVIRONMENTS (${world.environments.size})",
                        icon = Icons.Default.LocationOn,
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        world.environments.forEachIndexed { idx, env ->
                            EnvironmentProfileItem(env = env)
                            if (idx < world.environments.size - 1) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }
                    }
                }

                // 6. VISUAL LANGUAGE
                item {
                    SectionCard(
                        title = "VISUAL LANGUAGE",
                        icon = Icons.Default.Palette,
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        SpecRow("Colors", world.visualLanguage.colors)
                        SpecRow("Lighting", world.visualLanguage.lighting)
                        SpecRow("Textures", world.visualLanguage.textures)
                        SpecRow("Production Design", world.visualLanguage.productionDesign)
                        SpecRow("Atmosphere", world.visualLanguage.atmosphere)
                    }
                }

                // 7. CINEMATOGRAPHY
                item {
                    SectionCard(
                        title = "CINEMATOGRAPHY",
                        icon = Icons.Default.CameraAlt,
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        SpecRow("Framing", world.cinematography.framing)
                        SpecRow("Camera Movement", world.cinematography.cameraMovement)
                        SpecRow("Lens Style", world.cinematography.lensStyle)
                        SpecRow("Shot Types", world.cinematography.shotTypes)
                        SpecRow("Behavior", world.cinematography.cameraBehavior)
                    }
                }

                // 8. MOTION LANGUAGE
                item {
                    SectionCard(
                        title = "MOTION LANGUAGE",
                        icon = Icons.AutoMirrored.Filled.DirectionsRun,
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        SpecRow("Walking", world.motionLanguage.walking)
                        SpecRow("Dancing", world.motionLanguage.dancing)
                        SpecRow("Singing", world.motionLanguage.singing)
                        SpecRow("Gestures", world.motionLanguage.gestures)
                        SpecRow("Interaction", world.motionLanguage.interaction)
                        SpecRow("Environment", world.motionLanguage.environmentalMovement)
                        SpecRow("Camera", world.motionLanguage.cameraMovement)
                    }
                }

                // 9. VISUAL MOTIFS
                if (world.visualMotifs.isNotEmpty()) {
                    item {
                        SectionCard(
                            title = "VISUAL MOTIFS",
                            modifier = Modifier.padding(bottom = 14.dp)
                        ) {
                            world.visualMotifs.forEach { motif ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(AmberHalogen)
                                            .padding(top = 6.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = motif,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // 10. PERFORMANCE DIRECTION
                item {
                    SectionCard(
                        title = "PERFORMANCE DIRECTION",
                        modifier = Modifier.padding(bottom = 14.dp)
                    ) {
                        SpecRow("Singing", world.performanceDirection.singing)
                        SpecRow("Moving", world.performanceDirection.moving)
                        SpecRow("Interacting", world.performanceDirection.interacting)
                        SpecRow("Reacting", world.performanceDirection.reacting)
                        SpecRow("Emotion", world.performanceDirection.expressingEmotion)
                    }
                }

                // 11. CONTINUITY RULES
                if (world.continuityRules.isNotEmpty()) {
                    item {
                        SectionCard(
                            title = "CONTINUITY RULES",
                            icon = Icons.AutoMirrored.Filled.Rule,
                            badge = "Preserved",
                            modifier = Modifier.padding(bottom = 14.dp)
                        ) {
                            world.continuityRules.forEachIndexed { i, rule ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = "${i + 1}.",
                                        color = ElectricCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(20.dp)
                                    )
                                    Text(
                                        text = rule,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Refinement Dialog
    if (showRefineDialog) {
        AlertDialog(
            onDismissRequest = { showRefineDialog = false },
            title = {
                Text(
                    text = "Refine Visual World",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Provide director notes to guide the AI. What would you like to adjust or intensify? (e.g. \"Make it darker, add more neon rain, increase character tension\")",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = refineFeedback,
                        onValueChange = { refineFeedback = it },
                        placeholder = { Text("Enter your creative feedback...") },
                        minLines = 3,
                        colors = outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("refine_feedback_input")
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val fb = refineFeedback
                        showRefineDialog = false
                        refineFeedback = ""
                        viewModel.refineWorld(fb)
                    },
                    enabled = refineFeedback.isNotBlank()
                ) {
                    Text("APPLY REFINEMENT", color = ElectricCyan, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRefineDialog = false }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            containerColor = CinematicCardElevated,
            modifier = Modifier.testTag("refine_world_dialog")
        )
    }
}

@Composable
fun StoryArcItem(label: String, text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CinematicCardElevated)
            .padding(10.dp)
    ) {
        Column {
            Text(
                text = label,
                color = ElectricCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = text,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun CharacterProfileItem(char: CharacterProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CinematicCardElevated)
            .border(1.dp, BorderAccent, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = char.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = char.role,
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            SpecRow("Appearance", char.appearance)
            SpecRow("Clothing", char.clothing)
            SpecRow("Personality", char.personality)
            SpecRow("Emotion", char.emotionalState)
            SpecRow("Performance", char.performanceBehavior)
        }
    }
}

@Composable
fun EnvironmentProfileItem(env: EnvironmentProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CinematicCardElevated)
            .border(1.dp, BorderAccent, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = env.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = env.timeOfDay,
                    color = AmberHalogen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            SpecRow("Visual", env.appearance)
            SpecRow("Architecture", env.architecture)
            SpecRow("Lighting", env.lighting)
            SpecRow("Atmosphere", env.atmosphere)
            SpecRow("Key Objects", env.importantObjects)
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            text = "$label: ",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(110.dp)
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 12.sp,
            lineHeight = 16.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
