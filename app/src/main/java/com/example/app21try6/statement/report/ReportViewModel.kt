package com.example.app21try6.statement.report

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.Constants
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.formatDateRange
import kotlinx.coroutines.launch
import java.util.Date

class ReportViewModel(application: Application,
                      val expenseRepo: ExpensesRepository,
): AndroidViewModel(application) {
    val _startDate= MutableLiveData<Date?>(null)
    val _endDate= MutableLiveData<Date?>(null)
    val _dateRangeString = MutableLiveData<String>("Pilih Tanggal")

    private var _isDatePickerClicked = MutableLiveData<Boolean>()
    val isDatePickerClicked :LiveData<Boolean>get() = _isDatePickerClicked


    fun setStartAndEndDateRange(startDate: Date?,endDate: Date?){
        viewModelScope.launch {
            _startDate.value = startDate
            _endDate.value = endDate
            //resetLogs()
        }
    }
    fun updateDateRangeString(startDate: Date?, endDate: Date?) {
        _dateRangeString.value = formatDateRange(startDate, endDate)
    }
    fun getCategoryByType(){
        viewModelScope.launch {
           val list= expenseRepo.getcExpenseByTipe(Constants.TIPEEXPENSECATOGORY.HPP,_startDate.value,_endDate.value)
            list.forEach {
                Log.i("ReportProbs","${it.label} ${it.value}")
            }
        }
    }
    fun onStartDatePickerClick(){ _isDatePickerClicked.value = true }
    fun onStartDatePickerClicked(){ _isDatePickerClicked.value = false }
}