package com.example.lab13_animaciones_videojuego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import android.graphics.BitmapFactory
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import com.example.lab13_animaciones_videojuego.ui.theme.Lab13AnimacionesVideojuegoTheme
import kotlinx.coroutines.delay
import androidx.compose.animation.core.animateFloatAsState
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab13AnimacionesVideojuegoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    GameApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

enum class AppScreen { Menu, Game, Load, Settings }

@Composable
fun AnimatedVisibilityDemo(modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { visible = !visible }) {
            Text(text = if (visible) "Ocultar" else "Mostrar")
        }
        Spacer(Modifier.height(16.dp))
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(Color(0xFF4CAF50), RoundedCornerShape(16.dp))
            )
        }
    }
}

@Composable
fun AnimatedColorBoxDemo(modifier: Modifier = Modifier) {
    var isGreen by remember { mutableStateOf(false) }
    var useSpring by remember { mutableStateOf(false) }
    val targetColor = if (isGreen) Color(0xFF4CAF50) else Color(0xFF2196F3)
    val spec = if (useSpring) spring<Color>(dampingRatio = 0.6f, stiffness = 300f) else tween<Color>(durationMillis = 1000)
    val animatedColor by animateColorAsState(targetValue = targetColor, animationSpec = spec, label = "boxColor")
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { isGreen = !isGreen }) { Text(text = if (isGreen) "Azul" else "Verde") }
            Button(onClick = { useSpring = !useSpring }) { Text(text = if (useSpring) "Spring" else "Tween") }
        }
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(animatedColor, RoundedCornerShape(16.dp))
        )
    }
}

@Composable
fun AnimatedSizePositionDemo(modifier: Modifier = Modifier) {
    var toggled by remember { mutableStateOf(false) }
    val sizeTarget = if (toggled) 180.dp else 100.dp
    val xTarget = if (toggled) 80.dp else 0.dp
    val yTarget = if (toggled) 40.dp else 0.dp
    val size by animateDpAsState(targetValue = sizeTarget, animationSpec = tween(durationMillis = 500), label = "size")
    val x by animateDpAsState(targetValue = xTarget, animationSpec = tween(durationMillis = 500), label = "x")
    val y by animateDpAsState(targetValue = yTarget, animationSpec = tween(durationMillis = 500), label = "y")
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { toggled = !toggled }) { Text(text = if (toggled) "Reiniciar" else "Mover y Expandir") }
        Spacer(Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = x, y = y)
                .background(Color(0xFFFF9800), RoundedCornerShape(16.dp))
        )
    }
}

enum class UiState { Loading, Content, Error, Choices }

@Composable
fun AnimatedContentDemo(modifier: Modifier = Modifier) {
    var state by remember { mutableStateOf(UiState.Loading) }
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            state = when (state) {
                UiState.Loading -> UiState.Content
                UiState.Content -> UiState.Error
                UiState.Error -> UiState.Choices
                UiState.Choices -> UiState.Loading
            }
        }) { Text("Cambiar estado") }
        Spacer(Modifier.height(16.dp))
        AnimatedContent(
            targetState = state,
            transitionSpec = { fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(250)) },
            label = "animatedContent"
        ) { s ->
            when (s) {
                UiState.Loading -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text("Cargando...")
                }
                UiState.Content -> Text("Contenido listo")
                UiState.Error -> Text("Ocurrió un error")
                UiState.Choices -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { /* no-op demo */ }) { Text("Opción A") }
                    Button(onClick = { /* no-op demo */ }) { Text("Opción B") }
                }
            }
        }
    }
}

data class World(val id: Int, val name: String, val themeColor: Color)

@Suppress("unused")
enum class Scene { GalaxyMap, Travel, WorldScene, TravelBack }

enum class MissionState { Pending, InProgress, Completed, Failed }

data class Mission(val id: Int, val title: String, val description: String, val state: MissionState)

@Composable
fun GameApp(modifier: Modifier = Modifier) {
    var screen by remember { mutableStateOf(AppScreen.Menu) }
    when (screen) {
        AppScreen.Menu -> MainMenuScreen(
            modifier = modifier.fillMaxSize(),
            onStartGame = { screen = AppScreen.Game },
            onLoadState = { screen = AppScreen.Load },
            onSettings = { screen = AppScreen.Settings },
            onExit = { /* En Android, normalmente se cierra manualmente; aquí podemos permanecer en el menú */ }
        )
        AppScreen.Game -> Box(modifier = modifier.fillMaxSize()) {
            CyberRunnerGameScreen(
                modifier = Modifier.fillMaxSize(),
                onExitToMenu = { screen = AppScreen.Menu }
            )
        }
        AppScreen.Load -> Box(modifier = modifier.fillMaxSize().background(Color(0xFF102A43))) {
            Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                Text("Cargar estado (pendiente)", color = Color.White)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { screen = AppScreen.Menu }) { Text("Volver") }
            }
        }
        AppScreen.Settings -> Box(modifier = modifier.fillMaxSize().background(Color(0xFF0B3D91))) {
            Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.Center) {
                Text("Configuración (pendiente)", color = Color.White)
                Spacer(Modifier.height(12.dp))
                Button(onClick = { screen = AppScreen.Menu }) { Text("Volver") }
            }
        }
    }
}

@Composable
fun MainMenuScreen(
    modifier: Modifier = Modifier,
    onStartGame: () -> Unit,
    onLoadState: () -> Unit,
    onSettings: () -> Unit,
    onExit: () -> Unit
) {
    // Elegir fondo de menú según relación de aspecto del dispositivo
    val density = LocalDensity.current
    val config = LocalConfiguration.current
    val w = with(density) { config.screenWidthDp.dp.toPx() }
    val h = with(density) { config.screenHeightDp.dp.toPx() }
    val aspect = if (h > 0f) w / h else 1.8f
    val candidates = when {
        aspect >= 2.1f -> listOf("bg_menu_21x9.png", "bg_menu_21x9.jpg")
        aspect >= 2.0f -> listOf("bg_menu_20x9.png", "bg_menu_20x9.jpg")
        aspect >= 1.95f -> listOf("bg_menu_19_5x9.png", "bg_menu_19_5x9.jpg")
        aspect >= 1.78f -> listOf("bg_menu_16x9.png", "bg_menu_16x9.jpg")
        aspect >= 1.60f -> listOf("bg_menu_16x10.png", "bg_menu_16x10.jpg")
        else -> listOf("bg_menu_4x3.png", "bg_menu_4x3.jpg")
    } + listOf("bg_menu.png", "bg_menu.jpg", "menu_background.jpg", "map_initial_navigation.jpg")
    val bg = candidates.firstNotNullOfOrNull { rememberAssetImage(it) }
    Box(modifier = modifier.background(Color.Black)) {
        if (bg != null) {
            // Dibuja imagen de fondo en modo "cover": rellena todo sin deformar, recortando sobrante
            Canvas(modifier = Modifier.matchParentSize()) {
                val imgW = bg.width.toFloat()
                val imgH = bg.height.toFloat()
                val scale = kotlin.math.max(size.width / imgW, size.height / imgH)
                val drawW = (imgW * scale).toInt()
                val drawH = (imgH * scale).toInt()
                val dx = ((size.width - drawW) * 0.5f).toInt()
                // Sesgo vertical hacia la parte superior para asegurar que el título "ASTRO" quede visible
                // vBias=0f ancla arriba, 0.5f centra, 1f ancla abajo
                val vBias = 0f
                val dy = ((size.height - drawH) * vBias).toInt()
                drawImage(bg, IntOffset(0, 0), IntSize(bg.width, bg.height), IntOffset(dx, dy), IntSize(drawW, drawH))
            }
        }
        // Panel de botones más compacto, alineado arriba a la derecha, con leve desplazamiento
        Box(Modifier.fillMaxSize().padding(24.dp)) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = 40.dp)
                    .width(280.dp)
                    .background(Color(0x66000000), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MenuButton("Iniciar juego", onStartGame)
                MenuButton("Cargar estado", onLoadState)
                MenuButton("Configuración", onSettings)
                MenuButton("Salir", onExit)
            }
        }
    }
}

