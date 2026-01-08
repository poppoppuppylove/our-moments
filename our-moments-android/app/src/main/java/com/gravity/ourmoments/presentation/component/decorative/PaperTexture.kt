package com.gravity.ourmoments.presentation.component.decorative

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import com.gravity.ourmoments.ui.theme.KraftPaper
import com.gravity.ourmoments.ui.theme.Paper
import com.gravity.ourmoments.ui.theme.Watercolor

@Composable
fun PaperTexture(
    modifier: Modifier = Modifier,
    variant: PaperVariant = PaperVariant.LIGHT,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                when (variant) {
                    PaperVariant.LIGHT -> Paper
                    PaperVariant.WARM -> Color(0xFFFFF9F0)
                    PaperVariant.KRAFT -> KraftPaper
                    PaperVariant.WATERCOLOR -> Watercolor
                }
            )
    ) {
        content()
    }
}

enum class PaperVariant {
    LIGHT,
    WARM,
    KRAFT,
    WATERCOLOR
}