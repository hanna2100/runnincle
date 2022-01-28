package com.example.runnincle.framework.presentation.create_program.composable

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.runnincle.R


@Composable
fun CreateProgramTopAppBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.add_new_workout),
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                // 뒤로가기
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
                // program 추가
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
