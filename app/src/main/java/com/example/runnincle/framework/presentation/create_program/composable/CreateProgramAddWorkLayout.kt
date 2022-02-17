package com.example.runnincle.framework.presentation.create_program.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.runnincle.R
import com.example.runnincle.framework.presentation.composable.CustomMaterialDialog
import com.example.runnincle.framework.presentation.composable.NumberPicker
import com.example.runnincle.framework.presentation.create_program.BottomSheetSaveButtonStatus
import com.example.runnincle.ui.theme.NanumSquareFamily
import com.example.runnincle.ui.theme.TimerColorPalette
import com.siddroid.holi.colors.MaterialColor
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser
import kotlin.math.min

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddWorkLayout(
    modifier: Modifier,
    name: MutableState<String>,
    workMin: MutableState<Int>,
    workSec: MutableState<Int>,
    coolDownMin: MutableState<Int>,
    coolDownSec: MutableState<Int>,
    isSkipLastCoolDown: MutableState<Boolean>,
    set: MutableState<Int>,
    timerColor: MutableState<Color>
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(
        modifier = modifier
    ) {
        val myTextFieldColors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onPrimary,
            disabledTextColor = MaterialTheme.colors.onSecondary,
            backgroundColor = Color.Transparent,
            cursorColor =  MaterialTheme.colors.onPrimary,
            errorCursorColor =  MaterialTheme.colors.onPrimary,
            focusedIndicatorColor = MaterialTheme.colors.onPrimary,
            unfocusedIndicatorColor = MaterialTheme.colors.onSecondary,
        )

        // 이름
        WorkoutNameOutlineTextField(name, keyboardController, myTextFieldColors)
        // 운동 시간
        TimePickerOutlineTextField(workMin, workSec, stringResource(id = R.string.work_time))
        // 쿨다운 시간
        TimePickerOutlineTextField(coolDownMin, coolDownSec, stringResource(id = R.string.cool_down_time))
        // 쿨다운 생략
        LastCoolDownSkipOptionField(
            leadingText = stringResource(id = R.string.skip_last_cool_down),
            checked = isSkipLastCoolDown,
            onCheckedChange = { isChecked ->
                isSkipLastCoolDown.value = isChecked
                println("debug isSkipLastCoolDown = ${isSkipLastCoolDown.value}")
            },
            onFocused = {
                isSkipLastCoolDown.value = true
            }
        )
        // 세트 설정
        SetNumberPickerOutlineTextField(set = set, leadingText = stringResource(id = R.string.set))
        TimerColorPickerField(
            leadingText = stringResource(id = R.string.timer_color),
            timerColor = timerColor
        )
    }
}

