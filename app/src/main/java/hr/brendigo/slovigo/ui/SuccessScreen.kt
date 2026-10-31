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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.brendigo.slovigo.model.Alphabet
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Screen 06: achievement / "Bravo!".
 *
 * The reference is portrait; wide or short windows use scrollable content and
 * two columns. All decorations are local vector/Compose primitives, not an
 * untouchable bitmap. The letter is a real text label for a11y and scaling.
 */
@Composable
fun SlovigoSuccessScreen(
    letter: String,
    uppercase: Boolean,
    completedCount: Int,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    onRepeat: () -> Unit,
    onNext: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 700.dp && maxWidth > maxHeight
        val compact = maxHeight < 725.dp || maxWidth < 360.dp
        val progress = completedCount.coerceIn(0, Alphabet.letters.size)
        val letterLabel = Alphabet.label(letter, uppercase)
        SuccessLandscape(Modifier.fillMaxSize())

        if (wide) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Column(
                    Modifier.weight(.92f).fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    SuccessHeader(onBack, onSettings, compact = true)
                    SuccessRibbon(Modifier.fillMaxWidth().height(175.dp))
                    Text(
                        "Uspješno si napisao slovo ${letterLabel}!",
                        color = Navy,
                        fontSize = 25.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Box(Modifier.fillMaxWidth().height(145.dp), contentAlignment = Alignment.CenterStart) {
                        SplashPencil(Modifier.width(185.dp).fillMaxHeight())
                    }
                }
                Column(
                    Modifier.weight(1f).fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    LetterAchievement(letterLabel, Modifier.fillMaxWidth().height(220.dp), false)
                    ThreeStars()
                    AchievementProgress(progress, Modifier.fillMaxWidth())
                    SuccessAction("↻  Pokušaj ponovno", onRepeat, filled = false, modifier = Modifier.fillMaxWidth())
                    SuccessAction("Sljedeće slovo  ❯", onNext, filled = true, modifier = Modifier.fillMaxWidth())
                }
            }
        } else {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(horizontal = if (compact) 12.dp else 20.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (compact) 9.dp else 13.dp)
            ) {
                SuccessHeader(onBack, onSettings, compact)
                SuccessRibbon(
                    Modifier.fillMaxWidth()
                        .height(if (compact) 155.dp else 202.dp)
                )
                Text(
                    "Uspješno si napisao slovo ${letterLabel}!",
                    fontSize = if (compact) 23.sp else 29.sp,
                    lineHeight = if (compact) 27.sp else 34.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    color = Navy,
                    maxLines = 3
                )
                Row(
                    Modifier.fillMaxWidth().height(if (compact) 176.dp else 225.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    SplashPencil(
                        Modifier.weight(.35f).fillMaxHeight()
                    )
                    LetterAchievement(
                        letterLabel,
                        Modifier.weight(.65f).fillMaxHeight(),
                        compact
                    )
                }
                ThreeStars()
                AchievementProgress(progress, Modifier.fillMaxWidth())
                SuccessAction(
                    "↻  Pokušaj ponovno",
                    onRepeat,
                    filled = false,
                    modifier = Modifier.fillMaxWidth()
                )
                SuccessAction(
                    "Sljedeće slovo  ❯",
                    onNext,
                    filled = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun SuccessHeader(onBack: () -> Unit, onSettings: () -> Unit, compact: Boolean) {
    Row(
        Modifier.fillMaxWidth().height(if (compact) 57.dp else 70.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            onClick = onBack,
            modifier = Modifier.size(49.dp),
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 3.dp,
            color = Color.White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("❮", color = Navy, fontSize = 29.sp)
            }
        }
        Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SplashWordmark(if (compact) 29 else 35)
            Text(
                "Pisanje slova za djecu",
                color = Navy,
                fontSize = if (compact) 11.sp else 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
        Surface(
            onClick = onSettings,
            modifier = Modifier.size(49.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White.copy(alpha = .94f),
            shadowElevation = 3.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("⚙", fontSize = 27.sp, color = Teal)
            }
        }
    }
}

@Composable
private fun SuccessRibbon(modifier: Modifier) {
    Box(modifier, contentAlignment = Alignment.BottomCenter) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val center = Offset(w * .5f, h * .37f)
            drawCircle(Color.White.copy(alpha = .34f), w * .24f, center)
            val star = starPath(center, w * .235f, w * .118f, 5)
            drawPath(star, Brush.linearGradient(listOf(
                Color(0xFFFFF1A8), Color(0xFFFFCC24), Color(0xFFFFA500)
            )))
            drawPath(star, Color(0xFFFCE79C),
                style = Stroke(width = 4.dp.toPx()))
            // Friendly eyes and smile, drawn directly onto the star.
            drawArc(Navy, 195f, 140f, false,
                Offset(w*.425f,h*.30f),Size(w*.045f,h*.055f),
                style=Stroke(width=3.dp.toPx()))
            drawArc(Navy, 195f, 140f, false,
                Offset(w*.53f,h*.30f),Size(w*.045f,h*.055f),
                style=Stroke(width=3.dp.toPx()))
            drawOval(Coral.copy(alpha=.75f),Offset(w*.39f,h*.39f),Size(w*.045f,h*.045f))
            drawOval(Coral.copy(alpha=.75f),Offset(w*.565f,h*.39f),Size(w*.045f,h*.045f))
            drawArc(Navy, 8f, 165f, false,
                Offset(w*.469f,h*.39f),Size(w*.065f,h*.075f),
                style=Stroke(width=4.dp.toPx()))
            val ribbon = Path().apply {
                moveTo(w*.08f,h*.65f)
                lineTo(w*.16f,h*.55f)
                quadraticTo(w*.50f,h*.45f,w*.84f,h*.55f)
                lineTo(w*.92f,h*.65f)
                lineTo(w*.83f,h*.76f)
                quadraticTo(w*.50f,h*.67f,w*.17f,h*.76f)
                close()
            }
            drawPath(ribbon, Brush.verticalGradient(listOf(
                Color(0xFFFF8896),Color(0xFFF45E76),Color(0xFFDB4462)
            )))
            for (i in 0..5) {
                val angle = (i * 60f - 150f) * PI / 180.0
                val cx = w*.5f + cos(angle).toFloat()*w*.33f
                val cy = h*.36f + sin(angle).toFloat()*h*.35f
                drawLine(
                    Yellow,
                    Offset(cx,cy),
                    Offset(cx+cos(angle).toFloat()*w*.05f,
                        cy+sin(angle).toFloat()*h*.07f),
                    4.dp.toPx()
                )
            }
        }
        Text(
            "Bravo!",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 47.sp,
            maxLines = 1,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 9.dp)
        )
    }
}

