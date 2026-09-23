package com.example.data.provider

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Provider-neutral image-to-video adapter. A concrete provider endpoint can be
 * supplied later without changing BeatVision's creative models or UI.
 */
class HttpImageToVideoProvider(
    override val descriptor: ProviderDescriptor,
    private val endpoint: String,
    private val apiKey: String? = null,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .build()
) : MotionProvider {
    init {
        require(endpoint.startsWith("https://")) { "Motion provider endpoint must use HTTPS." }
    }

    override suspend fun generateMotion(request: MotionGenerationRequest): ProviderResult<VideoAsset> =
        withContext(Dispatchers.IO) {
            val payload = JSONObject().apply {
                put("scene_number", request.scene.sceneNumber)
                put("scene_title", request.scene.sceneTitle)
                put("image_url", request.scene.generatedImageUrl.orEmpty())
                put("motion_plan", JSONObject().apply {
                    put("scene_summary", request.motionPlan.sceneSummary)
                    put("character_motion", request.motionPlan.characterMotion.joinToString("; "))
                    put("facial_performance", request.motionPlan.facialPerformance.joinToString("; "))
                    put("interaction", request.motionPlan.interaction.joinToString("; "))
                    put("environment_motion", request.motionPlan.environmentMotion.joinToString("; "))
                    put("camera_motion", request.motionPlan.cameraMotion.joinToString("; "))
                    put("timing", request.motionPlan.timing)
                })
                put("character_references", request.characterReferences)
                put("environment_reference", request.environmentReference.orEmpty())
            }
            val builder = Request.Builder()
                .url(endpoint)
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .header("Content-Type", "application/json")
            apiKey?.takeIf { it.isNotBlank() }?.let { builder.header("Authorization", "Bearer $it") }

            try {
                client.newCall(builder.build()).execute().use { httpResponse ->
                    val raw = httpResponse.body?.string().orEmpty()
                    if (!httpResponse.isSuccessful) {
                        return@withContext ProviderResult.Failure(
                            descriptor.id,
                            "Motion provider returned HTTP ${httpResponse.code}.",
                            retryable = httpResponse.code == 429 || httpResponse.code >= 500
                        )
                    }
                    val json = JSONObject(raw)
                    val videoUrl = json.optString("video_url").ifBlank { json.optString("url") }
                    if (videoUrl.isBlank()) {
                        return@withContext ProviderResult.Failure(descriptor.id, "Motion provider returned no video URL.")
                    }
                    ProviderResult.Success(
                        VideoAsset(videoUrl, descriptor.id, json.optString("model").ifBlank { null }),
                        descriptor.id,
                        json.optString("model").ifBlank { null }
                    )
                }
            } catch (e: Exception) {
                ProviderResult.Failure(descriptor.id, "Motion provider request failed.", retryable = true, cause = e)
            }
        }
}
