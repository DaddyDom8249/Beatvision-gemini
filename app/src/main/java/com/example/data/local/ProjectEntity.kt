package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CharacterProfile
import com.example.data.model.Cinematography
import com.example.data.model.EnvironmentProfile
import com.example.data.model.MotionLanguage
import com.example.data.model.MotionPlan
import com.example.data.model.PerformanceDirection
import com.example.data.model.Project
import com.example.data.model.StoryArc
import com.example.data.model.StoryboardScene
import com.example.data.model.VisualLanguage
import com.example.data.model.VisualWorld
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val songTitle: String,
    val artist: String,
    val lyrics: String,
    val creativeDirection: String,
    val audioUri: String?,
    val audioFileName: String?,
    val audioDurationMs: Long,
    val worldJson: String?,
    val isWorldApproved: Boolean,
    val storyboardJson: String?,
    val updatedAt: Long,
    val isDemo: Boolean
) {
    fun toDomain(): Project {
        val world = worldJson?.let { parseWorldJson(it) }
        val scenes = storyboardJson?.let { parseStoryboardJson(it) } ?: emptyList()
        return Project(
            id = id,
            name = name,
            songTitle = songTitle,
            artist = artist,
            lyrics = lyrics,
            creativeDirection = creativeDirection,
            audioUri = audioUri,
            audioFileName = audioFileName,
            audioDurationMs = audioDurationMs,
            worldReport = world,
            isWorldApproved = isWorldApproved,
            storyboard = scenes,
            updatedAt = updatedAt,
            isDemo = isDemo
        )
    }

    companion object {
        fun fromDomain(project: Project): ProjectEntity {
            return ProjectEntity(
                id = project.id,
                name = project.name,
                songTitle = project.songTitle,
                artist = project.artist,
                lyrics = project.lyrics,
                creativeDirection = project.creativeDirection,
                audioUri = project.audioUri,
                audioFileName = project.audioFileName,
                audioDurationMs = project.audioDurationMs,
                worldJson = project.worldReport?.let { worldToJson(it) },
                isWorldApproved = project.isWorldApproved,
                storyboardJson = if (project.storyboard.isNotEmpty()) storyboardToJson(project.storyboard) else null,
                updatedAt = project.updatedAt,
                isDemo = project.isDemo
            )
        }

        fun worldToJson(world: VisualWorld): String {
            val root = JSONObject().apply {
                put("theWorld", world.theWorld)
                put("emotionalCore", world.emotionalCore)
                put("story", JSONObject().apply {
                    put("beginning", world.story.beginning)
                    put("middle", world.story.middle)
                    put("ending", world.story.ending)
                })
                put("characters", JSONArray().apply {
                    world.characters.forEach { c ->
                        put(JSONObject().apply {
                            put("name", c.name)
                            put("appearance", c.appearance)
                            put("clothing", c.clothing)
                            put("personality", c.personality)
                            put("emotionalState", c.emotionalState)
                            put("role", c.role)
                            put("performanceBehavior", c.performanceBehavior)
                        })
                    }
                })
                put("environments", JSONArray().apply {
                    world.environments.forEach { e ->
                        put(JSONObject().apply {
                            put("name", e.name)
                            put("appearance", e.appearance)
                            put("architecture", e.architecture)
                            put("lighting", e.lighting)
                            put("atmosphere", e.atmosphere)
                            put("timeOfDay", e.timeOfDay)
                            put("importantObjects", e.importantObjects)
                        })
                    }
                })
                put("visualLanguage", JSONObject().apply {
                    put("colors", world.visualLanguage.colors)
                    put("lighting", world.visualLanguage.lighting)
                    put("textures", world.visualLanguage.textures)
                    put("productionDesign", world.visualLanguage.productionDesign)
                    put("atmosphere", world.visualLanguage.atmosphere)
                })
                put("cinematography", JSONObject().apply {
                    put("framing", world.cinematography.framing)
                    put("cameraMovement", world.cinematography.cameraMovement)
                    put("lensStyle", world.cinematography.lensStyle)
                    put("shotTypes", world.cinematography.shotTypes)
                    put("cameraBehavior", world.cinematography.cameraBehavior)
                })
                put("motionLanguage", JSONObject().apply {
                    put("walking", world.motionLanguage.walking)
                    put("dancing", world.motionLanguage.dancing)
                    put("singing", world.motionLanguage.singing)
                    put("gestures", world.motionLanguage.gestures)
                    put("interaction", world.motionLanguage.interaction)
                    put("environmentalMovement", world.motionLanguage.environmentalMovement)
                    put("cameraMovement", world.motionLanguage.cameraMovement)
                })
                put("visualMotifs", JSONArray(world.visualMotifs))
                put("performanceDirection", JSONObject().apply {
                    put("singing", world.performanceDirection.singing)
                    put("moving", world.performanceDirection.moving)
                    put("interacting", world.performanceDirection.interacting)
                    put("reacting", world.performanceDirection.reacting)
                    put("expressingEmotion", world.performanceDirection.expressingEmotion)
                })
                put("continuityRules", JSONArray(world.continuityRules))
            }
            return root.toString()
        }

        fun parseWorldJson(jsonStr: String): VisualWorld? {
            return try {
                val json = JSONObject(jsonStr)
                val storyObj = json.optJSONObject("story") ?: JSONObject()
                val story = StoryArc(
                    beginning = storyObj.optString("beginning", ""),
                    middle = storyObj.optString("middle", ""),
                    ending = storyObj.optString("ending", "")
                )

                val charsList = mutableListOf<CharacterProfile>()
                val charsArr = json.optJSONArray("characters")
                if (charsArr != null) {
                    for (i in 0 until charsArr.length()) {
                        val c = charsArr.getJSONObject(i)
                        charsList.add(
                            CharacterProfile(
                                name = c.optString("name", ""),
                                appearance = c.optString("appearance", ""),
                                clothing = c.optString("clothing", ""),
                                personality = c.optString("personality", ""),
                                emotionalState = c.optString("emotionalState", ""),
                                role = c.optString("role", ""),
                                performanceBehavior = c.optString("performanceBehavior", "")
                            )
                        )
                    }
                }

                val envList = mutableListOf<EnvironmentProfile>()
                val envArr = json.optJSONArray("environments")
                if (envArr != null) {
                    for (i in 0 until envArr.length()) {
                        val e = envArr.getJSONObject(i)
                        envList.add(
                            EnvironmentProfile(
                                name = e.optString("name", ""),
                                appearance = e.optString("appearance", ""),
                                architecture = e.optString("architecture", ""),
                                lighting = e.optString("lighting", ""),
                                atmosphere = e.optString("atmosphere", ""),
                                timeOfDay = e.optString("timeOfDay", ""),
                                importantObjects = e.optString("importantObjects", "")
                            )
                        )
                    }
                }

                val vlObj = json.optJSONObject("visualLanguage") ?: JSONObject()
                val visualLanguage = VisualLanguage(
                    colors = vlObj.optString("colors", ""),
                    lighting = vlObj.optString("lighting", ""),
                    textures = vlObj.optString("textures", ""),
                    productionDesign = vlObj.optString("productionDesign", ""),
                    atmosphere = vlObj.optString("atmosphere", "")
                )

                val cineObj = json.optJSONObject("cinematography") ?: JSONObject()
                val cinematography = Cinematography(
                    framing = cineObj.optString("framing", ""),
                    cameraMovement = cineObj.optString("cameraMovement", ""),
                    lensStyle = cineObj.optString("lensStyle", ""),
                    shotTypes = cineObj.optString("shotTypes", ""),
                    cameraBehavior = cineObj.optString("cameraBehavior", "")
                )

                val mlObj = json.optJSONObject("motionLanguage") ?: JSONObject()
                val motionLanguage = MotionLanguage(
                    walking = mlObj.optString("walking", ""),
                    dancing = mlObj.optString("dancing", ""),
                    singing = mlObj.optString("singing", ""),
                    gestures = mlObj.optString("gestures", ""),
                    interaction = mlObj.optString("interaction", ""),
                    environmentalMovement = mlObj.optString("environmentalMovement", ""),
                    cameraMovement = mlObj.optString("cameraMovement", "")
                )

                val motifs = mutableListOf<String>()
                val motifsArr = json.optJSONArray("visualMotifs")
                if (motifsArr != null) {
                    for (i in 0 until motifsArr.length()) motifs.add(motifsArr.getString(i))
                }

                val pdObj = json.optJSONObject("performanceDirection") ?: JSONObject()
                val performanceDirection = PerformanceDirection(
                    singing = pdObj.optString("singing", ""),
                    moving = pdObj.optString("moving", ""),
                    interacting = pdObj.optString("interacting", ""),
                    reacting = pdObj.optString("reacting", ""),
                    expressingEmotion = pdObj.optString("expressingEmotion", "")
                )

                val rules = mutableListOf<String>()
                val rulesArr = json.optJSONArray("continuityRules")
                if (rulesArr != null) {
                    for (i in 0 until rulesArr.length()) rules.add(rulesArr.getString(i))
                }

                VisualWorld(
                    theWorld = json.optString("theWorld", ""),
                    emotionalCore = json.optString("emotionalCore", ""),
                    story = story,
                    characters = charsList,
                    environments = envList,
                    visualLanguage = visualLanguage,
                    cinematography = cinematography,
                    motionLanguage = motionLanguage,
                    visualMotifs = motifs,
                    performanceDirection = performanceDirection,
                    continuityRules = rules
                )
            } catch (_: Exception) {
                null
            }
        }

        fun storyboardToJson(scenes: List<StoryboardScene>): String {
            val array = JSONArray()
            scenes.forEach { s ->
                val obj = JSONObject().apply {
                    put("sceneNumber", s.sceneNumber)
                    put("sceneTitle", s.sceneTitle)
                    put("storyRole", s.storyRole)
                    put("storyPurpose", s.storyPurpose)
                    put("lyricsSection", s.lyricsSection)
                    put("characters", JSONArray(s.characters))
                    put("environment", s.environment)
                    put("characterActions", s.characterActions)
                    put("characterInteraction", s.characterInteraction)
                    put("performanceDirection", s.performanceDirection)
                    put("cameraDirection", s.cameraDirection)
                    put("lighting", s.lighting)
                    put("atmosphere", s.atmosphere)
                    put("motionDirection", s.motionDirection)
                    put("visualPrompt", s.visualPrompt)
                    put("continuityRequirements", s.continuityRequirements)
                    s.generatedImageUrl?.let { put("generatedImageUrl", it) }
                    s.generatedImageModel?.let { put("generatedImageModel", it) }
                    s.generatedImageStatus?.let { put("generatedImageStatus", it) }
                    put("motionPlan", JSONObject().apply {
                        put("sceneSummary", s.motionPlan.sceneSummary)
                        put("characterMotion", JSONArray(s.motionPlan.characterMotion))
                        put("facialPerformance", JSONArray(s.motionPlan.facialPerformance))
                        put("interaction", JSONArray(s.motionPlan.interaction))
                        put("environmentMotion", JSONArray(s.motionPlan.environmentMotion))
                        put("cameraMotion", JSONArray(s.motionPlan.cameraMotion))
                        put("timing", s.motionPlan.timing)
                    })
                }
                array.put(obj)
            }
            return array.toString()
        }

        fun parseStoryboardJson(jsonStr: String): List<StoryboardScene> {
            val result = mutableListOf<StoryboardScene>()
            try {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val s = array.getJSONObject(i)
                    val chars = mutableListOf<String>()
                    val charsArr = s.optJSONArray("characters")
                    if (charsArr != null) {
                        for (j in 0 until charsArr.length()) chars.add(charsArr.getString(j))
                    }

                    val mpObj = s.optJSONObject("motionPlan") ?: JSONObject()
                    val cm = mutableListOf<String>()
                    mpObj.optJSONArray("characterMotion")?.let { for (k in 0 until it.length()) cm.add(it.getString(k)) }
                    val fp = mutableListOf<String>()
                    mpObj.optJSONArray("facialPerformance")?.let { for (k in 0 until it.length()) fp.add(it.getString(k)) }
                    val ix = mutableListOf<String>()
                    mpObj.optJSONArray("interaction")?.let { for (k in 0 until it.length()) ix.add(it.getString(k)) }
                    val em = mutableListOf<String>()
                    mpObj.optJSONArray("environmentMotion")?.let { for (k in 0 until it.length()) em.add(it.getString(k)) }
                    val cam = mutableListOf<String>()
                    mpObj.optJSONArray("cameraMotion")?.let { for (k in 0 until it.length()) cam.add(it.getString(k)) }

                    val motionPlan = MotionPlan(
                        sceneSummary = mpObj.optString("sceneSummary", ""),
                        characterMotion = cm,
                        facialPerformance = fp,
                        interaction = ix,
                        environmentMotion = em,
                        cameraMotion = cam,
                        timing = mpObj.optString("timing", "")
                    )

                    result.add(
                        StoryboardScene(
                            sceneNumber = s.optInt("sceneNumber", i + 1),
                            sceneTitle = s.optString("sceneTitle", "Scene ${i + 1}"),
                            storyRole = s.optString("storyRole", "Scene"),
                            storyPurpose = s.optString("storyPurpose", ""),
                            lyricsSection = s.optString("lyricsSection", ""),
                            characters = chars,
                            environment = s.optString("environment", ""),
                            characterActions = s.optString("characterActions", ""),
                            characterInteraction = s.optString("characterInteraction", ""),
                            performanceDirection = s.optString("performanceDirection", ""),
                            cameraDirection = s.optString("cameraDirection", ""),
                            lighting = s.optString("lighting", ""),
                            atmosphere = s.optString("atmosphere", ""),
                            motionDirection = s.optString("motionDirection", ""),
                            visualPrompt = s.optString("visualPrompt", ""),
                            continuityRequirements = s.optString("continuityRequirements", ""),
                            motionPlan = motionPlan,
                            generatedImageUrl = s.optString("generatedImageUrl", "").takeIf { it.isNotBlank() },
                            generatedImageModel = s.optString("generatedImageModel", "").takeIf { it.isNotBlank() },
                            generatedImageStatus = s.optString("generatedImageStatus", "").takeIf { it.isNotBlank() }
                        )
                    )
                }
            } catch (_: Exception) {}
            return result
        }
    }
}
