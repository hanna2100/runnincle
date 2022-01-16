package com.example.runnincle.presentation.ui.create_workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.runnincle.R

class CreateWorkoutFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                Text("CreateWorkoutFragment", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.padding(10.dp))
                Button(onClick = {
                    findNavController().navigate(R.id.workoutListFragment)
                }) {
                    Text("WorkoutListFragment 로 이동")
                }
            }
        }
    }
}