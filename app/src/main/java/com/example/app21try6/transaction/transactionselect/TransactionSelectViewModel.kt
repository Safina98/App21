package com.example.app21try6.transaction.transactionselect

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.calculatePriceByQty
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.utils.AbsentLiveData
import com.example.app21try6.utils.Levenshtein
import kotlinx.coroutines.*
import java.util.Date
import java.util.Locale


class TransactionSelectViewModel(
    private val stockRepo: StockRepositories,
    private val transRepo:TransactionsRepository,
    val sum_id:Int,
    val sumAndProductId:Array<String>,
    application: Application): AndroidViewModel(application){
   // val trans_select_model =database6.getSubProduct(date[1].toInt(),sum_id)
    var transSelectModel = MutableLiveData<List<TransSelectModel>>()
    val trans_select_model :LiveData<List<TransSelectModel>> get() = transSelectModel

    private var _productId = MutableLiveData<Int?>(-1)
    val productId: LiveData<Int?> get() = _productId

    val trans_select_modelNew :LiveData<List<TransSelectModel>> = _productId.switchMap { productId ->
        if (productId == null || productId == -1 || _sumId.value==null || _sumId.value==-1) {
            AbsentLiveData.create()
        } else {
            Log.i("LiveDataProbs","TransProductFragment SumId ${sum_id}")
            transRepo.getTransSelectModelLive(productId,_sumId.value!!)
        }
    }

    private val _showDialog = MutableLiveData<TransSelectModel>()
    val showDialog:LiveData<TransSelectModel> get() = _showDialog

    private val lev=Levenshtein()

    /////////////////////////////////////////Product////////////////////////////////////////////
    private var _allProduct = MutableLiveData<List<Product>>()
    val allProduct :LiveData<List<Product>> get() = _allProduct
    private var _sumId = MutableLiveData<Int>(-1)

    private var expenseId=MutableLiveData (-1)



    private var _categoryEntries = MutableLiveData<List<String>>()
    val categoryEntries : LiveData<List<String>> get() = _categoryEntries

    private val _selectedKategoriSpinner = MutableLiveData<String>()
    val selectedKategoriSpinner: LiveData<String> get() = _selectedKategoriSpinner

    private val _navigateToTransSelect = MutableLiveData<Array<String>>()
    val navigateToTransSelect: LiveData<Array<String>> get() = _navigateToTransSelect

    private val _unFilteredProduct = MutableLiveData<List<Product>>()
    private val _unFilteredSub = MutableLiveData<List<TransSelectModel>>()
    private var pos:Int = 0

    private var _selectedItemId: Int? = null
    private var _selectedItemPosition: Int = 0
    val selectedItemPosition: Int get() = _selectedItemPosition

    init {
        getKategoriEntries()
        unCheckedAllSubs()
        _productId.value=-1
        Log.d("LiveDataProbs", "Init transRepo is ${transRepo?.toString() ?: "NULL"}")
        Log.d("LiveDataProbs", "Init productId is ${_productId.value}")
    }
    fun updateTransDetailList(updatedList: List<TransSelectModel>) {
        Log.i("DialogUtilProblems","viewModel updateTransDetailListCalled")
        transSelectModel.value = updatedList
    }

    fun updateItemInTransDetailList(trans: TransSelectModel) {
        viewModelScope.launch {
            Log.i("DialogUtilProblems","viewModel updateItemInTransDetailCalled")
            val updatedList = transSelectModel.value?.toMutableList() ?: mutableListOf()
            updatedList.forEach{
                Log.i("DialogUtilProblems","viewModel before update currentList $it ")
            }
            val index = updatedList.indexOfFirst { it.sub_product_id == trans.sub_product_id }
            val matchedItems=updatedList.filter { it.sub_product_id == trans.sub_product_id }
            Log.i("DialogUtilProblems","viewModel updateItemInTransDetail index: $index")
            if (matchedItems.size>1){
                //val newIndex = matchedItems.indexOfFirst { it.trans_detail_id == trans.trans_detail_id }
                //val matchedItem = updatedList.find { it.trans_detail_id == trans.trans_detail_id }
                //updatedList[newIndex] = matchedItem!!
                updateTransDetailList(updatedList)
            }else{
                if (index != -1) {
                    //updatedList[index] = trans
                    Log.i("DialogUtilProblems","viewModel updateItemInTransDetail updatedItemInList: ${updatedList[index]}")
                    updateTransDetailList(updatedList)
                }
            }
            updatedList.forEach{
                Log.i("DialogUtilProblems","viewModel after update currentList $it ")
            }
        }
    }

    fun resetTransModel(){
        transSelectModel.value =  emptyList()
    }
    /////////////////////////////////////old functions///////////////////////////////////////////
    fun setProductId(id:Int){
        _productId.value = id
    }
    fun setProductSumId(id:Int?){
        if (id != null) {
            if (id >= 0) { _sumId.value = id?:0 }else { expenseId.value=id*-1 }
        }
    }
    fun saveSelectedItemId(itemId: Int) {
        _selectedItemId = itemId
    }

    fun saveSelectedItemPosition(itemId: Int) {
        _selectedItemPosition = _unFilteredProduct.value?.sortedBy { it.product_name }?.indexOfFirst { it.product_id == itemId }
            ?: 0
        Log.i("SelectedRvPos","selected position: ${_selectedItemPosition}")
    }

    fun updateTransDetail(s:TransSelectModel){
        viewModelScope.launch {
            Log.i("DialogUtilProblems","View Model. Update TransDetail called")
            s.item_price= calculatePriceByQty(s.qty,s.item_default_price)
            val t = converter(s)
            var id =s.trans_detail_id
            if (id==0L) id = transRepo.insertTransDetail(t) else transRepo.updateTransDetail(t)
            s.trans_detail_id  = id ?: -1L
        //processTransDetailUpdate(s)
            //updateItemInTransDetailList(s)
        }
    }

    fun converter(s:TransSelectModel): TransactionDetail {
        val t = TransactionDetail()
        t.sum_id = _sumId.value ?: -1
        t.trans_detail_id = s.trans_detail_id
        t.qty = s.qty
        t.total_price = s.qty*s.item_price
        t.trans_item_name = s.item_name
        t.trans_price = s.item_price
        t.trans_detail_date = Date()
        t.item_position = pos
        t.sub_id=s.sub_product_id
        Log.i("drag","insert pos $pos")
        pos+=1
        return t
    }

    fun onCheckBoxClicked(s:TransSelectModel,boolean: Boolean){
        viewModelScope.launch {
            s.selected = boolean
            if (boolean == true) {
                var t = converter(s)
                try {
                    var id = transRepo.insertTransDetail(t)
                    s.trans_detail_id  = id ?: -1L
                     updateItemInTransDetailList(s)
                }catch (e:Exception)
                {
                    Log.i("DataProb",e.toString())
                }
            }
        }
    }

    fun insertDuplicateSubProduct(s:TransSelectModel){
        viewModelScope.launch {
            Log.i("DuplicateProbs","s id ${s.trans_detail_id}")
            val t = converter(s)
            val id =transRepo.insertTransDetail(t)
            Log.i("DuplicateProbs","inserted id $id")
            s.trans_detail_id  = id ?: -1L
            //getTransModel(_productId.value!!)
            //updateItemInTransDetailList(s)
        }
    }

    fun delete(s:TransSelectModel){
        viewModelScope.launch {
            transRepo.deleteTransDetail(s.trans_detail_id)
        }
    }

    @SuppressLint("NullSafeMutableLiveData")
    fun onCloseDialog(){
        _showDialog.value = null
    }
    fun unCheckedAllSubs(){
        viewModelScope.launch {
            stockRepo.uncheckedAllSubs()
        }
    }

    ///////////////////////////////////////////////PRODUCT//////////////////////////////////////
    fun filterProductOld(query: String?) {
        val list = mutableListOf<Product>()
        if(!query.isNullOrEmpty()) {
            list.addAll(_unFilteredProduct.value!!.filter {
                it.product_name.lowercase(Locale.getDefault()).contains(query.toString().lowercase(Locale.getDefault()))})
        } else {
            list.addAll(_unFilteredProduct.value!!)
        }
        _allProduct.value =list
    }
    fun filterProduct(query: String?) {
            val originalList = _unFilteredProduct.value ?: return
            val filteredList = if (!query.isNullOrEmpty()) {
                val input = query.lowercase(Locale.getDefault()).trim().replace(" ","")
                originalList.filter { merk ->
                    val name = merk.product_name.lowercase(Locale.getDefault()).trim().replace(" ","")
                    val words = name.split(" ")
                    words.any { word ->
                                word.contains(input)
                    }
                }
            } else {
                originalList
            }
            _allProduct.value = filteredList
    }

    fun updateRv(value: String){
        viewModelScope.launch {
            val categoryId = if (value != "ALL") { stockRepo.getCategoryIdByName(value) } else { null }
            val product = stockRepo.getProductListByCategoryId(categoryId)
            _allProduct.value = product
            _unFilteredProduct.value = product
        }
    }
    fun setSelectedKategoriValue(selectedCategory:String){
        _selectedKategoriSpinner.value = selectedCategory
    }
    fun getKategoriEntries(){
        viewModelScope.launch {
            var newData = stockRepo.getCategoryNameListWithAll()
            _categoryEntries.value = newData
        }
    }
    fun onNavigatetoTransSelect(id:String){
        _navigateToTransSelect.value= arrayOf(sum_id.toString(),id)
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedtoTransSelect(){
        _navigateToTransSelect.value =null
    }

    @SuppressLint("NullSafeMutableLiveData")
    override fun onCleared() {
        super.onCleared()
    }
    fun resetSelectedSpinnerValue(){
        _selectedKategoriSpinner.value="ALL"
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
                val savedStateHandle = extras.createSavedStateHandle()
               val stockRepo=StockRepositories(application)
                val transRepo=TransactionsRepository(application)

                return TransactionSelectViewModel(
                    stockRepo,transRepo,0, arrayOf("0","0","0"),application
                ) as T
            }
        }
    }

    }