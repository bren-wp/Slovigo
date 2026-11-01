package hr.brendigo.slovigo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Screen 02 — original native interpretation of the supplied Slovigo home reference.
 * Important: layout is responsive; an exact fixed-resolution bitmap would not work on
 * smaller phones, split-screen windows, tablets, or font scaling.
 */
@Composable
fun SlovigoHomeScreen(
    learnedCount: Int,
    onUpper: () -> Unit,
    onLower: () -> Unit,
    onProgress: () -> Unit,
    onGames: () -> Unit,
    onSettings: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wide = maxWidth >= 700.dp && maxWidth > maxHeight * .95f
        val tight = maxWidth < 360.dp || maxHeight < 650.dp
        Box(Modifier.fillMaxSize()) {
            HomeScenery(Modifier.fillMaxSize())
            Column(Modifier.fillMaxSize()) {
                if (wide) {
                    Row(
                        Modifier.weight(1f).fillMaxWidth().padding(horizontal = 26.dp),
                        horizontalArrangement = Arrangement.spacedBy(25.dp)
                    ) {
                        Column(
                            Modifier.weight(.92f).fillMaxHeight().verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            HomeBrandHeader(Modifier.fillMaxWidth(), true)
                            HomeTitle(tight)
                            ProgressHomeTile(
                                learnedCount, onProgress,
                                Modifier.fillMaxWidth()
                            )
                        }
                        Column(
                            Modifier.weight(1.08f).fillMaxHeight().verticalScroll(rememberScrollState())
                                .padding(top = 18.dp, bottom = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            LetterChoiceCard(true, onUpper, Modifier.fillMaxWidth(), false)
                            LetterChoiceCard(false, onLower, Modifier.fillMaxWidth(), false)
                        }
                    }
                } else {
                    Column(
                        Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState())
                            .padding(horizontal = if (tight) 12.dp else 18.dp)
                            .padding(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(if (tight) 12.dp else 18.dp)
                    ) {
                        HomeBrandHeader(Modifier.fillMaxWidth(), false)
                        HomeTitle(tight)
                        LetterChoiceCard(true, onUpper, Modifier.fillMaxWidth(), tight)
                        LetterChoiceCard(false, onLower, Modifier.fillMaxWidth(), tight)
                        ProgressHomeTile(learnedCount, onProgress, Modifier.fillMaxWidth())
                    }
                }
                HomeNavigation(
                    selected = "home",
                    onHome = {},
                    onProgress = onProgress,
                    onGames = onGames,
                    onSettings = onSettings
                )
            }
        }
    }
}

