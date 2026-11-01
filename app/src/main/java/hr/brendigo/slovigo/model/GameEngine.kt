package hr.brendigo.slovigo.model

/** Deterministic, offline quiz logic. The same answer set is testable without Android. */
object GameEngine {
    const val FIND = "find"
    const val LOWERCASE = "lowercase"
    const val POINTS_PER_FIRST_WIN = 10

    data class Question(
        val letter: String,
        val answer: String,
        val choices: List<String>
    )

    fun question(mode: String, round: Int): Question {
        require(mode == FIND || mode == LOWERCASE) { "Unknown game mode" }
        val letters = Alphabet.letters
        val index = Math.floorMod(round, letters.size)
        val targets = listOf(
            letters[index],
            letters[(index + 4) % letters.size],
            letters[(index + 9) % letters.size],
            letters[(index + 16) % letters.size]
        )
        val options = targets.map { Alphabet.label(it, mode == FIND) }
        val rotation = Math.floorMod(round, options.size)
        return Question(
            letter = letters[index],
            answer = options[0],
            choices = List(options.size) { options[(it + rotation) % options.size] }
        )
    }

    fun isCorrect(question: Question, chosen: String): Boolean =
        chosen == question.answer

    fun nextRound(round: Int): Int =
        (Math.floorMod(round, Alphabet.letters.size) + 1) % Alphabet.letters.size
}
