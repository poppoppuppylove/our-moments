package com.gravity.ourmoments.presentation.component.decorative

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gravity.ourmoments.ui.theme.TapeBlue
import com.gravity.ourmoments.ui.theme.TapeGreen
import com.gravity.ourmoments.ui.theme.TapePink
import com.gravity.ourmoments.ui.theme.TapePurple
import com.gravity.ourmoments.ui.theme.TapeYellow
import kotlin.random.Random

@Composable
fun TapeDecoration(
    modifier: Modifier = Modifier,
    variant: TapeVariant = TapeVariant.YELLOW,
    position: TapePosition = TapePosition.TOP_RIGHT
) {
    val colors = remember { getTapeColors(variant) }
    val rotation = remember { getTapeRotation(position) }

    Box(
        modifier = modifier
            .size(80.dp, 24.dp)
            .rotate(rotation)
            .background(
                brush = Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 0f),
                    end = Offset(80f, 24f)
                ),
                shape = RoundedCornerShape(2.dp)
            )
            .alpha(0.85f)
    )
}

enum class TapeVariant {
    YELLOW, PINK, BLUE, GREEN, PURPLE
}

enum class TapePosition {
    TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

private fun getTapeColors(variant: TapeVariant): List<Color> {
    return when (variant) {
        TapeVariant.YELLOW -> listOf(
            TapeYellow.copy(alpha = 0.8f),
            TapeYellow.copy(alpha = 0.6f)
        )
        TapeVariant.PINK -> listOf(
            TapePink.copy(alpha = 0.7f),
            TapePink.copy(alpha = 0.5f)
        )
        TapeVariant.BLUE -> listOf(
            TapeBlue.copy(alpha = 0.7f),
            TapeBlue.copy(alpha = 0.5f)
        )
        TapeVariant.GREEN -> listOf(
            TapeGreen.copy(alpha = 0.7f),
            TapeGreen.copy(alpha = 0.5f)
        )
        TapeVariant.PURPLE -> listOf(
            TapePurple.copy(alpha = 0.7f),
            TapePurple.copy(alpha = 0.5f)
        )
    }
}

private fun getTapeRotation(position: TapePosition): Float {
    return when (position) {
        TapePosition.TOP_LEFT -> -15f + Random.nextFloat() * 10f
        TapePosition.TOP_RIGHT -> 15f - Random.nextFloat() * 10f
        TapePosition.BOTTOM_LEFT -> -15f + Random.nextFloat() * 10f
        TapePosition.BOTTOM_RIGHT -> 15f - Random.nextFloat() * 10f
    }
}