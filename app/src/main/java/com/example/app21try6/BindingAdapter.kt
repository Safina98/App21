package com.example.app21try6

import android.graphics.Color
import android.view.View
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter

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