package com.example.data.provider

import com.example.data.arena.SceneImageResult
import com.example.data.model.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderRegistryTest {
    private val world = VisualWorld(
        theWorld = "test world",
        emotionalCore = "test emotion",
        story = StoryArc("a", "b", "c"),
        characters = emptyList(),
        environments = emptyList(),
        visualLanguage = VisualLanguage("colors", "light", "texture", "design", "air"),
        cinematography = Cinematography("frame", "move", "lens", "shots", "behavior"),
        motionLanguage = MotionLanguage("walk", "dance", "sing", "gesture", "interact", "environment", "camera"),
        visualMotifs = emptyList(),
        performanceDirection = PerformanceDirection("sing", "move", "interact", "react", "emotion"),
        continuityRules = emptyList()
    )
    private val scene = StoryboardScene(
        sceneNumber = 1,
        sceneTitle = "Test",
        storyRole = "Opening",
        storyPurpose = "Proof",
        lyricsSection = "",
        characters = emptyList(),
        environment = "",
        characterActions = "walk",
        characterInteraction = "",
        performanceDirection = "",
        cameraDirection = "",
        lighting = "",
        atmosphere = "",
        motionDirection = "",
        visualPrompt = "",
        continuityRequirements = "",
        motionPlan = MotionPlan("test", emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), "1s")
    )

    @Test
    fun automaticRoutingReturnsFirstSuccessfulProvider() = runTest {
        val failing = fake("first", retryable = true)
        val successful = fake("second", success = true)
        val registry = ProviderRegistry(listOf(failing, successful))

        val result = registry.generateImage(ImageGenerationRequest(scene, world))

        assertTrue(result is ProviderResult.Success)
        assertEquals("second", (result as ProviderResult.Success).providerId)
    }

    @Test
    fun manualRoutingRejectsUnconfiguredProvider() = runTest {
        val configured = fake("configured", success = true)
        val unconfigured = fake("unconfigured", success = true, configured = false)
        val registry = ProviderRegistry(listOf(configured, unconfigured))

        val result = registry.generateImage(
            ImageGenerationRequest(scene, world),
            ProviderSelectionMode.MANUAL,
            "unconfigured"
        )

        assertTrue(result is ProviderResult.Failure)
        assertEquals("unconfigured", (result as ProviderResult.Failure).providerId)
    }

    @Test
    fun manualRoutingDoesNotFallbackToAnotherProvider() = runTest {
        val first = fake("first", success = true)
        val second = fake("second", success = true)
        val registry = ProviderRegistry(listOf(first, second))

        val result = registry.generateImage(
            ImageGenerationRequest(scene, world),
            ProviderSelectionMode.MANUAL,
            "second"
        )

        assertTrue(result is ProviderResult.Success)
        assertEquals("second", (result as ProviderResult.Success).providerId)
    }

    private fun fake(id: String, success: Boolean = false, retryable: Boolean = false, configured: Boolean = true): ImageProvider =
        object : ImageProvider {
            override val descriptor = ProviderDescriptor(
                id = id,
                displayName = id,
                capabilities = setOf(ProviderCapability.IMAGE_GENERATION),
                configured = configured
            )

            override suspend fun generateImage(
                request: ImageGenerationRequest
            ): ProviderResult<SceneImageResult> {
                return if (success) {
                    ProviderResult.Success(
                        SceneImageResult(1, "scene-1", "https://example.invalid/image.png", id, "generated"),
                        id,
                        id
                    )
                } else {
                    ProviderResult.Failure(id, "failed", retryable)
                }
            }
        }
}
