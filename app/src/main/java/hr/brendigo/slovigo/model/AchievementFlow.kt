package hr.brendigo.slovigo.model

/** Pure logic shared by the learning completion UI and unit tests. */
object AchievementFlow {
    fun safeCompletedCount(value: Int): Int = value.coerceIn(0, Alphabet.letters.size)

    /** Returns null after the last letter or for an invalid letter key. */
    fun nextLetter(current: String): String? {
        val index = Alphabet.letters.indexOf(current)
        return if (index < 0) null else Alphabet.letters.getOrNull(index + 1)
    }
}