@Composable
fun MenuButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick) { Text(text) }
}

// -----------------------------
// Cyber Runner (endless runner vertical) - versión mínima jugable
// Controles: tap = salto, swipe abajo o long press = deslizar
// Obstáculos: rectángulos bajos (saltar) y altos (deslizar)
// Parallax simple de fondo, HUD con distancia/puntuación
// -----------------------------

private data class Obstacle(
    var x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    val high: Boolean, // true: alto (se pasa con deslizar), false: bajo (se pasa con salto)
    val bouncy: Boolean // true: actúa como trampolín (solo en obstáculos pequeños)
)

// Enemigo que dispara proyectiles hacia la izquierda
private data class Enemy(
    var x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
    var fireT: Float,    // temporizador para disparar
    val fireRate: Float  // cada cuánto dispara
)

// Proyectil circular
private data class Bullet(
    var x: Float,
    var y: Float,
    val r: Float,
    val speed: Float
)

// Enemigo de techo: viga/torque que gira 180° y actúa como peligro al barrer
private data class TorqueEnemy(
    var x: Float,        // pivote X, se desplaza de derecha a izquierda
    val y: Float,        // pivote Y (cerca del techo)
    val length: Float,   // longitud de la viga
    val thickness: Float,// grosor visual y umbral de colisión
    var angle: Float,    // en radianes; 0 apunta hacia abajo; rango [-PI/2, +PI/2]
    var angVel: Float,   // velocidad angular (rad/s)
    var dir: Int         // +1 o -1 para alternar giro
)

