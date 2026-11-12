package com.example.app21try6.utils
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.ListView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout


fun <T> setupMultiSelect(
    context: Context,
    inputLayout: TextInputLayout?,
    editText: TextInputEditText?,
    items: List<T>,
    label: (T) -> String,
    getChecked: (T) -> Boolean,
    setChecked: (T, Boolean) -> Unit,
    onChanged: (List<T>) -> Unit
) {
    fun updateText() {
        val selected = items.filter(getChecked)
        editText?.setText(
            when {
                selected.isEmpty() -> ""
                selected.size <= 2 -> selected.joinToString(", ") { label(it) }
                else -> "${selected.size} selected"
            }
        )
    }
    updateText()

    editText?.setOnClickListener {
        val popup = ListPopupWindow(context)
        popup.anchorView = inputLayout
        popup.width = inputLayout?.width?:0
        popup.isModal = true
        popup.setAdapter(
            ArrayAdapter(
                context,
                android.R.layout.simple_list_item_multiple_choice,
                items.map(label)
            )
        )
        popup.setOnItemClickListener { _, _, position, _ ->
            // ListView toggles the check state before this is called
            setChecked(items[position], popup.listView!!.isItemChecked(position))
            updateText()
            onChanged(items.filter(getChecked))
        }
        popup.show()

        // must be set after show(), when listView exists
        popup.listView?.apply {
            choiceMode = ListView.CHOICE_MODE_MULTIPLE
            items.forEachIndexed { i, item -> setItemChecked(i, getChecked(item)) }
        }
    }
}