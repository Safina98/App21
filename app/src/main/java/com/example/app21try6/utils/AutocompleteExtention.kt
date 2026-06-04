package com.example.app21try6.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView

fun AutoCompleteTextView.setupDropdown(
    onItemSelected: (String) -> Unit
) {
    var isItemSelected = false
    var isKeyboardOpen = false

    showSoftInputOnFocus = true

    rootView.viewTreeObserver.addOnGlobalLayoutListener {
        val rect = android.graphics.Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = rootView.height
        val keypadHeight = screenHeight - rect.bottom
        isKeyboardOpen = keypadHeight > screenHeight * 0.15
    }

    post {

    }

    setOnClickListener {
        isItemSelected = false
        showDropDown()
    }

    setOnFocusChangeListener { _, hasFocus ->
        if (hasFocus) {
            isItemSelected = false
            showDropDown()
        }
    }

    setOnDismissListener {
        if (isItemSelected) return@setOnDismissListener
        if (isKeyboardOpen) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(windowToken, 0)
            postDelayed({ showDropDown() }, 300)
        }
    }

    setOnItemClickListener { parent, _, position, _ ->
        val selected = parent.getItemAtPosition(position).toString()
        onItemSelected(selected)
        isItemSelected = true
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
        clearFocus()
    }
}