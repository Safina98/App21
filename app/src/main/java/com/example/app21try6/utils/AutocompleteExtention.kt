package com.example.app21try6.utils

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat




fun AutoCompleteTextView.setupDropdown(
    onItemSelected: (String) -> Unit
): () -> Boolean {
    var isItemSelected = false

    showSoftInputOnFocus = true

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

    setOnItemClickListener { parent, _, position, _ ->
        val selected = parent.getItemAtPosition(position).toString()
        onItemSelected(selected)
        isItemSelected = true
        clearFocus() // triggers IME to close naturally
    }

    // Only called when the dispatcher actually receives the back press
    return {
        if (isPopupShowing) {
            Log.i("Autocompleteprobs","popup showing")
            dismissDropDown()
            true
        } else {
            Log.i("Autocompleteprobs","popup not showing")
            false
        }
    }
}

