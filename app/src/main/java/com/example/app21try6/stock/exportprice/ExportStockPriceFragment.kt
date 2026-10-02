package com.example.app21try6.stock.exportprice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
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

val espTag="DraftTexrProblem"
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
        binding.viewModel=viewModel
        binding.lifecycleOwner =this
        viewModel.updateRv()

        val adapter = TransactionProductAdapter(ProductTransListener {
            Log.i(espTag,"${it.product_name} clicked")
            viewModel.onProductClicked(it)


        },true)

        binding.espRv.adapter=adapter
        binding.btnSend.setOnClickListener {
            exportTextToWhatsApp(viewModel.exportText.value?:"","")
        }
        binding.searchBarProduct.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.filterProduct(newText)
                return true
            }
        })

        viewModel.newList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it.sortedBy { it.product_name })
            viewModel.draftText()

        })
        viewModel.isPickingProduct.observe(viewLifecycleOwner, Observer{
            if (it==true){
                binding.view1.visibility= View.VISIBLE
                binding.view2.visibility= View.GONE
            }else{
                binding.view1.visibility= View.GONE
                binding.view2.visibility= View.VISIBLE

            }
        })

        return binding.root
    }

    private fun exportTextToWhatsApp(text: String,phoneNumber:String?) {

        val phone = phoneNumber // country code + number
        val message = text
        val intent = if (phone!=null){
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$phone?text=${Uri.encode(message)}")
            }
        }else{
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, text)
                // Set the MIME type
                type = "text/plain"
                // Set the package name of WhatsApp
                setPackage("com.whatsapp")
            }

        }

        //sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivity(intent)
            // startActivity(sendIntent)
        }catch (e : Exception){
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }

    }

}