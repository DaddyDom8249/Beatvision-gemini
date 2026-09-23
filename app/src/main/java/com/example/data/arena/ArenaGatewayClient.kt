package com.example.data.arena

import com.example.data.model.StoryboardScene
import com.example.data.model.VisualWorld
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import java.util.concurrent.TimeUnit

open class ArenaException(message: String, cause: Throwable? = null) : Exception(message, cause)
class ArenaSecurityException(message: String = "ARENA CONNECTION BLOCKED — CLIENT CREDENTIAL STORAGE IS NOT SAFE") : ArenaException(message)
class ArenaAuthException(message: String = "Arena authentication failed.") : ArenaException(message)
class ArenaRateLimitException(message: String = "Scene generation rate limited. Please wait 10 seconds before generating again.") : ArenaException(message)
class ArenaNetworkException(message: String = "Arena is unavailable. Your project data has been preserved.", cause: Throwable? = null) : ArenaException(message, cause)
class ArenaProviderException(message: String = "The visual provider is not configured or generation failed.") : ArenaException(message)
class ArenaResponseException(message: String = "Arena returned an invalid visual-generation response.") : ArenaException(message)

data class SceneImageResult(
    val sceneNumber: Int,
    val beatId: String,
    val imageUrl: String,
    val model: String,
    val status: String
)

