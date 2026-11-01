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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Screen 09 — Settings.
 *
 * Uses live persisted preferences: every switch and slider has a functional
 * effect on writing or playback. Scrollable content works on small phones;
 * cards are side by side on tablets / landscape. No external assets.
 */
@Composable
fun SlovigoSettingsScreen(
    sound: Boolean,
    guides: Boolean,
    hints: Boolean,
    numbers: Boolean,
    contrast: Boolean,
    boardSize: Int,
    tolerance: Int,
    onSound: (Boolean) -> Unit,
    onGuides: (Boolean) -> Unit,
    onHints: (Boolean) -> Unit,
    onNumbers: (Boolean) -> Unit,
    onContrast: (Boolean) -> Unit,
    onBoardSize: (Int) -> Unit,
    onTolerance: (Int) -> Unit,
    onTestSound: () -> Unit,
    onBack: () -> Unit,
    onAbout: () -> Unit,
    onReset: () -> Unit,
    onHome: () -> Unit,
    onGames: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 720.dp
        val compact = maxWidth < 360.dp
        SettingsBackdrop(highContrast = contrast)
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier.weight(1f).fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = if (wide) 22.dp else if (compact) 10.dp else 16.dp)
                    .padding(top = 7.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                SettingsHeader(compact, onBack)
                if (wide) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            SettingsLearning(
                                guides, sound, hints, numbers,
                                onGuides, onSound, onHints, onNumbers,
                                onTestSound
                            )
                        }
                        Column(
                            Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            SettingsAdaptation(
                                contrast, boardSize, tolerance,
                                onContrast, onBoardSize, onTolerance
                            )
                            SettingsParents(onAbout, onReset)
                        }
                    }
                } else {
                    SettingsLearning(
                        guides, sound, hints, numbers,
                        onGuides, onSound, onHints, onNumbers,
                        onTestSound
                    )
                    SettingsAdaptation(
                        contrast, boardSize, tolerance,
                        onContrast, onBoardSize, onTolerance
                    )
                    SettingsParents(onAbout, onReset)
                }
            }
            SettingsBottomNavigation(onHome, onGames)
        }
    }
}

@Composable
private fun SettingsHeader(compact: Boolean, onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Row(
            Modifier.fillMaxWidth().height(if (compact) 63.dp else 73.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBack,
                modifier = Modifier.size(50.dp)
                    .semantics { contentDescription = "Natrag na prethodni zaslon" },
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 3.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("❮", color = Navy, fontSize = 29.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SplashWordmark(if (compact) 28 else 35)
                Text(
                    "Učimo pisati kroz igru",
                    fontSize = if (compact) 11.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
            }
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.width(50.dp))
        }
        Row(
            Modifier.fillMaxWidth().height(if (compact) 93.dp else 121.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    "Postavke",
                    fontSize = if (compact) 34.sp else 42.sp,
                    color = Navy,
                    fontWeight = FontWeight.Black,
                    maxLines = 1
                )
                Text(
                    "Prilagodi pisanje svojem ritmu!",
                    color = Navy.copy(alpha = .77f),
                    fontSize = if (compact) 13.sp else 16.sp,
                    lineHeight = 20.sp,
                    maxLines = 2
                )
            }
            SplashPencil(
                Modifier.width(if (compact) 95.dp else 130.dp).fillMaxHeight()
            )
        }
    }
}

@Composable
private fun SettingsLearning(
    guides: Boolean,
    sound: Boolean,
    hints: Boolean,
    numbers: Boolean,
    onGuides: (Boolean) -> Unit,
    onSound: (Boolean) -> Unit,
    onHints: (Boolean) -> Unit,
    onNumbers: (Boolean) -> Unit,
    onTestSound: () -> Unit
) {
    SettingsSection("✎", "Učenje pisanja", Color(0xFFD3F4ED)) {
        SettingsSwitchRow(
            icon = "➜",
            title = "Strelice i vodilice",
            subtitle = "Prikaz smjera i putanje pisanja",
            checked = guides,
            onChange = onGuides
        )
        SettingsLine()
        SettingsSwitchRow(
            icon = "♫",
            title = "Izgovor slova",
            subtitle = "Zvuk putem sustavne TTS usluge",
            checked = sound,
            onChange = onSound
        )
        if (sound) {
            TextButton(
                onClick = onTestSound,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("♫  Isprobaj izgovor", color = Teal, fontWeight = FontWeight.Bold)
            }
        }
        SettingsLine()
        SettingsSwitchRow(
            icon = "?",
            title = "Dodatne upute",
            subtitle = "Prikaz pomoći na ploči za pisanje",
            checked = hints,
            onChange = onHints
        )
        SettingsLine()
        SettingsSwitchRow(
            icon = "1",
            title = "Brojevi poteza",
            subtitle = "Prikaz brojeva uz početne točke",
            checked = numbers,
            onChange = onNumbers
        )
    }
}

@Composable
private fun SettingsAdaptation(
    contrast: Boolean,
    boardSize: Int,
    tolerance: Int,
    onContrast: (Boolean) -> Unit,
    onBoardSize: (Int) -> Unit,
    onTolerance: (Int) -> Unit
) {
    SettingsSection("◉", "Prilagodba vježbanja", Color(0xFFFFE6C6)) {
        SettingsSliderRow(
            title = "Veličina ploče",
            description = "Odaberi veličinu područja za pisanje",
            value = boardSize,
            labels = listOf("Manja", "Srednja", "Veća"),
            onChange = onBoardSize
        )
        SettingsLine()
        SettingsSliderRow(
            title = "Osjetljivost dodira",
            description = "Koliko precizno treba pratiti liniju",
            value = tolerance,
            labels = listOf("Precizno", "Srednje", "Lakše"),
            onChange = onTolerance
        )
        SettingsLine()
        SettingsSwitchRow(
            icon = "◐",
            title = "Visoki kontrast",
            subtitle = "Svjetlija pozadina i jasnije razlike",
            checked = contrast,
            onChange = onContrast
        )
    }
}

