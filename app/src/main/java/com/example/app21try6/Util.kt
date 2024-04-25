package com.example.app21try6

import java.text.NumberFormat
import java.util.*

fun formatRupiah(number: Double?): String? {
    val localeID = Locale("in", "ID")
    val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
    return if (number != null) {
        formatRupiah.format(number)
    } else {
        formatRupiah.format(0.0) // or any default value you prefer
    }
}