package com.example.app21try6.grafik

import com.example.app21try6.R
import androidx.core.content.ContextCompat
import com.example.app21try6.database.models.BarChartModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

object BarChartUtils {
    fun setup(
        barChart: BarChart,
        data: List<BarChartModel>,
        chartLabel: String = ""
    ) {
        barChart.clear()
        barChart.notifyDataSetChanged()

        if (data.isEmpty()) {
            barChart.setNoDataText("Tidak ada data")
            barChart.invalidate()
            return
        }

        val entries = data.mapIndexed { index, item ->
            BarEntry(index.toFloat(), item.value.toFloat())
        }

        val labels = data.map { it.label }

        val dataSet = BarDataSet(entries, chartLabel).apply {
            color = ContextCompat.getColor(barChart.context, R.color.pastel_green_dark)
            valueTextSize = 10f
            setDrawValues(false) // hide values on top of bars
        }

        val barData = BarData(dataSet).apply {
            setValueTextSize(10f)
            barWidth = 0.7f
        }

        barChart.apply {
            this.data = barData
            xAxis.apply {
                this.valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                labelRotationAngle = -90f
                setDrawGridLines(false)
                labelCount = labels.size
            }

            axisLeft.apply {
                setDrawGridLines(true)
                axisMinimum = 0f
                setDrawLabels(true)
                textSize = 10f
            }
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = true
            setFitBars(true)
            setScaleEnabled(true)
            setPinchZoom(false)
            notifyDataSetChanged()
            invalidate()
        }
    }
}