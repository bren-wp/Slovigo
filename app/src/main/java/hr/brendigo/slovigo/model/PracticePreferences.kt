package hr.brendigo.slovigo.model

/**
 * Only supported preference values are permitted, including after corrupted
 * or manually-edited SharedPreferences values are read back.
 */
object PracticePreferences {
    const val BOARD_DEFAULT = 1
    const val TOLERANCE_DEFAULT = 1

    fun clamp(value: Int): Int = value.coerceIn(0, 2)

    /** Real fraction of available board width; never grows past its parent. */
    fun boardFraction(level: Int): Float = when (clamp(level)) {
        0 -> 0.82f
        1 -> 0.92f
        else -> 1.0f
    }

    /** Same normalized tolerance across screen densities. */
    fun strokeTolerance(level: Int): Float = when (clamp(level)) {
        0 -> 0.09f
        1 -> 0.13f
        else -> 0.18f
    }
}
