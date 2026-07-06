package com.example.app21try6.grafik

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
import com.example.app21try6.database.models.BarChartModel
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.formatRupiah
import com.example.app21try6.getMonthNumber
import kotlinx.coroutines.launch

class GraphicViewModel(
    private val stockRepo:StockRepositories,
    private val custRepo: DiscountRepository,
    private val transRepo:TransactionsRepository,
    application: Application
                      ): AndroidViewModel(application) {

    private val _productBCModel = MutableLiveData<List<BarChartModel>>()
    val productBCModel: LiveData<List<BarChartModel>> get() = _productBCModel

    private val _omzetBCModel = MutableLiveData<List<BarChartModel>>()
    val omzetBCModel: LiveData<List<BarChartModel>> get() = _omzetBCModel
    private val _profitBCModel = MutableLiveData<List<BarChartModel>>()
    val profitBCModel: LiveData<List<BarChartModel>> get() = _profitBCModel

    private val _productTrendBCModel = MutableLiveData<List<BarChartModel>>()
    val productTrendBCModel: LiveData<List<BarChartModel>> get() = _productTrendBCModel

    private val _customerTrendBCModel = MutableLiveData<List<BarChartModel>>()
    val customerTrendBCModel: LiveData<List<BarChartModel>> get() = _customerTrendBCModel

    private val _custCountProductList = MutableLiveData<List<BarChartModel>>()
    val custCountProductLit: LiveData<List<BarChartModel>> get() = _custCountProductList

    //spinner category entries
    private var _categoryEntries = MutableLiveData<List<String>>()
    val categoryEntries : LiveData<List<String>> get() = _categoryEntries

    //spinner product entries
    private var _productEntries = MutableLiveData<List<String>>()
    val productEntries : LiveData<List<String>> get() = _productEntries

    private var _spEntries = MutableLiveData<List<String>>()
    val spEntries : LiveData<List<String>> get() = _spEntries

    // selected category on category spinner
    private val _selectedStockCategorySpinner = MutableLiveData<String?>()
    val selectedStockCategorySpinner: LiveData<String?> get() = _selectedStockCategorySpinner

    // selected product on product spinner
    private val _selectedStockProductSpinner = MutableLiveData<String>("ALL")
    val selectedStockProductSpinner: LiveData<String> get() = _selectedStockProductSpinner

    //selected year on Stok year spinner
    private val _selectedStockYearSpinner = MutableLiveData<String?>()
    val selectedStockYearSpinner: LiveData<String?> get() = _selectedStockYearSpinner

    //selected month on Stok month spinner
    private val _selectedStockMonthSpinner = MutableLiveData<String>()
    val selectedStockMonthSpinner: LiveData<String> get() = _selectedStockMonthSpinner

    //selected year on Profit year spinner
    private val _selectedOmzetYearSpinner = MutableLiveData<String?>("2026")
    val selectedOmzetYearSpinner: LiveData<String?> get() = _selectedOmzetYearSpinner
    private val _selectedCustomerOmzet= MutableLiveData<String>("ALL")
    val selectedCustomerOmzet: LiveData<String> get() = _selectedCustomerOmzet
    private val _selectedProfitYearSpinner = MutableLiveData<String?>("2026")
    val selectedProfitYearSpinner: LiveData<String?> get() = _selectedProfitYearSpinner
    private val _selectedCustomerProfit= MutableLiveData<String>("ALL")
    val selectedCustomerProfit: LiveData<String> get() = _selectedCustomerProfit
    //selected month on Stok month spinner
    private val _selectedProfitMonthSpinner = MutableLiveData<String>()
    val selectedProfitMonthSpinner: LiveData<String> get() = _selectedProfitMonthSpinner

    private val _selectedCategorySpinnerPt = MutableLiveData<String?>()
    val selectedCategorySpinnerPt: LiveData<String?> get() = _selectedCategorySpinnerPt

    // selected product on product spinner
    private val _selectedProductSpinnerPt = MutableLiveData<String>("Off")
    val selectedProductSpinnerPt: LiveData<String> get() = _selectedProductSpinnerPt

    private val _selectedSPSpinnerPt = MutableLiveData<String?>()
    val selectedSPpinnerPt: LiveData<String?> get() = _selectedSPSpinnerPt

    //selected year on Stok year spinner
    private val _selectedYearSpinnerPt = MutableLiveData<String?>()
    val selectedYearSpinnerPt: LiveData<String?> get() = _selectedYearSpinnerPt

    //selected customer

    //customer list for spinner
    private val  _customerEntries = MutableLiveData<List<String>>()
    val customerEntries: LiveData<List<String>> get() = _customerEntries


    init {
        getKategoriEntries()
        newFilterModelList()
        getCustomerEntries()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    // populate category entries
    fun getKategoriEntries(){
        viewModelScope.launch {
            val newData = stockRepo.getCategoryNameListWithAll()
            _categoryEntries.value = newData
        }
    }
    fun getCustomerEntries(){
        viewModelScope.launch {
            val newData = custRepo.getAllCustomerNameList()
            _customerEntries.value = newData
        }
    }

    // populate product entries
    fun getProductEntriesStok(value:String?){
        viewModelScope.launch {
            val newData = stockRepo.getProductNameListByCategoryName(value?:"")
            _productEntries.value = newData
        }
    }

    fun getSPEntriesStok(value: String?){
        viewModelScope.launch {
            val newData = stockRepo.getSPNameListByProductName(value ?:"")
            _spEntries.value = newData
        }
    }

    //set selected spinner tahun
    fun setSelectedYearValueStok(selectedItem:String){
        _selectedStockYearSpinner.value = selectedItem
    }
    //set selecter month spinner
    fun setSelectedMonthValueStok(selectedItem:String){
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
            val product=_selectedStockProductSpinner.value.takeIf { it!="ALL" }

            val list = if (product ==null){
                transRepo.getFilteredProductBarChart(month,year,product,category)
            }else if(product=="Sub Product"){
                transRepo.getFilteredSubBarChart(month,year,category)
            }
            else{
                transRepo.getFilteredSubBarChart(month,year,product,category)
            }
            _productBCModel.value=list
        }
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////

    fun filterOmzetModelList(){
        viewModelScope.launch {

            val year = _selectedOmzetYearSpinner.value.takeIf { it != "ALL" } // null if "ALL"
            val customer=_selectedCustomerOmzet.value.takeIf { it  != "ALL"}
            val list=   transRepo.getFilteredOmzetBarChart(year,customer)
            val dividedList = list.map { it.copy(value = it.value / 1000000.0) }
            _omzetBCModel.value=dividedList
        }
    }
    fun filterProfitModelList(){
        viewModelScope.launch {

            val year = _selectedProfitYearSpinner.value.takeIf { it != "ALL" } // null if "ALL"
            val customer=_selectedCustomerProfit.value.takeIf { it  != "ALL"}
            val list=   transRepo.getFilteredProfitBarChart(year,null,null,null)
            val dividedList = list.map { it.copy(value = it.value / 1000000.0) }
            //sum of product with non null unit
            val sum = transRepo.gettransdetailwithnotnullunit(year)
           // Log.i("chartprobs","trans detail with not null unit year $year: ${formatRupiah(sum)}")
            _profitBCModel.value=dividedList
            val count =transRepo.get0ProductCapital()
            //Log.i("chartprobs","trans detail with 0 capita: $count")
        }
    }

    fun filterProductTrend(){
        viewModelScope.launch {
            val year = _selectedYearSpinnerPt.value.takeIf { it != "ALL" } // null if "ALL"
            val sp=_selectedSPSpinnerPt.value.takeIf { it!="ALL" }
            val product=_selectedProductSpinnerPt.value.takeIf { it != "ALL" }
            val category=_selectedCategorySpinnerPt.value.takeIf { it != "ALL" }
            val list=   if (year==null){
                transRepo.getYearlyProductTrend(category,product,sp)
            }else
                transRepo.getMonthlyProductTrend(year,category,product,sp)
            //val dividedList = list.map { it.copy(value = it.value / 1000000.0) }
            _productTrendBCModel.value=list
            customerProdutCountList()
        }
    }
    fun customerProdutCountList(){
        viewModelScope.launch {
            val year = _selectedYearSpinnerPt.value.takeIf { it != "ALL" } // null if "ALL"
            val sp=_selectedSPSpinnerPt.value.takeIf { it!="ALL" }
            val product=_selectedProductSpinnerPt.value.takeIf { it != "ALL" }
            val category=_selectedCategorySpinnerPt.value.takeIf { it != "ALL" }
           if (product!=null){
                val list= transRepo.getCustomerProductCount(year,category,product,sp)
               _custCountProductList.value=list
           }
        }
    }


    //set selected spinner tahun
    fun setSelectedYearValueOmzet(selectedItem:String){
        _selectedOmzetYearSpinner.value = selectedItem

    }

    //set selecter month spinner
    fun setSelectedCustomerValueOmzet(selectedItem:String){
        _selectedCustomerOmzet.value = selectedItem
    }
    fun setSelectedYearValueProfit(selectedItem:String){
        _selectedProfitYearSpinner.value = selectedItem

    }
    fun setSelectedCustomerValueProfit(selectedItem:String){
        _selectedCustomerProfit.value = selectedItem
    }
    fun setSelectedSP(selectedItem: String){
        _selectedSPSpinnerPt.value = selectedItem
    }
    fun setSelectedProductPt(selectedItem: String){
        _selectedProductSpinnerPt.value = selectedItem
    }
    fun setSelectedCategoryPt(selectedItem: String){
        _selectedCategorySpinnerPt.value = selectedItem
    }
    fun setSelectedYearPt(selectedItem: String){
        _selectedYearSpinnerPt.value = selectedItem
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
                val custRepo= DiscountRepository(application)

                return GraphicViewModel(
                    stockRepo,
                    custRepo,
                    transRepo,
                    application
                ) as T
            }
        }
    }

}