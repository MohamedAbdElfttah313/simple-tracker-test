package com.example.secquralsetask.UtilAndSystemServices

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimeGetter {

    fun getFormattedTimeStamp(locale: Locale = Locale.getDefault()) : String{
        return SimpleDateFormat("hh:mm:ss aa" , locale).run {
            format(Calendar.getInstance().time)
        }
    }

    fun getFormattedDay(locale: Locale = Locale.getDefault()) : String{
        return SimpleDateFormat("dd-MM-yyyy" , locale).run {
            format(Calendar.getInstance().time)
        }
    }
}