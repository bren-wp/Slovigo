package hr.brendigo.slovigo

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import hr.brendigo.slovigo.data.LocalProgress
import hr.brendigo.slovigo.model.Alphabet
import hr.brendigo.slovigo.ui.*
import java.util.Locale

class MainActivity : ComponentActivity() {
    private var speaker: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preview = if ((applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            intent.getStringExtra("preview_screen")?.takeIf { it in setOf("home", "welcome", "letters", "lowercase") } ?: "welcome"
        } else "welcome"
        setContent { SlovigoTheme { SlovigoApp(initialPage = preview) { text ->
            speakSafely(text)
        } } }
    }
    private var speechReady = false
    private var pendingSpeech: String? = null

    /** Never block initial UI launch on the optional OS speech engine. */
    private fun speakSafely(text: String) {
        if (text.isBlank()) return
        if (speaker == null) {
            pendingSpeech = text
            try {
                speaker = TextToSpeech(applicationContext) { status ->
                    runOnUiThread {
                        if (status == TextToSpeech.SUCCESS) {
                            val language = runCatching {
                                speaker?.setLanguage(Locale.forLanguageTag("hr-HR"))
                            }.getOrNull()
                            speechReady = language != null &&
                                language != TextToSpeech.LANG_MISSING_DATA &&
                                language != TextToSpeech.LANG_NOT_SUPPORTED
                            if (speechReady) pendingSpeech?.let(::sayNow)
                        }
                        pendingSpeech = null
                    }
                }
            } catch (_: RuntimeException) {
                speaker = null
                pendingSpeech = null
            }
        } else if (speechReady) sayNow(text) else pendingSpeech = text
    }

    private fun sayNow(text: String) {
        runCatching { speaker?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "slovigo-letter") }
    }

    override fun onStop() {
        runCatching { speaker?.stop() }
        super.onStop()
    }

    override fun onDestroy() {
        runCatching { speaker?.shutdown() }
        speaker = null
        super.onDestroy()
    }
}

