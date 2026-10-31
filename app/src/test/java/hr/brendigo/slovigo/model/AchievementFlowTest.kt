package hr.brendigo.slovigo.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AchievementFlowTest {
    @Test fun clampsLearnedLettersToThirty() {
        assertEquals(0, AchievementFlow.safeCompletedCount(-2))
        assertEquals(12, AchievementFlow.safeCompletedCount(12))
        assertEquals(30, AchievementFlow.safeCompletedCount(500))
    }

    @Test fun advancesInCorrectCroatianOrder() {
        assertEquals("B", AchievementFlow.nextLetter("A"))
        assertEquals("Ć", AchievementFlow.nextLetter("Č"))
        assertEquals("DŽ", AchievementFlow.nextLetter("D"))
        assertEquals("Đ", AchievementFlow.nextLetter("DŽ"))
        assertEquals("NJ", AchievementFlow.nextLetter("N"))
        assertEquals("Ž", AchievementFlow.nextLetter("Z"))
    }

    @Test fun endOfAlphabetReturnsToSelection() {
        assertNull(AchievementFlow.nextLetter("Ž"))
        assertNull(AchievementFlow.nextLetter("UNKNOWN"))
    }
}
