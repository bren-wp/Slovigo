package hr.brendigo.slovigo.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressInsightsTest {
    @Test fun progressIsBasedOnSixtyDistinctCaseSlots() {
        assertEquals(60, ProgressInsights.total(30, 30))
        assertEquals(12, ProgressInsights.total(8, 4))
        assertEquals(0, ProgressInsights.total(-1, -9))
        assertEquals(60, ProgressInsights.total(50, 40))
    }

    @Test fun progressRatioNeverExceedsItsRange() {
        assertEquals(0f, ProgressInsights.ratio(-5, 30), 0f)
        assertEquals(0.5f, ProgressInsights.ratio(15, 30), 0.00001f)
        assertEquals(1f, ProgressInsights.ratio(70, 30), 0f)
        assertEquals(0f, ProgressInsights.ratio(0, 0), 0f)
    }

    @Test fun nextGoalsUseRealUnfinishedCroatianLetters() {
        val learned = setOf("A", "B", "C", "Č")
        assertEquals(listOf("Ć", "D", "DŽ"),
            ProgressInsights.nextGoal(learned, emptySet()))
        assertEquals(listOf("a", "b", "c"),
            ProgressInsights.nextGoal(Alphabet.letters.toSet(), emptySet()))
        assertTrue(ProgressInsights.nextGoal(Alphabet.letters.toSet(),
            Alphabet.letters.toSet()).isEmpty())
    }

    @Test fun badgesFollowRealMilestonesNotHardcodedAwards() {
        assertFalse(ProgressInsights.firstFiveLearned(4))
        assertTrue(ProgressInsights.firstFiveLearned(5))
        assertFalse(ProgressInsights.tenLearned(9))
        assertTrue(ProgressInsights.tenLearned(10))
        assertFalse(ProgressInsights.weekStreak(6))
        assertTrue(ProgressInsights.weekStreak(7))
        assertFalse(ProgressInsights.masterAlphabet(59))
        assertTrue(ProgressInsights.masterAlphabet(60))
    }
}
