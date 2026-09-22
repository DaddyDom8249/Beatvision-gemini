package com.example.data.model

data class Project(
    val id: String,
    val name: String,
    val songTitle: String,
    val artist: String,
    val lyrics: String,
    val creativeDirection: String,
    val audioUri: String? = null,
    val audioFileName: String? = null,
    val audioDurationMs: Long = 0L,
    val worldReport: VisualWorld? = null,
    val isWorldApproved: Boolean = false,
    val storyboard: List<StoryboardScene> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDemo: Boolean = false
)

data class VisualWorld(
    val theWorld: String,
    val emotionalCore: String,
    val story: StoryArc,
    val characters: List<CharacterProfile>,
    val environments: List<EnvironmentProfile>,
    val visualLanguage: VisualLanguage,
    val cinematography: Cinematography,
    val motionLanguage: MotionLanguage,
    val visualMotifs: List<String>,
    val performanceDirection: PerformanceDirection,
    val continuityRules: List<String>
)

data class StoryArc(
    val beginning: String,
    val middle: String,
    val ending: String
)

data class CharacterProfile(
    val name: String,
    val appearance: String,
    val clothing: String,
    val personality: String,
    val emotionalState: String,
    val role: String,
    val performanceBehavior: String
)

data class EnvironmentProfile(
    val name: String,
    val appearance: String,
    val architecture: String,
    val lighting: String,
    val atmosphere: String,
    val timeOfDay: String,
    val importantObjects: String
)

data class VisualLanguage(
    val colors: String,
    val lighting: String,
    val textures: String,
    val productionDesign: String,
    val atmosphere: String
)

data class Cinematography(
    val framing: String,
    val cameraMovement: String,
    val lensStyle: String,
    val shotTypes: String,
    val cameraBehavior: String
)

data class MotionLanguage(
    val walking: String,
    val dancing: String,
    val singing: String,
    val gestures: String,
    val interaction: String,
    val environmentalMovement: String,
    val cameraMovement: String
)

data class PerformanceDirection(
    val singing: String,
    val moving: String,
    val interacting: String,
    val reacting: String,
    val expressingEmotion: String
)

data class StoryboardScene(
    val sceneNumber: Int,
    val sceneTitle: String,
    val storyRole: String,
    val storyPurpose: String,
    val lyricsSection: String,
    val characters: List<String>,
    val environment: String,
    val characterActions: String,
    val characterInteraction: String,
    val performanceDirection: String,
    val cameraDirection: String,
    val lighting: String,
    val atmosphere: String,
    val motionDirection: String,
    val visualPrompt: String,
    val continuityRequirements: String,
    val motionPlan: MotionPlan
)

data class MotionPlan(
    val sceneSummary: String,
    val characterMotion: List<String>,
    val facialPerformance: List<String>,
    val interaction: List<String>,
    val environmentMotion: List<String>,
    val cameraMotion: List<String>,
    val timing: String
)

data class SceneConcept(
    val sceneDescription: String,
    val visualComposition: String,
    val characterAppearance: String,
    val environment: String,
    val camera: String,
    val lighting: String,
    val motion: String
)