@Composable
fun CyberRunnerGameScreen(
    modifier: Modifier = Modifier,
    onExitToMenu: () -> Unit
) {
    // Paleta propuesta
    val bgColor = Color(0xFF0A0F1E)
    val layerFar = Color(0xFF111827)
    val layerMid = Color(0xFF1F2937)
    val layerNear = Color(0xFF374151)
    val playerColor = Color(0xFF00E5FF)
    val obstacleLowColor = Color(0xFFFF2D9A)
    val obstacleHighColor = Color(0xFFFFA726)
    // Color específico para obstáculos con rebote (trampolines)
    val obstacleBouncyColor = Color(0xFF4CAF50)
    val enemyColor = Color(0xFFE53935)
    val bulletColor = Color(0xFFB0BEC5)
    val hudColor = Color.White
    // Fondo opcional desde assets para la pantalla de juego (landscape)
    // Se intentan varios nombres para mayor flexibilidad
    val gameBg = rememberAssetImage("bg_planet.png")
        ?: rememberAssetImage("bg_planet.jpg")
        ?: rememberAssetImage("game_bg_landscape.jpg")
        ?: rememberAssetImage("game_bg_landscape.png")

    // Personaje del usuario: usar sprites recortados (sin márgenes transparentes) cuando existan
    val userRun = rememberTrimmedSprite("user_run.png") ?: rememberTrimmedSprite("hero_right.png")
    val userJetpack = rememberTrimmedSprite("user_up.png") ?: rememberTrimmedSprite("hero_up.png") ?: userRun
    val userCrouch = rememberTrimmedSprite("user_down.png") ?: rememberTrimmedSprite("hero_down.png") ?: userRun
    val userIdle = rememberTrimmedSprite("user.png") ?: rememberTrimmedSprite("hero_idle.png") ?: userRun
    // Sprites recortados para obstáculos y enemigos
    val medusaSpr = rememberTrimmedSprite("medusa_enemy.png")
    val marcianSpr = rememberTrimmedSprite("marcian_enemy.png")

    var running by remember { mutableStateOf(true) }
    var score by remember { mutableFloatStateOf(0f) } // basado en distancia
    var bestScore by remember { mutableFloatStateOf(0f) }
    // Factor de ZOOM global del juego (1.0 = normal). Aumenta tamaños de jugador, obstáculos, enemigos, etc.
    var zoom by remember { mutableFloatStateOf(1.4f) }

    // Física y juego
    var playerX by remember { mutableFloatStateOf(0f) }
    var playerY by remember { mutableFloatStateOf(0f) }
    var playerW by remember { mutableFloatStateOf(0f) }
    var playerH by remember { mutableFloatStateOf(0f) }
    var vy by remember { mutableFloatStateOf(0f) }
    var jumping by remember { mutableStateOf(false) } // estado físico: en salto/aire
    var jumpRequested by remember { mutableStateOf(false) } // input: se solicitó salto este frame
    var airJumpsLeft by remember { mutableIntStateOf(2) } // impulsos extra permitidos en aire
    var sliding by remember { mutableStateOf(false) }
    var slideTimer by remember { mutableFloatStateOf(0f) }
    var slideHeld by remember { mutableStateOf(false) }

    var groundY by remember { mutableFloatStateOf(0f) }
    var worldSpeed by remember { mutableFloatStateOf(320f) } // px/s
    // Ajustes de jugabilidad: más salto y un poco menos gravedad
    var gravity by remember { mutableFloatStateOf(1900f) }
    var jumpImpulse by remember { mutableFloatStateOf(1150f) }

    val obstacles = remember { mutableStateOf(listOf<Obstacle>()) }
    var spawnT by remember { mutableFloatStateOf(0f) }
    var spawnInterval by remember { mutableFloatStateOf(1.5f) }

    // Enemigos y proyectiles
    val enemies = remember { mutableStateOf(listOf<Enemy>()) }
    val bullets = remember { mutableStateOf(listOf<Bullet>()) }
    var enemySpawnT by remember { mutableFloatStateOf(4.5f) }
    var enemySpawnInterval by remember { mutableFloatStateOf(5.5f) }
    // Enemigos de techo (torques)
    val torques = remember { mutableStateOf(listOf<TorqueEnemy>()) }
    var torqueSpawnT by remember { mutableFloatStateOf(7.0f) }
    var torqueSpawnInterval by remember { mutableFloatStateOf(9.0f) }

    // Lienzo principal y bucle de juego
    Box(modifier = modifier.background(bgColor)) {
        val density = LocalDensity.current
        val config = LocalConfiguration.current
        val w = with(density) { config.screenWidthDp.dp.toPx() }
        val h = with(density) { config.screenHeightDp.dp.toPx() }
        // Setup inicial
        LaunchedEffect(w, h, zoom) {
            val px = w * 0.22f
            val ph = h * 0.08f * zoom
            val pw = ph * 0.8f
            playerX = px
            playerW = pw
            playerH = ph
            groundY = h * 0.85f
            playerY = groundY - playerH
        }

        // Gestos: tap = salto; swipe abajo/longPress = deslizar
        val gestureMod = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        if (running) {
                            // Unificar entrada: usar jumpRequested para que el salto se procese en el bucle físico
                            // evitando estados inconsistentes cuando se pulsa muchas veces.
                            jumpRequested = true
                        }
                    },
                    onLongPress = {
                        if (running && !jumping) {
                            sliding = true
                            slideTimer = 0.45f
                        }
                    }
                )
            }
            .pointerInput(sliding) {
                detectDragGestures(onDragStart = { /* no-op */ }, onDragEnd = { /* no-op */ }) { _, drag ->
                    if (drag.y > 18f && running && !jumping) {
                        sliding = true
                        slideTimer = 0.45f
                    }
                }
            }

        // Bucle de juego
        LaunchedEffect(running) {
            // Bucle de juego con control de dt para evitar saltos por frames largos
            var last = System.nanoTime()
            // Estado físico local: suelo/aire y velocidad horizontal en aire
            var groundedLocal = true
            var vxLocal = 0f
            // Estados para caída más realista desde bordes: "coyote time" y fase de desprendimiento
            var coyoteTimer = 0f           // ventana corta para saltar tras abandonar un borde
            var edgeFallTimer = 0f         // corto período con deriva horizontal anulada al inicio de la caída
            var hadSupportPrev = true      // soporte en el frame anterior
            while (true) {
                val now = System.nanoTime()
                var dt = ((now - last) / 1_000_000_000.0).toFloat()
                // Evitar saltos bruscos por frames largos (pausas), limitando dt
                dt = dt.coerceIn(0f, 0.05f)
                last = now
                if (running) {
                    // Posición previa del jugador para detectar cruces de superficies y trayectoria
                    val prevY = playerY
                    // Temporizadores auxiliares
                    if (coyoteTimer > 0f) coyoteTimer -= dt
                    if (edgeFallTimer > 0f) edgeFallTimer -= dt
                    // Aumentar velocidad y dificultad gradualmente
                    worldSpeed = (worldSpeed + 6f * dt).coerceAtMost(900f)
                    spawnInterval = (spawnInterval - 0.02f * dt).coerceAtLeast(0.9f)
                    enemySpawnInterval = (enemySpawnInterval - 0.01f * dt).coerceAtLeast(3.8f)

                    // Solicitud de salto: procesar input separado del estado físico
                    if (jumpRequested) {
                        if (groundedLocal || coyoteTimer > 0f) {
                            groundedLocal = false
                            vy = -jumpImpulse
                            jumping = true
                            // Separación mínima para escapar del epsilon de soporte (reducida para evitar saltos "extra altos")
                            playerY -= (h * 0.012f * zoom)
                            // Al saltar desde borde, cancelamos la fase de desprendimiento
                            edgeFallTimer = 0f
                        } else if (airJumpsLeft > 0) {
                            // Impulso adicional en aire (doble salto estilo propulsor)
                            vy = -jumpImpulse * 0.75f
                            airJumpsLeft -= 1
                            jumping = true
                        }
                        // Consumir la solicitud en este frame
                        jumpRequested = false
                    }

                    // Física del jugador: gravedad en aire y deriva horizontal sólo durante la caída
                    if (!groundedLocal) {
                        val airDriftFall = 0.16f // deriva ajustada para caer en el siguiente bloque durante descensos
                        if (edgeFallTimer <= 0f) {
                            if (vy >= 0f) {
                                // Deriva sólo al descender; se desactiva en los primeros ms tras dejar el borde
                                vxLocal = airDriftFall * worldSpeed
                                playerX += vxLocal * dt
                            } else {
                                vxLocal = 0f
                            }
                        } else {
                            // Fase de desprendimiento: anula la deriva horizontal para evitar "salto hacia delante" al caer del borde
                            vxLocal = 0f
                        }
                        vy += gravity * dt
                        playerY += vy * dt
                        // Límite suave para evitar salir de la escena por deriva
                        val minX = w * 0.10f
                        val maxX = w * 0.90f
                        if (playerX < minX) playerX = minX
                        if (playerX > maxX) playerX = maxX
                    } else {
                        vxLocal = 0f
                    }
                    // Deslizar: mantener agachado mientras el botón esté presionado; si no, usar temporizador
                    if (slideHeld || sliding) {
                        playerH = h * 0.05f * zoom // altura agachado
                        if (slideHeld) {
                            sliding = true
                            playerY = groundY - playerH
                        } else {
                            slideTimer -= dt
                            if (slideTimer <= 0f) {
                                sliding = false
                                playerH = h * 0.08f * zoom
                                playerY = groundY - playerH
                            } else {
                                playerY = groundY - playerH
                            }
                        }
                    } else if (groundedLocal) {
                        // Solo ajustar altura/posición si realmente estamos apoyados
                        playerH = h * 0.08f * zoom
                        playerY = groundY - playerH
                    }

                    // Soporte sólido: aterrizaje sobre cara superior
                    // Detecta cruce de la superficie superior entre el frame anterior y el actual
                    var landed = false
                    obstacles.value.forEach { o ->
                        val horiz = playerX < o.x + o.w && playerX + playerW > o.x
                        if (horiz) {
                            val topY = o.y
                            val prevBottom = prevY + playerH
                            val currBottom = playerY + playerH
                            // Si venía por encima de la cara superior y ahora la cruzó hacia abajo ⇒ ajustar/botar
                            if (prevBottom <= topY && currBottom >= topY && vy >= 0f) {
                                val bounceCoef = 0.65f
                                if (o.bouncy) {
                                    // Trampolín: rebote sólo en bloques pequeños
                                    playerY = topY - playerH - (h * 0.01f)
                                    vy = -jumpImpulse * bounceCoef
                                    jumping = true
                                    groundedLocal = false
                                    vxLocal = 0f // rebote estrictamente vertical
                                } else {
                                    // Soporte normal: quedarse encima
                                    playerY = topY - playerH
                                    vy = 0f
                                    jumping = false
                                    groundedLocal = true
                                    airJumpsLeft = 2 // recargar impulsos de aire al aterrizar en soporte sólido
                                }
                                landed = true
                            }
                        }
                    }
                    // Aterrizaje con el suelo (detección de cruce)
                    if (!landed) {
                        val prevBottom = prevY + playerH
                        val currBottom = playerY + playerH
                        if (prevBottom <= groundY && currBottom >= groundY && vy >= 0f) {
                            playerY = groundY - playerH
                            vy = 0f
                            jumping = false
                            groundedLocal = true
                            airJumpsLeft = 2 // recargar al tocar suelo
                            landed = true
                        }
                    }
                    // Evaluar si seguimos con soporte (suelo u obstáculo) sin cruce, para no caer de golpe
                    val epsilon = h * 0.02f * zoom
                    var hasSupport = false
                    if (!landed) {
                        hadSupportPrev = groundedLocal
                        obstacles.value.forEach { o ->
                            val horiz = playerX < o.x + o.w && playerX + playerW > o.x
                            if (horiz) {
                                // Sólo considerar soporte cuando DESCENDEMOS; así no bloqueamos el ascenso del salto
                                val onTop = (vy >= 0f) && kotlin.math.abs((playerY + playerH) - o.y) <= epsilon
                                if (onTop) hasSupport = true
                                // Corrección de penetración sólo al DESCENDER
                                if (vy >= 0f && (playerY + playerH) > o.y && (playerY + playerH) < o.y + epsilon) {
                                    playerY = o.y - playerH
                                    vy = 0f
                                    groundedLocal = true
                                    jumping = false
                                    hasSupport = true
                                }
                            }
                        }
                        // Suelo como soporte (solo al DESCENDER)
                        if (vy >= 0f && kotlin.math.abs((playerY + playerH) - groundY) <= epsilon) {
                            hasSupport = true
                        }
                        // Transición de soporte → aire: activar coyote + fase de desprendimiento
                        if (hadSupportPrev && !hasSupport) {
                            coyoteTimer = 0.12f
                            edgeFallTimer = 0.12f
                        }
                        // Si recupera soporte, cancelar coyote
                        if (hasSupport) {
                            coyoteTimer = 0f
                            edgeFallTimer = 0f
                        }
                        groundedLocal = hasSupport
                        // Clamp de seguridad contra penetración del suelo
                        if (playerY > groundY - playerH + epsilon) {
                            playerY = groundY - playerH
                            vy = 0f
                            groundedLocal = true
                            jumping = false
                        }
                    }

                    // Obstáculos
                    spawnT -= dt
                    if (spawnT <= 0f) {
                        spawnT = spawnInterval
                        val high = kotlin.random.Random.nextFloat() < 0.42f
                        // Variar tamaño dentro de rangos por tipo
                        // Reducir el grosor horizontal de los obstáculos para landscape
                        // Altos: 0.06–0.10·w, Bajos: 0.07–0.12·w
                        val ow = (if (high) {
                            w * (0.06f + kotlin.random.Random.nextFloat() * (0.10f - 0.06f))
                        } else {
                            w * (0.07f + kotlin.random.Random.nextFloat() * (0.12f - 0.07f))
                        }) * zoom
                        val oh = (if (high) {
                            h * (0.13f + kotlin.random.Random.nextFloat() * (0.20f - 0.13f))
                        } else {
                            h * (0.07f + kotlin.random.Random.nextFloat() * (0.10f - 0.07f))
                        }) * zoom
                        val gapFactor = (worldSpeed / 600f).coerceIn(0.5f, 1.2f)
                        // Elevar los obstáculos altos para permitir pasar por debajo al deslizar
                        // Altura del hueco ≈ 0.06h, mayor que la altura del jugador en slide (0.05h)
                        val oy = if (high) groundY - oh - h * 0.06f * zoom else groundY - oh
                        val startX = w + ow + (w * 0.10f * gapFactor)
                        // Trampolín solo para obstáculos bajos y realmente pequeños
                        val bouncy = (!high) && (oh <= h * 0.085f * zoom)
                        obstacles.value = obstacles.value + Obstacle(startX, oy, ow, oh, high, bouncy)
                    }
                    val moved = obstacles.value.map { it.copy(x = it.x - worldSpeed * dt) }
                    obstacles.value = moved.filter { it.x + it.w > -20f }

                    // Enemigos: pasan de derecha a izquierda y disparan
                    enemySpawnT -= dt
                    if (enemySpawnT <= 0f) {
                        enemySpawnT = enemySpawnInterval
                        val ew = w * 0.10f * zoom
                        val eh = h * 0.10f * zoom
                        val ey = groundY - eh
                        val startX = w + ew + w * 0.05f
                        // Kotlin no define .random() para rangos de Float en algunas versiones.
                        // Usamos Random.nextFloat() para generar un valor en [1.1f, 1.8f].
                        val rate = 1.1f + kotlin.random.Random.nextFloat() * (1.8f - 1.1f)
                        enemies.value = enemies.value + Enemy(startX, ey, ew, eh, fireT = 1.2f, fireRate = rate)
                    }
                    val enemiesMoved = enemies.value.map { e -> e.copy(x = e.x - worldSpeed * 0.85f * dt, fireT = e.fireT - dt) }
                    // Disparos
                    val newBullets = mutableListOf<Bullet>()
                    enemiesMoved.forEach { e ->
                        if (e.fireT <= 0f) {
                            val bR = h * 0.015f * zoom
                            val bSpeed = worldSpeed + (h * 0.70f)
                            newBullets += Bullet(e.x, e.y + e.h * 0.5f, bR, bSpeed)
                        }
                    }
                    enemies.value = enemiesMoved.filter { it.x + it.w > -20f }.map { e -> if (e.fireT <= 0f) e.copy(fireT = e.fireRate) else e }
                    if (newBullets.isNotEmpty()) bullets.value = bullets.value + newBullets
                    val bulletsMoved = bullets.value.map { b -> b.copy(x = b.x - b.speed * dt) }
                    bullets.value = bulletsMoved.filter { it.x + it.r > -20f }

                    // Torques de techo (vigas giratorias de 180°)
                    torqueSpawnT -= dt
                    if (torqueSpawnT <= 0f) {
                        torqueSpawnT = torqueSpawnInterval
                        val pivotY = h * 0.14f
                        val length = h * 0.28f * zoom
                        val thickness = h * 0.018f * zoom
                        val startX = w + length + w * 0.06f
                        val angVel = 2.2f // rad/s
                        torques.value = torques.value + TorqueEnemy(
                            x = startX,
                            y = pivotY,
                            length = length,
                            thickness = thickness,
                            angle = (-Math.PI / 2.0).toFloat(),
                            angVel = angVel,
                            dir = +1
                        )
                    }
                    val torquesMoved = torques.value.map { t ->
                        var a = t.angle + t.angVel * dt * t.dir
                        var d = t.dir
                        val minA = (-Math.PI / 2.0).toFloat()
                        val maxA = (Math.PI / 2.0).toFloat()
                        if (a > maxA) { a = maxA; d = -1 }
                        if (a < minA) { a = minA; d = +1 }
                        t.copy(
                            x = t.x - worldSpeed * dt,
                            angle = a,
                            dir = d
                        )
                    }
                    torques.value = torquesMoved.filter { it.x + it.length > -20f }

                    // Colisión AABB
                    val px = playerX
                    val py = playerY
                    val pw = playerW
                    val ph = playerH
                    var collided = false
                    for (o in obstacles.value) {
                        if (px < o.x + o.w && px + pw > o.x && py < o.y + o.h && py + ph > o.y) {
                            // Excepción: cara superior del obstáculo (no letal)
                            val epsilonTop = h * 0.04f * zoom
                            val onTop = (py + ph) <= (o.y + epsilonTop) && vy >= 0f
                            if (!onTop) {
                                collided = true
                                break
                            }
                        }
                    }
                    // Colisión con torques (aprox por muestreo del segmento)
                    if (!collided) {
                        outer@ for (tq in torques.value) {
                            val pivot = Offset(tq.x, tq.y)
                            val end = Offset(
                                (tq.x + tq.length * kotlin.math.sin(tq.angle)),
                                (tq.y + tq.length * kotlin.math.cos(tq.angle))
                            )
                            val samples = 8
                            for (i in 0..samples) {
                                val s = i.toFloat() / samples.toFloat()
                                val sx = pivot.x + (end.x - pivot.x) * s
                                val sy = pivot.y + (end.y - pivot.y) * s
                                if (sx >= px && sx <= px + pw && sy >= py && sy <= py + ph) {
                                    collided = true
                                    break@outer
                                }
                            }
                        }
                    }
                    // Colisión con balas (círculo vs AABB aproximado)
                    if (!collided) {
                        for (b in bullets.value) {
                            val withinX = b.x > (px - b.r) && b.x < (px + pw + b.r)
                            val withinY = b.y > (py - b.r) && b.y < (py + ph + b.r)
                            if (withinX && withinY) { collided = true; break }
                        }
                    }
                    if (collided) {
                        running = false
                        bestScore = maxOf(bestScore, score)
                    } else {
                        score += (worldSpeed * dt) / 10f
                    }
                }
                kotlinx.coroutines.delay(16)
            }
        }

        // Mueve los gestos al Canvas para no interferir con los botones superpuestos
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(modifier = gestureMod.matchParentSize()) {
                // Fondo personalizado si existe en assets
                if (gameBg != null) {
                    // Escalar la imagen para que ocupe el alto del canvas y desplazarla en X conforme avanzas
                    val scale = size.height / gameBg.height.toFloat()
                    val scaledW = (gameBg.width * scale).toInt()
                    val scaledH = size.height.toInt()
                    val tb = score / 50f
                    val bgX = -((tb * 12f) % scaledW)
                    val dstSize = IntSize(scaledW, scaledH)
                    // Dibujar dos copias consecutivas para efecto de “tile” infinito
                    drawImage(gameBg, IntOffset(0, 0), IntSize(gameBg.width, gameBg.height), IntOffset(bgX.toInt(), 0), dstSize)
                    drawImage(gameBg, IntOffset(0, 0), IntSize(gameBg.width, gameBg.height), IntOffset(bgX.toInt() + scaledW, 0), dstSize)
                } else {
                    // Color de fondo por defecto
                    drawRect(bgColor, topLeft = Offset(0f, 0f), size = Size(size.width, size.height))
                }
                // Parallax ajustado para que el fondo se vea más (capas con menos opacidad y más abajo)
                val t = score / 50f
                val farX = -((t * 20f) % size.width)
                val midX = -((t * 40f) % size.width)
                val nearX = -((t * 80f) % size.width)
                val farColor = layerFar.copy(alpha = 0.20f)
                val midColor = layerMid.copy(alpha = 0.15f)
                val nearColor = layerNear.copy(alpha = 0.10f)
                // Dejar libre la parte superior (~30% para que se vea el cielo del bg)
                val farTop = size.height * 0.30f
                val midTop = size.height * 0.40f
                val nearTop = size.height * 0.50f
                drawRect(farColor, topLeft = Offset(farX, farTop), size = Size(size.width, size.height - farTop))
                drawRect(farColor, topLeft = Offset(farX + size.width, farTop), size = Size(size.width, size.height - farTop))
                drawRect(midColor, topLeft = Offset(midX, midTop), size = Size(size.width, size.height - midTop))
                drawRect(midColor, topLeft = Offset(midX + size.width, midTop), size = Size(size.width, size.height - midTop))
                drawRect(nearColor, topLeft = Offset(nearX, nearTop), size = Size(size.width, size.height - nearTop))
                drawRect(nearColor, topLeft = Offset(nearX + size.width, nearTop), size = Size(size.width, size.height - nearTop))

                // Suelo
                drawRect(Color(0xFF0D1323), topLeft = Offset(0f, groundY), size = Size(size.width, size.height - groundY))

                // Obstáculos
            obstacles.value.forEach { o ->
                if (o.bouncy && medusaSpr != null) {
                    // Dibujar medusa recortada y escalada para calzar dentro del rectángulo del obstáculo (contain + centrado)
                    val srcW = medusaSpr.srcSize.width.toFloat()
                    val srcH = medusaSpr.srcSize.height.toFloat()
                    val scale = kotlin.math.min(o.w / srcW, o.h / srcH)
                    val drawW = (srcW * scale).toInt()
                    val drawH = (srcH * scale).toInt()
                    val drawX = (o.x + (o.w - drawW) * 0.5f).toInt()
                    val drawY = (o.y + (o.h - drawH) * 0.5f).toInt()
                    drawImage(
                        medusaSpr.img,
                        medusaSpr.srcOffset,
                        medusaSpr.srcSize,
                        IntOffset(drawX, drawY),
                        IntSize(drawW, drawH)
                    )
                } else {
                    val color = when {
                        o.high -> obstacleHighColor
                        o.bouncy -> obstacleBouncyColor
                        else -> obstacleLowColor
                    }
                    drawRect(color, topLeft = Offset(o.x, o.y), size = Size(o.w, o.h))
                }
            }

                // Enemigos
                enemies.value.forEach { e ->
                    if (marcianSpr != null) {
                        // Dibujar marciano recortado dentro del rectángulo del enemigo (contain + centrado)
                        val srcW = marcianSpr.srcSize.width.toFloat()
                        val srcH = marcianSpr.srcSize.height.toFloat()
                        val scale = kotlin.math.min(e.w / srcW, e.h / srcH)
                        val drawW = (srcW * scale).toInt()
                        val drawH = (srcH * scale).toInt()
                        val drawX = (e.x + (e.w - drawW) * 0.5f).toInt()
                        val drawY = (e.y + (e.h - drawH) * 0.5f).toInt()
                        drawImage(
                            marcianSpr.img,
                            marcianSpr.srcOffset,
                            marcianSpr.srcSize,
                            IntOffset(drawX, drawY),
                            IntSize(drawW, drawH)
                        )
                    } else {
                        drawRect(enemyColor, topLeft = Offset(e.x, e.y), size = Size(e.w, e.h))
                    }
                }
                // Torques de techo
                torques.value.forEach { t ->
                    // soporte en el techo
                    drawRect(enemyColor.copy(alpha = 0.8f), topLeft = Offset(t.x - t.thickness * 0.6f, t.y - t.thickness * 0.8f), size = Size(t.thickness * 1.2f, t.thickness * 0.8f))
                    // viga rotatoria
                    val endX = (t.x + t.length * kotlin.math.sin(t.angle))
                    val endY = (t.y + t.length * kotlin.math.cos(t.angle))
                    drawLine(enemyColor, start = Offset(t.x, t.y), end = Offset(endX, endY), strokeWidth = t.thickness)
                }
                // Balas
                bullets.value.forEach { b ->
                    drawCircle(bulletColor, radius = b.r, center = Offset(b.x, b.y))
                }

                // Jugador: seleccionar sprite por estado y dibujar preservando aspecto, alineado por los pies
                val spr = when {
                    !running -> userIdle
                    sliding -> userCrouch
                    jumping -> userJetpack
                    else -> userRun
                }
                if (spr != null) {
                    // Escalar en base al rectángulo recortado (contenido real), centrado y pegado al suelo
                    val srcW = spr.srcSize.width.toFloat()
                    val srcH = spr.srcSize.height.toFloat()
                    val sx = playerW / srcW
                    val sy = playerH / srcH
                    val scale = kotlin.math.min(sx, sy)
                    val drawW = (srcW * scale).toInt()
                    val drawH = (srcH * scale).toInt()
                    val drawX = (playerX + (playerW - drawW) * 0.5f).toInt()
                    val drawY = (playerY + playerH - drawH).toInt()
                    drawImage(
                        spr.img,
                        spr.srcOffset,
                        spr.srcSize,
                        IntOffset(drawX, drawY),
                        IntSize(drawW, drawH)
                    )
                } else {
                    drawRoundRect(
                        playerColor,
                        topLeft = Offset(playerX, playerY),
                        size = Size(playerW, playerH),
                        cornerRadius = CornerRadius(12f, 12f)
                    )
                }
            }

            // HUD
            Column(Modifier.fillMaxSize().padding(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Distancia: ${score.toInt()}", color = hudColor)
                    Text(text = "Mejor: ${bestScore.toInt()}", color = hudColor)
                }
            }

            // Controles táctiles en pantalla: Saltar y Deslizar (persisten visibles)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Controles de zoom para escalar todo el juego
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { zoom = (zoom + 0.1f).coerceAtMost(1.8f) }) { Text("Zoom +") }
                    Button(onClick = { zoom = (zoom - 0.1f).coerceAtLeast(1.0f) }) { Text("Zoom -") }
                }
                Button(
                    onClick = {
                        if (running) {
                            // Registrar solicitud de salto; se procesará en el bucle físico
                            jumpRequested = true
                        }
                    },
                    enabled = true
                ) {
                    Text("Saltar")
                }
                // Botón Deslizar: mantener agachado mientras se mantenga presionado
                val slideInteraction = remember { MutableInteractionSource() }
                val slidePressed by slideInteraction.collectIsPressedAsState()
                LaunchedEffect(slidePressed, running, jumping) {
                    slideHeld = slidePressed && running && !jumping
                }
                Button(
                    onClick = {
                        // Tap rápido: iniciar un desliz corto
                        if (running && !jumping) {
                            sliding = true
                            slideTimer = 0.45f
                        }
                    },
                    enabled = true,
                    interactionSource = slideInteraction
                ) {
                    Text("Deslizar")
                }
            }

            // Overlay Game Over
            AnimatedVisibility(visible = !running) {
                Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Game Over", color = Color.White)
                    Spacer(Modifier.height(8.dp))
                    Text("Puntuación: ${score.toInt()}", color = Color.White)
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = {
                            // Reiniciar partido al estado exacto del inicio
                            score = 0f
                            worldSpeed = 320f
                            obstacles.value = emptyList()
                            enemies.value = emptyList()
                            bullets.value = emptyList()
                            // Limpiar y reprogramar enemigos de techo (torques)
                            torques.value = emptyList()
                            enemySpawnT = 4.5f
                            enemySpawnInterval = 5.5f
                            torqueSpawnT = 7.0f
                            torqueSpawnInterval = 9.0f
                            // Recalcular posición y dimensiones iniciales del jugador y suelo
                            val px0 = w * 0.22f
                            val ph0 = h * 0.08f * zoom
                            val pw0 = ph0 * 0.8f
                            val gy0 = h * 0.85f
                            playerW = pw0
                            playerH = ph0
                            groundY = gy0
                            playerX = px0
                            playerY = groundY - playerH
                            // Estado físico
                            vy = 0f
                            jumping = false
                            jumpRequested = false
                            airJumpsLeft = 2
                            sliding = false
                            slideTimer = 0f
                            // Reanudar juego
                            running = true
                        }) { Text("Reintentar") }
                        Button(onClick = { onExitToMenu() }) { Text("Menú") }
                    }
                }
            }
        }
    }
}

