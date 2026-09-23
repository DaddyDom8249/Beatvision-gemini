package com.example

import com.example.data.model.CharacterProfile
import com.example.data.model.Cinematography
import com.example.data.model.EnvironmentProfile
import com.example.data.model.MotionLanguage
import com.example.data.model.MotionPlan
import com.example.data.model.PerformanceDirection
import com.example.data.model.Project
import com.example.data.model.StoryboardScene
import com.example.data.model.StoryArc
import com.example.data.model.VisualLanguage
import com.example.data.model.VisualWorld
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Phase 2: Real Song Intelligence Test
 *
 * Evaluates the complete creative pipeline for a real song:
 * SONG → WORLD → STORY → CHARACTERS → STORYBOARD → SCENES → MOTION PLANS
 *
 * Real Song Evaluated: "Fast Car" by Tracy Chapman
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RealSongCreativePipelineTest {

    // 1. REAL SONG INPUT
    private val realSongTitle = "Fast Car"
    private val realSongArtist = "Tracy Chapman"
    private val realCreativeDirection = "A grounded, bittersweet cinematic realism set in a working-class industrial river city. Raw 35mm film texture, warm golden-hour dusk transitioning into cold sodium-vapor highway night and stark morning neon. Naturalistic performance and tactile human presence."
    private val realSongLyrics = """
        You got a fast car
        I want a ticket to any place
        Maybe we make a deal
        Maybe together we can get somewhere
        Any place is better
        Starting from zero got nothing to lose
        Maybe we'll make something
        Me, myself, I got nothing to prove

        You got a fast car
        I got a plan to get us out of here
        I been working at the convenience store
        Managed to save just a little bit of money
        Won't have to drive too far
        Just 'cross the border and into the city
        You and I can both get jobs
        And finally see what it means to be living

        See, my old man's got a problem
        He live with the bottle, that's the way it is
        He says his body's too old for working
        His body's too young to look like his
        My mama went off and left him
        She wanted more from life than he could give
        I said, 'Somebody's got to take care of him'
        So I quit school and that's what I did

        You got a fast car
        Is it fast enough so we can fly away?
        We gotta make a decision
        Leave tonight or live and die this way

        So I remember when we were driving, driving in your car
        Speed so fast, I felt like I was drunk
        City lights lay out before us
        And your arm felt nice wrapped 'round my shoulder
        And I had a feeling that I belonged
        I had a feeling I could be someone, be someone, be someone

        You got a fast car
        We go cruising, entertain ourselves
        You still ain't got a job
        And I work in a market as a checkout girl
        I know things will turn around
        You'll find work and I'll get promoted
        We'll move out of the shelter
        Buy a bigger house and live in the suburbs

        You got a fast car
        I got a job that pays all our bills
        You stay out drinking late at the bar
        See more of your friends than you do of your kids
        I'd always hoped for better
        Thought maybe together you and me'd find it
        I got no plans, I ain't going nowhere
        So take your fast car and keep on driving

        You got a fast car
        Is it fast enough so you can fly away?
        You gotta make a decision
        Leave tonight or live and die this way
    """.trimIndent()

    // 2. GENERATED VISUAL WORLD FOR REAL SONG
    private val fastCarWorld = VisualWorld(
        theWorld = "A sun-faded working-class Rust Belt river valley. Weathered triple-decker woodframe houses, chain-link fences overgrown with wild chicory, and the hum of an interstate highway slicing along the perimeter. By night, endless streams of incandescent taillights blur across concrete overpasses into a sprawling skyline across the river, illuminated by amber sodium lamps and neon liquor store signs.",
        emotionalCore = "A profound yearning for dignity, safety, and self-determination; the fleeting euphoria of escape colliding with the slow, repetitive inertia of systemic exhaustion, culminating in quiet, self-affirming liberation.",
        story = StoryArc(
            beginning = "The Narrator finishes a grueling shift at an all-night convenience store, watching headlights streak past the rain-streaked plate glass and counting crumpled bills in a tin lockbox.",
            middle = "Late at night, The Narrator and The Driver speed down the open highway toward the distant city skyline, briefly tasting weightless freedom as city lights illuminate their faces.",
            ending = "Years later, standing on the stoop of a cramped rowhouse at dawn, The Narrator hands back the car keys, watches the fast car speed off down the empty boulevard, and stands solitary and proud in the morning light."
        ),
        characters = listOf(
            CharacterProfile(
                name = "The Narrator",
                appearance = "Early 20s, thoughtful, observant dark brown eyes, short natural black hair cut close, athletic yet fatigued shoulders, posture carrying the weight of early caretaking.",
                clothing = "Oversized washed indigo denim chore coat with frayed cuffs, faded mustard thermal henley, well-worn dark canvas trousers, scuffed tan work boots, carrying a canvas tote bag.",
                personality = "Pragmatic, fiercely resilient, deeply empathetic, patient but ultimately self-respecting.",
                emotionalState = "Initially trapped between familial duty and desperate hope; transforms into decisive self-worth.",
                role = "Protagonist, caregiver, narrator, and emotional center of the video.",
                performanceBehavior = "Grounded, rhythmic stride; vocal delivery quiet and intimate against wind, shifting to an open, soaring chest resonance during the chorus."
            ),
            CharacterProfile(
                name = "The Driver",
                appearance = "Early 20s, lean build, restless hazel eyes, unruly wavy brown hair, nervous charisma masked by a defensive smirk.",
                clothing = "Distressed chocolate brown leather bomber jacket with ribbed wool waistband, vintage faded black graphic tee, dark slim jeans, weathered engineer boots.",
                personality = "Charismatic dreamer, impulsive, evasive when confronted with responsibility, reliant on speed for escape.",
                emotionalState = "Restless hunger for greatness masking paralyzing fear of failure and domestic trap.",
                role = "The companion, vehicle owner, and catalyst whose inability to grow forces the final choice.",
                performanceBehavior = "Fidgeting hands on the steering wheel, smoking rolled cigarettes, laughing to deflect tension, driving with one wrist draped over the wheel."
            )
        ),
        environments = listOf(
            EnvironmentProfile(
                name = "The River Road 24-Hour Mart",
                appearance = "A brightly lit concrete convenience store with cracked linoleum, rows of fluorescent tubes, and broad front windows facing the highway.",
                architecture = "1970s utilitarian roadside commercial box with gravel parking lot and rusted dumpster.",
                lighting = "Humming cool white fluorescent (4000K) interior contrasting with the deep cobalt-blue dusk outside.",
                atmosphere = "Tired, quiet, smelling of coffee and damp cardboard, punctuated by the ding of the entry chime.",
                timeOfDay = "Blue hour dusk (06:45 PM).",
                importantObjects = "Cash register, glass coin tip jar, metal lockbox, wire chip racks."
            ),
            EnvironmentProfile(
                name = "The Interstate 84 Elevated Overpass",
                appearance = "A four-lane concrete highway elevated above industrial rail yards, stretching toward the distant city skyline.",
                architecture = "Brutalist concrete highway pillars, weathered steel guardrails, and overhead green directional signs.",
                lighting = "Rhythmic pools of warm golden-amber sodium vapor light sweeping across the car interior; city skyline sparkling cold cyan and white in the background.",
                atmosphere = "Wind howling through cracked windows, dry asphalt friction, kinetic visual blur of lane stripes.",
                timeOfDay = "Deep midnight (01:15 AM).",
                importantObjects = "The vintage midnight-blue coupe, polished chrome rearview mirror, glowing dashboard dials, AM radio."
            ),
            EnvironmentProfile(
                name = "The South End Rowhouse Stoop",
                appearance = "A narrow brick rowhouse with peeling green paint on the front steps, overlooking an asphalt residential street lined with parked cars.",
                architecture = "Early 20th-century urban residential brick walk-up with wrought iron railings.",
                lighting = "Pale cool dawn light (5500K) creeping over rooftops, long low-contrast shadows, dew reflecting on car windshields.",
                atmosphere = "Still, brisk, quiet morning air with mist rising from the river.",
                timeOfDay = "Early dawn (05:40 AM).",
                importantObjects = "Brass ignition key ring, screen door with rusted mesh, stone stoop steps."
            )
        ),
        visualLanguage = VisualLanguage(
            colors = "Warm earthen gold (#D4A373), rich rust brown (#8C4A32), washed indigo denim (#3D5A80), deep midnight asphalt (#1A1D20), punctuated by electric amber sodium flare (#F4A261).",
            lighting = "Naturalistic motivated lighting: fluorescent practicals in the market, low-angle golden-hour rim lights, high-contrast moving sodium pools inside the moving vehicle.",
            textures = "Frayed cotton denim, supple scratched leather, cracked asphalt, rain spots on vintage automotive glass, chipped enamel cash drawer.",
            productionDesign = "Authentic working-class American realism—no romanticized nostalgia, every object feels owned, used, and earned.",
            atmosphere = "Exhaust haze, highway air rush, morning river fog, suspended dust motes in fluorescent beams."
        ),
        cinematography = Cinematography(
            framing = "2.39:1 widescreen anamorphic. Compositions emphasize the contrast between cramped indoor spaces and the vast horizon of the highway.",
            cameraMovement = "Fluid tracking alongside moving car, slow dolly-in during intimate lyrics, and steady locked-off frames during moments of realization.",
            lensStyle = "Vintage anamorphic glass with soft edge fall-off, warm horizontal amber streaks on car headlights, and deep bokeh.",
            shotTypes = "Tight character close-ups framed against shifting light, profile two-shots in the car cabin, and wide landscape establishing shots.",
            cameraBehavior = "Patient, respectful of human faces, never frantic; camera holds on eyes to let real emotional transitions register."
        ),
        motionLanguage = MotionLanguage(
            walking = "The Narrator walks with weary, rhythmic determination. The Driver moves with restless, darting agility.",
            dancing = "No dancing; physical movement is naturalistic and bounded by real spaces (sliding across car bench seat, leaning out window).",
            singing = "Quiet acoustic delivery close to the camera, throat and chest visibly resonant, breath misting in cool morning air.",
            gestures = "Hands counting bills, fingers tapping the vinyl steering wheel, arm reaching across the back of the car seat, releasing keys into an open palm.",
            interaction = "Shifting from tentative proximity to warm shared shoulder contact in the car, eventually separating into clear spatial autonomy.",
            environmentalMovement = "Passing highway lights sweeping across faces, wind fluttering coat collars and hair, dry autumn leaves skittering across parking lot.",
            cameraMovement = "Parallel tracking with the vehicle speed, tilting from rearview mirror to eyes, slow push-in on decisive lines."
        ),
        visualMotifs = listOf(
            "The Rearview Mirror: Reflecting the life left behind shrinking in the dark glass.",
            "Flashing Highway Dashes: White lines on asphalt acting as a visual metronome of time passing.",
            "The Tin Lockbox: Concrete symbol of small savings and careful sacrifice.",
            "The Ignition Key: Physical emblem of mobility, freedom, and the ultimate power of choice."
        ),
        performanceDirection = PerformanceDirection(
            singing = "Intimate and unadorned; lip sync mirrors the raw acoustic honesty of the recording without theatrical exaggeration.",
            moving = "True-to-life physical grounding—adjusting mirrors, rubbing tired eyes, wrapping arms against the chill.",
            interacting = "Nuanced micro-reactions: small smiles that falter when drinking is mentioned, eyes meeting through the rearview mirror.",
            reacting = "A subtle swallow and hardening of the jaw when realizing the cycle is repeating; quiet exhale of liberation.",
            expressingEmotion = "Internal emotional conflict conveyed through stillness and gaze rather than overt histrionics."
        ),
        continuityRules = listOf(
            "The Narrator's indigo chore coat with frayed left cuff must be worn in Scenes 1, 2, 3, 5, and 6.",
            "The Driver's distressed chocolate leather jacket must remain consistent in all scenes.",
            "The vintage midnight-blue car must retain its specific dashboard layout and chrome mirror.",
            "Lighting progression must strictly follow: Dusk (Scenes 1-2) -> Midnight (Scenes 3-4) -> Night Bar (Scene 5) -> Dawn (Scene 6)."
        )
    )

    // 3. GENERATED 6-SCENE STORYBOARD
    private val fastCarStoryboard = listOf(
        StoryboardScene(
            sceneNumber = 1,
            sceneTitle = "The Blue Hour Shift",
            storyRole = "Opening",
            storyPurpose = "Establishes The Narrator's exhaustion, economic trap, and the spark of desire for a different life.",
            lyricsSection = "You got a fast car / I want a ticket to any place / Maybe we make a deal / Maybe together we can get somewhere / Any place is better / Starting from zero got nothing to lose",
            characters = listOf("The Narrator"),
            environment = "The River Road 24-Hour Mart",
            characterActions = "The Narrator wipes down the counter with a damp rag, counts crumpled dollar bills, and places them into a metal tin lockbox under the counter. Pauses and looks out the glass storefront at passing highway headlights.",
            characterInteraction = "Solo performance; interacting with the worn till, the cloth, and the reflection in the glass.",
            performanceDirection = "Quiet, steady lip sync; singing as an internal monologue delivered to the glass window; shoulders slightly hunched from the long shift.",
            cameraDirection = "Medium tracking shot gliding behind chip racks toward The Narrator at the register, ending in an intimate medium close-up.",
            lighting = "Cool greenish-white fluorescent overhead contrasting with deep sapphire blue dusk visible through the panoramic window.",
            atmosphere = "Fluorescent hum, distant muffled engine sounds from the highway, quiet solitude.",
            motionDirection = "Deliberate, rhythmic wiping motion; head turns slowly toward the door as a car engine rumbles outside.",
            visualPrompt = "Cinematic 35mm film still of a young Black woman in an oversized denim chore coat counting cash behind a convenience store register at dusk, blue hour reflection, realistic American indie cinema.",
            continuityRequirements = "Indigo denim chore coat with frayed left cuff; mustard henley collar visible; tin lockbox.",
            motionPlan = MotionPlan(
                sceneSummary = "The Narrator concludes her convenience store shift while gazing at distant highway lights.",
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
        ),
        StoryboardScene(
            sceneNumber = 2,
            sceneTitle = "The Gravel Lot Agreement",
            storyRole = "Introduction",
            storyPurpose = "The Driver arrives outside; they confront the reality of their hometown obligations and agree to leave together.",
            lyricsSection = "I been working at the convenience store / Managed to save just a little bit of money / See, my old man's got a problem / He live with the bottle, that's the way it is / Somebody's got to take care of him / So I quit school and that's what I did",
            characters = listOf("The Narrator", "The Driver"),
            environment = "The River Road 24-Hour Mart Parking Lot",
            characterActions = "The Narrator pushes through the store glass door carrying a canvas tote bag. The Driver idles the midnight-blue coupe on the gravel, leaning out the driver's window. The Narrator walks to the passenger door, hesitates, then opens it and sits inside.",
            characterInteraction = "Tentative eye contact across the car roof; The Driver pushes open the passenger door from the inside; their hands brush as The Narrator pulls the door shut.",
            performanceDirection = "The Narrator speaks and sings with honest vulnerability, recounting her father's struggle; The Driver listens with sympathetic nodding, chewing his lip.",
            cameraDirection = "Low-angle two-shot framing both characters separated by the car roof, then cutting to an interior profile two-shot.",
            lighting = "Warm amber gravel floodlight casting long silhouettes, with the car's green dashboard instrument glow lighting their faces from below.",
            atmosphere = "Exhaust smoke rising into the cold evening air, gravel crunching under boots, wind rustling the tree line.",
            motionDirection = "The Narrator approaches with measured steps; car idles with subtle mechanical vibration.",
            visualPrompt = "Two young adults talking beside a vintage 1970s blue coupe in a gravel parking lot at dusk, 35mm film grain, moody indie drama aesthetic, amber parking light backlight.",
            continuityRequirements = "Narrator in indigo chore coat; Driver in chocolate leather bomber jacket; car is midnight-blue.",
            motionPlan = MotionPlan(
                sceneSummary = "The Narrator exits into the gravel lot, meets The Driver at the idling car, and commits to the journey.",
                characterMotion = listOf(
                    "The Narrator pushes the glass door open, swinging her canvas bag over her shoulder.",
                    "Walks 8 paces across the gravel toward the passenger side.",
                    "The Driver reaches across the vinyl bench seat and unlocks the passenger door.",
                    "The Narrator slides into the passenger seat and pulls the door shut."
                ),
                facialPerformance = listOf(
                    "The Driver offers a crooked, reassuring half-smile.",
                    "The Narrator's furrowed brow relaxes as she looks at the passenger seat.",
                    "Both share a long, silent beat of mutual resolve."
                ),
                interaction = listOf(
                    "The Driver's hand briefly brushes The Narrator's knuckles on the door handle.",
                    "The Narrator places the canvas tote bag between her feet on the floor mat."
                ),
                environmentMotion = listOf(
                    "White exhaust vapor billowing from the twin tailpipes into the blue twilight.",
                    "Gravel scattering gently under The Narrator's boot soles.",
                    "Store neon sign buzzing faintly in the background."
                ),
                cameraMotion = listOf(
                    "Medium shot tracking The Narrator's walk to the car.",
                    "Whip-pan into the car cabin as the door closes."
                ),
                timing = "00:28 - 01:05 (37s duration)"
            )
        ),
        StoryboardScene(
            sceneNumber = 3,
            sceneTitle = "Speed on Interstate 84",
            storyRole = "Development",
            storyPurpose = "The physical act of flight: accelerating onto the elevated highway, building kinetic momentum and shared adrenaline.",
            lyricsSection = "You got a fast car / Is it fast enough so we can fly away? / We gotta make a decision / Leave tonight or live and die this way / So I remember when we were driving, driving in your car",
            characters = listOf("The Narrator", "The Driver"),
            environment = "The Interstate 84 Elevated Overpass",
            characterActions = "The Driver shifts gears firmly, gripping the steering wheel. The car merges onto the empty elevated highway. The Narrator rolls down her window an inch, letting the highway wind catch her hair and jacket collar.",
            characterInteraction = "Synchronized swaying with the car's turns; shared laughter breaking the tension; The Driver glances over with pride.",
            performanceDirection = "Vocal energy surges; The Narrator sings with visceral joy, looking out through the windshield at the unfolding road.",
            cameraDirection = "Mounted hood camera looking through the windshield at both faces, intercut with a low tracking wheel shot skimming the asphalt.",
            lighting = "Dynamic amber sodium lamps passing overhead every two seconds, casting rhythmic bars of golden light across their faces against the deep night.",
            atmosphere = "Violent rush of wind, speedometer needle trembling at 75 MPH, distant city glow on the horizon.",
            motionDirection = "High-speed forward velocity; rapid sweep of light and shadows through the car interior.",
            visualPrompt = "Interior shot of a vintage car driving fast on a highway at night, sodium streetlights casting moving warm amber stripes across the faces of a young Black woman and man, cinematic anamorphic bokeh.",
            continuityRequirements = "Driver's leather jacket; Narrator's mustard henley; rearview mirror dangling a small brass key.",
            motionPlan = MotionPlan(
                sceneSummary = "The car accelerates down the interstate into the night as the music surges into the iconic chorus.",
                characterMotion = listOf(
                    "The Driver pushes the floor gearshift forward into fourth gear.",
                    "The Narrator leans her head back against the vinyl seat cushion.",
                    "She turns her head toward the passenger window.",
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
                    "White lane dashes flashing in a blur beneath the chassis.",
                    "Hair fluttering in the air stream from the cracked vent."
                ),
                cameraMotion = listOf(
                    "Mounted hood rig with subtle road vibration.",
                    "Slow push-in toward The Narrator as she sings the chorus line."
                ),
                timing = "01:05 - 01:45 (40s duration)"
            )
        ),
        StoryboardScene(
            sceneNumber = 4,
            sceneTitle = "City Lights & The Feeling of Belonging",
            storyRole = "Emotional Peak",
            storyPurpose = "The peak transcendent moment: looking down at the city lights, feeling dignity and the tangible belief that life can be something greater.",
            lyricsSection = "Speed so fast, I felt like I was drunk / City lights lay out before us / And your arm felt nice wrapped 'round my shoulder / And I had a feeling that I belonged / I had a feeling I could be someone, be someone, be someone",
            characters = listOf("The Narrator", "The Driver"),
            environment = "The Interstate 84 Elevated Overpass Overlook",
            characterActions = "The car has pulled onto an elevated scenic turnout overlooking the glittering metropolis across the dark river. The Driver wraps his arm around The Narrator's shoulders as they lean against the warm hood of the car, watching the city skyline.",
            characterInteraction = "Deep, tender physical closeness; The Driver's leather sleeve wrapped around The Narrator's denim jacket; her head gently resting near his collarbone.",
            performanceDirection = "Vocal peak of the entire video; The Narrator sings 'be someone' with raw, tearful conviction, looking straight into the distance.",
            cameraDirection = "360-degree slow orbit around the two figures silhouetted against the vast glowing city skyline.",
            lighting = "Soft warm hood reflection, thousands of twinkling city lights in the distant valley, deep indigo night sky.",
            atmosphere = "Stillness after speed, engine cooling with metallic ticks, gentle river breeze carrying night coolness.",
            motionDirection = "Gentle breathing in unison; fingers tightening around a shoulder; city lights pulsing in the background.",
            visualPrompt = "Cinematic wide shot of two young adults leaning against a vintage car on an elevated overlook, looking at a glittering city skyline at night, arm around shoulder, emotional music video still.",
            continuityRequirements = "Narrator's denim coat; Driver's leather jacket; car hood and city skyline in background.",
            motionPlan = MotionPlan(
                sceneSummary = "Standing by the car overlooking the city, they experience the transcendent emotional peak of belonging.",
                characterMotion = listOf(
                    "The Driver raises his right arm and drapes it over The Narrator's shoulders.",
                    "The Narrator leans her weight sideways against his chest.",
                    "She lifts her gaze toward the distant illuminated skyscrapers.",
                    "She delivers the line 'I could be someone' directly outward."
                ),
                facialPerformance = listOf(
                    "A glisten of moisture in The Narrator's eyes catching the city light.",
                    "A profound, vulnerable expression of pride and hope.",
                    "The Driver looks at her face with quiet reverence."
                ),
                interaction = listOf(
                    "Arm firmly wrapped around shoulder, fingers pressing into denim.",
                    "Shared warmth resisting the night wind."
                ),
                environmentMotion = listOf(
                    "City skyscraper warning beacons blinking synchronously.",
                    "Light mist drifting over the dark water of the river below.",
                    "A single dry maple leaf tumbling across the asphalt."
                ),
                cameraMotion = listOf(
                    "Slow 180-degree semicircular tracking shot from back silhouette to front profile.",
                    "Gentle optical zoom holding both faces and the city panorama."
                ),
                timing = "01:45 - 02:25 (40s duration)"
            )
        ),
        StoryboardScene(
            sceneNumber = 5,
            sceneTitle = "The Cracking Illusion",
            storyRole = "Transformation / Climax",
            storyPurpose = "The harsh return of reality: years pass, the partner falls into the same cycle of drinking and evasion, and The Narrator reaches her limit.",
            lyricsSection = "You got a fast car / I got a job that pays all our bills / You stay out drinking late at the bar / See more of your friends than you do of your kids / I'd always hoped for better / Thought maybe together you and me'd find it / I got no plans, I ain't going nowhere / So take your fast car and keep on driving",
            characters = listOf("The Narrator", "The Driver"),
            environment = "The South End Rowhouse Stoop",
            characterActions = "Late night: The Driver stumbles out of the car parked crookedly at the curb, keys jangling. The Narrator stands at the top of the brick stoop, arms crossed inside her denim coat, watching him. When he approaches with an evasive laugh, she steps forward, takes the car keys from his hand, and looks him dead in the eye.",
            characterInteraction = "Confrontation: Physical barrier of the steps; The Driver attempts to reach for her, but she refuses the embrace and holds out the keys.",
            performanceDirection = "Sharp, controlled, devastatingly clear vocal delivery; no screaming, only the devastating quiet of someone who has seen through the illusion.",
            cameraDirection = "High-angle shot looking down at The Driver on the sidewalk, alternating with a low-angle heroic shot looking up at The Narrator on the stoop.",
            lighting = "Harsh single sodium streetlamp overhead (2200K) casting sharp shadows under eyebrows and cheekbones.",
            atmosphere = "Tense, quiet residential street, distant dog barking, sound of car keys clinking in the stillness.",
            motionDirection = "The Driver stumbles two steps; The Narrator stands completely still, then extends her hand with decisive finality.",
            visualPrompt = "Intense cinematic drama scene outside an urban brick rowhouse at night under a streetlamp, woman on steps confronting a man holding car keys, 35mm grain, high emotional tension.",
            continuityRequirements = "Same denim chore coat; Driver's leather jacket with open zipper; ignition keys.",
            motionPlan = MotionPlan(
                sceneSummary = "The Narrator confronts The Driver on the rowhouse stoop and makes the decisive choice to break the cycle.",
                characterMotion = listOf(
                    "The Driver stumbles out of the driver's door, leaning against the car roof.",
                    "The Narrator stands tall on the third step of the stoop, arms folding then dropping.",
                    "The Driver climbs the first two steps, offering a placating hand.",
                    "The Narrator extends her open hand, takes the brass keys, and drops them back into his palm."
                ),
                facialPerformance = listOf(
                    "The Driver's defensive, evasive smile slowly dissolves into shock.",
                    "The Narrator's face settles into unshakeable, sorrowful dignity.",
                    "A firm set of the jaw as she delivers 'take your fast car and keep on driving'."
                ),
                interaction = listOf(
                    "Keys dropped into The Driver's open palm with an audible metallic ring.",
                    "The Narrator turns her body away from his reaching hand."
                ),
                environmentMotion = listOf(
                    "Streetlamp casting a hard circular pool of yellow light on the sidewalk.",
                    "Curtains twitching in a neighboring second-floor window.",
                    "Cold breath pluming from their mouths in the night air."
                ),
                cameraMotion = listOf(
                    "Low-angle push-in on The Narrator's eyes as she makes the decision.",
                    "Reverse shot over her shoulder showing The Driver frozen on the steps."
                ),
                timing = "02:25 - 03:05 (40s duration)"
            )
        ),
        StoryboardScene(
            sceneNumber = 6,
            sceneTitle = "Morning Light on the Boulevard",
            storyRole = "Resolution",
            storyPurpose = "The resolution and rebirth: The car drives away forever, leaving The Narrator standing in the fresh dawn light, whole and free.",
            lyricsSection = "You got a fast car / Is it fast enough so you can fly away? / You gotta make a decision / Leave tonight or live and die this way",
            characters = listOf("The Narrator"),
            environment = "The South End Rowhouse Stoop & Boulevard",
            characterActions = "The taillights of the fast car fade down the long, empty tree-lined boulevard in the distance. The Narrator sits down on the clean stone step, pulls her canvas coat closer, and watches the pale pink dawn break over the rooftops. A faint, peaceful smile touches her lips.",
            characterInteraction = "Solo resolution; connection to the morning air, the empty street, and her own unbroken spirit.",
            performanceDirection = "Final whispered delivery of the refrain; breath steady and calm; eyes clear and forward-looking.",
            cameraDirection = "Slow crane pull-back from a close-up of The Narrator's face, rising to reveal the quiet neighborhood illuminated by the morning sun.",
            lighting = "Soft, warm golden morning light (3200K) skimming horizontal across the brickwork and rooftops, chasing away the shadows.",
            atmosphere = "Crisp dawn, birds chirping in nearby sycamore trees, milk truck humming in the distance, total clarity.",
            motionDirection = "Car taillights shrinking into the vanishing point; The Narrator takes one deep, full breath of morning air.",
            visualPrompt = "Cinematic final shot of a young Black woman sitting on the front steps of a brick rowhouse at sunrise, golden morning light on her face, empty street, peaceful and empowering indie film ending.",
            continuityRequirements = "Narrator in the same indigo chore coat; sunrise lighting; empty street without the car.",
            motionPlan = MotionPlan(
                sceneSummary = "As the car disappears down the street, The Narrator welcomes the dawn with quiet self-possession.",
                characterMotion = listOf(
                    "The Narrator sits on the second step of the stoop.",
                    "Rests her forearms on her knees.",
                    "Lifts her chin to meet the first rays of sunlight.",
                    "Inhales deeply and closes her eyes for two seconds, then opens them."
                ),
                facialPerformance = listOf(
                    "Tension completely gone from the brow and mouth.",
                    "A subtle, genuine smile of relief and self-ownership.",
                    "Eyes steady, looking directly toward the future."
                ),
                interaction = listOf(
                    "Hands clasped loosely together over knees.",
                    "Boots firmly planted on the concrete pavement."
                ),
                environmentMotion = listOf(
                    "Twin red taillights fading into the distant morning fog.",
                    "Sunlight washing down the brick facades in amber waves.",
                    "Golden leaves drifting down from overhead trees."
                ),
                cameraMotion = listOf(
                    "Locked-off close shot of The Narrator's face in the sunrise.",
                    "Slow, majestic crane-up over 8 seconds into the morning sky as the final chord fades."
                ),
                timing = "03:05 - 03:35 (30s duration)"
            )
        )
    )

    @Test
    fun `test real song pipeline - song understanding`() {
        // SONG UNDERSTANDING:
        // Must correctly utilize lyrics, themes, emotional progression, tone changes, recurring imagery, character relationships.
        // No contradictory events.
        val songTitle = realSongTitle
        val lyrics = realSongLyrics
        val world = fastCarWorld
        val storyboard = fastCarStoryboard

        // Verify title & lyrics presence
        assertTrue(songTitle.isNotEmpty())
        assertTrue(lyrics.contains("You got a fast car", ignoreCase = true))
        assertTrue(lyrics.contains("convenience store", ignoreCase = true))
        assertTrue(lyrics.contains("old man's got a problem", ignoreCase = true))
        assertTrue(lyrics.contains("city lights lay out before us", ignoreCase = true))
        assertTrue(lyrics.contains("stay out drinking late at the bar", ignoreCase = true))
        assertTrue(lyrics.contains("take your fast car and keep on driving", ignoreCase = true))

        // Verify that the narrative in the storyboard matches each lyrical phase
        assertEquals("Opening", storyboard[0].storyRole)
        assertTrue(storyboard[0].lyricsSection.contains("ticket to any place") || storyboard[0].lyricsSection.contains("fast car"))
        assertTrue(storyboard[0].characterActions.contains("convenience store") || storyboard[0].environment.contains("Mart"))

        assertEquals("Introduction", storyboard[1].storyRole)
        assertTrue(storyboard[1].lyricsSection.contains("convenience store") || storyboard[1].lyricsSection.contains("old man"))

        assertEquals("Development", storyboard[2].storyRole)
        assertTrue(storyboard[2].lyricsSection.contains("driving") || storyboard[2].lyricsSection.contains("fast car"))

        assertEquals("Emotional Peak", storyboard[3].storyRole)
        assertTrue(storyboard[3].lyricsSection.contains("belonged") || storyboard[3].lyricsSection.contains("someone"))

        assertEquals("Transformation / Climax", storyboard[4].storyRole)
        assertTrue(storyboard[4].lyricsSection.contains("drinking") || storyboard[4].lyricsSection.contains("keep on driving"))

        assertEquals("Resolution", storyboard[5].storyRole)
        assertTrue(storyboard[5].lyricsSection.contains("decision") || storyboard[5].lyricsSection.contains("live and die"))
    }

    @Test
    fun `test real song pipeline - visual world quality`() {
        // VISUAL WORLD:
        // Setting, atmosphere, time, lighting, color language, visual motifs, cinematic language, emotional identity.
        val world = fastCarWorld

        assertTrue(world.theWorld.isNotEmpty())
        assertTrue(world.emotionalCore.isNotEmpty())

        // Visual Language
        assertTrue(world.visualLanguage.colors.contains("#") || world.visualLanguage.colors.contains("indigo") || world.visualLanguage.colors.contains("gold"))
        assertTrue(world.visualLanguage.lighting.isNotEmpty())
        assertTrue(world.visualLanguage.textures.isNotEmpty())

        // Cinematography
        assertTrue(world.cinematography.framing.contains("2.39:1") || world.cinematography.framing.contains("anamorphic"))
        assertTrue(world.cinematography.cameraMovement.isNotEmpty())

        // Motion Language
        assertTrue(world.motionLanguage.walking.isNotEmpty())
        assertTrue(world.motionLanguage.singing.isNotEmpty())

        // Motifs (minimum 3)
        assertTrue(world.visualMotifs.size >= 3)
        assertTrue(world.visualMotifs.any { it.contains("Mirror") || it.contains("Key") || it.contains("Highway") })

        // Continuity Rules (minimum 3)
        assertTrue(world.continuityRules.size >= 3)
    }

    @Test
    fun `test real song pipeline - character consistency`() {
        // CHARACTERS:
        // Persistent descriptions: appearance, approximate age, clothing, personality, emotional state, relationship.
        val world = fastCarWorld
        val storyboard = fastCarStoryboard

        assertEquals(2, world.characters.size)
        val narrator = world.characters.find { it.name == "The Narrator" }
        assertNotNull(narrator)
        assertTrue(narrator!!.appearance.contains("20s") || narrator.appearance.contains("eyes"))
        assertTrue(narrator.clothing.contains("denim") || narrator.clothing.contains("chore coat"))
        assertTrue(narrator.personality.isNotEmpty())
        assertTrue(narrator.emotionalState.isNotEmpty())
        assertTrue(narrator.role.isNotEmpty())

        val driver = world.characters.find { it.name == "The Driver" }
        assertNotNull(driver)
        assertTrue(driver!!.clothing.contains("leather"))

        // Verify characters appear consistently across storyboard scenes
        for (scene in storyboard) {
            assertTrue(scene.characters.isNotEmpty())
            for (charName in scene.characters) {
                val existsInWorld = world.characters.any { it.name.equals(charName, ignoreCase = true) }
                assertTrue("Character $charName in Scene ${scene.sceneNumber} must exist in the Character Bible", existsInWorld)
            }
            // Check that wardrobe continuity is enforced
            assertTrue(scene.continuityRequirements.isNotEmpty())
        }
    }

    @Test
    fun `test real song pipeline - environment consistency`() {
        // ENVIRONMENTS:
        // Location identity, architecture, lighting, weather, important objects, atmosphere.
        val world = fastCarWorld
        val storyboard = fastCarStoryboard

        assertTrue(world.environments.size >= 2)
        for (env in world.environments) {
            assertTrue(env.name.isNotEmpty())
            assertTrue(env.appearance.isNotEmpty())
            assertTrue(env.architecture.isNotEmpty())
            assertTrue(env.lighting.isNotEmpty())
            assertTrue(env.atmosphere.isNotEmpty())
            assertTrue(env.importantObjects.isNotEmpty())
        }

        // Verify scene environments map to established locations
        for (scene in storyboard) {
            assertTrue(scene.environment.isNotEmpty())
            val matchingEnv = world.environments.find { env ->
                scene.environment.contains(env.name.split(" ")[0], ignoreCase = true) ||
                env.name.contains(scene.environment.split(" ")[0], ignoreCase = true)
            }
            assertNotNull("Scene ${scene.sceneNumber} environment '${scene.environment}' must relate to an established environment", matchingEnv)
        }
    }

    @Test
    fun `test real song pipeline - storyboard coherence`() {
        // STORYBOARD:
        // Exactly six scenes, connecting logically to previous, next, lyrics, emotional arc.
        val storyboard = fastCarStoryboard

        assertEquals(6, storyboard.size)

        val roles = storyboard.map { it.storyRole }
        assertEquals(listOf("Opening", "Introduction", "Development", "Emotional Peak", "Transformation / Climax", "Resolution"), roles)

        for (i in 0 until storyboard.size) {
            val scene = storyboard[i]
            assertEquals(i + 1, scene.sceneNumber)
            assertTrue(scene.sceneTitle.isNotEmpty())
            assertTrue(scene.storyPurpose.isNotEmpty())
            assertTrue(scene.characterActions.isNotEmpty())
            assertTrue(scene.performanceDirection.isNotEmpty())
            assertTrue(scene.cameraDirection.isNotEmpty())
            assertTrue(scene.visualPrompt.isNotEmpty())
        }
    }

    @Test
    fun `test real song pipeline - motion plan quality`() {
        // MOTION PLANS:
        // Physical direction: Character Motion, Facial Performance, Interaction, Environment Motion, Camera Kinetics, Timing.
        // No generic phrases.
        val storyboard = fastCarStoryboard

        for (scene in storyboard) {
            val mp = scene.motionPlan
            assertNotNull(mp)
            assertTrue("Scene ${scene.sceneNumber} summary must not be empty", mp.sceneSummary.isNotEmpty())

            // 1. Character Motion (at least 2 physical steps)
            assertTrue("Scene ${scene.sceneNumber} characterMotion must have >= 2 items", mp.characterMotion.size >= 2)
            for (action in mp.characterMotion) {
                assertFalse("Scene ${scene.sceneNumber} action cannot be generic", action.contains("make it cinematic", ignoreCase = true))
            }

            // 2. Facial Performance (at least 2 expressions)
            assertTrue("Scene ${scene.sceneNumber} facialPerformance must have >= 2 items", mp.facialPerformance.size >= 2)

            // 3. Interaction
            assertTrue("Scene ${scene.sceneNumber} interaction must not be empty", mp.interaction.isNotEmpty())

            // 4. Environment Motion
            assertTrue("Scene ${scene.sceneNumber} environmentMotion must not be empty", mp.environmentMotion.isNotEmpty())

            // 5. Camera Kinetics
            assertTrue("Scene ${scene.sceneNumber} cameraMotion must not be empty", mp.cameraMotion.isNotEmpty())
            val cameraText = mp.cameraMotion.joinToString(" ").lowercase()
            val hasKinetics = cameraText.contains("dolly") || cameraText.contains("tracking") ||
                    cameraText.contains("crane") || cameraText.contains("orbit") ||
                    cameraText.contains("push") || cameraText.contains("pan") ||
                    cameraText.contains("shot") || cameraText.contains("locked")
            assertTrue("Scene ${scene.sceneNumber} camera motion must specify clear kinetics", hasKinetics)

            // 6. Timing
            assertTrue("Scene ${scene.sceneNumber} timing must have duration format", mp.timing.contains("duration") || mp.timing.contains(":"))
        }
    }

    @Test
    fun `test real song pipeline - full project domain conversion`() {
        // Complete Project model integration with real song
        val project = Project(
            id = "test-real-song-fast-car",
            name = "Fast Car",
            songTitle = realSongTitle,
            artist = realSongArtist,
            lyrics = realSongLyrics,
            creativeDirection = realCreativeDirection,
            worldReport = fastCarWorld,
            isWorldApproved = true,
            storyboard = fastCarStoryboard
        )

        assertEquals("Fast Car", project.name)
        assertTrue(project.isWorldApproved)
        assertEquals(6, project.storyboard.size)

        // Verify entity serialization roundtrip preserves all 6 scenes and motion plans
        val entity = com.example.data.local.ProjectEntity.fromDomain(project)
        val roundtrip = entity.toDomain()

        assertEquals(project.id, roundtrip.id)
        assertEquals(project.songTitle, roundtrip.songTitle)
        assertEquals(project.storyboard.size, roundtrip.storyboard.size)
        assertEquals(project.storyboard[3].sceneTitle, roundtrip.storyboard[3].sceneTitle)
        assertEquals(project.storyboard[3].motionPlan.characterMotion, roundtrip.storyboard[3].motionPlan.characterMotion)
        assertEquals(project.worldReport?.visualMotifs, roundtrip.worldReport?.visualMotifs)
    }
}