@Composable
private fun SlovigoApp(initialPage: String, speak: (String) -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val storage = remember(context) { LocalProgress(context) }
    var page by rememberSaveable { mutableStateOf(if (initialPage == "lowercase") "letters" else initialPage) }
    var upper by rememberSaveable { mutableStateOf(initialPage != "lowercase") }
    var letter by rememberSaveable { mutableStateOf("A") }
    var revision by remember { mutableIntStateOf(0) }
    var sound by remember { mutableStateOf(storage.soundEnabled()) }
    var guide by remember { mutableStateOf(storage.guideEnabled()) }
    var contrast by remember { mutableStateOf(storage.contrastEnabled()) }
    var showReset by remember { mutableStateOf(false) }
    val upperCount = remember(revision) { storage.total(true) }
    val lowerCount = remember(revision) { storage.total(false) }
    val choose: (String) -> Unit = { letter = it; page = "trace" }
    val returnHome: () -> Unit = { page = "home" }
    val back: () -> Unit = {
        page = when(page) {
            "trace" -> "letters"
            "success" -> "letters"
            "letters", "progress", "settings", "games", "about" -> "home"
            else -> "home"
        }
    }
    BackHandler(enabled = page != "home" && page != "welcome") { back() }

    SkyBackground(highContrast = contrast) {
        BoxWithConstraints(
            Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            val tablet = maxWidth >= 700.dp && maxHeight >= 480.dp
            val contentWidth = if (tablet) 1040.dp else 620.dp
            Column(Modifier.fillMaxSize()) {
                if (page != "welcome" && page != "home" && page != "letters" && page != "trace") {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (page != "home") TextButton(onClick = back) { Text("❮  Natrag", color = Navy) }
                        else Spacer(Modifier.width(72.dp))
                        AppLogo(fontSize = if (tablet) 29 else 24)
                        Spacer(Modifier.width(72.dp))
                    }
                }
                Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
                    Box(Modifier.fillMaxHeight().widthIn(max = contentWidth).fillMaxWidth()) {
                        when (page) {
                            "welcome" -> SplashScreen(onStart = returnHome)
                            "home" -> SlovigoHomeScreen(
                                learnedCount = upperCount + lowerCount,
                                onUpper = { upper = true; page = "letters" },
                                onLower = { upper = false; page = "letters" },
                                onProgress = { page = "progress" },
                                onSettings = { page = "settings" }
                            )
                            "letters" -> if (upper) {
                                SlovigoAlphabetScreen(
                                    uppercase = true,
                                    completed = storage.completedLetters(true),
                                    onLetter = choose,
                                    onBack = back,
                                    onSound = { if (sound) speak("Odaberi veliko slovo za vježbanje") },
                                    onHome = returnHome,
                                    onGames = { page = "games" },
                                    onSettings = { page = "settings" }
                                )
                            } else {
                                SlovigoLowercaseScreen(
                                    completed = storage.completedLetters(false),
                                    onLetter = choose,
                                    onBack = back,
                                    onListen = { if (sound) speak("Odaberi malo tiskano slovo za vježbanje") },
                                    onHome = returnHome,
                                    onGames = { page = "games" },
                                    onSettings = { page = "settings" }
                                )
                            }
                            "trace" -> SlovigoWritingScreen(
                                letter = letter,
                                uppercase = upper,
                                initialGuide = guide,
                                onBack = { page = "letters" },
                                onHome = returnHome,
                                onUppercase = { upper = true; page = "letters" },
                                onLowercase = { upper = false; page = "letters" },
                                onSettings = { page = "settings" },
                                onListen = { if (sound) speak(Alphabet.label(letter, upper)) },
                                onFinished = {
                                    storage.markCompleted(letter, upper)
                                    revision++
                                    page = "success"
                                }
                            )
                            "success" -> SuccessScreen(
                                letter, upper,
                                onRepeat = { page = "trace" },
                                onNext = {
                                    val index = Alphabet.letters.indexOf(letter)
                                    if (index < Alphabet.letters.lastIndex) {
                                        letter = Alphabet.letters[index + 1]
                                        page = "trace"
                                    } else page = "letters"
                                }
                            )
                            "progress" -> ProgressScreen(upperCount, lowerCount) { page = "letters" }
                            "games" -> GameScreen { choose(it) }
                            "settings" -> SettingsScreen(
                                sound, guide, contrast,
                                onSound = { sound = it; storage.setSoundEnabled(it) },
                                onGuide = { guide = it; storage.setGuideEnabled(it) },
                                onContrast = { contrast = it; storage.setContrastEnabled(it) },
                                onReset = { showReset = true },
                                onAbout = { page = "about" }
                            )
                            "about" -> AboutScreen()
                        }
                    }
                }
                if (page !in setOf("welcome", "trace", "success", "home", "letters")) {
                    BottomMenu(page) { page = it }
                }
            }
        }
    }
    if (showReset) AlertDialog(
        onDismissRequest = { showReset = false },
        title = { Text("Izbrisati napredak?") },
        text = { Text("Sva naučena slova i postavke vježbanja bit će izbrisani s ovog uređaja.") },
        confirmButton = {
            TextButton(onClick = {
                storage.reset()
                revision++
                sound = true
                guide = true
                contrast = false
                showReset = false
            }) { Text("Izbriši", color = Coral) }
        },
        dismissButton = { TextButton(onClick = { showReset = false }) { Text("Odustani") } }
    )
}

@Composable
private fun SuccessScreen(letter: String, uppercase: Boolean, onRepeat: () -> Unit, onNext: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text("🌟", fontSize = 96.sp)
        ScreenTitle("Bravo!", "Uspješno si napisao slovo!")
        WhiteCard(Modifier.fillMaxWidth().widthIn(max = 420.dp)) {
            Text(
                Alphabet.label(letter,uppercase),
                modifier=Modifier.fillMaxWidth(),
                fontSize=120.sp,
                color=Teal,
                fontWeight=FontWeight.Black,
                textAlign=TextAlign.Center
            )
            Text("⭐⭐⭐", modifier=Modifier.fillMaxWidth(),fontSize=35.sp,textAlign=TextAlign.Center)
        }
        BigAction("❯  Sljedeće slovo", onNext, Modifier.fillMaxWidth())
        SmallAction("↻  Pokušaj ponovno", onRepeat, Modifier.fillMaxWidth())
    }
}

@Composable
private fun ProgressScreen(upperCount: Int, lowerCount: Int, onPractice: () -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ScreenTitle("Moj napredak", "Prati koliko si slova naučio/la")
        WhiteCard(Modifier.fillMaxWidth()) {
            Text("🏆  Uspješno naučeno", style = MaterialTheme.typography.titleLarge)
            Text("${upperCount + lowerCount} / 60", fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, color = Teal)
            ProgressBadge(upperCount + lowerCount, 60)
        }
        WhiteCard(Modifier.fillMaxWidth()) {
            Text("Velika tiskana slova", style = MaterialTheme.typography.titleLarge)
            ProgressBadge(upperCount, 30)
        }
        WhiteCard(Modifier.fillMaxWidth()) {
            Text("Mala tiskana slova", style = MaterialTheme.typography.titleLarge)
            ProgressBadge(lowerCount, 30)
        }
        Text("⭐  Svako slovo možeš vježbati više puta!", color = Navy)
        BigAction("▶  Nastavi vježbati", onPractice, Modifier.fillMaxWidth(), background = Yellow)
    }
}

