package hr.brendigo.slovigo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.brendigo.slovigo.model.Alphabet
import hr.brendigo.slovigo.model.Dot
import hr.brendigo.slovigo.model.Stroke as LetterStroke
import hr.brendigo.slovigo.model.StrokeTracker
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Screen 05 tracing canvas. Every stroke uses normalized 0..1 coordinates, so
 * validation is density-independent and rendering scales to every window width.
 *
 * Resetting is explicit through resetToken; a rejected gesture never advances
 * progress. The completion callback is dispatched once and only for valid strokes.
 */
@Composable
fun TraceBoard(
    letter: String,
    uppercase: Boolean,
    showGuide: Boolean,
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier,
    resetToken: Int = 0,
    showHelp: Boolean = false,
    showNumbers: Boolean = true,
    tolerance: Float = 0.13f,
    onProgress: (Int, Int) -> Unit = { _, _ -> }
) {
    val strokes = remember(letter, uppercase) { Alphabet.strokes(letter, uppercase) }
    var strokeIndex by remember(letter, uppercase, resetToken) { mutableIntStateOf(0) }
    var finished by remember(letter, uppercase, resetToken) { mutableStateOf(false) }
    var touchPoints by remember(letter, uppercase, resetToken, strokeIndex) {
        mutableStateOf(emptyList<Dot>())
    }
    var acceptedPaths by remember(letter, uppercase, resetToken) {
        mutableStateOf(emptyList<LetterStroke>())
    }
    var gestureStarted by remember(letter, uppercase, resetToken, strokeIndex) {
        mutableStateOf(false)
    }
    val current = strokes.getOrNull(strokeIndex)
    if (strokes.isEmpty() || current.isNullOrEmpty()) {
        Text("Putanja nije dostupna. Vrati se na izbor slova.", color = Navy)
        return
    }
    val validator = remember(letter, uppercase, resetToken, strokeIndex, tolerance) {
        StrokeTracker(current, tolerance.coerceIn(0.07f, 0.2f))
    }
    val completeNow by rememberUpdatedState(onCompleted)
    val reportProgress by rememberUpdatedState(onProgress)

    Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            if (finished) "Odlično! Svi potezi su završeni."
            else "Potez ${strokeIndex + 1} od ${strokes.size}  •  kreni od obojene točke",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Navy,
            maxLines = 2
        )
        BoxWithConstraints(
            Modifier.fillMaxWidth().aspectRatio(1f)
                .clip(RoundedCornerShape(22.dp))
                .background(Color.White)
                .border(2.dp, Color(0xFFD5EAEE), RoundedCornerShape(22.dp))
                .semantics {
                    contentDescription = "Ploča za pisanje slova ${Alphabet.label(letter, uppercase)}. Prati označene poteze."
                }
        ) {
            Canvas(
                Modifier.fillMaxSize()
                    .pointerInput(letter, uppercase, resetToken, strokeIndex, finished) {
                        fun normalized(at: Offset): Dot {
                            val width = size.width.toFloat().coerceAtLeast(1f)
                            val height = size.height.toFloat().coerceAtLeast(1f)
                            return Dot(
                                (at.x / width).coerceIn(0f, 1f),
                                (at.y / height).coerceIn(0f, 1f)
                            )
                        }
                        detectDragGestures(
                            onDragStart = { position ->
                                if (!finished) {
                                    val start = normalized(position)
                                    gestureStarted = validator.start(start)
                                    touchPoints = if (gestureStarted) listOf(start) else emptyList()
                                }
                            },
                            onDrag = { change, _ ->
                                if (!finished && gestureStarted) {
                                    val point = normalized(change.position)
                                    validator.move(point)
                                    if (touchPoints.size < 900) touchPoints = touchPoints + point
                                }
                                change.consume()
                            },
                            onDragEnd = {
                                if (!finished && gestureStarted && validator.end()) {
                                    acceptedPaths = acceptedPaths + listOf(current)
                                    val done = acceptedPaths.size
                                    touchPoints = emptyList()
                                    reportProgress(done, strokes.size)
                                    if (done >= strokes.size) {
                                        finished = true
                                        completeNow()
                                    } else {
                                        strokeIndex = done
                                    }
                                } else {
                                    validator.end()
                                    touchPoints = emptyList()
                                }
                                gestureStarted = false
                            },
                            onDragCancel = {
                                validator.end()
                                gestureStarted = false
                                touchPoints = emptyList()
                            }
                        )
                    }
            ) {
                val width = size.width
                val height = size.height
                val ruleColor = Color(0xFF8FCBE9)
                val thick = if (letter.length > 1) 12.dp.toPx() else 19.dp.toPx()
                for (row in listOf(.12f, .48f, .86f)) {
                    drawLine(
                        ruleColor.copy(alpha = if (row == .48f) .63f else .9f),
                        Offset(width * .04f, height * row),
                        Offset(width * .96f, height * row),
                        1.7.dp.toPx(),
                        pathEffect = if (row == .48f) PathEffect.dashPathEffect(
                            floatArrayOf(9.dp.toPx(), 7.dp.toPx())
                        ) else null
                    )
                }
                strokes.forEachIndexed { index, stroke ->
                    val guidePath = strokeToPath(stroke, width, height)
                    val done = index < acceptedPaths.size
                    if (showGuide || done || showHelp) {
                        drawPath(
                            guidePath,
                            if (done) Teal.copy(alpha = .82f) else Color(0xFFE2E5E9),
                            style = Stroke(width = thick, cap = StrokeCap.Round)
                        )
                    }
                    if (!done && (showGuide || showHelp)) {
                        drawPath(
                            guidePath,
                            Color(0xFF74869B),
                            style = Stroke(
                                width = 3.5.dp.toPx(),
                                cap = StrokeCap.Round,
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(8.dp.toPx(), 9.dp.toPx())
                                )
                            )
                        )
                        if (stroke.size >= 2) {
                            val end = stroke.last()
                            val prev = stroke[stroke.lastIndex - 1]
                            val angle = atan2(
                                (end.y - prev.y) * height,
                                (end.x - prev.x) * width
                            )
                            val len = 13.dp.toPx()
                            val tip = Offset(end.x * width, end.y * height)
                            val a = Offset(
                                tip.x - cos(angle - .55f) * len,
                                tip.y - sin(angle - .55f) * len
                            )
                            val b = Offset(
                                tip.x - cos(angle + .55f) * len,
                                tip.y - sin(angle + .55f) * len
                            )
                            drawLine(
                                if (index == strokeIndex) Teal else Color(0xFF8293A9),
                                a, tip, 3.dp.toPx(), cap = StrokeCap.Round
                            )
                            drawLine(
                                if (index == strokeIndex) Teal else Color(0xFF8293A9),
                                b, tip, 3.dp.toPx(), cap = StrokeCap.Round
                            )
                        }
                    }
                }
                if (!finished) {
                    val first = current.first()
                    drawCircle(
                        Color.White,
                        13.dp.toPx(),
                        Offset(first.x * width, first.y * height)
                    )
                    drawCircle(
                        if (strokeIndex % 3 == 0) Teal else if (strokeIndex % 3 == 1) Coral else Blue,
                        8.dp.toPx(),
                        Offset(first.x * width, first.y * height)
                    )
                }
                if (touchPoints.size > 1) {
                    drawPath(
                        strokeToPath(touchPoints, width, height),
                        if (strokeIndex % 3 == 0) Teal else if (strokeIndex % 3 == 1) Coral else Blue,
                        style = Stroke(width = 13.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
            if ((showGuide || showHelp) && showNumbers) {
                val boardWidth = maxWidth
                val boardHeight = maxHeight
                strokes.forEachIndexed { index, stroke ->
                    val first = stroke.firstOrNull()
                    if (first != null) {
                        val dim = if (boardWidth < 300.dp) 25.dp else 29.dp
                        val color = when (index % 3) {
                            0 -> Teal
                            1 -> Coral
                            else -> Blue
                        }
                        Box(
                            Modifier.offset(
                                x = (boardWidth - dim) * first.x.coerceIn(0f, 1f),
                                y = (boardHeight - dim) * first.y.coerceIn(0f, 1f)
                            )
                                .size(dim).clip(CircleShape)
                                .background(if (index < acceptedPaths.size) Green else color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "${index + 1}",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
        if (showHelp) {
            Text(
                "Počni od broja 1, prati isprekidanu liniju do strelice i podigni prst. Nastavi redom.",
                fontSize = 13.sp,
                color = Navy,
                maxLines = 3
            )
        }
    }
}

private fun strokeToPath(stroke: LetterStroke, width: Float, height: Float): Path =
    Path().apply {
        stroke.forEachIndexed { index, point ->
            if (index == 0) moveTo(point.x * width, point.y * height)
            else lineTo(point.x * width, point.y * height)
        }
    }
