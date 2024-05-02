package com.example.app21try6.transaction.transactionactive

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.app21try6.R
import com.example.app21try6.database.SummaryDatabase
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentTransactionActiveBinding

class TransactionActiveFragment : Fragment() {
    private lateinit var binding: FragmentTransactionActiveBinding
    private val viewModel:TransactionActiveViewModel by viewModels()

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

        val act = activity as AppCompatActivity?
        if (act!!.supportActionBar != null) {
            val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolbar)
            val img =  requireActivity().findViewById<ImageView>(R.id.delete_image)
            img.visibility = View.VISIBLE
            val btn_del =  requireActivity().findViewById<Button>(R.id.delete_button)
            val btn_cancel =  requireActivity().findViewById<Button>(R.id.cancel_button)
            val btn_linear =  requireActivity().findViewById<ConstraintLayout>(R.id.linear_btn)
            img.setOnClickListener {
                //Toast.makeText(context,"clicked",Toast.LENGTH_SHORT).show()
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
            }
        })

        viewModel.is_image_clicked.observe(this.viewLifecycleOwner, Observer {


            if (it == true) {
                (binding.recyclerViewActiveTrans.adapter as TransactionActiveAdapter).isActive(it)
                binding.btnAddNewTrans.visibility = View.GONE
            } else {
                binding.btnAddNewTrans.visibility = View.VISIBLE
                (binding.recyclerViewActiveTrans.adapter as TransactionActiveAdapter).deActivate()
            }
            adapter.notifyDataSetChanged()
        })

        viewModel.navigateToTransEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
            // this.findNavController().navigate(TransactionActiveFragmentDirections.actionTransactionActiveFragmentToTransactionEditFragment(it))
            //viewModel.onNavigatedToTransEdit()
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


        return binding.root
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
}