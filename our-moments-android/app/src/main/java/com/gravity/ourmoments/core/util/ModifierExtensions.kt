package com.gravity.ourmoments.core.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.random.Random

fun Modifier.randomRotation(seed: Long = 0): Modifier {
    val rotation = Random(seed).nextFloat() * 10 - 5
    return this.graphicsLayer {
        rotationZ = rotation
    }
}