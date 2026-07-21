package com.example.app21try6.statement.report

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentReportBinding
import com.example.app21try6.databinding.FragmentTransactionPurchaseBinding


class ReportFragment : Fragment() {
    private lateinit var binding: FragmentReportBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_transaction_purchase,container,false)
        val application= requireNotNull(this.activity).application
        binding.lifecycleOwner=this
        return binding.root
    }

}