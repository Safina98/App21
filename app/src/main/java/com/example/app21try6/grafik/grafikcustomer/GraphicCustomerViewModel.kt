package com.example.app21try6.grafik.grafikcustomer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.database.models.BarChartModel
import com.example.app21try6.database.models.CustomerWithTotalTransModel
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.getMonthNumber
import kotlinx.coroutines.launch

class GraphicCustomerViewModel (
    private val stockRepo:StockRepositories,
    private val bookRepo: BookkeepingRepository,
    private val transRepo:TransactionsRepository,
    application: Application
): AndroidViewModel(application){

    private val _custWithTotalTrans= MutableLiveData<List<CustomerWithTotalTransModel>>()
    val custWithTotalTrans : LiveData<List<CustomerWithTotalTransModel>> get() = _custWithTotalTrans

    private val _barChartModel= MutableLiveData<List<BarChartModel>>()
    val barChartModel: LiveData<List<BarChartModel>>  get() = _barChartModel

    private val _rvData=MutableLiveData<List<TransactionSummary>>()
    val rvData:LiveData<List<TransactionSummary>> get() = _rvData

    private val _selectedYearSpinner = MutableLiveData<String>("ALL")
    private val _selectedBulanSpinner = MutableLiveData<String>("ALL")

    fun setSelectedYearValueStok(selectedItem:String){
        _selectedYearSpinner.value = selectedItem
    }

    fun setSelectedYear(year: String) {
        _selectedYearSpinner.value = year
        filterCustomer(_selectedBulanSpinner.value ?: "ALL", year)
    }

    fun setSelectedBulan(bulan: String) {
        _selectedBulanSpinner.value = bulan
        filterCustomer(bulan, _selectedYearSpinner.value ?: "ALL")
    }

    fun getRvData(list: List<CustomerWithTotalTransModel>) {
        val tsList = mutableListOf<TransactionSummary>()
        var id = -1L
        list.forEach {
            // Create a new TransactionSummary object for each entry
            val item = TransactionSummary()
            item.cust_name = it.customerBussinessName
            item.total_trans = it.totalAfterDiscount
            item.total_after_discount=it.totalAfterDiscount
            item.tSCloudId = id
            tsList.add(item)
            id -= 1
        }
        _rvData.value = tsList
    }

    fun filterCustomer(selectedBulan: String, selectedTahun: String) {
        viewModelScope.launch {
            val month = getMonthNumber(_selectedBulanSpinner.value)            // null if "ALL"
            val year = selectedTahun.takeIf { it != "ALL" } // null if "ALL"
            val result = transRepo.getCustomerWithTotalTrans(month, year)
            //_custWithTotalTrans.value = result
            _barChartModel.value=result
        }

    }
    fun CustomerWithTotalTransModel.toBarChartModel() = BarChartModel(
        label = this.customerBussinessName,
        value = this.totalAfterDiscount
    )

    fun List<CustomerWithTotalTransModel>.toBarChartModelList() = this.map { it.toBarChartModel() }

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
                val stockRepo=StockRepositories(application)
                val transRepo=TransactionsRepository(application)
                val bookRepo=BookkeepingRepository(application)
                val savedStateHandle = extras.createSavedStateHandle()
                return GraphicCustomerViewModel(
                    stockRepo,
                    bookRepo,
                    transRepo,
                    application
                ) as T
            }
        }
    }

}