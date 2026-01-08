package com.gravity.ourmoments.presentation.component.common

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.gravity.ourmoments.presentation.navigation.BottomNavItem
import com.gravity.ourmoments.presentation.navigation.Screen
import com.gravity.ourmoments.ui.theme.InkFaded
import com.gravity.ourmoments.ui.theme.SoftPurple

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = Color.White.copy(alpha = 0.95f),
        tonalElevation = 2.dp
    ) {
        val screens = listOf(
            BottomNavItem.Home,
            BottomNavItem.Discover,
            BottomNavItem.Create,
            BottomNavItem.Friends,
            BottomNavItem.Profile
        )

        screens.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    // 这里应该使用实际的图标资源
                    // 暂时使用简单的圆点作为占位符
                    androidx.compose.foundation.layout.Box(
                        modifier = androidx.compose.ui.Modifier
                            .size(24.dp)
                            .background(
                                color = if (isSelected) SoftPurple else InkFaded,
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontFamily = FontFamily.Default,
                        fontSize = 12.sp,
                        color = if (isSelected) SoftPurple else InkFaded
                    )
                }
            )
        }
    }
}