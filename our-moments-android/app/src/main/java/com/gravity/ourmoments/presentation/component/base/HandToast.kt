package com.gravity.ourmoments.presentation.component.base

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravity.ourmoments.ui.theme.InkLight
import com.gravity.ourmoments.ui.theme.PaperSurface

@Composable
fun HandToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) {
    val context = LocalContext.current
    android.widget.Toast.makeText(context, message, duration).show()
}

@Composable
fun HandToastMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = PaperSurface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = message,
            color = InkLight,
            fontSize = 14.sp
        )
    }
}