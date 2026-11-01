package hr.brendigo.slovigo.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameEngineTest {
    @Test fun everyRoundHasFourDistinctValidChoicesAndOneCorrectAnswer() {
        for (mode in listOf(GameEngine.FIND, GameEngine.LOWERCASE)) {
            for (round in 0 until 30) {
                val q = GameEngine.question(mode, round)
                assertEquals(4, q.choices.size)
                assertEquals(4, q.choices.distinct().size)
                assertEquals(1, q.choices.count { it == q.answer })
                assertTrue(GameEngine.isCorrect(q, q.answer))
                assertFalse(GameEngine.isCorrect(q, "not-an-answer"))
            }
        }
    }

    @Test fun hrvatskiDigraphsAreValidLowercaseAnswers() {
        assertEquals("dž", GameEngine.question(GameEngine.LOWERCASE, 6).answer)
        assertEquals("lj", GameEngine.question(GameEngine.LOWERCASE, 16).answer)
        assertEquals("nj", GameEngine.question(GameEngine.LOWERCASE, 19).answer)
        assertEquals("č", GameEngine.question(GameEngine.LOWERCASE, 3).answer)
    }

    @Test fun everyQuestionRotatesAndGameNeverRunsPastAlphabet() {
        assertEquals(0, GameEngine.nextRound(29))
        assertEquals(1, GameEngine.nextRound(0))
        assertEquals("A", GameEngine.question(GameEngine.FIND, 30).answer)
        assertEquals(4, GameEngine.question(GameEngine.FIND, 0).choices.size)
    }

    @Test fun firstTimeAwardIsTenAndWrongAnswerNeverQualifies() {
        assertEquals(10, GameEngine.POINTS_PER_FIRST_WIN)
        val q = GameEngine.question(GameEngine.FIND, 0)
        assertFalse(GameEngine.isCorrect(q, "Ž"))
        assertTrue(GameEngine.isCorrect(q, "A"))
    }
}