@Composable
private fun HomeBrandHeader(modifier: Modifier, wide: Boolean) {
    BoxWithConstraints(modifier.height(if (wide) 210.dp else 156.dp)) {
        val pencilWidth = if (wide) maxWidth * .39f else maxWidth * .38f
        val compactLogo = maxWidth < 360.dp
        Row(
            Modifier.align(Alignment.TopStart).padding(top = if (wide) 18.dp else 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            SplashBook(Modifier.size(if (wide) 84.dp else 62.dp))
            Column(Modifier.padding(top = if (wide) 16.dp else 9.dp)) {
                SplashWordmark(if (wide) 39 else if (compactLogo) 26 else 30)
                Text(
                    "Uči pisati slova",
                    color = Navy,
                    fontSize = if (wide) 18.sp else 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }
        SplashPencil(
            Modifier.align(Alignment.CenterEnd)
                .width(pencilWidth)
                .fillMaxHeight(.96f)
        )
    }
}

@Composable
private fun HomeTitle(tight: Boolean) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            "Što želiš vježbati?",
            fontWeight = FontWeight.Black,
            fontSize = if (tight) 28.sp else 35.sp,
            lineHeight = if (tight) 33.sp else 42.sp,
            color = Navy,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Canvas(Modifier.align(Alignment.CenterHorizontally).width(95.dp).height(6.dp)) {
            drawLine(Yellow, Offset(3f,size.height/2), Offset(size.width-3f,size.height/2),5.dp.toPx(),cap=StrokeCap.Round)
        }
    }
}

@Composable
private fun LetterChoiceCard(
    uppercase: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    tight: Boolean
) {
    val bg = if (uppercase) {
        listOf(Color(0xFFFFFCED), Color(0xFFFFE8A7), Color(0xFFFFF0C4))
    } else {
        listOf(Color(0xFFF3FCFF), Color(0xFFD5F3FF), Color(0xFFB8EAFE))
    }
    val accent = if (uppercase) Color(0xFFFFC119) else Color(0xFF00A4B4)
    val title = if (uppercase) "Velika\ntiskana slova" else "Mala\ntiskana slova"
    val height = if (tight) 158.dp else 190.dp

    Box(
        modifier.height(height)
            .shadow(5.dp, RoundedCornerShape(32.dp))
            .clip(RoundedCornerShape(32.dp))
            .background(Brush.horizontalGradient(bg))
            .border(2.dp, if (uppercase) Yellow.copy(alpha=.58f) else Color(0xFF7ACDEB),RoundedCornerShape(32.dp))
            .clickable(onClick=onClick)
            .semantics { contentDescription = "Otvori vježbu: ${if (uppercase) "velika" else "mala"} tiskana slova" }
            .padding(horizontal = if (tight) 8.dp else 13.dp,vertical=12.dp)
    ) {
        Row(Modifier.fillMaxSize(), verticalAlignment=Alignment.CenterVertically) {
            HomeSchoolSupplies(
                uppercase,
                Modifier.fillMaxHeight().weight(.36f)
            )
            Column(
                Modifier.weight(.57f).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    title,
                    fontSize = if (tight) 23.sp else 28.sp,
                    lineHeight = if (tight) 26.sp else 30.sp,
                    color = Navy,
                    fontWeight = FontWeight.Black,
                    maxLines = 2
                )
                Box(
                    Modifier.fillMaxWidth().height(if(tight) 58.dp else 72.dp)
                        .shadow(3.dp,RoundedCornerShape(18.dp))
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha=.93f))
                        .padding(horizontal=10.dp,vertical=5.dp),
                    contentAlignment=Alignment.Center
                ) { HomeTracePreview(uppercase, Modifier.fillMaxSize()) }
            }
            Box(
                Modifier.weight(.13f).fillMaxHeight(),
                contentAlignment=Alignment.Center
            ) {
                Box(
                    Modifier.size(if(tight) 39.dp else 45.dp)
                        .shadow(3.dp,RoundedCornerShape(50))
                        .clip(RoundedCornerShape(50))
                        .background(accent),
                    contentAlignment=Alignment.Center
                ) {
                    Text("❯", fontSize=29.sp,fontWeight=FontWeight.Black,color=Color.White)
                }
            }
        }
    }
}

