package com.example.runnincle.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.runnincle.R

val infinitySansFamily = FontFamily(
    Font(R.font.infinity_sans_regular, FontWeight.Normal),
    Font(R.font.infinity_sans_regular, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.infinity_sans_bold, FontWeight.Medium),
    Font(R.font.infinity_sans_cond_bold, FontWeight.Bold)
)

val InfinitySansTypography = Typography(
    h1 =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Light,
        fontSize = 96.sp,
        letterSpacing = (-1.5).sp
    ),
    h2 =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Light,
        fontSize = 60.sp,
        letterSpacing = (-0.5).sp
    ), 
    h3 =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        letterSpacing = 0.sp
    ),
    h4 =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 34.sp,
        letterSpacing = 0.25.sp
    ),
    h5 =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    h6 =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.15.sp
    ),
    subtitle1 =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    subtitle2 =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    ),
    body1 =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    ),
    body2 =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    ),
    button =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 1.25.sp
    ),
    caption =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    ),
    overline =  TextStyle(
        fontFamily = infinitySansFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp,
        letterSpacing = 1.5.sp
    ),
)

