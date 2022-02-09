package com.example.runnincle.business.domain.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateUtil constructor(
    private val dateFormat: SimpleDateFormat
) {

    fun removeTimeFromDateString(sd: String): String{
        return sd.substring(0, sd.indexOf(" "))
    }

    // format: "2019-07-23 HH:mm:ss"
    fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }
}