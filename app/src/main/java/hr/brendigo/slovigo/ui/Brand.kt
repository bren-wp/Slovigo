package hr.brendigo.slovigo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Teal = Color(0xFF168D99)
val TealDark = Color(0xFF067B86)
val Yellow = Color(0xFFFFC857)
val Coral = Color(0xFFFF7477)
val Blue = Color(0xFF6C84E8)
val Ivory = Color(0xFFFFF9EF)
val Navy = Color(0xFF243449)
val Sky = Color(0xFFD6F5FF)
val Green = Color(0xFF9EDB64)

@Composable
fun SlovigoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Teal,
            onPrimary = Color.White,
            primaryContainer = Color(0xFFCDF6F1),
            secondary = Yellow,
            onSecondary = Navy,
            tertiary = Coral,
            background = Ivory,
            onBackground = Navy,
            surface = Color.White,
            onSurface = Navy,
            outline = Color(0xFFB5CED5)
        ),
        typography = Typography(
            headlineLarge = Typography().headlineLarge.copy(fontWeight = FontWeight.ExtraBold),
            headlineMedium = Typography().headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
            titleLarge = Typography().titleLarge.copy(fontWeight = FontWeight.Bold),
            titleMedium = Typography().titleMedium.copy(fontWeight = FontWeight.Bold),
            labelLarge = Typography().labelLarge.copy(fontWeight = FontWeight.Bold)
        ),
        content = content
    )
}

@Composable
fun SkyBackground(content: @Composable BoxScope.() -> Unit) {
    Box(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color(0xFFBFEFFF), Color(0xFFEAFBFF), Ivory, Color(0xFFE7F8CA))
            )
        )
    ) {
        Canvas(Modifier.fillMaxSize().align(Alignment.BottomCenter)) {
            val h = size.height
            val w = size.width
            val hill = Path().apply {
                moveTo(0f, h * 0.93f)
                cubicTo(w * 0.18f, h * 0.88f, w * 0.37f, h * 0.96f, w * 0.55f, h * 0.91f)
                cubicTo(w * 0.72f, h * 0.87f, w * 0.9f, h * 0.91f, w, h * 0.88f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(hill, Color(0xFFB6E983))
            drawCircle(Color.White.copy(alpha = 0.55f), radius = w * 0.09f, center = Offset(w * 0.12f, h * 0.11f))
            drawCircle(Color.White.copy(alpha = 0.60f), radius = w * 0.065f, center = Offset(w * 0.17f, h * 0.13f))
            drawCircle(Color.White.copy(alpha = 0.50f), radius = w * 0.075f, center = Offset(w * 0.81f, h * 0.22f))
        }
        content()
    }
}

@Composable
fun AppLogo(modifier: Modifier = Modifier, fontSize: Int = 36) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        val letters = "SLOVIGO"
        val colors = listOf(Teal, Blue, Yellow, Coral, Blue, Teal, Coral)
        letters.forEachIndexed { index, c ->
            Text(c.toString(), color = colors[index], fontWeight = FontWeight.Black, fontSize = fontSize.sp)
        }
    }
}

/** Vector pencil mascot rendered at every DPI without external resources. */
@Composable
fun PencilMascot(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val x = size.width
        val y = size.height
        rotate(-14f) {
            val body = Path().apply {
                moveTo(x * .25f, y * .23f)
                lineTo(x * .75f, y * .23f)
                lineTo(x * .75f, y * .72f)
                lineTo(x * .5f, y * .98f)
                lineTo(x * .25f, y * .72f)
                close()
            }
            drawPath(body, Yellow)
            drawPath(body, Navy.copy(alpha=.12f), style=Stroke(width=x*.025f))
            drawRect(Coral, Offset(x*.25f, y*.12f), androidx.compose.ui.geometry.Size(x*.5f, y*.14f))
            drawLine(Navy.copy(alpha=.25f), Offset(x*.25f,y*.25f), Offset(x*.75f,y*.25f), x*.03f)
            val tip = Path().apply {
                moveTo(x*.25f,y*.72f); lineTo(x*.75f,y*.72f); lineTo(x*.5f,y*.98f); close()
            }
            drawPath(tip, Color(0xFFF4C391))
            val lead = Path().apply {
                moveTo(x*.43f,y*.91f); lineTo(x*.57f,y*.91f); lineTo(x*.5f,y*.98f); close()
            }
            drawPath(lead, Navy)
            drawCircle(Navy, x*.038f, Offset(x*.4f,y*.41f))
            drawCircle(Navy, x*.038f, Offset(x*.6f,y*.41f))
            drawCircle(Color.White, x*.011f, Offset(x*.39f,y*.4f))
            drawCircle(Color.White, x*.011f, Offset(x*.59f,y*.4f))
            drawArc(Navy, 0f, 180f, false, Offset(x*.43f,y*.45f), androidx.compose.ui.geometry.Size(x*.14f,y*.1f), style=Stroke(width=x*.023f))
            drawCircle(Coral.copy(alpha=.65f), x*.05f, Offset(x*.31f,y*.5f))
            drawCircle(Coral.copy(alpha=.65f), x*.05f, Offset(x*.69f,y*.5f))
            drawLine(Navy, Offset(x*.25f,y*.54f),Offset(x*.07f,y*.43f),x*.035f)
            drawLine(Navy, Offset(x*.75f,y*.54f),Offset(x*.94f,y*.36f),x*.035f)
            drawCircle(Color.White,x*.058f,Offset(x*.06f,y*.42f))
            drawCircle(Color.White,x*.058f,Offset(x*.94f,y*.34f))
        }
    }
}

@Composable
fun WhiteCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = Color.White.copy(alpha = .96f),
        shadowElevation = 5.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Teal.copy(alpha = .12f))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable
fun BigAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, background: Color = Teal, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        modifier = modifier.defaultMinSize(minHeight = 56.dp),
        enabled = enabled,
        shape = RoundedCornerShape(22.dp),
        colors = ButtonDefaults.buttonColors(containerColor = background, contentColor = if (background == Yellow) Navy else Color.White)
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
    }
}

@Composable
fun SmallAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedButton(onClick, modifier.defaultMinSize(minHeight = 48.dp), shape = RoundedCornerShape(18.dp)) {
        Text(text, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable
fun ScreenTitle(title: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.headlineMedium, color = Navy)
        if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodyLarge, color = Navy.copy(alpha = .7f))
    }
}

@Composable
fun ProgressBadge(done: Int, total: Int, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("⭐  $done / $total slova", style = MaterialTheme.typography.titleMedium, color = Navy)
        LinearProgressIndicator(
            progress = { if (total == 0) 0f else done / total.toFloat() },
            modifier = Modifier.fillMaxWidth().height(9.dp),
            color = Teal,
            trackColor = Color(0xFFE2EBF0),
            gapSize = 0.dp,
            drawStopIndicator = {}
        )
    }
}
