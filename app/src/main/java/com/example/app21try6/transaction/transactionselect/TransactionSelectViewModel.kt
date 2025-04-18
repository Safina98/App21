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
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.calculatePriceByQty
import com.example.app21try6.database.*
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.TransactionDetail
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
    private val _showDialog = MutableLiveData<TransSelectModel>()
    val showDialog:LiveData<TransSelectModel> get() = _showDialog

    /////////////////////////////////////////Product////////////////////////////////////////////
    private var _allProduct = MutableLiveData<List<Product>>()
    val allProduct :LiveData<List<Product>> get() = _allProduct
    private var _sumId = MutableLiveData<Int>(-1)

    private var expenseId=MutableLiveData (-1)

    private var _productId = MutableLiveData<Int>(-1)
    val productId:LiveData<Int> get()=_productId

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
    }
    fun updateTransDetailList(updatedList: List<TransSelectModel>) {
        transSelectModel.value = updatedList
    }

    fun updateTransDetaill(trans: TransSelectModel) {
        viewModelScope.launch {
            Log.i("DuplicateProbs","model $trans")
            val updatedList = transSelectModel.value?.toMutableList() ?: mutableListOf()
            val index = updatedList.indexOfFirst { it.sub_product_id == trans.sub_product_id }
            Log.i("DuplicateProbs","index $index")
            if (index != -1) {
                updatedList[index] = trans
                Log.i("DuplicateProbs","updatedlist $updatedList")
                updateTransDetailList(updatedList)
            }
        }
    }

    fun getTransModel(productId:Int){
        viewModelScope.launch {
           val list = transRepo.getTransSelectModel(productId,_sumId.value?:0)
            transSelectModel.value = list
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
    //update on btn + or - click
    fun updateTransDetail(s:TransSelectModel){
        viewModelScope.launch {
            Log.i("QTYProbs","${s.item_name}: ${s.item_price}")
            s.item_price= calculatePriceByQty(s.qty,s.item_default_price)
            Log.i("QTYProbs","${s.item_name}: ${s.item_price}")
            val t = converter(s)
            var id =s.trans_detail_id
            if (id==0L) id = transRepo.insertTransDetail(t) else transRepo.updateTransDetail(t)
            s.trans_detail_id  = id ?: -1L
            updateTransDetaill(s)
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
            s.is_selected = boolean
            if (boolean == true) {
                var t = converter(s)
                try {
                    var id = transRepo.insertTransDetail(t)
                    s.trans_detail_id  = id ?: -1L
                     updateTransDetaill(s)
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
            getTransModel(_productId.value!!)
            updateTransDetaill(s)
        }
    }

    fun delete(s:TransSelectModel){
        viewModelScope.launch {
            transRepo.deleteTransDetail(s.trans_detail_id)
        }
    }

    fun onShowDialog(transSelectModel: TransSelectModel){
        _showDialog.value = transSelectModel
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
    fun filterProduct(query: String?) {
        val list = mutableListOf<Product>()
        if(!query.isNullOrEmpty()) {
            list.addAll(_unFilteredProduct.value!!.filter {
                it.product_name.lowercase(Locale.getDefault()).contains(query.toString().lowercase(Locale.getDefault()))})
        } else {
            list.addAll(_unFilteredProduct.value!!)
        }
        _allProduct.value =list
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
        // Toast.makeText(getApplication(),value+" kategori",Toast.LENGTH_SHORT).show()
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
      //  unCheckedAllSubs()
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