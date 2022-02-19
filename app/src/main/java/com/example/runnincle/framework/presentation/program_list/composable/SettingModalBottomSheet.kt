package com.example.runnincle.framework.presentation.program_list.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.runnincle.framework.presentation.composable.CustomMaterialDialog
import com.example.runnincle.framework.presentation.create_program.composable.LastCoolDownSkipOptionField
import com.example.runnincle.framework.presentation.create_program.composable.NumberPickerOutlineTextField
import com.example.runnincle.framework.presentation.create_program.composable.TimerColorPickerField
import com.vanpra.composematerialdialogs.*

@Composable
fun SettingModalBottomSheet(
    overlaySize: MutableState<Int>,
    totalTimerColor: MutableState<Color>,
    isTtsUsed: MutableState<Boolean>,
    onAdRemoveClick: ()->Unit,
    onSaveClick: ()->Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .background(
            brush = Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colors.primary,
                    MaterialTheme.colors.primaryVariant
                )
            ),
        )
        .padding(30.dp, 40.dp, 30.dp, 20.dp)
    ) {
        NumberPickerOutlineTextField(
            number = overlaySize,
            numberRange = 1..10,
            leadingText = "타이머 사이즈",
        )
        TimerColorPickerField(
            leadingText = "타이머 전체 진행도 색상",
            timerColor = totalTimerColor,
        )
        LastCoolDownSkipOptionField(
            leadingText = "TTS 사용여부",
            enabled = true,
            checked = isTtsUsed,
            onCheckedChange = { isChecked ->
                isTtsUsed.value = isChecked
            },
            onFocused = {
                isTtsUsed.value = !isTtsUsed.value
            }
        )
        BottomSheetButton(
            text = "광고제거",
            onButtonClick = onAdRemoveClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp)
                .height(55.dp)
        )
        BottomSheetButton(
            text = "저장하기",
            onButtonClick = onSaveClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        )
    }
}

@Composable
fun BottomSheetButton(
    text: String,
    onButtonClick: ()-> Unit,
    modifier: Modifier
) {
    Button(
        onClick = onButtonClick,
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = MaterialTheme.colors.onPrimary,
            contentColor = MaterialTheme.colors.primary
        ),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
fun AdRemoveDialog(
    dialogState: MaterialDialogState,
    onRemoveAdFor3DaysClick: ()-> Unit,
    onRemoveAdForeverClick: ()-> Unit,
) {
    CustomMaterialDialog(
        dialogState = dialogState,
        backgroundColor = MaterialTheme.colors.background,
        shape = RoundedCornerShape(20.dp),
        dialogWidthWeight = 0.8f
    ) {
        title("광고제거")
        customView {
            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                Button(
                    onClick = onRemoveAdFor3DaysClick,
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(bottom = 15.dp).fillMaxWidth().height(50.dp)
                ) {
                    Text(
                        text = "광고보고 3일동안 광고제거",
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Medium)
                    )
                }
                Button(
                    onClick = onRemoveAdForeverClick,
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = MaterialTheme.colors.primary,
                        contentColor = MaterialTheme.colors.onPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text(
                        text = "3900원으로 영원히 광고제거",
                        style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Medium)
                    )
                }
            }
        }
    }
}