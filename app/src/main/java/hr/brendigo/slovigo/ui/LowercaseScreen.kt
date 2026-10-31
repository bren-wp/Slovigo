package hr.brendigo.slovigo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.brendigo.slovigo.model.Alphabet

/**
 * Screen 04: small printed Croatian alphabet.
 *
 * A dedicated route and layout, not a bitmap of the reference. The grid adapts
 * to available width and scrolls independently while navigation stays visible.
 * Lowercase progress and accessible names are always separate from uppercase.
 */
@Composable
fun SlovigoLowercaseScreen(
    completed: Set<String>,
    onLetter: (String) -> Unit,
    onBack: () -> Unit,
    onListen: () -> Unit,
    onHome: () -> Unit,
    onGames: () -> Unit,
    onSettings: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val shortWindow = maxHeight < 590.dp
        val phone = maxWidth < 650.dp
        val smallPhone = maxWidth < 355.dp
        val completedHere = remember(completed) { completed.intersect(Alphabet.letters.toSet()) }
        val recommended = remember(completedHere) {
            Alphabet.letters.firstOrNull { it !in completedHere } ?: Alphabet.letters.first()
        }
        Box(Modifier.fillMaxSize()) {
            LowercaseLandscape()
            Column(
                Modifier.fillMaxSize()
                    .widthIn(max = if (phone) 575.dp else 960.dp)
                    .align(Alignment.TopCenter)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = if (phone) 12.dp else 24.dp, vertical = 4.dp)
                        .height(51.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onBack,
                        modifier = Modifier.size(46.dp),
                        color = Color.White.copy(alpha = .96f),
                        shape = RoundedCornerShape(15.dp),
                        shadowElevation = 2.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("❮", color = Navy, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    SplashWordmark(if (smallPhone) 25 else if (phone) 30 else 38)
                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.width(46.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .height(if (shortWindow) 104.dp else if (phone) 145.dp else 128.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(if (shortWindow) 3.dp else 7.dp)
                    ) {
                        Text(
                            "Mala tiskana slova",
                            fontWeight = FontWeight.Black,
                            fontSize = if (smallPhone) 25.sp else if (phone) 30.sp else 36.sp,
                            lineHeight = if (smallPhone) 29.sp else if (phone) 34.sp else 40.sp,
                            color = Navy,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "Odaberi slovo za vježbanje",
                            fontSize = if (smallPhone) 13.sp else 16.sp,
                            color = Navy.copy(alpha = .79f),
                            maxLines = 2
                        )
                        if (!shortWindow) {
                            Text(
                                "a   b   c   č   ć",
                                fontSize = if (phone) 18.sp else 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Teal,
                                maxLines = 1
                            )
                        }
                    }
                    SplashPencil(
                        Modifier.width(if (smallPhone) 92.dp else if (phone) 125.dp else 145.dp)
                            .fillMaxHeight()
                    )
                }
                Row(
                    Modifier.fillMaxWidth()
                        .padding(horizontal = if (smallPhone) 10.dp else 17.dp, vertical = 7.dp)
                        .height(60.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(54.dp),
                        onClick = onListen,
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("♫", color = Teal, fontSize = 30.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Surface(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(24.dp),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("★", fontSize = 30.sp, color = Yellow)
                            Column(
                                Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    "${completedHere.size} / 30",
                                    color = Navy,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 19.sp
                                )
                                LinearProgressIndicator(
                                    progress = { completedHere.size / 30f },
                                    modifier = Modifier.fillMaxWidth()
                                        .height(8.dp).clip(RoundedCornerShape(7.dp)),
                                    color = Teal,
                                    trackColor = Color(0xFFD9E3EE),
                                    gapSize = 0.dp,
                                    drawStopIndicator = {}
                                )
                            }
                        }
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = if (phone) 9.dp else 18.dp, vertical = 5.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Ivory,
                    shadowElevation = 4.dp
                ) {
                    BoxWithConstraints(Modifier.fillMaxSize().padding(if (smallPhone) 9.dp else 13.dp)) {
                        val cols = when {
                            maxWidth < 290.dp -> 4
                            maxWidth < 540.dp -> 5
                            maxWidth < 730.dp -> 6
                            else -> 8
                        }
                        val gutter = if (smallPhone) 6.dp else 9.dp
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(cols),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(gutter),
                            verticalArrangement = Arrangement.spacedBy(gutter),
                            contentPadding = PaddingValues(top = 2.dp, bottom = 14.dp)
                        ) {
                            items(Alphabet.letters, key = { it }) { letter ->
                                SmallLetterCard(
                                    letter = letter,
                                    done = letter in completedHere,
                                    focused = letter == recommended,
                                    compact = smallPhone,
                                    onClick = { onLetter(letter) }
                                )
                            }
                        }
                    }
                }
                LowercaseBottomBar(
                    onHome = onHome,
                    onGames = onGames,
                    onSettings = onSettings
                )
            }
        }
    }
}