@Suppress("unused")
@Composable
fun GalaxyMapScreen(worlds: List<World>, onSelect: (World) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Mapa Galáctico")
        Spacer(Modifier.height(12.dp))
        worlds.forEach { w ->
            var pressed by remember { mutableStateOf(false) }
            val bg by animateColorAsState(if (pressed) w.themeColor else w.themeColor.copy(alpha = 0.6f), animationSpec = tween(300), label = "worldBg")
            val size by animateDpAsState(if (pressed) 120.dp else 100.dp, animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f), label = "worldSize")
            Box(
                modifier = Modifier
                    .size(size)
                    .background(bg, RoundedCornerShape(16.dp))
            ) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(w.name, color = Color.White)
                    Spacer(Modifier.height(6.dp))
                    Button(onClick = { pressed = !pressed; onSelect(w) }) { Text("Viajar") }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Suppress("unused")
@Composable
fun WorldSceneScreen(world: World, energy: Int, onEnergyChange: (Int) -> Unit, onBackToMap: () -> Unit) {
    var showHud by remember { mutableStateOf(true) }
    var dialogState by remember { mutableStateOf(UiState.Loading) }
    var heroToggle by remember { mutableStateOf(false) }
    var exiting by remember { mutableStateOf(false) }
    var points by remember { mutableIntStateOf(0) }
    var missions by remember {
        mutableStateOf(
            listOf(
                Mission(1, "Recolectar cristal", "Obtener 3 cristales", MissionState.Pending),
                Mission(2, "Activar antena", "Restablecer comunicación", MissionState.InProgress),
                Mission(3, "Defender campamento", "Repeler incursión", MissionState.Failed)
            )
        )
    }
    val bg by animateColorAsState(world.themeColor, animationSpec = tween(600), label = "worldBg")
    val energyState = rememberUpdatedState(energy)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopDownRPGScene(energy = energy)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    showHud = false
                    dialogState = UiState.Loading
                    exiting = true
                }) { Text("Mapa") }
            Button(onClick = { showHud = !showHud }) { Text(if (showHud) "Ocultar HUD" else "Mostrar HUD") }
            Button(onClick = { dialogState = when (dialogState) { UiState.Loading -> UiState.Content; UiState.Content -> UiState.Error; UiState.Error -> UiState.Choices; UiState.Choices -> UiState.Loading } }) { Text("Diálogo") }
        }
        Spacer(Modifier.height(16.dp))
        val moveDuration = (900 - (energy * 6)).coerceIn(300, 900)
        val sizeStiffness = (200f + energy * 4f).coerceIn(200f, 600f)
        val heroSize by animateDpAsState(if (heroToggle) 72.dp else 48.dp, animationSpec = spring(dampingRatio = 0.7f, stiffness = sizeStiffness), label = "heroSize")
        val heroX by animateDpAsState(if (heroToggle) 80.dp else 0.dp, animationSpec = tween(moveDuration), label = "heroX")
        val heroY by animateDpAsState(if (heroToggle) 40.dp else 0.dp, animationSpec = tween(moveDuration), label = "heroY")
        Box(
            modifier = Modifier
                .size(heroSize)
                .offset(heroX, heroY)
                .background(Color(0xFFFFEB3B), CircleShape)
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = { heroToggle = !heroToggle }) { Text(if (heroToggle) "Reiniciar" else "Mover/Expandir") }
        Spacer(Modifier.height(16.dp))
        AnimatedVisibility(visible = showHud, enter = fadeIn(animationSpec = tween(300)) + expandIn(), exit = fadeOut(animationSpec = tween(250)) + shrinkOut()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Misiones")
                Text("Puntos: $points")
                val energyProgress by animateFloatAsState(targetValue = energy / 100f, animationSpec = tween(400), label = "energy")
                val missionProgressTarget = if (missions.isEmpty()) 0f else missions.count { it.state == MissionState.Completed }.toFloat() / missions.size.toFloat()
                val missionProgress by animateFloatAsState(targetValue = missionProgressTarget, animationSpec = tween(400), label = "missionProgress")
                Text("Energía: $energy")
                LinearProgressIndicator(progress = { energyProgress }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(6.dp))
                Text("Progreso misiones")
                LinearProgressIndicator(progress = { missionProgress }, modifier = Modifier.fillMaxWidth())
                val hudMoveDuration = (900 - (energy * 6)).coerceIn(300, 900)
                val speedPercent = ((900 - hudMoveDuration) / 6)
                Text("Velocidad héroe: $speedPercent%")
                MissionBoard(missions) { idx ->
                    val list = missions.toMutableList()
                    val prev = list[idx]
                    val next = advanceMission(prev)
                    list[idx] = next
                    missions = list
                    if (prev.state != MissionState.Completed && next.state == MissionState.Completed) {
                        points += 10
                        onEnergyChange((energy + 3).coerceIn(0, 100))
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        AnimatedContent(
            targetState = dialogState,
            transitionSpec = { fadeIn(animationSpec = tween(350)) togetherWith fadeOut(animationSpec = tween(250)) },
            label = "dialogContent"
        ) { s ->
            when (s) {
                UiState.Loading -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text("Cargando diálogo")
                }
                UiState.Content -> Text("NPC: Bienvenido a ${world.name}")
                UiState.Error -> Text("Sin señal en este sector")
                UiState.Choices -> Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {
                        val nextId = (missions.maxOfOrNull { it.id } ?: 0) + 1
                        missions = missions + Mission(nextId, "Explorar zona", "Investiga anomalía", MissionState.Pending)
                        onEnergyChange((energy - 5).coerceIn(0, 100))
                        dialogState = UiState.Content
                    }) { Text("Explorar") }
                    Button(onClick = {
                        val list = missions.toMutableList()
                        val i = list.indexOfFirst { it.state == MissionState.InProgress || it.state == MissionState.Pending }
                        if (i >= 0) list[i] = list[i].copy(state = MissionState.Completed)
                        missions = list
                        points += 10
                        onEnergyChange((energy + 5).coerceIn(0, 100))
                        dialogState = UiState.Content
                    }) { Text("Comerciar") }
                    Button(onClick = { dialogState = UiState.Loading }) { Text("Salir") }
                }
            }
        }
        }
        LaunchedEffect(showHud) {
            if (showHud) {
                while (showHud) {
                    delay(2000)
                    onEnergyChange((energyState.value + 1).coerceIn(0, 100))
                }
            }
        }
        LaunchedEffect(exiting) {
            if (exiting) {
                delay(600)
                onBackToMap()
            }
        }
        AnimatedVisibility(
            visible = exiting,
            enter = fadeIn(animationSpec = tween(200)),
            exit = fadeOut(animationSpec = tween(200))
        ) {
            val exitProgress by animateFloatAsState(targetValue = if (exiting) 1f else 0f, animationSpec = tween(600), label = "exitProgress")
            val exitRing by animateDpAsState(targetValue = if (exiting) 220.dp else 0.dp, animationSpec = tween(600), label = "exitRing")
            Box(Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val c = Offset(size.width / 2f, size.height / 2f)
                    val angles = listOf(0f, 60f, 120f, 180f, 240f, 300f)
                    val len = size.minDimension / 4f
                    angles.forEach { deg ->
                        val rad = Math.toRadians(deg.toDouble())
                        val dx = (cos(rad) * len * exitProgress).toFloat()
                        val dy = (sin(rad) * len * exitProgress).toFloat()
                        drawLine(Color.White.copy(alpha = 0.8f), c, Offset(c.x + dx, c.y + dy), strokeWidth = 3f)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(exitRing)
                        .align(Alignment.Center)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                )
            }
        }
    }
}

