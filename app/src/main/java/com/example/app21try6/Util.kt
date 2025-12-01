package com.example.app21try6

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.app21try6.transaction.transactionselect.TransSelectModel
import com.example.app21try6.utils.MasterSyncWorker
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

fun scheduleImmediateSync(context: Context) {
    // 1. Define Constraints: Requires network connectivity
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    // 2. Define the One-Time work request
    val syncRequest = OneTimeWorkRequest.Builder(MasterSyncWorker::class.java) // FIX 1 & 2
        .setConstraints(constraints)
        .setBackoffCriteria(
            BackoffPolicy.LINEAR,
            WorkRequest.MIN_BACKOFF_MILLIS, // FIX 3
            TimeUnit.MILLISECONDS
        )
        .build()

    // 3. Enqueue the work (Unique to avoid scheduling duplicates)
    WorkManager.getInstance(context).enqueueUniqueWork(
        "RealtimeDatabaseImmediateSync",
        ExistingWorkPolicy.KEEP, // If unsynced data is saved again, replace the previous scheduled sync.
        syncRequest
    )
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
//trans detail and trans sum csv
fun parseDate(dateString: String): Date? {
    // Specify the format pattern
    val pattern = "EEE MMM dd HH:mm:ss zzz yyyy"
    // Create a SimpleDateFormat instance with the specified pattern and locale
    val dateFormat = SimpleDateFormat(pattern, Locale.ENGLISH)

    return try {
        // Parse the date string into a Date object
        dateFormat.parse(dateString)
    } catch (e: ParseException) {
        // Handle the exception if the date string is unparseable
        e.printStackTrace()
        Log.i("INSERTCSVPROB","parse date catch: $e")
        null
    }
}
// trans detail and trans sum insert csv
fun getDate(dateString:String?):Date?{
    val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return if (dateString!==null)inputFormat.parse(dateString) else null
}

fun calculatePriceByQty(qty:Double,defaultPrice:Int):Int{
    Log.i("DialogUtilProblems","calculatePriceByQtyCalled")
    val price=  when {
        qty > 0.0 && qty < 0.35 -> {
            defaultPrice + 9000
        }
        qty >= 0.35 && qty < 0.9 -> {
            defaultPrice + if ((defaultPrice / 1000) % 2 == 0) 6000 else 5000
        }
        else -> defaultPrice
    }
    Log.i("DialogUtilProblems","Qty ${qty} Price ${formatRupiah(price.toDouble())}")
    return price
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
fun formatDateRange(startDate: Date?, endDate: Date?): String {
    return if (startDate != null && endDate != null) {
        val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("in", "ID"))
        val startDateString = dateFormat.format(startDate)
        val endDateString = dateFormat.format(endDate)
        "$startDateString - $endDateString"
    } else {
        "Pilih Tanggal"
    }
}