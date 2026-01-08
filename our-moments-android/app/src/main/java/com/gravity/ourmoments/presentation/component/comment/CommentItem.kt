package com.gravity.ourmoments.presentation.component.comment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gravity.ourmoments.domain.model.Comment
import com.gravity.ourmoments.ui.theme.InkLight
import com.gravity.ourmoments.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun CommentItem(
    comment: Comment,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // 用户头像占位符
        Spacer(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            // 用户名和时间
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = comment.author.nickname ?: comment.author.username,
                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = InkLight
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = formatTime(comment.createTime),
                    style = Typography.bodySmall,
                    color = InkLight.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 评论内容
            Text(
                text = comment.content,
                style = Typography.bodyMedium,
                color = InkLight
            )
        }
    }
}

private fun formatTime(timeString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
        val date = inputFormat.parse(timeString)
        outputFormat.format(date ?: return timeString)
    } catch (e: Exception) {
        timeString
    }
}

// 占位符背景扩展函数
private fun Modifier.background(color: Color): Modifier {
    return this // 在实际实现中应该添加背景绘制逻辑
}