@Composable
fun TimerColorPickerField(leadingText: String, timerColor: MutableState<Color>) {
    val focusRequester = FocusRequester()
    val dialogState = rememberMaterialDialogState()
    OutlinedTextField(
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusEvent {
                if (it.hasFocus) {
                    dialogState.show()
                }
            }
            .fillMaxWidth()
            .padding(bottom = 15.dp),
        leadingIcon = {
            Text(
                text = leadingText,
                color = MaterialTheme.colors.onSecondary,
                modifier = Modifier.padding(start = 20.dp)
            )
        },
        trailingIcon = {
            Row {
                ColorPickerDialogButton(
                    selectedColor = timerColor.value,
                    dialogState = dialogState
                ) {
                    title(stringResource(id = R.string.pick_color))
                    colorChooser(
                        colors = TimerColorPalette,
                        initialSelection = TimerColorPalette.indexOf(timerColor.value),
                        waitForPositiveButton = false,
                        onColorSelected = {
                            if (timerColor.value != it) {
                                timerColor.value = it
                                dialogState.hide()
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.width(20.dp))
            }
        },
        value = "",
        onValueChange = { },
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onPrimary,
            disabledTextColor = MaterialTheme.colors.onSecondary,
            backgroundColor = Color.Transparent,
            cursorColor =  Color.Transparent,
            errorCursorColor =  MaterialTheme.colors.onPrimary,
            focusedIndicatorColor = MaterialTheme.colors.onPrimary,
            unfocusedIndicatorColor = MaterialTheme.colors.onSecondary,
        ),
        shape = RoundedCornerShape(20.dp),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
        singleLine = true,
        readOnly = true
    )
}

@Composable
fun SetNumberPickerOutlineTextField(set: MutableState<Int>, leadingText: String) {
    val focusRequester = FocusRequester()
    var label by remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                label = if (it.hasFocus) {
                    "숫자를 스크롤하여 입력"
                } else {
                    ""
                }
            }
            .fillMaxWidth()
            .padding(bottom = 15.dp),
        leadingIcon = {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = leadingText,
                    color = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Text(
                    text = label,
                    color = MaterialTheme.colors.error,
                    style = LocalTextStyle.current.copy(
                        fontSize = 10.sp,
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        },
        trailingIcon = {
            Row {
                NumberPicker(
                    state = remember { set },
                    range = 1..100,
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.onPrimary,
                        fontFamily = NanumSquareFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.width(20.dp))
            }
        },
        value = "",
        onValueChange = { },
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onPrimary,
            disabledTextColor = MaterialTheme.colors.onSecondary,
            backgroundColor = Color.Transparent,
            cursorColor =  Color.Transparent,
            errorCursorColor =  MaterialTheme.colors.onPrimary,
            focusedIndicatorColor = MaterialTheme.colors.onPrimary,
            unfocusedIndicatorColor = MaterialTheme.colors.onSecondary,
        ),
        shape = RoundedCornerShape(20.dp),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
        singleLine = true,
        readOnly = true
    )
}

@Composable
fun LastCoolDownSkipOptionField(
    leadingText: String,
    checked: MutableState<Boolean>,
    onCheckedChange: (Boolean) -> Unit,
    onFocused: () -> Unit
) {
    val focusRequester = FocusRequester()
    OutlinedTextField(
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.hasFocus) {
                    onFocused()
                }
            }
            .fillMaxWidth()
            .padding(bottom = 15.dp),
        leadingIcon = {
            Text(
                text = leadingText,
                color = MaterialTheme.colors.onSecondary,
                modifier = Modifier.padding(start = 20.dp)
            )
        },
        trailingIcon = {
            Switch(
                checked = checked.value,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(end = 20.dp),
                colors = SwitchDefaults.colors(
                    checkedTrackColor = MaterialColor.CYAN_A700,
                    checkedThumbColor = MaterialColor.CYAN_A400,
                    uncheckedTrackColor = MaterialColor.GREY_600,
                    uncheckedThumbColor = MaterialColor.GREY_400,
                )
            )
        },
        value = "",
        onValueChange = { },
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onPrimary,
            disabledTextColor = MaterialTheme.colors.onSecondary,
            backgroundColor = Color.Transparent,
            cursorColor =  Color.Transparent,
            errorCursorColor =  MaterialTheme.colors.onPrimary,
            focusedIndicatorColor = MaterialTheme.colors.onPrimary,
            unfocusedIndicatorColor = MaterialTheme.colors.onSecondary,
        ),
        shape = RoundedCornerShape(20.dp),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
        singleLine = true,
        readOnly = true
    )
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun WorkoutNameOutlineTextField(
    name: MutableState<String>,
    keyboardController: SoftwareKeyboardController?,
    myTextFieldColors: TextFieldColors
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 15.dp),
        leadingIcon = {
            Text(
                text = stringResource(id = R.string.name),
                color = MaterialTheme.colors.onSecondary,
                modifier = Modifier.padding(start = 20.dp)
            )
        },
        value = name.value,
        onValueChange = {
            name.value = it
        },
        colors = myTextFieldColors,
        shape = RoundedCornerShape(20.dp),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
            }
        ),
        singleLine = true
    )
}

