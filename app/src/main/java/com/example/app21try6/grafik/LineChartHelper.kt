package com.example.app21try6.grafik

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.app21try6.R
import com.example.app21try6.database.models.BarChartModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.BarHighlighter

object LineChartHelper {

    /**
     * Call this once per chart to apply shared visual config.
     * Put it in onViewCreated, before any data is loaded.
     */
    fun setupChart(
        chart: LineChart,
        context: Context,
        showLegend: Boolean = false,
        enableTouch: Boolean = true
    ) {
        chart.apply {
            description.isEnabled = false
            legend.isEnabled = showLegend
            setTouchEnabled(enableTouch)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(false)
            setDrawGridBackground(false)
            animateX(600)

            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textColor = ContextCompat.getColor(context, R.color.pastel_green_dark) // adjust to your color
            }

            axisLeft.apply {
                setDrawGridLines(true)
                textColor = ContextCompat.getColor(context, R.color.pastel_green_dark)
                axisMinimum = 0f
            }

            axisRight.isEnabled = false
            renderer = DropLineRenderer(
                chart = this,
                animator = animator,
                viewPortHandler = viewPortHandler,
                dashColor = ContextCompat.getColor(context, R.color.grey),
                dashWidth = 1f,
                dashLength = 8f,
                dashGap = 4f
            )
        }
    }

    /**
     * Feed data into any pre-configured LineChart.
     * @param entries   list of LineChartModel (label + value)
     * @param lineLabel shown in legend if enabled
     * @param lineColor e.g. ContextCompat.getColor(ctx, R.color.colorPrimary)
     * @param fillColor pass null to disable fill under the line
     */
    fun setData(
        chart: LineChart,
        entries: List<BarChartModel>,
        lineLabel: String = "",
        lineColor: Int,
        fillColor: Int? = null
    ) {
        val xLabels = entries.map { it.label }
        val chartEntries = entries.mapIndexed { i, model ->
            Entry(i.toFloat(), model.value.toFloat())
        }

        val dataSet = LineDataSet(chartEntries, lineLabel).apply {
            color = lineColor
            setCircleColor(lineColor)
            circleRadius = 4f
            lineWidth = 2f
            mode = LineDataSet.Mode.LINEAR
            setDrawValues(true)
            valueTextSize = 10f
            valueTextColor = lineColor  // or any color you want
            valueFormatter = object : ValueFormatter() {
                override fun getPointLabel(entry: Entry): String {
                    return entry.y.toInt().toString()
                    // or for thousands: "%,.0f".format(entry.y)
                }
            }
            setDrawFilled(fillColor != null)
            fillColor?.let { this.fillColor = it; fillAlpha = 50 }
        }



        chart.xAxis.apply {
            valueFormatter = IndexAxisValueFormatter(xLabels)
            granularity = 1f
            isGranularityEnabled = true
            labelCount = xLabels.size   // ✅ force all labels to show
        }
        //chart.xAxis.valueFormatter = IndexAxisValueFormatter(xLabels)
        chart.data = LineData(dataSet)
        chart.invalidate()
    }

    /** Clears chart without destroying configuration. */
    fun clear(chart: LineChart) {
        chart.clear()
        chart.invalidate()
    }
}