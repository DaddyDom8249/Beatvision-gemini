package com.example.data.provider

import com.example.data.arena.SceneImageResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OpenAiCompatibleImageProvider(
    override val descriptor: ProviderDescriptor,
    private val endpoint: String,
    private val apiKey: String,
    private val model: String,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .build()
) : ImageProvider {
    init {
        require(endpoint.startsWith("https://")) { "Provider endpoint must use HTTPS." }
        require(apiKey.isNotBlank()) { "Provider API key must not be blank." }
        require(model.isNotBlank()) { "Provider model must not be blank." }
    }

    override suspend fun generateImage(request: ImageGenerationRequest): ProviderResult<SceneImageResult> =
        withContext(Dispatchers.IO) {
            val payload = JSONObject().apply {
                put("model", model)
                put("prompt", buildPrompt(request))
            }
            val httpRequest = Request.Builder()
                .url(endpoint)
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .build()
            try {
                client.newCall(httpRequest).execute().use { httpResponse ->
                    val raw = httpResponse.body?.string().orEmpty()
                    if (!httpResponse.isSuccessful) {
                        return@withContext ProviderResult.Failure(
                            descriptor.id,
                            "Provider returned HTTP ${httpResponse.code}.",
                            retryable = httpResponse.code == 429 || httpResponse.code >= 500
                        )
                    }
                    val data = JSONObject(raw).optJSONArray("data")
                        ?: return@withContext ProviderResult.Failure(descriptor.id, "Provider returned no image data.")
                    val item = data.optJSONObject(0)
                        ?: return@withContext ProviderResult.Failure(descriptor.id, "Provider returned no image data.")
                    val url = item.optString("url")
                    if (url.isNotBlank()) {
                        return@withContext ProviderResult.Success(
                            SceneImageResult(request.scene.sceneNumber, "scene-${request.scene.sceneNumber}", url, model, "generated"),
                            descriptor.id,
                            model
                        )
                    }
                    val b64 = item.optString("b64_json")
                    if (b64.isNotBlank()) {
                        return@withContext ProviderResult.Success(
                            SceneImageResult(request.scene.sceneNumber, "scene-${request.scene.sceneNumber}", "data:image/png;base64,$b64", model, "generated"),
                            descriptor.id,
                            model
                        )
                    }
                    ProviderResult.Failure(descriptor.id, "Provider returned no usable image URL.")
                }
            } catch (e: Exception) {
                ProviderResult.Failure(descriptor.id, "Custom image provider request failed.", retryable = true, cause = e)
            }
        }

    private fun buildPrompt(request: ImageGenerationRequest): String {
        val scene = request.scene
        val world = request.world
        return listOf(
            "Create one cinematic music-video frame.",
            "Scene ${scene.sceneNumber}: ${scene.sceneTitle}.",
            scene.visualPrompt,
            "Story purpose: ${scene.storyPurpose}.",
            "Characters: ${scene.characters.joinToString(", ")}.",
            "Actions: ${scene.characterActions}.",
            "Camera: ${scene.cameraDirection}.",
            "Lighting: ${scene.lighting}.",
            "Atmosphere: ${scene.atmosphere}.",
            "World: ${world.theWorld}.",
            "Emotional core: ${world.emotionalCore}.",
            "Continuity: ${world.continuityRules.joinToString("; ")}."
        ).joinToString(" ")
    }
}
