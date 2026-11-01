package hr.brendigo.slovigo.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PracticePreferencesTest {
    @Test fun boardSizeIsClampedAndFitsAllScreens() {
        assertEquals(0.82f, PracticePreferences.boardFraction(-11), 0f)
        assertEquals(0.92f, PracticePreferences.boardFraction(1), 0f)
        assertEquals(1.0f, PracticePreferences.boardFraction(100), 0f)
    }

    @Test fun strokeSensitivityUsesDistinctNormalizedTolerances() {
        val strict = PracticePreferences.strokeTolerance(0)
        val normal = PracticePreferences.strokeTolerance(1)
        val relaxed = PracticePreferences.strokeTolerance(2)
        assertTrue(strict < normal && normal < relaxed)
        assertEquals(strict, PracticePreferences.strokeTolerance(-5), 0f)
        assertEquals(relaxed, PracticePreferences.strokeTolerance(999), 0f)
    }
}
