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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.gravity.ourmoments.core.util.ValidationUtil
import com.gravity.ourmoments.presentation.component.base.HandButton
import com.gravity.ourmoments.presentation.component.base.HandTextField
import com.gravity.ourmoments.presentation.navigation.Screen
import com.gravity.ourmoments.ui.theme.InkLight
import com.gravity.ourmoments.ui.theme.Typography

@Composable
fun RegisterScreen(
    navController: NavController
) {
    var username by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var nicknameError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Logo或标题
        Text(
            text = "注册账号",
            style = Typography.headlineMedium,
            color = InkLight,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "欢迎加入Our Moments大家庭",
            style = Typography.bodyMedium,
            color = InkLight.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 用户名输入
        HandTextField(
            value = username,
            onValueChange = {
                username = it
                usernameError = ValidationUtil.getErrorMessage("username", it)
            },
            label = "用户名",
            placeholder = "请输入用户名",
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Text,
            isError = usernameError != null,
            errorMessage = usernameError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 昵称输入
        HandTextField(
            value = nickname,
            onValueChange = {
                nickname = it
                nicknameError = ValidationUtil.getErrorMessage("nickname", it)
            },
            label = "昵称",
            placeholder = "请输入昵称",
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Text,
            isError = nicknameError != null,
            errorMessage = nicknameError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 密码输入
        HandTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = ValidationUtil.getErrorMessage("password", it)
                // 检查确认密码是否匹配
                if (confirmPassword.isNotEmpty() && confirmPassword != it) {
                    confirmPasswordError = "两次输入的密码不一致"
                } else {
                    confirmPasswordError = null
                }
            },
            label = "密码",
            placeholder = "请输入密码",
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Password,
            isPassword = true,
            isError = passwordError != null,
            errorMessage = passwordError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 确认密码输入
        HandTextField(
            value = confirmPassword,
            onValueChange = {
                confirmPassword = it
                if (it != password) {
                    confirmPasswordError = "两次输入的密码不一致"
                } else {
                    confirmPasswordError = null
                }
            },
            label = "确认密码",
            placeholder = "请再次输入密码",
            modifier = Modifier.fillMaxWidth(),
            keyboardType = KeyboardType.Password,
            isPassword = true,
            isError = confirmPasswordError != null,
            errorMessage = confirmPasswordError
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 注册按钮
        HandButton(
            text = "注册",
            onClick = {
                // 验证所有字段
                usernameError = ValidationUtil.getErrorMessage("username", username)
                nicknameError = ValidationUtil.getErrorMessage("nickname", nickname)
                passwordError = ValidationUtil.getErrorMessage("password", password)

                if (confirmPassword != password) {
                    confirmPasswordError = "两次输入的密码不一致"
                }

                val isAllValid = usernameError == null &&
                                nicknameError == null &&
                                passwordError == null &&
                                confirmPasswordError == null

                if (isAllValid) {
                    // 这里将在后续实现实际的注册逻辑
                    // 暂时跳转到登录页
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 登录链接
        HandButton(
            text = "已有账号？去登录",
            onClick = {
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth(),
            variant = HandButton.HandButtonVariant.Outline
        )
    }
}