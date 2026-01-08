package com.gravity.ourmoments.presentation.component.decorative

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.gravity.ourmoments.ui.theme.PinBlue
import com.gravity.ourmoments.ui.theme.PinGreen
import com.gravity.ourmoments.ui.theme.PinPurple
import com.gravity.ourmoments.ui.theme.PinRed
import com.gravity.ourmoments.ui.theme.PinYellow
import kotlin.random.Random

@Composable
fun PinDecoration(
    modifier: Modifier = Modifier,
    variant: PinVariant = PinVariant.RED,
    size: PinSize = PinSize.MEDIUM
) {
    val pinColor = remember { getPinColor(variant) }
    val pinSize = remember { getPinSize(size) }

    Canvas(
        modifier = modifier.size(pinSize)
    ) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val pinRadius = size.width * 0.4f

        // 绘制图钉头部（圆形）
        drawCircle(
            color = pinColor,
            radius = pinRadius,
            center = Offset(centerX, centerY),
            style = Fill
        )

        // 绘制图钉尖部（三角形）
        val path = Path().apply {
            moveTo(centerX, centerY + pinRadius * 0.8f)
            lineTo(centerX - pinRadius * 0.3f, centerY + pinRadius * 1.5f)
            lineTo(centerX + pinRadius * 0.3f, centerY + pinRadius * 1.5f)
            close()
        }
        drawPath(
            path = path,
            color = pinColor,
            style = Fill
        )
    }
}

enum class PinVariant {
    RED, BLUE, GREEN, YELLOW, PURPLE
}

enum class PinSize {
    SMALL, MEDIUM, LARGE
}

private fun getPinColor(variant: PinVariant): Color {
    return when (variant) {
        PinVariant.RED -> PinRed
        PinVariant.BLUE -> PinBlue
        PinVariant.GREEN -> PinGreen
        PinVariant.YELLOW -> PinYellow
        PinVariant.PURPLE -> PinPurple
    }
}

private fun getPinSize(size: PinSize): androidx.compose.ui.unit.Dp {
    return when (size) {
        PinSize.SMALL -> 24.dp
        PinSize.MEDIUM -> 32.dp
        PinSize.LARGE -> 40.dp
    }
}