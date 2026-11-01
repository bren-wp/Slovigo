package hr.brendigo.slovigo.model

/** Testable rules behind the "Moj napredak" dashboard. */
object ProgressInsights {
    const val LETTERS_PER_CASE = 30
    const val TOTAL_LETTERS = LETTERS_PER_CASE * 2

    fun total(upper: Int, lower: Int) =
        upper.coerceIn(0, LETTERS_PER_CASE) + lower.coerceIn(0, LETTERS_PER_CASE)

    fun ratio(count: Int, max: Int): Float =
        if (max <= 0) 0f else (count.toFloat() / max).coerceIn(0f, 1f)

    fun nextGoal(upperDone: Set<String>, lowerDone: Set<String>): List<String> {
        val upper = Alphabet.letters.filterNot { it in upperDone }.map { it }
        val lower = Alphabet.letters.filterNot { it in lowerDone }
            .map { Alphabet.label(it, false) }
        return (upper + lower).take(3)
    }

    fun firstFiveLearned(total: Int) = total >= 5
    fun tenLearned(total: Int) = total >= 10
    fun weekStreak(days: Int) = days >= 7
    fun masterAlphabet(total: Int) = total >= TOTAL_LETTERS
}
