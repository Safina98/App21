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
import java.util.Date

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import java.text.NumberFormat
import java.util.Locale


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

@BindingAdapter("app:logColor")
fun setLogColor(imageView: ImageView, isLogged: Boolean) {
    val drawableRes = if (isLogged) {
        R.drawable.baseline_scissor_active
    } else {
        R.drawable.baseline_scissor_inactive
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

@BindingAdapter("dateFormattedNullable")
fun bindDateFormattedNullale(textView: TextView, date: Date?) {
    if (date != null) {
        val dateString = SIMPLE_DATE_FORMATTER.format(date)
        textView.text = dateString
    } else {
        textView.text = SIMPLE_DATE_FORMATTER.format(Date())
    }
}

@BindingAdapter("dateFormatted")
fun bindDateFormatted(textView: TextView, date: Date?) {
    if (date != null) {
        val dateString = SIMPLE_DATE_FORMATTER.format(date)
        textView.text = dateString
    } else {
        textView.text = "Pick a Date"
    }
}
@BindingAdapter("detailedDateFormatted")
fun bindDetailedDateFormatted(textView: TextView, date: Date?) {
    if (date != null) {
        val dateString = DETAILED_DATE_FORMATTER.format(date)
        textView.text = dateString
    } else {
        textView.text = "Pick a Date"
    }
}
@BindingAdapter("startDatePickerFormat")
fun bindStartDatePickerFormatted(textView: TextView, date: Date?) {
    if (date != null) {
        val dateString = SIMPLE_DATE_FORMATTER.format(date)
        textView.text = "from $dateString"
    } else {
        textView.text = "Pick Start Date"
    }
}
@BindingAdapter("endDatePickerFormat")
fun bindEndDatePickerFormatted(textView: TextView, date: Date?) {
    if (date != null) {
        val dateString = SIMPLE_DATE_FORMAT.format(date)
        textView.text = "to  $dateString"
    } else {
        textView.text = "Pick End Date"
    }
}
// Formatter for Rupiah
private val rupiahFormatOld = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

// Format and display an Int value as Rupiah
@BindingAdapter("rupiahValue")
fun setRupiahValue(editText: EditText, value: Int?) {
    val formattedValue = value?.let {
        "${rupiahFormatOld.format(it.toDouble()).split(",")[0]}" // Remove decimals by splitting at comma
    } ?: "Rp 0"
    if (editText.text.toString() != formattedValue) {
        editText.setText(formattedValue)
    }
}

// Format and display a Double value as Rupiah
@BindingAdapter("rupiahValue")
fun setRupiahValue(editText: EditText, value: Double?) {
    val formattedValue = value?.let {
        "${rupiahFormatOld.format(it.toDouble()).split(",")[0]}" // Remove decimals by splitting at comma
    } ?: "Rp 0"
    if (editText.text.toString() != formattedValue) {
        editText.setText(formattedValue)
    }
}


// Retrieve the raw double value from formatted text
@InverseBindingAdapter(attribute = "rupiahValue", event = "rupiahValueAttrChanged")
fun getRupiahValue(editText: EditText): Double {
    val text = editText.text.toString()
    return try {
        val cleanedText = text.replace("[Rp,.\\s]".toRegex(), "")
        cleanedText.toDouble()
    } catch (e: NumberFormatException) {
        0.0
    }
}

// Listener for data binding to notify view model on text change
private val rupiahFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID")).apply {
    maximumFractionDigits = 0
}
@BindingAdapter("rupiahValueAttrChanged")
fun setRupiahValueListener(editText: EditText, listener: InverseBindingListener?) {
    editText.addTextChangedListener(object : TextWatcher {
        private var current = ""
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            if (s.toString() != current) {
                editText.removeTextChangedListener(this)

                val cleanString = s.toString().replace("[^\\d]".toRegex(), "")
                val parsed = cleanString.toDoubleOrNull() ?: 0.0

                current = if (cleanString.isEmpty()) "" else rupiahFormat.format(parsed)
                editText.setText(current)
                editText.setSelection(current.length)

                editText.addTextChangedListener(this)
            }
            listener?.onChange()
        }
    })
}

@BindingAdapter("doubleText")
fun setDoubleText(editText: EditText, value: Double?) {
    // Only update if the value is different from the current parsed value
    val currentValue = editText.text.toString().toDoubleOrNull() ?: 0.0
    if (value != currentValue) {
        val displayText = if (value == 0.0 || value == null) "" else value.toString()
        if (editText.text.toString() != displayText) {
            // Save selection
            val selection = editText.selectionStart
            val textLength = editText.text.length

            editText.setText(displayText)

            // Restore selection (if at end, keep at end)
            editText.setSelection(
                if (selection == textLength) editText.text.length
                else selection.coerceIn(0, editText.text.length)
            )
        }
    }
}

@InverseBindingAdapter(attribute = "doubleText")
fun getDoubleText(editText: EditText): Double {
    return editText.text.toString().toDoubleOrNull() ?: 0.0
}

@BindingAdapter("doubleTextAttrChanged")
fun setDoubleTextListener(editText: EditText, listener: InverseBindingListener?) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            listener?.onChange()
        }
    })
}

@BindingAdapter("intText")
fun setIntText(editText: EditText, value: Int?) {
    val displayText = if (value == 0 || value == null) "" else value.toString()
    if (editText.text.toString() != displayText) {
        editText.setText(displayText)
    }
}

@InverseBindingAdapter(attribute = "intText")
fun getIntText(editText: EditText): Int {
    return editText.text.toString().toIntOrNull() ?: 0
}

@BindingAdapter("intTextAttrChanged")
fun setIntTextListener(editText: EditText, listener: InverseBindingListener?) {
    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
            listener?.onChange()
        }
    })
}

@BindingAdapter("isVisible")
fun setTextTotalTransVisibility(view: View, isVisible: Boolean) {
    view.visibility = if (isVisible) View.VISIBLE else View.GONE
}