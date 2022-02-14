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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import com.example.runnincle.R
import com.example.runnincle.framework.presentation.composable.OutlineTextField
import com.example.runnincle.framework.presentation.create_program.BottomSheetSaveButtonStatus
import com.vanpra.composematerialdialogs.*
import com.vanpra.composematerialdialogs.color.ColorPalette
import com.vanpra.composematerialdialogs.color.colorChooser


@Composable
fun AddWorkLayout(
    modifier: Modifier,
    name: MutableState<String>,
    workMin: MutableState<String>,
    workSec: MutableState<String>,
    coolDownMin: MutableState<String>,
    coolDownSec: MutableState<String>,
    isSkipLastCoolDown: MutableState<Boolean>,
    set: MutableState<String>,
    timerColor: MutableState<Color>
) {
    Column(
        modifier = modifier
    ) {

        Text(
            text = stringResource(id = R.string.name),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 5.dp),
            textAlign = TextAlign.Start,
            color = MaterialTheme.colors.onPrimary
        )
        OutlineTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            fontSize = 20.sp,
            value = name.value,
            onValueChange = {
                name.value = it
            }
        )
        Column {
            Text(
                text = stringResource(id = R.string.work_time),
                modifier = Modifier
                    .width(132.dp)
                    .padding(bottom = 5.dp),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colors.onPrimary
            )
            Row {
                OutlineTextField(
                    modifier = Modifier
                        .width(50.dp)
                        .padding(bottom = 10.dp),
                    fontSize = 20.sp,
                    value = workMin.value,
                    onValueChange = {
                        if (it.isDigitsOnly() && it.length <= 2) {
                            workMin.value = it
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
                OutlineTextField(
                    modifier = Modifier
                        .width(50.dp)
                        .padding(bottom = 10.dp),
                    fontSize = 20.sp,
                    value = workSec.value,
                    onValueChange = {
                        if (it.isDigitsOnly() && it.length <= 2) {
                            if(it.isBlank()) {
                                workSec.value = ""
                            } else if(it.toInt() <= 60) {
                                workSec.value= it
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
                    text = stringResource(id = R.string.cool_down_time),
                    modifier = Modifier
                        .width(132.dp)
                        .padding(bottom = 5.dp),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.onPrimary
                )
                Row {
                    OutlineTextField(
                        modifier = Modifier
                            .width(50.dp)
                            .padding(bottom = 10.dp),
                        fontSize = 20.sp,
                        value = coolDownMin.value,
                        onValueChange = {
                            if (it.isDigitsOnly() && it.length <= 2) {
                                coolDownMin.value = it
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
                    OutlineTextField(
                        modifier = Modifier
                            .width(50.dp)
                            .padding(bottom = 10.dp),
                        fontSize = 20.sp,
                        value = coolDownSec.value,
                        onValueChange = {
                            if (it.isDigitsOnly() && it.length <= 2) {
                                if(it.isBlank()) {
                                    coolDownSec.value= ""
                                } else if(it.toInt() <= 60) {
                                    coolDownSec.value = it
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
            Row(
                modifier = Modifier.padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isSkipLastCoolDown.value,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = Color.White,
                        unselectedColor = Color.White.copy(alpha = 0.6f),
                        disabledColor = Color.White.copy(alpha = 0.3f)
                    ),
                    onClick = { isSkipLastCoolDown.value = !isSkipLastCoolDown.value },
                )
                Text(
                    text = stringResource(id = R.string.skip_last_cool_down),
                    modifier = Modifier
                        .wrapContentSize(),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.onPrimary
                )
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
                    text = stringResource(id = R.string.set),
                    modifier = Modifier.padding(bottom = 5.dp),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.onPrimary
                )
                OutlineTextField(
                    modifier = Modifier
                        .width(50.dp)
                        .padding(bottom = 10.dp),
                    fontSize = 20.sp,
                    value = set.value,
                    onValueChange = {
                        if (it.isDigitsOnly() && it.length <= 2) {
                            set.value = it
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                DialogAndShowButton(
                    selectedColor = timerColor.value
                ) {
                    title(stringResource(id = R.string.pick_color))
                    colorChooser(
                        colors = ColorPalette.Primary,
                        initialSelection = ColorPalette.Primary.indexOf(timerColor.value),
                        waitForPositiveButton = false,
                        onColorSelected = {
                            if (timerColor.value != it) {
                                timerColor.value = it
                                this.dialogState.hide()
                            }
                        }
                    )
                    customView {
                        Spacer(modifier = Modifier
                            .height(20.dp)
                            .fillMaxWidth())
                    }
                }
                Text(
                    text = stringResource(id = R.string.timer_color),
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
                shape = RoundedCornerShape(10.dp),
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
            shape = RoundedCornerShape(10.dp),
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
