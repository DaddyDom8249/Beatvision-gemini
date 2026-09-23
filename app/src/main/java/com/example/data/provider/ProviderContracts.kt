package com.example.data.provider

import com.example.data.arena.SceneImageResult
import com.example.data.model.StoryboardScene
import com.example.data.model.VisualWorld

enum class ProviderCapability {
    TEXT_INTELLIGENCE,
    IMAGE_GENERATION,
    IMAGE_EDITING,
    CHARACTER_GENERATION,
    CHARACTER_ANIMATION,
    IMAGE_TO_VIDEO,
    TEXT_TO_VIDEO,
    VIDEO_EXTENSION,
    VIDEO_TO_VIDEO,
    LIP_SYNC,
    VOICE_GENERATION,
    AUDIO_ANALYSIS,
    MOTION_TRANSFER,
    VIDEO_ASSEMBLY,
    STORAGE
}

enum class ProviderSelectionMode { AUTOMATIC, MANUAL }

data class ImageGenerationRequest(
    val scene: StoryboardScene,
    val world: VisualWorld
)

data class ProviderDescriptor(
    val id: String,
    val displayName: String,
    val capabilities: Set<ProviderCapability>,
    val configured: Boolean,
    val supportsReferenceImages: Boolean = false,
    val supportsCharacterReferences: Boolean = false,
    val supportsAudio: Boolean = false,
    val supportsLipSync: Boolean = false,
    val supportsCameraControls: Boolean = false,
    val authMethod: String = "none",
    val commercialUseRestrictions: String? = null
)

sealed class ProviderResult<out T> {
    data class Success<T>(val value: T, val providerId: String, val model: String? = null) : ProviderResult<T>()
    data class Failure(
        val providerId: String,
        val message: String,
        val retryable: Boolean = false,
        val cause: Throwable? = null
    ) : ProviderResult<Nothing>()
}

interface ImageProvider {
    val descriptor: ProviderDescriptor
    suspend fun generateImage(request: ImageGenerationRequest): ProviderResult<SceneImageResult>
}

interface MotionProvider {
    val descriptor: ProviderDescriptor
    suspend fun generateMotion(request: MotionGenerationRequest): ProviderResult<VideoAsset>
}

interface AudioProvider {
    val descriptor: ProviderDescriptor
    suspend fun analyzeAudio(request: AudioAnalysisRequest): ProviderResult<AudioAnalysis>
}

interface LanguageProvider {
    val descriptor: ProviderDescriptor
    suspend fun generateCreativeDirection(request: CreativeRequest): ProviderResult<CreativeResponse>
}

data class MotionGenerationRequest(
    val scene: StoryboardScene,
    val world: VisualWorld,
    val motionPlan: com.example.data.model.MotionPlan,
    val characterReferences: List<String> = emptyList(),
    val environmentReference: String? = null
)

data class VideoAsset(val url: String, val providerId: String, val model: String? = null)
data class AudioAnalysisRequest(val audioUri: String, val projectId: String)
data class AudioAnalysis(val providerId: String, val durationMs: Long? = null, val beatsPerMinute: Double? = null, val summary: String? = null)
data class CreativeRequest(val songTitle: String, val artist: String, val lyrics: String, val creativeDirection: String)
data class CreativeResponse(val providerId: String, val rawJson: String)
