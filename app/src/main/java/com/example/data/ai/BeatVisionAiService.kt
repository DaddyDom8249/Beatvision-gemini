package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.CharacterProfile
import com.example.data.model.Cinematography
import com.example.data.model.EnvironmentProfile
import com.example.data.model.MotionLanguage
import com.example.data.model.MotionPlan
import com.example.data.model.PerformanceDirection
import com.example.data.model.SceneConcept
import com.example.data.model.StoryArc
import com.example.data.model.StoryboardScene
import com.example.data.model.VisualLanguage
import com.example.data.model.VisualWorld
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

interface BeatVisionAiService {
    suspend fun generateWorld(
        songTitle: String,
        artist: String,
        lyrics: String,
        creativeDirection: String
    ): Result<VisualWorld>

    suspend fun refineWorld(
        currentWorld: VisualWorld,
        feedback: String,
        songTitle: String,
        lyrics: String
    ): Result<VisualWorld>

    suspend fun generateStoryboard(
        world: VisualWorld,
        songTitle: String,
        lyrics: String
    ): Result<List<StoryboardScene>>

    suspend fun generateSceneConcept(
        scene: StoryboardScene,
        world: VisualWorld
    ): Result<SceneConcept>

    suspend fun generateMotionPlan(
        scene: StoryboardScene,
        world: VisualWorld
    ): Result<MotionPlan>

    // Future image-to-video model interface placeholder (e.g. Wan, LTX)
    suspend fun generateVideo(
        scene: StoryboardScene,
        characterReferences: List<String>,
        environmentReference: String,
        motionPlan: MotionPlan
    ): Result<String>
}

