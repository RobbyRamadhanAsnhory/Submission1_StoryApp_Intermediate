package com.example.submissionstoryapp_robbyramadhana_md_07

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

object Complement {
    fun String.withDateFormat(): String {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val date = format.parse(this) as Date
        return DateFormat.getDateInstance(DateFormat.FULL).format(date)
    }
}