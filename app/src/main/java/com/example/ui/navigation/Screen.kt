package com.example.ui.navigation

enum class Stage(val displayName: String, val index: Int) {
    SONG("Song", 0),
    WORLD("World", 1),
    STORYBOARD("Storyboard", 2),
    SCENES("Scenes", 3),
    PREVIEW("Preview", 4)
}

sealed class Screen {
    object Home : Screen()
    object SongInput : Screen()
    object VisualWorld : Screen()
    object Storyboard : Screen()
    data class SceneDetail(val sceneIndex: Int) : Screen()
    object CinematicPreview : Screen()
    object ProjectList : Screen()
}
