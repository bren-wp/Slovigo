package hr.brendigo.slovigo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.brendigo.slovigo.model.GameEngine

/**
 * Screen 08 — games and challenges.
 * Adaptive, self-contained, fully offline mini-games. Unlike a static mockup
 * every selectable mode and answer executes a real action.
 */
@Composable
fun SlovigoGamesScreen(
    points: Int,
    onAward: (mode: String, letter: String) -> Boolean,
    onPractice: (letter: String) -> Unit,
    onSpeak: (text: String) -> Unit,
    onHome: () -> Unit,
    onProgress: () -> Unit,
    onSettings: () -> Unit
) {
    var mode by rememberSaveable { mutableStateOf("") }
    var round by rememberSaveable { mutableIntStateOf(0) }
    var selected by rememberSaveable(mode, round) { mutableStateOf("") }
    var correct by rememberSaveable(mode, round) { mutableStateOf(false) }
    var reward by rememberSaveable(mode, round) { mutableIntStateOf(0) }
    val question = remember(mode, round) {
        if (mode.isEmpty()) null else GameEngine.question(mode, round)
    }

    fun start(newMode: String) {
        mode = newMode
        round = 0
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 715.dp
        val compact = maxWidth < 355.dp
        GameSky()
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier.weight(1f).fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = if (compact) 10.dp else if (wide) 22.dp else 16.dp)
                    .padding(top = 10.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(if (compact) 11.dp else 16.dp)
            ) {
                GameHeader(compact, onSettings)
                GameSummary(points, compact)

                if (wide) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            Modifier.weight(.92f),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            ModeCard(
                                title = "Pronađi slovo",
                                subtitle = "Prepoznaj slovo između četiri kartice.",
                                emblem = "ABC",
                                background = Color(0xFFFFE9A9),
                                selected = mode == GameEngine.FIND,
                                onClick = { start(GameEngine.FIND) }
                            )
                            ModeCard(
                                title = "Veliko i malo",
                                subtitle = "Pronađi odgovarajuće malo tiskano slovo.",
                                emblem = "Aa",
                                background = Color(0xFFC5EFF6),
                                selected = mode == GameEngine.LOWERCASE,
                                onClick = { start(GameEngine.LOWERCASE) }
                            )
                            ModeCard(
                                title = "Vježbaj pisanje",
                                subtitle = "Prati poteze i pravilno napiši slovo.",
                                emblem = "✎",
                                background = Color(0xFFFFD4DC),
                                selected = false,
                                onClick = { onPractice(question?.letter ?: "A") }
                            )
                        }
                        Box(Modifier.weight(1.08f)) {
                            if (question == null) {
                                GameIntroduction()
                            } else {
                                ChallengeCard(
                                    question = question,
                                    mode = mode,
                                    round = round,
                                    selected = selected,
                                    correct = correct,
                                    reward = reward,
                                    onSpeak = onSpeak,
                                    onAnswer = { choice ->
                                        if (!correct) {
                                            selected = choice
                                            if (GameEngine.isCorrect(question, choice)) {
                                                correct = true
                                                reward = if (onAward(mode, question.letter)) {
                                                    GameEngine.POINTS_PER_FIRST_WIN
                                                } else 0
                                            }
                                        }
                                    },
                                    onNext = { round = GameEngine.nextRound(round) },
                                    onClose = { mode = "" }
                                )
                            }
                        }
                    }
                } else {
                    ModeCard(
                        title = "Pronađi slovo",
                        subtitle = "Prepoznaj slovo između četiri kartice.",
                        emblem = "ABC",
                        background = Color(0xFFFFE9A9),
                        selected = mode == GameEngine.FIND,
                        onClick = { start(GameEngine.FIND) }
                    )
                    ModeCard(
                        title = "Veliko i malo",
                        subtitle = "Pronađi odgovarajuće malo tiskano slovo.",
                        emblem = "Aa",
                        background = Color(0xFFC5EFF6),
                        selected = mode == GameEngine.LOWERCASE,
                        onClick = { start(GameEngine.LOWERCASE) }
                    )
                    ModeCard(
                        title = "Vježbaj pisanje",
                        subtitle = "Prati poteze i pravilno napiši slovo.",
                        emblem = "✎",
                        background = Color(0xFFFFD4DC),
                        selected = false,
                        onClick = { onPractice(question?.letter ?: "A") }
                    )
                    if (question != null) {
                        ChallengeCard(
                            question = question,
                            mode = mode,
                            round = round,
                            selected = selected,
                            correct = correct,
                            reward = reward,
                            onSpeak = onSpeak,
                            onAnswer = { choice ->
                                if (!correct) {
                                    selected = choice
                                    if (GameEngine.isCorrect(question, choice)) {
                                        correct = true
                                        reward = if (onAward(mode, question.letter)) {
                                            GameEngine.POINTS_PER_FIRST_WIN
                                        } else 0
                                    }
                                }
                            },
                            onNext = { round = GameEngine.nextRound(round) },
                            onClose = { mode = "" }
                        )
                    }
                }
            }
            GamesNavigation(onHome, onProgress, onSettings)
        }
    }
}