private fun starPath(center: Offset, outer: Float, inner: Float, tips: Int): Path =
    Path().apply {
        repeat(tips * 2) { index ->
            val a = ((index * PI / tips) - PI / 2).toFloat()
            val radius = if (index % 2 == 0) outer else inner
            val x = center.x + cos(a) * radius
            val y = center.y + sin(a) * radius
            if (index == 0) moveTo(x,y) else lineTo(x,y)
        }
        close()
    }

@Composable
private fun LetterAchievement(letterLabel: String, modifier: Modifier, compact: Boolean) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(29.dp),
        shadowElevation = 5.dp,
        color = Color.White,
        border = androidx.compose.foundation.BorderStroke(4.dp, Teal)
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                letterLabel,
                color = Navy,
                fontWeight = FontWeight.Black,
                fontSize = when {
                    letterLabel.length > 1 && compact -> 68.sp
                    letterLabel.length > 1 -> 100.sp
                    compact -> 105.sp
                    else -> 135.sp
                },
                maxLines = 1,
                softWrap = false,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Clip,
                modifier = Modifier.padding(8.dp)
                    .semantics { contentDescription = "Uspješno napisano slovo $letterLabel" }
            )
        }
    }
}

@Composable
private fun ThreeStars() {
    Row(
        Modifier.fillMaxWidth()
            .semantics { contentDescription = "Tri zvjezdice za uspješno napisano slovo" },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { _ ->
            Canvas(Modifier.size(54.dp).padding(horizontal = 3.dp)) {
                val star = starPath(
                    Offset(size.width*.5f,size.height*.5f),
                    size.minDimension*.46f,
                    size.minDimension*.21f,
                    5
                )
                drawPath(star, Brush.verticalGradient(listOf(
                    Color(0xFFFFE878), Color(0xFFFFB700), Color(0xFFF99A0F)
                )))
                drawPath(star, Color(0xFFF4A622),
                    style = Stroke(width = 2.dp.toPx()))
            }
        }
    }
}

