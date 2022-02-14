package com.example.runnincle.business.domain.util

import java.text.SimpleDateFormat
import java.util.*

class DateUtil constructor(
    private val dateFormat: SimpleDateFormat
) {

    fun removeTimeFromDateString(sd: String): String{
        return sd.substring(0, sd.indexOf(" "))
    }

    // format: "yyyy-MM-dd HH:mm:ss"
    fun getCurrentTimestamp(): String {
        return dateFormat.format(Date())
    }
}