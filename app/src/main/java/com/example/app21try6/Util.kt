package com.example.app21try6

import java.text.NumberFormat
import java.util.*

fun formatRupiah(number: Double): String? {
    val localeID = Locale("in", "ID")
    val formatRupiah: NumberFormat = NumberFormat.getCurrencyInstance(localeID)
    if(number==null) {
        return formatRupiah.format(0.0)
    }
    else {
        return formatRupiah.format(number)
    }
}