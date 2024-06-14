package com.example.app21try6.database

import android.util.Log
import androidx.room.TypeConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateTypeConverter {
    companion object {
        private const val dateFormat = "yyyy-MM-dd"
        private val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        @TypeConverter
        @JvmStatic
        fun fromDate(date: Date?): String? {
            return date?.let { formatter.format(it) }
        }

        @TypeConverter
        @JvmStatic

        fun toDate(dateString: String?): Date? {
            return dateString?.let { formatter.parse(it) }
        }


    }
}

