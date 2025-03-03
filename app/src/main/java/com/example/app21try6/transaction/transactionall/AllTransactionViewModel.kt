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
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.formatRupiah
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AllTransactionViewModel(application: Application,var dataSource1: TransSumDao):AndroidViewModel(application) {


    private var viewModelJob = Job()
    //ui scope for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)
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
    private val _selectedSpinner = MutableLiveData<String>("Hari Ini")
    val selectedSpinner: LiveData<String> get() =_selectedSpinner
    //show or hide start date picker dialog
    private var _isStartDatePickerClicked = MutableLiveData<Boolean>()
    val isStartDatePickerClicked :LiveData<Boolean>get() = _isStartDatePickerClicked

    //To update icons color on night mode or liht mode
    private var _uiMode = MutableLiveData<Int>(16)
    val uiMode :LiveData<Int> get() =_uiMode

    val _dateRangeString = MutableLiveData<String>()

    val queryM=MutableLiveData<String?>()

    private val _navigateToTransDetail = MutableLiveData<Int>()
    val navigateToTransDetail: LiveData<Int> get() = _navigateToTransDetail
    var itemCount :LiveData<String> = allTransactionSummary.map { items->
        "${items.size} transaksi"
    }
    var totalTrans:LiveData<String> = allTransactionSummary.map{items->
        val totalSum = items.sumOf { it.total_trans }
        val formattedTotal = formatRupiah(totalSum)
        "${formattedTotal}"
    }

    val isTextViewVisible = MutableLiveData<Boolean>(true)

    private val _selectedTransSum= MutableLiveData<Int?>()
    val selectedTransSum:LiveData<Int?>get() = _selectedTransSum


    fun toggleTextViewVisibility() {
        isTextViewVisible.value = !(isTextViewVisible.value ?: true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setSelectedSpinner(value:String){
        if(selectedSpinner.value!=value){
            _selectedSpinner.value = value
            val (start, end) = when (value){
                "Hari Ini"->getTodayStartAndEnd(0)
                "Kemarin"->getTodayStartAndEnd(-1)
                "Semua"->Pair(null,null)
                "Bulan Ini"->getCurrentMonthDateRange()
                "Tahun Ini"->getCurrentYearDateRange()
                else->Pair(_selectedStartDate.value,selectedEndDate.value)
            }

            _selectedStartDate.value = start
            _selectedEndDate.value = end
            updateRv5()
            updateDateRangeString(start,end)
        }
    }

    fun updateDateRangeString(startDate: Date?, endDate: Date?) {
        _dateRangeString.value = formatDateRange(startDate, endDate)
    }
    //Set ui to change icons color when night mode or day mode on
    fun setUiMode(mode:Int){
        _uiMode.value = mode
    }

    fun getSelectedTransSumId(id:Int?){
        if (_selectedTransSum.value==id){
            _selectedTransSum.value=null
        }else {_selectedTransSum.value=id}
    }

    private fun formatDateRange(startDate: Date?, endDate: Date?): String {
        return if (startDate != null && endDate != null) {
            val dateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("in", "ID"))
            val startDateString = dateFormat.format(startDate)
            val endDateString = dateFormat.format(endDate)
            "$startDateString - $endDateString"
        } else {
            "Pilih Tanggal"
        }
    }
    fun setStartAndEndDateRange(startDate: Date?,endDate: Date?){
        viewModelScope.launch {
            _selectedStartDate.value = startDate
            _selectedEndDate.value = endDate
        }
    }
    //Filter data based on search query
    fun filterData(query: String?) {
        uiScope.launch {
            queryM.value=query
            val list = mutableListOf<TransactionSummary>()
            if(!query.isNullOrEmpty()) {

                val listFilterByTransName= getSummaryByQuery(query)

                list.addAll(_allTransactionSummary.value!!.filter {

                    it.cust_name.lowercase(Locale.getDefault()).contains(query.toString().lowercase(Locale.getDefault()))})

                list.addAll(listFilterByTransName)

                val distinctList = list.distinctBy { it.sum_id }

                _allTransactionSummary.value =distinctList
            } else {
                list.addAll(_unFilteredrecyclerViewData.value!!)
                _allTransactionSummary.value =list
            }
            Log.i("SearchBarProbs","Query: $query")

        }
    }
    private suspend fun getSummaryByQuery(query: String):List<TransactionSummary>{
        return withContext(Dispatchers.IO){
            dataSource1.getTransactionSummariesByItemName(query,_selectedStartDate.value,selectedEndDate.value)
        }
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
    private fun getTodayStartAndEnd(day:Int): Pair<Date, Date> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, day)
        // Start of the day (00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time

        // End of the day (23:59)

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.time

        // Format the dates
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        return Pair(startOfDay, endOfDay)
    }

    fun getCurrentMonthDateRange(): Pair<Date, Date> {
        val calendar = Calendar.getInstance()

        // Set to the first day of the current month at 00:00
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val firstDayOfMonth = calendar.time

        // Set to the last day of the current month at 23:59
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val lastDayOfMonth = calendar.time

        return Pair(firstDayOfMonth, lastDayOfMonth)
    }

    fun getCurrentYearDateRange(): Pair<Date, Date> {
        val calendar = Calendar.getInstance()

        // Set to the first day of the current year at 00:00
        calendar.set(Calendar.MONTH, Calendar.JANUARY)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val firstDayOfYear = calendar.time

        // Set to the last day of the current year at 23:59
        calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        calendar.set(Calendar.DAY_OF_MONTH, 31)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val lastDayOfYear = calendar.time

        return Pair(firstDayOfYear, lastDayOfYear)
    }



    fun updateRv5(){
        viewModelScope.launch {
            performDataFiltering(selectedStartDate.value, selectedEndDate.value)
        }
    }
    private fun performDataFiltering(startDate: Date?, endDate: Date?) {
        viewModelScope.launch {
            try {
                val filteredData = withContext(Dispatchers.IO) {
                    dataSource1.getFilteredData4(startDate, endDate,null)
                }
                // Update LiveData on the main thread
                withContext(Dispatchers.Main) {
                    _allTransactionSummary.value = filteredData
                    _unFilteredrecyclerViewData.value = filteredData
                }
            } catch (e: Exception) {
                Log.e("DataFilteringError", "Error during data filtering", e)
            }
        }
    }

    //show hide date picker dialog
    fun onStartDatePickerClick(){ _isStartDatePickerClicked.value = true }
    fun onStartDatePickerClicked(){ _isStartDatePickerClicked.value = false }

    //Navigation
    fun onNavigatetoTransDetail(id:Int){ _navigateToTransDetail.value = id }
    fun onNavigatedToTransDetail(){ this._navigateToTransDetail.value = null }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
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