package com.example.runnincle.framework.presentation.create_program.composable

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.runnincle.ui.theme.DarkRippleTheme


@Composable
fun CreateProgramTopAppBar(
    title: String,
    onBackClick: ()->Unit,
    onProgramSaveClick: ()->Unit,
) {
    CompositionLocalProvider(LocalRippleTheme provides DarkRippleTheme) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    color = MaterialTheme.colors.onBackground,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    onBackClick()
                }) {
                    Icon(
                        Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "",
                        tint = MaterialTheme.colors.onBackground
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    onProgramSaveClick()
                }) {
                    Icon(
                        Icons.Filled.Done,
                        contentDescription = "",
                        tint = MaterialTheme.colors.primary
                    )
                }
            },
            backgroundColor = MaterialTheme.colors.background,
            elevation = 0.dp,
        )
    }
}
