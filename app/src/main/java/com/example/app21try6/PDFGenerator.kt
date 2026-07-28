package com.example.app21try6

import android.R
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.app21try6.database.tables.Summary
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PDFGenerator() {
    var pageHeight = 842
    var pagewidth = 595
    fun generatePDF(context: Context,file: File, allItemFromSummary: List<Summary>?, itemPosition: Int, year_list:Array<String>){
        val listBulanan = allItemFromSummary
        var pdfDocument = PdfDocument()
        val title = Paint()
        val month_style = Paint()
        var pageNumber =1
        var mypageInfo = PdfDocument.PageInfo.Builder(pagewidth, pageHeight, pageNumber).create()
        var myPage = pdfDocument.startPage(mypageInfo)
        var canvas: Canvas = myPage.canvas
        title.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        title.textSize = 45F
        title.color = ContextCompat.getColor(context, R.color.black)
        month_style.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
        month_style.textSize = 30F
        month_style.color = ContextCompat.getColor(context, R.color.black)
        month_style.textAlign = Paint.Align.CENTER
        val year = year_list.get(itemPosition)
        canvas.drawText("LAPORAN KEUANGAN", 60F, 260F, title)
        canvas.drawText("TOKO 21", 200F, 330F, title)
        canvas.drawText("TAHUN $year", 160F, 390F, title)
        title.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        title.color = ContextCompat.getColor(context, R.color.black)
        title.textSize = 15F
        var this_month = ""
        var x = 50F
        var y =220F
        var totalIncome = mutableListOf<Double>()
        if (listBulanan != null) {
            for (v in listBulanan){
                if(v.year==year.toInt()) {
                    if (v.month != this_month) {
                        if (this_month!="") {
                            canvas.drawText("Total", 400F, y, title)
                            canvas.drawText(totalIncome.sum().toInt().toString(), 500F, y, title) }
                        totalIncome.clear()
                        y = 120F
                        this_month = v.month
                        pdfDocument.finishPage(myPage)
                        pageNumber += 1
                        mypageInfo = PdfDocument.PageInfo.Builder(pagewidth, pageHeight, pageNumber).create()
                        myPage = pdfDocument.startPage(mypageInfo)
                        canvas = myPage.canvas
                        canvas.drawText(this_month, 270F, y-60F, month_style)
                        canvas.drawText("Tanggal", 20F, y-20, title)
                        canvas.drawText("Barang", 120F, y-20, title)
                        canvas.drawText("Jumlah", 300F, y-20, title)
                        canvas.drawText("Harga Satuan", 400F, y-20, title)
                        canvas.drawText("Total", 500F, y-20, title)
                        title.textAlign = Paint.Align.LEFT
                    }
                    if (y>750F){
                        pdfDocument.finishPage(myPage)
                        pageNumber+=1
                        mypageInfo = PdfDocument.PageInfo.Builder(pagewidth, pageHeight, pageNumber).create()
                        myPage = pdfDocument.startPage(mypageInfo)
                        canvas = myPage.canvas
                        y = 120f
                    }
                    if(v.item_name!="empty") {
                        canvas.drawText(v.day_name.toString() + ", " + v.day.toString(), 20F, y, title)
                        canvas.drawText(v.item_name.toString(), 120F, y, title)
                        canvas.drawText(v.item_sold.toString(), 300F, y, title)
                        canvas.drawText(v.price.toInt().toString(), 400F, y, title)
                        canvas.drawText(v.total_income.toInt().toString(), 500F, y, title)
                        totalIncome.add(v.total_income)
                        y = y + 20F
                    }
                }
            }
            canvas.drawText("Total", 400F, y, title)
            canvas.drawText(totalIncome.sum().toInt().toString(), 500F, y, title)
        }else{
            Toast.makeText(context,"SUmary null", Toast.LENGTH_SHORT).show()}
        pdfDocument.finishPage(myPage)
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(context, "PDF file generated succesfully.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(context, "Failed to create PDF", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        pdfDocument.close()

    }
    fun generateReportPdf(
        context: Context,
        dateRangeString: String,
        hppItems: List<Pair<String, String>>,      // name, value strings
        bebanOpItems: List<Pair<String, String>>,
        totalTrans: String,
        totalHPP: String,
        labaKotor: String,
        totalBOP: String,
        totalPengeluaran: String,
        labaBersih: String,
        fileName: String = "laporan_${System.currentTimeMillis()}.pdf"
    ) {
        val pdfDocument = PdfDocument()
        val pageWidth = 595   // A4 at 72dpi
        val pageHeight = 842
        val margin = 40f
        val lineHeight = 20f
        val titlePaint = Paint().apply { textSize = 16f; isFakeBoldText = true;textAlign = Paint.Align.CENTER }
        val headerPaint = Paint().apply { textSize = 13f; isFakeBoldText = true }
        val textPaint = Paint().apply { textSize = 11f }
        val linePaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 1f }
        val centeredTextPaint = Paint().apply {
            textSize = 11f
            textAlign = Paint.Align.CENTER
        }
        var pageNum = 1
        var page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create())
        var canvas = page.canvas
        var y = margin

        fun newPageIfNeeded(need: Float = lineHeight) {
            if (y + need > pageHeight - margin) {
                pdfDocument.finishPage(page)
                pageNum++
                page = pdfDocument.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNum).create())
                canvas = page.canvas
                y = margin
            }
        }

        fun row(label: String, value: String, paint: Paint = textPaint) {
            newPageIfNeeded()
            canvas.drawText(label, margin, y, paint)
            canvas.drawText(value, pageWidth - margin - paint.measureText(value), y, paint)
            y += lineHeight
        }

        // Title
        canvas.drawText("Laporan Periode", pageWidth / 2f, y, titlePaint); y += lineHeight
        canvas.drawText(dateRangeString, pageWidth / 2f, y, centeredTextPaint); y += lineHeight * 1.5f
        y += 20
        // Pemasukan
        canvas.drawText("Pemasukan", margin, y, headerPaint); y += 8f
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint); y += 16
        y += 6
        row("Pemasukan", totalTrans)
        y -= 6
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint); y += 16
        y += 8

        // HPP
        canvas.drawText("Harga Pokok Penjualan", margin, y, headerPaint); y += lineHeight
        hppItems.forEach { (name, value) -> row(name, value) }
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint); y += 16
        y += 6
        row("Total", totalHPP)
        y -= 6
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint); y += 16
        y += 20

        // Laba Kotor
        canvas.drawText("Laba Kotor", margin, y, headerPaint); y += 8f
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint); y += 16
        y += 6
        row("Pemasukan - Harga pokok", labaKotor)
        y -= 6
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint); y += 16
        y += 20

        // Beban Operasional
        canvas.drawText("Beban Operasional", margin, y, headerPaint); y += lineHeight
        bebanOpItems.forEach { (name, value) -> row(name, value) }
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint); y += 16
        y += 6
        row("Sub Total", totalBOP)
        y -= 6
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint); y += 16
        y += 20

        // Total Pengeluaran
        canvas.drawText("Total Pengeluaran", margin, y, headerPaint); y +=8f
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint); y += 16
        y += 6
        row("Segmen 1 + segmen 2 + segmen 3", totalPengeluaran)
        y -= 6
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint); y += 16
        y += 20

        // Laba Bersih
        canvas.drawText("Laba Bersih", margin, y, headerPaint); y +=8f
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint); y += 16
        y += 6
        row("Total Pemasukan - total Pengeluaran", labaBersih)
        y -= 6
        canvas.drawLine(margin, y, pageWidth - margin, y, linePaint); y += 16


        pdfDocument.finishPage(page)

        val file = File(context.getExternalFilesDir(null), fileName)

        val uri: Uri? = try {
            FileOutputStream(file).use { pdfDocument.writeTo(it) }
            pdfDocument.close()
            FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
        } catch (e: IOException) {
            pdfDocument.close()
            Log.e("GenerateReportPdf", "Failed to write PDF", e)
            null
        }

        if (uri == null) {
            Toast.makeText(context, "Gagal membuat PDF", Toast.LENGTH_SHORT).show()
            return
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "application/pdf"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Bagikan laporan"))
    }
}