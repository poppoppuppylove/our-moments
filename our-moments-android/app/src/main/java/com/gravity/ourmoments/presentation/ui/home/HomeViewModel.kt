package com.gravity.ourmoments.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravity.ourmoments.core.result.Result
import com.gravity.ourmoments.domain.model.Post
import com.gravity.ourmoments.domain.usecase.post.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var currentPage = 0
    private val pageSize = 10
    private var hasMorePages = true

    init {
        loadPosts()
    }

    fun loadPosts(visibility: String? = null) {
        if (!hasMorePages && currentPage > 0) return

        viewModelScope.launch {
            _isLoading.value = true
            if (currentPage == 0) {
                _uiState.value = HomeUiState.Loading
            }

            when (val result = getPostsUseCase(currentPage, pageSize, visibility)) {
                is Result.Success -> {
                    val newPosts = result.data
                    if (newPosts.isEmpty()) {
                        hasMorePages = false
                        if (currentPage == 0) {
                            _uiState.value = HomeUiState.Empty
                        }
                    } else {
                        if (currentPage == 0) {
                            _posts.value = newPosts
                            _uiState.value = HomeUiState.Success
                        } else {
                            _posts.value = _posts.value + newPosts
                        }
                        currentPage++
                    }
                }
                is Result.Error -> {
                    if (currentPage == 0) {
                        _uiState.value = HomeUiState.Error(result.message)
                    }
                }
                is Result.Loading -> {
                    if (currentPage == 0) {
                        _uiState.value = HomeUiState.Loading
                    }
                }
            }
            _isLoading.value = false
        }
    }

    fun loadMorePosts(visibility: String? = null) {
        if (_isLoading.value || !hasMorePages) return
        loadPosts(visibility)
    }

    fun refreshPosts(visibility: String? = null) {
        currentPage = 0
        hasMorePages = true
        loadPosts(visibility)
    }

    fun clearError() {
        _uiState.value = HomeUiState.Success
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Success : HomeUiState()
    object Empty : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}