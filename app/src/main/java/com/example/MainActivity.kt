package com.example

import android.os.Bundle
import android.view.ActionMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BeatVisionViewModel
import com.example.ui.navigation.Screen
import com.example.ui.screens.CinematicPreviewScreen
import com.example.ui.screens.CreateProjectScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SceneDetailScreen
import com.example.ui.screens.StoryboardScreen
import com.example.ui.screens.VisualWorldScreen
import com.example.ui.theme.CinematicBlack
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private var currentFloatingActionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BeatVisionApp()
            }
        }
    }

    override fun onWindowStartingActionMode(callback: ActionMode.Callback?, type: Int): ActionMode? {
        if (type == ActionMode.TYPE_FLOATING) {
            return try {
                val mode = super.onWindowStartingActionMode(callback, type)
                currentFloatingActionMode = mode
                mode
            } catch (_: Exception) {
                null
            }
        }
        return super.onWindowStartingActionMode(callback, type)
    }

    override fun onActionModeFinished(mode: ActionMode?) {
        if (mode == currentFloatingActionMode) {
            currentFloatingActionMode = null
        }
        try {
            super.onActionModeFinished(mode)
        } catch (_: Exception) {
            // Guard against unexpected DecorView floating action mode teardown
        }
    }
}

@Composable
fun BeatVisionApp(
    viewModel: BeatVisionViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val focusManager = LocalFocusManager.current

    // Android back navigation handling
    BackHandler(enabled = currentScreen !is Screen.Home) {
        focusManager.clearFocus()
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = CinematicBlack,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CinematicBlack)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> HomeScreen(viewModel = viewModel)
                is Screen.SongInput -> CreateProjectScreen(viewModel = viewModel)
                is Screen.VisualWorld -> VisualWorldScreen(viewModel = viewModel)
                is Screen.Storyboard -> StoryboardScreen(viewModel = viewModel)
                is Screen.SceneDetail -> SceneDetailScreen(
                    sceneIndex = screen.sceneIndex,
                    viewModel = viewModel
                )
                is Screen.CinematicPreview -> CinematicPreviewScreen(viewModel = viewModel)
                is Screen.ProjectList -> HomeScreen(viewModel = viewModel)
            }
        }
    }
}
