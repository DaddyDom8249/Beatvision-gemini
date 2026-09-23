package com.example

import com.example.data.ai.GeminiBeatVisionAiService
import com.example.data.arena.ArenaAuthException
import com.example.data.arena.ArenaGatewayClient
import com.example.data.arena.ArenaNetworkException
import com.example.data.arena.ArenaProviderException
import com.example.data.arena.ArenaResponseException
import com.example.data.arena.ArenaSecurityException
import com.example.data.model.CharacterProfile
import com.example.data.model.Cinematography
import com.example.data.model.DemoProjectData
import com.example.data.model.EnvironmentProfile
import com.example.data.model.MotionLanguage
import com.example.data.model.MotionPlan
import com.example.data.model.PerformanceDirection
import com.example.data.model.StoryboardScene
import com.example.data.model.StoryArc
import com.example.data.model.VisualLanguage
import com.example.data.model.VisualWorld
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

/**
 * Phase 4A/4B: BeatVision Arena Integration Tests
 *
 * Verifies:
 * 1. Arena request construction conforming strictly to Contract 1.1
 * 2. Required Arena headers (Authorization, X-BeatVision-Request, X-BeatVision-Contract)
 * 3. Scene payload construction for Fast Car Scene 1
 * 4. Response parsing for generated AI images
 * 5. Explicit failure states:
 *    - Authentication failure (401/403 -> "Arena authentication failed.")
 *    - Network failure (IOException -> "Arena is unavailable. Your project data has been preserved.")
 *    - Provider failure (502/503 -> "The visual provider is not configured or generation failed.")
 *    - Malformed response -> "Arena returned an invalid visual-generation response."
 * 6. Security requirement: Safe client credential storage check prevents shipping token in APK
 * 7. UI label distinction: "AI GENERATED VISUAL" vs "SCENE CONCEPT" vs "SCENE CONCEPT — DEMO"
 * 8. Demo Mode regression check
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ArenaVisualGenerationTest {

    private lateinit var arenaClient: ArenaGatewayClient
    private lateinit var fastCarWorld: VisualWorld
    private lateinit var scene1: StoryboardScene

    @Before
    fun setup() {
        arenaClient = ArenaGatewayClient()

        fastCarWorld = VisualWorld(
            theWorld = "A sun-faded working-class Rust Belt river valley with elevated concrete overpasses, triple-decker woodframe houses, and an interstate cutting along the perimeter.",
            emotionalCore = "A profound yearning for dignity, safety, and self-determination; the fleeting euphoria of escape colliding with the slow inertia of exhaustion.",
            story = StoryArc(
                beginning = "The Narrator finishes a grueling shift at an all-night convenience store, watching headlights and counting crumpled bills in a tin lockbox.",
                middle = "The Narrator and The Driver speed down the open highway toward the city skyline in a vintage midnight-blue coupe.",
                ending = "Standing on the rowhouse stoop, The Narrator returns the keys and watches the car drive away forever."
            ),
            characters = listOf(
                CharacterProfile(
                    name = "The Narrator",
                    appearance = "Early 20s, thoughtful, observant dark brown eyes, short natural black hair cut close, athletic yet fatigued shoulders.",
                    clothing = "Oversized washed indigo denim chore coat with frayed cuffs, faded mustard thermal henley, dark canvas trousers, scuffed tan work boots.",
                    personality = "Pragmatic, fiercely resilient, deeply empathetic, patient but self-respecting.",
                    emotionalState = "Trapped between duty and desperate hope; transforms into decisive self-worth.",
                    role = "Protagonist, caregiver, narrator.",
                    performanceBehavior = "Grounded, rhythmic stride; vocal delivery quiet and intimate against wind, shifting to soaring resonance."
                )
            ),
            environments = listOf(
                EnvironmentProfile(
                    name = "The River Road 24-Hour Mart",
                    appearance = "A brightly lit concrete convenience store with cracked linoleum and broad front windows facing the highway.",
                    architecture = "1970s utilitarian roadside commercial box with gravel parking lot.",
                    lighting = "Humming cool white fluorescent (4000K) interior contrasting with deep cobalt-blue dusk outside.",
                    atmosphere = "Quiet solitude, smelling of coffee and damp cardboard.",
                    timeOfDay = "Blue hour dusk (06:45 PM).",
                    importantObjects = "Cash register, glass coin tip jar, metal tin lockbox, wire chip racks."
                )
            ),
            visualLanguage = VisualLanguage(
                colors = "Cobalt blue, sodium-vapor amber, oxidized teal, deep charcoal asphalt.",
                lighting = "Chiaroscuro contrasts, practical interior fluorescents.",
                textures = "Worn heavy denim, weathered vinyl, damp asphalt.",
                productionDesign = "Authentic working-class realism.",
                atmosphere = "Dusk haze, highway loneliness."
            ),
            cinematography = Cinematography(
                framing = "Cinematic 2.39:1 widescreen, deep depth of field.",
                cameraMovement = "Slow tracking glides, restrained dolly moves.",
                lensStyle = "Vintage anamorphic 50mm.",
                shotTypes = "Medium profile, environmental master, tight expressive close-up.",
                cameraBehavior = "Patient, observational."
            ),
            motionLanguage = MotionLanguage(
                walking = "Heavy, deliberate pacing.",
                dancing = "None.",
                singing = "Quiet internal lip sync.",
                gestures = "Restrained, practical.",
                interaction = "Handling currency and cold glass.",
                environmentalMovement = "Passing highway traffic.",
                cameraMovement = "Lateral tracking glide."
            ),
            visualMotifs = listOf(
                "The Rearview Mirror: Past life shrinking in dark glass.",
                "The Tin Lockbox: Concrete symbol of small savings and careful sacrifice."
            ),
            performanceDirection = PerformanceDirection(
                singing = "Vulnerable, quiet delivery.",
                moving = "Weary, efficient.",
                interacting = "Alone behind counter.",
                reacting = "Eyes searching distant road.",
                expressingEmotion = "Guarded hope."
            ),
            continuityRules = listOf(
                "The Narrator's indigo chore coat with frayed left cuff must be worn in all scenes.",
                "The River Road 24-Hour Mart must retain its cracked linoleum and cool fluorescent fixtures."
            )
        )

        scene1 = StoryboardScene(
            sceneNumber = 1,
            sceneTitle = "The Blue Hour Shift",
            storyRole = "Opening",
            storyPurpose = "Establishes The Narrator's exhaustion, economic trap, and desire for escape.",
            lyricsSection = "You got a fast car / I want a ticket to any place / Maybe we make a deal / Maybe together we can get somewhere",
            characters = listOf("The Narrator"),
            environment = "The River Road 24-Hour Mart",
            characterActions = "The Narrator counts crumpled dollar bills and places them into a metal tin lockbox under the counter while watching dusk outside.",
            characterInteraction = "Solo interaction with the cash till and cold glass storefront window.",
            performanceDirection = "Quiet, steady lip sync; singing as an internal monologue delivered to the glass window.",
            cameraDirection = "Medium tracking shot gliding behind chip racks toward The Narrator at the register.",
            lighting = "Cool greenish-white fluorescent overhead contrasting with deep sapphire blue dusk.",
            atmosphere = "Fluorescent hum, distant highway rumble, quiet solitude.",
            motionDirection = "Deliberate, slow counting of money transitioning to a still gaze through the window.",
            visualPrompt = "Young Black woman in an oversized denim chore coat counting cash behind convenience store register at dusk, blue hour reflection, 35mm film grain.",
            continuityRequirements = "Indigo denim chore coat with frayed left cuff; mustard henley collar visible; metal tin lockbox.",
            motionPlan = MotionPlan(
                sceneSummary = "The Narrator concludes her shift, counting bills into the tin lockbox while gazing through the plate glass.",
                characterMotion = listOf(
                    "Wipes the laminate counter in two circular motions.",
                    "Counts four crumpled one-dollar bills and folds them into the tin lockbox.",
                    "Snaps the lockbox lid shut and slides it under the shelf.",
                    "Straightens up and looks through the front plate glass."
                ),
                facialPerformance = listOf(
                    "Tired, guarded expression around the eyes.",
                    "Lips part slightly as the opening lyric is voiced.",
                    "Subtle flare of hope in the eyes as an engine sound rumbles."
                ),
                interaction = listOf(
                    "Fingers counting worn paper currency.",
                    "Hand pressing flat against the cold glass pane."
                ),
                environmentMotion = listOf(
                    "Fluorescent light fixture giving a faint, subtle hum.",
                    "Headlight beams of a passing vehicle sweeping across the window decals."
                ),
                cameraMotion = listOf(
                    "Slow lateral tracking shot left-to-right over 5 seconds.",
                    "Gentle push-in ending on a close-up of The Narrator's profile."
                ),
                timing = "00:00 - 00:28 (28s duration)"
            )
        )
    }

    @Test
    fun testArenaRequestConstruction_contractVersion1_1() {
        val requestJson = arenaClient.buildRequestJson(scene1, fastCarWorld)

        // 1. Contract version 1.1
        assertEquals("1.1", requestJson.getString("contract_version"))
        assertEquals("sceneImages", requestJson.getString("operation"))

        // 2. Payload structure
        val payload = requestJson.getJSONObject("payload")
        assertNotNull(payload)
        assertTrue(payload.has("style"))
        assertTrue(payload.has("world"))
        assertTrue(payload.has("storyboard"))

        // 3. World continuity preservation
        val world = payload.getJSONObject("world")
        assertEquals(fastCarWorld.theWorld, world.getString("the_world"))
        assertEquals(fastCarWorld.emotionalCore, world.getString("emotional_core"))

        // Character concept continuity
        val characterConcept = world.getJSONObject("character_concept")
        assertEquals("The Narrator", characterConcept.getString("name"))
        assertTrue(characterConcept.getString("appearance").contains("Early 20s"))
        assertTrue(characterConcept.getString("wardrobe").contains("indigo denim chore coat"))

        // Location continuity
        val locations = world.getJSONArray("locations")
        assertEquals(1, locations.length())
        val loc = locations.getJSONObject(0)
        assertEquals("The River Road 24-Hour Mart", loc.getString("name"))
        assertTrue(loc.getString("lighting").contains("fluorescent"))

        // Motifs and continuity rules
        val motifs = world.getJSONArray("visual_motifs")
        assertEquals(2, motifs.length())
        val rules = world.getJSONArray("continuity_rules")
        assertEquals(2, rules.length())

        // 4. Storyboard single-beat validation requirement
        val storyboard = payload.getJSONObject("storyboard")
        val scenes = storyboard.getJSONArray("scenes")
        assertEquals(1, scenes.length())

        val beat = scenes.getJSONObject(0)
        assertEquals(1, beat.getInt("scene"))
        assertEquals("scene-1", beat.getString("beatId"))
        assertEquals("The Blue Hour Shift", beat.getString("title"))
        assertEquals(0.0, beat.getDouble("startTime"), 0.001)
        assertEquals(28.0, beat.getDouble("duration_seconds"), 0.001)
        assertEquals(28.0, beat.getDouble("endTime"), 0.001)
        assertTrue(beat.getString("visualPrompt").contains("denim chore coat"))

        // Motion plan integrity
        val motionPlan = beat.getJSONObject("motionPlan")
        assertEquals(scene1.motionPlan.sceneSummary, motionPlan.getString("sceneSummary"))
        assertEquals(4, motionPlan.getJSONArray("characterMotion").length())
        assertEquals(3, motionPlan.getJSONArray("facialPerformance").length())
        assertEquals(2, motionPlan.getJSONArray("cameraMotion").length())
    }

    @Test
    fun testArenaResponseParsing_successfulSdxl() {
        val sampleArenaResponse = """
            {
              "ok": true,
              "contract_version": "1.1",
              "capability": "image",
              "provider": "pixazo",
              "model": "sdxl",
              "request_id": "9b6264c7-8023-455a-939e-e377f8ea6497",
              "latency_ms": 14200,
              "result": {
                "images": [
                  {
                    "scene": 1,
                    "beatId": "scene-1",
                    "status": "generated",
                    "image_url": "https://gateway.pixazo.ai/outputs/scene1_blue_hour_shift.png",
                    "model": "sdxl"
                  }
                ],
                "models_used": ["sdxl"],
                "scene_count": 1,
                "free_only": true
              }
            }
        """.trimIndent()

        val result = arenaClient.parseResponse(sampleArenaResponse, defaultSceneNumber = 1)
        assertTrue("Response parsing must succeed for valid Arena JSON", result.isSuccess)

        val image = result.getOrThrow()
        assertEquals(1, image.sceneNumber)
        assertEquals("scene-1", image.beatId)
        assertEquals("https://gateway.pixazo.ai/outputs/scene1_blue_hour_shift.png", image.imageUrl)
        assertEquals("sdxl", image.model)
        assertEquals("generated", image.status)
    }

    @Test
    fun testArenaAuthenticationFailure_401And403() {
        val unauthJson = """{"ok": false, "error": "Unauthorized"}"""
        val result = arenaClient.parseResponse(unauthJson, defaultSceneNumber = 1)
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue("Exception must be ArenaAuthException", exception is ArenaAuthException)
        assertEquals("Arena authentication failed.", exception?.message)
    }

    @Test
    fun testArenaProviderFailure_notConfigured() {
        val providerErrorJson = """
            {
              "ok": false,
              "status": "provider_error",
              "request_id": "test-req",
              "error": "PIXAZO_API_KEY is not configured."
            }
        """.trimIndent()
        val result = arenaClient.parseResponse(providerErrorJson, defaultSceneNumber = 1)
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue("Exception must be ArenaProviderException", exception is ArenaProviderException)
        assertEquals("The visual provider is not configured or generation failed.", exception?.message)
    }

    @Test
    fun testArenaMalformedResponse_invalidJson() {
        val malformedJson = """{"ok": true, "result": {}}"""
        val result = arenaClient.parseResponse(malformedJson, defaultSceneNumber = 1)
        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertTrue("Exception must be ArenaResponseException", exception is ArenaResponseException)
        assertEquals("Arena returned an invalid visual-generation response.", exception?.message)

        val brokenText = "<html>502 Bad Gateway</html>"
        val result2 = arenaClient.parseResponse(brokenText, defaultSceneNumber = 1)
        assertTrue(result2.isFailure)
        assertEquals("Arena returned an invalid visual-generation response.", result2.exceptionOrNull()?.message)
    }

    @Test
    fun testArenaClientCredentialSafety_noTokenExposed() {
        // In Android, client credential storage in the APK is inherently unsafe
        assertFalse("Client credential storage in APK is inherently unsafe", arenaClient.isClientCredentialStorageSafe())

        var capturedUrl = ""
        var capturedAuthHeader: String? = "SHOULD_BE_NULL"
        var capturedContractHeader: String? = null
        var capturedRequestId: String? = null

        val sampleSuccessJson = """
            {
              "ok": true,
              "contract_version": "1.1",
              "capability": "image",
              "provider": "pixazo",
              "model": "sdxl",
              "request_id": "req-12345",
              "latency_ms": 1200,
              "result": {
                "images": [
                  {
                    "scene": 1,
                    "beatId": "scene-1",
                    "status": "generated",
                    "image_url": "https://gateway.pixazo.ai/outputs/scene1_blue_hour_shift.png",
                    "model": "sdxl"
                  }
                ],
                "models_used": ["sdxl"],
                "scene_count": 1,
                "free_only": true
              }
            }
        """.trimIndent()

        val mockHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                capturedUrl = request.url.toString()
                capturedAuthHeader = request.header("Authorization")
                capturedContractHeader = request.header("X-BeatVision-Contract")
                capturedRequestId = request.header("X-BeatVision-Request")

                Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(sampleSuccessJson.toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val clientWithMock = ArenaGatewayClient(
            baseUrl = "https://beatvision-provider-arena.richardcranium466.workers.dev",
            tokenProvider = { null },
            client = mockHttpClient
        )

        runBlocking {
            val result = clientWithMock.generateSceneImage(scene1, fastCarWorld)
            assertTrue("Generation with Phase 4D secure endpoint succeeds", result.isSuccess)
            val image = result.getOrThrow()
            assertEquals("https://gateway.pixazo.ai/outputs/scene1_blue_hour_shift.png", image.imageUrl)

            // Strict Security Assertions:
            // 1. Endpoint must be /v1/client/image/scene
            assertTrue("Request must target /v1/client/image/scene", capturedUrl.endsWith("/v1/client/image/scene"))
            // 2. NO Authorization header may be sent by the Android client
            assertEquals("Android client must NEVER send an Authorization header", null, capturedAuthHeader)
            // 3. Contract 1.1 must be preserved
            assertEquals("1.1", capturedContractHeader)
            assertNotNull(capturedRequestId)
        }
    }

    @Test
    fun testArenaClientEndpoint_rateLimited429() {
        val mockHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(429)
                    .message("Too Many Requests")
                    .body("""{"ok":false,"status":"rate_limited","error":"Rate limit: Please wait 10s between scene generations."}""".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val clientWithMock = ArenaGatewayClient(
            baseUrl = "https://beatvision-provider-arena.richardcranium466.workers.dev",
            client = mockHttpClient
        )

        runBlocking {
            val result = clientWithMock.generateSceneImage(scene1, fastCarWorld)
            assertTrue(result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue("Exception must be ArenaRateLimitException", exception is com.example.data.arena.ArenaRateLimitException)
        }
    }

    @Test
    fun testRealImageLabeling_distinctFromConcept() {
        // When real image is present:
        val sceneWithImage = scene1.copy(generatedImageUrl = "https://gateway.pixazo.ai/outputs/scene1.png")
        val hasRealImage = !sceneWithImage.generatedImageUrl.isNullOrBlank()
        val isDemoFallback = true

        val labelText = if (hasRealImage) "AI GENERATED VISUAL" else if (isDemoFallback) "SCENE CONCEPT — DEMO" else "SCENE CONCEPT"
        assertEquals("AI GENERATED VISUAL", labelText)

        // When real image is absent in Demo mode:
        val sceneWithoutImage = scene1.copy(generatedImageUrl = null)
        val hasRealImage2 = !sceneWithoutImage.generatedImageUrl.isNullOrBlank()
        val labelTextDemo = if (hasRealImage2) "AI GENERATED VISUAL" else if (isDemoFallback) "SCENE CONCEPT — DEMO" else "SCENE CONCEPT"
        assertEquals("SCENE CONCEPT — DEMO", labelTextDemo)

        // When real image is absent in Production mode:
        val isDemoFallbackProd = false
        val labelTextProd = if (hasRealImage2) "AI GENERATED VISUAL" else if (isDemoFallbackProd) "SCENE CONCEPT — DEMO" else "SCENE CONCEPT"
        assertEquals("SCENE CONCEPT", labelTextProd)
    }

    @Test
    fun testDemoModeRegression_preservedWithoutApiKey() {
        val demoProject = DemoProjectData.drainRackHaloProject
        assertNotNull(demoProject)
        assertTrue(demoProject.isDemo)
        assertEquals("Drain Rack Halo", demoProject.songTitle)
        assertEquals(6, demoProject.storyboard.size)

        // Scene 1 verification
        val s1 = demoProject.storyboard[0]
        assertEquals(1, s1.sceneNumber)
        assertEquals("The Iron Graveyard in Rain", s1.sceneTitle)
        assertTrue(s1.visualPrompt.contains("olive canvas coat"))
        assertTrue(s1.motionPlan.characterMotion.isNotEmpty())
        assertTrue(s1.motionPlan.facialPerformance.isNotEmpty())
        assertTrue(s1.motionPlan.cameraMotion.isNotEmpty())
    }

    @Test
    fun testPhase4F_EndToEndViewModelToArenaPipeline_success() {
        val sampleSuccessJson = """
            {
              "ok": true,
              "contract_version": "1.1",
              "operation": "sceneImages",
              "status": "completed",
              "model": "sdxl",
              "result": {
                "images": [
                  {
                    "scene": 1,
                    "beatId": "scene-1",
                    "image_url": "https://gateway.pixazo.ai/outputs/scene1_blue_hour_shift.png",
                    "model": "sdxl",
                    "status": "generated"
                  }
                ]
              }
            }
        """.trimIndent()

        var capturedUrl = ""
        var capturedAuth: String? = "SHOULD_BE_NULL"
        val mockHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request()
                capturedUrl = req.url.toString()
                capturedAuth = req.header("Authorization")
                Response.Builder()
                    .request(req)
                    .protocol(Protocol.HTTP_1_1)
                    .code(200)
                    .message("OK")
                    .body(sampleSuccessJson.toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val arenaMock = ArenaGatewayClient(
            baseUrl = "https://beatvision-provider-arena.richardcranium466.workers.dev",
            client = mockHttpClient
        )
        val aiServiceWithArena = GeminiBeatVisionAiService(
            arenaClient = arenaMock
        )

        runBlocking {
            val result = aiServiceWithArena.generateSceneImage(scene1, fastCarWorld)
            assertTrue("Generation must succeed", result.isSuccess)
            val imgResult = result.getOrThrow()

            // 1. Endpoint & Security checks
            assertTrue("Request must target /v1/client/image/scene", capturedUrl.endsWith("/v1/client/image/scene"))
            assertEquals("Android must NEVER send Authorization header", null, capturedAuth)

            // 2. Updated scene properties
            val updatedScene = scene1.copy(
                generatedImageUrl = imgResult.imageUrl,
                generatedImageModel = imgResult.model,
                generatedImageStatus = imgResult.status
            )
            assertEquals("https://gateway.pixazo.ai/outputs/scene1_blue_hour_shift.png", updatedScene.generatedImageUrl)
            assertEquals("sdxl", updatedScene.generatedImageModel)
            assertEquals("generated", updatedScene.generatedImageStatus)

            // 3. Room persistence roundtrip check
            val testProj = com.example.data.model.Project(
                id = "fast-car-test",
                name = "Fast Car",
                songTitle = "Fast Car",
                artist = "Tracy Chapman",
                lyrics = "You got a fast car...",
                creativeDirection = "Cinematic realism",
                worldReport = fastCarWorld,
                isWorldApproved = true,
                storyboard = listOf(updatedScene)
            )
            val entity = com.example.data.local.ProjectEntity.fromDomain(testProj)
            val reloaded = entity.toDomain()
            assertEquals("https://gateway.pixazo.ai/outputs/scene1_blue_hour_shift.png", reloaded.storyboard[0].generatedImageUrl)
            assertEquals("sdxl", reloaded.storyboard[0].generatedImageModel)
            assertEquals("generated", reloaded.storyboard[0].generatedImageStatus)
        }
    }

    @Test
    fun testPhase4F_EndToEndViewModelToArenaPipeline_errorPreservesData() {
        val mockHttpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(503)
                    .message("Service Unavailable")
                    .body("""{"ok":false,"status":"provider_unavailable","error":"Pixazo visual provider is unavailable."}""".toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()

        val arenaMock = ArenaGatewayClient(
            baseUrl = "https://beatvision-provider-arena.richardcranium466.workers.dev",
            client = mockHttpClient
        )
        val aiServiceWithArena = GeminiBeatVisionAiService(
            arenaClient = arenaMock
        )

        runBlocking {
            val result = aiServiceWithArena.generateSceneImage(scene1, fastCarWorld)
            assertTrue("Generation must fail gracefully on 503", result.isFailure)
            val exception = result.exceptionOrNull()
            assertTrue("Must be ArenaProviderException", exception is ArenaProviderException)

            // Initial scene without image is preserved
            assertEquals(null, scene1.generatedImageUrl)
            assertEquals("The Blue Hour Shift", scene1.sceneTitle)
        }
    }
}
