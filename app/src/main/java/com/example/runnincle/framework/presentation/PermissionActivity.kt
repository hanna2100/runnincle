package com.example.runnincle.framework.presentation

import android.annotation.TargetApi
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.runnincle.R
import com.example.runnincle.ui.theme.RunnincleTheme
import com.siddroid.holi.colors.MaterialColor

const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1

class PermissionActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PermissionAlert(
                onCancelClick = {
                    finish()
                },
                onConfirmClick = {
                    requestPermission()
                }
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermission() {
        val intent = Intent(
            ACTION_MANAGE_OVERLAY_PERMISSION,
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

    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                println("permission check : no permission")
                finish()
            } else {
                println("permission check : ok permission")
                finish()
            }
        }
    }

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PermissionAlert(
    onConfirmClick: ()->Unit,
    onCancelClick: ()->Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val cardWidth = configuration.screenWidthDp.dp.times(0.75f)

    RunnincleTheme(darkSystemBar = true) {
        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialColor.GREY_900)
        ) {
            val (cardRef) = createRefs()
            Card(
                modifier = Modifier
                    .width(cardWidth)
                    .constrainAs(cardRef) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                backgroundColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp, 20.dp),
                        text = context.getString(R.string.permission_required_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialColor.GREY_200)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.permission),
                        contentDescription = "permission picture",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp, 20.dp)
                    )
                    Text(
                        text = context.getString(R.string.permission_required_text),
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(25.dp, 10.dp, 25.dp, 30.dp)
                    )
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp)
                    ) {
                        Column(modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(MaterialColor.GREY_400)
                            .clickable {
                                onCancelClick()
                            },
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = context.getString(R.string.cancel),
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                        Column(modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(MaterialTheme.colors.primary)
                            .clickable {
                                onConfirmClick()
                            },
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = context.getString(R.string.confirm),
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            )
                        }
                    }

                }
            }
        }
    }
}