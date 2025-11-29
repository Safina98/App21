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
import com.example.app21try6.database.tables.Category
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SummaryDbDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.getMonthName
import com.example.app21try6.stock.brandstock.CategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class GraphicViewModel(
    private val stockRepo:StockRepositories,
    private val bookRepo: BookkeepingRepository,
    private val transRepo:TransactionsRepository,
    application: Application
                      ): AndroidViewModel(application) {

    val summariesLiveData = bookRepo.getAllSummary()
    //val productsLiveData: LiveData<List<Product>> = stockRepo.getAllProduct()
    //val categoriesLiveData: LiveData<List<CategoryModel>> = stockRepo.getCategoryModelLiveData()

    private val _combinedStockLiveData = MediatorLiveData<List<StockModel>?>()
    val combinedStockLiveData: LiveData<List<StockModel>?> get() = _combinedStockLiveData

    private val _summarycombinedLiveData = MediatorLiveData<List<StockModel>?>()

    val transDetailModel = transRepo.getStockModel()


    private val _unFilteredmodelList = MutableLiveData<List<StockModel>>()

    private val _filteredmodelList = MutableLiveData<List<StockModel>>()
    val filteredmodelList: LiveData<List<StockModel>> get() = _filteredmodelList

    //live data untuk map
    private val _mapModel = MutableLiveData<Map<String,Double>>()
    val mapModel: LiveData<Map<String, Double>> get() = _mapModel
    //live data untuk top 8 map
    val _topEightMap = MutableLiveData<Map<String,Double>>()
    val topEightMap: LiveData<Map<String, Double>> get() = _topEightMap

    //live data untuk recyclerview
    val _rvData=MutableLiveData<List<TransactionSummary>>()
    val rvData:LiveData<List<TransactionSummary>> get() = _rvData

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
    val yearProfitEntries = bookRepo.getAllYear()
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

    val monthIncomeMap: LiveData<Map<String, Double>> = transDetailModel.map { list ->

        list.groupBy { it.month }
            .mapValues { entry ->
                entry.value.sumOf { it.total_income ?: 0.0 }
            }
    }
    init {

        getKategoriEntries()
        getCombinedStockLiveData()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getCombinedStockLiveData(){
        viewModelScope.launch {
            val stockList = transRepo.getStockModelList()
            val list=stockList.map { stock ->
                stock.copy(month = getMonthName(stock.month.toInt())) // Replace numeric month with its name
            }
            _unFilteredmodelList.value=list
            _combinedStockLiveData.value = list
            _summarycombinedLiveData.value =list
        }
    }


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
    //populate _fileredModelList
    fun populateListModelStok(){
        _filteredmodelList.value = combinedStockLiveData.value
    }
    fun getCurrentYearAndMothData(){
        val year = Calendar.getInstance().get(Calendar.YEAR).toString()
        val currentMonth = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale("id", "ID"))
        val filteredList = combinedStockLiveData.value?.filter { model ->
            model.year.toString() == year &&
                    model.month.toString()==currentMonth
        }
        if (filteredList!=null){
            _filteredmodelList.value=filteredList!!
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

        if(selectedItem =="Off"){
            val updatedStockList = _summarycombinedLiveData.value?.map { stock ->
                stock.copy(item_name = stock.product_name ?: stock.item_name) // Use `product_name` if available, otherwise keep `item_name`
            }
            _combinedStockLiveData.value = updatedStockList
        }else {
            val updatedStockList = _summarycombinedLiveData.value?.map { stock ->
                stock.copy(item_name = stock.sub_name ?: stock.item_name) // Use `product_name` if available, otherwise keep `item_name`
            }
            _combinedStockLiveData.value = updatedStockList
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
        filteredList?.forEach {
            Log.i("GRAPHICPROBS","$it")
        }
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

            } catch (e: NumberFormatException) {
            }
        }
        _mapModel.value = itemCountMap
        Log.i("LINE","itemcountmap: $itemCountMap")

        //mau dihapus
        return _mapModel.value!!
    }
    //fungsi untuk load recyclerview
    fun getRvData(map: Map<String, Double>) {
        val sortedEntries = map.entries.sortedByDescending { it.value }
        val tsList = mutableListOf<TransactionSummary>()
        var id = -1
        for ((key, value) in sortedEntries) {
            // Create a new TransactionSummary object for each entry
            val item = TransactionSummary()
            item.cust_name = key
            item.total_trans = value
            item.sum_id = id
            tsList.add(item)

            id -= 1
        }
        _rvData.value = tsList
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
                //Log.i("ChartProb","${model.year}")
                val doubleValue = model.total_income?:0.0
                itemCountMap[itemName] = itemCountMap.getOrDefault(itemName, 0.0) + doubleValue

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
        Log.i("ChartProb","selected spinner${selectedProfitYearSpinner.value}")
        filteredList?.forEach {
            //Log.i("ChartProb","${it.year}")
        }
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
                val stockRepo=StockRepositories(application)
                val transRepo=TransactionsRepository(application)
                val bookRepo=BookkeepingRepository(application)
                val savedStateHandle = extras.createSavedStateHandle()
                return GraphicViewModel(
                    stockRepo,
                    bookRepo,
                    transRepo,
                    application
                ) as T
            }
        }
    }

}