@Composable
fun TopDownRPGScene(energy: Int) {
    val gridW = 12
    val gridH = 8
    val map = remember {
        listOf(
            "111111111111",
            "100000001001",
            "101111001001",
            "100001001001",
            "100001001001",
            "100001001001",
            "100000000001",
            "111111111111"
        )
    }
    val tileset = rememberAssetImage("tileset.png")
    val heroDown = rememberAssetImage("hero_down.png")
    val heroLeft = rememberAssetImage("hero_left.png")
    val heroRight = rememberAssetImage("hero_right.png")
    val heroUp = rememberAssetImage("hero_up.png")
    val alienImg = rememberAssetImage("alien_idle.png")
    var heroX by remember { mutableFloatStateOf(1.5f) }
    var heroY by remember { mutableFloatStateOf(1.5f) }
    var up by remember { mutableStateOf(false) }
    var down by remember { mutableStateOf(false) }
    var left by remember { mutableStateOf(false) }
    var right by remember { mutableStateOf(false) }
    var facing by remember { mutableStateOf(0) }
    val speed = 2.2f + energy * 0.02f
    LaunchedEffect(Unit) {
        var last = System.nanoTime()
        while (true) {
            val now = System.nanoTime()
            val dt = (now - last) / 1_000_000_000.0
            last = now
            var vx = 0f
            var vy = 0f
            if (up) vy -= 1f
            if (down) vy += 1f
            if (left) vx -= 1f
            if (right) vx += 1f
            val len = kotlin.math.sqrt((vx * vx + vy * vy).toDouble()).toFloat()
            if (len > 0f) {
                vx /= len
                vy /= len
                val nx = heroX + vx * speed * dt.toFloat()
                val ny = heroY + vy * speed * dt.toFloat()
                val r = 0.3f
                val txY = kotlin.math.floor(heroY).toInt()
                val tyX = kotlin.math.floor(heroX).toInt()
                fun isWall(x: Int, y: Int): Boolean = map[y][x] == '1'
                val tryX = !isWall(kotlin.math.floor(nx + r).toInt(), txY) && !isWall(kotlin.math.floor(nx - r).toInt(), txY)
                val tryY = !isWall(tyX, kotlin.math.floor(ny + r).toInt()) && !isWall(tyX, kotlin.math.floor(ny - r).toInt())
                if (tryX) heroX = nx
                if (tryY) heroY = ny
                facing = when {
                    vy < 0f -> 3
                    vy > 0f -> 0
                    vx < 0f -> 1
                    else -> 2
                }
            }
            delay(16)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .background(Color.Black)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val tileW = size.width / gridW
            val tileH = size.height / gridH
            for (y in 0 until gridH) {
                for (x in 0 until gridW) {
                    val c = map[y][x]
                    if (tileset != null) {
                        val cols = 2
                        val frameW = tileset.width / cols
                        val frameH = tileset.height
                        val srcOffset = if (c == '1') IntOffset(frameW, 0) else IntOffset(0, 0)
                        val srcSize = IntSize(frameW, frameH)
                        val dstOffset = IntOffset((x * tileW).toInt(), (y * tileH).toInt())
                        val dstSize = IntSize(tileW.toInt(), tileH.toInt())
                        drawImage(tileset, srcOffset, srcSize, dstOffset, dstSize)
                    } else {
                        val color = if (c == '1') Color(0xFF0D47A1) else Color(0xFF1B5E20)
                        drawRect(color, topLeft = Offset(x * tileW, y * tileH), size = Size(tileW, tileH))
                    }
                }
            }
            val hx = heroX * tileW
            val hy = heroY * tileH
            val heroSelected = when (facing % 4) {
                0 -> heroDown
                1 -> heroLeft
                2 -> heroRight
                else -> heroUp
            }
            if (heroSelected != null) {
                val halfW = tileW * 0.5f
                val halfH = tileH * 0.5f
                val dstOffset = IntOffset((hx - halfW).toInt(), (hy - halfH).toInt())
                val dstSize = IntSize((tileW).toInt(), (tileH).toInt())
                drawImage(
                    heroSelected,
                    IntOffset(0, 0),
                    IntSize(heroSelected.width, heroSelected.height),
                    dstOffset,
                    dstSize
                )
            } else {
                drawCircle(Color.Yellow, radius = kotlin.math.min(tileW, tileH) * 0.35f, center = Offset(hx, hy))
            }
            if (alienImg != null) {
                val ax = 9 * tileW + tileW * 0.5f
                val ay = 5 * tileH + tileH * 0.5f
                val fW = alienImg.width
                val fH = alienImg.height
                val dstOffset = IntOffset((ax - tileW * 0.5f).toInt(), (ay - tileH * 0.5f).toInt())
                val dstSize = IntSize(tileW.toInt(), tileH.toInt())
                drawImage(alienImg, IntOffset(0, 0), IntSize(fW, fH), dstOffset, dstSize)
            } else {
                drawCircle(Color.Magenta, radius = kotlin.math.min(tileW, tileH) * 0.3f, center = Offset(9.5f * tileW, 5.5f * tileH))
            }
        }
        // Controles táctiles anteriores eliminados: el juego se diseñará para pantalla horizontal con
        // navegación desde menú principal. Los controles se añadirán más adelante según el nuevo diseño.
    }
}

