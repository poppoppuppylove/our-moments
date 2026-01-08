package com.gravity.ourmoments.presentation.component.base

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gravity.ourmoments.ui.theme.InkLight
import com.gravity.ourmoments.ui.theme.PaperSurface
import com.gravity.ourmoments.ui.theme.TapeYellow

@Composable
fun HandTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    color = if (isError) PaperSurface.copy(alpha = 0.7f) else PaperSurface,
                    shape = RoundedCornerShape(12.dp)
                )
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = when {
                            isError -> Color.Red.copy(alpha = 0.7f)
                            isFocused -> TapeYellow
                            else -> Color.Gray.copy(alpha = 0.3f)
                        }
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            enabled = enabled,
            singleLine = singleLine,
            textStyle = TextStyle(
                color = InkLight,
                fontSize = 16.sp
            ),
            cursorBrush = SolidColor(TapeYellow),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty() && placeholder != null) {
                        Text(
                            text = placeholder,
                            color = InkLight.copy(alpha = 0.5f),
                            fontSize = 16.sp
                        )
                    }
                    innerTextField()
                }
            }
        )

        // 标签
        if (label != null) {
            Text(
                text = label,
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 4.dp)
                    .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                    .padding(horizontal = 4.dp),
                color = if (isError) Color.Red else InkLight,
                fontSize = 12.sp
            )
        }

        // 错误信息
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                modifier = Modifier
                    .padding(top = 4.dp, start = 4.dp),
                color = Color.Red,
                fontSize = 12.sp
            )
        }
    }
}