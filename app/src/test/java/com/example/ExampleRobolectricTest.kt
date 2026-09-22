package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ProjectEntity
import com.example.data.model.DemoProjectData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("BeatVision Lite", appName)
    }

    @Test
    fun `verify activity launch`() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        assertNotNull(controller.get())
    }

    @Test
    fun `verify demo project integrity`() {
        val demo = DemoProjectData.drainRackHaloProject
        assertEquals("Drain Rack Halo", demo.name)
        assertEquals("Drain Rack Halo", demo.songTitle)
        assertNotNull(demo.worldReport)
        assertEquals(6, demo.storyboard.size)

        // Check scene 1 structure
        val scene1 = demo.storyboard[0]
        assertEquals(1, scene1.sceneNumber)
        assertEquals("Opening", scene1.storyRole)
        assertTrue(scene1.motionPlan.characterMotion.isNotEmpty())
        assertTrue(scene1.motionPlan.facialPerformance.isNotEmpty())
        assertTrue(scene1.motionPlan.environmentMotion.isNotEmpty())
        assertTrue(scene1.motionPlan.cameraMotion.isNotEmpty())
    }

    @Test
    fun `verify project entity conversion roundtrip`() {
        val demo = DemoProjectData.drainRackHaloProject
        val entity = ProjectEntity.fromDomain(demo)
        val roundtrip = entity.toDomain()

        assertEquals(demo.id, roundtrip.id)
        assertEquals(demo.name, roundtrip.name)
        assertEquals(demo.songTitle, roundtrip.songTitle)
        assertEquals(demo.storyboard.size, roundtrip.storyboard.size)
        assertEquals(demo.worldReport?.theWorld, roundtrip.worldReport?.theWorld)
    }

    @Test
    fun `verify full user workflow and demo navigation`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.BeatVisionViewModel(app)

        // 1. Home
        assertTrue(vm.currentScreen.value is com.example.ui.navigation.Screen.Home)

        // 2. Load Demo Project
        vm.loadDemoProject()
        var attempts = 0
        while (vm.activeProject.value == null && attempts < 50) {
            Thread.sleep(20)
            org.robolectric.shadows.ShadowLooper.idleMainLooper()
            attempts++
        }
        val active = vm.activeProject.value
        assertNotNull(active)
        assertEquals("Drain Rack Halo", active?.name)
        assertTrue(vm.currentScreen.value is com.example.ui.navigation.Screen.VisualWorld)

        // 3. Verify Visual World Approval & Navigation to Storyboard
        vm.approveWorld()
        org.robolectric.shadows.ShadowLooper.idleMainLooper()
        assertTrue(vm.currentScreen.value is com.example.ui.navigation.Screen.Storyboard)

        // 4. Open Scene Detail (Scene 0)
        vm.openSceneDetail(0)
        assertTrue(vm.currentScreen.value is com.example.ui.navigation.Screen.SceneDetail)

        // 5. Open Cinematic Preview
        vm.openCinematicPreview()
        assertTrue(vm.currentScreen.value is com.example.ui.navigation.Screen.CinematicPreview)
        assertEquals(0, vm.previewSceneIndex.value)

        // 6. Navigation Controls in Preview
        vm.nextPreviewScene()
        assertEquals(1, vm.previewSceneIndex.value)
        vm.prevPreviewScene()
        assertEquals(0, vm.previewSceneIndex.value)

        // 7. Back Navigation back through stack
        val backResult = vm.navigateBack()
        assertTrue(backResult)
    }

    @Test
    fun `verify error safety when ai service has no key`() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.BeatVisionViewModel(app)

        vm.startNewProject()
        vm.updateProjectDetails(
            name = "Test Project",
            songTitle = "Test Song",
            artist = "Test Artist",
            lyrics = "Testing lyrics without key",
            creativeDirection = "Cinematic"
        )

        // Calling revealWorld without an API key must not crash and must populate errorMessage cleanly
        vm.revealWorld()
        var safetyAttempts = 0
        while (vm.errorMessage.value == null && safetyAttempts < 50) {
            Thread.sleep(20)
            org.robolectric.shadows.ShadowLooper.idleMainLooper()
            safetyAttempts++
        }

        // Verify project data is intact and preserved
        assertNotNull(vm.activeProject.value)
        assertEquals("Test Song", vm.activeProject.value?.songTitle)
        assertTrue(vm.errorMessage.value?.contains("Gemini API key") == true)
    }
}
