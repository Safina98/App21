package com.example.app21try6.grafik

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.database.models.BarChartModel
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.getMonthNumber
import kotlinx.coroutines.launch

class GraphicViewModel(
    private val stockRepo:StockRepositories,
    private val transRepo:TransactionsRepository,
    application: Application
                      ): AndroidViewModel(application) {



    private val _productBCModel = MutableLiveData<List<BarChartModel>>()
    val productBCModel: LiveData<List<BarChartModel>> get() = _productBCModel

    private val _profitBCModel = MutableLiveData<List<BarChartModel>>()
    val profitBCModel: LiveData<List<BarChartModel>> get() = _profitBCModel


    //spinner category entries
    private var _categoryEntries = MutableLiveData<List<String>>()
    val categoryEntries : LiveData<List<String>> get() = _categoryEntries

    //spinner product entries
    private var _productEntries = MutableLiveData<List<String>>()
    val productEntries : LiveData<List<String>> get() = _productEntries

    // selected category on category spinner
    private val _selectedStockCategorySpinner = MutableLiveData<String?>()
    val selectedStockCategorySpinner: LiveData<String?> get() = _selectedStockCategorySpinner

    // selected product on product spinner
    private val _selectedStockProductSpinner = MutableLiveData<String>("Off")
    val selectedStockProductSpinner: LiveData<String> get() = _selectedStockProductSpinner

    //selected year on Stok year spinner
    private val _selectedStockYearSpinner = MutableLiveData<String?>()
    val selectedStockYearSpinner: LiveData<String?> get() = _selectedStockYearSpinner

    //selected month on Stok month spinner
    private val _selectedStockMonthSpinner = MutableLiveData<String>()
    val selectedStockMonthSpinner: LiveData<String> get() = _selectedStockMonthSpinner


    //selected year on Profit year spinner
    private val _selectedProfitYearSpinner = MutableLiveData<String?>("2026")
    val selectedProfitYearSpinner: LiveData<String?> get() = _selectedProfitYearSpinner

    //selected month on Stok month spinner
    private val _selectedProfitMonthSpinner = MutableLiveData<String>()
    val selectedProfitMonthSpinner: LiveData<String> get() = _selectedProfitMonthSpinner


    init {
        getKategoriEntries()

        newFilterModelList()
    }
    @RequiresApi(Build.VERSION_CODES.O)



    // populate category entries
    fun getKategoriEntries(){
        viewModelScope.launch {
            val newData = stockRepo.getCategoryNameListWithAll()
            _categoryEntries.value = newData
        }
    }
    // populate product entries
    fun getProductEntriesStok(){
        viewModelScope.launch {
            val newData = stockRepo.getProductNameListByCategoryName(_selectedStockCategorySpinner.value?:"")
            _productEntries.value = newData
        }
    }


    //set selected spinner tahun
    fun setSelectedYearValueStok(selectedItem:String){
        _selectedStockYearSpinner.value = selectedItem
    }
    //set selecter month spinner
    fun setSelectedMonthValueStok(selectedItem:String){
        Log.i("SpinnerProbs","setSelecterMonth: $selectedItem")
        _selectedStockMonthSpinner.value = selectedItem
    }
    //set selected category spinner
    fun setSelectedCategoryValueStok(selectedItem: String){
        _selectedStockCategorySpinner.value = selectedItem
    }
    //set selected product spinner
    fun setSelectedProductValueStok(selectedItem: String){
        _selectedStockProductSpinner.value = selectedItem
    }

    fun newFilterModelList(){
        viewModelScope.launch {
            val month = getMonthNumber(_selectedStockMonthSpinner.value)            // null if "ALL"
            val year = _selectedStockYearSpinner.value.takeIf { it != "ALL" } // null if "ALL"
            val category=_selectedStockCategorySpinner.value.takeIf { it != "ALL" } // null if "ALL"
            val product=_selectedStockProductSpinner.value.takeIf { it!="Off" }


            val list = if (product ==null){
                transRepo.getFilteredProductBarChart(month,year,product,category)
            }else{
                //transRepo.getFilteredSubBarChart(month,year,product,category)
                transRepo.getFilteredSubBarChart(month,year,product,category)
            }

            _productBCModel.value=list
        }
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////

    fun filterProfitModelList(){
        viewModelScope.launch {
            val year = _selectedProfitYearSpinner.value.takeIf { it != "ALL" } // null if "ALL"
            val list=   transRepo.getFilteredProfitBarChart(year)
            val dividedList = list.map { it.copy(value = it.value / 1000000.0) }
            _profitBCModel.value=dividedList
        }
    }



    //set selected spinner tahun
    fun setSelectedYearValueProfit(selectedItem:String){
        _selectedProfitYearSpinner.value = selectedItem
    }
    //set selecter month spinner
    fun setSelectedMonthValueProfit(selectedItem:String){
        _selectedProfitMonthSpinner.value = selectedItem
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
                val stockRepo=StockRepositories(application)
                val transRepo=TransactionsRepository(application)
                val bookRepo=BookkeepingRepository(application)
                val savedStateHandle = extras.createSavedStateHandle()
                return GraphicViewModel(
                    stockRepo,
                    transRepo,
                    application
                ) as T
            }
        }
    }

}