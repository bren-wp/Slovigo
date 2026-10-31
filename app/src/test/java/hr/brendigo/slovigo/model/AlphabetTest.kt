package hr.brendigo.slovigo.model

import org.junit.Assert.*
import org.junit.Test

class AlphabetTest {
    @Test fun hasEveryCroatianLetterExactlyOnce() {
        assertEquals(30, Alphabet.letters.size)
        assertEquals(30, Alphabet.letters.distinct().size)
        assertTrue(Alphabet.letters.containsAll(listOf("Č", "Ć", "DŽ", "Đ", "LJ", "NJ", "Š", "Ž")))
    }

    @Test fun eachLetterHasTraceableStrokesInBothCases() {
        for (letter in Alphabet.letters) for (upper in listOf(true, false)) {
            val strokes = Alphabet.strokes(letter, upper)
            assertTrue("Missing strokes for $letter/$upper", strokes.isNotEmpty())
            for (stroke in strokes) {
                assertTrue(stroke.size >= 2)
                assertTrue(stroke.all { it.x >= 0f && it.x <= 1f && it.y >= 0f && it.y <= 1f })
            }
        }
    }

    @Test fun strokeMustStartAtCorrectPlaceAndFinish() {
        val stroke = listOf(Dot(0.1f, 0.1f), Dot(0.5f, 0.5f), Dot(0.9f, 0.9f))
        val tracker = StrokeTracker(stroke)
        assertFalse(tracker.start(Dot(0.9f, 0.9f)))
        assertFalse(tracker.end())
        assertTrue(tracker.start(Dot(0.1f, 0.1f)))
        tracker.move(Dot(0.5f, 0.5f))
        tracker.move(Dot(0.9f, 0.9f))
        assertTrue(tracker.end())
    }
}
