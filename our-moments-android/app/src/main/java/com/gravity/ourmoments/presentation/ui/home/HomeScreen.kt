package com.gravity.ourmoments.presentation.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gravity.ourmoments.domain.model.Post
import com.gravity.ourmoments.presentation.component.base.HandButton
import com.gravity.ourmoments.presentation.component.base.HandLoader
import com.gravity.ourmoments.presentation.component.post.FilterBar
import com.gravity.ourmoments.presentation.component.post.StaggeredGrid
import com.gravity.ourmoments.presentation.navigation.Screen
import com.gravity.ourmoments.ui.theme.InkLight
import com.gravity.ourmoments.ui.theme.Typography

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var selectedVisibility by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedVisibility) {
        viewModel.refreshPosts(selectedVisibility)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 顶部标题
        Text(
            text = "Our Moments",
            style = Typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = InkLight,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )

        // 筛选栏
        FilterBar(
            selectedVisibility = selectedVisibility,
            onFilterChange = { visibility ->
                selectedVisibility = visibility
            }
        )

        // 内容区域
        when (uiState) {
            is HomeUiState.Loading -> {
                if (posts.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        HandLoader(text = "加载中...")
                    }
                } else {
                    PostListContent(
                        posts = posts,
                        isLoading = isLoading,
                        onPostClick = { post ->
                            navController.navigate("${Screen.PostDetail.route}/${post.postId}")
                        },
                        onLoadMore = {
                            viewModel.loadMorePosts(selectedVisibility)
                        },
                        onRefresh = {
                            viewModel.refreshPosts(selectedVisibility)
                        }
                    )
                }
            }
            is HomeUiState.Success -> {
                PostListContent(
                    posts = posts,
                    isLoading = isLoading,
                    onPostClick = { post ->
                        navController.navigate("${Screen.PostDetail.route}/${post.postId}")
                    },
                    onLoadMore = {
                        viewModel.loadMorePosts(selectedVisibility)
                    },
                    onRefresh = {
                        viewModel.refreshPosts(selectedVisibility)
                    }
                )
            }
            is HomeUiState.Empty -> {
                EmptyState(
                    onRefresh = {
                        viewModel.refreshPosts(selectedVisibility)
                    }
                )
            }
            is HomeUiState.Error -> {
                ErrorState(
                    message = (uiState as HomeUiState.Error).message,
                    onRetry = {
                        viewModel.refreshPosts(selectedVisibility)
                    }
                )
            }
        }
    }
}

@Composable
private fun PostListContent(
    posts: List<Post>,
    isLoading: Boolean,
    onPostClick: (Post) -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        StaggeredGrid(
            posts = posts,
            onPostClick = onPostClick,
            onEndReached = onLoadMore
        )

        // 下拉刷新或加载更多时的加载指示器
        if (isLoading && posts.isNotEmpty()) {
            HandLoader(
                text = "加载中...",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun EmptyState(
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(
            text = "暂无内容",
            style = Typography.bodyLarge,
            color = InkLight.copy(alpha = 0.6f)
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))

        HandButton(
            text = "刷新",
            onClick = onRefresh
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(
            text = message,
            style = Typography.bodyLarge,
            color = androidx.compose.material3.MaterialTheme.colorScheme.error
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(16.dp))

        HandButton(
            text = "重试",
            onClick = onRetry
        )
    }
}