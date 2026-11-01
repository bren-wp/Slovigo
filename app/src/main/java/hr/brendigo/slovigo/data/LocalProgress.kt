package hr.brendigo.slovigo.data

import android.content.Context
import hr.brendigo.slovigo.model.Alphabet
import hr.brendigo.slovigo.model.PracticePreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Private on-device data only. No account, network or Android backup. */
class LocalProgress(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "slovigo_progress", Context.MODE_PRIVATE
    )

    fun completed(letter: String, uppercase: Boolean): Boolean =
        prefs.getBoolean("done_" + (if (uppercase) "upper" else "lower") + "_" + letter, false)

    fun markCompleted(letter: String, uppercase: Boolean) {
        if (letter !in Alphabet.letters) return
        prefs.edit().putBoolean(
            "done_" + (if (uppercase) "upper" else "lower") + "_" + letter, true
        ).apply()
    }

    fun total(uppercase: Boolean): Int = Alphabet.letters.count { completed(it, uppercase) }

    fun completedLetters(uppercase: Boolean): Set<String> =
        Alphabet.letters.filterTo(mutableSetOf()) { completed(it, uppercase) }

    data class ActivityDay(val label: String, val seconds: Int)

    /** Only completed exercises contribute. No invented sample statistics. */
    fun recordCompletedExercise(durationMillis: Long) {
        val seconds = (durationMillis / 1000L).coerceIn(1L, 1800L).toInt()
        val key = "practice_" + todayKey()
        val old = prefs.getInt(key, 0)
        prefs.edit().putInt(key, (old.toLong() + seconds).coerceAtMost(86400L).toInt()).apply()
    }

    private fun todayKey(): String =
        SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(Date())

    fun weeklyActivity(): List<ActivityDay> {
        val cal = Calendar.getInstance()
        val days = ArrayList<ActivityDay>(7)
        val names = listOf("Ned", "Pon", "Uto", "Sri", "Čet", "Pet", "Sub")
        repeat(7) {
            val key = SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(cal.time)
            days.add(ActivityDay(names[cal.get(Calendar.DAY_OF_WEEK) - 1],
                prefs.getInt("practice_" + key, 0).coerceAtLeast(0)))
            cal.add(Calendar.DATE, -1)
        }
        return days.asReversed()
    }

    /** Current consecutive practice days; may include yesterday as the latest day. */
    fun practiceStreak(): Int {
        val cal = Calendar.getInstance()
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.ROOT)
        fun hasActivity(): Boolean = prefs.getInt("practice_" + fmt.format(cal.time), 0) > 0
        if (!hasActivity()) cal.add(Calendar.DATE, -1)
        var streak = 0
        while (streak < 366 && hasActivity()) {
            streak++
            cal.add(Calendar.DATE, -1)
        }
        return streak
    }

    /** Unique completed mini-game challenges; replay cannot farm points. */
    fun gamePoints(): Int = prefs.getInt("game_points", 0).coerceIn(0, 600)

    fun awardGameOnce(mode: String, letter: String): Boolean {
        if (mode !in setOf("find", "lowercase") || letter !in Alphabet.letters) return false
        val key = "game_" + mode + "_" + letter
        if (prefs.getBoolean(key, false)) return false
        prefs.edit()
            .putBoolean(key, true)
            .putInt("game_points", (gamePoints() + 10).coerceAtMost(600))
            .apply()
        return true
    }

    fun hintsEnabled(): Boolean = prefs.getBoolean("hints", false)
    fun setHintsEnabled(value: Boolean) = prefs.edit().putBoolean("hints", value).apply()
    fun strokeNumbersEnabled(): Boolean = prefs.getBoolean("numbers", true)
    fun setStrokeNumbersEnabled(value: Boolean) = prefs.edit().putBoolean("numbers", value).apply()
    fun boardSize(): Int = PracticePreferences.clamp(prefs.getInt("board_size", PracticePreferences.BOARD_DEFAULT))
    fun setBoardSize(value: Int) = prefs.edit().putInt("board_size", PracticePreferences.clamp(value)).apply()
    fun touchTolerance(): Int = PracticePreferences.clamp(prefs.getInt("touch_tolerance", PracticePreferences.TOLERANCE_DEFAULT))
    fun setTouchTolerance(value: Int) = prefs.edit().putInt("touch_tolerance", PracticePreferences.clamp(value)).apply()

    fun soundEnabled(): Boolean = prefs.getBoolean("sound", true)
    fun setSoundEnabled(value: Boolean) = prefs.edit().putBoolean("sound", value).apply()
    fun guideEnabled(): Boolean = prefs.getBoolean("guide", true)
    fun setGuideEnabled(value: Boolean) = prefs.edit().putBoolean("guide", value).apply()
    fun contrastEnabled(): Boolean = prefs.getBoolean("contrast", false)
    fun setContrastEnabled(value: Boolean) = prefs.edit().putBoolean("contrast", value).apply()
    fun reset() = prefs.edit().clear().apply()
}
