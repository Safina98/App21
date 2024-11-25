package com.example.app21try6.bookkeeping.summary

import android.Manifest
import android.R
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.app21try6.bookkeeping.editdetail.BookkeepingViewModel
import com.example.app21try6.databinding.FragmentBookSummaryBinding
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class BookSummaryFragment : Fragment(){

    // constant code for runtime permissions
    private val PERMISSION_REQUEST_CODE = 200
    //private lateinit var binding: FragmentBookSummaryBinding
    private lateinit var binding: FragmentBookSummaryBinding
    private val summaryViewModel :BookkeepingViewModel by activityViewModels { BookkeepingViewModel.Factory }
    //private val viewModel:SummaryViewModel by viewModels()
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        Log.i("Insert Csv", "result Launcher")
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            var isFirstLine = true
            Log.i("InsertCsv", "result Launcher if " + data?.data?.path.toString())
            val tokensList = mutableListOf<List<String>>()
            try {
                    context?.contentResolver?.openInputStream(data!!.data!!)?.bufferedReader()
                        ?.forEachLine { line ->
                            if (!isFirstLine) {
                                val tokens: List<String> = line.split(",")
                                tokensList.add(tokens)
                            }
                            isFirstLine = false
                        }
                    summaryViewModel.insertCSVBatch(tokensList)
                } catch (e: Exception) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                    Log.e("Insert Csv", "Error reading CSV: $e")
                }

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, com.example.app21try6.R.layout.fragment_book_summary,container,false)
        val application = requireNotNull(this.activity).application
        binding.lifecycleOwner = this
        binding.summaryViewModel = summaryViewModel
        if (checkPermission()) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
           requestPermission()
        }
        val act = activity as AppCompatActivity?
        if (act!!.supportActionBar != null) {
            val toolbar = requireActivity().findViewById<Toolbar>(com.example.app21try6.R.id.toolbar)
            val img =  requireActivity().findViewById<ImageView>(com.example.app21try6.R.id.delete_image)
            img.visibility = View.GONE
            val btn_linear =  requireActivity().findViewById<ConstraintLayout>(com.example.app21try6.R.id.linear_btn)
            btn_linear.visibility=View.GONE
        }
        summaryViewModel.initialRv()
        val adapter = SummaryAdapter(SummaryListener {
            summaryViewModel.onRvClick(it)
            val monthname = summaryViewModel.selectedMonth.value
            val defaultPosition =summaryViewModel.months_list.indexOf(monthname)
            binding.spinnerM.setSelection(defaultPosition)
                })
        binding.recyclerViewSumary.adapter = adapter

        summaryViewModel.allItemFromSummary.observe(viewLifecycleOwner, Observer {
            Log.i("ProfitProbs","alltemsummary: $it")
        })
        val adapterYear = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, summaryViewModel.year_list)
        binding.spinner.adapter = adapterYear

        val adapterMonth = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, summaryViewModel.months_list)
        binding.spinnerM.adapter = adapterMonth
        val catname = summaryViewModel.selectedMonth.value
        val defaultPosition =summaryViewModel.months_list.indexOf(catname)
        binding.spinnerM.setSelection(defaultPosition)
        val thisyear = summaryViewModel.year.toString()
        val defaultPositionYear =summaryViewModel.year_list.indexOf(thisyear)
        binding.spinner.setSelection(defaultPositionYear)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                summaryViewModel.setSelectedYear(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.spinnerM.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                summaryViewModel.setSelectedMonth(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
        summaryViewModel.getSummaryWithNullProductId()
        summaryViewModel.selectedYear.observe(viewLifecycleOwner) {
           // summaryViewModel.updateRvNew()
        }
        summaryViewModel.selectedMonth.observe(viewLifecycleOwner){
           // summaryViewModel.updateRvNew()
        }
        summaryViewModel.recyclerViewData.observe(viewLifecycleOwner){
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        }
        summaryViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if(isLoading==true){
                binding.recyclerViewSumary.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.recyclerViewSumary.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
            //binding.progressBar.visibility = if (isLoading ==true) View.VISIBLE else View.GONE
            })
        summaryViewModel.insertionCompleted.observe(viewLifecycleOwner, Observer { insertionCompleted ->
        if (insertionCompleted) {
           summaryViewModel.setSelectedYear(summaryViewModel.year.toString())
            val thisyear = summaryViewModel.selectedYear.value.toString()
            val defaultPosition =summaryViewModel.year_list.indexOf(thisyear)
            binding.spinner.setSelection(defaultPosition)
            }
         })
        summaryViewModel.date.observe(viewLifecycleOwner) { date ->
        }
        summaryViewModel.navigateBookKeeping.observe(viewLifecycleOwner, Observer { date->
            date?.let {
                this.findNavController().navigate(BookSummaryFragmentDirections.actionBookSummaryFragmentToBookKeepeingFragment3(date))
                summaryViewModel.onDayNavigated()
            }
        })
        setHasOptionsMenu(true)
        return binding.root
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(com.example.app21try6.R.menu.option_menu,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            com.example.app21try6.R.id.menu_export_csv -> {
                exportCSVBook()
                return true
            }
            com.example.app21try6.R.id.menu_import->{
                importCSVBook()
                return  true
            }
            com.example.app21try6.R.id.menu_export_pdf->{
                exportPDFBook()
                return true
            }
        }
        return super.onOptionsItemSelected(item) // important line
    }

    private fun exportPDFBook() {
        val fileName = generateFileName()
        val file = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            File(context?.getExternalFilesDir(null), "Laporan 21 "+fileName+".pdf")
        } else {
            TODO("VERSION.SDK_INT < FROYO")
        }
        Log.i("filepath",""+file.path.toString())

        val photoURI:Uri = FileProvider.getUriForFile(this.requireContext(), requireContext().applicationContext.packageName + ".provider",file)
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, photoURI)
            type = "application/pdf"
        }
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivity(Intent.createChooser(shareIntent, "book"))
        }catch (e :Exception){
            Log.i("error_msg",e.toString())
        }
        }


    //private fun importCSVBook() {
    //    var fileIntent = Intent(Intent.ACTION_GET_CONTENT)
    //    fileIntent.type = "text/*"
     //   try { resultLauncher.launch(fileIntent) } catch (e: FileNotFoundException) { Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show() }
    //}

    private fun importCSVBook() {
        val fileIntent = Intent(Intent.ACTION_GET_CONTENT)
        fileIntent.type = "text/*"
        try {
            resultLauncher.launch(fileIntent)
        } catch (e: FileNotFoundException) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }
    private fun exportCSVBook() {
        val fileName = "Pembukuan 21"
        val file = File(context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName + ".csv")
        Log.i("filepath"," "+context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString())
        Toast.makeText(context,context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),Toast.LENGTH_LONG).show()
        summaryViewModel.writeCSV(file)
        val photoURI:Uri = FileProvider.getUriForFile(this.requireContext(), requireContext().applicationContext.packageName + ".provider",file)
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, photoURI)
            type = "text/*"
        }
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivity(Intent.createChooser(shareIntent, "book"))
        }catch (e :Exception){
            Log.i("error_msg",e.toString())
        }
    }

    private fun generateFileName():String {
        val sdf = SimpleDateFormat("dd-M-yyyy hh:mm:ss")
        return sdf.format(Date()).toString()
    }

    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(requireActivity(),arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

}