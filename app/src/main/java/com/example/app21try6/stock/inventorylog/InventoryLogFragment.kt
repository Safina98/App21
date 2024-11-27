package com.example.app21try6.stock.inventorylog

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.R
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentInventoryLogBinding
import com.example.app21try6.statement.purchase.UpdateListener
import com.example.app21try6.stock.subproductstock.SubViewModel



class InventoryLogFragment : Fragment() {

    private lateinit var binding:FragmentInventoryLogBinding
    private lateinit var viewModel: InventoryLogViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_inventory_log,container,false)
        val application = requireNotNull(this.activity).application
        val dataSource1 = VendibleDatabase.getInstance(application).subProductDao
        val dataSource2 = VendibleDatabase.getInstance(application).inventoryLogDao
        val viewModelFactory = InventoryLogViewModelFactory(dataSource1,dataSource2,application)
        binding.lifecycleOwner =this
        val viewModel = ViewModelProvider(this,viewModelFactory).get(InventoryLogViewModel::class.java)
        binding.viewModel = viewModel
        val adapter=InventoryLogAdapter(InventoryLogUpdateListener{},InventoryLogDeleteListener {  })
        binding.logRv.adapter=adapter
        viewModel.allLogFromD.observe(viewLifecycleOwner,Observer{
            it?.let {
                adapter.submitList(it)
            }
        })
        // Inflate the layout for this fragment
        return binding.root
    }

}