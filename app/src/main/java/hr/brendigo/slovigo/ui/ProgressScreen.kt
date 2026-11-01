package hr.brendigo.slovigo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.brendigo.slovigo.data.LocalProgress
import hr.brendigo.slovigo.model.Alphabet
import hr.brendigo.slovigo.model.ProgressInsights
import kotlin.math.max

/**
 * Screen 07 — "Moj napredak".
 *
 * Live local statistics; no sample minutes or invented achievements. The
 * reference illustration serves as a guide, not a full-screen background
 * image. Cards reflow into two columns on wider landscape/tablet windows.
 */
@Composable
fun SlovigoProgressScreen(
    upperCompleted: Set<String>,
    lowerCompleted: Set<String>,
    weeklyActivity: List<LocalProgress.ActivityDay>,
    streakDays: Int,
    onPractice: () -> Unit,
    onHome: () -> Unit,
    onLetters: () -> Unit,
    onSettings: () -> Unit
) {
    val upper = upperCompleted.count { it in Alphabet.letters }
    val lower = lowerCompleted.count { it in Alphabet.letters }
    val total = ProgressInsights.total(upper, lower)
    val next = ProgressInsights.nextGoal(upperCompleted, lowerCompleted)
    val badges = remember(total, streakDays) {
        listOf(
            ProgressBadgeInfo("Prvih 5 slova", "★", Color(0xFFFFBE24),
                ProgressInsights.firstFiveLearned(total), "Nauči prvih pet velikih ili malih slova"),
            ProgressBadgeInfo("Uredan rukopis", "✎", Color(0xFF6C84E8),
                false, "Ova značka nije dostupna dok se ne uvede ocjenjivanje rukopisa"),
            ProgressBadgeInfo("7 dana zaredom", "✓", Color(0xFF00B88B),
                ProgressInsights.weekStreak(streakDays), "Završi barem jednu vježbu sedam dana zaredom"),
            ProgressBadgeInfo("10 slova naučeno", "◎", Color(0xFFF36F6F),
                ProgressInsights.tenLearned(total), "Nauči ukupno deset velikih ili malih slova"),
            ProgressBadgeInfo("Majstor abecede", "♛", Color(0xFFA8B3BF),
                ProgressInsights.masterAlphabet(total), "Nauči svih 60 velikih i malih slova")
        )
    }
    var badgeDialog by remember { mutableStateOf(false) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 720.dp
        val compact = maxWidth < 355.dp
        ProgressSky()
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(horizontal = if (wide) 24.dp else if (compact) 11.dp else 17.dp)
                    .padding(top = 8.dp, bottom = 15.dp),
                verticalArrangement = Arrangement.spacedBy(if (compact) 12.dp else 16.dp)
            ) {
                ProgressHeader(
                    compact = compact,
                    onShowBadges = { badgeDialog = true },
                    onSettings = onSettings
                )
                if (wide) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ProgressOverview(total, next, Modifier.fillMaxWidth())
                            CaseProgress(upper, lower, false)
                            PracticeContinue(onPractice)
                        }
                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            WeeklyPractice(weeklyActivity)
                            AchievementsCard(badges, { badgeDialog = true }, compact)
                        }
                    }
                } else {
                    ProgressOverview(total, next, Modifier.fillMaxWidth())
                    CaseProgress(upper, lower, compact)
                    WeeklyPractice(weeklyActivity)
                    AchievementsCard(badges, { badgeDialog = true }, compact)
                    PracticeContinue(onPractice)
                }
            }
            ProgressNavigation(onHome, onLetters, onSettings)
        }

        if (badgeDialog) {
            AlertDialog(
                onDismissRequest = { badgeDialog = false },
                title = { Text("Moje značke", color = Navy, fontWeight = FontWeight.Black) },
                text = {
                    Column(
                        Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        badges.forEach { badge ->
                            Text(
                                badge.title + " — " +
                                    (if (badge.earned) "Osvojeno" else "Zaključano"),
                                fontWeight = FontWeight.Bold,
                                color = if (badge.earned) Teal else Navy
                            )
                            Text(badge.description, color = Navy.copy(alpha = .78f), fontSize = 13.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { badgeDialog = false }) {
                        Text("Zatvori", color = Teal)
                    }
                }
            )
        }
    }
}

