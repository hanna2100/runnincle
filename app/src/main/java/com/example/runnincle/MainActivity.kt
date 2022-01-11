package com.example.runnincle

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.example.runnincle.ui.theme.RunnincleTheme

class MainActivity : ComponentActivity() {

    companion object {
        const val CALL_TYPE = "CallType"
        const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 1
    }

    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    private var warmingUp = MutableLiveData(60)
    private var setBoost = MutableLiveData(30)
    private var setCoolDown = MutableLiveData(20)
    private var retry = MutableLiveData(10)
    private var coolDown = MutableLiveData(60)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RunnincleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainView(
                        warmingUp.value!!,
                        setBoost.value!!,
                        setCoolDown.value!!,
                        retry.value!!,
                        coolDown.value!!,
                        { onStartBtnClicked() },
                        { onCancelBtnClicked() }
                    )
                }
            }
        }

        resultLauncher = registerForActivityResult(
            StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val callType = intent!!.getIntExtra(CALL_TYPE, 0)

                when (callType) {
                    ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE -> {
                        if (!Settings.canDrawOverlays(this)) {
                            // 동의를 받지 못했을때 처리
                            Toast.makeText(this, "동의를 받지 못했어요", Toast.LENGTH_LONG)
                        }
                    }
                }
            }
        }

        getPermission()

    }

    private fun onStartBtnClicked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(Intent(this, IntervalOverlayService::class.java))
        } else {
            startService(Intent(this, IntervalOverlayService::class.java))
        }
    }

    private fun onCancelBtnClicked() {
        val intent = Intent(this, IntervalOverlayService::class.java)
        stopService(intent)
    }

    private fun getPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package: $packageName")
            )
            intent.putExtra(CALL_TYPE, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            resultLauncher.launch(intent)
        }
    }
}

@Composable
fun Title(name: String) {
    Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun TimeSetting(sec: Int, label: String) {
    var timeSecond by remember { mutableStateOf(sec.toString())}
    TextField(
        value = timeSecond,
        onValueChange = { timeSecond = it },
        label = { Text(text = label)}
    )
}

@Composable
fun CheckBox(name: String) {
    val checkedState = remember { mutableStateOf(true) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = it }
        )
        Text(text = name, fontSize = 12.sp)
    }
}

@Composable
fun MainView(
    warmingUp: Int,
    boost: Int,
    setCoolDown: Int,
    retry: Int,
    coolDown:Int,
    onStartBtnClicked: () -> Unit,
    onCancelBtnClicked: () -> Unit
) {
    Column {
        Title("워밍업")
        TimeSetting(sec = warmingUp, "워밍업")
        Title("세트반복")
        TimeSetting(sec = boost, label = "부스트")
        TimeSetting(sec = setCoolDown, label = "쿨다운")
        TimeSetting(sec = retry, label = "반복횟수")
        CheckBox("마지막 쿨다운 생략")
        Title("쿨다운")
        TimeSetting(sec = coolDown, "쿨다운")
        Row() {
            OutlinedButton(onClick = onStartBtnClicked) {
                Text(text = "시작하기")
            }
            OutlinedButton(onClick = onCancelBtnClicked) {
                Text(text = "끝내기")
            }
        }
    }
}

@Preview
@Composable
fun PreviewTest() {
    RunnincleTheme {
        MainView(60,30,20,10,60, {}, {})
    }
}