@Composable
private fun HomeSchoolSupplies(uppercase:Boolean, modifier:Modifier) {
    Box(modifier) {
        Canvas(Modifier.fillMaxSize()) {
        val w=size.width
        val h=size.height
        val navy=Navy
        if (uppercase) {
            // School blackboard, books and wooden legs, in reference proportions.
            drawLine(Color(0xFFAE6C2E),Offset(w*.28f,h*.31f),Offset(w*.19f,h*.88f),w*.055f,cap=StrokeCap.Round)
            drawLine(Color(0xFFAE6C2E),Offset(w*.77f,h*.32f),Offset(w*.84f,h*.87f),w*.055f,cap=StrokeCap.Round)
            drawRoundRect(
                Color(0xFFD58A35),
                Offset(w*.09f,h*.08f),Size(w*.82f,h*.69f),CornerRadius(w*.065f)
            )
            drawRoundRect(
                Brush.linearGradient(listOf(Color(0xFF075C51),Color(0xFF168779))),
                Offset(w*.14f,h*.125f),Size(w*.72f,h*.57f),CornerRadius(w*.03f)
            )
            drawRoundRect(
                Color(0xFFFFD171),
                Offset(w*.12f,h*.10f),Size(w*.76f,h*.61f),CornerRadius(w*.045f),
                style=Stroke(width=w*.04f)
            )
            // Books with local, scalable colored covers.
            drawRoundRect(Color(0xFFEC456A),Offset(w*.02f,h*.83f),Size(w*.58f,h*.13f),CornerRadius(w*.055f))
            drawRoundRect(Color(0xFFFFF5DE),Offset(w*.06f,h*.855f),Size(w*.50f,h*.062f),CornerRadius(w*.02f))
            drawRoundRect(Color(0xFF258DD7),Offset(w*.09f,h*.73f),Size(w*.59f,h*.12f),CornerRadius(w*.045f))
            drawRoundRect(Color(0xFFEFF7FB),Offset(w*.14f,h*.758f),Size(w*.48f,h*.06f),CornerRadius(w*.02f))
        } else {
            // Spiral-bound alphabet notebook and pencil cup.
            drawRoundRect(Color(0xFF258DDD),Offset(w*.12f,h*.12f),Size(w*.73f,h*.67f),CornerRadius(w*.07f))
            drawRoundRect(Color(0xFFFFFAF2),Offset(w*.15f,h*.10f),Size(w*.73f,h*.64f),CornerRadius(w*.05f))
            repeat(4) { i ->
                val x=w*(.23f+i*.16f)
                drawOval(navy,Offset(x,h*.075f),Size(w*.06f,h*.10f),style=Stroke(width=w*.02f))
            }
            drawRoundRect(Color(0xFFEE5D83),Offset(w*.05f,h*.76f),Size(w*.3f,h*.21f),CornerRadius(w*.065f))
            drawLine(Yellow,Offset(w*.12f,h*.80f),Offset(w*.16f,h*.47f),w*.045f,cap=StrokeCap.Round)
            drawLine(Color(0xFF168CB4),Offset(w*.23f,h*.81f),Offset(w*.30f,h*.45f),w*.042f,cap=StrokeCap.Round)
        }
    }
        Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center) {
        Text(
            if (uppercase) "ABC" else "abc",
            modifier=Modifier.offset(
                x=if(uppercase) 0.dp else 2.dp,
                y=if(uppercase) (-12).dp else (-11).dp
            ),
            fontSize=if(uppercase) 26.sp else 32.sp,
            fontWeight=FontWeight.Medium,
            color=Navy,
            textAlign=TextAlign.Center
        )
    }
    }
}

@Composable
private fun HomeTracePreview(uppercase: Boolean, modifier: Modifier) {
    Canvas(modifier) {
        val w=size.width
        val h=size.height
        val ink=Navy.copy(alpha=.87f)
        val lineWidth=3.dp.toPx()
        // Three character silhouettes — instructional guide only, not used for recognition.
        if(uppercase) {
            val a=Path().apply {
                moveTo(w*.06f,h*.91f);lineTo(w*.18f,h*.11f);lineTo(w*.32f,h*.91f)
                moveTo(w*.10f,h*.62f);lineTo(w*.275f,h*.62f)
            }
            drawPath(a,ink,style=Stroke(lineWidth,cap=StrokeCap.Round,pathEffect=androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(9f,7f))))
            val b=Path().apply {
                moveTo(w*.40f,h*.12f);lineTo(w*.40f,h*.92f)
                cubicTo(w*.66f,h*.9f,w*.59f,h*.5f,w*.40f,h*.53f)
                cubicTo(w*.65f,h*.49f,w*.64f,h*.13f,w*.40f,h*.12f)
            }
            drawPath(b,ink,style=Stroke(lineWidth,cap=StrokeCap.Round,pathEffect=androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(9f,7f))))
            val c=Path().apply {
                moveTo(w*.91f,h*.24f)
                cubicTo(w*.70f,h*.0f,w*.67f,h*.9f,w*.93f,h*.82f)
            }
            drawPath(c,ink,style=Stroke(lineWidth,cap=StrokeCap.Round,pathEffect=androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(9f,7f))))
        } else {
            val a=Path().apply { moveTo(w*.25f,h*.33f);cubicTo(w*.07f,h*.18f,w*.03f,h*.85f,w*.24f,h*.83f);lineTo(w*.25f,h*.29f);lineTo(w*.25f,h*.89f) }
            val b=Path().apply { moveTo(w*.41f,h*.08f);lineTo(w*.41f,h*.90f);moveTo(w*.41f,h*.43f);cubicTo(w*.66f,h*.24f,w*.65f,h*.90f,w*.41f,h*.80f) }
            val c=Path().apply {moveTo(w*.92f,h*.4f);cubicTo(w*.68f,h*.10f,w*.62f,h*.92f,w*.92f,h*.79f)}
            listOf(a,b,c).forEach { drawPath(it,ink,style=Stroke(lineWidth,cap=StrokeCap.Round,pathEffect=androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(8f,6f)))) }
        }
        repeat(3) { i -> drawCircle(Coral,5.dp.toPx(),Offset(w*(.12f+i*.31f),h*.19f)) }
    }
}