@Composable
private fun AchievementProgress(count: Int, modifier: Modifier) {
    Surface(
        modifier,
        color = Color.White.copy(alpha = .94f),
        shadowElevation = 3.dp,
        shape = RoundedCornerShape(30.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal=18.dp, vertical=12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            Text("▣", fontSize = 26.sp, color = Teal)
            Column(Modifier.weight(1f), verticalArrangement=Arrangement.spacedBy(5.dp)) {
                Text(
                    "$count od 30 naučenih slova",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = Navy,
                    maxLines = 2
                )
                LinearProgressIndicator(
                    progress = { count / 30f },
                    color = Teal,
                    trackColor = Color(0xFFD5D7DB),
                    gapSize = 0.dp,
                    drawStopIndicator = {},
                    modifier = Modifier.fillMaxWidth().height(8.dp)
                        .clip(RoundedCornerShape(9.dp))
                )
            }
        }
    }
}

@Composable
private fun SuccessAction(
    label: String,
    onClick: () -> Unit,
    filled: Boolean,
    modifier: Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 58.dp)
            .semantics { contentDescription = label },
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 5.dp),
        shape = RoundedCornerShape(32.dp),
        border = if (filled) null else androidx.compose.foundation.BorderStroke(3.dp, Teal),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (filled) Teal else Color.White,
            contentColor = if (filled) Color.White else Teal
        )
    ) {
        Text(
            label,
            fontWeight = FontWeight.Black,
            fontSize = 21.sp,
            maxLines = 2,
            lineHeight = 25.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SuccessLandscape(modifier: Modifier) {
    Canvas(
        modifier.background(Brush.verticalGradient(listOf(
            Color(0xFF75CFF4),Color(0xFFCCF2FF),Color(0xFFF4F6DA),Color(0xFFB7E96D)
        )))
    ) {
        val w = size.width
        val h = size.height
        val cloud = Color.White.copy(alpha=.65f)
        drawOval(cloud, Offset(w*.04f,h*.20f),Size(w*.23f,h*.045f))
        drawOval(cloud, Offset(w*.78f,h*.16f),Size(w*.23f,h*.05f))
        val hills = Path().apply {
            moveTo(0f,h*.89f)
            cubicTo(w*.20f,h*.81f,w*.38f,h*.93f,w*.62f,h*.86f)
            cubicTo(w*.79f,h*.82f,w*.90f,h*.92f,w,h*.86f)
            lineTo(w,h);lineTo(0f,h);close()
        }
        drawPath(hills,Color(0xFF7FCF77))
        val meadow = Path().apply {
            moveTo(0f,h*.95f)
            cubicTo(w*.30f,h*.90f,w*.48f,h*.98f,w*.72f,h*.93f)
            cubicTo(w*.84f,h*.90f,w*.94f,h*.95f,w,h*.92f)
            lineTo(w,h);lineTo(0f,h);close()
        }
        drawPath(meadow,Color(0xFF9BE35B))
    }
}