@Composable
private fun GameScreen(onChoose: (String) -> Unit) {
    var current by rememberSaveable { mutableIntStateOf(0) }
    var celebrate by remember { mutableStateOf(false) }
    val target = Alphabet.letters[current % Alphabet.letters.size]
    val other = Alphabet.letters[(current + 5) % Alphabet.letters.size]
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ScreenTitle("Igraj se slovima", "Pronađi slovo koje piše na kartici")
        WhiteCard(Modifier.fillMaxWidth()) {
            Text("Pronađi slovo:", style = MaterialTheme.typography.titleLarge)
            Text(target, Modifier.fillMaxWidth(), color = Teal, fontWeight = FontWeight.Black, fontSize = 85.sp, textAlign = TextAlign.Center)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BigAction(target, { celebrate = true }, Modifier.weight(1f), background = Yellow)
                BigAction(other, { celebrate = false }, Modifier.weight(1f), background = Blue)
            }
            if (celebrate) {
                Text("Bravo! ⭐ Točno slovo.", color = Teal, fontWeight = FontWeight.Bold)
                BigAction("Sljedeći izazov", { current++; celebrate = false }, Modifier.fillMaxWidth())
                SmallAction("Vježbaj pisanje", { onChoose(target) }, Modifier.fillMaxWidth())
            } else {
                Text("Dodirni odgovarajuće slovo.", color = Navy.copy(alpha = .7f))
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    sound: Boolean, guide: Boolean, contrast: Boolean,
    onSound: (Boolean) -> Unit, onGuide: (Boolean) -> Unit, onContrast: (Boolean) -> Unit,
    onReset: () -> Unit, onAbout: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        ScreenTitle("Postavke", "Prilagodi način učenja")
        WhiteCard(Modifier.fillMaxWidth()) {
            Text("Učenje", style = MaterialTheme.typography.titleLarge, color = Teal)
            SettingToggle("Prikaz vodilica", "Oznake smjera pisanja", guide, onGuide)
            HorizontalDivider()
            SettingToggle("Zvuk slova", "Izgovor putem Android TTS usluge", sound, onSound)
        }
        WhiteCard(Modifier.fillMaxWidth()) {
            Text("Pristupačnost", style = MaterialTheme.typography.titleLarge, color = Teal)
            SettingToggle("Visoki kontrast", "Dodatna prilagodba kontrasta", contrast, onContrast)
            Text("Aplikacija koristi velike tipke i podržava promjenu veličine teksta u sustavu.", color=Navy.copy(alpha=.68f))
        }
        WhiteCard(Modifier.fillMaxWidth()) {
            Text("Roditeljske mogućnosti", style=MaterialTheme.typography.titleLarge, color=Teal)
            SmallAction("O aplikaciji i privatnosti", onAbout, Modifier.fillMaxWidth())
            SmallAction("Izbriši lokalni napredak", onReset, Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun SettingToggle(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, color = Navy)
            Text(subtitle, fontSize = 13.sp, color = Navy.copy(alpha = .7f))
        }
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun AboutScreen() {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
        verticalArrangement=Arrangement.spacedBy(16.dp)
    ) {
        ScreenTitle("O aplikaciji", "SLOVIGO — Učimo pisati kroz igru")
        WhiteCard(Modifier.fillMaxWidth()) {
            Text("SLOVIGO pomaže uvježbavati 30 slova hrvatske abecede.", color=Navy)
            Text("Bez oglasa, računa i internetskih dopuštenja. Podaci o naučenim slovima ostaju na uređaju.", color=Navy)
            Text("Zvuk koristi odabranu sustavnu TextToSpeech uslugu. Njezina pravila obrade glasa ovise o pružatelju usluge.", color=Navy)
            Text("Verzija 0.1 — edukativna aplikacija u razvoju.", color=Navy.copy(alpha=.7f))
        }
    }
}

@Composable
private fun BottomMenu(page: String, onClick: (String) -> Unit) {
    val destinations = listOf("home" to "⌂  Početna", "letters" to "▦  Slova", "progress" to "★  Napredak", "settings" to "⚙  Postavke")
    Surface(color=Color.White.copy(alpha=.96f), tonalElevation=5.dp) {
        Row(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal=4.dp,vertical=4.dp)) {
            destinations.forEach { (target, label) ->
                TextButton(
                    onClick = { onClick(target) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColors(contentColor = if (page == target) Teal else Navy)
                ) {
                    Text(
                        label,
                        fontWeight = if (page == target) FontWeight.Black else FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        fontSize = 11.sp,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
