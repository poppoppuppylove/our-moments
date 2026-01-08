package com.gravity.ourmoments.core.util

object ValidationUtil {

    fun isValidUsername(username: String): Boolean {
        return username.matches(Regex("^[a-zA-Z0-9_]{3,20}$"))
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))
    }

    fun isValidNickname(nickname: String): Boolean {
        return nickname.length in 1..20
    }

    fun getErrorMessage(field: String, value: String): String? {
        return when (field) {
            "username" -> {
                when {
                    value.isEmpty() -> "用户名不能为空"
                    !isValidUsername(value) -> "用户名必须是3-20位字母、数字或下划线"
                    else -> null
                }
            }
            "password" -> {
                when {
                    value.isEmpty() -> "密码不能为空"
                    !isValidPassword(value) -> "密码长度至少6位"
                    else -> null
                }
            }
            "email" -> {
                when {
                    value.isNotEmpty() && !isValidEmail(value) -> "邮箱格式不正确"
                    else -> null
                }
            }
            "nickname" -> {
                when {
                    value.isEmpty() -> "昵称不能为空"
                    !isValidNickname(value) -> "昵称长度必须在1-20位之间"
                    else -> null
                }
            }
            else -> null
        }
    }
}