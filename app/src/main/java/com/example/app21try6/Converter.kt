package com.example.app21try6

import androidx.databinding.InverseMethod
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
}