@Composable
fun rememberAssetImage(name: String): ImageBitmap? {
    val ctx = LocalContext.current
    return remember(name) {
        runCatching {
            ctx.assets.open(name).use { BitmapFactory.decodeStream(it).asImageBitmap() }
        }.getOrNull()
    }
}

// Sprite recortado: imagen + región útil sin transparencia alrededor
private data class Sprite(val img: ImageBitmap, val srcOffset: IntOffset, val srcSize: IntSize)

// Calcula el rectángulo mínimo que contiene píxeles con alpha > umbral.
// Se realiza una sola vez por imagen y se memoriza.
@Composable
private fun rememberTrimmedSprite(name: String, alphaThreshold: Float = 0.02f): Sprite? {
    val img = rememberAssetImage(name) ?: return null
    return remember(name) {
        runCatching {
            val pm = img.toPixelMap()
            val w = pm.width
            val h = pm.height
            var minX = w
            var minY = h
            var maxX = -1
            var maxY = -1
            // Barrido para localizar contenido no transparente
            for (y in 0 until h) {
                for (x in 0 until w) {
                    val a = pm[x, y].alpha
                    if (a > alphaThreshold) {
                        if (x < minX) minX = x
                        if (y < minY) minY = y
                        if (x > maxX) maxX = x
                        if (y > maxY) maxY = y
                    }
                }
            }
            // Si toda la imagen es transparente, usar todo para evitar errores
            if (maxX < 0 || maxY < 0) {
                Sprite(img, IntOffset(0, 0), IntSize(w, h))
            } else {
                val srcW = (maxX - minX + 1).coerceAtLeast(1)
                val srcH = (maxY - minY + 1).coerceAtLeast(1)
                Sprite(img, IntOffset(minX, minY), IntSize(srcW, srcH))
            }
        }.getOrElse {
            // En caso de error, no recortar
            Sprite(img, IntOffset(0, 0), IntSize(img.width, img.height))
        }
    }
}

