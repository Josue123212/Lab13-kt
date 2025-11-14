package com.example.lab13_animaciones_videojuego

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
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
import com.example.lab13_animaciones_videojuego.ui.theme.Lab13AnimacionesVideojuegoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab13AnimacionesVideojuegoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimatedColorBoxDemo(modifier = Modifier.padding(innerPadding))
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
    val spec = if (useSpring) spring<Color>(dampingRatio = 0.6f, stiffness = 300f) else tween<Color>(durationMillis = 600)
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