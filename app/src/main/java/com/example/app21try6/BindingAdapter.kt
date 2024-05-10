package com.example.app21try6

import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.Date

@BindingAdapter("app:buttonColor")
fun setButtonColor(button: Button, isEnabled: Boolean) {
    val color = if (isEnabled) Color.GREEN else Color.RED

    button.setBackgroundColor(color)
}

@BindingAdapter("backgroundColor")
fun setBackgroundColor(view: View, isPrepared: Boolean) {
    val color = if (isPrepared) {
        ContextCompat.getColor(view.context,R.color.greenn)
    } else {
        ContextCompat.getColor(view.context, R.color.white)
    }
    view.setBackgroundColor(color)
}
@BindingAdapter("buttonBackgroundColor")
fun setButtontBackgroundColor(view: View, isPrepared: Boolean) {
    val color = if (isPrepared) {
        ContextCompat.getColor(view.context,R.color.greenn)
    } else {
        ContextCompat.getColor(view.context, R.color.black)
    }
    view.setBackgroundColor(color)
}

@BindingAdapter("dateFormatted")
fun bindDateFormatted(textView: TextView, date: Date?) {
    if (date != null) {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy") // Change the date format according to your requirements
        val dateString = dateFormatter.format(date)
        textView.text = dateString
    } else {
        textView.text = "Pick a Date"
    }
}
@BindingAdapter("startDatePickerFormat")
fun bindStartDatePickerFormatted(textView: TextView, date: Date?) {
    if (date != null) {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy") // Change the date format according to your requirements
        val dateString = dateFormatter.format(date)
        textView.text = "from $dateString"
    } else {
        textView.text = "Pick Start Date"
    }
}
@BindingAdapter("endDatePickerFormat")
fun bindEndDatePickerFormatted(textView: TextView, date: Date?) {
    if (date != null) {
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy") // Change the date format according to your requirements
        val dateString = dateFormatter.format(date)
        textView.text = "to  $dateString"
    } else {
        textView.text = "Pick End Date"
    }
}