private data class ProgressBadgeInfo(
    val title: String,
    val icon: String,
    val color: Color,
    val earned: Boolean,
    val description: String
)

@Composable
private fun ProgressHeader(compact: Boolean, onShowBadges: () -> Unit, onSettings: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
            Modifier.fillMaxWidth().height(if (compact) 78.dp else 92.dp),
            verticalAlignment = Alignment.Top
        ) {
            SplashBook(Modifier.size(if (compact) 52.dp else 66.dp))
            Column(Modifier.weight(1f).padding(top = 8.dp)) {
                SplashWordmark(if (compact) 22 else 28)
                Text(
                    "Uči slova i lijepo piši",
                    color = Navy,
                    fontSize = if (compact) 11.sp else 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            ProgressHeaderIcon("🏆", "Značke", onShowBadges)
            Spacer(Modifier.width(6.dp))
            ProgressHeaderIcon("⚙", "Postavke", onSettings)
        }
        Row(
            Modifier.fillMaxWidth().height(if (compact) 100.dp else 138.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Moj napredak",
                    color = Navy,
                    fontSize = if (compact) 32.sp else 39.sp,
                    lineHeight = if (compact) 36.sp else 45.sp,
                    fontWeight = FontWeight.Black,
                    maxLines = 2
                )
                Canvas(Modifier.width(71.dp).height(7.dp)) {
                    drawLine(
                        Yellow, Offset(3f, size.height / 2),
                        Offset(size.width - 3f, size.height / 2),
                        5.dp.toPx(), cap = StrokeCap.Round
                    )
                }
                Text(
                    "Prati svoje učenje slova i pisanja.",
                    color = Navy.copy(alpha = .68f),
                    fontSize = if (compact) 12.sp else 15.sp,
                    lineHeight = 18.sp,
                    maxLines = 2
                )
            }
            SplashPencil(
                Modifier.width(if (compact) 104.dp else 143.dp)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun ProgressHeaderIcon(glyph: String, label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(43.dp).semantics { contentDescription = label },
        shape = RoundedCornerShape(15.dp),
        color = Color.White.copy(alpha = .88f),
        shadowElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(glyph, fontSize = 24.sp, color = Navy)
        }
    }
}

@Composable
private fun ProgressOverview(total: Int, next: List<String>, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(29.dp),
        color = Color.White.copy(alpha = .95f),
        shadowElevation = 4.dp
    ) {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BoxWithConstraints(Modifier.weight(.44f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                Canvas(Modifier.fillMaxSize()) {
                    val line = 19.dp.toPx().coerceAtMost(size.minDimension * .15f)
                    val padding = line / 2f + 5.dp.toPx()
                    drawArc(
                        color = Color(0xFFE4E9EB),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(padding, padding),
                        size = Size(size.width - padding * 2f, size.height - padding * 2f),
                        style = Stroke(line, cap = StrokeCap.Round)
                    )
                    if (total > 0) drawArc(
                        brush = Brush.sweepGradient(listOf(Teal, Color(0xFF00B9B0), Teal)),
                        startAngle = -90f,
                        sweepAngle = 360f * ProgressInsights.ratio(total, 60),
                        useCenter = false,
                        topLeft = Offset(padding, padding),
                        size = Size(size.width - padding * 2f, size.height - padding * 2f),
                        style = Stroke(line, cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$total / 60",
                        color = Navy,
                        fontWeight = FontWeight.Black,
                        fontSize = 27.sp,
                        maxLines = 1
                    )
                    Text("slova", fontSize = 15.sp, color = Navy)
                }
            }
            Column(
                Modifier.weight(.56f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressSmallIcon("✓", Color(0xFFD7F9DF), Teal)
                    Spacer(Modifier.width(7.dp))
                    Column {
                        Text(
                            "Uspješno naučeno",
                            color = Navy,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 19.sp
                        )
                        Text(
                            "$total naučenih oblika slova",
                            color = Navy.copy(alpha = .72f),
                            fontSize = 12.sp
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProgressSmallIcon("◎", Color(0xFFFFE8E8), Coral)
                    Spacer(Modifier.width(7.dp))
                    Column {
                        Text(
                            "Sljedeći cilj",
                            color = Navy,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            if (next.isEmpty()) "Sva slova su naučena!"
                            else "Nauči još: " + next.joinToString(", "),
                            color = Navy.copy(alpha = .72f),
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            maxLines = 3
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressSmallIcon(glyph: String, background: Color, color: Color) {
    Box(
        Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(background),
        contentAlignment = Alignment.Center
    ) { Text(glyph, color = color, fontSize = 24.sp, fontWeight = FontWeight.Black) }
}

@Composable
private fun CaseProgress(upper: Int, lower: Int, compact: Boolean) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        CaseBar("Velika slova", upper, Teal, Color(0xFFCEF9F0), Modifier.weight(1f), compact)
        CaseBar("Mala slova", lower, Yellow, Color(0xFFEDF2FA), Modifier.weight(1f), compact)
    }
}

@Composable
private fun CaseBar(
    title: String,
    count: Int,
    color: Color,
    background: Color,
    modifier: Modifier,
    compact: Boolean
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = background,
        shadowElevation = 3.dp
    ) {
        Column(
            Modifier.padding(horizontal = if (compact) 10.dp else 13.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Text(
                title,
                fontSize = if (compact) 14.sp else 16.sp,
                fontWeight = FontWeight.Black,
                color = Navy,
                maxLines = 2,
                lineHeight = 19.sp
            )
            Text("$count / 30", color = Navy, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(
                progress = { ProgressInsights.ratio(count, 30) },
                modifier = Modifier.fillMaxWidth().height(10.dp)
                    .clip(RoundedCornerShape(8.dp)),
                color = color,
                trackColor = Color.White,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )
        }
    }
}

@Composable
private fun WeeklyPractice(days: List<LocalProgress.ActivityDay>) {
    val chartDays = days.takeLast(7)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(27.dp),
        color = Color.White.copy(alpha = .96f),
        shadowElevation = 3.dp
    ) {
        Column(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressSmallIcon("▦", Color(0xFFD8F5FD), Teal)
                Spacer(Modifier.width(9.dp))
                Column {
                    Text(
                        "Tjedna aktivnost",
                        color = Navy,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Vrijeme uspješno dovršenih vježbi",
                        color = Navy.copy(alpha = .65f),
                        fontSize = 12.sp
                    )
                }
            }
            if (chartDays.isEmpty() || chartDays.all { it.seconds == 0 }) {
                Box(
                    Modifier.fillMaxWidth().height(110.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Još nema završenih vježbi ovaj tjedan.",
                        color = Navy.copy(alpha = .75f),
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            } else {
                val peak = max(1, chartDays.maxOf { it.seconds })
                Row(
                    Modifier.fillMaxWidth().height(124.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    chartDays.forEach { day ->
                        Column(
                            Modifier.weight(1f).fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Text(
                                if (day.seconds == 0) "0"
                                else if (day.seconds < 60) "<1"
                                else (day.seconds / 60).toString(),
                                fontSize = 10.sp,
                                color = Navy.copy(alpha = .7f),
                                maxLines = 1
                            )
                            Spacer(Modifier.height(3.dp))
                            Box(
                                Modifier.fillMaxWidth(.72f)
                                    .height((82f * day.seconds / peak).coerceAtLeast(if (day.seconds > 0) 4f else 0f).dp)
                                    .clip(RoundedCornerShape(topStart = 7.dp, topEnd = 7.dp))
                                    .background(Brush.verticalGradient(listOf(Color(0xFF06C8C1), Teal)))
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(day.label, color = Navy, fontSize = 11.sp, maxLines = 1)
                        }
                    }
                }
                Text(
                    "Minute po danima (zaokruženo prema dolje; <1 znači manje od minute).",
                    color = Navy.copy(alpha = .65f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun AchievementsCard(
    badges: List<ProgressBadgeInfo>,
    onShowAll: () -> Unit,
    compact: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White.copy(alpha = .96f),
        shape = RoundedCornerShape(27.dp),
        shadowElevation = 3.dp
    ) {
        Column(
            Modifier.fillMaxWidth().padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProgressSmallIcon("★", Color(0xFFFFF1CC), Color(0xFFF2A700))
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Moje značke",
                        color = Navy,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Postignuća koja si do sada osvojio/la.",
                        color = Navy.copy(alpha = .65f),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
                TextButton(onClick = onShowAll) {
                    Text("Prikaži sve ❯", color = Teal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                badges.forEach { badge ->
                    Column(
                        Modifier.weight(1f)
                            .clickable(onClick = onShowAll)
                            .semantics {
                                contentDescription = badge.title + ": " +
                                    (if (badge.earned) "osvojeno" else "zaključano")
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(Modifier.fillMaxWidth().aspectRatio(1f), contentAlignment = Alignment.Center) {
                            Canvas(Modifier.fillMaxSize()) {
                                val color = if (badge.earned) badge.color else Color(0xFFB3BCC7)
                                drawCircle(
                                    brush = Brush.verticalGradient(
                                        listOf(color.copy(alpha = .58f), color)
                                    ),
                                    radius = size.minDimension * .42f
                                )
                                drawCircle(
                                    color = Color.White.copy(alpha = .78f),
                                    radius = size.minDimension * .35f,
                                    style = Stroke(2.dp.toPx())
                                )
                            }
                            Text(
                                if (badge.earned) badge.icon else "🔒",
                                fontSize = if (compact) 17.sp else 22.sp,
                                color = if (badge.earned) Color.White else Navy
                            )
                        }
                        Text(
                            badge.title,
                            color = if (badge.earned) Navy else Navy.copy(alpha = .6f),
                            fontSize = if (compact) 9.sp else 10.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 12.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PracticeContinue(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 61.dp),
        contentPadding = PaddingValues(horizontal = 9.dp),
        shape = RoundedCornerShape(35.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Yellow, contentColor = Navy)
    ) {
        Text(
            "▶  Nastavi vježbati",
            fontWeight = FontWeight.Black,
            fontSize = 21.sp,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProgressNavigation(
    onHome: () -> Unit,
    onLetters: () -> Unit,
    onSettings: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        shadowElevation = 6.dp,
        color = Color.White
    ) {
        Row(
            Modifier.fillMaxWidth().height(70.dp)
                .padding(horizontal = 6.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            listOf(
                Triple("⌂", "Početna", onHome),
                Triple("▣", "Slova", onLetters),
                Triple("▥", "Napredak", {}),
                Triple("⚙", "Postavke", onSettings)
            ).forEach { (glyph, label, action) ->
                val selected = label == "Napredak"
                Column(
                    Modifier.weight(1f).fillMaxHeight()
                        .clip(RoundedCornerShape(22.dp))
                        .background(if (selected) Color(0xFFB5EEEC) else Color.Transparent)
                        .clickable(enabled = !selected, onClick = action)
                        .semantics { contentDescription = label },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(glyph, fontSize = 24.sp, color = Navy)
                    Text(
                        label,
                        color = Navy,
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressSky() {
    Canvas(
        Modifier.fillMaxSize().background(
            Brush.verticalGradient(
                listOf(Color(0xFF9BDBF9), Color(0xFFDFF4FF), Ivory, Color(0xFFE1F4BC))
            )
        )
    ) {
        val w = size.width
        val h = size.height
        drawOval(Color.White.copy(alpha = .74f), Offset(w * .11f, h * .11f), Size(w * .29f, h * .041f))
        drawOval(Color.White.copy(alpha = .74f), Offset(w * .79f, h * .07f), Size(w * .19f, h * .037f))
        val hills = Path().apply {
            moveTo(0f, h * .92f)
            cubicTo(w * .24f, h * .85f, w * .43f, h * .97f, w * .64f, h * .89f)
            cubicTo(w * .83f, h * .85f, w * .93f, h * .93f, w, h * .87f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(hills, Color(0xFF97D96B))
    }
}
