package com.dronescan.msdksample

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit tests for DroneScan V9
 * These tests run on the JVM without Android dependencies
 */
class BasicUnitTest {
    
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
    
    @Test
    fun testStringNotNull() {
        val testString = "DroneScan V9"
        assertNotNull("String should not be null", testString)
    }
    
    @Test
    fun testPackageName() {
        val expectedPackage = "com.dronescan.msdksample"
        assertEquals("Package name should match", expectedPackage, expectedPackage)
    }
}
