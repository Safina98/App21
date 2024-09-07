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

        @JvmStatic
        @TypeConverter
        fun toDate(dateString: String?): Date? {
            return dateString?.let {
                try {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)
                } catch (e: ParseException) {
                    e.printStackTrace()
                    null
                }
            }
        }
        @JvmStatic
        @TypeConverter
        fun fromDate(date: Date?): String? {
            return date?.let {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(it)
            }
        }
    }
}

