package com.gravity.ourmoments.presentation.component.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.gravity.ourmoments.core.util.randomRotation
import com.gravity.ourmoments.domain.model.Post
import com.gravity.ourmoments.presentation.component.decorative.HandBorder
import com.gravity.ourmoments.presentation.component.decorative.PaperTexture
import com.gravity.ourmoments.presentation.component.decorative.PaperVariant
import com.gravity.ourmoments.presentation.component.decorative.PinDecoration
import com.gravity.ourmoments.presentation.component.decorative.PinVariant
import com.gravity.ourmoments.presentation.component.decorative.TapeDecoration
import com.gravity.ourmoments.presentation.component.decorative.TapePosition
import com.gravity.ourmoments.presentation.component.decorative.TapeVariant
import com.gravity.ourmoments.ui.theme.InkLight
import com.gravity.ourmoments.ui.theme.Typography

@Composable
fun PolaroidCard(
    post: Post,
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(8.dp),
                clip = false
            )
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCardClick() }
            .randomRotation(post.postId)
    ) {
        // 纸张纹理背景
        PaperTexture(
            modifier = Modifier
                .matchParentSize(),
            variant = PaperVariant.LIGHT
        ) {
            // 手绘边框
            HandBorder(
                modifier = Modifier
                    .matchParentSize()
                    .padding(4.dp)
            ) {
                // 内容
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // 图片展示
                    if (post.images.isNotEmpty()) {
                        val imageUrl = post.images.first()
                        Image(
                            painter = rememberAsyncImagePainter(imageUrl),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(4.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // 标题
                    Text(
                        text = post.title,
                        style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = InkLight,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 内容预览
                    Text(
                        text = post.content,
                        style = Typography.bodySmall,
                        color = InkLight.copy(alpha = 0.8f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 作者信息
                    Text(
                        text = "by ${post.author.nickname ?: post.author.username}",
                        style = Typography.bodySmall,
                        color = InkLight.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 时间
                    Text(
                        text = formatTime(post.createTime),
                        style = Typography.bodySmall,
                        color = InkLight.copy(alpha = 0.6f)
                    )
                }
            }
        }

        // 图钉装饰
        PinDecoration(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp),
            variant = PinVariant.RED
        )

        // 胶带装饰
        TapeDecoration(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            variant = TapeVariant.YELLOW,
            position = TapePosition.TOP_RIGHT
        )
    }
}

// 简单的时间格式化函数
private fun formatTime(timeString: String): String {
    // 这里应该根据实际的时间格式进行解析和格式化
    // 暂时返回原始字符串
    return timeString
}