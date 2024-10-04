package com.example.app21try6

import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

val SIMPLE_DATE_FORMAT ="dd/MM/yyyy"
val DATE_FORMAT = SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault())
object DISCTYPE {
    const val CashbackPrinted = "Printed"
    const val CashbackNotPrinted = "Not Printed"

}

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
object ToolbarUtil {
    fun hideToolbarButtons(activity: FragmentActivity) {
        val toolbar = activity.findViewById<Toolbar>(com.example.app21try6.R.id.toolbar)
        val img = activity.findViewById<ImageView>(com.example.app21try6.R.id.delete_image)
        img.visibility = View.VISIBLE
        toolbar.visibility=View.VISIBLE
        val btnLinear = activity.findViewById<ConstraintLayout>(com.example.app21try6.R.id.linear_btn)
        btnLinear.visibility = View.GONE
    }
}