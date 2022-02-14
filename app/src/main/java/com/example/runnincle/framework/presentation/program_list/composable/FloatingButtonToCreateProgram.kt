package com.example.runnincle.framework.presentation.program_list.composable

import android.view.View
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.findNavController
import com.example.runnincle.framework.presentation.program_list.ProgramListFragmentDirections

@Composable
fun FloatingButtonToCreateProgram(view: View, modifier: Modifier) {
    FloatingActionButton(
        modifier = modifier.padding(20.dp),
        backgroundColor = MaterialTheme.colors.primary,
        onClick = {
            val action = ProgramListFragmentDirections.actionProgramListFragmentToCreateProgramFragment()
            view.findNavController().navigate(action)
        }
    ) {
        Icon(Icons.Filled.Add,"")
    }
}
