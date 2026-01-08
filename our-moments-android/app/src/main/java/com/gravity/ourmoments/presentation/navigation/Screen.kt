package com.gravity.ourmoments.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * 屏幕路由定义
 */
sealed class Screen(val route: String) {

    // 认证相关
    data object Login : Screen("login")
    data object Register : Screen("register")

    // 主要功能
    data object Home : Screen("home")
    data object Discover : Screen("discover")
    data object PostCreate : Screen("post_create")

    // 文章相关
    data object PostDetail : Screen("post_detail/{postId}") {
        fun createRoute(postId: String) = "post_detail/$postId"
    }

    // 好友相关
    data object Friends : Screen("friends")
    data object FriendRequests : Screen("friend_requests")
    data object AddFriend : Screen("add_friend")

    // 聊天相关
    data object ChatList : Screen("chat_list")
    data object Chat : Screen("chat/{friendId}") {
        fun createRoute(friendId: String) = "chat/$friendId"
    }

    // 通知
    data object Notifications : Screen("notifications")

    // 个人资料
    data object Profile : Screen("profile")
    data object EditProfile : Screen("edit_profile")
}

/**
 * 底部导航项
 */
data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: Int // 这里使用图标资源ID，后期可以改为ImageVector
) {
    companion object {
        val Home = BottomNavItem(Screen.Home.route, "首页", 1)
        val Discover = BottomNavItem(Screen.Discover.route, "发现", 2)
        val Create = BottomNavItem(Screen.PostCreate.route, "发布", 3)
        val Friends = BottomNavItem(Screen.Friends.route, "好友", 4)
        val Profile = BottomNavItem(Screen.Profile.route, "我的", 5)
    }
}