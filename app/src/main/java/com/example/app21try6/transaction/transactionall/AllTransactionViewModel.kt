package com.example.app21try6.transaction.transactionall

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.database.TransSumDao
import com.example.app21try6.database.TransactionSummary
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.transaction.transactionselect.TransactionSelectViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AllTransactionViewModel(application: Application,var dataSource1:TransSumDao):AndroidViewModel(application) {
    val allTransactionSummary = dataSource1.getAllTransSum()
    private val _navigateToTransDetail = MutableLiveData<Int>()
    val navigateToTransDetail: LiveData<Int> get() = _navigateToTransDetail
    private var _is_date_picker_clicked = MutableLiveData<Boolean>(false)
    val is_date_picker_clicked :LiveData<Boolean>get() = _is_date_picker_clicked

    private val _selectedStartDate = MutableLiveData<Date>()
    val selectedStartDate: LiveData<Date> get() = _selectedStartDate

    private val _selectedEndDate = MutableLiveData<Date>()
    val selectedEndDate: LiveData<Date> get() = _selectedEndDate

    init {

    }

    fun setDateRange(startDate: Date, endDate: Date){
        _selectedStartDate.value = startDate
        _selectedEndDate.value=endDate
    }

    fun onNavigatetoTransDetail(id:Int){
        _navigateToTransDetail.value = id
    }
    fun onNavigatedToTransDetail(){
        this._navigateToTransDetail.value = null

    }
    fun onDatePickerClick(){ _is_date_picker_clicked.value = true }
    fun onDatePickerClicked(){ _is_date_picker_clicked.value = false }

    private fun formatDate(date: Date?): String? {
        if (date != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(date)
        }
        return null
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateRv4(){
        //val type = if (selectedTipeSpinner.value == "ALL") null else selectedTipeSpinner.value
        val startDate: String?
        val endDate: String?
        startDate = formatDate(selectedStartDate.value)
        endDate = formatDate(selectedEndDate.value)
        performDataFiltering( startDate, endDate)


                }


private fun performDataFiltering(startDate: String?, endDate: String?) {
    viewModelScope.launch {
       /*
        val categoryId = withContext(Dispatchers.IO) {
            if (category == "ALL") null else datasource1.getCategoryIdByName(category)
        }
        val filteredData = withContext(Dispatchers.IO) {
            datasource2.getFilteredData3(type, categoryId, startDate, endDate)
        }
        val filteredSum =  withContext(Dispatchers.IO) {
            datasource2.getFilteredDataSum(type, categoryId, startDate, endDate)
        }

        */

        //_recyclerViewData.value = filteredData
        //_unFilteredrecyclerViewData.value = filteredData
        //_filter_trans_sum.value = filteredSum
        //_filteredDataSum.value = _filter_trans_sum.value
    }

}

    companion object {
        @JvmStatic
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                // Create a SavedStateHandle for this ViewModel from extras
                val dataSource1 = VendibleDatabase.getInstance(application).transSumDao

                return AllTransactionViewModel(
                    application,dataSource1
                ) as T
            }
        }
    }

}