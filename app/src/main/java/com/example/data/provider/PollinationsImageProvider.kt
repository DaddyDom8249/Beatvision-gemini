package com.example.data.provider

import android.net.Uri
import com.example.data.arena.SceneImageResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.util.concurrent.TimeUnit

class PollinationsImageProvider(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .build(),
    private val baseUrl: String = "https://image.pollinations.ai/prompt/"
) : ImageProvider {
    override val descriptor = ProviderDescriptor(
        id = "pollinations",
        displayName = "Pollinations",
        capabilities = setOf(ProviderCapability.IMAGE_GENERATION),
        configured = true,
        authMethod = "none"
    )

    override suspend fun generateImage(request: ImageGenerationRequest): ProviderResult<SceneImageResult> =
        withContext(Dispatchers.IO) {
            val url = baseUrl + Uri.encode(buildPrompt(request))
            try {
                client.newCall(Request.Builder().url(url).get().build()).execute().use { httpResponse ->
                    if (!httpResponse.isSuccessful) {
                        return@withContext ProviderResult.Failure(
                            descriptor.id,
                            "Pollinations returned HTTP ${httpResponse.code}.",
                            retryable = httpResponse.code == 429 || httpResponse.code >= 500
                        )
                    }
                    ProviderResult.Success(
                        SceneImageResult(
                            sceneNumber = request.scene.sceneNumber,
                            beatId = "scene-${request.scene.sceneNumber}",
                            imageUrl = httpResponse.request.url.toString(),
                            model = "pollinations",
                            status = "generated"
                        ),
                        descriptor.id,
                        "pollinations"
                    )
                }
            } catch (e: IOException) {
                ProviderResult.Failure(
                    descriptor.id,
                    "Pollinations is unavailable. BeatVision preserved your project data.",
                    retryable = true,
                    cause = e
                )
            } catch (e: Exception) {
                ProviderResult.Failure(descriptor.id, "Pollinations image generation failed.", cause = e)
            }
        }

    private fun buildPrompt(request: ImageGenerationRequest): String {
        val scene = request.scene
        val world = request.world
        val character = world.characters.firstOrNull()
        val environment = world.environments.firstOrNull()
        return listOf(
            "Cinematic music video still.",
            "Scene ${scene.sceneNumber}: ${scene.sceneTitle}.",
            "Story purpose: ${scene.storyPurpose}.",
            "Scene action: ${scene.characterActions}.",
            "Performance: ${scene.performanceDirection}.",
            "Camera: ${scene.cameraDirection}.",
            "Lighting: ${scene.lighting}.",
            "Atmosphere: ${scene.atmosphere}.",
            "World: ${world.theWorld}.",
            "Emotional core: ${world.emotionalCore}.",
            "Character continuity: ${character?.appearance.orEmpty()}; ${character?.clothing.orEmpty()}.",
            "Environment continuity: ${environment?.appearance.orEmpty()}; ${environment?.architecture.orEmpty()}.",
            "Visual language: ${world.visualLanguage.colors}; ${world.visualLanguage.lighting}; ${world.visualLanguage.textures}.",
            "Continuity rules: ${world.continuityRules.joinToString("; ")}.",
            "Do not add unrelated characters, locations, props, or events."
        ).joinToString(" ")
    }
}