class GeminiBeatVisionAiService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
) : BeatVisionAiService {

    private val modelName = "gemini-3.5-flash"
    private val apiKey: String
        get() = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Throwable) {
            ""
        }

    private fun isApiKeyValid(): Boolean {
        val key = apiKey.trim()
        return key.isNotEmpty() && key != "MY_GEMINI_API_KEY"
    }

    override suspend fun generateWorld(
        songTitle: String,
        artist: String,
        lyrics: String,
        creativeDirection: String
    ): Result<VisualWorld> = withContext(Dispatchers.IO) {
        if (!isApiKeyValid()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add GEMINI_API_KEY in the AI Studio Secrets panel, or explore our complete offline Demo Mode.")
            )
        }

        val prompt = """
You are an award-winning music video director, cinematographer, and visual world-builder for BeatVision.
Analyze this song and creative direction to design a complete, cohesive cinematic visual world for the music video.

Song: "$songTitle"
Artist: "$artist"
Creative Direction: "$creativeDirection"
Lyrics:
$lyrics

Return ONLY a valid JSON object matching this exact schema:
{
  "theWorld": "Detailed description of the visual universe, architecture, weather, and world scale",
  "emotionalCore": "How the song feels visually; emotional progression and tone",
  "story": {
    "beginning": "Opening scenario and character state",
    "middle": "Escalation, conflict or encounter",
    "ending": "Climactic resolution and final visual beat"
  },
  "characters": [
    {
      "name": "Character name",
      "appearance": "Age, physical traits, hair, face",
      "clothing": "Specific garments, colors, textures",
      "personality": "Core traits",
      "emotionalState": "Current emotional state",
      "role": "Narrative role in the video",
      "performanceBehavior": "Physical posture, how they sing/move"
    }
  ],
  "environments": [
    {
      "name": "Location name",
      "appearance": "Visual look and feel",
      "architecture": "Building style or environmental structures",
      "lighting": "Source, temperature, direction",
      "atmosphere": "Weather, air, particles",
      "timeOfDay": "Specific time of day or night",
      "importantObjects": "Key interactive items or machinery"
    }
  ],
  "visualLanguage": {
    "colors": "Palette description with hex or color names",
    "lighting": "Key lighting style and contrast",
    "textures": "Tactile surfaces and materials",
    "productionDesign": "Set dressing and aesthetic style",
    "atmosphere": "Ambient haze, smoke, rain, or particles"
  },
  "cinematography": {
    "framing": "Aspect ratio and composition rules",
    "cameraMovement": "Tracking, dolly, crane, handheld behavior",
    "lensStyle": "Focal lengths, bokeh, anamorphic flare style",
    "shotTypes": "Wide, medium, macro distribution",
    "cameraBehavior": "Pacing and emotional camera reaction"
  },
  "motionLanguage": {
    "walking": "Gait and pace description",
    "dancing": "Movement style or naturalistic choreography",
    "singing": "Vocal physical delivery and breath",
    "gestures": "Key hand and body gestures",
    "interaction": "How characters connect or touch",
    "environmentalMovement": "Wind, water, debris, light motion",
    "cameraMovement": "Synchronized camera kinetics"
  },
  "visualMotifs": [
    "Recurring symbolic visual element 1",
    "Recurring symbolic visual element 2",
    "Recurring symbolic visual element 3"
  ],
  "performanceDirection": {
    "singing": "How the song is delivered on camera",
    "moving": "Grounded physical behavior in environment",
    "interacting": "Eye contact and physical tension",
    "reacting": "Subtle micro-expressions",
    "expressingEmotion": "How internal feelings are externalized"
  },
  "continuityRules": [
    "Strict rule 1 for character appearance across all scenes",
    "Strict rule 2 for environment and weather continuity",
    "Strict rule 3 for lighting color palette"
  ]
}
""".trimIndent()

        callGeminiJson(prompt).mapCatching { json ->
            parseVisualWorld(json)
        }
    }

    override suspend fun refineWorld(
        currentWorld: VisualWorld,
        feedback: String,
        songTitle: String,
        lyrics: String
    ): Result<VisualWorld> = withContext(Dispatchers.IO) {
        if (!isApiKeyValid()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add GEMINI_API_KEY in the AI Studio Secrets panel.")
            )
        }

        val prompt = """
You are refining an existing Visual World for the music video of "$songTitle".
User creative refinement request: "$feedback"

Current World Summary:
- Universe: ${currentWorld.theWorld}
- Characters: ${currentWorld.characters.joinToString { it.name }}
- Primary Environment: ${currentWorld.environments.firstOrNull()?.name ?: "Unknown"}

Apply the user's creative feedback while preserving the core song identity, character continuity, and emotional integrity.
Return ONLY a valid JSON object matching the full VisualWorld schema (same keys: theWorld, emotionalCore, story, characters, environments, visualLanguage, cinematography, motionLanguage, visualMotifs, performanceDirection, continuityRules).
""".trimIndent()

        callGeminiJson(prompt).mapCatching { json ->
            parseVisualWorld(json)
        }
    }

    override suspend fun generateStoryboard(
        world: VisualWorld,
        songTitle: String,
        lyrics: String
    ): Result<List<StoryboardScene>> = withContext(Dispatchers.IO) {
        if (!isApiKeyValid()) {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is not configured. Please add GEMINI_API_KEY in the AI Studio Secrets panel.")
            )
        }

        val charSummary = world.characters.joinToString("\n") {
            "- ${it.name}: ${it.appearance}. Clothing: ${it.clothing}. Behavior: ${it.performanceBehavior}"
        }
        val envSummary = world.environments.joinToString("\n") {
            "- ${it.name}: ${it.appearance}. Lighting: ${it.lighting}. Atmosphere: ${it.atmosphere}"
        }

        val prompt = """
You are the storyboard director and continuity supervisor for BeatVision.
Create an EXACT 6-SCENE storyboard for the music video of "$songTitle".

Song Lyrics:
$lyrics

Visual World Context:
Universe: ${world.theWorld}
Emotional Core: ${world.emotionalCore}
Visual Language: ${world.visualLanguage.colors}, ${world.visualLanguage.lighting}
Characters:
$charSummary
Environments:
$envSummary
Continuity Rules:
${world.continuityRules.joinToString("\n") { "- $it" }}

Structure requirements:
- Exactly 6 scenes:
  Scene 1: Opening
  Scene 2: Introduction
  Scene 3: Development
  Scene 4: Emotional Peak
  Scene 5: Transformation / Climax
  Scene 6: Resolution
- Every scene must include a structured Motion Plan detailing physical character motion, facial performance, interaction, environment motion, camera motion, and timing.
- Characters and environments must STRICTLY preserve their defined appearances.

Return ONLY a JSON array of 6 objects with this schema:
[
  {
    "sceneNumber": 1,
    "sceneTitle": "Title of Scene",
    "storyRole": "Opening",
    "storyPurpose": "Why this scene exists in the narrative arc",
    "lyricsSection": "Lines of lyrics corresponding to this moment",
    "characters": ["Character Name 1"],
    "environment": "Location Name",
    "characterActions": "Detailed physical actions performed by characters",
    "characterInteraction": "Physical or emotional interactions between characters",
    "performanceDirection": "Specific instructions on vocal delivery, breathing, posture",
    "cameraDirection": "Focal length, camera path, angle, framing",
    "lighting": "Lighting setup and color temperature",
    "atmosphere": "Atmospheric elements (rain, fog, wind)",
    "motionDirection": "Overview of scene motion energy",
    "visualPrompt": "Cinematic visual description for image generation",
    "continuityRequirements": "Wardrobe, scar, lighting continuity items to check",
    "motionPlan": {
      "sceneSummary": "One-sentence overview of the motion arc",
      "characterMotion": ["Specific physical step 1", "Specific physical step 2"],
      "facialPerformance": ["Expression detail 1", "Expression detail 2"],
      "interaction": ["Interaction detail 1"],
      "environmentMotion": ["Environmental movement 1", "Environmental movement 2"],
      "cameraMotion": ["Camera path description 1", "Camera path description 2"],
      "timing": "00:00 - 00:15 (15s duration)"
    }
  }
]
""".trimIndent()

        callGeminiJson(prompt).mapCatching { json ->
            val scenesArray = if (json.has("scenes")) {
                json.getJSONArray("scenes")
            } else if (json.has("storyboard")) {
                json.getJSONArray("storyboard")
            } else {
                throw IllegalStateException("Unexpected JSON response structure for storyboard")
            }
            parseStoryboardScenes(scenesArray)
        }.recoverCatching {
            // If the model wrapped in an array root or raw array, try parsing root
            callGeminiRawText(prompt).getOrThrow().let { raw ->
                val cleaned = cleanJsonString(raw)
                val jsonArr = JSONArray(cleaned)
                parseStoryboardScenes(jsonArr)
            }
        }
    }

    override suspend fun generateSceneConcept(
        scene: StoryboardScene,
        world: VisualWorld
    ): Result<SceneConcept> = withContext(Dispatchers.IO) {
        val concept = SceneConcept(
            sceneDescription = "${scene.sceneTitle}: ${scene.storyPurpose}",
            visualComposition = "${scene.cameraDirection}. Lighting: ${scene.lighting}",
            characterAppearance = scene.characters.joinToString(", ") { charName ->
                val profile = world.characters.find { it.name.equals(charName, ignoreCase = true) }
                if (profile != null) "$charName (${profile.clothing})" else charName
            },
            environment = scene.environment,
            camera = scene.cameraDirection,
            lighting = scene.lighting,
            motion = scene.motionPlan.sceneSummary
        )
        Result.success(concept)
    }

    override suspend fun generateMotionPlan(
        scene: StoryboardScene,
        world: VisualWorld
    ): Result<MotionPlan> = withContext(Dispatchers.IO) {
        Result.success(scene.motionPlan)
    }

    override suspend fun generateVideo(
        scene: StoryboardScene,
        characterReferences: List<String>,
        environmentReference: String,
        motionPlan: MotionPlan
    ): Result<String> {
        // Architecture placeholder for future video model integration (Wan, LTX, etc.)
        return Result.failure(
            UnsupportedOperationException(
                "Full video generation pipeline will be available in future releases. Use the Cinematic Preview to experience the real-time animated storyboard."
            )
        )
    }

    // --- Private Helper Methods ---

    private suspend fun callGeminiJson(prompt: String): Result<JSONObject> {
        return callGeminiRawText(prompt).mapCatching { rawText ->
            val cleaned = cleanJsonString(rawText)
            if (cleaned.startsWith("[")) {
                JSONObject().apply { put("items", JSONArray(cleaned)) }
            } else {
                JSONObject(cleaned)
            }
        }
    }

    private suspend fun callGeminiRawText(prompt: String): Result<String> = withContext(Dispatchers.IO) {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent?key=$apiKey"

        val requestJson = JSONObject().apply {
            val contentsArr = JSONArray().apply {
                val contentObj = JSONObject().apply {
                    val partsArr = JSONArray().apply {
                        val partObj = JSONObject().apply {
                            put("text", prompt)
                        }
                        put(partObj)
                    }
                    put("parts", partsArr)
                }
                put(contentObj)
            }
            put("contents", contentsArr)

            val configObj = JSONObject().apply {
                put("temperature", 0.6)
                put("responseMimeType", "application/json")
            }
            put("generationConfig", configObj)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = requestJson.toString().toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string()
                if (!response.isSuccessful || responseBody == null) {
                    val errorMsg = if (response.code == 403 || response.code == 400) {
                        "Gemini API request rejected (${response.code}). Please check your GEMINI_API_KEY in the Secrets panel."
                    } else {
                        "Gemini service unavailable (${response.code}). Please try again."
                    }
                    return@withContext Result.failure(Exception(errorMsg))
                }

                val parsedResponse = JSONObject(responseBody)
                val candidates = parsedResponse.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    return@withContext Result.failure(Exception("No content returned by Gemini."))
                }
                val content = candidates.getJSONObject(0).optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text") ?: ""
                Result.success(text)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error contacting Gemini: ${e.localizedMessage ?: "Unknown error"}. Your project data is safely saved."))
        }
    }

    private fun cleanJsonString(raw: String): String {
        var trimmed = raw.trim()
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7)
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3)
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length - 3)
        }
        return trimmed.trim()
    }

    private fun parseVisualWorld(json: JSONObject): VisualWorld {
        val storyObj = json.optJSONObject("story") ?: JSONObject()
        val story = StoryArc(
            beginning = storyObj.optString("beginning", "The visual journey begins in solitude."),
            middle = storyObj.optString("middle", "Characters navigate emotional stakes in the central environment."),
            ending = storyObj.optString("ending", "A climactic convergence provides thematic resolution.")
        )

        val charactersList = mutableListOf<CharacterProfile>()
        val charArray = json.optJSONArray("characters")
        if (charArray != null) {
            for (i in 0 until charArray.length()) {
                val c = charArray.optJSONObject(i) ?: continue
                charactersList.add(
                    CharacterProfile(
                        name = c.optString("name", "Protagonist"),
                        appearance = c.optString("appearance", "Distinctive visual presence"),
                        clothing = c.optString("clothing", "Cinematic styling"),
                        personality = c.optString("personality", "Nuanced and driven"),
                        emotionalState = c.optString("emotionalState", "Vulnerable yet determined"),
                        role = c.optString("role", "Lead character"),
                        performanceBehavior = c.optString("performanceBehavior", "Expressive and grounded")
                    )
                )
            }
        }

        val envList = mutableListOf<EnvironmentProfile>()
        val envArray = json.optJSONArray("environments")
        if (envArray != null) {
            for (i in 0 until envArray.length()) {
                val e = envArray.optJSONObject(i) ?: continue
                envList.add(
                    EnvironmentProfile(
                        name = e.optString("name", "Key Location"),
                        appearance = e.optString("appearance", "Atmospheric environment"),
                        architecture = e.optString("architecture", "Stylized industrial/cinematic structures"),
                        lighting = e.optString("lighting", "High-contrast directional lighting"),
                        atmosphere = e.optString("atmosphere", "Textured ambient atmosphere"),
                        timeOfDay = e.optString("timeOfDay", "Night"),
                        importantObjects = e.optString("importantObjects", "Focal set dressing elements")
                    )
                )
            }
        }

        val vlObj = json.optJSONObject("visualLanguage") ?: JSONObject()
        val visualLanguage = VisualLanguage(
            colors = vlObj.optString("colors", "Deep dark canvas with electric cyan and amber accents"),
            lighting = vlObj.optString("lighting", "Chiaroscuro with specular highlights"),
            textures = vlObj.optString("textures", "Tactile weather-beaten surfaces"),
            productionDesign = vlObj.optString("productionDesign", "High-end cinematic realism"),
            atmosphere = vlObj.optString("atmosphere", "Suspended particles and dramatic haze")
        )

        val cineObj = json.optJSONObject("cinematography") ?: JSONObject()
        val cinematography = Cinematography(
            framing = cineObj.optString("framing", "2.39:1 widescreen anamorphic framing"),
            cameraMovement = cineObj.optString("cameraMovement", "Deliberate tracking and slow push-ins"),
            lensStyle = cineObj.optString("lensStyle", "Shallow depth of field with cinematic flare"),
            shotTypes = cineObj.optString("shotTypes", "Atmospheric establishing wides to macro intimacy"),
            cameraBehavior = cineObj.optString("cameraBehavior", "Observational and rhythmically synchronized")
        )

        val mlObj = json.optJSONObject("motionLanguage") ?: JSONObject()
        val motionLanguage = MotionLanguage(
            walking = mlObj.optString("walking", "Purposeful, heavy, naturalistic movement"),
            dancing = mlObj.optString("dancing", "Tension and release spatial choreography"),
            singing = mlObj.optString("singing", "Physically resonant and breath-synced delivery"),
            gestures = mlObj.optString("gestures", "Expressive micro-gestures"),
            interaction = mlObj.optString("interaction", "Gradual collapse of physical distance"),
            environmentalMovement = mlObj.optString("environmentalMovement", "Kinetic weather and light motion"),
            cameraMovement = mlObj.optString("cameraMovement", "Dynamic fluid camera tracking")
        )

        val motifsList = mutableListOf<String>()
        val motifsArray = json.optJSONArray("visualMotifs")
        if (motifsArray != null) {
            for (i in 0 until motifsArray.length()) {
                motifsList.add(motifsArray.optString(i))
            }
        }

        val pdObj = json.optJSONObject("performanceDirection") ?: JSONObject()
        val performanceDirection = PerformanceDirection(
            singing = pdObj.optString("singing", "Emotional lip sync with visible breathing"),
            moving = pdObj.optString("moving", "Grounded naturalism in response to the space"),
            interacting = pdObj.optString("interacting", "Direct eye contact and lingering touch"),
            reacting = pdObj.optString("reacting", "Micro-expressions reflecting lyrical tone"),
            expressingEmotion = pdObj.optString("expressingEmotion", "Vulnerability transitioning to strength")
        )

        val rulesList = mutableListOf<String>()
        val rulesArray = json.optJSONArray("continuityRules")
        if (rulesArray != null) {
            for (i in 0 until rulesArray.length()) {
                rulesList.add(rulesArray.optString(i))
            }
        }

        return VisualWorld(
            theWorld = json.optString("theWorld", "A cinematic visual world crafted for this song."),
            emotionalCore = json.optString("emotionalCore", "Emotional journey mirroring the musical composition."),
            story = story,
            characters = charactersList,
            environments = envList,
            visualLanguage = visualLanguage,
            cinematography = cinematography,
            motionLanguage = motionLanguage,
            visualMotifs = motifsList,
            performanceDirection = performanceDirection,
            continuityRules = rulesList
        )
    }

    private fun parseStoryboardScenes(array: JSONArray): List<StoryboardScene> {
        val scenes = mutableListOf<StoryboardScene>()
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            val sceneNumber = item.optInt("sceneNumber", i + 1)
            val sceneTitle = item.optString("sceneTitle", "Scene $sceneNumber")
            val storyRole = item.optString("storyRole", when (sceneNumber) {
                1 -> "Opening"
                2 -> "Introduction"
                3 -> "Development"
                4 -> "Emotional Peak"
                5 -> "Transformation / Climax"
                else -> "Resolution"
            })

            val charsList = mutableListOf<String>()
            val charsArray = item.optJSONArray("characters")
            if (charsArray != null) {
                for (c in 0 until charsArray.length()) {
                    charsList.add(charsArray.optString(c))
                }
            }

            val mpObj = item.optJSONObject("motionPlan") ?: JSONObject()
            val charMotionList = mutableListOf<String>()
            val charMotionArray = mpObj.optJSONArray("characterMotion")
            if (charMotionArray != null) {
                for (m in 0 until charMotionArray.length()) charMotionList.add(charMotionArray.optString(m))
            }

            val facialList = mutableListOf<String>()
            val facialArray = mpObj.optJSONArray("facialPerformance")
            if (facialArray != null) {
                for (m in 0 until facialArray.length()) facialList.add(facialArray.optString(m))
            }

            val interactionList = mutableListOf<String>()
            val interactionArray = mpObj.optJSONArray("interaction")
            if (interactionArray != null) {
                for (m in 0 until interactionArray.length()) interactionList.add(interactionArray.optString(m))
            }

            val envMotionList = mutableListOf<String>()
            val envMotionArray = mpObj.optJSONArray("environmentMotion")
            if (envMotionArray != null) {
                for (m in 0 until envMotionArray.length()) envMotionList.add(envMotionArray.optString(m))
            }

            val camMotionList = mutableListOf<String>()
            val camMotionArray = mpObj.optJSONArray("cameraMotion")
            if (camMotionArray != null) {
                for (m in 0 until camMotionArray.length()) camMotionList.add(camMotionArray.optString(m))
            }

            val motionPlan = MotionPlan(
                sceneSummary = mpObj.optString("sceneSummary", "Dynamic scene motion synchronized with the musical rhythm."),
                characterMotion = if (charMotionList.isNotEmpty()) charMotionList else listOf("Characters execute intentional physical staging."),
                facialPerformance = if (facialList.isNotEmpty()) facialList else listOf("Expressions mirror the lyric sentiment."),
                interaction = if (interactionList.isNotEmpty()) interactionList else listOf("Mutual eye contact and spatial framing."),
                environmentMotion = if (envMotionList.isNotEmpty()) envMotionList else listOf("Ambient atmospheric motion."),
                cameraMotion = if (camMotionList.isNotEmpty()) camMotionList else listOf("Slow tracking and steady framing."),
                timing = mpObj.optString("timing", "15s duration")
            )

            scenes.add(
                StoryboardScene(
                    sceneNumber = sceneNumber,
                    sceneTitle = sceneTitle,
                    storyRole = storyRole,
                    storyPurpose = item.optString("storyPurpose", "Progresses the emotional arc."),
                    lyricsSection = item.optString("lyricsSection", ""),
                    characters = charsList,
                    environment = item.optString("environment", "Main Location"),
                    characterActions = item.optString("characterActions", "Character moves through scene."),
                    characterInteraction = item.optString("characterInteraction", "Characters communicate through performance."),
                    performanceDirection = item.optString("performanceDirection", "Grounded, emotive performance."),
                    cameraDirection = item.optString("cameraDirection", "Medium tracking shot."),
                    lighting = item.optString("lighting", "Atmospheric key lighting."),
                    atmosphere = item.optString("atmosphere", "Haze and specular highlights."),
                    motionDirection = item.optString("motionDirection", "Kinetic and rhythmic."),
                    visualPrompt = item.optString("visualPrompt", "Cinematic music video frame, dramatic lighting."),
                    continuityRequirements = item.optString("continuityRequirements", "Preserve wardrobe and location details."),
                    motionPlan = motionPlan
                )
            )
        }
        return scenes
    }
}
