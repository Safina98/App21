package com.example.app21try6.statement.purchaseorder

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.R
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.databinding.FragmentPurchaseOrderBinding

val pOTag="OrderProbs"
class PurchaseOrderFragment : Fragment() {
    private lateinit var binding: FragmentPurchaseOrderBinding
    private lateinit var viewModel: PurchaseOrderViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_purchase_order,container,false)
        val application = requireNotNull(this.activity).application
        val repository = StockRepositories(application)
        val expenseRepository= ExpensesRepository(application)

        viewModel = ViewModelProvider(requireActivity(),
            PurchaseOrderViewModelFactory(repository,expenseRepository, application))
            .get(PurchaseOrderViewModel::class.java)
        binding.viewModel=viewModel
        binding.lifecycleOwner =this

        viewModel.suplierList.observe(viewLifecycleOwner){supliers->
            if (supliers != null) {
                val adapterSuplier = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    supliers
                )
                binding.textSuplier.setAdapter(adapterSuplier)
            }
        }

        return binding.root
    }
}
//input suplier name
//load all sub product where the product have the suplier id
//what to be selected from loading is: sub_product name, net, qty*batchqty of the last 30 and 90 days from itansaction,
//calculate how many stock to be added
//The formula is
// if(qty of 30 days > than pty of the last 90 days)
// daylysells = qty 30 days :30 else pty 90 days :90
//if (net<daily sells * 45) buy, else no
//model: sub_id, sub_name, net, total sellsLast30Days, totalSalesLast90Days, daily sells, new order