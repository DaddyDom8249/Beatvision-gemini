package com.example.data.provider

import com.example.data.arena.SceneImageResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.net.URI
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
        validateEndpoint(endpoint)
        require(apiKey.isNotBlank()) { "Provider API key must not be blank." }
        require(model.isNotBlank()) { "Provider model must not be blank." }
        require(model.length <= MAX_MODEL_LENGTH) { "Provider model is too long." }
    }

    override suspend fun generateImage(request: ImageGenerationRequest): ProviderResult<SceneImageResult> =
        withContext(Dispatchers.IO) {
            val prompt = buildPrompt(request)
            if (prompt.length > MAX_PROMPT_LENGTH) {
                return@withContext ProviderResult.Failure(descriptor.id, "The image prompt exceeds the provider safety limit.")
            }
            val payload = JSONObject().apply {
                put("model", model)
                put("prompt", prompt)
            }
            val httpRequest = Request.Builder()
                .url(endpoint)
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
                .build()
            try {
                client.newCall(httpRequest).execute().use { httpResponse ->
                    val body = httpResponse.body
                        ?: return@withContext ProviderResult.Failure(descriptor.id, "Provider returned an empty response.", retryable = httpResponse.code >= 500)
                    if (body.contentLength() > MAX_RESPONSE_BYTES) {
                        return@withContext ProviderResult.Failure(descriptor.id, "Provider response exceeds the safety limit.")
                    }
                    val raw = body.stringLimited(MAX_RESPONSE_BYTES)
                        ?: return@withContext ProviderResult.Failure(descriptor.id, "Provider response exceeds the safety limit.")
                    if (!httpResponse.isSuccessful) {
                        return@withContext ProviderResult.Failure(
                            descriptor.id,
                            "Provider returned HTTP ${httpResponse.code}.",
                            retryable = httpResponse.code == 408 || httpResponse.code == 429 || httpResponse.code >= 500
                        )
                    }
                    val data = JSONObject(raw).optJSONArray("data")
                        ?: return@withContext ProviderResult.Failure(descriptor.id, "Provider returned no image data.")
                    val item = data.optJSONObject(0)
                        ?: return@withContext ProviderResult.Failure(descriptor.id, "Provider returned no image data.")
                    val url = item.optString("url")
                    if (url.isNotBlank()) {
                        if (!isHttpsUrl(url)) return@withContext ProviderResult.Failure(descriptor.id, "Provider returned an insecure image URL.")
                        return@withContext ProviderResult.Success(
                            SceneImageResult(request.scene.sceneNumber, "scene-${request.scene.sceneNumber}", url, model, "generated"),
                            descriptor.id,
                            model
                        )
                    }
                    val b64 = item.optString("b64_json")
                    if (b64.isNotBlank()) {
                        if (b64.length > MAX_BASE64_LENGTH) return@withContext ProviderResult.Failure(descriptor.id, "Provider returned an image payload that is too large.")
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
    
    private fun validateEndpoint(value: String) {
        require(value.length <= MAX_ENDPOINT_LENGTH) { "Provider endpoint is too long." }
        val uri = try { URI(value) } catch (_: Exception) {
            throw IllegalArgumentException("Provider endpoint is invalid.")
        }
        require(uri.scheme.equals("https", ignoreCase = true) && !uri.host.isNullOrBlank()) {
            "Provider endpoint must use HTTPS and a valid host."
        }
        require(uri.userInfo.isNullOrBlank()) { "Provider endpoint must not contain embedded credentials." }
        require(uri.fragment.isNullOrBlank()) { "Provider endpoint must not contain a URL fragment." }
    }

    private fun isHttpsUrl(value: String): Boolean = try {
        val uri = URI(value)
        uri.scheme.equals("https", ignoreCase = true) &&
            !uri.host.isNullOrBlank() &&
            uri.userInfo.isNullOrBlank() &&
            uri.fragment.isNullOrBlank()
    } catch (_: Exception) {
        false
    }

    private fun okhttp3.ResponseBody.stringLimited(maxBytes: Long): String? {
        if (contentLength() > maxBytes) return null
        val output = java.io.ByteArrayOutputStream()
        byteStream().use { input ->
            val buffer = ByteArray(16 * 1024)
            var total = 0L
            while (true) {
                val read = input.read(buffer)
                if (read < 0) break
                total += read
                if (total > maxBytes) return null
                output.write(buffer, 0, read)
            }
        }
        return output.toString(Charsets.UTF_8.name())
    }

    companion object {
        private const val MAX_RESPONSE_BYTES = 4L * 1024L * 1024L
        private const val MAX_BASE64_LENGTH = 8 * 1024 * 1024
        private const val MAX_PROMPT_LENGTH = 32 * 1024
        private const val MAX_ENDPOINT_LENGTH = 2048
        private const val MAX_MODEL_LENGTH = 256
    }
}

