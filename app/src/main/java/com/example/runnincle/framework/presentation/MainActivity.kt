package com.example.runnincle.framework.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.runnincle.R
import com.example.runnincle.business.domain.model.IntervalProgram
import com.example.runnincle.startFloatingServiceWithCommand
import com.example.runnincle.util.FloatingService.Companion.INTENT_COMMAND_OPEN
import com.example.runnincle.ui.theme.RunnincleTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint // 생명주기에 맞춰 컨테이너를 만들어 의존성 주입을 받을 수 있음.
class MainActivity : AppCompatActivity() {

    private var warmingUp by mutableStateOf(60)
    private var setBoost by mutableStateOf(60)
    private var setCoolDown by mutableStateOf(60)
    private var retryTime by mutableStateOf(60)
    private var coolDown by mutableStateOf(60)
    private var skipCoolDownState by mutableStateOf(true)

    @Inject
    lateinit var app: BaseApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setContent {
//            RunnincleTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
//                    MainView(
//                        warmingUp = warmingUp,
//                        setBoost = setBoost,
//                        setCoolDown = setCoolDown,
//                        retryTime = retryTime,
//                        coolDown = coolDown,
//                        onSkipLastCoolDownCheckedChange = { onSkipLastCoolDownCheckedChange() },
//                        onStartBtnClicked = { onStartBtnClicked() }
//                    )
//                }
//            }
//        }
    }

    private fun onSkipLastCoolDownCheckedChange() {
        skipCoolDownState = !skipCoolDownState
    }


    private fun onStartBtnClicked() {
        val mIntervalProgram = IntervalProgram(
            warmingUp = warmingUp,
            setBoost = setBoost,
            setCoolDown = setCoolDown,
            retryTime = retryTime,
            coolDown = coolDown,
            isSkipLastSetCoolDown = skipCoolDownState
        )
        startFloatingServiceWithCommand(INTENT_COMMAND_OPEN, mIntervalProgram)
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
fun CheckBox(name: String, onCheckedChange: ()-> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val checkedState = remember { mutableStateOf(true) }
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = {
                checkedState.value = it
                onCheckedChange.invoke()
            }
        )
        Text(text = name, fontSize = 12.sp)
    }
}

@Composable
fun MainView(
    warmingUp: Int,
    setBoost: Int,
    setCoolDown: Int,
    retryTime: Int,
    coolDown:Int,
    onSkipLastCoolDownCheckedChange: () -> Unit,
    onStartBtnClicked: () -> Unit
) {
    Column {
        Title("워밍업")
        TimeSetting(sec = warmingUp, "워밍업")
        Title("세트반복")
        TimeSetting(sec = setBoost, label = "부스트")
        TimeSetting(sec = setCoolDown, label = "쿨다운")
        TimeSetting(sec = retryTime, label = "반복횟수")
        CheckBox("마지막 쿨다운 생략", onCheckedChange = onSkipLastCoolDownCheckedChange)
        Title("쿨다운")
        TimeSetting(sec = coolDown, "쿨다운")
        OutlinedButton(onClick = onStartBtnClicked) {
            Text(text = "시작하기")
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