package com.example.app21try6.statement.report

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.R
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.databinding.FragmentReportBinding
import com.example.app21try6.databinding.FragmentTransactionPurchaseBinding
import com.example.app21try6.getFirstDayOfYear
import com.example.app21try6.getLastDayOfYear
import com.example.app21try6.statement.purchase.PurchaseViewModel
import com.example.app21try6.statement.purchase.PurchaseViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Calendar
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import com.example.app21try6.formatRupiah


class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    private lateinit var viewModel: ReportViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_report,container,false)
        val application= requireNotNull(this.activity).application
        binding.lifecycleOwner=this
        val expenseRepo= ExpensesRepository(application)
        val transRepo= TransactionsRepository(application)
        val viewModelFactory = ReportViewModelFactory(application,expenseRepo,transRepo)
        viewModel = ViewModelProvider(this,viewModelFactory).get(ReportViewModel::class.java)
        binding.viewModel=viewModel
        val spinnerTahun =binding.spinnerTahunR
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        viewModel.setStartAndEndDateRange(getFirstDayOfYear(currentYear), getLastDayOfYear(currentYear))
        viewModel.getCategoryByType()
        val adapter_year = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, resources.getStringArray(R.array.tahun))
        spinnerTahun.adapter=adapter_year
        val positionY = (spinnerTahun.adapter as ArrayAdapter<String>).getPosition(currentYear.toString())
        spinnerTahun.setSelection(positionY)

        val adapterHPP= ReportAdapter(ReportListener{})
        val adapterBOP=ReportAdapter(ReportListener{})
        binding.recyclerViewHpp.adapter=adapterHPP
        binding.recyclerViewBebanOp.adapter=adapterBOP

        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position).toString()
                when (parent.id) {
                    R.id.spinner_tahun_r ->{
                        viewModel.setStartAndEndDateRange(getFirstDayOfYear(selected.toInt()),getLastDayOfYear(selected.toInt()))
                        viewModel.getCategoryByType()
                    }
                    // R.id.spinner_customer_pg -> viewModel.setSelectedCustomerValueProfit(selected)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        spinnerTahun.onItemSelectedListener = spinnerListener


        viewModel.HPPList.observe(viewLifecycleOwner){
            adapterHPP.submitList(it)

        }
        viewModel.BOPList.observe(viewLifecycleOwner){
            adapterBOP.submitList(it)
        }
        viewModel.isDatePickerClicked.observe(viewLifecycleOwner){
            showDatePickerDialog()
        }
        return binding.root
    }
    private fun showDatePickerDialog() {
        //clearSearchQuery()
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.pop_up_date_picker, null)
        val datePickerStart = dialogView.findViewById<DatePicker>(R.id.datePickerStart)
        val datePickerEnd = dialogView.findViewById<DatePicker>(R.id.datePickerEnd)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Date Range")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val startYear = datePickerStart.year
                val startMonth = datePickerStart.month
                val startDay = datePickerStart.dayOfMonth
                val endYear = datePickerEnd.year
                val endMonth = datePickerEnd.month
                val endDay = datePickerEnd.dayOfMonth

                val startDate = Calendar.getInstance().apply {
                    set(startYear, startMonth, startDay, 0, 0, 1) // Set time to start of the day
                    set(Calendar.MILLISECOND, 0)
                }.time

                val endDate = Calendar.getInstance().apply {
                    set(endYear, endMonth, endDay, 23, 59, 58) // Set time to end of the day
                    set(Calendar.MILLISECOND, 999)
                }.time


                viewModel.setStartAndEndDateRange(startDate,endDate)
                viewModel.getCategoryByType()

            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.setOnDismissListener {

        }

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.export_report_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_export_reportt -> {
                        generateReportPdf(
                            requireContext(),
                            dateRangeString = viewModel._dateRangeString.value ?: "",
                            hppItems = viewModel.HPPList.value?.map { it.label to formatRupiah(it.value)!! } ?: emptyList(),
                            bebanOpItems = viewModel.BOPList.value?.map { it.label to formatRupiah(it.value) !! } ?: emptyList(),
                            totalTrans = viewModel.totalTrans.value ?: "",
                            totalHPP = viewModel.totalHPP.value ?: "",
                            labaKotor = viewModel.labaKotor.value ?: "",
                            totalBOP = viewModel.totalBOP.value ?: "",
                            totalPengeluaran = viewModel.totalPengeluaran.value ?: "",
                            labaBersih = viewModel.labaBersih.value ?: ""
                        )
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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

