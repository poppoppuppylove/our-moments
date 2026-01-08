package com.gravity.ourmoments.presentation.component.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gravity.ourmoments.domain.model.Post

@Composable
fun StaggeredGrid(
    posts: List<Post>,
    modifier: Modifier = Modifier,
    columns: Int = 2,
    onPostClick: (Post) -> Unit = {},
    onEndReached: () -> Unit = {}
) {
    val chunkedPosts = posts.chunked(columns)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(
            items = chunkedPosts,
            key = { chunk ->
                chunk.joinToString { it.postId.toString() }
            }
        ) { columnPosts ->
            PostRow(
                posts = columnPosts,
                onPostClick = onPostClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            if (posts.isNotEmpty()) {
                Box(modifier = Modifier.padding(vertical = 16.dp)) {
                    onEndReached()
                }
            }
        }
    }
}

@Composable
private fun PostRow(
    posts: List<Post>,
    modifier: Modifier = Modifier,
    onPostClick: (Post) -> Unit = {}
) {
    androidx.compose.foundation.layout.Row(
        modifier = modifier
    ) {
        posts.forEach { post ->
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp, vertical = 4.dp)
            ) {
                PolaroidCard(
                    post = post,
                    onCardClick = { onPostClick(post) }
                )
            }
        }

        // 如果最后一行不足columns列，添加空的Box占位
        val emptySlots = 2 - posts.size
        repeat(emptySlots) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.weight(1f)
            )
        }
    }
}