@Composable
private fun ProgressHomeTile(totalDone: Int, onClick:()->Unit, modifier:Modifier) {
    Row(
        modifier.shadow(5.dp,RoundedCornerShape(25.dp))
            .clip(RoundedCornerShape(25.dp))
            .background(Color.White.copy(alpha=.98f))
            .clickable(onClick=onClick)
            .padding(horizontal=14.dp,vertical=11.dp)
            .heightIn(min=60.dp),
        verticalAlignment=Alignment.CenterVertically,
        horizontalArrangement=Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier.size(58.dp).clip(RoundedCornerShape(17.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFFFFF8CF),Color(0xFFFFDA79)))),
            contentAlignment=Alignment.Center
        ) { Text("★",fontSize=43.sp,color=Color(0xFFFFB900)) }
        Column(Modifier.weight(1f),verticalArrangement=Arrangement.spacedBy(1.dp)) {
            Text("Moj napredak",fontSize=22.sp,color=Navy,fontWeight=FontWeight.Black)
            Text("Pogledaj svoje rezultate i postignuća",fontSize=13.sp,color=Navy.copy(alpha=.66f),maxLines=2)
        }
        Text("❯",fontSize=25.sp,color=Color(0xFF53688A))
    }
}

/** Bottom navigation intentionally matches the three-tab home reference. */
@Composable
private fun HomeNavigation(
    selected: String,
    onHome: ()->Unit,
    onProgress: ()->Unit,
    onGames: ()->Unit,
    onSettings: ()->Unit
) {
    Row(
        Modifier.fillMaxWidth()
            .shadow(9.dp,RoundedCornerShape(topStart=34.dp,topEnd=34.dp))
            .clip(RoundedCornerShape(topStart=34.dp,topEnd=34.dp))
            .background(Color.White.copy(alpha=.97f))
            .padding(horizontal=10.dp,vertical=5.dp)
            .height(77.dp),
        horizontalArrangement=Arrangement.spacedBy(5.dp),
        verticalAlignment=Alignment.CenterVertically
    ) {
        listOf(
            Triple("home","Početna",onHome),
            Triple("games","Igre",onGames),
            Triple("progress","Napredak",onProgress),
            Triple("settings","Postavke",onSettings)
        ).forEach { (key,label,click) ->
            val active=selected==key
            Column(
                Modifier.weight(1f).fillMaxHeight()
                    .clip(RoundedCornerShape(26.dp))
                    .background(if(active) Brush.verticalGradient(listOf(Teal,TealDark)) else Brush.verticalGradient(listOf(Color.Transparent,Color.Transparent)))
                    .clickable(onClick=click)
                    .semantics { contentDescription=label },
                horizontalAlignment=Alignment.CenterHorizontally,
                verticalArrangement=Arrangement.Center
            ) {
                HomeNavIcon(key,if(active)Color.White else Navy,Modifier.size(28.dp))
                Text(label,fontSize=14.sp,fontWeight=FontWeight.Bold,color=if(active)Color.White else Navy,maxLines=1)
            }
        }
    }
}