@Composable
private fun GameHeader(compact: Boolean, onSettings: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        Row(
            Modifier.fillMaxWidth().height(if (compact) 65.dp else 85.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SplashBook(Modifier.size(if (compact) 53.dp else 68.dp))
            Spacer(Modifier.width(5.dp))
            Column(Modifier.weight(1f)) {
                SplashWordmark(if (compact) 23 else 30)
                Text(
                    "Učimo kroz igru!",
                    color = Navy,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Surface(
                onClick = onSettings,
                modifier = Modifier.size(49.dp).semantics {
                    contentDescription = "Otvori postavke"
                },
                color = Color.White,
                shape = RoundedCornerShape(17.dp),
                shadowElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("⚙", fontSize = 27.sp, color = Teal)
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().height(if (compact) 105.dp else 125.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    "Igre i izazovi",
                    fontSize = if (compact) 30.sp else 37.sp,
                    lineHeight = if (compact) 34.sp else 42.sp,
                    fontWeight = FontWeight.Black,
                    color = Navy,
                    maxLines = 2
                )
                Text(
                    "Igraj se, prepoznaj i nauči slova!",
                    fontSize = if (compact) 13.sp else 15.sp,
                    lineHeight = 19.sp,
                    color = Navy.copy(alpha = .73f)
                )
            }
            SplashPencil(
                Modifier.width(if (compact) 96.dp else 128.dp).fillMaxHeight()
            )
        }
    }
}

@Composable
private fun GameSummary(points: Int, compact: Boolean) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        color = Color.White.copy(alpha = .95f),
        shadowElevation = 3.dp
    ) {
        Row(
            Modifier.fillMaxWidth()
                .padding(horizontal = if (compact) 12.dp else 17.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(52.dp).clip(RoundedCornerShape(17.dp))
                    .background(Color(0xFFFFF0C8)),
                contentAlignment = Alignment.Center
            ) {
                Text("★", fontSize = 39.sp, color = Color(0xFFF6AE03))
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Moji bodovi",
                    fontSize = 17.sp,
                    color = Navy,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "10 za svaki prvi točan izazov",
                    fontSize = if (compact) 11.sp else 12.sp,
                    color = Navy.copy(alpha = .7f)
                )
            }
            Text(
                "$points",
                fontSize = 29.sp,
                fontWeight = FontWeight.Black,
                color = Teal
            )
        }
    }
}

