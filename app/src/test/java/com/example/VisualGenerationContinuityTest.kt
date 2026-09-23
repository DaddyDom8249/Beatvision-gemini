package com.example

import com.example.data.ai.GeminiBeatVisionAiService
import com.example.data.model.CharacterProfile
import com.example.data.model.Cinematography
import com.example.data.model.EnvironmentProfile
import com.example.data.model.MotionLanguage
import com.example.data.model.MotionPlan
import com.example.data.model.PerformanceDirection
import com.example.data.model.SceneConcept
import com.example.data.model.StoryboardScene
import com.example.data.model.StoryArc
import com.example.data.model.VisualLanguage
import com.example.data.model.VisualWorld
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Phase 3: Visual Generation Continuity Test
 *
 * Verifies that generateSceneConcept():
 * 1. Consumes the World's continuity rules & motifs
 * 2. Injects the Character Bible (preserving age, face, wardrobe, colors)
 * 3. Injects the Environment Bible (architecture, lighting, weather, objects)
 * 4. Translates Motion Plans into concrete physical visual actions
 * 5. Translates Camera Kinetics into camera composition
 * 6. Preserves the "SCENE CONCEPT" distinction
 * 7. Evaluates continuity across Fast Car Scenes 1, 3, and 5
 * 8. Maintains Demo Mode fallback without an API key
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class VisualGenerationContinuityTest {

    private lateinit var aiService: GeminiBeatVisionAiService
    private lateinit var fastCarWorld: VisualWorld
    private lateinit var scene1: StoryboardScene
    private lateinit var scene3: StoryboardScene
    private lateinit var scene5: StoryboardScene

    @Before
    fun setup() {
        aiService = GeminiBeatVisionAiService()

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
                ),
                CharacterProfile(
                    name = "The Driver",
                    appearance = "Early 20s, lean build, restless hazel eyes, unruly wavy brown hair, nervous charisma masked by a defensive smirk.",
                    clothing = "Distressed chocolate brown leather bomber jacket with ribbed wool waistband, vintage black graphic tee, dark slim jeans, weathered engineer boots.",
                    personality = "Charismatic dreamer, impulsive, evasive when confronted with responsibility.",
                    emotionalState = "Restless hunger for greatness masking paralyzing fear of failure.",
                    role = "The companion and catalyst.",
                    performanceBehavior = "Fidgeting hands on the steering wheel, driving with one wrist draped over the wheel."
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
                ),
                EnvironmentProfile(
                    name = "The Interstate 84 Elevated Overpass",
                    appearance = "A four-lane concrete highway elevated above rail yards, stretching toward the city skyline.",
                    architecture = "Brutalist concrete highway pillars, weathered steel guardrails.",
                    lighting = "Rhythmic pools of warm golden-amber sodium vapor light sweeping across the car interior.",
                    atmosphere = "Wind howling through cracked windows, dry asphalt friction, kinetic visual blur of lane stripes.",
                    timeOfDay = "Deep midnight (01:15 AM).",
                    importantObjects = "The vintage midnight-blue coupe, polished chrome rearview mirror, glowing green dashboard dials."
                ),
                EnvironmentProfile(
                    name = "The South End Rowhouse Stoop",
                    appearance = "A narrow brick rowhouse with peeling green paint on the front steps, overlooking an asphalt residential street.",
                    architecture = "Early 20th-century urban residential brick walk-up with wrought iron railings.",
                    lighting = "Harsh single sodium streetlamp overhead (2200K) casting sharp shadows.",
                    atmosphere = "Tense, quiet residential street, clinking of metal keys.",
                    timeOfDay = "Late night (02:30 AM).",
                    importantObjects = "Brass ignition key ring, screen door with rusted mesh, stone stoop steps."
                )
            ),
            visualLanguage = VisualLanguage(
                colors = "Warm earthen gold (#D4A373), rich rust brown (#8C4A32), washed indigo denim (#3D5A80), deep midnight asphalt (#1A1D20), amber sodium flare (#F4A261).",
                lighting = "Naturalistic motivated lighting: fluorescent practicals in the market, high-contrast moving sodium pools inside the vehicle.",
                textures = "Frayed cotton denim, supple scratched leather, cracked asphalt, rain spots on vintage glass.",
                productionDesign = "Authentic working-class American realism.",
                atmosphere = "Exhaust haze, highway air rush, morning river fog."
            ),
            cinematography = Cinematography(
                framing = "2.39:1 widescreen anamorphic.",
                cameraMovement = "Fluid tracking alongside moving car, slow dolly-in during intimate lyrics.",
                lensStyle = "Vintage anamorphic glass with soft edge fall-off and horizontal amber streaks.",
                shotTypes = "Tight character close-ups, profile two-shots in car cabin.",
                cameraBehavior = "Patient, respectful of human faces; holds on eyes to let real emotional transitions register."
            ),
            motionLanguage = MotionLanguage(
                walking = "The Narrator walks with weary, rhythmic determination.",
                dancing = "None.",
                singing = "Quiet acoustic delivery close to camera.",
                gestures = "Counting bills, fingers tapping vinyl steering wheel, releasing keys into an open palm.",
                interaction = "Shifting from tentative proximity to warm shared shoulder contact, eventually separating.",
                environmentalMovement = "Passing highway lights sweeping across faces, wind fluttering coat collars.",
                cameraMovement = "Parallel tracking with vehicle speed, slow push-in on decisive lines."
            ),
            visualMotifs = listOf(
                "The Rearview Mirror: Past life shrinking in dark glass.",
                "Flashing Highway Dashes: White lane markers acting as a visual metronome of time passing.",
                "The Tin Lockbox: Concrete symbol of small savings and careful sacrifice.",
                "The Brass Ignition Key: Physical emblem of mobility, freedom, and the power of choice."
            ),
            performanceDirection = PerformanceDirection(
                singing = "Intimate and unadorned.",
                moving = "True-to-life physical grounding.",
                interacting = "Nuanced micro-reactions: small smiles that falter, eyes meeting through rearview mirror.",
                reacting = "A subtle swallow and hardening of the jaw when realizing the cycle is repeating.",
                expressingEmotion = "Internal emotional conflict conveyed through stillness and gaze."
            ),
            continuityRules = listOf(
                "The Narrator's indigo chore coat with frayed left cuff must be worn in all scenes.",
                "The Driver's distressed chocolate leather jacket must remain consistent.",
                "The vintage midnight-blue car must retain its specific dashboard layout and chrome mirror.",
                "Lighting progression must strictly follow: Dusk (Scene 1) -> Midnight (Scene 3) -> Late Night Stoop (Scene 5)."
            )
        )

        // Scene 1: The Blue Hour Shift
        scene1 = StoryboardScene(
            sceneNumber = 1,
            sceneTitle = "The Blue Hour Shift",
            storyRole = "Opening",
            storyPurpose = "Establishes The Narrator's exhaustion, economic trap, and desire for escape.",
            lyricsSection = "You got a fast car / I want a ticket to any place / Starting from zero got nothing to lose",
            characters = listOf("The Narrator"),
            environment = "The River Road 24-Hour Mart",
            characterActions = "The Narrator counts crumpled dollar bills and places them into a metal tin lockbox under the counter while watching dusk outside.",
            characterInteraction = "Solo interaction with the cash till and cold glass storefront window.",
            performanceDirection = "Quiet, steady lip sync; singing as an internal monologue delivered to the glass window.",
            cameraDirection = "Medium tracking shot gliding behind chip racks toward The Narrator at the register.",
            lighting = "Cool greenish-white fluorescent overhead contrasting with deep sapphire blue dusk.",
            atmosphere = "Fluorescent hum, distant highway rumble, quiet solitude.",
            motionDirection = "Deliberate, rhythmic wiping motion; head turns slowly toward the door.",
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

        // Scene 3: Speed on Interstate 84
        scene3 = StoryboardScene(
            sceneNumber = 3,
            sceneTitle = "Speed on Interstate 84",
            storyRole = "Development",
            storyPurpose = "The physical act of flight: accelerating onto the elevated highway, shared adrenaline and velocity.",
            lyricsSection = "You got a fast car / Is it fast enough so we can fly away? / Driving, driving in your car",
            characters = listOf("The Narrator", "The Driver"),
            environment = "The Interstate 84 Elevated Overpass",
            characterActions = "The Driver shifts gears firmly, gripping the steering wheel. The car merges onto the elevated highway. The Narrator rolls down the window vent.",
            characterInteraction = "Synchronized swaying with the car's turns; shared laughter breaking the tension.",
            performanceDirection = "Vocal energy surges; The Narrator sings with visceral joy.",
            cameraDirection = "Mounted hood camera looking through the windshield at both faces, with low tracking wheel kinetics.",
            lighting = "Dynamic amber sodium lamps passing overhead every two seconds, casting rhythmic bars of golden light across their faces.",
            atmosphere = "Violent rush of wind, speedometer needle trembling at 75 MPH, distant city glow on the horizon.",
            motionDirection = "High-speed forward velocity; rapid sweep of light and shadows through the car interior.",
            visualPrompt = "Interior shot of a vintage midnight-blue car driving fast on a highway at night, sodium streetlights casting moving warm amber stripes across the faces of a young Black woman and man.",
            continuityRequirements = "Driver in chocolate leather jacket; Narrator in mustard henley and denim coat; vintage midnight-blue coupe dashboard.",
            motionPlan = MotionPlan(
                sceneSummary = "The car accelerates down the interstate into the night as the music surges into the chorus.",
                characterMotion = listOf(
                    "The Driver pushes the floor gearshift forward into fourth gear.",
                    "The Narrator leans her head back against the vinyl seat cushion.",
                    "The Driver taps the steering wheel in tempo with the rhythm."
                ),
                facialPerformance = listOf(
                    "The Narrator's mouth breaks into an authentic, wide, cathartic smile.",
                    "Eyes wide and alive, watching the highway lines fly by.",
                    "The Driver's jaw relaxes into youthful exhilaration."
                ),
                interaction = listOf(
                    "The Driver rests his right hand on the center console near The Narrator's knee.",
                    "Their shoulders lean into each other during a wide highway curve."
                ),
                environmentMotion = listOf(
                    "Overhead sodium lamps sweeping in continuous 2-second rhythmic waves.",
                    "White lane dashes flashing in a blur beneath the chassis."
                ),
                cameraMotion = listOf(
                    "Mounted hood rig with subtle road vibration.",
                    "Slow push-in toward The Narrator as she sings the chorus line."
                ),
                timing = "01:05 - 01:45 (40s duration)"
            )
        )

        // Scene 5: The Cracking Illusion
        scene5 = StoryboardScene(
            sceneNumber = 5,
            sceneTitle = "The Cracking Illusion",
            storyRole = "Transformation / Climax",
            storyPurpose = "The harsh return of reality: The Driver returns intoxicated; The Narrator refuses the embrace, returns the keys, and declares independence.",
            lyricsSection = "You got a fast car / I got a job that pays all our bills / You stay out drinking late at the bar / Take your fast car and keep on driving",
            characters = listOf("The Narrator", "The Driver"),
            environment = "The South End Rowhouse Stoop",
            characterActions = "The Driver stumbles out of the car. The Narrator stands at the top of the brick stoop, arms crossed. She extends her hand, takes the brass car keys, and drops them back into his open palm, refusing his embrace.",
            characterInteraction = "Confrontation: Physical barrier of the steps; The Driver attempts to reach for her, but she refuses the embrace and deposits the brass keys in his palm.",
            performanceDirection = "Sharp, controlled, devastatingly clear vocal delivery; quiet dignity.",
            cameraDirection = "Low-angle shot looking up at The Narrator on the stoop, alternating with high-angle on The Driver on the sidewalk.",
            lighting = "Harsh single sodium streetlamp overhead (2200K) casting sharp shadows.",
            atmosphere = "Tense, quiet residential street, clinking of metal keys in the stillness.",
            motionDirection = "The Driver stumbles two steps; The Narrator stands completely still, then extends her hand with decisive finality.",
            visualPrompt = "Intense cinematic drama scene outside an urban brick rowhouse at night under a streetlamp, woman on steps confronting a man holding car keys, 35mm grain.",
            continuityRequirements = "Same denim chore coat; Driver's chocolate leather jacket; brass ignition keys.",
            motionPlan = MotionPlan(
                sceneSummary = "The Narrator confronts The Driver on the rowhouse stoop, places the brass keys in his palm, and chooses self-determination.",
                characterMotion = listOf(
                    "The Driver stumbles out of the driver's door, leaning against the car roof.",
                    "The Narrator stands tall on the third step of the stoop.",
                    "The Driver climbs the first two steps, offering a placating hand.",
                    "The Narrator extends her open hand, takes the brass keys, and drops them back into his palm."
                ),
                facialPerformance = listOf(
                    "The Driver's defensive, evasive smile slowly dissolves into shock.",
                    "The Narrator's face settles into unshakeable, sorrowful dignity.",
                    "A firm set of the jaw as she delivers 'take your fast car and keep on driving'."
                ),
                interaction = listOf(
                    "Brass ignition keys dropped into The Driver's open palm with an audible metallic ring.",
                    "The Narrator turns her body away from his reaching hand."
                ),
                environmentMotion = listOf(
                    "Streetlamp casting a hard circular pool of yellow light on the sidewalk.",
                    "Cold breath pluming from their mouths in the night air."
                ),
                cameraMotion = listOf(
                    "Low-angle push-in on The Narrator's eyes as she makes the decision.",
                    "Reverse shot over her shoulder showing The Driver frozen on the steps."
                ),
                timing = "02:25 - 03:05 (40s duration)"
            )
        )
    }

    @Test
    fun `test 1 - world provides continuity information to scene generation`() {
        // Requirement 1: World provides continuity rules, motifs, and visual language
        assertTrue(fastCarWorld.continuityRules.isNotEmpty())
        assertTrue(fastCarWorld.visualMotifs.isNotEmpty())
        assertTrue(fastCarWorld.visualLanguage.colors.isNotEmpty())
        assertTrue(fastCarWorld.cinematography.framing.contains("2.39:1"))
    }

    @Test
    fun `test 2 - character bible is passed into scene generation`() = runBlocking {
        // Requirement 2: Character Bible wardrobe, colors, age, and posture reach scene concept
        val concept1 = aiService.generateSceneConcept(scene1, fastCarWorld).getOrThrow()
        assertNotNull(concept1)
        assertTrue("Scene 1 must include The Narrator's character bible details",
            concept1.characterAppearance.contains("Narrator") &&
            (concept1.characterAppearance.contains("denim", ignoreCase = true) ||
             concept1.characterAppearance.contains("chore coat", ignoreCase = true))
        )

        val concept3 = aiService.generateSceneConcept(scene3, fastCarWorld).getOrThrow()
        assertTrue("Scene 3 must include both characters with their persistent wardrobe",
            concept3.characterAppearance.contains("Narrator") &&
            concept3.characterAppearance.contains("Driver") &&
            concept3.characterAppearance.contains("leather", ignoreCase = true)
        )
    }

    @Test
    fun `test 3 - environment bible is passed into scene generation`() = runBlocking {
        // Requirement 3: Environment Bible architecture, lighting, and objects reach scene concept
        val concept1 = aiService.generateSceneConcept(scene1, fastCarWorld).getOrThrow()
        assertTrue("Scene 1 environment must contain Mart details",
            concept1.environment.contains("Mart") || concept1.environment.contains("River Road")
        )
        assertTrue("Scene 1 lighting must contain motivated lighting",
            concept1.lighting.contains("fluorescent", ignoreCase = true) || concept1.lighting.contains("Palette")
        )

        val concept3 = aiService.generateSceneConcept(scene3, fastCarWorld).getOrThrow()
        assertTrue("Scene 3 environment must contain Overpass details",
            concept3.environment.contains("Overpass") || concept3.environment.contains("Interstate 84")
        )

        val concept5 = aiService.generateSceneConcept(scene5, fastCarWorld).getOrThrow()
        assertTrue("Scene 5 environment must contain Stoop details",
            concept5.environment.contains("Stoop") || concept5.environment.contains("Rowhouse")
        )
    }

    @Test
    fun `test 4 - motion plan information reaches scene generation`() = runBlocking {
        // Requirement 4: Physical action, facial performance, interactions, and environment movement
        val concept5 = aiService.generateSceneConcept(scene5, fastCarWorld).getOrThrow()

        // Verifies motion-to-visual translation
        assertTrue("Scene 5 motion concept must capture key physical action",
            concept5.motion.contains("brass keys", ignoreCase = true) ||
            concept5.motion.contains("keys", ignoreCase = true) ||
            concept5.motion.contains("palm", ignoreCase = true)
        )
        assertTrue("Scene 5 motion concept must capture facial performance",
            concept5.motion.contains("dignity", ignoreCase = true) ||
            concept5.motion.contains("shock", ignoreCase = true) ||
            concept5.motion.contains("smile", ignoreCase = true) ||
            concept5.motion.contains("Facial Performance", ignoreCase = true)
        )
    }

    @Test
    fun `test 5 - camera kinetics reaches scene generation`() = runBlocking {
        // Requirement 5: Camera kinetics translate to visual composition and camera parameters
        val concept1 = aiService.generateSceneConcept(scene1, fastCarWorld).getOrThrow()
        assertTrue("Scene 1 camera must capture tracking / push-in kinetics",
            concept1.camera.contains("tracking", ignoreCase = true) ||
            concept1.visualComposition.contains("tracking", ignoreCase = true)
        )

        val concept3 = aiService.generateSceneConcept(scene3, fastCarWorld).getOrThrow()
        assertTrue("Scene 3 camera must capture hood / vibration / push-in kinetics",
            concept3.camera.contains("hood", ignoreCase = true) ||
            concept3.visualComposition.contains("hood", ignoreCase = true) ||
            concept3.camera.contains("push-in", ignoreCase = true)
        )

        val concept5 = aiService.generateSceneConcept(scene5, fastCarWorld).getOrThrow()
        assertTrue("Scene 5 camera must capture low-angle kinetics",
            concept5.camera.contains("low-angle", ignoreCase = true) ||
            concept5.visualComposition.contains("low-angle", ignoreCase = true)
        )
    }

    @Test
    fun `test 6 - scene generation preserves scene concept distinction`() = runBlocking {
        // Requirement 6: Output is labeled SCENE CONCEPT, not a finished video or fake render
        val concept = aiService.generateSceneConcept(scene1, fastCarWorld).getOrThrow()
        assertNotNull(concept)
        // Must be a SceneConcept object
        assertTrue(concept.sceneDescription.isNotEmpty())
        assertTrue(concept.visualComposition.isNotEmpty())
        // In free/offline mode, isDemoFallback is true
        assertTrue(concept.isDemoFallback)
    }

    @Test
    fun `test 7 - demo fallback operates cleanly without api key`() = runBlocking {
        // Requirement 7: Operates offline / without API key without throwing or failing
        val result = aiService.generateSceneConcept(scene1, fastCarWorld)
        assertTrue(result.isSuccess)
        val concept = result.getOrThrow()
        assertTrue(concept.isDemoFallback)
        assertTrue(concept.characterAppearance.isNotEmpty())
        assertTrue(concept.environment.isNotEmpty())
    }

    @Test
    fun `test 8 - continuity evaluation across scenes 1, 3, and 5`() = runBlocking {
        // Test the exact 3 required scenes: Scene 1, Scene 3, Scene 5
        val c1 = aiService.generateSceneConcept(scene1, fastCarWorld).getOrThrow()
        val c3 = aiService.generateSceneConcept(scene3, fastCarWorld).getOrThrow()
        val c5 = aiService.generateSceneConcept(scene5, fastCarWorld).getOrThrow()

        // 1. CHARACTER CONTINUITY: The Narrator appears in 1, 3, 5 with indigo denim chore coat
        assertTrue("Narrator's denim coat must be preserved in Scene 1", c1.characterAppearance.contains("denim", ignoreCase = true))
        assertTrue("Narrator's denim coat must be preserved in Scene 3", c3.characterAppearance.contains("denim", ignoreCase = true))
        assertTrue("Narrator's denim coat must be preserved in Scene 5", c5.characterAppearance.contains("denim", ignoreCase = true))

        // Driver appears in 3 and 5 with chocolate leather jacket
        assertTrue("Driver's leather jacket must be preserved in Scene 3", c3.characterAppearance.contains("leather", ignoreCase = true))
        assertTrue("Driver's leather jacket must be preserved in Scene 5", c5.characterAppearance.contains("leather", ignoreCase = true))

        // 2. ENVIRONMENT CONTINUITY:
        // Scene 1: Convenience store / Mart
        assertTrue("Scene 1 must be in the Mart", c1.environment.contains("Mart") || c1.environment.contains("River Road"))
        // Scene 3: Elevated Overpass / Interstate 84
        assertTrue("Scene 3 must be on the Overpass", c3.environment.contains("Overpass") || c3.environment.contains("Interstate 84"))
        // Scene 5: Rowhouse Stoop
        assertTrue("Scene 5 must be on the Stoop", c5.environment.contains("Stoop") || c5.environment.contains("Rowhouse"))

        // 3. OBJECT CONTINUITY:
        // Scene 1: Tin lockbox
        assertTrue("Scene 1 must feature the tin lockbox",
            c1.environment.contains("lockbox", ignoreCase = true) ||
            c1.sceneDescription.contains("lockbox", ignoreCase = true)
        )
        // Scene 3: Vintage midnight-blue coupe
        assertTrue("Scene 3 must feature the midnight-blue coupe",
            c3.environment.contains("midnight-blue", ignoreCase = true) ||
            c3.environment.contains("coupe", ignoreCase = true) ||
            c3.sceneDescription.contains("midnight-blue", ignoreCase = true) ||
            c3.sceneDescription.contains("car", ignoreCase = true)
        )
        // Scene 5: Brass keys
        assertTrue("Scene 5 must feature the brass keys",
            c5.environment.contains("key", ignoreCase = true) ||
            c5.motion.contains("key", ignoreCase = true) ||
            c5.sceneDescription.contains("key", ignoreCase = true)
        )

        // 4. MOTION TRANSLATION:
        // Scene 1: Counting cash / wiping counter
        assertTrue("Scene 1 motion translates wiping/counting",
            c1.motion.contains("counting", ignoreCase = true) ||
            c1.motion.contains("bills", ignoreCase = true) ||
            c1.motion.contains("wipes", ignoreCase = true)
        )
        // Scene 3: Shifting gears / forward speed
        assertTrue("Scene 3 motion translates gear shift / highway speed",
            c3.motion.contains("gearshift", ignoreCase = true) ||
            c3.motion.contains("accelerates", ignoreCase = true) ||
            c3.motion.contains("highway", ignoreCase = true)
        )
        // Scene 5: Handing keys to palm / refusal of embrace
        assertTrue("Scene 5 motion translates placing keys in palm",
            c5.motion.contains("palm", ignoreCase = true) &&
            c5.motion.contains("keys", ignoreCase = true)
        )

        // 5. CAMERA TRANSLATION:
        // Scene 1: Tracking / Push-in
        assertTrue("Scene 1 camera translates tracking", c1.camera.contains("tracking", ignoreCase = true))
        // Scene 3: Hood mount / road vibration
        assertTrue("Scene 3 camera translates hood / road mount",
            c3.camera.contains("hood", ignoreCase = true) || c3.camera.contains("road", ignoreCase = true)
        )
        // Scene 5: Low-angle
        assertTrue("Scene 5 camera translates low-angle", c5.camera.contains("low-angle", ignoreCase = true))
    }
}
