package com.gravity.ourmoments.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gravity.ourmoments.core.model.UiState
import com.gravity.ourmoments.core.result.Result
import com.gravity.ourmoments.domain.model.LoginRequest
import com.gravity.ourmoments.domain.model.User
import com.gravity.ourmoments.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(username: String, nickname: String, password: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            // 这里应该调用注册用例，但暂时使用登录用例作为占位符
            when (val result = loginUseCase(LoginRequest(username, password))) {
                is Result.Success -> {
                    _uiState.value = RegisterUiState.Success(result.data.user)
                }
                is Result.Error -> {
                    _uiState.value = RegisterUiState.Error(result.message)
                }
                is Result.Loading -> {
                    _uiState.value = RegisterUiState.Loading
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = RegisterUiState.Idle
    }
}

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val user: User) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}