class ArenaGatewayClient(
    val baseUrl: String = "https://beatvision-provider-arena.richardcranium466.workers.dev",
    val tokenProvider: () -> String? = { null },
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
) {
    companion object {
        const val CONTRACT_VERSION = "1.1"
        const val OPERATION_SCENE_IMAGES = "sceneImages"
        const val PATH_SCENE_IMAGES = "/v1/image/scenes"
        const val PATH_CLIENT_SCENE_IMAGE = "/v1/client/image/scene"
    }

    /**
     * Checks whether client-side credential storage is safe in the Android client APK.
     * In Android, shipping private gateway tokens or credentials inside the APK exposes
     * them to extraction upon decompilation.
     */
    fun isClientCredentialStorageSafe(): Boolean {
        return false
    }

    /**
     * Constructs the canonical BeatVision Arena Contract 1.1 request JSON payload.
     */
    fun buildRequestJson(scene: StoryboardScene, world: VisualWorld): JSONObject {
        // Find matching characters or lead character
        val sceneChars = scene.characters
        val matchedChar = world.characters.firstOrNull { char ->
            sceneChars.any { sc -> sc.contains(char.name, ignoreCase = true) || char.name.contains(sc, ignoreCase = true) }
        } ?: world.characters.firstOrNull()

        val characterConcept = JSONObject().apply {
            matchedChar?.let { c ->
                put("name", c.name)
                put("role", c.role)
                put("appearance", c.appearance)
                put("wardrobe", c.clothing)
                put("personality", c.personality)
                put("emotional_state", c.emotionalState)
                put("performance_behavior", c.performanceBehavior)
            }
        }

        // Find matching environment or first environment
        val matchedEnv = world.environments.firstOrNull { env ->
            scene.environment.contains(env.name, ignoreCase = true) || env.name.contains(scene.environment, ignoreCase = true)
        } ?: world.environments.firstOrNull()

        val locationsArray = JSONArray().apply {
            matchedEnv?.let { e ->
                put(JSONObject().apply {
                    put("name", e.name)
                    put("appearance", e.appearance)
                    put("architecture", e.architecture)
                    put("lighting", e.lighting)
                    put("atmosphere", e.atmosphere)
                    put("time_of_day", e.timeOfDay)
                    put("important_objects", e.importantObjects)
                })
            }
        }

        val worldObj = JSONObject().apply {
            put("the_world", world.theWorld)
            put("emotional_core", world.emotionalCore)
            put("character_concept", characterConcept)
            put("locations", locationsArray)
            put("visual_motifs", JSONArray(world.visualMotifs))
            put("continuity_rules", JSONArray(world.continuityRules))
            put("visual_language", JSONObject().apply {
                put("colors", world.visualLanguage.colors)
                put("lighting", world.visualLanguage.lighting)
                put("textures", world.visualLanguage.textures)
                put("production_design", world.visualLanguage.productionDesign)
                put("atmosphere", world.visualLanguage.atmosphere)
            })
            put("cinematography", JSONObject().apply {
                put("framing", world.cinematography.framing)
                put("camera_movement", world.cinematography.cameraMovement)
                put("lens_style", world.cinematography.lensStyle)
                put("shot_types", world.cinematography.shotTypes)
                put("camera_behavior", world.cinematography.cameraBehavior)
            })
        }

        // Calculate timing / duration in seconds
        val durationSeconds = parseTimingToSeconds(scene.motionPlan.timing)
        val startTime = 0.0
        val endTime = startTime + durationSeconds

        val sceneObj = JSONObject().apply {
            put("scene", scene.sceneNumber)
            put("beatId", "scene-${scene.sceneNumber}")
            put("title", scene.sceneTitle)
            put("storyRole", scene.storyRole)
            put("storyPurpose", scene.storyPurpose)
            put("lyricsSection", scene.lyricsSection)
            put("characters", JSONArray(scene.characters))
            put("environment", scene.environment)
            put("characterActions", scene.characterActions)
            put("characterInteraction", scene.characterInteraction)
            put("performanceDirection", scene.performanceDirection)
            put("cameraDirection", scene.cameraDirection)
            put("lighting", scene.lighting)
            put("atmosphere", scene.atmosphere)
            put("motionDirection", scene.motionDirection)
            put("visualPrompt", scene.visualPrompt)
            put("continuityRequirements", scene.continuityRequirements)
            put("startTime", startTime)
            put("duration_seconds", durationSeconds)
            put("endTime", endTime)
            put("motionPlan", JSONObject().apply {
                put("sceneSummary", scene.motionPlan.sceneSummary)
                put("characterMotion", JSONArray(scene.motionPlan.characterMotion))
                put("facialPerformance", JSONArray(scene.motionPlan.facialPerformance))
                put("interaction", JSONArray(scene.motionPlan.interaction))
                put("environmentMotion", JSONArray(scene.motionPlan.environmentMotion))
                put("cameraMotion", JSONArray(scene.motionPlan.cameraMotion))
                put("timing", scene.motionPlan.timing)
            })
        }

        val storyboardObj = JSONObject().apply {
            put("scenes", JSONArray().put(sceneObj))
        }

        val payloadObj = JSONObject().apply {
            put("style", "Dark industrial realism, cinematic lighting, coherent recurring character and environment.")
            put("world", worldObj)
            put("storyboard", storyboardObj)
        }

        return JSONObject().apply {
            put("contract_version", CONTRACT_VERSION)
            put("operation", OPERATION_SCENE_IMAGES)
            put("payload", payloadObj)
        }
    }

    /**
     * Parses and validates an Arena visual-generation response string.
     */
    fun parseResponse(responseBody: String, defaultSceneNumber: Int): Result<SceneImageResult> {
        val root = try {
            JSONObject(responseBody)
        } catch (e: JSONException) {
            return Result.failure(ArenaResponseException("Arena returned an invalid visual-generation response."))
        }

        val ok = root.optBoolean("ok", false)
        if (!ok) {
            val error = root.optString("error", "")
            val status = root.optString("status", "")
            if (status == "rate_limited" || error.contains("rate limit", ignoreCase = true)) {
                return Result.failure(ArenaRateLimitException("Scene generation rate limited. Please wait 10 seconds before generating again."))
            }
            if (status == "unauthorized" || error.contains("Unauthorized", ignoreCase = true) || error.contains("authentication", ignoreCase = true)) {
                return Result.failure(ArenaAuthException("Arena authentication failed."))
            }
            if (status == "provider_error" || status == "provider_unavailable" || error.contains("PIXAZO_API_KEY", ignoreCase = true) || error.contains("provider", ignoreCase = true)) {
                return Result.failure(ArenaProviderException("The visual provider is not configured or generation failed."))
            }
            return Result.failure(ArenaResponseException(error.ifBlank { "Arena returned an invalid visual-generation response." }))
        }

        val resultObj = root.optJSONObject("result")
            ?: return Result.failure(ArenaResponseException("Arena returned an invalid visual-generation response."))

        val imagesArr = resultObj.optJSONArray("images")
        if (imagesArr == null || imagesArr.length() == 0) {
            return Result.failure(ArenaResponseException("Arena returned an invalid visual-generation response."))
        }

        val firstImg = imagesArr.optJSONObject(0)
            ?: return Result.failure(ArenaResponseException("Arena returned an invalid visual-generation response."))

        val imageUrl = firstImg.optString("image_url", "").ifBlank {
            firstImg.optString("url", "")
        }

        if (imageUrl.isBlank()) {
            return Result.failure(ArenaResponseException("Arena returned an invalid visual-generation response."))
        }

        val sceneNum = firstImg.optInt("scene", defaultSceneNumber)
        val beatId = firstImg.optString("beatId", "scene-$sceneNum")
        val model = firstImg.optString("model", root.optString("model", "sdxl"))
        val status = firstImg.optString("status", "generated")

        return Result.success(
            SceneImageResult(
                sceneNumber = sceneNum,
                beatId = beatId,
                imageUrl = imageUrl,
                model = model,
                status = status
            )
        )
    }

    suspend fun generateSceneImage(
        scene: StoryboardScene,
        world: VisualWorld
    ): Result<SceneImageResult> = withContext(Dispatchers.IO) {
        val requestBodyJson = buildRequestJson(scene, world)
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestBodyJson.toString().toRequestBody(mediaType)
        val requestId = UUID.randomUUID().toString()

        val request = Request.Builder()
            .url("$baseUrl$PATH_CLIENT_SCENE_IMAGE")
            .post(body)
            .addHeader("X-BeatVision-Request", requestId)
            .addHeader("X-BeatVision-Contract", CONTRACT_VERSION)
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val code = response.code
                val responseString = response.body?.string().orEmpty()

                if (code == 429) {
                    return@withContext Result.failure(
                        ArenaRateLimitException("Scene generation rate limited. Please wait 10 seconds before generating again.")
                    )
                }
                if (code == 401 || code == 403) {
                    return@withContext Result.failure(ArenaAuthException("Arena authentication failed."))
                }
                if (code == 502 || code == 503) {
                    return@withContext Result.failure(ArenaProviderException("The visual provider is not configured or generation failed."))
                }

                if (!response.isSuccessful && responseString.isBlank()) {
                    return@withContext Result.failure(ArenaResponseException("Arena returned an invalid visual-generation response."))
                }

                parseResponse(responseString, scene.sceneNumber)
            }
        } catch (e: IOException) {
            Result.failure(ArenaNetworkException("Arena is unavailable. Your project data has been preserved.", e))
        } catch (e: ArenaException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(ArenaResponseException("Arena returned an invalid visual-generation response."))
        }
    }

    private fun parseTimingToSeconds(timing: String): Double {
        // e.g. "00:00 - 00:28 (28s duration)" -> 28.0
        val durationMatch = Regex("(\\d+)\\s*s", RegexOption.IGNORE_CASE).find(timing)
        if (durationMatch != null) {
            return durationMatch.groupValues[1].toDoubleOrNull() ?: 28.0
        }
        val rangeMatch = Regex("(\\d{2}):(\\d{2})\\s*-\\s*(\\d{2}):(\\d{2})").find(timing)
        if (rangeMatch != null) {
            val startMin = rangeMatch.groupValues[1].toDoubleOrNull() ?: 0.0
            val startSec = rangeMatch.groupValues[2].toDoubleOrNull() ?: 0.0
            val endMin = rangeMatch.groupValues[3].toDoubleOrNull() ?: 0.0
            val endSec = rangeMatch.groupValues[4].toDoubleOrNull() ?: 0.0
            val startTotal = startMin * 60 + startSec
            val endTotal = endMin * 60 + endSec
            val diff = endTotal - startTotal
            if (diff > 0) return diff
        }
        return 28.0
    }
}
