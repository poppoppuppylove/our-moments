package com.gravity.ourmoments.presentation.component.base

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravity.ourmoments.ui.theme.InkLight
import com.gravity.ourmoments.ui.theme.Mauve
import com.gravity.ourmoments.ui.theme.PaperSurface
import com.gravity.ourmoments.ui.theme.SoftPurple

@Composable
fun HandButton(
    text: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: HandButtonVariant = HandButtonVariant.Primary,
    size: HandButtonSize = HandButtonSize.Medium,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    val colors = getButtonColors(variant)
    val textSize = when (size) {
        HandButtonSize.Small -> 14.sp
        HandButtonSize.Medium -> 16.sp
        HandButtonSize.Large -> 18.sp
    }
    val height = when (size) {
        HandButtonSize.Small -> 36.dp
        HandButtonSize.Medium -> 44.dp
        HandButtonSize.Large -> 52.dp
    }

    // 不规则圆角
    val shape = RoundedCornerShape(12.dp)

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Button(
        onClick = onClick,
        modifier = modifier
            .height(height)
            .then(
                if (variant != HandButtonVariant.Ghost && variant != HandButtonVariant.Outline)
                    Modifier.graphicsLayer {
                        // 按下时的缩放效果
                        val scale = if (isPressed) 0.95f else 1f
                        scaleX = scale
                        scaleY = scale
                    }
                else Modifier
            ),
        enabled = enabled && !loading,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = colors.container,
            contentColor = colors.content,
            disabledContainerColor = colors.disabledContainer,
            disabledContentColor = colors.disabledContent
        ),
        border = when (variant) {
            HandButtonVariant.Outline -> BorderStroke(
                1.dp,
                if (enabled) colors.content else colors.disabledContent.copy(alpha = 0.5f)
            )
            else -> null
        },
        elevation = when (variant) {
            HandButtonVariant.Ghost, HandButtonVariant.Outline -> null
            else -> ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 0.dp
            )
        },
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = colors.content,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text ?: "",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                        fontSize = textSize,
                        fontWeight = FontWeight.Medium
                    ),
                    fontFamily = FontFamily.Default
                )
            }
        }
    }
}

enum class HandButtonVariant {
    Primary, Secondary, Outline, Ghost
}

enum class HandButtonSize {
    Small, Medium, Large
}

private data class ButtonColors(
    val container: Color,
    val content: Color,
    val disabledContainer: Color,
    val disabledContent: Color
)

@Composable
private fun getButtonColors(variant: HandButtonVariant): ButtonColors {
    return when (variant) {
        HandButtonVariant.Primary -> ButtonColors(
            container = SoftPurple,
            content = Color.White,
            disabledContainer = SoftPurple.copy(alpha = 0.5f),
            disabledContent = Color.White.copy(alpha = 0.7f)
        )
        HandButtonVariant.Secondary -> ButtonColors(
            container = Mauve,
            content = Color.White,
            disabledContainer = Mauve.copy(alpha = 0.5f),
            disabledContent = Color.White.copy(alpha = 0.7f)
        )
        HandButtonVariant.Outline -> ButtonColors(
            container = Color.Transparent,
            content = SoftPurple,
            disabledContainer = Color.Transparent,
            disabledContent = InkLight.copy(alpha = 0.5f)
        )
        HandButtonVariant.Ghost -> ButtonColors(
            container = Color.Transparent,
            content = InkLight,
            disabledContainer = Color.Transparent,
            disabledContent = InkLight.copy(alpha = 0.5f)
        )
    }
}