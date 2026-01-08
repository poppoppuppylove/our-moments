package com.gravity.ourmoments.presentation.component.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.gravity.ourmoments.core.util.HandDrawnUtil
import com.gravity.ourmoments.presentation.component.decorative.TapeDecoration
import com.gravity.ourmoments.presentation.component.decorative.TapePosition
import com.gravity.ourmoments.presentation.component.decorative.TapeVariant
import com.gravity.ourmoments.ui.theme.InkFaded
import com.gravity.ourmoments.ui.theme.InkLight

@Composable
fun PolaroidCard(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    title: String,
    content: String? = null,
    onClick: () -> Unit
) {
    // 随机旋转角度
    val randomRotation = remember { HandDrawnUtil.generateRandomRotation() }

    Card(
        onClick = onClick,
        modifier = modifier
            .graphicsLayer { rotationZ = randomRotation },
        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(
                start = 12.dp,
                end = 12.dp,
                top = 12.dp,
                bottom = 48.dp  // 底部留空模拟拍立得样式
            )
        ) {
            // 图片区域
            if (imageUrl != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(Color.Gray)
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 胶带装饰（随机位置）
            TapeDecoration(
                variant = when (HandDrawnUtil.getRandomTapeVariant()) {
                    0 -> TapeVariant.YELLOW
                    1 -> TapeVariant.PINK
                    2 -> TapeVariant.BLUE
                    3 -> TapeVariant.GREEN
                    else -> TapeVariant.PURPLE
                },
                position = when (HandDrawnUtil.getRandomTapePosition()) {
                    0 -> TapePosition.TOP_LEFT
                    1 -> TapePosition.TOP_RIGHT
                    2 -> TapePosition.BOTTOM_LEFT
                    else -> TapePosition.BOTTOM_RIGHT
                }
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 标题
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                color = InkLight,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // 内容摘要
            if (content != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = content,
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = InkFaded,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}