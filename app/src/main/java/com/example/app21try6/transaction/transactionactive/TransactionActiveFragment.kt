package com.example.app21try6.transaction.transactionactive

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.R
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentTransactionActiveBinding
import java.io.File
import java.io.FileNotFoundException

class TransactionActiveFragment : Fragment() {
    private lateinit var binding: FragmentTransactionActiveBinding
    private val viewModel:TransactionActiveViewModel by viewModels()
    private val PERMISSION_REQUEST_CODE = 200
    val requestcode = 1

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
                            Log.i("INSERTCSVPROB","tokens: $tokens")
                            tokensList.add(tokens)
                        }
                        isFirstLine = false
                    }
                viewModel.insertCSVBatch(tokensList)
            } catch (e: java.lang.Exception) {
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
                Log.e("Insert Csv", "reulst loadingError reading CSV: $e")
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_transaction_active,container,false)
        val application = requireNotNull(this.activity).application
        val datasource1= VendibleDatabase.getInstance(application).transSumDao
        val datasource2= VendibleDatabase.getInstance(application).transDetailDao
        val viewModelFactory = TransactionActiveViewModelFactory(application,datasource1,datasource2)
        val viewModel = ViewModelProvider(this,viewModelFactory).get(TransactionActiveViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.getActiveTrans()
        if (checkPermission()) {
            //Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission()
        }


        val act = activity as AppCompatActivity?
        if (act!!.supportActionBar != null) {
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
            val img =  requireActivity().findViewById<ImageView>(R.id.delete_image)
            img.visibility = View.VISIBLE
            val btn_del =  requireActivity().findViewById<Button>(R.id.delete_button)
            val btn_cancel =  requireActivity().findViewById<Button>(R.id.cancel_button)
            val btn_linear =  requireActivity().findViewById<ConstraintLayout>(R.id.linear_btn)
            img.setOnClickListener {
                viewModel.onImageClicked()
                btn_linear.visibility = View.VISIBLE
                toolbar.visibility = View.GONE
            }
            btn_del.setOnClickListener {
                toolbar.visibility = View.VISIBLE
                deleteDialog()
                btn_linear.visibility = View.GONE
            }
            btn_cancel.setOnClickListener {
                toolbar.visibility = View.VISIBLE
                btn_linear.visibility = View.GONE
                viewModel.onButtonClicked()
            }
        }
        val adapter = TransactionActiveAdapter(ActiveClickListener {

            viewModel.onNavigatetoTransDetail(it.sum_id)
        }
            ,CheckBoxListenerTransActive{view, stok ->
            val cb = view as CheckBox
            viewModel.onCheckBoxClicked(stok, cb.isChecked)
        }
        )
        val displayMetrics = resources.displayMetrics
        val screenWidthPx = displayMetrics.widthPixels
        val itemWidthPx = resources.getDimensionPixelSize(R.dimen.rv_width) // Change this to the width of your item

        val spanCount = screenWidthPx / itemWidthPx
        binding.recyclerViewActiveTrans.adapter = adapter
        binding.recyclerViewActiveTrans.layoutManager = GridLayoutManager(context, spanCount, RecyclerView.VERTICAL, false)

        viewModel.active_trans.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it.sortedByDescending { it.sum_id })
                adapter.notifyDataSetChanged()
            }
        })
        viewModel.allTransFromDB.observe(viewLifecycleOwner){ Log.i("INSERTCSVPROB","AllTansSum: $it") }

        viewModel.is_image_clicked.observe(this.viewLifecycleOwner, Observer {
            if (it == true) {
                (binding.recyclerViewActiveTrans.adapter as TransactionActiveAdapter).isActive(it)
                binding.btnAddNewTrans.visibility = View.GONE
            } else {
                binding.btnAddNewTrans.visibility = View.VISIBLE
                (binding.recyclerViewActiveTrans.adapter as TransactionActiveAdapter).deActivate()
            }
            adapter.notifyDataSetChanged()
            viewModel.getActiveTrans()
        })


        viewModel.navigateToTransEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
            this.findNavController().navigate(TransactionActiveFragmentDirections.actionTransactionActiveFragmentToTransactionEditFragment(it))
            viewModel.onNavigatedToTransEdit()
          }
        })
        viewModel.navigatToAllTrans.observe(viewLifecycleOwner, Observer {
           if(it==true) {
                this.findNavController().navigate(TransactionActiveFragmentDirections.actionTransactionActiveFragmentToAllTransactionsFragment())
                viewModel.onNavigatedToAllTrans()
            }
        })
        viewModel.navigateToTransDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(TransactionActiveFragmentDirections.actionTransactionActiveFragmentToTransactionDetailFragment(it))
                viewModel.onNavigatedToTransDetail()
            }
        })
        setHasOptionsMenu(true)
        return binding.root
    }



    override fun onStart() {
        super.onStart()
        viewModel.getActiveTrans()
    }


    fun deleteDialog(){
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to Delete all the Selected Item?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialog, id ->  viewModel.delete() }
            .setNegativeButton("No") { dialog, id ->
                viewModel.onButtonClicked()
                dialog.dismiss() }
            .setOnCancelListener {
                viewModel.onButtonClicked()
            }
        val alert = builder.create()
        alert.show()
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.option_menu,menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.menu_export_csv -> {
                viewModel.getAllTransactions()
                exportTransCSV()
                return true
            }
            R.id.menu_import->{
                importCSVTrans()
                return  true
            }
        }
        return super.onOptionsItemSelected(item) // important line
    }


    private fun importCSVTrans() {
        var fileIntent = Intent(Intent.ACTION_GET_CONTENT)
        fileIntent.type = "text/*"
        try { resultLauncher.launch(fileIntent) }
        catch (e: FileNotFoundException) {
            Log.i("INSERTCSVPROB","fragment $e.toString()")
        }
            //Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show() }
    }
    private fun exportTransCSV() {
        val fileName = "Transaction 21"
        //var file:File
        val file = File(context?.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName + ".csv")
        Log.i("FilePath","else path: " +file.path.toString())
        viewModel.writeCSV(file)
        val photoURI: Uri = FileProvider.getUriForFile(this.requireContext(), requireContext().applicationContext.packageName + ".provider",file)
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, photoURI)
            type = "text/*"
        }
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivity(Intent.createChooser(shareIntent, "Stok"))
        }catch (e : java.lang.Exception){
            Log.i("error_msg",e.toString())
        }
    }
    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(requireActivity(),arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }
    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }
}