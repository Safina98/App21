package com.example.app21try6.grafik

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.app21try6.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ChartRenderer(private val context: Context) {

    fun showChart(barChart: BarChart, dataMap: Map<String, Double>) {
        val entries = ArrayList<BarEntry>()
        val labels = mutableListOf<String>()

        dataMap.entries.forEachIndexed { index, entry ->
            val key = entry.key
            val value = entry.value.toFloat()
            val label = entry.key
            entries.add(BarEntry(index.toFloat(), value))
            labels.add(label)
        }

        val dataSet = BarDataSet(entries, "Sample Data")
        dataSet.color = ContextCompat.getColor(context, R.color.pastel_green_dark)
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.labelRotationAngle = 90f
        xAxis.granularity = 0.5f
        xAxis.textSize = 16f
        val yAxis: YAxis = barChart.axisLeft
        yAxis.textSize = 16f
        val barData = BarData(dataSet)
        barChart.data = barData
        barChart.setFitBars(true)
        barChart.invalidate()
    }
}
