package com.example.app21try6.statement.manage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentManageBinding


class ManageFragment : Fragment() {

    private lateinit var binding: FragmentManageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= DataBindingUtil.inflate(inflater,R.layout.fragment_manage,container,false)

        binding.btnSuplier.setOnClickListener {
            this.findNavController().navigate(ManageFragmentDirections.actionManageFragmentToSuplierFragment())
        }

        binding.btnCustomer.setOnClickListener {
            this.findNavController().navigate(ManageFragmentDirections.actionManageFragmentToCustomerFragment())
        }
        binding.btnDiskon.setOnClickListener {
            this.findNavController().navigate(ManageFragmentDirections.actionManageFragmentToStatementHsFragment())
        }

        return binding.root
    }


}