package com.example.app21try6.grafik

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.database.Category
import com.example.app21try6.database.CategoryDao
import com.example.app21try6.database.Product
import com.example.app21try6.database.ProductDao
import com.example.app21try6.database.Summary
import com.example.app21try6.database.SummaryDbDao
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao
import com.example.app21try6.database.VendibleDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GraphicViewModel(application: Application,
                       private val productSource1:ProductDao,
                       private val summarySource: SummaryDbDao,
                       private val categorySource3:CategoryDao,
                       private val TransDetailSource4:TransDetailDao,
                       private val transSumSource5:TransSumDao,): AndroidViewModel(application) {

    val summariesLiveData = summarySource.getAllSummary()
    val productsLiveData: LiveData<List<Product>> = productSource1.getAllProduct()
    val categoriesLiveData: LiveData<List<Category>> = categorySource3.getAll()

    private val _combinedStockLiveData = MediatorLiveData<List<StockModel>?>()
    val combinedStockLiveData: LiveData<List<StockModel>?> get() = _combinedStockLiveData

    private val _summarycombinedLiveData = MediatorLiveData<List<StockModel>?>()

    val transDetailModel = TransDetailSource4.getTransactionDetails()

    private val _filteredmodelList = MutableLiveData<List<StockModel>>()
    val filteredmodelList: LiveData<List<StockModel>> get() = _filteredmodelList

    //live data untuk map
    private val _mapModel = MutableLiveData<Map<String,Double>>()
    val mapModel: LiveData<Map<String, Double>> get() = _mapModel
    //live data untuk top 8 map
    val _topEightMap = MutableLiveData<Map<String,Double>>()
    val topEightMap: LiveData<Map<String, Double>> get() = _topEightMap

    //spinner category entries
    private var _categoryEntries = MutableLiveData<List<String>>()
    val categoryEntries : LiveData<List<String>> get() = _categoryEntries
    //spinner product entries
    private var _productEntries = MutableLiveData<List<String>>()
    val productEntries : LiveData<List<String>> get() = _productEntries
    // selected category on category spinner
    private val _selectedStockCategorySpinner = MutableLiveData<String>()
    val selectedStockCategorySpinner: LiveData<String> get() = _selectedStockCategorySpinner
    // selected product on product spinner
    private val _selectedStockProductSpinner = MutableLiveData<String>("Off")
    val selectedStockProductSpinner: LiveData<String> get() = _selectedStockProductSpinner
    //selected year on Stok year spinner
    private val _selectedStockYearSpinner = MutableLiveData<String>()
    val selectedStockYearSpinner: LiveData<String> get() = _selectedStockYearSpinner
    //selected month on Stok month spinner
    private val _selectedStockMonthSpinner = MutableLiveData<String>()
    val selectedStockMonthSpinner: LiveData<String> get() = _selectedStockMonthSpinner

    //Year spinner entries for profit
    val yearProfitEntries = summarySource.getAllYear()
    //SummaryModel
    //val summaryModel = summarySource.getSummaryWithProduct()
    //selected year on Profit year spinner
    private val _selectedProfitYearSpinner = MutableLiveData<String>()
    val selectedProfitYearSpinner: LiveData<String> get() = _selectedProfitYearSpinner
    //selected month on Stok month spinner
    private val _selectedProfitMonthSpinner = MutableLiveData<String>()
    val selectedProfitMonthSpinner: LiveData<String> get() = _selectedProfitMonthSpinner

    private val _filteredmodelListProfit = MutableLiveData<List<StockModel>>()
    val filteredmodelListProfit: LiveData<List<StockModel>> get() = _filteredmodelListProfit
    init {
        getKategoriEntries()
        getCombinedStockLiveData()
    }
    fun getCombinedStockLiveData(){
        viewModelScope.launch {
            var list = withContext(Dispatchers.IO){
                summarySource.getAllStockModels()
            }
            _combinedStockLiveData.value = list
            _summarycombinedLiveData.value =list
        }
    }

    // populate category entries
    fun getKategoriEntries(){
        viewModelScope.launch {
            var newData = withContext(Dispatchers.IO) {
                val list = categorySource3.getAllCategoryName()
                val modifiedList = listOf("ALL") + list // Create a new list with the added value
                modifiedList // Return the modified list
            }
            _categoryEntries.value = newData
        }
    }
    // populate product entries
    fun getProductEntriesStok(){
        viewModelScope.launch {
            var newData = withContext(Dispatchers.IO) {
                val list = productSource1.getProductNameByCategoryName(selectedStockCategorySpinner.value?:"")
                val modifiedList = listOf("Off","ALL") + list // Create a new list with the added value
                modifiedList // Return the modified list
            }
            _productEntries.value = newData
        }
    }
    //populate _fileredModelList
    fun populateListModelStok(){
        _filteredmodelList.value = combinedStockLiveData.value
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
        if(selectedItem =="Off"){
            _combinedStockLiveData.value = _summarycombinedLiveData.value
        }else {
            _combinedStockLiveData.value = transDetailModel.value
        }
        _selectedStockProductSpinner.value = selectedItem
    }

    fun filterModelListStok() {
        if (_filteredmodelList.value != null) {
            populateListModelStok()
            if (selectedStockYearSpinner.value != "ALL") {
                filterModelListByYearStok()
            }
            if (selectedStockMonthSpinner.value != "ALL") {
                filterModelListByMonthStok()
            }
            if (selectedStockCategorySpinner.value != "ALL") {
                filterModelListByCategoryStok()

            }
            if(selectedStockProductSpinner.value !="ALL" && selectedStockProductSpinner.value!="Off"){
                filteredmodelListByProductStok()
            }
        }
    }
    fun filteredmodelListByProductStok(){
        val filteredList = _filteredmodelList.value?.filter { model -> model.product_name == selectedStockProductSpinner.value }
        _filteredmodelList.value = filteredList!!
    }
    fun filterModelListByCategoryStok() {
        val filteredList = _filteredmodelList.value?.filter { model -> model.category_name == selectedStockCategorySpinner.value }
        _filteredmodelList.value = filteredList!!
    }

    fun filterModelListByYearStok() {
        val filteredList = _filteredmodelList.value?.filter { model -> model.year.toString() == selectedStockYearSpinner.value }
        // _filteredmodelList.postValue(filteredList!!)
        _filteredmodelList.value = filteredList!!
    }
    fun filterModelListByMonthStok() {
        val filteredList = _filteredmodelList.value?.filter { model -> model.month == selectedStockMonthSpinner.value }
        _filteredmodelList.value = filteredList!!
    }

    //fungsi untuk map
    fun calculateTotalItemCountStok(stockModels: List<StockModel>, tahun:String, bulan:String): Map<String, Double> {
        val itemCountMap = mutableMapOf<String, Double>()
        // Log.i("LINE","models: "+models.toString())
        for (model in _filteredmodelList.value!!) {
            val itemName = model.item_name
            try {
                val doubleValue = model.itemCount
                itemCountMap[itemName] = itemCountMap.getOrDefault(itemName, 0.0) + doubleValue
                //  Log.i("LINE","itemcountmap: "+doubleValue.toString())
            } catch (e: NumberFormatException) {
            }
        }
        _mapModel.value = itemCountMap

        //mau dihapus
        return _mapModel.value!!
    }
    //fungsi untuk top 8 map
    fun getTopEightItemsStok(map: Map<String, Double>) {
        // Sort the map by values in descending order
        val sortedEntries = map.entries.sortedByDescending { it.value }
        // Take the top eight entries
        val topEightEntries = sortedEntries.take(8)
        // Convert the top eight entries to a map
        val topEightMap = mutableMapOf<String, Double>()
        for ((key, value) in topEightEntries) {
            topEightMap[key] = value
        }
        _topEightMap.value = topEightMap
        // Log the top eight items

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////

    fun populateListModelProfit(){
        _filteredmodelList.value = combinedStockLiveData.value
    }
    fun mapAndSumByMonth(summaries: List<StockModel>): Map<String, Double> {
        return summaries.groupBy { it.month!! }
            .mapValues { entry -> entry.value.sumOf { it.total_income!!} }
    }
    fun calculateTotalItemCountProfit(stockModels: List<StockModel>): Map<String, Double> {
        val itemCountMap = mutableMapOf<String, Double>()
        // Log.i("LINE","models: "+models.toString())
        for (model in stockModels) {
            val itemName = model.month
            try {
                val doubleValue = model.total_income!!.toDouble()
                itemCountMap[itemName] = itemCountMap.getOrDefault(itemName, 0.0) + doubleValue
                //  Log.i("LINE","itemcountmap: "+doubleValue.toString())
            } catch (e: NumberFormatException) {
            }
        }
        //mau dihapus
        return itemCountMap
    }
    //set selected spinner tahun
    fun setSelectedYearValueProfit(selectedItem:String){
        _selectedProfitYearSpinner.value = selectedItem
    }
    //set selecter month spinner
    fun setSelectedMonthValueProfit(selectedItem:String){
        _selectedProfitMonthSpinner.value = selectedItem
    }
    fun filterModelListProfit(){
        _filteredmodelListProfit.value = combinedStockLiveData.value
        filterModelListByYearProfit()
    }
    fun filterModelListByYearProfit() {
        val filteredList = _filteredmodelListProfit.value?.filter { model -> model.year.toString() == selectedProfitYearSpinner.value }
        // _filteredmodelList.postValue(filteredList!!)
        _filteredmodelListProfit.value = filteredList!!
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
                val dataSource1 = VendibleDatabase.getInstance(application).productDao
                val dataSource2 = VendibleDatabase.getInstance(application).summaryDbDao
                val dataSource3 = VendibleDatabase.getInstance(application).categoryDao
                val dataSource4 = VendibleDatabase.getInstance(application).transDetailDao
                val dataSource5 = VendibleDatabase.getInstance(application).transSumDao
                val savedStateHandle = extras.createSavedStateHandle()
                return GraphicViewModel(
                    application,
                    dataSource1,
                    dataSource2,
                    dataSource3,
                    dataSource4,
                    dataSource5
                ) as T
            }
        }
    }

}