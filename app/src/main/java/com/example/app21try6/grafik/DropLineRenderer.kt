package com.example.app21try6.grafik

import android.graphics.*
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.utils.ViewPortHandler
import com.github.mikephil.charting.components.YAxis

class DropLineRenderer(
    chart: LineDataProvider,
    animator: ChartAnimator,
    viewPortHandler: ViewPortHandler,
    private val dashColor: Int = Color.GRAY,
    private val dashWidth: Float = 1f,
    private val dashLength: Float = 8f,
    private val dashGap: Float = 4f
) : LineChartRenderer(chart, animator, viewPortHandler) {

    private val dropPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = dashWidth
        color = dashColor
        pathEffect = DashPathEffect(floatArrayOf(dashLength, dashGap), 0f)
    }

    override fun drawExtras(canvas: Canvas) {
        super.drawExtras(canvas)
        drawDropLines(canvas)
    }

    private fun drawDropLines(canvas: Canvas) {
        val lineData = mChart.lineData ?: return
        val transformer = mChart.getTransformer(YAxis.AxisDependency.LEFT)
        val bottomY = mViewPortHandler.contentBottom()

        for (dataSet in lineData.dataSets) {
            if (!dataSet.isVisible) continue

            // collect all points
            val pts = FloatArray(dataSet.entryCount * 2)
            for (i in 0 until dataSet.entryCount) {
                val entry = dataSet.getEntryForIndex(i)
                pts[i * 2]     = entry.x
                pts[i * 2 + 1] = entry.y
            }

            // convert data coords → pixel coords
            transformer.pointValuesToPixel(pts)

            for (i in 0 until dataSet.entryCount) {
                val px = pts[i * 2]
                val py = pts[i * 2 + 1]

                val path = Path().apply {
                    moveTo(px, py)
                    lineTo(px, bottomY)
                }
                canvas.drawPath(path, dropPaint)
            }
        }
    }
}