@Composable
fun TimePickerOutlineTextField(min: MutableState<Int>, sec: MutableState<Int>, leadingText: String) {
    val focusRequester = FocusRequester()
    var label by remember { mutableStateOf("") }
    OutlinedTextField(
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                label = if (it.hasFocus) {
                    "숫자를 스크롤하여 입력"
                } else {
                    ""
                }
            }
            .fillMaxWidth()
            .padding(bottom = 15.dp),
        leadingIcon = {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = leadingText,
                    color = MaterialTheme.colors.onSecondary,
                    modifier = Modifier.padding(start = 20.dp)
                )
                Text(
                    text = label,
                    color = MaterialTheme.colors.error,
                    style = LocalTextStyle.current.copy(
                        fontSize = 10.sp,
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        },
        trailingIcon = {
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NumberPicker(
                    state = remember { min },
                    range = 0..90,
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.onPrimary,
                        fontFamily = NanumSquareFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                )
                Text(text = " : ", style = TextStyle(
                    color = MaterialTheme.colors.onPrimary,
                    fontFamily = NanumSquareFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                ))
                NumberPicker(
                    state = remember { sec },
                    range = 0..59,
                    textStyle = TextStyle(
                        color = MaterialTheme.colors.onPrimary,
                        fontFamily = NanumSquareFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.width(20.dp))
            }

        },
        value = "",
        onValueChange = { },
        colors = TextFieldDefaults.textFieldColors(
            textColor = MaterialTheme.colors.onPrimary,
            disabledTextColor = MaterialTheme.colors.onSecondary,
            backgroundColor = Color.Transparent,
            cursorColor =  Color.Transparent,
            errorCursorColor =  MaterialTheme.colors.onPrimary,
            focusedIndicatorColor = MaterialTheme.colors.onPrimary,
            unfocusedIndicatorColor = MaterialTheme.colors.onSecondary,
        ),
        shape = RoundedCornerShape(20.dp),
        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
        singleLine = true,
        readOnly = true
    )
}

@Composable
fun ColorPickerDialogButton(
    selectedColor: Color,
    dialogState: MaterialDialogState,
    content: @Composable MaterialDialogScope.() -> Unit
) {
    OutlinedButton(
        onClick = { dialogState.show() },
        modifier = Modifier
            .size(30.dp),
        shape = RoundedCornerShape(30),
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = selectedColor
        ),
        border = BorderStroke(1.dp, MaterialTheme.colors.onPrimary),
    ) { }

    CustomMaterialDialog(
        dialogState = dialogState,
        backgroundColor = MaterialTheme.colors.background,
        shape = RoundedCornerShape(20.dp)
    ) {
        content()
    }
}

@Composable
fun ButtonAddWork(
    modifier: Modifier,
    onSaveClick: ()-> Unit,
    onDeleteClick: (()->Unit)? = null,
    buttonStatus: BottomSheetSaveButtonStatus
) {
    Row(modifier = modifier) {
        if (buttonStatus == BottomSheetSaveButtonStatus.EDIT) {
            Button(
                onClick = {
                    onDeleteClick?.let { it() }
                },
                colors = ButtonDefaults.textButtonColors(
                    backgroundColor = MaterialTheme.colors.onPrimary,
                    contentColor = MaterialTheme.colors.primary
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = stringResource(id = R.string.delete),
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
        Button(
            onClick = onSaveClick,
            colors = ButtonDefaults.textButtonColors(
                backgroundColor = MaterialTheme.colors.onPrimary,
                contentColor = MaterialTheme.colors.primary
            ),
            shape = RoundedCornerShape(20.dp),
            modifier = if (buttonStatus == BottomSheetSaveButtonStatus.EDIT) {
                Modifier
                    .weight(0.7f)
                    .fillMaxHeight()
                    .padding(start = 10.dp)
            } else {
                Modifier
                    .weight(1.0f)
                    .fillMaxHeight()
            }
        ) {
            Text(
                text = stringResource(id = R.string.save),
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Medium)
            )
        }
    }

}
