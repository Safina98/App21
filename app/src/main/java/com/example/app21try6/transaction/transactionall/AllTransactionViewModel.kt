package com.example.app21try6.transaction.transactionall

import android.app.Application
import android.os.Build
import android.util.Log
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

    //all transactionSummary
    private var _allTransactionSummary = MutableLiveData<List<TransactionSummary>>()
    val allTransactionSummary :LiveData<List<TransactionSummary>> get() = _allTransactionSummary
    private val _unFilteredrecyclerViewData = MutableLiveData<List<TransactionSummary>>()
    //selected start date
    private val _selectedStartDate = MutableLiveData<Date?>()
    val selectedStartDate: LiveData<Date?> get() = _selectedStartDate
    //selected end date
    private val _selectedEndDate = MutableLiveData<Date?>()
    val selectedEndDate: LiveData<Date?> get() = _selectedEndDate
    //selected spinner value
    private val _selectedSpinner = MutableLiveData<String>()
    val selectedSpinner: LiveData<String> get() =_selectedSpinner
    //show or hide start date picker dialog
    private var _isStartDatePickerClicked = MutableLiveData<Boolean>()
    val isStartDatePickerClicked :LiveData<Boolean>get() = _isStartDatePickerClicked
    //show or hide end date picker dialog
    private var _isEndDatePickerClicked = MutableLiveData<Boolean>()
    val isEndDatePickerClicked :LiveData<Boolean>get() = _isEndDatePickerClicked

    private val _navigateToTransDetail = MutableLiveData<Int>()
    val navigateToTransDetail: LiveData<Int> get() = _navigateToTransDetail

    fun setSelectedSpinner(value:String){
        _selectedSpinner.value = value
    }


    fun setStartDateRange(startDate: Date?){
        _selectedStartDate.value = startDate
        if (selectedEndDate.value ==null)
        {_selectedEndDate.value=startDate}
        Log.i("DATEPROB","setStartDateRange stardate ${startDate.toString()}")
        Log.i("DATEPROB","setStartDateRange stardate mutable ${_selectedStartDate.value .toString()}")
        Log.i("DATEPROB","setStartDateRange enddate mutable ${_selectedEndDate.value .toString()}")
    }
    fun setEndDateRange(startDate: Date?){
        _selectedEndDate.value=startDate
        Log.i("DATEPROB","setEndDateRange stardate ${startDate.toString()}")
        Log.i("DATEPROB","setEndDateRange stardate mutable ${_selectedStartDate.value .toString()}")
        Log.i("DATEPROB","setEndDateRange enddate mutable ${_selectedEndDate.value .toString()}")

    }
    //Filter data based on search query
    fun filterData(query: String?) {
        val list = mutableListOf<TransactionSummary>()
        if(!query.isNullOrEmpty()) {
            list.addAll(_unFilteredrecyclerViewData.value!!.filter {
                it.cust_name.lowercase(Locale.getDefault()).contains(query.toString().lowercase(Locale.getDefault()))})
        } else {
            list.addAll(_unFilteredrecyclerViewData.value!!)
        }
        _allTransactionSummary.value =list
    }


    //convert date to string
    private fun formatDate(date: Date?): String? {
        if (date != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(date)
        }
        return null
    }

    //get today's date
    @RequiresApi(Build.VERSION_CODES.O)
    private fun constructTodaysDate(month: Int): String? {
        val date =Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }
    //get yesterday's date
    @RequiresApi(Build.VERSION_CODES.O)
    private fun constructYesterdayDate(month: Int): String? {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val date = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date)
    }

    //Construcnt startDate and endDate spinner select or date picker select
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateRv4(){
        var startDate: String?
        var endDate: String?
        if (selectedSpinner.value != "Date Range") {
            // Extract the month value from the selected date spinner
            if (selectedSpinner.value == "Hari Ini") {
                startDate = constructTodaysDate(0)
                endDate =constructTodaysDate(0)
            }
            else {
                if (selectedSpinner.value=="Kemarin")
                {
                    startDate = constructYesterdayDate(1)
                    endDate = constructYesterdayDate(12)}
                else{
                    // Invalid month value, handle the error case
                    startDate = null
                    endDate = null
                }
            }
        } else {
            // Date range option selected, use the selected start and end dates
            startDate = formatDate(selectedStartDate.value)
            endDate = formatDate(selectedEndDate.value)
        }
        performDataFiltering(startDate, endDate)

                }
    //filter data from database by date
    private fun performDataFiltering(startDate: String?, endDate: String?) {
        viewModelScope.launch {
            val filteredData = withContext(Dispatchers.IO) {
                dataSource1.getFilteredData3( startDate, endDate)
            }
            _allTransactionSummary.value = filteredData
            _unFilteredrecyclerViewData.value = filteredData

        }
    }
    //show hide date picker dialog
    fun onStartDatePickerClick(){ _isStartDatePickerClicked.value = true }
    fun onStartDatePickerClicked(){ _isStartDatePickerClicked.value = false }
    fun onEndDatePickerClick(){ _isEndDatePickerClicked.value = true }
    fun onEndDatePickerClicked(){ _isEndDatePickerClicked.value = false }
    //Navigation
    fun onNavigatetoTransDetail(id:Int){ _navigateToTransDetail.value = id }
    fun onNavigatedToTransDetail(){ this._navigateToTransDetail.value = null }

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