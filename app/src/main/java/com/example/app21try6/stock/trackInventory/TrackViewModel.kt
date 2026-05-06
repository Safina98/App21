package com.example.app21try6.stock.trackInventory

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.database.models.TracketailWarnaModel
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.formatDateRange
import kotlinx.coroutines.launch
import java.util.Date


class TrackViewModel(
    application: Application,
    val transRepo: TransactionsRepository):AndroidViewModel(application) {

    private val _trackWarnaList = MutableLiveData<List<TracketailWarnaModel>>()
    val trackWarnaList:LiveData<List<TracketailWarnaModel>> get() = _trackWarnaList

    private var currentOffset = 0
    private val pageSize = 20
    private var isLoading = false
    private var lastQuery = ""

    val _dateRangeString = MutableLiveData<String>("Pilih Tanggal")
    private val _selectedStartDate = MutableLiveData<Date?>()
    val selectedStartDate: LiveData<Date?> get() = _selectedStartDate
    //selected end date
    private val _selectedEndDate = MutableLiveData<Date?>()
    val selectedEndDate: LiveData<Date?> get() = _selectedEndDate

    private var _isStartDatePickerClicked = MutableLiveData<Boolean>()
    val isStartDatePickerClicked :LiveData<Boolean>get() = _isStartDatePickerClicked

    private var _navigateToTransDetail = MutableLiveData<Long?>()
    val navigateToTransDetail :LiveData<Long?>get() = _navigateToTransDetail


    fun updateDateRangeString(startDate: Date?, endDate: Date?) {
        _dateRangeString.value = formatDateRange(startDate, endDate)
    }

    fun setStartAndEndDateRange(startDate: Date?,endDate: Date?){
        viewModelScope.launch {
            _selectedStartDate.value = startDate
            _selectedEndDate.value = endDate
            //resetLogs()
        }
    }
    fun filterData(query: String?) {
        viewModelScope.launch {
            lastQuery = query ?: ""
            currentOffset = 0
            _trackWarnaList.value = emptyList()
            if (lastQuery!="")
            loadMore()
        }
    }

    fun loadMore() {
        if (isLoading) return
        viewModelScope.launch {
            isLoading = true
            val newItems = transRepo.getSubProductTrans(
                lastQuery,
                _selectedStartDate.value,
                _selectedEndDate.value,
                limit = pageSize,
                offset = currentOffset
            )
            val current = _trackWarnaList.value ?: emptyList()
            _trackWarnaList.value = current + newItems
            currentOffset += newItems.size
            isLoading = false
        }
    }

    fun updateRv(){
        viewModelScope.launch {
            filterData(lastQuery)
        }
    }

    //show hide date picker dialog
    fun onStartDatePickerClick(){ _isStartDatePickerClicked.value = true }
    fun onStartDatePickerClicked(){ _isStartDatePickerClicked.value = false }
    fun onNavigateToTransDetail(id:Long){_navigateToTransDetail.value=id}
    fun onNavigatedToTransDetail(){_navigateToTransDetail.value=null}
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
                val stockRepo= StockRepositories(application)
                val transRepo= TransactionsRepository(application)
                val bookRepo= BookkeepingRepository(application)
                val savedStateHandle = extras.createSavedStateHandle()
                return TrackViewModel(
                    application,transRepo
                ) as T
            }
        }
    }

}