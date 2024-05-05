package com.example.app21try6

import androidx.databinding.InverseMethod
import androidx.room.TypeConverter
import java.text.NumberFormat
import java.util.*

object Converter3{
    @InverseMethod("textToDecimal")
    @JvmStatic
    fun decimalToText(number: Double): String {
        val localeID = Locale("in", "ID")
        val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)

        return formatRupiah.format(number.toInt())
    }
    @JvmStatic
    fun textToDecimal(num: String): Double
    {

        return try {
            num.toDouble()
        } catch (ex: Exception) {
            0.0
        }

    }
    @TypeConverter
    @JvmStatic
    fun toDate(timestamp: Long): Date {
        return Date(timestamp)
    }

    @TypeConverter
    @JvmStatic
    fun toTimestamp(date: Date): Long {
        return date.time
    }
}

