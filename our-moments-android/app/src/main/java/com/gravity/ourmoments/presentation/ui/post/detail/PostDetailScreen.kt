package com.gravity.ourmoments.presentation.ui.post.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.gravity.ourmoments.domain.model.Comment
import com.gravity.ourmoments.presentation.component.base.HandButton
import com.gravity.ourmoments.presentation.component.base.HandLoader
import com.gravity.ourmoments.presentation.component.base.HandTextField
import com.gravity.ourmoments.presentation.component.comment.CommentItem
import com.gravity.ourmoments.presentation.component.decorative.PaperTexture
import com.gravity.ourmoments.presentation.component.decorative.PaperVariant
import com.gravity.ourmoments.ui.theme.InkLight
import com.gravity.ourmoments.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun PostDetailScreen(
    viewModel: PostDetailViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isCommentLoading by viewModel.isCommentLoading.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState) {
            is PostDetailUiState.Loading -> {
                HandLoader(
                    text = "加载中...",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is PostDetailUiState.Success -> {
                val post = (uiState as PostDetailUiState.Success).post
                val comments = (uiState as PostDetailUiState.Success).comments
                PostContent(
                    post = post,
                    comments = comments,
                    isCommentLoading = isCommentLoading,
                    onAddComment = { content -> viewModel.addComment(content) },
                    onBack = onBack,
                    onRefresh = { viewModel.refresh() }
                )
            }
            is PostDetailUiState.Error -> {
                ErrorState(
                    message = (uiState as PostDetailUiState.Error).message,
                    onRetry = { viewModel.refresh() },
                    onBack = onBack
                )
            }
        }
    }
}

@Composable
private fun PostContent(
    post: com.gravity.ourmoments.domain.model.Post,
    comments: List<Comment>,
    isCommentLoading: Boolean,
    onAddComment: (String) -> Unit,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }

    PaperTexture(
        modifier = Modifier.fillMaxSize(),
        variant = PaperVariant.LIGHT
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 返回按钮
            HandButton(
                text = "返回",
                onClick = onBack,
                variant = HandButton.HandButtonVariant.Outline
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 标题
            Text(
                text = post.title,
                style = Typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = InkLight
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 作者和时间信息
            Text(
                text = "by ${post.author.nickname ?: post.author.username}",
                style = Typography.bodyMedium,
                color = InkLight.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatDateTime(post.createTime),
                style = Typography.bodySmall,
                color = InkLight.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 图片展示
            if (post.images.isNotEmpty()) {
                post.images.forEach { imageUrl ->
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(vertical = 8.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 内容
            Text(
                text = post.content,
                style = Typography.bodyLarge,
                color = InkLight
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 评论部分标题
            Text(
                text = "评论 (${comments.size})",
                style = Typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = InkLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 评论输入框
            HandTextField(
                value = commentText,
                onValueChange = { commentText = it },
                label = "添加评论",
                placeholder = "写下你的想法...",
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCommentLoading
            )

            Spacer(modifier = Modifier.height(8.dp))

            HandButton(
                text = if (isCommentLoading) "发送中..." else "发送",
                onClick = {
                    if (commentText.isNotBlank() && !isCommentLoading) {
                        onAddComment(commentText)
                        commentText = ""
                    }
                },
                enabled = commentText.isNotBlank() && !isCommentLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 评论列表
            comments.forEach { comment ->
                CommentItem(
                    comment = comment,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(
            text = message,
            style = Typography.bodyLarge,
            color = androidx.compose.material3.MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        HandButton(
            text = "重试",
            onClick = onRetry
        )

        Spacer(modifier = Modifier.height(8.dp))

        HandButton(
            text = "返回",
            onClick = onBack,
            variant = HandButton.HandButtonVariant.Outline
        )
    }
}

private fun formatDateTime(dateTimeString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault())
        val date = inputFormat.parse(dateTimeString)
        outputFormat.format(date ?: return dateTimeString)
    } catch (e: Exception) {
        dateTimeString
    }
}