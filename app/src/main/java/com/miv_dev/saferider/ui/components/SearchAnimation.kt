package com.miv_dev.saferider.ui.components

import androidx.compose.animation.Animatable
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun ScanningAnimation(isDarkTheme: Boolean = isSystemInDarkTheme(),  isScanning: Boolean = false, onClick: () -> Unit) {
    val theme = MaterialTheme.colorScheme

    val color = remember { Animatable(Color.Transparent) }
    var currentRotation by remember { mutableStateOf(0f) }

    val rotation = remember { Animatable(currentRotation) }

    LaunchedEffect(isScanning) {
        if (isScanning) {
            launch {
                val green = if(isDarkTheme){
                    Color.Green
                }else{
                    Color.Green.copy(green = 0.7f)
                }

                color.animateTo(green, tween(1250, easing = LinearEasing))
            }
            launch {
                rotation.animateTo(
                    targetValue = currentRotation + 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(3000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                ) {
                    currentRotation = value
                }
            }
        } else {
            launch {
                color.animateTo(
                    Color.Black.copy(0.6f), tween(
                        1250, easing = LinearOutSlowInEasing
                    )
                )
            }
            if (currentRotation > 0f) {
                rotation.animateTo(
                    targetValue = currentRotation + 50,
                    animationSpec = tween(
                        durationMillis = 1250,
                        easing = LinearOutSlowInEasing
                    )
                ) {
                    currentRotation = value
                }
            }
        }
    }

    Box(Modifier.size(200.dp).noRippleClickable{
        onClick()
    }) {
        AnimatedContent(rotation) {
            Canvas(Modifier.fillMaxSize().align(Alignment.Center).rotate(it.value)) {
                drawCircle(
                    theme.surfaceColorAtElevation(4.dp),
                    radius = size.minDimension / 1.8f
                )
                drawCircle(
                    theme.background,
                    radius = size.minDimension / 2.2f
                )

                val amount = 18
                val degreeStep = 360 / amount
                for (i in 0 until amount) {
                    val degree = degreeStep * i
                    val radian = degree * 0.0174533

                    val radius = size.minDimension / 2

                    val x = (cos(radian) * 100.0).roundToInt() / 100.0
                    val y = (sin(radian) * 100.0).roundToInt() / 100.0
                    drawCircle(
                        color.value,
                        radius = 10f,
                        center = this.center + Offset((x * radius).toFloat(), (y * radius).toFloat())
                    )
                }

            }
        }

        Box(
            Modifier
                .background(theme.primary, CircleShape)
                .padding(10.dp)
                .align(Alignment.Center)

        ) {
            Icon(Icons.Rounded.Bluetooth, contentDescription = "Bluetooth Scan", tint = Color.White)
        }

    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    clickable(indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@Preview
@Composable
fun SearchAnimationPreview(){
    ScanningAnimation(false, true){

    }
}
