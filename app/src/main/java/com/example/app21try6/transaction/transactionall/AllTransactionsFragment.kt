package com.example.app21try6.transaction.transactionall

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.bookkeeping.editdetail.BookkeepingViewModel
import com.example.app21try6.databinding.FragmentAllTransactionsBinding
import com.example.app21try6.databinding.TransactionAllItemListBinding
import com.example.app21try6.transaction.transactionactive.ActiveClickListener
import com.example.app21try6.transaction.transactionactive.CheckBoxListenerTransActive
import com.example.app21try6.transaction.transactionactive.TransactionActiveAdapter
import com.example.app21try6.transaction.transactionactive.TransactionActiveFragmentDirections

class AllTransactionsFragment : Fragment() {
    private lateinit var binding: FragmentAllTransactionsBinding
    private val viewModel  : AllTransactionViewModel by activityViewModels { AllTransactionViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_all_transactions,container,false)
        // Inflate the layout for this fragment
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        val adapter = AllTransactionAdapter(AllTransClickListener {
            viewModel.onNavigatetoTransDetail(it.sum_id)
        }
            , CheckBoxListenerTransAll{ view, stok ->
                val cb = view as CheckBox
                //stok.is_checked = cb.isChecked
              //  viewModel.onCheckBoxClicked(stok, cb.isChecked)
            }
        )
        binding.recyclerViewAllTrans.adapter = adapter
        viewModel.allTransactionSummary.observe(viewLifecycleOwner){
            it?.let {
                adapter.submitList(it)
            }
        }
        viewModel.navigateToTransDetail.observe(viewLifecycleOwner){
            it?.let {
                this.findNavController().navigate(AllTransactionsFragmentDirections.actionAllTransactionsFragmentToTransactionDetailFragment(it))
                viewModel.onNavigatedToTransDetail()
            }
        }

        return binding.root

    }

}