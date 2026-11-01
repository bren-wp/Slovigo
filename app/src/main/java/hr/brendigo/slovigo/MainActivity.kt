package hr.brendigo.slovigo

import android.os.Bundle
import android.os.SystemClock
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
import hr.brendigo.slovigo.model.AchievementFlow
import hr.brendigo.slovigo.model.PracticePreferences
import hr.brendigo.slovigo.ui.*
import java.util.Locale

class MainActivity : ComponentActivity() {
    private var speaker: TextToSpeech? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preview = if ((applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            intent.getStringExtra("preview_screen")?.takeIf { it in setOf("home", "welcome", "letters", "lowercase", "trace", "success", "progress", "games", "settings") } ?: "welcome"
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
    var hints by remember { mutableStateOf(storage.hintsEnabled()) }
    var numbers by remember { mutableStateOf(storage.strokeNumbersEnabled()) }
    var boardSize by remember { mutableIntStateOf(storage.boardSize()) }
    var tolerance by remember { mutableIntStateOf(storage.touchTolerance()) }
    var settingsOrigin by rememberSaveable { mutableStateOf("home") }
    val openSettings: () -> Unit = {
        settingsOrigin = page
        page = "settings"
    }
    var showReset by remember { mutableStateOf(false) }
    var practiceStartedAt by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    val upperCount = remember(revision) { storage.total(true) }
    val lowerCount = remember(revision) { storage.total(false) }
    val choose: (String) -> Unit = {
        letter = it
        practiceStartedAt = SystemClock.elapsedRealtime()
        page = "trace"
    }
    val returnHome: () -> Unit = { page = "home" }
    val back: () -> Unit = {
        page = when(page) {
            "trace" -> "letters"
            "success" -> "letters"
            "settings" -> if (settingsOrigin == "settings") "home" else settingsOrigin
            "about" -> "settings"
            "letters", "progress", "games" -> "home"
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
                if (page !in setOf("welcome", "home", "letters", "trace", "success", "progress", "games", "settings")) {
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
                                onGames = { page = "games" },
                                onSettings = openSettings
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
                                    onSettings = openSettings
                                )
                            } else {
                                SlovigoLowercaseScreen(
                                    completed = storage.completedLetters(false),
                                    onLetter = choose,
                                    onBack = back,
                                    onListen = { if (sound) speak("Odaberi malo tiskano slovo za vježbanje") },
                                    onHome = returnHome,
                                    onGames = { page = "games" },
                                    onSettings = openSettings
                                )
                            }
                            "trace" -> SlovigoWritingScreen(
                                letter = letter,
                                uppercase = upper,
                                initialGuide = guide,
                                initialHelp = hints,
                                showStrokeNumbers = numbers,
                                boardSize = boardSize,
                                strokeTolerance = PracticePreferences.strokeTolerance(tolerance),
                                onBack = { page = "letters" },
                                onHome = returnHome,
                                onUppercase = { upper = true; page = "letters" },
                                onLowercase = { upper = false; page = "letters" },
                                onSettings = openSettings,
                                onListen = { if (sound) speak(Alphabet.label(letter, upper)) },
                                onFinished = {
                                    storage.markCompleted(letter, upper)
                                    storage.recordCompletedExercise(SystemClock.elapsedRealtime() - practiceStartedAt)
                                    revision++
                                    page = "success"
                                }
                            )
                            "success" -> SlovigoSuccessScreen(
                                letter = letter,
                                uppercase = upper,
                                completedCount = if (upper) upperCount else lowerCount,
                                onBack = back,
                                onSettings = openSettings,
                                onRepeat = {
                                    practiceStartedAt = SystemClock.elapsedRealtime()
                                    page = "trace"
                                },
                                onNext = {
                                    val next = AchievementFlow.nextLetter(letter)
                                    if (next == null) {
                                        page = "letters"
                                    } else {
                                        letter = next
                                        practiceStartedAt = SystemClock.elapsedRealtime()
                                        page = "trace"
                                    }
                                }
                            )
                            "progress" -> SlovigoProgressScreen(
                                upperCompleted = remember(revision) { storage.completedLetters(true) },
                                lowerCompleted = remember(revision) { storage.completedLetters(false) },
                                weeklyActivity = remember(revision) { storage.weeklyActivity() },
                                streakDays = remember(revision) { storage.practiceStreak() },
                                onPractice = {
                                    upper = true
                                    page = "letters"
                                },
                                onHome = returnHome,
                                onLetters = { page = "letters" },
                                onSettings = openSettings
                            )
                            "games" -> SlovigoGamesScreen(
                                points = remember(revision) { storage.gamePoints() },
                                onAward = { mode, gameLetter ->
                                    val earned = storage.awardGameOnce(mode, gameLetter)
                                    if (earned) revision++
                                    earned
                                },
                                onPractice = { gameLetter ->
                                    upper = true
                                    choose(gameLetter)
                                },
                                onSpeak = { spoken -> if (sound) speak(spoken) },
                                onHome = returnHome,
                                onProgress = { page = "progress" },
                                onSettings = openSettings
                            )
                            "settings" -> SlovigoSettingsScreen(
                                sound = sound,
                                guides = guide,
                                hints = hints,
                                numbers = numbers,
                                contrast = contrast,
                                boardSize = boardSize,
                                tolerance = tolerance,
                                onSound = { sound = it; storage.setSoundEnabled(it) },
                                onGuides = { guide = it; storage.setGuideEnabled(it) },
                                onHints = { hints = it; storage.setHintsEnabled(it) },
                                onNumbers = { numbers = it; storage.setStrokeNumbersEnabled(it) },
                                onContrast = { contrast = it; storage.setContrastEnabled(it) },
                                onBoardSize = { boardSize = it; storage.setBoardSize(it) },
                                onTolerance = { tolerance = it; storage.setTouchTolerance(it) },
                                onTestSound = { if (sound) speak("SLOVIGO. Učimo pisati kroz igru.") },
                                onBack = back,
                                onAbout = { page = "about" },
                                onReset = { showReset = true },
                                onHome = returnHome,
                                onGames = { page = "games" }
                            )
                            "about" -> AboutScreen()
                        }
                    }
                }
                if (page !in setOf("welcome", "trace", "success", "home", "letters", "progress", "games", "settings")) {
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
                hints = false
                numbers = true
                boardSize = PracticePreferences.BOARD_DEFAULT
                tolerance = PracticePreferences.TOLERANCE_DEFAULT
                showReset = false
            }) { Text("Izbriši", color = Coral) }
        },
        dismissButton = { TextButton(onClick = { showReset = false }) { Text("Odustani") } }
    )
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
