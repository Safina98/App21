package com.example.app21try6

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.app21try6.database.tables.Summary
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PDFGenerator(private val context: Context) {
    var pageHeight = 842
    var pagewidth = 595
    fun generatePDF(file: File, allItemFromSummary: List<Summary>?, itemPosition: Int, year_list:Array<String>){
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
            Toast.makeText(this.context, "PDF file generated succesfully.", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this.context, "Failed to create PDF", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
        pdfDocument.close()


    }
}