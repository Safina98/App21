package com.example.app21try6.statement

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app21try6.R
import com.example.app21try6.database.CustomerTable
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentStatementHsBinding
import com.example.app21try6.databinding.PopUpDiscBinding


class StatementHsFragment : Fragment() {
    private lateinit var binding: FragmentStatementHsBinding
    private lateinit var viewModel: StatementHSViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =DataBindingUtil.inflate(inflater,R.layout.fragment_statement_hs,container,false)
        val application= requireNotNull(this.activity).application
        val dataSource1 = VendibleDatabase.getInstance(application).discountDao
        val dataSource2 = VendibleDatabase.getInstance(application).customerDao
        val viewModelFactory = StatementHSViewModelFactory(application,dataSource1,dataSource2)
        viewModel = ViewModelProvider(this,viewModelFactory).get(StatementHSViewModel::class.java)
        binding.viewModel=viewModel

        val adapter = DiscountAdapter(DiscountListener {  }, DiscountLongListener {  },
            DiscountDelListener {viewModel.deleteDiscountTable(it)  })
        binding.btnAddDiscount.setOnClickListener {
            showDiscountDialog()
        }
        val adapterCustomer = CustomerAdapter(
            CustomerListener {  },
            CustomerLongListener {  },
            CustomerDelListener {  }
        )
        binding.btnAddDiscount.setOnClickListener {
            showDiscountDialog()
        }
        binding.btnAddCustomer.setOnClickListener {
            viewModel.insertBatch()
        }
        val suggestions = arrayOf(
            "Asia Jok", "Alyka Jok","Aisya Jok","Auto 354","AMV","Akbar Sengkang","Asep Ramlan","Anugrah Mebel","AT Jok",
            "Bandung Jok","Bandung Jok Gowa","Bagus Jok","Beo","Berkah Variasi",
            "Cahaya Variasi",
            "dr Jok","Dyna Jok","D'fun Kendari","Densus 99",
            "Eka Jok","Evolution",
            "Fiesta Jok","Fakhri Jok",
            "Green Design",
            "HSR Auto",
            "Jabal","Jok 88",
            "King Variasi","Karya Jok","Kubis Mebel",
            "Makassar Variasi","Mega Buana","Mas Tono",
            "Laquna",
            "Pak Maliang", "Pak Ilham", "Pak Ramli Sidrap","Pak Ibet","Pak Agus Saputra","Pattalassang Variasi","Prima leather","Pak Alim",
            "Rajawali Motor Timika","Rumah Kursi","Rumah Sofa Kolut","RGARAGE","Rezky Jok","Riski Jok",
            "Sun Variasi","Susan Jok", "Sumber Jok","Surabaya Motor","Selayar","SKAAD Bintuni","Sam & Sons",
            "Terminal Jok",
            "Unang",
            "Variasi 77",
            "Wendy",
            "Yusdar Motor")

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        binding.rvDisc.adapter = adapter
        binding.rvCust.adapter=adapterCustomer
        binding.rvDisc.addItemDecoration(dividerItemDecoration)
        binding.rvCust.addItemDecoration(dividerItemDecoration)
        viewModel.allDiscountFromDB.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })
        viewModel.allCustomerFromDb.observe(viewLifecycleOwner, Observer {
            adapterCustomer.submitList(it)
            adapter.notifyDataSetChanged()
        })

        return binding.root
    }
    private fun showDiscountDialog() {
        // Inflate the layout using data binding
        val binding: PopUpDiscBinding = DataBindingUtil.inflate(
            LayoutInflater.from(requireContext()),
            R.layout.pop_up_disc, // Replace with your dialog layout file
            null,
            false
        )

        binding.textDiscValue.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        binding.textDiscQty.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        // Create the dialog using AlertDialog.Builder
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .setTitle("Enter Discount Details")
            .setPositiveButton("OK") { dialog, _ ->
                // Get values from the input fields
                val discName = binding.textDiscName.text.toString().uppercase().trim()
                val discValue = binding.textDiscValue.text.toString().trim().toDouble()
                val discMinQty = binding.textDiscQty.text.toString().trim().toDoubleOrNull()
                val selectedDiscType = binding.spinnerM.selectedItem.toString().trim()
                val custLocation = binding.textCustLoc.text.toString().uppercase().trim()
                viewModel.insertDiscount(discValue,discName,discMinQty,selectedDiscType,custLocation)
                // Do something with the input data, e.g., save or pass it
                // ...

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        // Show the dialog
        val dialog = dialogBuilder.create()
        dialog.show()
    }

}