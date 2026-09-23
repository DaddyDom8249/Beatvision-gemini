package com.example.data.provider

import com.example.data.arena.SceneImageResult
import com.example.data.model.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class MotionProviderRegistryTest {
    @Test
    fun noConfiguredMotionProviderFailsHonestly() = runTest {
        val registry = MotionProviderRegistry()
        val result = registry.generateMotion(
            MotionGenerationRequest(
                scene = sampleScene(),
                world = sampleWorld(),
                motionPlan = sampleScene().motionPlan
            )
        )
        assertTrue(result is ProviderResult.Failure)
    }

    private fun sampleWorld() = VisualWorld(
        "world", "emotion", StoryArc("a", "b", "c"), emptyList(), emptyList(),
        VisualLanguage("c", "l", "t", "d", "a"),
        Cinematography("f", "m", "l", "s", "b"),
        MotionLanguage("w", "d", "s", "g", "i", "e", "c"),
        emptyList(), PerformanceDirection("s", "m", "i", "r", "e"), emptyList()
    )

    private fun sampleScene() = StoryboardScene(
        1, "test", "Opening", "test", "", emptyList(), "", "", "", "", "", "", "", "", "", "",
        MotionPlan("test", emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), "1s")
    )
}
