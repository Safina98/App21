package com.example.app21try6.transaction.transactiondetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.database.SummaryDatabase
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentDetailBinding
import com.example.app21try6.databinding.FragmentTransactionDetailBinding
import com.example.app21try6.transaction.transactionedit.TransactionEditDummyModel
import com.example.app21try6.transaction.transactionedit.TransactionEditFragmentArgs


class TransactionDetailFragment : Fragment() {
    private lateinit var binding:FragmentTransactionDetailBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_transaction_detail,container,false)
        val application= requireNotNull(this.activity).application
        val id= arguments?.let{ TransactionDetailFragmentArgs.fromBundle(it).id}

        val datasource1 = VendibleDatabase.getInstance(application).transSumDao
        val datasource2 = VendibleDatabase.getInstance(application).transDetailDao
        val viewModelFactory = TransactionDetailViewModelFactory(application,datasource1,datasource2,id!!)
        val viewModel =ViewModelProvider(this,viewModelFactory).get(TransactionDetailViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val adapter = TransactionDetailAdapter(TransDetailClickListener {

        })
        binding.recyclerViewDetailTrans.adapter = adapter

        viewModel.transDetail.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.navigateToEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
                //Toast.makeText(context,id.toString(),Toast.LENGTH_SHORT).show()
                this.findNavController().navigate(TransactionDetailFragmentDirections.actionTransactionDetailFragmentToTransactionEditFragment(id))
                viewModel.onNavigatedToEdit()
            }
        })



        return binding.root
    }

}