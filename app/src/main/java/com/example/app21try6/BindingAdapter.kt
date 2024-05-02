package com.example.app21try6

import android.graphics.Color
import android.widget.Button
import androidx.databinding.BindingAdapter

@BindingAdapter("app:buttonColor")
fun setButtonColor(button: Button, isEnabled: Boolean) {
    val color = if (isEnabled) Color.GREEN else Color.RED

    button.setBackgroundColor(color)
}

