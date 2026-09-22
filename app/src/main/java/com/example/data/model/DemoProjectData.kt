package com.example.data.model

object DemoProjectData {
    val drainRackHaloProject: Project by lazy {
        val world = VisualWorld(
            theWorld = "A sprawling, rain-drenched automotive salvage yard on the industrial periphery of a rain-swept metropolis. Miles of stacked rusted chassis, towering iron gantry cranes, and pools of engine oil reflect cold cyan neon signage and flickering sodium floodlights. A forgotten industrial world alive with falling mist, metallic creaks, and diesel exhaust.",
            emotionalCore = "A heavy, suffocating isolation that slowly fractures into vulnerable human tenderness. Melancholic yet intensely kinetic; cold mechanical stillness giving way to warm organic touch.",
            story = StoryArc(
                beginning = "Eli operates a lone gantry crane high above the flooded yard, isolated in the mechanical rhythm of scrapping metal while watching headlights fade in the distance.",
                middle = "A silhouette appears in the chassis corridors below—Mara, moving purposefully through the rain. Eli descends, and the two traverse the labyrinth of steel toward one another.",
                ending = "Under a humming halogen work lamp in an open compressor bay, they meet, clasp hands, and share an unspoken reckoning as the rain storm softens into twilight mist."
            ),
            characters = listOf(
                CharacterProfile(
                    name = "Eli",
                    appearance = "Late 20s, tall, gaunt frame, hollow jawline with dark stubble, damp unruly dark hair plastered to his forehead by rain. Deep-set exhausted brown eyes that carry cautious hope.",
                    clothing = "Heavy waxed canvas salvage jacket in oil-stained dark olive with frayed cuffs, faded charcoal work trousers tucked into scuffed steel-toe leather boots, fingerless insulated grip gloves.",
                    personality = "Stoic, guarded, deliberate in every motion, carrying deep unexpressed loyalty.",
                    emotionalState = "Numb and exhausted from routine, suddenly shocked into hyper-awareness.",
                    role = "The isolated crane worker seeking human connection in an mechanical purgatory.",
                    performanceBehavior = "Slow, heavy strides; hesitant gestures that become steady and deliberate when making contact; subtle vocal delivery marked by controlled breath and sudden release."
                ),
                CharacterProfile(
                    name = "Mara",
                    appearance = "Mid 20s, sharp angular features, piercing amber eyes, dark auburn braided hair threaded with high-visibility neon cord, wet skin catching specular neon reflections.",
                    clothing = "Technical matte-charcoal hooded storm breaker with amber reflective chest chevron, weatherproof combat cargo pants, reinforced rubber-soled boots.",
                    personality = "Direct, observant, resilient, and unapologetically urgent.",
                    emotionalState = "Determined and breathless, battling both the cold elements and the fear of being too late.",
                    role = "The catalyst who penetrates the industrial wasteland to bring Eli back into the living world.",
                    performanceBehavior = "Quick, agile footsteps navigating wet steel; expressive brow and open chest; singing with visceral resonance and steady eye contact."
                )
            ),
            environments = listOf(
                EnvironmentProfile(
                    name = "The North Gantry Crane",
                    appearance = "A fifty-foot rusted steel lattice crane towering over the scrap lot, surrounded by dangling chains and floodlight brackets.",
                    architecture = "Mid-century brutalist heavy industrial steelwork covered in oxidation and rivets.",
                    lighting = "Cold top-down cyan floodlight cutting through vertical rain sheets, casting long geometric shadows across the puddles.",
                    atmosphere = "Windy, cavernous, drenched, smelling of ozone and wet iron.",
                    timeOfDay = "Deep overcast night (02:30 AM).",
                    importantObjects = "Crane control cabin, rusted cable pulleys, dangling industrial electromagnets."
                ),
                EnvironmentProfile(
                    name = "Engine Block Alley (Corridor 4)",
                    appearance = "A canyon of crushed automobiles stacked four tiers high on either side of a narrow asphalt lane.",
                    architecture = "Chaotic geometric piles of scrap metal, twisted chrome bumpers, and radiator grilles.",
                    lighting = "Flickering amber sodium lamp at the corridor end creating silhouetted backlighting and golden reflections on oil puddles.",
                    atmosphere = "Claustrophobic, intimate, muffled rainfall dripping from stacked hoods.",
                    timeOfDay = "02:45 AM.",
                    importantObjects = "Cracked windshields, waterlogged tires, dripping suspension springs."
                ),
                EnvironmentProfile(
                    name = "The Compressor Bay",
                    appearance = "A semi-covered shelter with corrugated iron roof, exposed diesel generators, and a workbench lined with tools.",
                    architecture = "Industrial utilitarian workshop with open bay doors framing the rainy night outside.",
                    lighting = "Warm tungsten work lamps (2700K) casting an inviting amber glow that contrasts with the exterior blue-gray darkness.",
                    atmosphere = "Dry, warm, resonant with the rhythmic thrum of idling compressors.",
                    timeOfDay = "03:10 AM.",
                    importantObjects = "Heavy vice, vintage radio playing static, steam vent venting gentle vapor."
                )
            ),
            visualLanguage = VisualLanguage(
                colors = "Dominant palette of deep charcoal (#0D1117), cold cobalt blue (#1A365D), and raw iron rust (#8B3A22), punctuated by selective accents of intense halogen amber (#FFB300) and electric cyan (#00E5FF).",
                lighting = "Low-key chiaroscuro with high specular contrast. Raindrops catch crisp edge lights; puddles act as secondary bounce mirrors; volumetric fog cones from industrial lamps.",
                textures = "Wet asphalt, pitted oxidized steel, slick canvas fabric, beadlets of condensation on cold glass, grease-stained leather.",
                productionDesign = "Authentic tactile industrial realism mixed with subtle neo-cyberpunk accents—no holographic gimmicks, only tangible heavy machinery and raw elements.",
                atmosphere = "Permeating rain, rising steam plumes, cold moisture in every breath, kinetic droplets streaking across the camera lens."
            ),
            cinematography = Cinematography(
                framing = "Wide anamorphic 2.39:1 aspect ratio. Isolating single frames with characters framed in lower thirds against towering machinery, transitioning to intimate over-the-shoulder medium shots.",
                cameraMovement = "Slow, deliberate dolly-ins and tracking shots on low dollies. Subtle handheld micro-shakes during heightened emotional beats to convey human vulnerability.",
                lensStyle = "Vintage anamorphic lenses with shallow depth of field, warm oval bokeh highlights from distant yard lights, and restrained horizontal blue flare.",
                shotTypes = "Extreme wide atmospheric establishing shots, medium two-shots with rain foreground separation, and macro close-ups on hands and eyes.",
                cameraBehavior = "Unrushed and observational; allows performances to breathe within the environment rather than cutting frantically."
            ),
            motionLanguage = MotionLanguage(
                walking = "Eli walks with heavy, deliberate, weary steps, shoulders slightly hunched against the downpour. Mara walks with determined, light-footed rhythm, cutting through the water.",
                dancing = "No formal choreography; movement consists of naturalistic tension-and-release choreography—hesitant approaches, sudden pauses, and synchronized turning.",
                singing = "Physicalized vocal delivery—chest expanding against fabric, visible breath vaporizing in the cold air, jaw tension on emotive notes.",
                gestures = "Wiping water from eyes, gloved hands gripping wet guardrails, reaching outward across negative space, fingers intertwining.",
                interaction = "Initial distance and cautious circling gives way to an earnest, grounding physical contact.",
                environmentalMovement = "Continuous vertical rain streaks, swirling steam vents from generator pipes, swaying overhead chains, wind whipping jacket hoods.",
                cameraMovement = "Parallel tracking along characters' paths, slow push-ins that intensify during vocal peaks."
            ),
            visualMotifs = listOf(
                "Halogen Halo: Circular warm light cones framing heads like industrial halos.",
                "Mirrored Water: Inverted reflections of characters and cranes in asphalt puddles.",
                "Chains in Wind: Heavy iron links swaying rhythmically like pendulums of time.",
                "Steam and Breath: Condensation symbolizing living human warmth inside dead machinery."
            ),
            performanceDirection = PerformanceDirection(
                singing = "Sincere, raw, lip-synced with emotional commitment; breath control visibly synced with the lyric phrases; quiet passages delivered close to the chest.",
                moving = "Grounded physical behavior—never posed or artificial. Characters react realistically to the cold wet climate.",
                interacting = "Eye contact held across long beats before speech or touch; mutual recognition that overrides isolation.",
                reacting = "Subtle micro-expressions: a flinch at thunder, an exhale of relief, a loosening of clenched fists.",
                expressingEmotion = "Vulnerability breaking through protective hardness; eyes welling with moisture that blends with the raindrops."
            ),
            continuityRules = listOf(
                "Eli's dark olive canvas coat must retain the same grease stain across the left collar in every scene.",
                "Mara's high-visibility braided neon hair strand must be consistently visible framing the right side of her face.",
                "Rain intensity remains steady until easing into mist in Scene 6.",
                "The blue-gray exterior color grading remains consistent until transitioning to amber interior warmth in the final scene.",
                "Lighting direction must consistently reflect the elevated position of the yard floodlights."
            )
        )

        val scenes = listOf(
            StoryboardScene(
                sceneNumber = 1,
                sceneTitle = "The Iron Graveyard in Rain",
                storyRole = "Opening",
                storyPurpose = "Establish the oppressive mechanical scale of the salvage yard and Eli's total isolation within the storm.",
                lyricsSection = "Beneath the rusted gantry where the cold rain falls...",
                characters = listOf("Eli"),
                environment = "The North Gantry Crane platform",
                characterActions = "Eli stands on the narrow catwalk fifty feet up, gripping the wet railing. He gazes out over the endless dark scrap stacks, drops of water rolling down his jaw.",
                characterInteraction = "Solo performance; interacting only with the cold iron handrail and the falling deluge.",
                performanceDirection = "Eli sings the opening lines under his breath, mouth close to the collar of his jacket, shoulders resisting the wind.",
                cameraDirection = "Extreme wide crane shot slowly descending through rain toward a medium close-up of Eli's weathered profile.",
                lighting = "Top-down cold cyan wash from the gantry floodlight; harsh rim lighting on the wet canvas of his shoulders.",
                atmosphere = "Violent downpour, low whistling wind, metallic groaned creaks of swaying crane cables.",
                motionDirection = "Rain sheets cascading diagonally; Eli takes one slow step along the wet grating.",
                visualPrompt = "Cinematic 35mm film still, wide shot of an industrial salvage crane at night in heavy rain, lone worker in olive canvas coat on high catwalk, blue-gray cyberpunk lighting, anamorphic lens flare.",
                continuityRequirements = "Olive canvas jacket with left-collar grease smudge; damp messy hair.",
                motionPlan = MotionPlan(
                    sceneSummary = "Eli stands solitary on the elevated crane catwalk as rain pours down over the dark industrial yard.",
                    characterMotion = listOf(
                        "Eli rests gloved hands on the wet railing.",
                        "He raises his head slowly toward the storm.",
                        "He delivers the opening lyric with a visible cloud of breath.",
                        "He begins walking toward the steel access ladder."
                    ),
                    facialPerformance = listOf(
                        "Eyes squinting against driving droplets.",
                        "Mouth set in a hardened, weary line that slightly relaxes as he sings.",
                        "Subtle swallow and blink conveying solitude."
                    ),
                    interaction = listOf(
                        "Gloves gripping oxidized steel rail.",
                        "Droplets flicked away as fingers shift."
                    ),
                    environmentMotion = listOf(
                        "Heavy rain falling in slanted vertical sheets.",
                        "Overhead chains swaying slowly in the crosswind.",
                        "Water streaming in continuous rivulets down the rusted crane tower."
                    ),
                    cameraMotion = listOf(
                        "Slow vertical crane descent over 4 seconds.",
                        "Gentle slow push-in as it centers on Eli's eyes."
                    ),
                    timing = "00:00 - 00:15 (15s duration)"
                )
            ),
            StoryboardScene(
                sceneNumber = 2,
                sceneTitle = "A Shadow in Alley 4",
                storyRole = "Introduction",
                storyPurpose = "Introduce Mara navigating the salvage yard labyrinth and establish the initial spark of mutual awareness.",
                lyricsSection = "Two shadows drifting down industrial halls...",
                characters = listOf("Eli", "Mara"),
                environment = "Engine Block Alley (Corridor 4)",
                characterActions = "Eli reaches the bottom of the ladder and steps onto flooded asphalt. In the distant background between stacked chassis, Mara's reflective jacket flashes under a sodium lamp.",
                characterInteraction = "No physical touch yet; distant visual connection across 40 yards of rain.",
                performanceDirection = "Mara turns and looks through the alley corridor; Eli stops in his tracks as he spots her movement.",
                cameraDirection = "Low-angle dolly track along the flooded ground, capturing reflections of both figures separated by scrap walls.",
                lighting = "Dual-tone: Cyan foreground from yard lights contrasting with warm amber backlighting where Mara enters.",
                atmosphere = "Dripping water echo, deep mechanical drone from distant power generators.",
                motionDirection = "Mara steps smoothly around a wet tyre pile; Eli halts mid-stride and turns his torso.",
                visualPrompt = "Cinematic shot of two figures at opposite ends of a rain-slicked alley of stacked car chassis, night, amber and cyan volumetric lighting, puddles reflecting headlights.",
                continuityRequirements = "Mara's hooded charcoal jacket with amber chest stripe; Eli's dark olive coat.",
                motionPlan = MotionPlan(
                    sceneSummary = "Eli grounds himself in the alley just as Mara emerges into view between stacks of scrap steel.",
                    characterMotion = listOf(
                        "Eli steps off the ladder rung into a shallow puddle.",
                        "He freezes and stiffens as he notices a figure moving.",
                        "Mara enters frame left, hood pulled up, walking with urgency.",
                        "Both pause, separated by fifty paces."
                    ),
                    facialPerformance = listOf(
                        "Eli's eyes widen with guarded disbelief.",
                        "Mara pushes back her hood slightly, revealing her sharp gaze.",
                        "Both display heightened breath in the cold air."
                    ),
                    interaction = listOf(
                        "Locked eye contact across the spatial divide.",
                        "Mutual recognition registered in body language."
                    ),
                    environmentMotion = listOf(
                        "Water droplets bouncing in concentric rings on the puddle surface.",
                        "Steam drifting from a cracked engine block near Mara.",
                        "Rain spattering against tin roofs overhead."
                    ),
                    cameraMotion = listOf(
                        "Low dolly tracking right at knee height.",
                        "Rack focus from Eli's waterlogged boot to Mara's distant silhouette."
                    ),
                    timing = "00:15 - 00:30 (15s duration)"
                )
            ),
            StoryboardScene(
                sceneNumber = 3,
                sceneTitle = "The Approach on Flooded Asphalt",
                storyRole = "Development",
                storyPurpose = "Build kinetic emotional momentum as both characters walk toward one another through the rain.",
                lyricsSection = "The halogen halos flicker in the blue-gray mist...",
                characters = listOf("Eli", "Mara"),
                environment = "Central Yard Crossing",
                characterActions = "Both walk steadily toward the center of the yard. Water splashes under their synchronized footsteps.",
                characterInteraction = "Closing the physical distance step by step; rhythmically moving in tempo with the music.",
                performanceDirection = "Both sing in counterpoint harmonies, heads held high despite the pelting rain.",
                cameraDirection = "Parallel tracking two-shot circling gradually around the closing gap between them.",
                lighting = "An array of overhead industrial floodlights forming high-contrast beams slicing through the mist.",
                atmosphere = "Intensified precipitation, mist swirling at ankle level, rising tension in the air.",
                motionDirection = "Forward purposeful strides; water kicks up from boot heels in crisp illuminated sprays.",
                visualPrompt = "Two people walking toward each other on flooded asphalt in an industrial yard at night, cinematic framing, rain back-lit by high power halogen lights, moody cyberpunk aesthetic.",
                continuityRequirements = "Consistent water spray physics; clothing soaked on upper surfaces; braided neon hair visible.",
                motionPlan = MotionPlan(
                    sceneSummary = "Eli and Mara march toward one another across the open expanse of the flooded salvage lot.",
                    characterMotion = listOf(
                        "Eli walks forward with lengthened strides, arms swinging naturally.",
                        "Mara accelerates her pace, her rain-jacket catching the wind.",
                        "Their paths converge at the center floodlight pool.",
                        "They slow down as they reach arm's length."
                    ),
                    facialPerformance = listOf(
                        "Mara's expression shifts from fierce determination to open vulnerability.",
                        "Eli's guarded grimace softens into an emotional half-smile.",
                        "Lip sync matches the chorus vocal surge."
                    ),
                    interaction = listOf(
                        "Distance closes from 20 feet to 3 feet.",
                        "Both pause simultaneously, chests heaving from the walk."
                    ),
                    environmentMotion = listOf(
                        "Water sprays bursting from each boot impact.",
                        "Wind whipping Mara's auburn braid across her cheek.",
                        "Raindrops gleaming like diamonds in the backlights."
                    ),
                    cameraMotion = listOf(
                        "360-degree dynamic orbit around the converging pair.",
                        "Dolly-in tightening the frame as they halt."
                    ),
                    timing = "00:30 - 00:48 (18s duration)"
                )
            ),
            StoryboardScene(
                sceneNumber = 4,
                sceneTitle = "The Halogen Halo",
                storyRole = "Emotional Peak",
                storyPurpose = "The peak emotional convergence where their worlds collide directly beneath the glowing yard floodlight.",
                lyricsSection = "Searching for a memory we both almost missed...",
                characters = listOf("Eli", "Mara"),
                environment = "Beneath the Main Halogen Floodlight",
                characterActions = "Standing face to face. Water drips from their brows. They look deeply into each other's eyes.",
                characterInteraction = "Intense proximity, breathing each other's vaporized breath, hands hovering inches apart.",
                performanceDirection = "Vocal climax: Eli and Mara deliver the highest emotional phrase together with visceral intensity.",
                cameraDirection = "Slow orbital push-in right at eye-level, capturing the circular glow of the overhead lamp directly behind them.",
                lighting = "Golden 3200K halogen spotlight directly above, crowning both characters in a radiant halo against the dark backdrop.",
                atmosphere = "Time feels suspended; rain appears in ultra-crisp slow motion; warm halo haze.",
                motionDirection = "Micro movements: trembling hands, eyelids fluttering, rain rolling down noses.",
                visualPrompt = "Close-up portrait of two people face to face in heavy rain under a circular golden floodlight, mist, emotional music video cinematic shot, anamorphic bokeh.",
                continuityRequirements = "Grease smudge on Eli's collar; Mara's amber eyes and reflective stripe.",
                motionPlan = MotionPlan(
                    sceneSummary = "Face to face under the glowing circular floodlight, the emotional climax unfolds.",
                    characterMotion = listOf(
                        "Mara tilts her head up to meet Eli's gaze.",
                        "Eli slowly removes his right grip glove with his teeth.",
                        "He lets the glove drop to the wet ground.",
                        "Both breathe deeply in unison."
                    ),
                    facialPerformance = listOf(
                        "Trembling lips delivering the key song lyric.",
                        "Water running across cheekbones like tears.",
                        "Eyes conveying total mutual recognition."
                    ),
                    interaction = listOf(
                        "Their shoulders brush against one another.",
                        "Warm breath visibly mingling in the cold air."
                    ),
                    environmentMotion = listOf(
                        "The halogen bulb gives a subtle electrical flicker.",
                        "Rain droplets falling in sharp golden needle-like streaks.",
                        "Steam wafting from their warm soaked clothes."
                    ),
                    cameraMotion = listOf(
                        "Tight eye-level push-in over 5 seconds.",
                        "Subtle handheld breathing motion to amplify emotional intimacy."
                    ),
                    timing = "00:48 - 01:05 (17s duration)"
                )
            ),
            StoryboardScene(
                sceneNumber = 5,
                sceneTitle = "Reaching Through the Cold",
                storyRole = "Transformation / Climax",
                storyPurpose = "The physical barrier of isolation breaks as Mara reaches out and Eli clasps her hand.",
                lyricsSection = "Hold my hands before the salvage lights go dim...",
                characters = listOf("Eli", "Mara"),
                environment = "Beneath the Main Halogen Floodlight",
                characterActions = "Mara reaches her hand forward into the negative space. Eli hesitates for a microsecond, then clasps her palm firmly. Their fingers interlock.",
                characterInteraction = "First tactile contact: skin to skin, firm and anchoring.",
                performanceDirection = "Full emotional release; shared vocal crescendo as their joined hands pull them together in an embrace.",
                cameraDirection = "Macro tracking shot gliding along their arms down to the interlocking fingers, then tilting up to their faces.",
                lighting = "Warm amber halo overhead, accented by a cold blue rim light from a passing train headlight in the far distance.",
                atmosphere = "Electrified, tactile, the sound of the storm fading into the background of their connection.",
                motionDirection = "Hand extending forward; fingers wrapping around cold fingers; immediate tightening of grip.",
                visualPrompt = "Macro cinematic shot of two hands clasping in heavy rain at night, water splashing off knuckles, golden backlight, emotional music video still.",
                continuityRequirements = "Eli's bare right hand clasping Mara's hand; wet fabrics and textures.",
                motionPlan = MotionPlan(
                    sceneSummary = "Mara extends her hand across the cold rain, and Eli takes it, locking their fingers together.",
                    characterMotion = listOf(
                        "Mara raises her right hand slowly, palm open.",
                        "Eli watches her hand, lifts his own bare hand.",
                        "Their palms meet and fingers curl together tightly.",
                        "The clasp pulls them a step closer together."
                    ),
                    facialPerformance = listOf(
                        "A deep exhale of relief escapes Eli's mouth.",
                        "Mara's brow relaxes and a tear-like streak of rain slips down her smile.",
                        "Both sing the climactic vocal phrase with joyful release."
                    ),
                    interaction = listOf(
                        "Firm, grounding clasp of wet fingers.",
                        "Mara's left hand lightly rests on Eli's soaked jacket sleeve."
                    ),
                    environmentMotion = listOf(
                        "Water droplets scattering from the impact of their clasped palms.",
                        "Far background warning beacon rotating with a flash of amber.",
                        "Rain gradually thinning into fine mist."
                    ),
                    cameraMotion = listOf(
                        "Macro pan across their clasped hands.",
                        "Smooth crane up to capture both smiling faces framed by the halo."
                    ),
                    timing = "01:05 - 01:25 (20s duration)"
                )
            ),
            StoryboardScene(
                sceneNumber = 6,
                sceneTitle = "Shelter in the Compressor Bay",
                storyRole = "Resolution",
                storyPurpose = "Provide closure and warmth as both characters step into shelter, looking back at the storm together.",
                lyricsSection = "We built a world on iron, now we learn to swim.",
                characters = listOf("Eli", "Mara"),
                environment = "The Compressor Bay",
                characterActions = "Eli and Mara step inside the covered workshop. Eli flicks on the warm workbench lamp. They stand side by side, looking out into the twilight mist.",
                characterInteraction = "Standing shoulder to shoulder, still holding hands, calm companionship.",
                performanceDirection = "Quiet, reflective final vocal phrase; soft breathing; peaceful smiles as the song resolves.",
                cameraDirection = "Slow dolly pull-back from inside the warm bay looking outward, framing both silhouettes against the rainy yard.",
                lighting = "Warm 2700K tungsten interior glow wrapping their bodies; deep sapphire blue of dawn mist in the exterior background.",
                atmosphere = "Gentle hum of machinery, rain softening to a quiet patter, warm rising steam.",
                motionDirection = "Gentle settling; jacket unzipped; fingers resting relaxed.",
                visualPrompt = "Cinematic wide shot from inside a cozy industrial workshop looking out at a rainy scrap yard at dawn, two characters side by side in warm lamp light, moody atmospheric lighting.",
                continuityRequirements = "Same wardrobe; natural disheveled wet hair drying in the warm air.",
                motionPlan = MotionPlan(
                    sceneSummary = "Inside the dry compressor bay, Eli and Mara stand together as the storm settles into dawn mist.",
                    characterMotion = listOf(
                        "They step across the bay threshold into the dry concrete.",
                        "Eli switches on the low tungsten lamp on the workbench.",
                        "They turn together to look out at the yard through the open doorway.",
                        "They lean gently against one another."
                    ),
                    facialPerformance = listOf(
                        "Peaceful, quiet contemplative smiles.",
                        "Eyes calm, watching the mist rise from the wet ground.",
                        "Soft final vocal note sustained until fading out."
                    ),
                    interaction = listOf(
                        "Shoulders pressed together for warmth.",
                        "Intertwined hands resting gently on the wooden bench."
                    ),
                    environmentMotion = listOf(
                        "Idling compressor belt spinning with gentle rhythmic motion.",
                        "Mist slowly drifting across the threshold floor.",
                        "Rain outside dwindling into sparse gentle drops."
                    ),
                    cameraMotion = listOf(
                        "Slow 6-second dolly pull-back into the dark interior.",
                        "Final fade to black on the golden doorway framing the blue dawn."
                    ),
                    timing = "01:25 - 01:45 (20s duration)"
                )
            )
        )

        Project(
            id = "demo-drain-rack-halo",
            name = "Drain Rack Halo",
            songTitle = "Drain Rack Halo",
            artist = "Demo Artist",
            lyrics = """Beneath the rusted gantry where the cold rain falls,
Two shadows drifting down industrial halls.
The halogen halos flicker in the blue-gray mist,
Searching for a memory we both almost missed.
Hold my hands before the salvage lights go dim,
We built a world on iron, now we learn to swim.""".trimIndent(),
            creativeDirection = "Dark industrial cyberpunk music video. A lonely worker moves through an abandoned automotive salvage yard at night. Rain, blue-gray lighting, emotional tension, realistic human performances, two characters gradually reconnecting.",
            audioUri = null,
            audioFileName = "drain_rack_halo_demo.mp3",
            audioDurationMs = 105000L,
            worldReport = world,
            isWorldApproved = true,
            storyboard = scenes,
            updatedAt = System.currentTimeMillis(),
            isDemo = true
        )
    }
}
