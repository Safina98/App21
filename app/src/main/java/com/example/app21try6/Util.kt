package com.example.app21try6

import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

val DETAILED_DATE_FORMAT ="dd-MM-yyyy HH:mm"
val DETAILED_DATE_FORMATTER = SimpleDateFormat(DETAILED_DATE_FORMAT, Locale.getDefault())
val SIMPLE_DATE_FORMAT ="dd-MM-yyyy"
val SIMPLE_DATE_FORMATTER = SimpleDateFormat(SIMPLE_DATE_FORMAT, Locale.getDefault())
object BARANGLOGKET {
    const val masuk = "MASUK"
    const val keluar = "KELUAR"
}
object VIEWOPTIONS {
    const val potrait = "Portrait"
    const val lanscape = "Lanscape"
    const val smallScreen= "Small"
    const val bigScreen="Big"
}
object DISCTYPE {
    const val CashbackPrinted = "Printed"
    const val CashbackNotPrinted = "Not Printed"
    const val UbahHarga ="Ubah Harga"
}
object MODELTYPE {
    const val brand = "Brand"
    const val Product = "Product"
    const val subProduct ="Sub Product"
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
// Define a function to convert month numbers to month names
fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Januari"
        2 -> "Februari"
        3 -> "Maret"
        4 -> "April"
        5 -> "Mei"
        6 -> "Juni"
        7 -> "Juli"
        8 -> "Agustus"
        9 -> "September"
        10 -> "Oktober"
        11 -> "November"
        12 -> "Desember"
        else -> "Invalid Month"
    }
}
fun getMonthNumber(month: String?): String? {
    return when (month) {
        "ALL" -> null
        "Januari" -> "01"
        "Februari" -> "02"
        "Maret" -> "03"
        "April" -> "04"
        "Mei" -> "05"
        "Juni" -> "06"
        "Juli" -> "07"
        "Agustus" -> "08"
        "September" -> "09"
        "Oktober" -> "10"
        "November" -> "11"
        "Desember" -> "12"
        else -> null
    }
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