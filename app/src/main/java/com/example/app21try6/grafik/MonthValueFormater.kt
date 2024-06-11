package com.example.app21try6.grafik

import com.github.mikephil.charting.formatter.ValueFormatter

class MonthValueFormatter : ValueFormatter() {
    private val months = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index >= 0 && index < months.size) {
            months[index]
        } else {
            ""
        }
    }
}