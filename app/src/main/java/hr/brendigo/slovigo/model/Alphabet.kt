package hr.brendigo.slovigo.model

import kotlin.math.hypot
import kotlin.math.min

/** Immutable, device-independent points in a 0..1 square. */
data class Dot(val x: Float, val y: Float) {
    fun distance(other: Dot): Float = hypot(x - other.x, y - other.y)
}
typealias Stroke = List<Dot>

object Alphabet {
    val letters = listOf(
        "A", "B", "C", "Č", "Ć", "D", "DŽ", "Đ", "E", "F",
        "G", "H", "I", "J", "K", "L", "LJ", "M", "N", "NJ",
        "O", "P", "R", "S", "Š", "T", "U", "V", "Z", "Ž"
    )
    init { check(letters.size == 30 && letters.distinct().size == 30) }

    fun label(letter: String, uppercase: Boolean) =
        if (uppercase) letter else letter.lowercase(java.util.Locale("hr", "HR"))

    private fun line(spec: String): Stroke = spec.split(" ").map {
        val coords = it.split(",")
        Dot(coords[0].toFloat(), coords[1].toFloat())
    }

    private val upper = mapOf(
        'A' to listOf("0.14,0.9 0.5,0.1", "0.5,0.1 0.86,0.9", "0.28,0.57 0.72,0.57"),
        'B' to listOf("0.22,0.1 0.22,0.9", "0.22,0.1 0.63,0.1 0.78,0.25 0.67,0.48 0.22,0.49", "0.22,0.49 0.71,0.5 0.82,0.72 0.65,0.9 0.22,0.9"),
        'C' to listOf("0.82,0.22 0.58,0.1 0.3,0.19 0.15,0.5 0.3,0.81 0.6,0.9 0.82,0.78"),
        'D' to listOf("0.2,0.1 0.2,0.9", "0.2,0.1 0.52,0.1 0.83,0.3 0.83,0.7 0.52,0.9 0.2,0.9"),
        'E' to listOf("0.75,0.1 0.2,0.1 0.2,0.9 0.75,0.9", "0.2,0.5 0.67,0.5"),
        'F' to listOf("0.2,0.9 0.2,0.1 0.78,0.1", "0.2,0.5 0.67,0.5"),
        'G' to listOf("0.82,0.23 0.58,0.1 0.28,0.2 0.15,0.5 0.3,0.82 0.6,0.9 0.83,0.76 0.83,0.55 0.57,0.55"),
        'H' to listOf("0.21,0.1 0.21,0.9", "0.79,0.1 0.79,0.9", "0.21,0.5 0.79,0.5"),
        'I' to listOf("0.3,0.1 0.7,0.1", "0.5,0.1 0.5,0.9", "0.3,0.9 0.7,0.9"),
        'J' to listOf("0.3,0.1 0.8,0.1", "0.7,0.1 0.7,0.72 0.55,0.9 0.28,0.81"),
        'K' to listOf("0.21,0.1 0.21,0.9", "0.78,0.1 0.21,0.56", "0.47,0.43 0.8,0.9"),
        'L' to listOf("0.2,0.1 0.2,0.9 0.78,0.9"),
        'M' to listOf("0.12,0.9 0.12,0.1 0.5,0.54 0.88,0.1 0.88,0.9"),
        'N' to listOf("0.2,0.9 0.2,0.1 0.8,0.9 0.8,0.1"),
        'O' to listOf("0.5,0.1 0.77,0.2 0.86,0.5 0.77,0.8 0.5,0.9 0.23,0.8 0.14,0.5 0.23,0.2 0.5,0.1"),
        'P' to listOf("0.22,0.9 0.22,0.1 0.62,0.1 0.82,0.3 0.64,0.53 0.22,0.53"),
        'R' to listOf("0.22,0.9 0.22,0.1 0.62,0.1 0.82,0.3 0.64,0.52 0.22,0.52", "0.52,0.52 0.85,0.9"),
        'S' to listOf("0.78,0.21 0.55,0.1 0.25,0.24 0.25,0.43 0.7,0.58 0.79,0.78 0.5,0.9 0.2,0.79"),
        'T' to listOf("0.12,0.1 0.88,0.1", "0.5,0.1 0.5,0.9"),
        'U' to listOf("0.2,0.1 0.2,0.7 0.34,0.9 0.66,0.9 0.8,0.7 0.8,0.1"),
        'V' to listOf("0.15,0.1 0.5,0.9 0.85,0.1"),
        'Z' to listOf("0.17,0.1 0.83,0.1 0.17,0.9 0.83,0.9")
    )
    private val lower = mapOf(
        'a' to listOf("0.75,0.4 0.51,0.31 0.23,0.45 0.22,0.72 0.46,0.88 0.75,0.72 0.75,0.34 0.75,0.9"),
        'b' to listOf("0.25,0.08 0.25,0.9", "0.25,0.49 0.56,0.32 0.79,0.51 0.78,0.74 0.52,0.89 0.25,0.74"),
        'c' to listOf("0.77,0.43 0.51,0.32 0.23,0.49 0.21,0.73 0.5,0.88 0.77,0.79"),
        'd' to listOf("0.76,0.08 0.76,0.9", "0.76,0.48 0.46,0.32 0.21,0.5 0.23,0.76 0.52,0.88 0.76,0.7"),
        'e' to listOf("0.22,0.62 0.78,0.62 0.73,0.43 0.48,0.32 0.22,0.51 0.26,0.78 0.55,0.89 0.78,0.79"),
        'f' to listOf("0.74,0.18 0.55,0.08 0.39,0.23 0.39,0.9", "0.22,0.42 0.65,0.42"),
        'g' to listOf("0.76,0.35 0.76,0.94 0.59,0.99 0.3,0.92", "0.76,0.43 0.51,0.32 0.23,0.5 0.23,0.74 0.5,0.86 0.76,0.7"),
        'h' to listOf("0.22,0.08 0.22,0.9", "0.22,0.52 0.47,0.33 0.71,0.42 0.75,0.9"),
        'i' to listOf("0.5,0.36 0.5,0.9", "0.5,0.13 0.5,0.16"),
        'j' to listOf("0.62,0.36 0.62,0.9 0.44,0.99 0.28,0.91", "0.62,0.13 0.62,0.16"),
        'k' to listOf("0.25,0.08 0.25,0.9", "0.74,0.35 0.25,0.7", "0.5,0.55 0.78,0.9"),
        'l' to listOf("0.5,0.08 0.5,0.9"),
        'm' to listOf("0.15,0.9 0.15,0.35 0.36,0.34 0.45,0.49 0.45,0.9", "0.45,0.49 0.66,0.34 0.81,0.51 0.81,0.9"),
        'n' to listOf("0.2,0.9 0.2,0.35 0.45,0.34 0.73,0.47 0.73,0.9"),
        'o' to listOf("0.5,0.32 0.75,0.45 0.78,0.7 0.5,0.88 0.22,0.7 0.25,0.45 0.5,0.32"),
        'p' to listOf("0.22,0.35 0.22,0.99", "0.22,0.45 0.52,0.32 0.78,0.51 0.76,0.74 0.49,0.86 0.22,0.71"),
        'r' to listOf("0.22,0.9 0.22,0.35 0.48,0.34 0.76,0.41"),
        's' to listOf("0.76,0.42 0.5,0.32 0.24,0.49 0.55,0.62 0.76,0.77 0.48,0.88 0.21,0.77"),
        't' to listOf("0.47,0.15 0.47,0.76 0.65,0.9 0.8,0.83", "0.25,0.4 0.75,0.4"),
        'u' to listOf("0.22,0.35 0.22,0.74 0.47,0.89 0.77,0.7 0.77,0.35 0.77,0.9"),
        'v' to listOf("0.18,0.35 0.5,0.9 0.82,0.35"),
        'z' to listOf("0.2,0.35 0.8,0.35 0.2,0.9 0.8,0.9")
    )

