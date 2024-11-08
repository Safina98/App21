package com.example.app21try6

import android.graphics.drawable.ColorDrawable
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.example.app21try6.database.tables.TransactionDetail
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date

import android.content.res.Configuration
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView


@BindingAdapter("textVisibility")
fun TextView.setTextVisibility(text: String?) {
    visibility = if (text.isNullOrEmpty()) {
        View.GONE
    } else {
        View.VISIBLE
    }
}
@BindingAdapter("cardViewVisibility")
fun CardView.setCardVisibility(bool: Boolean){
    visibility = if (bool ==false) {
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
@BindingAdapter("viewVisibility")
fun seViewVisibility(view: View,bool: Boolean) {
    view.visibility = if (bool ==true) { View.GONE } else { View.VISIBLE
    }
}
@BindingAdapter("app:noteColor")
fun setNoteColor(imageView: ImageView, isActive: Boolean) {
    val drawableRes = if (isActive) {
        R.drawable.sticky_note_active
    } else {
        R.drawable.sticky_note_inactive
    }
    imageView.setImageResource(drawableRes)
}

@BindingAdapter("textVisibilityDetail")
fun setTextVisibilityDetail(textView: TextView,item: TransactionDetail) {
    val decimalFormat = DecimalFormat("#.##")
    textView.text = "(${decimalFormat.format(item.unit_qty)})"
    textView.visibility= if (item.unit.isNullOrEmpty() || item.unit_qty ==1.0) {
        View.GONE
    } else {
        View.VISIBLE
    }
}
@BindingAdapter("dayModeResource", "nightModeResource", requireAll = true)
fun setNightModeResource(imageView: ImageView, dayModeResource: Int, nightModeResource: Int) {
    val nightModeFlags = imageView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    when (nightModeFlags) {
        Configuration.UI_MODE_NIGHT_YES -> {
            imageView.setImageResource(nightModeResource)
        }
        Configuration.UI_MODE_NIGHT_NO -> {
            imageView.setImageResource(dayModeResource)
        }
        Configuration.UI_MODE_NIGHT_UNDEFINED -> {
            imageView.setImageResource(dayModeResource)
        }
    }
}
@BindingAdapter("app:imageBasedOnNightMode")
fun setImageBasedOnNightMode(imageView: ImageView, uiMode: Int) {
    when (uiMode and Configuration.UI_MODE_NIGHT_MASK) {
        Configuration.UI_MODE_NIGHT_YES -> {
            when (imageView.id) {
                R.id.btn_print_new -> imageView.setImageResource(R.drawable.baseline_print_light)
                R.id.btn_edit_trans_new -> imageView.setImageResource(R.drawable.baseline_edit_light)
                R.id.btn_send_new -> imageView.setImageResource(R.drawable.wa_vector_light)
                R.id.btn_is_paid_off->imageView.setImageResource(R.drawable.baseline_calendar_month_light)
            }
        }
        Configuration.UI_MODE_NIGHT_NO, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
            when (imageView.id) {
                R.id.btn_print_new -> imageView.setImageResource(R.drawable.baseline_print_24)
                R.id.btn_edit_trans_new -> imageView.setImageResource(R.drawable.baseline_edit_24)
                R.id.btn_send_new -> imageView.setImageResource(R.drawable.wa_vector)
                R.id.btn_is_paid_off->imageView.setImageResource(R.drawable.baseline_calendar_month_24)
            }
        }
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
    val parent = view.parent as? View
    val parentColor = parent?.background?.let { background ->
        if (background is ColorDrawable) {
            background.color
        } else {
            ContextCompat.getColor(view.context, android.R.color.transparent)
        }
    } ?: ContextCompat.getColor(view.context, android.R.color.transparent)

    val color = if (isPrepared) {
        ContextCompat.getColor(view.context,R.color.pastel_green2)
    } else {
        parentColor
    }
    view.setBackgroundColor(color)
}
@BindingAdapter("buttonBackgroundColor")
fun setButtontBackgroundColor(view: View, isPrepared: Boolean) {
    val color = if (isPrepared) {
        ContextCompat.getColor(view.context,R.color.pastel_green_dark)
    } else {
        ContextCompat.getColor(view.context, R.color.black)
    }
    view.setBackgroundColor(color)
}

@BindingAdapter("applyGradientBackground")
fun Button.applyGradientBackground(isGradient: Boolean) {
    if (isGradient) {
        // Apply the gradient background
        Log.i("bgprobs","isgradient $isGradient")
        this.background = ContextCompat.getDrawable(context, R.drawable.active_btn)
    } else {
        // Apply a black background
        this.background = ColorDrawable(ContextCompat.getColor(context, android.R.color.black))
    }
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