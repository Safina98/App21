package com.example.app21try6.transaction.transactionall

import android.app.Application
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.database.TransSumDao
import com.example.app21try6.database.TransactionSummary
import com.example.app21try6.database.VendibleDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AllTransactionViewModel(application: Application,var dataSource1:TransSumDao):AndroidViewModel(application) {
   // val allTransactionSummary = dataSource1.getAllTransSum()
    private var _allTransactionSummary = MutableLiveData<List<TransactionSummary>>()
    val allTransactionSummary :LiveData<List<TransactionSummary>> get() = _allTransactionSummary
    private val _navigateToTransDetail = MutableLiveData<Int>()
    val navigateToTransDetail: LiveData<Int> get() = _navigateToTransDetail
    private var _isStartDatePickerClicked = MutableLiveData<Boolean>()
    val isStartDatePickerClicked :LiveData<Boolean>get() = _isStartDatePickerClicked

    private var _isEndDatePickerClicked = MutableLiveData<Boolean>()
    val isEndDatePickerClicked :LiveData<Boolean>get() = _isEndDatePickerClicked

    private val _selectedStartDate = MutableLiveData<Date>()
    val selectedStartDate: LiveData<Date> get() = _selectedStartDate

    private val _selectedEndDate = MutableLiveData<Date>()
    val selectedEndDate: LiveData<Date> get() = _selectedEndDate

    private val _selectedSpinner = MutableLiveData<String>()
    val selectedSpinner: LiveData<String> get() =_selectedSpinner


    init {

    }

    fun setSelectedSpinner(value:String){
        _selectedSpinner.value = value
    }

    fun setStartDateRange(startDate: Date){
        _selectedStartDate.value = startDate
        if (selectedEndDate.value ==null)
        {_selectedEndDate.value=startDate}
        Log.i("DATEPROB","setStartDateRange stardate ${startDate.toString()}")
        Log.i("DATEPROB","setStartDateRange stardate mutable ${_selectedStartDate.value .toString()}")
        Log.i("DATEPROB","setStartDateRange enddate mutable ${_selectedEndDate.value .toString()}")
    }
    fun setEndDateRange(startDate: Date){
        _selectedEndDate.value=startDate
        Log.i("DATEPROB","setEndDateRange stardate ${startDate.toString()}")
        Log.i("DATEPROB","setEndDateRange stardate mutable ${_selectedStartDate.value .toString()}")
        Log.i("DATEPROB","setEndDateRange enddate mutable ${_selectedEndDate.value .toString()}")

    }
    fun onNavigatetoTransDetail(id:Int){
        _navigateToTransDetail.value = id
    }
    fun onNavigatedToTransDetail(){
        this._navigateToTransDetail.value = null

    }
    fun onStartDatePickerClick(){ _isStartDatePickerClicked.value = true }
    //fun onEndDatePickerClick(){ _is_date_picker_clicked.value = "END" }
    fun onDatePickerClicked(){ _isStartDatePickerClicked.value = false }
    fun onEndDatePickerClick(){ _isEndDatePickerClicked.value = true }
    fun onEndDatePickerClicked(){ _isEndDatePickerClicked.value = false }

    private fun formatDate(date: Date?): String? {
        if (date != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(date)
        }
        return null
    }

    fun getStartOfDay(date: Date = Calendar.getInstance().time): Date {
        val calendar = Calendar.getInstance().apply {
            time = date
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.time
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateRv4(){
        var startDate: String?
        var endDate: String?

        if (selectedSpinner.value != "Date Range") {

            startDate = formatDate(selectedStartDate.value)
            endDate = formatDate(selectedEndDate.value)
        }
        else{
            startDate = formatDate(selectedStartDate.value)
            endDate = formatDate(selectedEndDate.value)
        }


       // Log.i("DATEPROB","updateRV4 ${startDate.toString()}")
        //Log.i("DATEPROB","updateRV4 ${endDate.toString()}")
        performDataFiltering(startDate, endDate)

                }


    private fun performDataFiltering(startDate: String?, endDate: String?) {
        viewModelScope.launch {

            val filteredData = withContext(Dispatchers.IO) {
                dataSource1.getFilteredData3( startDate, endDate)
            }
            _allTransactionSummary.value = filteredData

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