@Composable
private fun ModeCard(
    title: String,
    subtitle: String,
    emblem: String,
    background: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 108.dp),
        shape = RoundedCornerShape(27.dp),
        color = background,
        border = androidx.compose.foundation.BorderStroke(
            if (selected) 3.dp else 1.dp,
            if (selected) Teal else Color.White.copy(alpha = .75f)
        ),
        shadowElevation = if (selected) 7.dp else 3.dp
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 13.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier.size(72.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color.White.copy(alpha = .92f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    emblem,
                    fontSize = if (emblem == "ABC") 25.sp else 32.sp,
                    fontWeight = FontWeight.Black,
                    color = if (emblem == "Aa") Blue else Teal
                )
            }
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Black,
                    color = Navy,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    maxLines = 2
                )
                Text(
                    subtitle,
                    color = Navy.copy(alpha = .75f),
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    maxLines = 3
                )
            }
            Text(
                "❯",
                fontSize = 26.sp,
                color = Teal,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun GameIntroduction() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = Ivory,
        shadowElevation = 4.dp
    ) {
        Column(
            Modifier.fillMaxWidth().padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(13.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Spreman za igru?",
                fontSize = 27.sp,
                fontWeight = FontWeight.Black,
                color = Navy
            )
            Text(
                "Odaberi jednu od igara i započni izazov. Uči hrvatsku abecedu bez interneta.",
                fontSize = 16.sp,
                lineHeight = 23.sp,
                color = Navy,
                textAlign = TextAlign.Center
            )
            Text(
                "A  B  C  Č  Ć",
                color = Teal,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ChallengeCard(
    question: GameEngine.Question,
    mode: String,
    round: Int,
    selected: String,
    correct: Boolean,
    reward: Int,
    onSpeak: (String) -> Unit,
    onAnswer: (String) -> Unit,
    onNext: () -> Unit,
    onClose: () -> Unit
) {
    val find = mode == GameEngine.FIND
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(29.dp),
        color = Color.White.copy(alpha = .97f),
        shadowElevation = 6.dp
    ) {
        Column(
            Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Izazov ${round + 1} / 30",
                    color = Teal,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onClose) {
                    Text("Zatvori", color = Navy)
                }
            }
            Text(
                if (find) "Pronađi ovo slovo:" else "Koje malo slovo odgovara?",
                fontSize = 21.sp,
                fontWeight = FontWeight.Black,
                color = Navy,
                textAlign = TextAlign.Center
            )
            Box(
                Modifier.fillMaxWidth().height(115.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFF3FBFE), Color(0xFFDDF7F3))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    question.letter,
                    color = Navy,
                    fontSize = 82.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 1
                )
            }
            TextButton(onClick = { onSpeak(question.letter) }) {
                Text("♫  Poslušaj slovo", color = Teal, fontWeight = FontWeight.Bold)
            }
            Column(
                Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                question.choices.chunked(2).forEach { pair ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(9.dp)
                    ) {
                        pair.forEach { option ->
                            val chosen = option == selected
                            val buttonBg = when {
                                chosen && correct -> Color(0xFF9CE7B7)
                                chosen -> Color(0xFFFFD1D0)
                                else -> Color(0xFFE4ECFA)
                            }
                            Surface(
                                onClick = { onAnswer(option) },
                                enabled = !correct,
                                modifier = Modifier.weight(1f).height(79.dp).semantics {
                                    contentDescription = "Odgovor $option"
                                },
                                shape = RoundedCornerShape(21.dp),
                                color = buttonBg,
                                shadowElevation = 2.dp
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        option,
                                        color = Navy,
                                        fontSize = if (option.length > 1) 31.sp else 39.sp,
                                        fontWeight = FontWeight.Black,
                                        maxLines = 1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (selected.isNotEmpty()) {
                Text(
                    if (correct) {
                        if (reward > 0) "Bravo! Točno! +$reward bodova ⭐"
                        else "Bravo! Točno! Ovaj izazov već je riješen. ⭐"
                    } else "Pokušaj ponovno. Odaberi odgovarajuće slovo.",
                    color = if (correct) TealDark else Color(0xFFB63F50),
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            Button(
                onClick = onNext,
                enabled = correct,
                modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Teal,
                    disabledContainerColor = Color(0xFFD9E3E5),
                    disabledContentColor = Navy.copy(alpha = .6f)
                )
            ) {
                Text(
                    "Sljedeći izazov  ❯",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 2,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun GamesNavigation(
    onHome: () -> Unit,
    onProgress: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        color = Color.White,
        shadowElevation = 6.dp
    ) {
        Row(
            Modifier.fillMaxWidth().height(72.dp)
                .padding(horizontal = 7.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                Triple("⌂", "Početna", onHome),
                Triple("★", "Igre", {}),
                Triple("▥", "Napredak", onProgress),
                Triple("⚙", "Postavke", onSettings)
            ).forEach { (symbol, title, action) ->
                val active = title == "Igre"
                Column(
                    Modifier.weight(1f).fillMaxHeight()
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (active) Color(0xFFC3F1ED) else Color.Transparent)
                        .clickable(enabled = !active, role = Role.Button, onClick = action)
                        .semantics { contentDescription = title },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(symbol, fontSize = 25.sp, color = Navy)
                    Text(
                        title,
                        fontSize = 12.sp,
                        fontWeight = if (active) FontWeight.Black else FontWeight.Medium,
                        color = Navy,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun GameSky() {
    Canvas(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color(0xFF8BD7F7), Color(0xFFD7F5FC), Ivory, Color(0xFFD0EE99))
            )
        )
    ) {
        val w = size.width
        val h = size.height
        drawOval(
            Color.White.copy(alpha = .65f),
            Offset(w * .06f, h * .13f),
            Size(w * .26f, h * .047f)
        )
        drawOval(
            Color.White.copy(alpha = .65f),
            Offset(w * .76f, h * .16f),
            Size(w * .22f, h * .045f)
        )
        val meadow = Path().apply {
            moveTo(0f, h * .91f)
            cubicTo(w * .24f, h * .85f, w * .47f, h * .96f, w * .66f, h * .90f)
            cubicTo(w * .84f, h * .84f, w * .94f, h * .92f, w, h * .88f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(meadow, Green)
    }
}
