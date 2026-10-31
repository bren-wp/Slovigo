package hr.brendigo.slovigo.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Screen 01: reference-based SLOVIGO welcome screen.
 *
 * Artwork is local Compose vector drawing (no network, no baked-in UI screenshot).
 * Proportions are derived from the 725x1536 supplied phone reference.
 * On short, wide screens the content changes to two columns instead of cropping
 * the mascot or hiding the primary CTA.
 */
@Composable
fun SplashScreen(onStart: () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val landscape = maxWidth >= 650.dp && maxWidth > maxHeight
        val short = maxHeight < 560.dp
        SplashLandscape(Modifier.fillMaxSize())

        if (landscape) {
            Row(
                modifier = Modifier.fillMaxSize().padding(horizontal = 26.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    Modifier.weight(1f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    SplashBook(Modifier.size(if (short) 80.dp else 112.dp))
                    SplashWordmark(if (short) 32 else 42)
                    Text(
                        "Učimo pisati kroz igru",
                        fontSize = if (short) 18.sp else 23.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Navy,
                        textAlign = TextAlign.Center
                    )
                    SplashStartButton(onStart, Modifier.widthIn(max = 390.dp).fillMaxWidth(.9f))
                    Text(
                        "Nauči slova, potez po potez",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Navy,
                        textAlign = TextAlign.Center
                    )
                }
                Box(
                    Modifier.weight(1f).fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    SplashPencil(
                        Modifier.fillMaxHeight(.94f).fillMaxWidth(.9f)
                    )
                }
            }
        } else {
            val columnWidth = minOf(maxWidth, 490.dp)
            val bookSize = (maxHeight * .13f).coerceIn(65.dp, 124.dp)
            val mascotHeight = (maxHeight * .47f).coerceIn(180.dp, 520.dp)
            val logoFont = (columnWidth.value * .123f).toInt().coerceIn(30, 54)
            val small = maxHeight < 610.dp
            Column(
                Modifier.align(Alignment.TopCenter)
                    .width(columnWidth)
                    .offset(y = maxHeight * if (small) .025f else .055f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (small) 0.dp else 5.dp)
            ) {
                SplashBook(Modifier.size(bookSize))
                SplashWordmark(logoFont)
                Text(
                    "Učimo pisati kroz igru",
                    fontSize = if (small) 16.sp else 21.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Navy,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
                Canvas(Modifier.padding(top = 7.dp).width(140.dp).height(7.dp)) {
                    drawLine(Yellow, Offset(4f, size.height / 2), Offset(size.width - 4f, size.height / 2), 5.dp.toPx(), cap = StrokeCap.Round)
                }
            }
            Box(
                Modifier.align(Alignment.Center)
                    .offset(y = maxHeight * .085f)
                    .height(mascotHeight)
                    .width((columnWidth * .88f).coerceAtMost(420.dp)),
                contentAlignment = Alignment.Center
            ) {
                SplashPencil(Modifier.fillMaxSize())
            }
            Column(
                Modifier.align(Alignment.BottomCenter)
                    .width(columnWidth)
                    .padding(horizontal = 22.dp)
                    .padding(bottom = if (small) 10.dp else maxHeight * .035f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (small) 8.dp else 20.dp)
            ) {
                SplashStartButton(onStart, Modifier.fillMaxWidth())
                Text(
                    "Nauči slova, potez po potez",
                    color = Navy,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = if (small) 15.sp else 19.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun SplashStartButton(onStart: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onStart,
        modifier = modifier
            .height(72.dp)
            .shadow(8.dp, RoundedCornerShape(37.dp))
            .semantics { contentDescription = "Započni vježbanje slova" },
        shape = RoundedCornerShape(37.dp),
        contentPadding = PaddingValues(horizontal = 18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009DA6), contentColor = Color.White)
    ) {
        Text("▶", fontSize = 30.sp, modifier = Modifier.padding(end = 12.dp))
        Text("Započni", fontSize = 27.sp, fontWeight = FontWeight.Black, maxLines = 1)
    }
}

@Composable
private fun SplashWordmark(fontSize: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val colors = listOf(Color(0xFF009AA2), Yellow, Coral, Blue, Yellow, Teal, Coral)
        "SLOVIGO".forEachIndexed { i, c ->
            Text(
                c.toString(),
                style = TextStyle(
                    color = colors[i],
                    fontSize = fontSize.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2.5).sp,
                    shadow = Shadow(Color.White, Offset(1.5f, 2.5f), 6f)
                )
            )
        }
    }
}

@Composable
private fun SplashBook(modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            // Yellow rays.
            listOf(.18f to .13f, .50f to .02f, .81f to .13f).forEach { (x, y) ->
                drawLine(Yellow, Offset(w * x, h * y), Offset(w * x, h * (y + .09f)), 6.dp.toPx(), cap = StrokeCap.Round)
            }
            val left = Path().apply {
                moveTo(w*.49f,h*.31f)
                cubicTo(w*.34f,h*.19f,w*.14f,h*.2f,w*.11f,h*.30f)
                lineTo(w*.11f,h*.75f)
                cubicTo(w*.22f,h*.66f,w*.40f,h*.68f,w*.50f,h*.80f)
                close()
            }
            val right = Path().apply {
                moveTo(w*.51f,h*.31f)
                cubicTo(w*.66f,h*.19f,w*.86f,h*.2f,w*.89f,h*.30f)
                lineTo(w*.89f,h*.75f)
                cubicTo(w*.78f,h*.66f,w*.60f,h*.68f,w*.50f,h*.80f)
                close()
            }
            drawPath(left, Color(0xFFFFFCF4))
            drawPath(right, Color(0xFFFFFCF4))
            drawPath(left, Navy, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
            drawPath(right, Navy, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
            drawLine(Teal, Offset(w*.50f,h*.32f), Offset(w*.50f,h*.80f), 3.dp.toPx())
            drawLine(Teal, Offset(w*.11f,h*.78f), Offset(w*.49f,h*.88f), 6.dp.toPx(), cap = StrokeCap.Round)
            drawLine(Teal, Offset(w*.89f,h*.78f), Offset(w*.51f,h*.88f), 6.dp.toPx(), cap = StrokeCap.Round)
        }
        Text(
            "Aa",
            modifier = Modifier.offset(y = 5.dp),
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp,
            color = Navy
        )
    }
}

@Composable
private fun SplashLandscape(modifier: Modifier = Modifier) {
    Canvas(modifier.background(Brush.verticalGradient(
        listOf(Color(0xFF6BCBF1), Color(0xFFC7F4FF), Color(0xFFE0F8F1), Color(0xFFBDF082))
    ))) {
        val w = size.width
        val h = size.height
        fun cloud(cx: Float, cy: Float, s: Float) {
            val white = Color.White.copy(alpha = .85f)
            drawOval(white, Offset(cx-s*.5f, cy), Size(s, s*.32f))
            drawCircle(white, s*.24f, Offset(cx-s*.12f, cy))
            drawCircle(white, s*.19f, Offset(cx+s*.13f, cy+s*.01f))
        }
        cloud(w*.03f,h*.10f,w*.28f)
        cloud(w*.93f,h*.12f,w*.30f)
        cloud(w*.08f,h*.48f,w*.22f)
        cloud(w*.91f,h*.49f,w*.20f)
        val farHills = Path().apply {
            moveTo(0f,h*.73f)
            cubicTo(w*.18f,h*.66f,w*.26f,h*.8f,w*.50f,h*.72f)
            cubicTo(w*.74f,h*.66f,w*.85f,h*.74f,w,h*.69f)
            lineTo(w,h);lineTo(0f,h);close()
        }
        drawPath(farHills, Color(0xFF8DDDA9))
        val hills = Path().apply {
            moveTo(0f,h*.78f)
            cubicTo(w*.18f,h*.73f,w*.31f,h*.84f,w*.55f,h*.78f)
            cubicTo(w*.76f,h*.73f,w*.9f,h*.78f,w,h*.75f)
            lineTo(w,h);lineTo(0f,h);close()
        }
        drawPath(hills, Color(0xFFB4E77A))
        val foreground = Path().apply {
            moveTo(0f,h*.87f)
            cubicTo(w*.28f,h*.84f,w*.49f,h*.90f,w*.68f,h*.86f)
            cubicTo(w*.87f,h*.82f,w*.92f,h*.86f,w,h*.84f)
            lineTo(w,h);lineTo(0f,h);close()
        }
        drawPath(foreground, Color(0xFFA5DA6B))
        // Small white flowers and dark leaves near the ground, like the reference.
        listOf(.12f to .90f, .86f to .89f).forEach { (x,y) ->
            val center=Offset(w*x,h*y)
            for (i in 0..4) {
                val a=i*1.2566
                drawCircle(Color.White, w*.012f, Offset(center.x+kotlin.math.cos(a).toFloat()*w*.016f, center.y+kotlin.math.sin(a).toFloat()*w*.016f))
            }
            drawCircle(Yellow, w*.010f, center)
        }
    }
}

/**
 * Larger friendly pencil mascot for the welcome scene; every part is vector artwork.
 * Intentionally keeps all decoration in Compose rather than drawing interface text into a bitmap.
 */
@Composable
private fun SplashPencil(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        rotate(-11f, Offset(w*.5f,h*.5f)) {
            // Arms go behind the pencil, drawn with heavy navy outlines.
            drawLine(Navy, Offset(w*.28f,h*.54f),Offset(w*.06f,h*.42f),w*.042f,cap=StrokeCap.Round)
            drawLine(Navy, Offset(w*.72f,h*.57f),Offset(w*.93f,h*.49f),w*.042f,cap=StrokeCap.Round)
            // Yellow ray accents to the left of the waving glove.
            drawLine(Yellow,Offset(w*.04f,h*.31f),Offset(w*.01f,h*.25f),w*.027f,cap=StrokeCap.Round)
            drawLine(Yellow,Offset(w*.12f,h*.30f),Offset(w*.13f,h*.23f),w*.027f,cap=StrokeCap.Round)
            // Main pencil barrel.
            val barrel = Path().apply {
                moveTo(w*.31f,h*.22f)
                lineTo(w*.69f,h*.22f)
                lineTo(w*.72f,h*.73f)
                lineTo(w*.50f,h*.95f)
                lineTo(w*.28f,h*.73f)
                close()
            }
            drawPath(barrel, Brush.horizontalGradient(
                listOf(Color(0xFFF1AE15),Color(0xFFFFDA3E),Color(0xFFFFBA15))
            ))
            drawPath(barrel, Navy,style=Stroke(width=w*.013f,cap=StrokeCap.Round))
            // subtle yellow highlights
            drawLine(Color.White.copy(alpha=.34f),Offset(w*.37f,h*.3f),Offset(w*.37f,h*.66f),w*.017f,cap=StrokeCap.Round)
            drawLine(Color(0xFFDE9420).copy(alpha=.4f),Offset(w*.63f,h*.32f),Offset(w*.64f,h*.65f),w*.009f)
            // Pink eraser curved cap.
            val eraser = Path().apply {
                moveTo(w*.30f,h*.23f)
                lineTo(w*.30f,h*.125f)
                cubicTo(w*.28f,h*.035f,w*.38f,h*.015f,w*.53f,h*.03f)
                cubicTo(w*.66f,h*.033f,w*.72f,h*.09f,w*.71f,h*.155f)
                lineTo(w*.70f,h*.23f)
                close()
            }
            drawPath(eraser,Brush.verticalGradient(listOf(Color(0xFFFFA5AB),Color(0xFFFF5F78))))
            drawPath(eraser,Navy,style=Stroke(width=w*.013f))
            drawOval(Color.White.copy(alpha=.45f),topLeft=Offset(w*.39f,h*.048f),size=Size(w*.18f,h*.024f))
            // Metallic collar drawn over the eraser lower edge.
            drawRoundRect(
                Brush.verticalGradient(listOf(Color(0xFFFCFCFF),Color(0xFFC8D8E7),Color(0xFF94AABC))),
                topLeft=Offset(w*.28f,h*.174f),
                size=Size(w*.45f,h*.09f),
                cornerRadius=CornerRadius(w*.04f)
            )
            drawRoundRect(
                Navy.copy(alpha=.7f),
                topLeft=Offset(w*.28f,h*.174f),
                size=Size(w*.45f,h*.09f),
                cornerRadius=CornerRadius(w*.04f),
                style=Stroke(width=w*.009f)
            )
            drawLine(Color.White.copy(alpha=.85f), Offset(w*.31f,h*.195f),Offset(w*.69f,h*.195f),w*.009f)
            // Wood triangle / graphite point.
            val wood = Path().apply {
                moveTo(w*.28f,h*.73f)
                lineTo(w*.50f,h*.95f)
                lineTo(w*.72f,h*.73f)
                quadraticTo(w*.60f,h*.80f,w*.50f,h*.74f)
                quadraticTo(w*.38f,h*.80f,w*.28f,h*.73f)
                close()
            }
            drawPath(wood, Brush.verticalGradient(listOf(Color(0xFFFFD9A5),Color(0xFFD6A373))))
            drawPath(wood,Navy,style=Stroke(width=w*.012f))
            val lead = Path().apply {
                moveTo(w*.425f,h*.88f);lineTo(w*.50f,h*.95f);lineTo(w*.575f,h*.88f)
                quadraticTo(w*.50f,h*.90f,w*.425f,h*.88f);close()
            }
            drawPath(lead,Navy)
            // White eyes with large glossy navy pupils.
            listOf(.414f,.60f).forEach { xx ->
                drawOval(Color.White,Offset(w*(xx-.067f),h*.37f),Size(w*.13f,h*.12f))
                drawOval(Navy,Offset(w*(xx-.041f),h*.393f),Size(w*.075f,h*.085f))
                drawCircle(Color.White,w*.014f,Offset(w*(xx-.012f),h*.412f))
                drawCircle(Color.White,w*.007f,Offset(w*(xx+.023f),h*.438f))
            }
            val leftBrow=Path().apply{moveTo(w*.35f,h*.35f);quadraticTo(w*.42f,h*.32f,w*.47f,h*.345f)}
            val rightBrow=Path().apply{moveTo(w*.54f,h*.345f);quadraticTo(w*.60f,h*.32f,w*.66f,h*.35f)}
            drawPath(leftBrow,Navy,style=Stroke(width=w*.014f,cap=StrokeCap.Round))
            drawPath(rightBrow,Navy,style=Stroke(width=w*.014f,cap=StrokeCap.Round))
            // Smiling mouth and tongue.
            val mouth=Path().apply{
                moveTo(w*.425f,h*.505f)
                quadraticTo(w*.5f,h*.545f,w*.59f,h*.502f)
                quadraticTo(w*.595f,h*.605f,w*.50f,h*.61f)
                quadraticTo(w*.413f,h*.60f,w*.425f,h*.505f);close()
            }
            drawPath(mouth,Navy)
            drawOval(Coral,Offset(w*.466f,h*.567f),Size(w*.07f,h*.036f))
            drawCircle(Coral.copy(alpha=.6f),w*.048f,Offset(w*.332f,h*.51f))
            drawCircle(Coral.copy(alpha=.6f),w*.048f,Offset(w*.67f,h*.51f))
            // White gloves and fingers.
            drawCircle(Color.White,w*.078f,Offset(w*.055f,h*.405f))
            drawCircle(Color.White,w*.077f,Offset(w*.918f,h*.475f))
            drawCircle(Color.White,w*.031f,Offset(w*.02f,h*.35f))
            drawCircle(Color.White,w*.031f,Offset(w*.078f,h*.335f))
            drawCircle(Color.White,w*.031f,Offset(w*.13f,h*.37f))
            drawLine(Navy,Offset(w*.018f,h*.395f),Offset(w*.025f,h*.35f),w*.010f,cap=StrokeCap.Round)
            drawLine(Navy,Offset(w*.065f,h*.395f),Offset(w*.08f,h*.346f),w*.010f,cap=StrokeCap.Round)
            drawCircle(Navy,w*.078f,Offset(w*.918f,h*.475f),style=Stroke(width=w*.014f))
            drawCircle(Navy,w*.078f,Offset(w*.055f,h*.405f),style=Stroke(width=w*.014f))
        }
    }
}
