package com.example.app21try6.statement.report

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.Constants
import com.example.app21try6.database.models.BarChartModel
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.formatDateRange
import com.example.app21try6.formatRupiah
import com.example.app21try6.reportFormatDateRange
import kotlinx.coroutines.launch
import java.util.Date

class ReportViewModel(application: Application,
                      val expenseRepo: ExpensesRepository,
                        val transRepo: TransactionsRepository
): AndroidViewModel(application) {
    val _startDate= MutableLiveData<Date?>(null)
    val _endDate= MutableLiveData<Date?>(null)
    val _dateRangeString = MutableLiveData<String>("")

    private var _isDatePickerClicked = MutableLiveData<Boolean>()
    val isDatePickerClicked :LiveData<Boolean>get() = _isDatePickerClicked

    private val _HPPList= MutableLiveData<List<BarChartModel>>()
    val HPPList: LiveData<List<BarChartModel>> get() = _HPPList

    private val _BOPList= MutableLiveData<List<BarChartModel>>()
    val BOPList: LiveData<List<BarChartModel>> get() = _BOPList

    val totalTrans=MutableLiveData<String>()
    val totalHPP=MutableLiveData<String>()
    val labaKotor=MutableLiveData<String>()
    val totalBOP=MutableLiveData<String>()
    val totalPengeluaran=MutableLiveData<String>()
    val labaBersih=MutableLiveData<String>()

    fun setStartAndEndDateRange(startDate: Date?,endDate: Date?){
        viewModelScope.launch {
            _startDate.value = startDate
            _endDate.value = endDate
            _dateRangeString.value = reportFormatDateRange(startDate, endDate)
            //resetLogs()
        }
    }
    fun getCategoryByType(){
        viewModelScope.launch {

           val hppList= expenseRepo.getcExpenseByTipe(Constants.TIPEEXPENSECATOGORY.HPP,_startDate.value,_endDate.value)
            val bopList= expenseRepo.getcExpenseByTipe(Constants.TIPEEXPENSECATOGORY.BEBAN,_startDate.value,_endDate.value)
            val total = transRepo.getTotalTransactionAfterDiscount(null,_startDate.value,_endDate.value)?:0.0
            val totalhpp=expenseRepo.getTotalcExpenseByTipe(Constants.TIPEEXPENSECATOGORY.HPP,_startDate.value,_endDate.value)?:0.0
            val totalbop=expenseRepo.getTotalcExpenseByTipe(Constants.TIPEEXPENSECATOGORY.BEBAN,_startDate.value,_endDate.value)?:0.0
            val labakotor=total - totalhpp
            val totalpengeluaran=totalhpp+totalbop
            val lababersih=total-totalpengeluaran
            val totalText = formatRupiah(total)
            totalHPP.value = formatRupiah(totalhpp) ?:"Rp0"
            totalBOP.value=formatRupiah(totalbop)?:"Rp0"
            labaKotor.value=formatRupiah(labakotor)?:"Rp0"
            totalPengeluaran.value=formatRupiah(totalpengeluaran)?:"Rp0"
            labaBersih.value =formatRupiah(lababersih)?:"Rp0"

            totalTrans.value=totalText ?: "Rp."
            _HPPList.value=hppList
            _BOPList.value=bopList

        }
    }
    fun onStartDatePickerClick(){ _isDatePickerClicked.value = true }
    fun onStartDatePickerClicked(){ _isDatePickerClicked.value = false }
}