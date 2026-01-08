package com.gravity.ourmoments.presentation.component.decorative

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import com.gravity.ourmoments.core.util.HandDrawnUtil
import com.gravity.ourmoments.ui.theme.InkLight
import kotlin.random.Random

@Composable
fun HandBorder(
    modifier: Modifier = Modifier,
    color: Color = InkLight,
    strokeWidth: Float = 2f,
    roughness: Float = 5f,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier) {
        content()
        Canvas(
            modifier = Modifier.matchParentSize()
        ) {
            val density = density
            val path = HandDrawnUtil.generateHandDrawnPath(
                size = size,
                roughness = roughness,
                strokeWidth = strokeWidth
            )
            drawPath(
                path = path,
                color = color.copy(alpha = 0.3f),
                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.cornerPathEffect(roughness)
                )
            )
        }
    }
}