    /** Each stroke is directional and has a fixed starting point; Croatian digraphs are combined. */
    fun strokes(letter: String, uppercase: Boolean): List<Stroke> {
        val parts = letter.toList()
        return parts.flatMapIndexed { idx, char ->
            val base = if (uppercase) char.uppercaseChar() else char.lowercaseChar()
            val simple = when (base) {
                'Č', 'Ć' -> if (uppercase) 'C' else 'c'
                'Š' -> if (uppercase) 'S' else 's'
                'Ž' -> if (uppercase) 'Z' else 'z'
                'Đ' -> if (uppercase) 'D' else 'd'
                else -> base
            }
            val source = if (uppercase) upper[simple] else lower[simple]
            val main = (source ?: upper[simple.uppercaseChar()] ?: emptyList()).map(::line).toMutableList()
            when (base.uppercaseChar()) {
                'Č', 'Š', 'Ž' -> main.add(line("0.35,0.03 0.5,0.14 0.65,0.03"))
                'Ć' -> main.add(line("0.44,0.15 0.66,0.02"))
                'Đ' -> main.add(line("0.09,0.53 0.49,0.53"))
            }
            // Each component of DŽ, LJ, NJ occupies half the same normalized tracing surface.
            main.map { stroke ->
                stroke.map { dot ->
                    if (parts.size == 1) dot
                    else Dot(0.03f + (dot.x * 0.46f) + idx * 0.48f, dot.y)
                }
            }
        }
    }
}

/**
 * Lightweight, deterministic ordered-stroke validation. Distance is normalized,
 * so recognition works the same way across screen densities and aspect ratios.
 */
class StrokeTracker(private val reference: Stroke, private val tolerance: Float = 0.13f) {
    var progress: Float = 0f
        private set
    private var active = false

    fun start(at: Dot): Boolean {
        progress = 0f
        active = reference.size >= 2 && at.distance(reference.first()) <= tolerance
        return active
    }

    fun move(at: Dot): Float {
        if (!active) return progress
        val segments = reference.size - 1
        var best = progress
        for (i in 0 until segments) {
            val a = reference[i]
            val b = reference[i + 1]
            val dx = b.x - a.x
            val dy = b.y - a.y
            val length = dx * dx + dy * dy
            val t = if (length == 0f) 0f else
                (((at.x - a.x) * dx + (at.y - a.y) * dy) / length).coerceIn(0f, 1f)
            val nearest = Dot(a.x + t * dx, a.y + t * dy)
            val candidate = (i + t) / segments.toFloat()
            if (nearest.distance(at) <= tolerance && candidate >= progress &&
                candidate <= progress + 0.55f) {
                best = maxOf(best, candidate)
            }
        }
        progress = min(1f, best)
        return progress
    }

    fun end(): Boolean {
        val completed = active && progress >= 0.88f
        active = false
        if (!completed) progress = 0f
        return completed
    }
}
