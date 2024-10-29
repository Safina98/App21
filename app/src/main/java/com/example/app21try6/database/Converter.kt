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
        private val fullFormatter = SimpleDateFormat(fullDateFormat, Locale.getDefault())
        private val simpleFormatter = SimpleDateFormat(simpleDateFormat, Locale.getDefault())

        @JvmStatic
        @TypeConverter
        fun toDate(dateString: String?): Date? {
            return dateString?.let {
                try {
                    fullFormatter.parse(it)
                } catch (e: ParseException) {
                    // Try parsing with the simpler format if the full format fails
                    try {
                        simpleFormatter.parse(it)
                    } catch (e2: ParseException) {
                        e2.printStackTrace()
                        null
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

