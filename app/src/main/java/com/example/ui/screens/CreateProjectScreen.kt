package com.example.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.BeatVisionViewModel
import com.example.ui.components.AudioTimelineBar
import com.example.ui.components.ChipTag
import com.example.ui.components.CinematicErrorBanner
import com.example.ui.components.CinematicTopBar
import com.example.ui.components.GlowingButton
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    viewModel: BeatVisionViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val project by viewModel.activeProject.collectAsState()
    val isGenerating by viewModel.isGeneratingWorld.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val isPlaying by viewModel.isAudioPlaying.collectAsState()
    val audioPosMs by viewModel.audioPositionMs.collectAsState()
    val audioDurationMs by viewModel.audioDurationMs.collectAsState()

    val focusManager = LocalFocusManager.current
    var projectName by remember(project?.id) { mutableStateOf(project?.name ?: "") }
    var songTitle by remember(project?.id) { mutableStateOf(project?.songTitle ?: "") }
    var artist by remember(project?.id) { mutableStateOf(project?.artist ?: "") }
    var lyrics by remember(project?.id) { mutableStateOf(project?.lyrics ?: "") }
    var creativeDirection by remember(project?.id) { mutableStateOf(project?.creativeDirection ?: "") }

    val creativePresets = listOf(
        "Dark and emotional",
        "Realistic cyberpunk",
        "Romantic but dangerous",
        "Industrial and haunting",
        "Surreal and dreamlike"
    )

    // Audio picker launcher
    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            var fileName = "audio_track.mp3"
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIndex >= 0) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            } catch (_: Exception) {}
            viewModel.setAudioUri(uri, fileName)
        }
    }

    Scaffold(
        topBar = {
            Column {
                CinematicTopBar(
                    title = "Song & Vision",
                    subtitle = "Define your music and creative direction",
                    onBack = {
                        focusManager.clearFocus()
                        viewModel.navigateBack()
                    }
                )
                StageProgressBar(
                    currentStage = Stage.SONG,
                    onStageClick = { stage ->
                        focusManager.clearFocus()
                        viewModel.navigateToStage(stage)
                    }
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
                    text = "REVEAL MY WORLD",
                    onClick = {
                        focusManager.clearFocus()
                        viewModel.updateProjectDetails(
                            name = projectName,
                            songTitle = songTitle,
                            artist = artist,
                            lyrics = lyrics,
                            creativeDirection = creativeDirection
                        )
                        viewModel.revealWorld()
                    },
                    icon = Icons.Default.AutoAwesome,
                    isLoading = isGenerating,
                    enabled = songTitle.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    testTag = "reveal_my_world_button"
                )
            }
        },
        containerColor = CinematicBlack,
        modifier = modifier.testTag("create_project_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { focusManager.clearFocus() })
                },
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Error banner if any
            if (errorMessage != null) {
                item {
                    CinematicErrorBanner(
                        errorMessage = errorMessage ?: "",
                        onRetry = { viewModel.revealWorld() },
                        onDismiss = { viewModel.clearError() },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }

            // Project & Track Identity
            item {
                SectionCard(
                    title = "TRACK IDENTITY",
                    badge = "Required",
                    modifier = Modifier.padding(bottom = 14.dp)
                ) {
                    OutlinedTextField(
                        value = projectName,
                        onValueChange = {
                            projectName = it
                            viewModel.updateProjectDetails(it, songTitle, artist, lyrics, creativeDirection)
                        },
                        label = { Text("Project Name") },
                        placeholder = { Text("e.g. Drain Rack Halo") },
                        singleLine = true,
                        colors = outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                            .testTag("project_name_input")
                    )

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = songTitle,
                            onValueChange = {
                                songTitle = it
                                viewModel.updateProjectDetails(projectName, it, artist, lyrics, creativeDirection)
                            },
                            label = { Text("Song Title *") },
                            placeholder = { Text("Song title") },
                            singleLine = true,
                            colors = outlinedTextFieldColors(),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("song_title_input")
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        OutlinedTextField(
                            value = artist,
                            onValueChange = {
                                artist = it
                                viewModel.updateProjectDetails(projectName, songTitle, it, lyrics, creativeDirection)
                            },
                            label = { Text("Artist") },
                            placeholder = { Text("Artist name") },
                            singleLine = true,
                            colors = outlinedTextFieldColors(),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("artist_input")
                        )
                    }
                }
            }

            // Lyrics
            item {
                SectionCard(
                    title = "LYRICS",
                    modifier = Modifier.padding(bottom = 14.dp)
                ) {
                    Text(
                        text = "Paste the song lyrics below. BeatVision analyzes stanza flow and emotional peaks to map your 6 storyboard scenes.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    OutlinedTextField(
                        value = lyrics,
                        onValueChange = {
                            lyrics = it
                            viewModel.updateProjectDetails(projectName, songTitle, artist, it, creativeDirection)
                        },
                        label = { Text("Song Lyrics") },
                        placeholder = { Text("Paste verses, chorus, and bridge here...") },
                        minLines = 5,
                        maxLines = 12,
                        colors = outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("lyrics_input")
                    )
                }
            }

            // Creative Direction
            item {
                SectionCard(
                    title = "CREATIVE DIRECTION",
                    modifier = Modifier.padding(bottom = 14.dp)
                ) {
                    Text(
                        text = "What do you want this song to look and feel like?",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Quick suggestion chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        creativePresets.forEach { preset ->
                            val isSelected = creativeDirection.contains(preset, ignoreCase = true)
                            ChipTag(
                                label = preset,
                                color = ElectricCyan,
                                isSelected = isSelected,
                                onClick = {
                                    focusManager.clearFocus()
                                    creativeDirection = if (creativeDirection.isBlank()) {
                                        preset
                                    } else if (!isSelected) {
                                        "$creativeDirection, $preset"
                                    } else {
                                        creativeDirection
                                    }
                                    viewModel.updateProjectDetails(projectName, songTitle, artist, lyrics, creativeDirection)
                                }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = creativeDirection,
                        onValueChange = {
                            creativeDirection = it
                            viewModel.updateProjectDetails(projectName, songTitle, artist, lyrics, it)
                        },
                        label = { Text("Visual Style & Tone Direction") },
                        placeholder = { Text("e.g. Dark industrial cyberpunk music video, wet asphalt, high emotional tension...") },
                        minLines = 3,
                        maxLines = 6,
                        colors = outlinedTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("creative_direction_input")
                    )
                }
            }

            // Audio File Upload & Timeline
            item {
                SectionCard(
                    title = "SONG AUDIO FILE (OPTIONAL)",
                    modifier = Modifier.padding(bottom = 14.dp)
                ) {
                    Text(
                        text = "Select an audio track from device storage (MP3, WAV, M4A, AAC, FLAC) to synchronize scene transitions and play during the Cinematic Preview.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    if (project?.audioFileName != null) {
                        AudioTimelineBar(
                            fileName = project?.audioFileName,
                            isPlaying = isPlaying,
                            currentPosMs = audioPosMs,
                            durationMs = if (audioDurationMs > 0) audioDurationMs else 90000L,
                            onPlayPause = { viewModel.toggleAudio() },
                            onSeek = { viewModel.seekAudio(it) },
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(CinematicCardElevated)
                            .border(1.dp, BorderAccent, RoundedCornerShape(10.dp))
                            .clickable { audioPickerLauncher.launch("audio/*") }
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (project?.audioFileName != null) Icons.Default.CheckCircle else Icons.Default.UploadFile,
                                contentDescription = null,
                                tint = if (project?.audioFileName != null) AmberHalogen else ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (project?.audioFileName != null) "Change Audio: ${project?.audioFileName}" else "Select Audio File from Device",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Audio remains local on your Android device. BeatVision never uploads your raw audio to external cloud servers.",
                        color = TextTertiary,
                        fontSize = 11.sp
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun outlinedTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ElectricCyan,
    unfocusedBorderColor = BorderAccent,
    focusedLabelColor = ElectricCyan,
    unfocusedLabelColor = TextSecondary,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    cursorColor = ElectricCyan,
    focusedContainerColor = CinematicCardElevated,
    unfocusedContainerColor = CinematicCard
)
