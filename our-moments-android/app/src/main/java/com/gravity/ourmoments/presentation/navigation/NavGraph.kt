package com.gravity.ourmoments.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gravity.ourmoments.presentation.ui.auth.LoginScreen
import com.gravity.ourmoments.presentation.ui.auth.RegisterScreen
import com.gravity.ourmoments.presentation.ui.home.HomeScreen
import com.gravity.ourmoments.presentation.ui.post.detail.PostDetailScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // 认证相关
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        // 主页
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // 文章详情
        composable(
            route = Screen.PostDetail.route,
            arguments = listOf(navArgument("postId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            PostDetailScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // 其他页面将在后续开发中添加
    }
}