@Composable
private fun SmallLetterCard(
    letter: String,
    done: Boolean,
    focused: Boolean,
    compact: Boolean,
    onClick: () -> Unit
) {
    val label = Alphabet.label(letter, false)
    val digraph = letter.length > 1
    val hue = when {
        focused -> Teal
        done -> Color(0xFFA8EDA3)
        digraph -> Color(0xFFF9C1D7)
        letter == "Š" || letter == "Ž" -> Color(0xFFFFDF96)
        else -> Color(0xFFCCD5FC)
    }
    Surface(
        modifier = Modifier.fillMaxWidth().aspectRatio(1f)
            .semantics { contentDescription = "Malo tiskano slovo $label. ${if (done) "Naučeno" else "Za vježbanje"}." },
        onClick = onClick,
        shape = RoundedCornerShape(if (compact) 14.dp else 19.dp),
        color = hue,
        shadowElevation = if (focused) 6.dp else 1.dp,
        border = if (focused) androidx.compose.foundation.BorderStroke(3.dp, Color.White)
            else androidx.compose.foundation.BorderStroke(1.dp, Navy.copy(alpha = .07f))
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = if (focused) Color.White else Navy,
                fontSize = if (digraph) (if (compact) 21.sp else 25.sp)
                    else (if (compact) 32.sp else 39.sp),
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Clip
            )
            if (done && !focused) {
                Text(
                    "★",
                    fontSize = 15.sp,
                    color = Color(0xFFFFBA08),
                    modifier = Modifier.align(Alignment.TopEnd).padding(2.dp)
                )
            }
        }
    }
}

@Composable
private fun LowercaseBottomBar(
    onHome: () -> Unit,
    onGames: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(topStart = 29.dp, topEnd = 29.dp),
        shadowElevation = 7.dp
    ) {
        Row(
            Modifier.fillMaxWidth().height(70.dp).padding(horizontal = 6.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            listOf(
                Triple("⌂", "Početna", onHome),
                Triple("▣", "Slova", {}),
                Triple("♧", "Igre", onGames),
                Triple("⚙", "Postavke", onSettings)
            ).forEach { (glyph, title, action) ->
                val active = title == "Slova"
                Column(
                    Modifier.weight(1f).fillMaxHeight()
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (active) Teal else Color.Transparent)
                        .clickable(enabled = !active, role = Role.Button, onClick = action),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(glyph, fontSize = 25.sp, color = if (active) Color.White else Navy, maxLines = 1)
                    Text(
                        title,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (active) Color.White else Navy,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun LowercaseLandscape() {
    Canvas(
        Modifier.fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF7FD2EF), Color(0xFFC5F1FC), Color(0xFFF0FAF4), Color(0xFF9EE576))
                )
            )
    ) {
        val w = size.width
        val h = size.height
        val cloud = Color.White.copy(alpha = .67f)
        drawOval(cloud, Offset(w * .05f, h * .11f), Size(w * .30f, h * .045f))
        drawOval(cloud, Offset(w * .78f, h * .20f), Size(w * .25f, h * .047f))
        val ridge = Path().apply {
            moveTo(0f, h * .92f)
            cubicTo(w * .22f, h * .88f, w * .42f, h * .94f, w * .59f, h * .90f)
            cubicTo(w * .81f, h * .86f, w * .92f, h * .93f, w, h * .87f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(ridge, Color(0xFF87D16A))
    }
}
