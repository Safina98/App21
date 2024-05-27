package com.example.app21try6

import java.text.NumberFormat
import java.util.*
import kotlin.math.absoluteValue

fun formatRupiah(number: Double?): String? {
    val localeID = Locale("in", "ID")
    val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
    formatRupiah.maximumFractionDigits = 0
    return if (number != null) {
        formatRupiah.format(number)
    } else {
        formatRupiah.format(0.0) // or any default value you prefer
    }
}