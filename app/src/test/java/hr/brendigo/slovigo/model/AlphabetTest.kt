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

    @Test fun lowercaseCroatianLabelsAreDistinctAndCorrect() {
        assertEquals("dž", Alphabet.label("DŽ", false))
        assertEquals("lj", Alphabet.label("LJ", false))
        assertEquals("nj", Alphabet.label("NJ", false))
        assertEquals("č", Alphabet.label("Č", false))
        assertEquals("ć", Alphabet.label("Ć", false))
        assertEquals("đ", Alphabet.label("Đ", false))
        assertEquals("š", Alphabet.label("Š", false))
        assertEquals("ž", Alphabet.label("Ž", false))
        assertEquals(30, Alphabet.letters.map { Alphabet.label(it, false) }.toSet().size)
    }

    @Test fun lowerCaseDigraphsHaveIndependentTraceablePaths() {
        for (digraph in listOf("DŽ", "LJ", "NJ")) {
            val parts = Alphabet.strokes(digraph, false)
            assertTrue("No strokes for $digraph", parts.isNotEmpty())
            assertTrue("No left-side component in $digraph", parts.any { path -> path.any { it.x < 0.48f } })
            assertTrue("No right-side component in $digraph", parts.any { path -> path.any { it.x > 0.52f } })
        }
    }

    @Test fun reversedStrokeCannotFinishLetter() {
        val segment = listOf(Dot(0.1f, 0.9f), Dot(0.5f, 0.1f))
        val tracker = StrokeTracker(segment)
        assertFalse(tracker.start(Dot(0.5f, 0.1f)))
        tracker.move(Dot(0.1f, 0.9f))
        assertFalse(tracker.end())
    }

    @Test fun incompleteStrokeCannotFinishLetter() {
        val segment = listOf(Dot(0.1f, 0.9f), Dot(0.5f, 0.1f))
        val tracker = StrokeTracker(segment)
        assertTrue(tracker.start(Dot(0.1f, 0.9f)))
        tracker.move(Dot(0.2f, 0.7f))
        assertFalse(tracker.end())
    }

    @Test fun everyCroatianDigraphHasMoreThanOneStroke() {
        for (letter in listOf("DŽ", "LJ", "NJ")) {
            assertTrue(Alphabet.strokes(letter, true).size > 1)
            assertTrue(Alphabet.strokes(letter, false).size > 1)
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
