package com.example.app21try6.stock.subdetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.app21try6.R
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.databinding.FragmentDetailBinding


class DetailFragment : Fragment() {
    private lateinit var binding: FragmentDetailBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_detail,container,false)
        val application = requireNotNull(this.activity).application
        val dataSource2 = VendibleDatabase.getInstance(application).subProductDao
        val id = arguments?.let { DetailFragmentArgs.fromBundle(it).subId}
        val id_ = id?.map { it.toInt() }?.toTypedArray()
        val viewModelFactory = DetailViewModelFactory(dataSource2,application,id_!!)
        binding.lifecycleOwner =this
        val viewModel = ViewModelProvider(this,viewModelFactory)
            .get(DetailViewModel::class.java)
        binding.detailViewModel = viewModel

        viewModel.item_.observe(viewLifecycleOwner, Observer {
            Toast.makeText(context,"detail",Toast.LENGTH_SHORT).show()
        })

        // Inflate the layout for this fragment
        return binding.root
    }

}