package com.ikhokha.techcheck.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateHelper {

    private const val CURRENT_DATE_FORMAT = "EEE MMM dd HH:mm:ss"

    fun dateFormat(mDate: Long): String {
        val format = SimpleDateFormat(CURRENT_DATE_FORMAT, Locale.ENGLISH)
        val date = Date(mDate)
        return format.format(date)
    }
}