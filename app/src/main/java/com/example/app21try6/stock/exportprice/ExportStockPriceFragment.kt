package com.example.app21try6.stock.exportprice

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.app21try6.R
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.databinding.FragmentExportStockPriceBinding
import com.example.app21try6.stock.brandstock.BrandStockViewModel
import com.example.app21try6.stock.brandstock.BrandStockViewModelFactory
import com.example.app21try6.transaction.transactionproduct.ProductTransListener
import com.example.app21try6.transaction.transactionproduct.TransactionProductAdapter

class ExportStockPriceFragment : Fragment() {
    private lateinit var binding: FragmentExportStockPriceBinding

    private lateinit var viewModel: ExportStockPriceViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_export_stock_price,container,false)
        val application = requireNotNull(this.activity).application
        val repository = StockRepositories(application)
        val discountRepository=DiscountRepository(application)

        viewModel = ViewModelProvider(requireActivity(),
            ExportStockPriceViewModelFactory(repository,discountRepository, application))
            .get(ExportStockPriceViewModel::class.java)
        binding.lifecycleOwner =this
        viewModel.updateRv()

        val adapter = TransactionProductAdapter(ProductTransListener {


        },true)

        binding.espRv.adapter=adapter
        viewModel.allProduct.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.sortedBy { it.product_name })

        })

        return binding.root
    }

}