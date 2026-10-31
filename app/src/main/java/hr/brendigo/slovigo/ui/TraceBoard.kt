package hr.brendigo.slovigo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.brendigo.slovigo.model.Alphabet
import hr.brendigo.slovigo.model.Dot
import hr.brendigo.slovigo.model.Stroke as LetterStroke
import hr.brendigo.slovigo.model.StrokeTracker

/**
 * Canvas is normalized to a square. Input is converted to 0..1 coordinates
 * before validation to avoid density- or screen-size-dependent results.
 */
@Composable
fun TraceBoard(
    letter: String,
    uppercase: Boolean,
    showGuide: Boolean,
    onCompleted: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strokes = remember(letter, uppercase) { Alphabet.strokes(letter, uppercase) }
    var strokeIndex by remember(letter, uppercase) { mutableIntStateOf(0) }
    var touchPoints by remember(letter, uppercase, strokeIndex) { mutableStateOf(emptyList<Dot>()) }
    var acceptedPaths by remember(letter, uppercase) { mutableStateOf(emptyList<LetterStroke>()) }
    val selectedStroke = strokes.getOrNull(strokeIndex)
    if (selectedStroke.isNullOrEmpty()) {
        Text("Putanja nije dostupna. Vrati se na izbor slova.", color = Navy)
        return
    }
    val validator = remember(letter, uppercase, strokeIndex) {
        StrokeTracker(selectedStroke)
    }
    var validGesture by remember(letter, uppercase, strokeIndex) { mutableStateOf(false) }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            "Potez ${strokeIndex + 1} / ${strokes.size} — povuci od označene točke",
            style = MaterialTheme.typography.titleMedium,
            color = Navy
        )
        Canvas(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.White, RoundedCornerShape(24.dp))
                .border(2.dp, Teal.copy(alpha = .35f), RoundedCornerShape(24.dp))
                .pointerInput(letter, uppercase, strokeIndex) {
                    fun normalized(p: Offset) = Dot(
                        p.x / size.width.toFloat(),
                        p.y / size.height.toFloat()
                    )
                    detectDragGestures(
                        onDragStart = { point ->
                            val norm = normalized(point)
                            validGesture = validator.start(norm)
                            touchPoints = if (validGesture) listOf(norm) else emptyList()
                        },
                        onDrag = { change, _ ->
                            val norm = normalized(change.position)
                            if (validGesture) {
                                validator.move(norm)
                                touchPoints = (touchPoints + norm).takeLast(600)
                            }
                            change.consume()
                        },
                        onDragEnd = {
                            if (validator.end()) {
                                acceptedPaths = acceptedPaths + listOf(strokes[strokeIndex])
                                touchPoints = emptyList()
                                if (strokeIndex == strokes.lastIndex) onCompleted()
                                else strokeIndex++
                            } else touchPoints = emptyList()
                            validGesture = false
                        },
                        onDragCancel = {
                            validator.end()
                            validGesture = false
                            touchPoints = emptyList()
                        }
                    )
                }
        ) {
            val w = size.width
            val h = size.height
            for (y in listOf(.16f, .5f, .88f)) {
                drawLine(Color(0xFFAED9EC), Offset(w*.05f,h*y), Offset(w*.95f,h*y), 2.dp.toPx())
            }
            if (showGuide) {
                strokes.forEachIndexed { i, stroke ->
                    val path = strokeToPath(stroke,w,h)
                    drawPath(
                        path,
                        when {
                            i < strokeIndex -> Teal.copy(alpha = .9f)
                            i == strokeIndex -> Color(0xFFB5C1CC)
                            else -> Color(0xFFE1E7EC)
                        },
                        style = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)
                    )
                    if (i == strokeIndex) {
                        val first = stroke.first()
                        drawCircle(Yellow, 16.dp.toPx(), Offset(first.x*w, first.y*h))
                        drawCircle(Navy, 5.dp.toPx(), Offset(first.x*w, first.y*h))
                    }
                }
            } else {
                acceptedPaths.forEach { drawPath(strokeToPath(it,w,h), Teal.copy(alpha=.75f), style=Stroke(width=14.dp.toPx(),cap=StrokeCap.Round)) }
                val active = strokes[strokeIndex].first()
                drawCircle(Yellow, 12.dp.toPx(), Offset(active.x*w,active.y*h))
            }
            if (touchPoints.size > 1) {
                drawPath(strokeToPath(touchPoints,w,h), Teal, style=Stroke(width=13.dp.toPx(),cap=StrokeCap.Round))
            }
        }
        Text(
            "Prati poteze redom. Nije potrebna internetska veza.",
            color = Navy.copy(alpha = .68f),
            fontSize = 13.sp
        )
    }
}

private fun strokeToPath(stroke: LetterStroke, width: Float, height: Float): Path = Path().apply {
    stroke.forEachIndexed { index, point ->
        if (index == 0) moveTo(point.x * width, point.y * height)
        else lineTo(point.x * width, point.y * height)
    }
}
