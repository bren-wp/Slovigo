package hr.brendigo.slovigo.data

import android.content.Context
import hr.brendigo.slovigo.model.Alphabet

/** Private on-device data only; no backup and no network synchronization. */
class LocalProgress(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("slovigo_progress", Context.MODE_PRIVATE)

    fun completed(letter: String, uppercase: Boolean): Boolean =
        prefs.getBoolean("done_${if (uppercase) "upper" else "lower"}_$letter", false)

    fun markCompleted(letter: String, uppercase: Boolean) {
        prefs.edit().putBoolean("done_${if (uppercase) "upper" else "lower"}_$letter", true).apply()
    }

    fun total(uppercase: Boolean): Int = Alphabet.letters.count { completed(it, uppercase) }
    fun soundEnabled(): Boolean = prefs.getBoolean("sound", true)
    fun setSoundEnabled(value: Boolean) = prefs.edit().putBoolean("sound", value).apply()
    fun guideEnabled(): Boolean = prefs.getBoolean("guide", true)
    fun setGuideEnabled(value: Boolean) = prefs.edit().putBoolean("guide", value).apply()
    fun contrastEnabled(): Boolean = prefs.getBoolean("contrast", false)
    fun setContrastEnabled(value: Boolean) = prefs.edit().putBoolean("contrast", value).apply()
    fun reset() = prefs.edit().clear().apply()
}
