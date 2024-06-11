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
fun getDateFromComponents(year: Int, month: String, monthNumber: Int, day: Int, dayName: String): Date {
    // Create a map for month names to month numbers (if needed)
    val monthMap = mapOf(
        "Januari" to Calendar.JANUARY,
        "Februari" to Calendar.FEBRUARY,
        "Maret" to Calendar.MARCH,
        "April" to Calendar.APRIL,
        "Mei" to Calendar.MAY,
        "Juni" to Calendar.JUNE,
        "Juli" to Calendar.JULY,
        "Agustus" to Calendar.AUGUST,
        "September" to Calendar.SEPTEMBER,
        "Oktober" to Calendar.OCTOBER,
        "November" to Calendar.NOVEMBER,
        "Desember" to Calendar.DECEMBER
    )
    // Get the month number from the map
    val monthNum = monthMap[month] ?: monthNumber - 1 // Use monthNumber if month name is not in the map

    // Create a Calendar instance and set the date
    val calendar = Calendar.getInstance()
    calendar.set(year, monthNum, day)

    // Return the Date object
    return calendar.time
}