@Composable
private fun SettingsParents(onAbout: () -> Unit, onReset: () -> Unit) {
    SettingsSection("♜", "Za roditelje", Color(0xFFD9E2FF)) {
        SettingsClickRow(
            icon = "ⓘ",
            title = "O aplikaciji i privatnosti",
            subtitle = "Kako se čuvaju podaci i koristi zvuk",
            onClick = onAbout
        )
        SettingsLine()
        SettingsClickRow(
            icon = "↺",
            title = "Izbriši napredak",
            subtitle = "Trajno uklanja slova, bodove i postavke",
            onClick = onReset,
            warning = true
        )
        Text(
            "Svi podaci ostaju na ovom uređaju. Brisanje traži potvrdu.",
            fontSize = 12.sp,
            lineHeight = 16.sp,
            color = Navy.copy(alpha = .71f)
        )
    }
}

@Composable
private fun SettingsSection(
    icon: String,
    title: String,
    iconColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(27.dp),
        color = Color.White.copy(alpha = .97f),
        shadowElevation = 5.dp
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 15.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, fontSize = 26.sp, color = Teal, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    title,
                    fontSize = 21.sp,
                    lineHeight = 25.sp,
                    color = Navy,
                    fontWeight = FontWeight.Black
                )
            }
            content()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit
) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 58.dp)
            .clickable(onClick = { onChange(!checked) }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier.size(36.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color(0xFFF1F7FB)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = Teal, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
        Column(Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = Navy,
                maxLines = 2
            )
            Text(
                subtitle,
                color = Navy.copy(alpha = .68f),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                maxLines = 3
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            modifier = Modifier.semantics { contentDescription = title }
        )
    }
}

@Composable
private fun SettingsSliderRow(
    title: String,
    description: String,
    value: Int,
    labels: List<String>,
    onChange: (Int) -> Unit
) {
    Column(
        Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = Navy, fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text(description, color = Navy.copy(alpha = .7f), fontSize = 12.sp)
            }
            Text(
                labels.getOrElse(value.coerceIn(0, 2)) { "Srednje" },
                color = Teal,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )
        }
        Slider(
            value = value.toFloat(),
            onValueChange = { onChange(it.toInt().coerceIn(0, 2)) },
            valueRange = 0f..2f,
            steps = 1,
            modifier = Modifier.fillMaxWidth()
                .semantics { contentDescription = title }
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            labels.forEach { label ->
                Text(
                    label,
                    color = Navy.copy(alpha = .61f),
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun SettingsClickRow(
    icon: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    warning: Boolean = false
) {
    Row(
        Modifier.fillMaxWidth().heightIn(min = 60.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .semantics { contentDescription = title },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            Modifier.size(37.dp).clip(RoundedCornerShape(12.dp))
                .background(if (warning) Color(0xFFFFE4E7) else Color(0xFFE2F7F7)),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 24.sp, color = if (warning) Coral else Teal)
        }
        Column(Modifier.weight(1f)) {
            Text(
                title,
                color = if (warning) Color(0xFFAD4152) else Navy,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
            Text(
                subtitle,
                color = Navy.copy(alpha = .7f),
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
        Text("❯", fontSize = 22.sp, color = Teal)
    }
}

@Composable
private fun SettingsLine() {
    Box(
        Modifier.fillMaxWidth().height(1.dp)
            .background(Color(0xFFECF0F3))
    )
}

@Composable
private fun SettingsBottomNavigation(onHome: () -> Unit, onGames: () -> Unit) {
    Surface(
        Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(topStart = 27.dp, topEnd = 27.dp),
        shadowElevation = 6.dp
    ) {
        Row(
            Modifier.fillMaxWidth().height(68.dp).padding(horizontal = 6.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            listOf(
                Triple("⌂", "Početna", onHome),
                Triple("★", "Igre", onGames),
                Triple("⚙", "Postavke", {})
            ).forEach { (glyph, title, action) ->
                val active = title == "Postavke"
                Column(
                    Modifier.weight(1f).fillMaxHeight()
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (active) Teal else Color.Transparent)
                        .clickable(enabled = !active, onClick = action),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(glyph, fontSize = 24.sp, color = if (active) Color.White else Navy)
                    Text(
                        title,
                        color = if (active) Color.White else Navy,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsBackdrop(highContrast: Boolean) {
    Canvas(
        Modifier.fillMaxSize()
            .background(
                Brush.verticalGradient(
                    if (highContrast) listOf(Color.White, Color.White, Ivory)
                    else listOf(
                        Color(0xFF93DCF8),
                        Color(0xFFD5F4FD),
                        Color(0xFFFFFCED),
                        Color(0xFFC7EC9A)
                    )
                )
            )
    ) {
        if (highContrast) return@Canvas
        val w = size.width
        val h = size.height
        val white = Color.White.copy(alpha = .67f)
        drawOval(white, Offset(w * .08f, h * .1f), Size(w * .24f, h * .05f))
        drawOval(white, Offset(w * .71f, h * .16f), Size(w * .23f, h * .045f))
        val hill = Path().apply {
            moveTo(0f, h * .93f)
            cubicTo(w * .22f, h * .88f, w * .44f, h * .96f, w * .67f, h * .90f)
            cubicTo(w * .84f, h * .87f, w * .94f, h * .94f, w, h * .90f)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(hill, Green)
    }
}
