package com.example.runnincle.framework.presentation.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle

@Composable
fun AutoSizeText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
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

    var scaledTextStyle by remember { mutableStateOf(textStyle) }
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text,
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
            } else {
                readyToDraw = true
            }
        }
    )
}

inline fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    noinline onClick: () -> Unit
): Modifier = composed {
    clickable(
        enabled = enabled,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}


@OptIn(ExperimentalMaterialApi::class)
val BottomSheetScaffoldState.currentFraction: Float
    get() {
        val fraction = bottomSheetState.progress.fraction
        val targetValue = bottomSheetState.targetValue
        val currentValue = bottomSheetState.currentValue

        return when {
            currentValue == Collapsed && targetValue == Collapsed -> 0f
            currentValue == Expanded && targetValue == Expanded -> 1f
            currentValue == Collapsed && targetValue == Expanded -> fraction
            else -> 1f - fraction
        }
    }