@Composable
private fun HomeNavIcon(key:String,color:Color,modifier:Modifier=Modifier) {
    Canvas(modifier) {
        val w=size.width
        val h=size.height
        val line=3.dp.toPx()
        when(key) {
            "home" -> {
                val roof=Path().apply {moveTo(w*.11f,h*.45f);lineTo(w*.5f,h*.12f);lineTo(w*.9f,h*.45f)}
                drawPath(roof,color,style=Stroke(line,cap=StrokeCap.Round))
                drawRoundRect(color,Offset(w*.19f,h*.43f),Size(w*.62f,h*.49f),CornerRadius(w*.055f))
                drawRoundRect(if(color==Color.White)Teal else Color.White,Offset(w*.43f,h*.66f),Size(w*.16f,h*.26f),CornerRadius(w*.03f))
            }
            "games" -> {
                drawRoundRect(color,Offset(w*.08f,h*.28f),Size(w*.84f,h*.54f),CornerRadius(w*.25f))
                drawLine(Color.White,Offset(w*.28f,h*.39f),Offset(w*.28f,h*.69f),line)
                drawLine(Color.White,Offset(w*.17f,h*.54f),Offset(w*.39f,h*.54f),line)
                drawCircle(Color.White,w*.06f,Offset(w*.69f,h*.45f))
                drawCircle(Color.White,w*.06f,Offset(w*.79f,h*.62f))
            }
            "progress" -> {
                listOf(.37f,.61f,.88f).forEachIndexed { i,v ->
                    drawRoundRect(color,Offset(w*(.12f+i*.30f),h*(1-v)),Size(w*.17f,h*v),CornerRadius(w*.08f))
                }
            }
            else -> {
                drawCircle(color,w*.37f,Offset(w*.5f,h*.5f))
                drawCircle(Color.White,w*.16f,Offset(w*.5f,h*.5f))
                repeat(8) { i ->
                    val a=(i*Math.PI/4).toFloat()
                    val center=Offset(
                        w*.5f + kotlin.math.cos(a)*w*.39f,
                        h*.5f + kotlin.math.sin(a)*h*.39f
                    )
                    drawLine(color,Offset(w*.5f+kotlin.math.cos(a)*w*.23f,h*.5f+kotlin.math.sin(a)*h*.23f),center,line,cap=StrokeCap.Round)
                }
            }
        }
    }
}

/** Full-bleed sky + green rolling hills, matching the project’s local visual identity. */
@Composable
private fun HomeScenery(modifier:Modifier) {
    Canvas(modifier.background(Brush.verticalGradient(
        listOf(Color(0xFF80D6F4),Color(0xFFD9F7FF),Color(0xFFECFAEF),Color(0xFFBFF08C))
    ))) {
        val w=size.width;val h=size.height
        fun cloud(cx:Float,cy:Float,scale:Float) {
            val white=Color.White.copy(alpha=.76f)
            drawOval(white,Offset(cx-scale*.5f,cy),Size(scale,scale*.27f))
            drawCircle(white,scale*.18f,Offset(cx-scale*.16f,cy))
            drawCircle(white,scale*.22f,Offset(cx+scale*.09f,cy))
        }
        cloud(w*.20f,h*.12f,w*.23f)
        cloud(w*.85f,h*.19f,w*.27f)
        val background=Path().apply{
            moveTo(0f,h*.88f)
            cubicTo(w*.22f,h*.81f,w*.43f,h*.87f,w*.63f,h*.83f)
            cubicTo(w*.78f,h*.80f,w*.90f,h*.87f,w,h*.83f)
            lineTo(w,h);lineTo(0f,h);close()
        }
        drawPath(background,Color(0xFF84D6A3))
        val hill=Path().apply{
            moveTo(0f,h*.92f)
            cubicTo(w*.24f,h*.87f,w*.44f,h*.93f,w*.62f,h*.90f)
            cubicTo(w*.79f,h*.87f,w*.95f,h*.92f,w,h*.88f)
            lineTo(w,h);lineTo(0f,h);close()
        }
        drawPath(hill,Color(0xFF8DD968))
    }
}
