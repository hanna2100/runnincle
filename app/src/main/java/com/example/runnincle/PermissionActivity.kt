package com.example.runnincle

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1
const val INTENT_TYPE = "INTENT_TYPE"

class PermissionActivity: AppCompatActivity() {

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private fun showDialog(titleText: String, messageText: String) {
        with(AlertDialog.Builder(this)) {
            title = titleText
            setMessage(messageText)
            setPositiveButton(R.string.confirm) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        try {
            startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
        } catch (e: Exception) {
            showDialog(
                getString(R.string.permission_error_title),
                getString(R.string.permission_error_text)
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
                Text(
                    text = getString(R.string.permission_required_title),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
                )

                Text(
                    text = getString(R.string.permission_required_text),
                    modifier = Modifier.padding(16.dp, 4.dp)
                )

                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            finish()
                        } else {
                            requestPermission()
                        }
                    },
                    modifier = Modifier.padding(16.dp, 8.dp)
                ) {
                    Text(text = getString(R.string.permission_required_open))
                }

            }
        }
    }

}