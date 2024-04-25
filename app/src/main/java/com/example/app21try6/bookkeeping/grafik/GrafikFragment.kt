package com.example.app21try6.bookkeeping.grafik

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.app21try6.R
import com.example.app21try6.databinding.FragmentGrafikBinding

class GrafikFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding :FragmentGrafikBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_grafik,container,false)


        return binding.root
    }



}