package com.example.runnincle.framework.presentation.program_list.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.runnincle.ui.theme.NanumSquareFamily
import com.google.accompanist.flowlayout.FlowRow
import com.google.accompanist.flowlayout.SizeMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProgramSearchBar(
    searchButtonState: Boolean,
    searchString: String,
    onSearchTextFieldValueChange: (String)->Unit,
    onSearchButtonClick: ()->Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val scope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        AnimatedVisibility(visible = !searchButtonState) {
            Column(
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.h4.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    ),
                )
                Text(
                    text = "workouts",
                    style = MaterialTheme.typography.h6.copy(
                        color = Color.White,
                    ),
                    modifier = Modifier.offset(y = -10.dp)
                )
            }
        }
        Row (
            modifier = Modifier
                .height(46.dp)
                .fillMaxWidth()
                .offset(y = 10.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .border(
                        border = BorderStroke(1.dp, Color.White),
                        shape = RoundedCornerShape(40.dp)
                    )
            ) {
                AnimatedVisibility(searchButtonState) {
                    val configuration = LocalConfiguration.current
                    val textFieldWidth = configuration.screenWidthDp.dp - 150.dp
                    val keyboardController = LocalSoftwareKeyboardController.current
                    BasicTextField(
                        value = searchString,
                        onValueChange = {
                            onSearchTextFieldValueChange(it)
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .padding(20.dp, 10.dp, 20.dp, 4.dp)
                            .fillMaxHeight()
                            .width(textFieldWidth)
                        ,
                        textStyle = MaterialTheme.typography.h6.copy(
                            color = Color.White,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Start
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        cursorBrush = SolidColor(Color.White),
                        singleLine = true,
                        maxLines = 1,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                onSearchButtonClick()
                            }
                        ),
                    )
                }
                IconButton(
                    onClick = {
                        scope.launch {
                            onSearchButtonClick()
                            // 키보드 오픈
                            if(!searchButtonState) {
                                delay(500) // AnimatedVisibility 효과 기다림
                                focusRequester.requestFocus()
                            }
                        }
                    }
                ) {
                    Icon (
                        modifier = Modifier
                            .size(30.dp),
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

data class SearchChip(
    var text: String
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ChipGroup(
    items: List<SearchChip>,
    selectedChipIndex: MutableState<Int>,
    modifier: Modifier = Modifier,
    onChipClick: (chipText:String) -> Unit,
    onChipDeleteButtonClick: (SearchChip) -> Unit
) {
    FlowRow(
        modifier = modifier,
        mainAxisSize = SizeMode.Expand,
    ) {
        items.forEachIndexed { index, chip ->
            ChipItem(
                item = chip,
                itemIndex = index,
                selectIndex = selectedChipIndex,
                onChipClick = onChipClick,
                onChipDeleteButtonClick = onChipDeleteButtonClick
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChipItem(
    item: SearchChip,
    itemIndex: Int,
    selectIndex: MutableState<Int>,
    onChipClick: (chipText: String) -> Unit,
    onChipDeleteButtonClick: (SearchChip)->Unit
) {
    val backgroundColor = if (selectIndex.value == itemIndex) {
        MaterialTheme.colors.background
    } else {
        Color.Transparent
    }
    val textColor = if (selectIndex.value == itemIndex) {
        MaterialTheme.colors.primary
    } else {
        Color.White
    }
    val borderColor = if (selectIndex.value == itemIndex) {
        MaterialTheme.colors.primary
    } else {
        Color.White
    }
    Box(
        modifier = Modifier
            .padding(6.dp)
            .background(backgroundColor, CircleShape)
            .clip(CircleShape)
            .border(
                border = BorderStroke(1.dp, borderColor),
                shape = CircleShape
            )
            .clickable {
                // 한번 더 클릭 할 경우 검색 비활성화
                if (selectIndex.value == itemIndex) {
                    selectIndex.value = -1
                    onChipClick("")
                } else {
                    selectIndex.value = itemIndex
                    onChipClick(item.text)
                }
            }
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .padding(end = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = item.text,
                color = textColor,
                style = TextStyle(
                    fontFamily = NanumSquareFamily,
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(8.dp, 5.dp),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            IconButton(
                onClick = {
                    onChipDeleteButtonClick(item)
                },
                modifier = Modifier
                    .size(12.dp)
                    .offset(10.dp)
            ) {
                Icon (
                    modifier = Modifier
                        .size(12.dp)
                        .offset(-(5).dp),
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = textColor
                )
            }
        }
    }
}