@Composable
fun MissionBoard(missions: List<Mission>, onAdvance: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        missions.forEachIndexed { index, m ->
            MissionItem(m) { onAdvance(index) }
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun advanceMission(m: Mission): Mission {
    val next = when (m.state) {
        MissionState.Pending -> MissionState.InProgress
        MissionState.InProgress -> MissionState.Completed
        MissionState.Completed -> MissionState.Completed
        MissionState.Failed -> MissionState.Pending
    }
    return m.copy(state = next)
}

@Composable
fun MissionItem(mission: Mission, onAdvance: () -> Unit) {
    AnimatedContent(
        targetState = mission.state,
        transitionSpec = { fadeIn(animationSpec = tween(250)) togetherWith fadeOut(animationSpec = tween(200)) },
        label = "missionState"
    ) { s ->
        val color = when (s) {
            MissionState.Pending -> Color(0xFF455A64)
            MissionState.InProgress -> Color(0xFF0277BD)
            MissionState.Completed -> Color(0xFF2E7D32)
            MissionState.Failed -> Color(0xFFC62828)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(color, RoundedCornerShape(12.dp))
        ) {
            Row(Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(mission.title, color = Color.White)
                    Text(mission.description, color = Color.White.copy(alpha = 0.8f))
                }
                Button(onClick = onAdvance) { Text("Avanzar") }
            }
        }
    }
}

@Suppress("unused")
@Composable
fun TravelTransition(world: World, energy: Int, onEnergyChange: (Int) -> Unit, onArrive: () -> Unit) {
    var start by remember { mutableStateOf(false) }
    val travelDuration = (1200 - (energy * 6)).coerceIn(400, 1200)
    val ringTarget = (240 + energy).dp
    val progress by animateFloatAsState(targetValue = if (start) 1f else 0f, animationSpec = tween(durationMillis = travelDuration), label = "progress")
    val ring by animateDpAsState(targetValue = if (start) ringTarget else 0.dp, animationSpec = tween(durationMillis = travelDuration), label = "ring")
    val fadeBg by animateColorAsState(targetValue = world.themeColor.copy(alpha = 0.9f), animationSpec = tween(800), label = "fadeBg")
    LaunchedEffect(Unit) {
        onEnergyChange((energy - 10).coerceIn(0, 100))
        start = true
        delay(travelDuration.toLong())
        onEnergyChange(((energy - 10) + 5).coerceIn(0, 100))
        onArrive()
    }
    Box(modifier = Modifier.fillMaxSize().background(fadeBg)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val angles = listOf(0f, 30f, 60f, 90f, 120f, 150f, 180f, 210f, 240f, 270f, 300f, 330f)
            val farLen = size.minDimension / 3f
            val nearLen = size.minDimension / 2.2f
            angles.forEach { deg ->
                val rad = Math.toRadians(deg.toDouble())
                val farDx = (cos(rad) * farLen * progress).toFloat()
                val farDy = (sin(rad) * farLen * progress).toFloat()
                drawLine(Color.White.copy(alpha = 0.6f), c, Offset(c.x + farDx, c.y + farDy), strokeWidth = 2f)
                val nearDx = (cos(rad) * nearLen * (progress * 1.2f).coerceAtMost(1f)).toFloat()
                val nearDy = (sin(rad) * nearLen * (progress * 1.2f).coerceAtMost(1f)).toFloat()
                drawLine(Color.White.copy(alpha = 0.9f), c, Offset(c.x + nearDx, c.y + nearDy), strokeWidth = 4f)
                val dotDx = (cos(rad) * farLen * (progress * 0.7f)).toFloat()
                val dotDy = (sin(rad) * farLen * (progress * 0.7f)).toFloat()
                drawCircle(Color.White.copy(alpha = 0.8f), radius = 3f, center = Offset(c.x + dotDx, c.y + dotDy))
            }
        }
        Box(
            modifier = Modifier
                .size(ring)
                .align(Alignment.Center)
                .background(Color.White.copy(alpha = 0.85f), CircleShape)
        )
        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Viajando a ${world.name}")
        }
    }
}

