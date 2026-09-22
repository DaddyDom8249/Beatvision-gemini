package com.example.ui

import android.app.Application
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.BeatVisionAiService
import com.example.data.ai.GeminiBeatVisionAiService
import com.example.data.local.BeatVisionDatabase
import com.example.data.local.ProjectRepository
import com.example.data.model.DemoProjectData
import com.example.data.model.Project
import com.example.data.model.StoryboardScene
import com.example.ui.navigation.Screen
import com.example.ui.navigation.Stage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

class BeatVisionViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: ProjectRepository = ProjectRepository(
        BeatVisionDatabase.getDatabase(application).projectDao()
    ),
    private val aiService: BeatVisionAiService = GeminiBeatVisionAiService()
) : AndroidViewModel(application) {

    // Navigation state with backstack history
    private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
    val currentScreen: StateFlow<Screen> = MutableStateFlow<Screen>(Screen.Home).apply {
        viewModelScope.launch {
            _screenStack.collect { stack ->
                this@apply.value = stack.lastOrNull() ?: Screen.Home
            }
        }
    }

    // Active project state
    private val _activeProject = MutableStateFlow<Project?>(null)
    val activeProject: StateFlow<Project?> = _activeProject.asStateFlow()

    // Saved projects list from Room
    val savedProjects: StateFlow<List<Project>> = repository.getSavedProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Generation & UI status flags
    private val _isGeneratingWorld = MutableStateFlow(false)
    val isGeneratingWorld: StateFlow<Boolean> = _isGeneratingWorld.asStateFlow()

    private val _isRefiningWorld = MutableStateFlow(false)
    val isRefiningWorld: StateFlow<Boolean> = _isRefiningWorld.asStateFlow()

    private val _isGeneratingStoryboard = MutableStateFlow(false)
    val isGeneratingStoryboard: StateFlow<Boolean> = _isGeneratingStoryboard.asStateFlow()

    private val _isRegeneratingScene = MutableStateFlow(false)
    val isRegeneratingScene: StateFlow<Boolean> = _isRegeneratingScene.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Audio & Timeline state
    private var mediaPlayer: MediaPlayer? = null
    private val _isAudioPlaying = MutableStateFlow(false)
    val isAudioPlaying: StateFlow<Boolean> = _isAudioPlaying.asStateFlow()

    private val _audioPositionMs = MutableStateFlow(0L)
    val audioPositionMs: StateFlow<Long> = _audioPositionMs.asStateFlow()

    private val _audioDurationMs = MutableStateFlow(0L)
    val audioDurationMs: StateFlow<Long> = _audioDurationMs.asStateFlow()

    private var audioProgressJob: Job? = null

    // Cinematic Preview state
    private val _previewSceneIndex = MutableStateFlow(0)
    val previewSceneIndex: StateFlow<Int> = _previewSceneIndex.asStateFlow()

    private val _isPreviewPlaying = MutableStateFlow(true)
    val isPreviewPlaying: StateFlow<Boolean> = _isPreviewPlaying.asStateFlow()

    private var previewTimerJob: Job? = null

    // Compute active stage from current screen
    val currentStage: Stage
        get() = when (currentScreen.value) {
            is Screen.SongInput -> Stage.SONG
            is Screen.VisualWorld -> Stage.WORLD
            is Screen.Storyboard -> Stage.STORYBOARD
            is Screen.SceneDetail -> Stage.SCENES
            is Screen.CinematicPreview -> Stage.PREVIEW
            else -> Stage.SONG
        }

    init {
        // Automatically ensure demo project exists in database for seamless immediate access
        viewModelScope.launch {
            repository.loadOrInitDemo()
        }
    }

    // --- Navigation methods ---

    fun navigateTo(screen: Screen) {
        _errorMessage.value = null
        val currentList = _screenStack.value.toMutableList()
        // Prevent duplicate consecutive screens
        if (currentList.lastOrNull() != screen) {
            currentList.add(screen)
            _screenStack.value = currentList
        }
    }

    fun navigateBack(): Boolean {
        _errorMessage.value = null
        val currentList = _screenStack.value.toMutableList()
        if (currentList.size > 1) {
            currentList.removeAt(currentList.size - 1)
            _screenStack.value = currentList
            return true
        }
        return false
    }

    fun navigateToStage(stage: Stage) {
        val proj = _activeProject.value ?: return
        when (stage) {
            Stage.SONG -> navigateTo(Screen.SongInput)
            Stage.WORLD -> if (proj.worldReport != null) navigateTo(Screen.VisualWorld)
            Stage.STORYBOARD -> if (proj.storyboard.isNotEmpty()) navigateTo(Screen.Storyboard)
            Stage.SCENES -> if (proj.storyboard.isNotEmpty()) navigateTo(Screen.SceneDetail(0))
            Stage.PREVIEW -> if (proj.storyboard.isNotEmpty()) openCinematicPreview()
        }
    }

    fun openHome() {
        stopAudio()
        _screenStack.value = listOf(Screen.Home)
    }

    fun openProjectList() {
        navigateTo(Screen.ProjectList)
    }

    // --- Project Management ---

    fun startNewProject() {
        stopAudio()
        val newProj = Project(
            id = UUID.randomUUID().toString(),
            name = "",
            songTitle = "",
            artist = "",
            lyrics = "",
            creativeDirection = "",
            audioUri = null,
            audioFileName = null,
            audioDurationMs = 90000L,
            worldReport = null,
            isWorldApproved = false,
            storyboard = emptyList(),
            isDemo = false
        )
        _activeProject.value = newProj
        navigateTo(Screen.SongInput)
    }

    fun loadDemoProject() {
        stopAudio()
        viewModelScope.launch {
            val demo = repository.loadOrInitDemo()
            _activeProject.value = demo
            _audioDurationMs.value = demo.audioDurationMs
            navigateTo(Screen.VisualWorld)
        }
    }

    fun openSavedProject(id: String) {
        stopAudio()
        viewModelScope.launch {
            val proj = repository.getProject(id)
            if (proj != null) {
                _activeProject.value = proj
                _audioDurationMs.value = proj.audioDurationMs
                if (proj.storyboard.isNotEmpty()) {
                    navigateTo(Screen.Storyboard)
                } else if (proj.worldReport != null) {
                    navigateTo(Screen.VisualWorld)
                } else {
                    navigateTo(Screen.SongInput)
                }
            }
        }
    }

    fun deleteSavedProject(id: String) {
        viewModelScope.launch {
            repository.deleteProject(id)
            if (_activeProject.value?.id == id) {
                _activeProject.value = null
                openHome()
            }
        }
    }

    fun updateProjectDetails(
        name: String,
        songTitle: String,
        artist: String,
        lyrics: String,
        creativeDirection: String
    ) {
        val current = _activeProject.value ?: return
        val updated = current.copy(
            name = name.ifBlank { songTitle },
            songTitle = songTitle,
            artist = artist,
            lyrics = lyrics,
            creativeDirection = creativeDirection
        )
        _activeProject.value = updated
        viewModelScope.launch {
            repository.saveProject(updated)
        }
    }

    fun setAudioUri(uri: Uri?, fileName: String?, durationMs: Long = 0L) {
        val current = _activeProject.value ?: return
        val effectiveDuration = if (durationMs > 0) durationMs else 90000L
        val updated = current.copy(
            audioUri = uri?.toString(),
            audioFileName = fileName,
            audioDurationMs = effectiveDuration
        )
        _activeProject.value = updated
        _audioDurationMs.value = effectiveDuration
        viewModelScope.launch {
            repository.saveProject(updated)
        }
        initAudio(uri?.toString(), effectiveDuration)
    }

    // --- AI Engine Operations ---

    fun revealWorld() {
        val current = _activeProject.value ?: return
        if (current.songTitle.isBlank()) {
            _errorMessage.value = "Please enter a Song Title."
            return
        }

        _errorMessage.value = null
        _isGeneratingWorld.value = true

        viewModelScope.launch {
            val result = aiService.generateWorld(
                songTitle = current.songTitle,
                artist = current.artist,
                lyrics = current.lyrics,
                creativeDirection = current.creativeDirection
            )

            _isGeneratingWorld.value = false
            result.onSuccess { world ->
                val updated = current.copy(
                    worldReport = world,
                    isWorldApproved = false
                )
                _activeProject.value = updated
                repository.saveProject(updated)
                navigateTo(Screen.VisualWorld)
            }.onFailure { err ->
                _errorMessage.value = err.localizedMessage ?: "Failed to generate visual world. Your project is safe. Please check your API key or network."
            }
        }
    }

    fun refineWorld(feedback: String) {
        val current = _activeProject.value ?: return
        val currentWorld = current.worldReport ?: return
        if (feedback.isBlank()) return

        _errorMessage.value = null
        _isRefiningWorld.value = true

        viewModelScope.launch {
            val result = aiService.refineWorld(
                currentWorld = currentWorld,
                feedback = feedback,
                songTitle = current.songTitle,
                lyrics = current.lyrics
            )

            _isRefiningWorld.value = false
            result.onSuccess { refinedWorld ->
                val updated = current.copy(worldReport = refinedWorld)
                _activeProject.value = updated
                repository.saveProject(updated)
            }.onFailure { err ->
                _errorMessage.value = "World refinement failed: ${err.localizedMessage}. Original world preserved."
            }
        }
    }

    fun approveWorld() {
        val current = _activeProject.value ?: return
        val world = current.worldReport ?: return

        val updated = current.copy(isWorldApproved = true)
        _activeProject.value = updated
        viewModelScope.launch {
            repository.saveProject(updated)
        }

        // If storyboard already exists, navigate directly; otherwise generate storyboard
        if (updated.storyboard.isNotEmpty()) {
            navigateTo(Screen.Storyboard)
        } else {
            generateStoryboard()
        }
    }

    fun generateStoryboard() {
        val current = _activeProject.value ?: return
        val world = current.worldReport ?: return

        _errorMessage.value = null
        _isGeneratingStoryboard.value = true

        viewModelScope.launch {
            val result = aiService.generateStoryboard(
                world = world,
                songTitle = current.songTitle,
                lyrics = current.lyrics
            )

            _isGeneratingStoryboard.value = false
            result.onSuccess { scenes ->
                val updated = current.copy(storyboard = scenes)
                _activeProject.value = updated
                repository.saveProject(updated)
                navigateTo(Screen.Storyboard)
            }.onFailure { err ->
                // Fallback: If AI call failed, provide a structured template based on approved world
                _errorMessage.value = "Storyboard generation notice: ${err.localizedMessage}."
            }
        }
    }

    fun openSceneDetail(index: Int) {
        navigateTo(Screen.SceneDetail(index))
    }

    fun regenerateScene(sceneIndex: Int) {
        val current = _activeProject.value ?: return
        val scenes = current.storyboard.toMutableList()
        if (sceneIndex !in scenes.indices) return

        val scene = scenes[sceneIndex]
        val world = current.worldReport ?: return

        _errorMessage.value = null
        _isRegeneratingScene.value = true

        viewModelScope.launch {
            val conceptResult = aiService.generateSceneConcept(scene, world)
            val motionResult = aiService.generateMotionPlan(scene, world)

            _isRegeneratingScene.value = false
            if (conceptResult.isSuccess && motionResult.isSuccess) {
                val updatedMotion = motionResult.getOrThrow()
                val updatedScene = scene.copy(motionPlan = updatedMotion)
                scenes[sceneIndex] = updatedScene
                val updatedProj = current.copy(storyboard = scenes)
                _activeProject.value = updatedProj
                repository.saveProject(updatedProj)
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // --- Audio Player Controls ---

    private fun initAudio(uriString: String?, durationMs: Long) {
        stopAudio()
        _audioDurationMs.value = durationMs
        _audioPositionMs.value = 0L

        if (uriString != null) {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(getApplication(), Uri.parse(uriString))
                    prepareAsync()
                    setOnPreparedListener { mp ->
                        _audioDurationMs.value = mp.duration.toLong()
                    }
                    setOnCompletionListener {
                        _isAudioPlaying.value = false
                        _audioPositionMs.value = 0L
                    }
                }
            } catch (_: Exception) {
                mediaPlayer = null
            }
        }
    }

    fun toggleAudio() {
        if (_isAudioPlaying.value) {
            pauseAudio()
        } else {
            playAudio()
        }
    }

    fun playAudio() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.start()
                _isAudioPlaying.value = true
                startAudioProgressTicker()
            } catch (_: Exception) {
                simulateAudioTicker()
            }
        } else {
            // Simulated audio timeline playback for demo or audio-less projects
            simulateAudioTicker()
        }
    }

    fun pauseAudio() {
        mediaPlayer?.pause()
        _isAudioPlaying.value = false
        audioProgressJob?.cancel()
    }

    fun stopAudio() {
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {}
        mediaPlayer = null
        _isAudioPlaying.value = false
        _audioPositionMs.value = 0L
        audioProgressJob?.cancel()
        previewTimerJob?.cancel()
    }

    fun seekAudio(positionMs: Long) {
        val target = positionMs.coerceIn(0L, _audioDurationMs.value)
        _audioPositionMs.value = target
        try {
            mediaPlayer?.seekTo(target.toInt())
        } catch (_: Exception) {}
    }

    private fun startAudioProgressTicker() {
        audioProgressJob?.cancel()
        audioProgressJob = viewModelScope.launch {
            while (isActive && _isAudioPlaying.value) {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        _audioPositionMs.value = mp.currentPosition.toLong()
                    }
                }
                delay(200)
            }
        }
    }

    private fun simulateAudioTicker() {
        _isAudioPlaying.value = true
        audioProgressJob?.cancel()
        audioProgressJob = viewModelScope.launch {
            val total = if (_audioDurationMs.value > 0) _audioDurationMs.value else 90000L
            while (isActive && _isAudioPlaying.value) {
                delay(250)
                val next = _audioPositionMs.value + 250
                if (next >= total) {
                    _audioPositionMs.value = 0L
                    _isAudioPlaying.value = false
                    break
                } else {
                    _audioPositionMs.value = next
                }
            }
        }
    }

    // --- Cinematic Preview Controller ---

    fun openCinematicPreview() {
        _previewSceneIndex.value = 0
        _isPreviewPlaying.value = true
        navigateTo(Screen.CinematicPreview)
        startCinematicPreviewLoop()
        playAudio()
    }

    fun togglePreviewPlayPause() {
        val next = !_isPreviewPlaying.value
        _isPreviewPlaying.value = next
        if (next) {
            startCinematicPreviewLoop()
            playAudio()
        } else {
            previewTimerJob?.cancel()
            pauseAudio()
        }
    }

    fun nextPreviewScene() {
        val scenesCount = _activeProject.value?.storyboard?.size ?: 1
        _previewSceneIndex.value = (_previewSceneIndex.value + 1) % scenesCount
    }

    fun prevPreviewScene() {
        val scenesCount = _activeProject.value?.storyboard?.size ?: 1
        val current = _previewSceneIndex.value
        _previewSceneIndex.value = if (current - 1 < 0) scenesCount - 1 else current - 1
    }

    fun selectPreviewScene(index: Int) {
        val scenesCount = _activeProject.value?.storyboard?.size ?: 1
        if (index in 0 until scenesCount) {
            _previewSceneIndex.value = index
        }
    }

    private fun startCinematicPreviewLoop() {
        previewTimerJob?.cancel()
        previewTimerJob = viewModelScope.launch {
            while (isActive && _isPreviewPlaying.value) {
                // Each scene displays for 6 seconds in the animated preview loop
                delay(6000)
                val scenesCount = _activeProject.value?.storyboard?.size ?: 6
                if (scenesCount > 0) {
                    _previewSceneIndex.value = (_previewSceneIndex.value + 1) % scenesCount
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopAudio()
    }
}
