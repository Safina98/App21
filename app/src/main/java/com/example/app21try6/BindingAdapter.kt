package com.example.app21try6

import android.graphics.Color
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.app21try6.database.TransactionDetail
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

@BindingAdapter("textVisibility")
fun TextView.setTextVisibility(text: String?) {
    visibility = if (text.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("textVisibility")
fun TextView.setTextVisibility(bool: Boolean) {
    visibility = if (bool ==false) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("textVisibilityDetail")
fun setTextVisibilityDetail(textView: TextView,item:TransactionDetail) {
    val decimalFormat = DecimalFormat("#.##")
    textView.text = "(${decimalFormat.format(item.unit_qty)})"
    textView.visibility= if (item.unit.isNullOrEmpty() || item.unit_qty ==1.0) {
        View.GONE
    } else {
        View.VISIBLE
    }

}
@BindingAdapter("selectedItemValue")
fun Spinner.setSelectedItemValue(selectedItemValue: String?) {
    val adapter = adapter as? ArrayAdapter<String>
    val position = adapter?.getPosition(selectedItemValue)
    if (position != null && position != AdapterView.INVALID_POSITION) {
        setSelection(position)
    }
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