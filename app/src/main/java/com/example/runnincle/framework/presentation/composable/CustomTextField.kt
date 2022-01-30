package com.example.runnincle.framework.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun OutlineTextField(
    modifier: Modifier,
    fontSize: TextUnit,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
) {
    BasicTextField(
        modifier = modifier,
        value = value,
        onValueChange = {
            onValueChange(it)
        },
        textStyle = TextStyle(
            color = Color.White,
            fontSize = fontSize,
        ),
        enabled = enabled,
        singleLine = true,
        decorationBox = { innerTextField ->
            Row(
                Modifier
                    .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(percent = 30))
                    .padding(12.dp)
            ) {
                innerTextField()  //<-- Add this
            }
        },
        cursorBrush = SolidColor(Color.White),
        keyboardOptions = keyboardOptions,
    )
}