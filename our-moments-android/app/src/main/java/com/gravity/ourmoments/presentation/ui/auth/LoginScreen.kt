package com.gravity.ourmoments.presentation.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.gravity.ourmoments.core.util.ValidationUtil
import com.gravity.ourmoments.presentation.component.base.HandButton
import com.gravity.ourmoments.presentation.component.base.HandLoader
import com.gravity.ourmoments.presentation.component.base.HandTextField
import com.gravity.ourmoments.presentation.navigation.Screen
import com.gravity.ourmoments.ui.theme.InkLight
import com.gravity.ourmoments.ui.theme.Typography

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // 如果已经登录，跳转到主页
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    // 处理登录成功状态
    LaunchedEffect(uiState) {
        when (uiState) {
            is LoginUiState.Success -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            is LoginUiState.Error -> {
                // 错误处理在UI中显示
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (uiState) {
            is LoginUiState.Loading -> {
                HandLoader(text = "登录中...")
            }
            else -> {
                LoginContent(
                    username = username,
                    password = password,
                    usernameError = usernameError,
                    passwordError = passwordError,
                    onUsernameChange = {
                        username = it
                        usernameError = ValidationUtil.getErrorMessage("username", it)
                    },
                    onPasswordChange = {
                        password = it
                        passwordError = ValidationUtil.getErrorMessage("password", it)
                    },
                    onLoginClick = {
                        val isUsernameValid = ValidationUtil.getErrorMessage("username", username) == null
                        val isPasswordValid = ValidationUtil.getErrorMessage("password", password) == null

                        usernameError = ValidationUtil.getErrorMessage("username", username)
                        passwordError = ValidationUtil.getErrorMessage("password", password)

                        if (isUsernameValid && isPasswordValid) {
                            viewModel.login(username, password)
                        }
                    },
                    onRegisterClick = {
                        navController.navigate(Screen.Register.route)
                    },
                    errorMessage = if (uiState is LoginUiState.Error) (uiState as LoginUiState.Error).message else null
                )
            }
        }
    }
}

@Composable
fun LoginContent(
    username: String,
    password: String,
    usernameError: String?,
    passwordError: String?,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    errorMessage: String?
) {
    Spacer(modifier = Modifier.height(48.dp))

    // Logo或标题
    Text(
        text = "Our Moments",
        style = Typography.headlineMedium,
        color = InkLight,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "记录生活中的每个美好瞬间",
        style = Typography.bodyMedium,
        color = InkLight.copy(alpha = 0.7f),
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(48.dp))

    // 错误信息
    if (errorMessage != null) {
        Text(
            text = errorMessage,
            color = androidx.compose.material3.MaterialTheme.colorScheme.error,
            style = Typography.bodySmall
        )
        Spacer(modifier = Modifier.height(8.dp))
    }

    // 用户名输入
    HandTextField(
        value = username,
        onValueChange = onUsernameChange,
        label = "用户名",
        placeholder = "请输入用户名",
        modifier = Modifier.fillMaxWidth(),
        keyboardType = KeyboardType.Text,
        isError = usernameError != null,
        errorMessage = usernameError
    )

    Spacer(modifier = Modifier.height(16.dp))

    // 密码输入
    HandTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = "密码",
        placeholder = "请输入密码",
        modifier = Modifier.fillMaxWidth(),
        keyboardType = KeyboardType.Password,
        isPassword = true,
        isError = passwordError != null,
        errorMessage = passwordError
    )

    Spacer(modifier = Modifier.height(24.dp))

    // 登录按钮
    HandButton(
        text = "登录",
        onClick = onLoginClick,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    // 注册链接
    HandButton(
        text = "还没有账号？去注册",
        onClick = onRegisterClick,
        modifier = Modifier.fillMaxWidth(),
        variant = HandButton.HandButtonVariant.Outline
    )
}