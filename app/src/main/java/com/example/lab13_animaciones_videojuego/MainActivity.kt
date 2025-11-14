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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.example.lab13_animaciones_videojuego.ui.theme.Lab13AnimacionesVideojuegoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab13AnimacionesVideojuegoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimatedContentDemo(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

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
    val size by animateDpAsState(targetValue = sizeTarget, animationSpec = tween<Dp>(durationMillis = 500), label = "size")
    val x by animateDpAsState(targetValue = xTarget, animationSpec = tween<Dp>(durationMillis = 500), label = "x")
    val y by animateDpAsState(targetValue = yTarget, animationSpec = tween<Dp>(durationMillis = 500), label = "y")
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

enum class UiState { Loading, Content, Error }

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
                UiState.Error -> UiState.Loading
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
            }
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