package com.example.app21try6.bookkeeping.summary

import android.Manifest
import android.R
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.BuildConfig
import com.example.app21try6.database.SummaryDatabase
import com.example.app21try6.databinding.FragmentBookSummaryBinding
import java.io.*
import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.IndexOutOfBoundsException
import java.text.SimpleDateFormat
import java.util.*


class BookSummaryFragment : Fragment(){


    // declaring width and height
    // for our PDF file.
    var pageHeight = 1120
    var pagewidth = 792

    // creating a bitmap variable
    // for storing our images
    lateinit var bmp: Bitmap
    lateinit var scaledbmp: Bitmap // creating a bitmap variable
    // for storing our images

    // constant code for runtime permissions
    private val PERMISSION_REQUEST_CODE = 200
    //private lateinit var binding: FragmentBookSummaryBinding
    private lateinit var binding: FragmentBookSummaryBinding
    lateinit var generatePDFbtn:Button

    private val viewModel:SummaryViewModel by viewModels()
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            Log.i("URI_", data?.data?.path.toString())
            var i = 1
            try {
                context?.contentResolver?.openInputStream(data!!.data!!)?.bufferedReader()?.forEachLine {
                    val tokens: List<String> = it.split(",")
                    if (i!=1) { viewModel.insertCSV(tokens) }
                    i+=1
                }
            }catch (e: Exception){
                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show()
                Log.e("message_e", ""+e)
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, com.example.app21try6.R.layout.fragment_book_summary,container,false)
        //generatePDFbtn = binding.btn3
        val application = requireNotNull(this.activity).application
        val dataSource = SummaryDatabase.getInstance(application).summaryDbDao
        val viewModelFactory = SummaryViewModelFactory(dataSource, application)
        val summaryViewModel = ViewModelProvider(
                this, viewModelFactory).get(SummaryViewModel::class.java)
        binding.lifecycleOwner = this
        binding.summaryViewModel = summaryViewModel
        if (checkPermission()) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
           requestPermission()
        }
        val adapter = SummaryAdapter(SummaryListener {
                    //if (it.day_n==0) {
                      if (summaryViewModel.is_month.value ==true){
                        Toast.makeText(context, it.month_n.toString(), Toast.LENGTH_LONG).show()
                        summaryViewModel._itemMonthPosition.value = summaryViewModel.months_list.indexOf(it.month_n)
                          summaryViewModel.onMontSelected()

                    } else{
                        var date = arrayOf(it.year_n.toString(),it.month_n,it.day_n.toString())
                          summaryViewModel.onMonthDoneSelected()
                        summaryViewModel.onDayClicked(date)
                    }
                })
        binding.recyclerViewSumary.adapter = adapter

        summaryViewModel.allItemFromSummary.observe(viewLifecycleOwner, Observer {
           // Toast.makeText(context,it.toString(),Toast.LENGTH_SHORT).show()
        })
        summaryViewModel.is_month.observe(requireActivity(), Observer {
            summaryViewModel.__days?.observe(viewLifecycleOwner, Observer {
                it?.let {
                    adapter.submitList(it)
                    adapter.notifyDataSetChanged()
                }
            })
        })

        summaryViewModel._itemPosition.observe(requireActivity(),
                object : Observer<Int> {
                    override fun onChanged(t: Int) {
                        summaryViewModel._itemMonthPosition.value = 0
                        summaryViewModel.__months.observe(viewLifecycleOwner, Observer {
                            it?.let {
                                adapter.submitList(it.sortedBy { it.month_nbr})
                                adapter.notifyDataSetChanged()
                            }
                        })
                    }})
        summaryViewModel._itemMonthPosition.observe(requireActivity(),
                object : Observer<Int> {
                    override fun onChanged(t: Int?) {
                        //you will get the position on selection os spinner
                        if (t==0){
                            summaryViewModel.onMonthDoneSelected()
                        }
                        summaryViewModel.__days?.observe(viewLifecycleOwner, Observer {
                            it?.let {
                                adapter.submitList(it)
                                adapter.notifyDataSetChanged()
                            }
                        })
                    }})
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
        viewModel.generatePDF(file)
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

    private fun importCSVBook() {
        var fileIntent = Intent(Intent.ACTION_GET_CONTENT)
        fileIntent.type = "text/*"
        try { resultLauncher.launch(fileIntent) } catch (e: FileNotFoundException) { Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show() }
    }

    private fun exportCSVBook() {
        val fileName = "Pembukuan 21"
        val file = File(context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName + ".csv")
        Log.i("filepath"," "+context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString())
        Toast.makeText(context,context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString(),Toast.LENGTH_LONG).show()
        viewModel.writeCSV(file)
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