package com.example.app21try6.bookkeeping.summary

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.app21try6.bookkeeping.editdetail.BookkeepingViewModel
import com.example.app21try6.databinding.FragmentBookSummaryBinding

class BookSummaryFragment : Fragment(){

    // constant code for runtime permissions
    private val PERMISSION_REQUEST_CODE = 200

    private lateinit var binding: FragmentBookSummaryBinding
    private val summaryViewModel :BookkeepingViewModel by activityViewModels { BookkeepingViewModel.Factory }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, com.example.app21try6.R.layout.fragment_book_summary,container,false)
        binding.lifecycleOwner = this
        binding.summaryViewModel = summaryViewModel
        if (checkPermission()) {
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
           requestPermission()
        }
        val act = activity as AppCompatActivity?
        if (act!!.supportActionBar != null) {
            val img =  requireActivity().findViewById<ImageView>(com.example.app21try6.R.id.delete_image)
            img.visibility = View.GONE
            val btn_linear =  requireActivity().findViewById<ConstraintLayout>(com.example.app21try6.R.id.linear_btn)
            btn_linear.visibility=View.GONE
        }
        summaryViewModel.initialRv()
        val adapter = SummaryAdapter(SummaryListener {
            summaryViewModel.onRvClick(it)
            val monthname = summaryViewModel.selectedMonth.value
            val defaultPosition =summaryViewModel.months_list.indexOf(monthname)
            binding.spinnerM.setSelection(defaultPosition)
                })
        binding.recyclerViewSumary.adapter = adapter

        val adapterYear = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(com.example.app21try6.R.array.tahun)
        )
        val adapterMonth = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            resources.getStringArray(com.example.app21try6.R.array.bulan)
        )
      //  val adapterYear = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, summaryViewModel.year_list)
        binding.spinner.adapter = adapterYear

        //val adapterMonth = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, summaryViewModel.months_list)
        binding.spinnerM.adapter = adapterMonth

        val catname = summaryViewModel.selectedMonth.value
        val defaultPosition =summaryViewModel.months_list.indexOf(catname)
        binding.spinnerM.setSelection(defaultPosition)
        val thisyear = summaryViewModel.year.toString()
        val defaultPositionYear =summaryViewModel.year_list.indexOf(thisyear)
        binding.spinner.setSelection(defaultPositionYear)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                summaryViewModel.setSelectedYear(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        binding.spinnerM.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                summaryViewModel.setSelectedMonth(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        summaryViewModel.selectedYear.observe(viewLifecycleOwner) {
        }
        summaryViewModel.selectedMonth.observe(viewLifecycleOwner){
           // summaryViewModel.updateRvNew()
        }
        summaryViewModel.recyclerViewData.observe(viewLifecycleOwner){
            it?.let {
                adapter.submitList(it)
                adapter.notifyDataSetChanged()
            }
        }
        summaryViewModel.isLoading.observe(viewLifecycleOwner, Observer { isLoading ->
            if(isLoading==true){
                binding.recyclerViewSumary.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            }else{
                binding.recyclerViewSumary.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
            //binding.progressBar.visibility = if (isLoading ==true) View.VISIBLE else View.GONE
            })

        summaryViewModel.navigateBookKeeping.observe(viewLifecycleOwner, Observer { date->
            date?.let {
                this.findNavController().navigate(BookSummaryFragmentDirections.actionBookSummaryFragmentToBookKeepeingFragment3(date))
                summaryViewModel.onDayNavigated()
            }
        })

        return binding.root
    }

    private fun checkPermission(): Boolean {
        // checking of permissions.
        val permission1 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(requireActivity(),arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
    }

}