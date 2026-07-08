package com.example.app21try6.stock.upsertproduk

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.app21try6.R
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.databinding.FragmentInputUpdateProductBinding
import com.example.app21try6.stock.brandstock.BrandStockViewModel
import com.example.app21try6.stock.brandstock.BrandStockViewModelFactory
import com.example.app21try6.stock.subproductstock.SubProductStockFragmentArgs
import java.util.Calendar
import java.util.Date

class InputUpdateProduct : Fragment() {

    private lateinit var binding: FragmentInputUpdateProductBinding
    private lateinit var viewModel: UpsertProductViewModel
    private var list= mutableListOf<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=
            DataBindingUtil.inflate(inflater, R.layout.fragment_input_update_product,container,false)
        val application = requireNotNull(this.activity).application
        binding.lifecycleOwner = viewLifecycleOwner
        val id = arguments?.let { InputUpdateProductArgs.fromBundle(it).id }
        Log.i("ProductProbs","InputUpdateProduct arguments $id")
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.clearMutable()
                    findNavController().popBackStack()
                }
            }
        )
        val repository = StockRepositories(application)
        val discountRepository= DiscountRepository(application)
        val transactionsRepository= TransactionsRepository(application)

        viewModel = ViewModelProvider(
            this,
            UpsertProductViewModelFactory(id,repository, discountRepository, transactionsRepository,application)
        )
            .get(UpsertProductViewModel::class.java)
        binding.viewModel=viewModel
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -30)
        val thirtyDaysAgo: Date = calendar.time
        viewModel.setStartAndEndDateRange(thirtyDaysAgo, Date())
        viewModel.updateDateRangeString(thirtyDaysAgo, Date())
        viewModel.getLongClickedProduct(id?:0L)
        viewModel.getTotalQtyPerProduct()

        // Observe the ViewModel LiveData and update the adapter

        viewModel.isStartDatePickerClicked.observe(viewLifecycleOwner) {
            if (it==true){
                showDatePickerDialog()
                viewModel.onStartDatePickerClicked()
            }
        }
        viewModel.discountList.observe(viewLifecycleOwner, Observer { discounts ->
            if (discounts != null) {
                val adapterr = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    discounts
                )
                //autoCompleteTextView.setAdapter(adapterr)
                binding.textDiscount.setAdapter(adapterr)
            }
        })
        viewModel.ctgNameList.observe(viewLifecycleOwner){ entries->
            if(entries!=null){
                val adapterCat = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    entries
                )
                binding.textCategory.setAdapter(adapterCat)
            }
        }
        viewModel.branName.observe(viewLifecycleOwner){

        }
        binding.textCategory.setOnItemClickListener { parent, view, position, id ->
            val selectedCategory = parent.getItemAtPosition(position) as String
            //viewModel.onCategorySelected(selectedCategory)
        }
        viewModel.brandList.observe(viewLifecycleOwner) { brands ->
            if (brands != null) {
                val adapterBrand = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_dropdown_item_1line,
                    brands
                )
                binding.textBrand.setAdapter(adapterBrand)
            }
        }
        viewModel.navigateBackToBrandStok.observe(viewLifecycleOwner, Observer {
            if (it !=null) {
                requireActivity().onBackPressedDispatcher.onBackPressed()
                viewModel.clearMutable()
                viewModel.onNavigatedBackToBrandStock()
            }
        })
        return binding.root
    }
    fun handleBackPress(): Boolean {
        viewModel.clearMutable()
        findNavController().popBackStack()
        return true
    }
    private fun showDatePickerDialog() {
        //clearSearchQuery()
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.pop_up_date_picker, null)
        val datePickerStart = dialogView.findViewById<DatePicker>(R.id.datePickerStart)
        val datePickerEnd = dialogView.findViewById<DatePicker>(R.id.datePickerEnd)
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Date Range")
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val startYear = datePickerStart.year
                val startMonth = datePickerStart.month
                val startDay = datePickerStart.dayOfMonth
                val endYear = datePickerEnd.year
                val endMonth = datePickerEnd.month
                val endDay = datePickerEnd.dayOfMonth

                val startDate = Calendar.getInstance().apply {
                    set(startYear, startMonth, startDay, 0, 0, 1) // Set time to start of the day
                    set(Calendar.MILLISECOND, 0)
                }.time

                val endDate = Calendar.getInstance().apply {
                    set(endYear, endMonth, endDay, 23, 59, 58) // Set time to end of the day
                    set(Calendar.MILLISECOND, 999)
                }.time

                viewModel.updateDateRangeString(startDate,endDate)
                viewModel.setStartAndEndDateRange(startDate,endDate)

                // viewModel.setEndDateRange(endDate)
            }
            .setNegativeButton("Cancel", null)
            .create()
        dialog.setOnDismissListener {

        }

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), R.color.dialogbtncolor))
    }
}