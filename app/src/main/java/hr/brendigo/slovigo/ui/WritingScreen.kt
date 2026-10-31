package hr.brendigo.slovigo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.brendigo.slovigo.model.Alphabet

/**
 * Screen 05 — learning how to write a letter.
 * The portrait layout follows the supplied Slovigo phone reference; landscape
 * uses a left navigation rail, central tracing card and right action panel.
 *
 * A completed letter is committed only by the "Sljedeće slovo" action.
 * Reset never saves progress and replay is available on both layouts.
 */
@Composable
fun SlovigoWritingScreen(
    letter: String,
    uppercase: Boolean,
    initialGuide: Boolean,
    onBack: () -> Unit,
    onHome: () -> Unit,
    onUppercase: () -> Unit,
    onLowercase: () -> Unit,
    onSettings: () -> Unit,
    onListen: () -> Unit,
    onFinished: () -> Unit
) {
    val label = remember(letter, uppercase) { Alphabet.label(letter, uppercase) }
    val strokesTotal = remember(letter, uppercase) { Alphabet.strokes(letter, uppercase).size }
    val letterPosition = remember(letter) { Alphabet.letters.indexOf(letter).coerceAtLeast(0) + 1 }
    var reset by remember(letter, uppercase) { mutableIntStateOf(0) }
    var completed by remember(letter, uppercase) { mutableStateOf(false) }
    var help by remember(letter, uppercase) { mutableStateOf(false) }
    var doneStrokes by remember(letter, uppercase) { mutableIntStateOf(0) }

    fun restart() {
        reset++
        completed = false
        doneStrokes = 0
    }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 790.dp && maxWidth > maxHeight
        val compact = maxHeight < 670.dp || maxWidth < 350.dp
        val narrowLandscape = maxWidth < 930.dp
        TraceScene()

        if (wide) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TraceNavigationRail(
                    Modifier.width(if (narrowLandscape) 170.dp else 225.dp).fillMaxHeight(),
                    onHome, onUppercase, onLowercase, onSettings, uppercase
                )
                Column(
                    Modifier.weight(1f).fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TraceHeading(
                        label = label,
                        letterPosition = letterPosition,
                        compact = true,
                        onBack = onBack,
                        onSettings = onSettings,
                        showBrand = false
                    )
                    TraceCard(
                        label, uppercase, initialGuide, help, reset,
                        onCompleted = { completed = true },
                        onProgress = { done, _ -> doneStrokes = done }
                    )
                    StrokeProgress(doneStrokes, strokesTotal, letterPosition)
                }
                Column(
                    Modifier.width(if (narrowLandscape) 178.dp else 235.dp)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TraceExampleCard(letter, uppercase, compact = true)
                    TraceOptionButton(
                        "🔊  Poslušaj",
                        onListen,
                        Color(0xFF168AD4),
                        Modifier.fillMaxWidth()
                    )
                    TraceOptionButton(
                        "ⓘ  Pomoć",
                        { help = !help },
                        Color(0xFF8FDDF9),
                        Modifier.fillMaxWidth(),
                        darkText = true
                    )
                    TraceOptionButton(
                        "↻  Ponovi",
                        ::restart,
                        Coral,
                        Modifier.fillMaxWidth()
                    )
                    TraceOptionButton(
                        if (completed) "Sljedeće slovo  ❯" else "Dovrši poteze",
                        onFinished,
                        Color(0xFF43B04B),
                        Modifier.fillMaxWidth(),
                        enabled = completed
                    )
                    if (help) {
                        Surface(shape = RoundedCornerShape(18.dp), color = Color.White) {
                            Text(
                                "Stavi prst na obojenu točku. Prati isprekidanu liniju do strelice.",
                                Modifier.padding(12.dp),
                                color = Navy,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = if (compact) 10.dp else 15.dp, vertical = 7.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 13.dp)
            ) {
                TraceHeading(
                    label = label,
                    letterPosition = letterPosition,
                    compact = compact,
                    onBack = onBack,
                    onSettings = onSettings,
                    showBrand = true
                )
                TraceCard(
                    label, uppercase, initialGuide, help, reset,
                    onCompleted = { completed = true },
                    onProgress = { done, _ -> doneStrokes = done }
                )
                if (compact) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        TraceOptionButton(
                            "🔊  Poslušaj",
                            onListen,
                            Color(0xFFB6EBFA),
                            Modifier.weight(1f), darkText = true, small = true
                        )
                        TraceOptionButton(
                            "↻  Ponovi",
                            ::restart,
                            Color.White,
                            Modifier.weight(1f), darkText = true, small = true
                        )
                        TraceOptionButton(
                            "ⓘ  Pomoć",
                            { help = !help },
                            Yellow,
                            Modifier.weight(1f), darkText = true, small = true
                        )
                    }
                } else {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TraceOptionButton(
                            "🔊  Poslušaj",
                            onListen,
                            Color(0xFFB6EBFA),
                            Modifier.weight(1f), darkText = true
                        )
                        TraceOptionButton(
                            "↻  Ponovi",
                            ::restart,
                            Color.White,
                            Modifier.weight(1f), darkText = true
                        )
                        TraceOptionButton(
                            "ⓘ  Pomoć",
                            { help = !help },
                            Yellow,
                            Modifier.weight(1f), darkText = true
                        )
                    }
                }
                TraceOptionButton(
                    if (completed) "Sljedeće slovo  ❯" else "Prati sve poteze za nastavak",
                    onFinished,
                    Teal,
                    Modifier.fillMaxWidth().height(61.dp),
                    enabled = completed
                )
                StrokeProgress(doneStrokes, strokesTotal, letterPosition)
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun TraceHeading(
    label: String,
    letterPosition: Int,
    compact: Boolean,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    showBrand: Boolean
) {
    if (showBrand) {
        Row(
            Modifier.fillMaxWidth().height(if (compact) 63.dp else 94.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SplashWordmark(if (compact) 31 else 40)
            Spacer(Modifier.weight(1f))
            SplashPencil(Modifier.width(if (compact) 51.dp else 72.dp).fillMaxHeight())
        }
    }
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            onClick = onBack,
            modifier = Modifier.size(47.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("❮", fontSize = 29.sp, color = Navy)
            }
        }
        Column(Modifier.weight(1f).padding(horizontal = 9.dp)) {
            Text(
                "Vježbaj slovo $label",
                fontSize = if (compact) 23.sp else 29.sp,
                lineHeight = if (compact) 28.sp else 34.sp,
                color = Navy,
                fontWeight = FontWeight.Black,
                maxLines = 2
            )
            Text(
                "Prati strelice od početka do kraja",
                fontSize = if (compact) 12.sp else 15.sp,
                color = Navy.copy(alpha = .72f),
                maxLines = 2
            )
        }
        TextButton(onClick = onSettings, modifier = Modifier.width(41.dp)) {
            Text("⚙", fontSize = 23.sp, color = Teal)
        }
    }
}

