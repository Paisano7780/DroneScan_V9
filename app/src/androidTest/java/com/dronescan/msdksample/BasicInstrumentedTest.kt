package com.dronescan.msdksample

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for DroneScan V9
 * These tests run on an Android device or emulator
 */
@RunWith(AndroidJUnit4::class)
class BasicInstrumentedTest {
    
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun useAppContext() {
        // Context of the app under test
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.dronescan.msdksample", appContext.packageName)
    }
    
    @Test
    fun testAppLaunches() {
        // Test that the main activity launches successfully
        activityRule.scenario.onActivity { activity ->
            assertNotNull("Activity should not be null", activity)
        }
    }
    
    @Test
    fun testApplicationInstance() {
        // Test that the DroneScanApplication initializes
        activityRule.scenario.onActivity { activity ->
            val app = activity.application
            assertNotNull("Application should not be null", app)
            assertEquals("Application class should be DroneScanApplication", 
                "com.dronescan.msdksample.DroneScanApplication", 
                app.javaClass.name)
        }
    }
    
    @Test
    fun testSDKInitializationDoesNotCrash() {
        // Test that SDK initialization doesn't crash the app
        // This is a basic smoke test
        activityRule.scenario.onActivity { activity ->
            // Wait for SDK initialization (3 seconds should be enough)
            Thread.sleep(3000)
            
            // If we reach here, the app didn't crash during initialization
            assertNotNull("Activity should still be alive after SDK init", activity)
        }
    }
    
    @Test
    fun testUIElementsExist() {
        // Test that main UI elements are present
        activityRule.scenario.onActivity { activity ->
            val rootView = activity.findViewById<android.view.View>(android.R.id.content)
            assertNotNull("Root view should exist", rootView)
        }
    }
}
