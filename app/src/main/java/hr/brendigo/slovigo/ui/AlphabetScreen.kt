package hr.brendigo.slovigo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.brendigo.slovigo.model.Alphabet

/** Screen 03. Local, scalable Compose art; UI is not a resized bitmap. */
@Composable
fun SlovigoAlphabetScreen(
    uppercase: Boolean,
    completed: Set<String>,
    onLetter: (String) -> Unit,
    onBack: () -> Unit,
    onSound: () -> Unit,
    onHome: () -> Unit,
    onGames: () -> Unit,
    onSettings: () -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxWidth < 360.dp || maxHeight < 620.dp
        val wide = maxWidth >= 720.dp
        val maxContent = if (wide) 960.dp else 570.dp
        Box(Modifier.fillMaxSize()) {
            AlphabetSky()
            Column(
                Modifier.fillMaxSize().widthIn(max = maxContent).align(Alignment.TopCenter)
            ) {
                Row(
                    Modifier.fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.size(48.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Text("❮", fontSize = 29.sp, color = Navy) }
                    Spacer(Modifier.weight(1f))
                    SplashWordmark(if (compact) 24 else 34)
                    Spacer(Modifier.weight(1f))
                    // Keep symmetrical safe touch area.
                    Spacer(Modifier.width(48.dp))
                }
                Row(
                    Modifier.fillMaxWidth()
                        .height(if (compact) 128.dp else 166.dp)
                        .padding(start = if (compact) 14.dp else 23.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(
                            if (uppercase) "Velika tiskana slova" else "Mala tiskana slova",
                            color = Navy,
                            fontWeight = FontWeight.Black,
                            fontSize = if (compact) 25.sp else 31.sp,
                            lineHeight = if (compact) 30.sp else 35.sp,
                            maxLines = 2
                        )
                        Text(
                            "Odaberi slovo za vježbanje",
                            color = Navy.copy(alpha = .78f),
                            fontSize = if (compact) 14.sp else 17.sp,
                            maxLines = 2
                        )
                    }
                    SplashPencil(
                        Modifier.width(if (compact) 104.dp else 144.dp)
                            .fillMaxHeight()
                    )
                }
                Row(
                    Modifier.padding(horizontal = 17.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .height(61.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    Surface(
                        onClick = onSound,
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        color = Color.White,
                        shadowElevation = 3.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("◖))", color = Teal, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Surface(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = RoundedCornerShape(25.dp),
                        color = Color.White,
                        shadowElevation = 3.dp
                    ) {
                        Row(
                            Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(9.dp)
                        ) {
                            Text("★", color = Yellow, fontSize = 32.sp)
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "${completed.size} / 30",
                                    color = Navy,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black
                                )
                                LinearProgressIndicator(
                                    progress = { completed.size / Alphabet.letters.size.toFloat() },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(6.dp)),
                                    color = Teal,
                                    trackColor = Color(0xFFDDE6ED),
                                    gapSize = 0.dp,
                                    drawStopIndicator = {}
                                )
                            }
                        }
                    }
                }
                Surface(
                    Modifier.weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = if (compact) 8.dp else 12.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Ivory,
                    shadowElevation = 3.dp
                ) {
                    BoxWithConstraints(Modifier.fillMaxSize().padding(if (compact) 10.dp else 14.dp)) {
                        val columns = if (maxWidth >= 730.dp) 8 else if (maxWidth >= 530.dp) 6 else 5
                        val gap = if (compact) 6.dp else 9.dp
                        // Lazy grid remains bounded on phones, tablets and in landscape.
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(gap),
                            verticalArrangement = Arrangement.spacedBy(gap),
                            contentPadding = PaddingValues(bottom = 12.dp)
                        ) {
                            itemsIndexed(Alphabet.letters, key = { _, letter -> letter }) { index, letter ->
                                AlphabetCell(
                                    letter = letter,
                                    uppercase = uppercase,
                                    selected = index == 0,
                                    done = letter in completed,
                                    compact = compact,
                                    onClick = { onLetter(letter) }
                                )
                            }
                        }
                    }
                }
                AlphabetBottomNav(onHome, onGames, onSettings)
            }
        }
    }
}

@Composable
private fun AlphabetCell(
    letter: String,
    uppercase: Boolean,
    selected: Boolean,
    done: Boolean,
    compact: Boolean,
    onClick: () -> Unit,
) {
    val compound = letter.length > 1
    val color = when {
        selected -> Teal
        done -> Color(0xFFA9F39E)
        compound -> Color(0xFFFFC3D0)
        letter in setOf("Š", "Ž") -> Color(0xFFFFDC8E)
        else -> Color(0xFFC1CAFA)
    }
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
            .aspectRatio(1f)
            .semantics { contentDescription = "Vježbaj slovo ${Alphabet.label(letter, uppercase)}" },
        shape = RoundedCornerShape(if (compact) 15.dp else 19.dp),
        color = color,
        shadowElevation = if (selected) 6.dp else 1.dp,
        border = if (selected) androidx.compose.foundation.BorderStroke(3.dp, Color.White) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                Alphabet.label(letter, uppercase),
                fontSize = if (compound) (if (compact) 22 else 27).sp else (if (compact) 30 else 39).sp,
                fontWeight = FontWeight.Black,
                color = if (selected) Color.White else Navy,
                maxLines = 1,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Clip
            )
            if (done && !selected) {
                Text(
                    "★",
                    color = Color(0xFFF9BB13),
                    fontSize = if (compact) 14.sp else 17.sp,
                    modifier = Modifier.align(Alignment.TopEnd).padding(end = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun AlphabetBottomNav(onHome: () -> Unit, onGames: () -> Unit, onSettings: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .shadow(5.dp, RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
            .clip(RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp))
            .background(Color.White)
            .navigationBarsPadding()
            .height(65.dp)
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(
            Triple("⌂", "Početna", onHome),
            Triple("▣", "Slova", {}),
            Triple("♧", "Igre", onGames),
            Triple("⚙", "Postavke", onSettings)
        ).forEach { (icon, label, click) ->
            val active = label == "Slova"
            Column(
                Modifier.weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(19.dp))
                    .background(if (active) Teal else Color.Transparent)
                    .clickable(enabled = !active, onClick = click),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(icon, fontSize = 24.sp, color = if (active) Color.White else Navy)
                Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (active) Color.White else Navy)
            }
        }
    }
}

@Composable
private fun AlphabetSky() {
    Canvas(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color(0xFF8BDEFC), Color(0xFFE6F8FF), Color(0xFF8CD96B))
            )
        )
    ) {
        val w = size.width
        val h = size.height
        // Cloud banks are decorative, not part of the touch area.
        drawOval(Color.White.copy(alpha = .65f), Offset(w * .08f, h * .15f), androidx.compose.ui.geometry.Size(w * .35f, h * .05f))
        drawOval(Color.White.copy(alpha = .6f), Offset(w * .75f, h * .12f), androidx.compose.ui.geometry.Size(w * .25f, h * .05f))
        drawOval(Color(0xFF7ED46A), Offset(-w * .1f, h * .94f), androidx.compose.ui.geometry.Size(w * .75f, h * .14f))
        drawOval(Color(0xFFACE371), Offset(w * .45f, h * .93f), androidx.compose.ui.geometry.Size(w * .75f, h * .15f))
    }
}
