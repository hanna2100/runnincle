package com.devhanna91.runnincle.framework.presentation.program_list.composable

import androidx.compose.animation.defaultDecayAnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.devhanna91.runnincle.R
import com.devhanna91.runnincle.framework.datasource.cache.model.Language
import com.devhanna91.runnincle.framework.presentation.composable.CustomMaterialDialog
import com.devhanna91.runnincle.framework.presentation.create_program.composable.LastCoolDownSkipOptionField
import com.devhanna91.runnincle.framework.presentation.create_program.composable.NumberPickerOutlineTextField
import com.devhanna91.runnincle.framework.presentation.create_program.composable.TimerColorPickerField
import com.devhanna91.runnincle.ui.theme.DarkRippleTheme
import com.devhanna91.runnincle.ui.theme.WhiteRippleTheme
import com.siddroid.holi.colors.MaterialColor
import com.vanpra.composematerialdialogs.*

@Composable
fun SettingModalBottomSheet(
    overlaySize: MutableState<Int>,
    totalTimerColor: MutableState<Color>,
    coolDownTimerColor: MutableState<Color>,
    isTtsUsed: MutableState<Boolean>,
    defaultLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
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
            leadingText = stringResource(id = R.string.timer_size),
        )
        TimerColorPickerField(
            leadingText = stringResource(id = R.string.total_timer_color),
            timerColor = totalTimerColor,
        )
        TimerColorPickerField(
            leadingText = stringResource(id = R.string.cool_down_timer_color),
            timerColor = coolDownTimerColor,
        )
        LanguagePickerField(
            leadingText = stringResource(id = R.string.language),
            defaultLanguage = defaultLanguage,
            onLanguageSelected = onLanguageSelected,
        )
//        LastCoolDownSkipOptionField(
//            leadingText = stringResource(id = R.string.is_used_tts),
//            enabled = true,
//            checked = isTtsUsed,
//            onCheckedChange = { isChecked ->
//                isTtsUsed.value = isChecked
//            },
//            onFocused = {
//                isTtsUsed.value = !isTtsUsed.value
//            }
//        )
        BottomSheetButton(
            text = stringResource(id = R.string.remove_ad),
            onButtonClick = onAdRemoveClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp)
                .height(55.dp)
        )
        BottomSheetButton(
            text = stringResource(id = R.string.save),
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
    CompositionLocalProvider(LocalRippleTheme provides DarkRippleTheme) {
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
}


@Composable
fun LanguagePickerField(
    leadingText: String,
    defaultLanguage: Language,
    onLanguageSelected: (Language) -> Unit,
) {
    val focusRequester = FocusRequester()
    var expanded by remember { mutableStateOf(false) }
    var selectedLanguage = remember { defaultLanguage }
    val suggestions = listOf(Language.KO, Language.EN)
    OutlinedTextField(
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusEvent {
                if (it.hasFocus) {
                    expanded = true
                }
            }
            .fillMaxWidth()
            .padding(bottom = 15.dp),
        leadingIcon = {
            Text(
                modifier = Modifier.padding(start = 20.dp),
                text = leadingText,
                color = MaterialTheme.colors.onSecondary,
            )
        },
        trailingIcon = {
            Column(modifier = Modifier.padding(end = 20.dp)) {
                Button(
                    onClick = { expanded = !expanded },
                ){
                    Text (selectedLanguage.value)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    suggestions.forEach { language ->
                        DropdownMenuItem(onClick = {
                            expanded = false
                            selectedLanguage = language
                            onLanguageSelected(language)
                        }) {
                            Text(text = language.value)
                        }
                    }
                }
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
fun deleteProgramDialog(
    dialogState: MaterialDialogState,
    programName: String,
    onConfirmClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    CustomMaterialDialog(
        dialogState = dialogState,
        backgroundColor = MaterialTheme.colors.surface,
        shape = RoundedCornerShape(20.dp),
        dialogWidthWeight = 0.8f,
        elevation = 0.dp
    ) {
        title(stringResource(id = R.string.delete_timer))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                text = stringResource(id = R.string.delete_timer_confirm, programName),
                style = MaterialTheme.typography.subtitle1.copy(
                    color = MaterialTheme.colors.secondary
                ),
                modifier = Modifier.padding(30.dp, 10.dp, 30.dp, 30.dp)
            )
            Row {
                CompositionLocalProvider(LocalRippleTheme provides WhiteRippleTheme) {
                    Button(
                        onClick = onCancelClick,
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = MaterialColor.GREY_400,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                    Button(
                        onClick = onConfirmClick,
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = MaterialTheme.colors.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.confirm),
                            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }
        }
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
        title(stringResource(id = R.string.remove_ad))
        customView {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                CompositionLocalProvider(LocalRippleTheme provides WhiteRippleTheme) {
                    Button(
                        onClick = onRemoveAdFor3DaysClick,
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = MaterialTheme.colors.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .padding(bottom = 15.dp)
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.remove_ad_for_3days),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.remove_ad_forever),
                            style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Medium)
                        )
                    }
                }
            }
        }
    }
}