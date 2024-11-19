package com.example.app21try6.database

import android.util.Log
import androidx.room.TypeConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateTypeConverter {
    companion object {
        private const val fullDateFormat = "yyyy-MM-dd HH:mm"
        private const val simpleDateFormat = "yyyy-MM-dd"
        private val fullFormatter = SimpleDateFormat(fullDateFormat, Locale.getDefault()).apply { isLenient = false }
        private val simpleFormatter = SimpleDateFormat(simpleDateFormat, Locale.getDefault()).apply { isLenient = false }

        @JvmStatic
        @TypeConverter
        fun toDate(dateString: String?): Date? {
            return dateString?.let {
                try {
                    fullFormatter.parse(it)
                } catch (e: ParseException) {
                    // Log and try simpler format
                    println("Full date format failed, trying simple date format. Error: ${e.message}")
                    try {
                        simpleFormatter.parse(it)
                    } catch (e2: ParseException) {
                        println("Simple date format also failed. Error: ${e2.message}")
                        null  // Return null if parsing fails
                    }
                }
            }
        }

        @JvmStatic
        @TypeConverter
        fun fromDate(date: Date?): String? {
            return date?.let {
                fullFormatter.format(it)
            }
        }
    }
}


