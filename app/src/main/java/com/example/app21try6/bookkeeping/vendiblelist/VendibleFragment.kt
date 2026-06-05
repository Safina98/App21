package com.example.app21try6.bookkeeping.vendiblelist

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.databinding.FragmentVendibleBinding
import com.example.app21try6.transaction.transactionedit.TransactionEditViewModel
import com.example.app21try6.utils.DialogUtils
import com.google.android.material.textfield.TextInputEditText

class VendibleFragment : Fragment() {
    private lateinit var binding: FragmentVendibleBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_vendible,container,false)
        val application = requireNotNull(this.activity).application

        val repository = StockRepositories(application)
        val sumRepo = BookkeepingRepository(application)
        val date= arguments?.let { VendibleFragmentArgs.fromBundle(it).date }

        val viewModelFactory = VendibleViewModelFactory(sumRepo,repository, date!!,application)
        binding.lifecycleOwner =this
        val vendibleViewModel =ViewModelProvider(this,viewModelFactory)
            .get(VendibleViewModel::class.java)
        binding.vendibleViewModel = vendibleViewModel
        val adapter = VendibleAdapter(1, CheckBoxListener { view: View, product: Product ->
            val cb = view as CheckBox
            product.checkBoxBoolean = cb.isChecked
            Toast.makeText(context, product.checkBoxBoolean.toString(), Toast.LENGTH_SHORT).show()

            vendibleViewModel.onCheckBoxClicked(product, cb.isChecked)
        }, TextListener { view, vendible ->

        }, DelLongListenerV {
           // deleteDialog(it, vendibleViewModel)
            DialogUtils.showDeleteDialog(requireContext(), vendibleViewModel, it, { vm, item -> (vm as VendibleViewModel).deleteItemVendible(it.productCloudId) })
        })


        binding.recyclerViewVendible.adapter =adapter
        var h =0
        vendibleViewModel.itemCathPosition.observe(viewLifecycleOwner, Observer {t->
            vendibleViewModel.cathList.observe(viewLifecycleOwner, Observer {})
            h = t
            vendibleViewModel.all_item_from_db.observe(viewLifecycleOwner, Observer {
                it?.let { if (h==t){
                    adapter.submitList(it.sortedBy { it.product_name})
                    adapter.notifyDataSetChanged()
                }}
            })
        })

        vendibleViewModel.navigateToEditDetail.observe(viewLifecycleOwner, Observer {
            if (it==true){
                //this.findNavController().navigate(VendibleFragmentDirections.actionVendibleFragmentToBookKeepeingFragment3(date))
                requireActivity().onBackPressed()
                vendibleViewModel.onNavigatedToEditThings()
            }
        })


        return binding.root
    }

}