@Suppress("unused")
@Composable
fun TravelBackTransition(world: World, energy: Int, onEnergyChange: (Int) -> Unit, onArrive: () -> Unit) {
    var start by remember { mutableStateOf(false) }
    val travelDuration = (1200 - (energy * 6)).coerceIn(400, 1200)
    val ringTarget = (220 + energy).dp
    val progress by animateFloatAsState(targetValue = if (start) 1f else 0f, animationSpec = tween(durationMillis = travelDuration), label = "progressBack")
    val progressNear by animateFloatAsState(targetValue = if (start) 1f else 0f, animationSpec = tween(durationMillis = (travelDuration * 1.2f).toInt()), label = "progressNearBack")
    val ring by animateDpAsState(targetValue = if (start) ringTarget else 0.dp, animationSpec = tween(durationMillis = travelDuration), label = "ringBack")
    val fadeBg by animateColorAsState(targetValue = Color.Black.copy(alpha = 0.85f), animationSpec = tween(800), label = "fadeBgBack")
    LaunchedEffect(Unit) {
        onEnergyChange((energy - 6).coerceIn(0, 100))
        start = true
        delay(travelDuration.toLong())
        onEnergyChange(((energy - 6) + 4).coerceIn(0, 100))
        onArrive()
    }
    Box(modifier = Modifier.fillMaxSize().background(fadeBg)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val c = Offset(size.width / 2f, size.height / 2f)
            val angles = listOf(15f, 45f, 75f, 105f, 135f, 165f, 195f, 225f, 255f, 285f, 315f, 345f)
            val farLen = size.minDimension / 3.5f
            val nearLen = size.minDimension / 2.6f
            angles.forEach { deg ->
                val rad = Math.toRadians(deg.toDouble())
                val farDx = (cos(rad) * farLen * progress).toFloat()
                val farDy = (sin(rad) * farLen * progress).toFloat()
                drawLine(Color.White.copy(alpha = 0.5f), c, Offset(c.x - farDx, c.y - farDy), strokeWidth = 2f)
                val nearDx = (cos(rad) * nearLen * (progressNear * 1.1f).coerceAtMost(1f)).toFloat()
                val nearDy = (sin(rad) * nearLen * (progressNear * 1.1f).coerceAtMost(1f)).toFloat()
                drawLine(Color.White.copy(alpha = 0.85f), c, Offset(c.x - nearDx, c.y - nearDy), strokeWidth = 4f)
                val dotDx = (cos(rad) * farLen * (progress * 0.7f)).toFloat()
                val dotDy = (sin(rad) * farLen * (progress * 0.7f)).toFloat()
                drawCircle(Color.White.copy(alpha = 0.75f), radius = 3f, center = Offset(c.x - dotDx, c.y - dotDy))
            }
            val swirlAngles = listOf(0f, 120f, 240f)
            val swirlLen = size.minDimension / 5f
            swirlAngles.forEach { deg ->
                val rad = Math.toRadians(deg.toDouble())
                val dx = (cos(rad) * swirlLen * progressNear).toFloat()
                val dy = (sin(rad) * swirlLen * progressNear).toFloat()
                drawCircle(Color.White.copy(alpha = 0.4f), radius = 2f + 6f * progressNear, center = Offset(c.x - dx, c.y - dy))
            }
        }
        Box(
            modifier = Modifier
                .size(ring)
                .align(Alignment.Center)
                .background(Color.White.copy(alpha = 0.8f), CircleShape)
        )
        Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Regresando")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedVisibilityPreview() {
    Lab13AnimacionesVideojuegoTheme {
        AnimatedVisibilityDemo()
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedColorBoxPreview() {
    Lab13AnimacionesVideojuegoTheme {
        AnimatedColorBoxDemo()
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedSizePositionPreview() {
    Lab13AnimacionesVideojuegoTheme {
        AnimatedSizePositionDemo()
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatedContentPreview() {
    Lab13AnimacionesVideojuegoTheme {
        AnimatedContentDemo()
    }
}

@Preview(showBackground = true)
@Composable
fun GameAppPreview() {
    Lab13AnimacionesVideojuegoTheme {
        GameApp()
    }
}