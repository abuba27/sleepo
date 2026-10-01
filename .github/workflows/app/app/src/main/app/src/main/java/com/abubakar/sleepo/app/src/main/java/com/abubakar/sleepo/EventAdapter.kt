package com.abubakar.sleepo

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object EventFormatter {
    private val dateFmt = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
    private val timeFmt = SimpleDateFormat("hh:mm:ss a", Locale.getDefault())

    fun formatDate(ts: Long): String = dateFmt.format(Date(ts))
    fun formatTime(ts: Long): String = timeFmt.format(Date(ts))
}
