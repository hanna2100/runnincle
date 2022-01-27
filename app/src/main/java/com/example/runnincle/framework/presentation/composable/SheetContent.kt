package com.example.runnincle.framework.presentation.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun SheetExpanded(
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colors.primaryVariant)
            .padding(top = 40.dp, start = 40.dp, end = 40.dp, bottom = 40.dp)
    ) {
        content()
    }
}

@Composable
fun SheetCollapsed(
    isCollapsed: Boolean,
    currentFraction: Float,
    onSheetClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height((100 * (1f - currentFraction)).dp)
            .background(MaterialTheme.colors.primaryVariant)
            .graphicsLayer(alpha = 1f - currentFraction)
            .noRippleClickable(
                onClick = onSheetClick,
                enabled = isCollapsed
            )
    ) {
        content()
    }
}