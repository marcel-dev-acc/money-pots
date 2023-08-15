package com.marcelm.myfinances.ui.methods

import java.text.SimpleDateFormat
import java.util.*

fun convertDateToEpochTimeMs(dateString: String): Long {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = dateFormat.parse(dateString)
    return date?.time ?: -1L
}

fun convertEpochTimeMsToDate(epochTime: Long): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val date = Date(epochTime * 1000)
    return dateFormat.format(date)
}