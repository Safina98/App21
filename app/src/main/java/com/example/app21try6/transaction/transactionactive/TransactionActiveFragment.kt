package com.example.app21try6.transaction.transactionactive

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
import com.example.app21try6.databinding.FragmentTransactionActiveBinding

class TransactionActiveFragment : Fragment() {
    private lateinit var binding: FragmentTransactionActiveBinding
    private var dummyModel = listOf<ActiveDummyModel>(
        ActiveDummyModel(1,"chessy",200000,"29-09-2910"),
        ActiveDummyModel(1,"global",200000,"29-09-2910"),
        ActiveDummyModel(1,"makassar variasi",200000,"29-09-2910"),
        ActiveDummyModel(1,"Laquna",200000,"29-09-2910"),
        ActiveDummyModel(1,"Fiesta",200000,"29-09-2910"),
        ActiveDummyModel(1,"Susan",200000,"29-09-2910"),
        ActiveDummyModel(1,"RumahKursi",200000,"29-09-2910"),
        ActiveDummyModel(1,"s",200000,"29-09-2910"),
        ActiveDummyModel(1,"s",200000,"29-09-2910"),
        ActiveDummyModel(1,"d",200000,"29-09-2910"),
        ActiveDummyModel(1,"g",200000,"29-09-2910"),
        ActiveDummyModel(1,"g",200000,"29-09-2910"))
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_transaction_active,container,false)
        val application = requireNotNull(this.activity).application
        val datasource1= SummaryDatabase.getInstance(application).transSumDao
        val datasource2= SummaryDatabase.getInstance(application).transDetailDao
        val viewModelFactory = TransactionActiveViewModelFactory(application,datasource1,datasource2)
        val viewModel = ViewModelProvider(this,viewModelFactory).get(TransactionActiveViewModel::class.java)
        binding.setLifecycleOwner(this)
        binding.viewModel = viewModel
        val adapter = TransactionActiveAdapter(ActiveClickListener {
            viewModel.onNavigatetoTransDetail(it.sum_id)
           // Toast.makeText(context,it.sum_id.toString(),Toast.LENGTH_SHORT).show()

        })
        binding.recyclerViewActiveTrans.adapter = adapter
        //adapter.submitList(dummyModel)

        viewModel.active_trans.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.navigateToTransEdit.observe(viewLifecycleOwner, Observer {
          it?.let{
             this.findNavController().navigate(TransactionActiveFragmentDirections.actionTransactionActiveFragmentToTransactionEditFragment(it))
            viewModel.onNavigatedToTransEdit()
          }
        })
        viewModel.navigateToTransDetail.observe(viewLifecycleOwner, Observer {
            it?.let {

                this.findNavController().navigate(TransactionActiveFragmentDirections.actionTransactionActiveFragmentToTransactionDetailFragment(it))
            viewModel.onNavigatedToTransDetail()
            }
        })
        // Inflate the layout for this fragment
        return binding.root
    }
}