@Composable
private fun TraceCard(
    label: String,
    uppercase: Boolean,
    guide: Boolean,
    help: Boolean,
    resetToken: Int,
    onCompleted: () -> Unit,
    onProgress: (Int, Int) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(30.dp),
        color = Color.White,
        shadowElevation = 5.dp
    ) {
        Column(
            Modifier.padding(11.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            TraceBoard(
                letter = label.uppercase(java.util.Locale("hr", "HR")),
                uppercase = uppercase,
                showGuide = guide,
                onCompleted = onCompleted,
                resetToken = resetToken,
                showHelp = help,
                onProgress = onProgress,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TraceOptionButton(
    label: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier,
    darkText: Boolean = false,
    enabled: Boolean = true,
    small: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = if (small) 50.dp else 55.dp)
            .semantics { contentDescription = label },
        enabled = enabled,
        shape = RoundedCornerShape(if (small) 17.dp else 21.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = if (darkText) Navy else Color.White,
            disabledContainerColor = Color(0xFFD8E2E9),
            disabledContentColor = Navy.copy(alpha = .68f)
        ),
        contentPadding = PaddingValues(horizontal = if (small) 5.dp else 10.dp)
    ) {
        Text(
            label,
            textAlign = TextAlign.Center,
            fontSize = if (small) 12.sp else 15.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = if (small) 15.sp else 19.sp,
            maxLines = 2
        )
    }
}

@Composable
private fun StrokeProgress(done: Int, total: Int, letterPosition: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = Color.White.copy(alpha = .91f)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("▣", color = Teal, fontSize = 24.sp)
            Column(Modifier.weight(1f)) {
                Text(
                    "Slovo $letterPosition od 30  •  potezi $done/$total",
                    color = Navy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { if (total > 0) (done.toFloat() / total).coerceIn(0f, 1f) else 0f },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp).height(7.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = Teal,
                    trackColor = Color(0xFFD8DFE3),
                    gapSize = 0.dp,
                    drawStopIndicator = {}
                )
            }
        }
    }
}

