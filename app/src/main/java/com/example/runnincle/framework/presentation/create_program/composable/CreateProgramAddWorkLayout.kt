package com.example.runnincle.framework.presentation.create_program.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.runnincle.R
import com.example.runnincle.framework.presentation.composable.BottomOutlineTextField
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser


@Composable
fun AddWorkLayout(modifier: Modifier) {
    Column(
        modifier = modifier
    ) {
        var name by remember { mutableStateOf("") }
        var workMin by remember { mutableStateOf("") }
        var workSec by remember { mutableStateOf("") }
        var coolDownMin by remember { mutableStateOf("") }
        var coolDownSec by remember { mutableStateOf("") }
        var isNoCoolDown by remember { mutableStateOf(false) }
        var isSkipLastCoolDown by remember { mutableStateOf(false) }
        var set by remember { mutableStateOf("") }

        var timerColor: Color by remember { mutableStateOf(Color.Black) }

        Text(
            text = "이름",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colors.onPrimary
        )
        BottomOutlineTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            fontSize = 20.sp,
            value = name,
            onValueChange = {
                name = it
            }
        )
        Column {
            Text(
                text = "운동시간",
                modifier = Modifier
                    .width(132.dp)
                    .padding(bottom = 5.dp),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colors.onPrimary
            )
            Row {
                BottomOutlineTextField(
                    modifier = Modifier
                        .width(60.dp)
                        .padding(bottom = 10.dp),
                    fontSize = 20.sp,
                    value = workMin,
                    onValueChange = {
                        if (it.isDigitsOnly() && it.length <= 3) {
                            workMin = it
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Text(
                    text = ":",
                    modifier = Modifier.padding(horizontal = 6.dp),
                    fontSize = 40.sp,
                    color = MaterialTheme.colors.onPrimary
                )
                BottomOutlineTextField(
                    modifier = Modifier
                        .width(60.dp)
                        .padding(bottom = 10.dp),
                    fontSize = 20.sp,
                    value = workSec,
                    onValueChange = {
                        if (it.isDigitsOnly() && it.length <= 2) {
                            if(it.isBlank()) {
                                workSec = ""
                            } else if(it.toInt() <= 60) {
                                workSec = it
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text(
                    text = "쿨다운 시간",
                    modifier = Modifier
                        .width(132.dp)
                        .padding(bottom = 5.dp),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.onPrimary
                )
                Row {
                    BottomOutlineTextField(
                        enabled = !isNoCoolDown,
                        modifier = Modifier
                            .width(60.dp)
                            .padding(bottom = 10.dp),
                        fontSize = 20.sp,
                        value = coolDownMin,
                        onValueChange = {
                            if (it.isDigitsOnly() && it.length <= 3) {
                                coolDownMin = it
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Text(
                        text = ":",
                        modifier = Modifier.padding(horizontal = 6.dp),
                        fontSize = 40.sp,
                        color = MaterialTheme.colors.onPrimary
                    )
                    BottomOutlineTextField(
                        enabled = !isNoCoolDown,
                        modifier = Modifier
                            .width(60.dp)
                            .padding(bottom = 10.dp),
                        fontSize = 20.sp,
                        value = coolDownSec,
                        onValueChange = {
                            if (it.isDigitsOnly() && it.length <= 2) {
                                if(it.isBlank()) {
                                    coolDownSec = ""
                                } else if(it.toInt() <= 60) {
                                    coolDownSec = it
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
            Column(modifier = Modifier.padding(bottom = 10.dp)) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.height(24.dp)
                ) {
                    RadioButton(
                        selected = isNoCoolDown,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.White.copy(alpha = 0.6f),
                            disabledColor = Color.White.copy(alpha = 0.3f)
                        ),
                        onClick = {
                            isNoCoolDown = !isNoCoolDown
                            if(isNoCoolDown) {
                                isSkipLastCoolDown = false
                                coolDownMin = ""
                                coolDownSec = ""
                            }
                        }
                    )
                    Text(
                        text = "쿨다운 없음",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(bottom = 5.dp),
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colors.onPrimary
                    )
                }
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.height(24.dp)
                ) {
                    RadioButton(
                        selected = isSkipLastCoolDown,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.White,
                            unselectedColor = Color.White.copy(alpha = 0.6f),
                            disabledColor = Color.White.copy(alpha = 0.3f)
                        ),
                        onClick = { isSkipLastCoolDown = !isSkipLastCoolDown },
                        enabled = !isNoCoolDown
                    )
                    Text(
                        text = "마지막 쿨다운 생략",
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(bottom = 5.dp),
                        textAlign = TextAlign.Start,
                        color = if (!isNoCoolDown) {
                            MaterialTheme.colors.onPrimary
                        } else {
                            MaterialTheme.colors.onPrimary.copy(alpha = 0.3f)
                        }
                    )
                }
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(bottom = 10.dp)
            ){
                Text(
                    text = "세트",
                    modifier = Modifier.padding(bottom = 5.dp),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.onPrimary
                )
                BottomOutlineTextField(
                    modifier = Modifier
                        .width(60.dp)
                        .padding(bottom = 10.dp),
                    fontSize = 20.sp,
                    value = set,
                    onValueChange = {
                        if (it.isDigitsOnly() && it.length <= 2) {
                            set = it
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                DialogAndShowButton(
                    selectedColor = timerColor,
                    buttons = {
                        positiveButton("선택")
                        negativeButton("취소")
                    }
                ) {
                    title("색상 선택하기")
                    colorChooser(colors = ColorPalette.Primary) {
                        timerColor = it
                    }
                }
                Text(
                    text = "타이머 색상",
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(start = 10.dp),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
    }
}

@Composable
fun DialogAndShowButton(
    selectedColor: Color,
    buttons: @Composable MaterialDialogButtons.() -> Unit = {},
    content: @Composable MaterialDialogScope.() -> Unit
) {
    val dialogState = rememberMaterialDialogState()

    MaterialDialog(
        dialogState = dialogState,
        buttons = buttons,
        backgroundColor = MaterialTheme.colors.background,
        shape = RoundedCornerShape(15.dp)
    ) {
        content()
    }
    OutlinedButton(
        onClick = { dialogState.show() },
        modifier = Modifier
            .size(40.dp),
        shape = RoundedCornerShape(100),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = selectedColor
        ),
        border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
    ) {

    }
}


@Composable
fun ButtonAddWork(modifier: Modifier) {
    Button(
        onClick = {

        },
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.primary
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Image(
            ImageVector.vectorResource(id = R.drawable.ic_baseline_add),
            "저장",
            colorFilter = ColorFilter.tint(MaterialTheme.colors.primary),
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = "저장하기",
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Medium)
        )
    }
}
