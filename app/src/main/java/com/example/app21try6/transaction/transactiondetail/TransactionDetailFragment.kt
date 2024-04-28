package com.example.app21try6.transaction.transactiondetail

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import java.lang.Exception


class TransactionDetailFragment : Fragment() {
    private lateinit var binding:FragmentTransactionDetailBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_transaction_detail,container,false)
        val application= requireNotNull(this.activity).application
        val id= arguments?.let{ TransactionDetailFragmentArgs.fromBundle(it).id}
        Log.e("SUMVM","transum in TransEditFragmnet id is "+id.toString()+"")
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
        viewModel.sendReceipt.observe(viewLifecycleOwner){
            if (it==true){
                var exportedString=viewModel.generateReceiptText()

                exportTextToWhatsApp(exportedString)
            }
        }
        viewModel.navigateToEdit.observe(viewLifecycleOwner, Observer {
            it?.let {
                //Toast.makeText(context,id.toString(),Toast.LENGTH_SHORT).show()
                this.findNavController().navigate(TransactionDetailFragmentDirections.actionTransactionDetailFragmentToTransactionEditFragment(id))
                viewModel.onNavigatedToEdit()
                //var exportedString=""
                //exportTextToWhatsApp("Text successfully exported")
            }
        })



        return binding.root
    }
    private fun exportTextToWhatsApp(text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            // Set the MIME type
            type = "text/plain"
            // Set the package name of WhatsApp
            setPackage("com.whatsapp")
        }
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        try {
            startActivity(sendIntent)
        }catch (e : Exception){
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }
        // Check if WhatsApp is installed
        /*
        activity?.packageManager?.let { packageManager ->
            if (sendIntent.resolveActivity(packageManager) != null) {
                startActivity(sendIntent)
            } else {
                // If WhatsApp is not installed, show an error message
                Toast.makeText(requireContext(), "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
            }
        }

         */
    }

}