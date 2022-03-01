package com.devhanna91.runnincle.framework.presentation.composable

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle

@Composable
fun AutoSizeText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
) {
    // 미리보기용
    if (LocalInspectionMode.current) {
        Text(
            text,
            modifier,
            style = textStyle
        )
        return
    }

    var isScaled by remember { mutableStateOf(false) }
    var scaledTextStyle by remember { mutableStateOf(textStyle) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        modifier.drawWithContent {
            if (readyToDraw) {
                drawContent()
            }
        },
        style = scaledTextStyle,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                scaledTextStyle =
                    scaledTextStyle.copy(fontSize = scaledTextStyle.fontSize * 0.9)
                isScaled = true
            } else {
                if (isScaled) {
                    readyToDraw = true
                    isScaled = false
                } else {
                    readyToDraw = true
                    scaledTextStyle = textStyle
                }
            }
        }
    )
}