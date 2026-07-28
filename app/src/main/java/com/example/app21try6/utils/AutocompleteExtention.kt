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

/*
* fun AutoCompleteTextView.setupDropdown(
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
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
        clearFocus()
    }

    // Real-time check: is the IME actually visible right now?
    fun isImeVisible(): Boolean {
        val insets = ViewCompat.getRootWindowInsets(rootView) ?: return false
        return insets.isVisible(WindowInsetsCompat.Type.ime())
    }

    // Called from the host's OnBackPressedCallback.
    // Returns true if this consumed the back press, false if the
    // caller should fall through to default back behavior.
    return handleBack@{
        Log.i("Autocompleteprobs","retun handle back is called")
        when {
            isImeVisible() -> {
                Log.i("Autocompleteprobs","is ime visible")
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(windowToken, 0)
                true
            }
            isPopupShowing -> {
                Log.i("Autocompleteprobs","is popup showing")
                dismissDropDown()
                true
            }
            else -> {
                Log.i("Autocompleteprobs","else")
                false}
        }
    }
}*/