@Composable
private fun TraceExampleCard(letter: String, uppercase: Boolean, compact: Boolean) {
    val examples = mapOf(
        "A" to "Jabuka", "B" to "Brod", "C" to "Cvijet",
        "Č" to "Čaša", "Ć" to "Ćup", "D" to "Duga", "DŽ" to "Džep",
        "Đ" to "Đak", "E" to "Ekipa", "F" to "Frula", "G" to "Grad",
        "H" to "Hrast", "I" to "Igra", "J" to "Jaje", "K" to "Knjiga",
        "L" to "List", "LJ" to "Ljubav", "M" to "More",
        "N" to "Nos", "NJ" to "Njiva", "O" to "Oblak", "P" to "Pas",
        "R" to "Riba", "S" to "Sunce", "Š" to "Šuma",
        "T" to "Trava", "U" to "Uho", "V" to "Voda",
        "Z" to "Zima", "Ž" to "Žir"
    )
    val label = Alphabet.label(letter, uppercase)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            Modifier.fillMaxWidth().padding(13.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                "Slovo $label",
                color = Navy,
                fontWeight = FontWeight.Black,
                fontSize = if (compact) 24.sp else 28.sp
            )
            Box(Modifier.fillMaxWidth().height(82.dp).clip(RoundedCornerShape(15.dp))
                .background(Color(0xFFFFF4DC)),contentAlignment = Alignment.Center) {
                Text(
                    "✏  ${examples[letter] ?: letter}",
                    color = Navy,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TraceNavigationRail(
    modifier: Modifier,
    onHome: () -> Unit,
    onUppercase: () -> Unit,
    onLowercase: () -> Unit,
    onSettings: () -> Unit,
    uppercase: Boolean
) {
    Column(
        modifier.clip(RoundedCornerShape(22.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF087B8C), Color(0xFF035B76))))
            .verticalScroll(rememberScrollState()).padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SplashWordmark(23)
        Text("Učimo pisati slova", color = Color.White, fontSize = 12.sp, textAlign = TextAlign.Center)
        TraceOptionButton("⌂  Početna", onHome, Color.White, Modifier.fillMaxWidth(), darkText = true)
        TraceOptionButton(
            "A  Velika slova", onUppercase,
            if (uppercase) Yellow else Color.White, Modifier.fillMaxWidth(), darkText = true
        )
        TraceOptionButton(
            "a  Mala slova", onLowercase,
            if (!uppercase) Yellow else Color.White, Modifier.fillMaxWidth(), darkText = true
        )
        TraceOptionButton("⚙  Postavke", onSettings, Color.White, Modifier.fillMaxWidth(), darkText = true)
        SplashPencil(Modifier.width(120.dp).height(170.dp))
    }
}

@Composable
private fun TraceScene() {
    Canvas(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color(0xFF73CFF3), Color(0xFFC9F3FF), Ivory, Color(0xFFB8E88A))
            )
        )
    ) {
        val w = size.width
        val h = size.height
        val cloud = Color.White.copy(alpha = .68f)
        drawOval(cloud, Offset(w * .05f, h * .12f),
            androidx.compose.ui.geometry.Size(w * .22f, h * .055f))
        drawOval(cloud, Offset(w * .76f, h * .15f),
            androidx.compose.ui.geometry.Size(w * .25f, h * .05f))
        val green = Path().apply {
            moveTo(0f, h * .92f)
            cubicTo(w * .22f, h * .87f, w * .48f, h * .94f, w * .67f, h * .9f)
            cubicTo(w * .82f, h * .86f, w * .95f, h * .9f, w, h * .87f)
            lineTo(w, h); lineTo(0f, h); close()
        }
        drawPath(green, Green)
    }
}
