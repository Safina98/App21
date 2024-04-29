package com.example.app21try6.transaction.transactionselect

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.app21try6.database.*
import kotlinx.coroutines.*
import java.util.Locale

class TransactionSelectViewModel(
    val sum_id:Int,
    val database1: CategoryDao,
    val database2: ProductDao,
    val database4: SubProductDao,
    val date:Array<String>,
    val database6:TransDetailDao,
    application: Application): AndroidViewModel(application){


    val trans_select_model =database6.getSubProduct(date[1].toInt(),sum_id)
    private val _showDialog = MutableLiveData<TransSelectModel>()
    val showDialog:LiveData<TransSelectModel> get() = _showDialog

    /////////////////////////////////////////Product////////////////////////////////////////////
    private var _allProduct = MutableLiveData<List<Product>>()
    val allProduct :LiveData<List<Product>> get() = _allProduct
    private var _categoryEntries = MutableLiveData<List<String>>()
    val categoryEntries : LiveData<List<String>> get() = _categoryEntries
    private val _navigateToTransSelect = MutableLiveData<Array<String>>()
    val navigateToTransSelect: LiveData<Array<String>> get() = _navigateToTransSelect
    private val _selectedKategoriSpinner = MutableLiveData<String>()
    val selectedKategoriSpinner: LiveData<String> get() = _selectedKategoriSpinner

    private val _unFilteredProduct = MutableLiveData<List<Product>>()
    private val _unFilteredSub = MutableLiveData<List<TransSelectModel>>()

    init {
        getKategoriEntries()
       // _unFilteredProduct.value =
    }

    fun updateTransDetail(s:TransSelectModel){
        viewModelScope.launch {
            var t = converter(s)
            t.trans_detail_id = s.trans_detail_id
            _updateTransDetail(t)
        }
    }
    fun converter(s:TransSelectModel): TransactionDetail {
        var t = TransactionDetail()
        t.sum_id = sum_id
        t.qty = s.qty
        t.total_price = s.qty*s.item_price
        t.trans_item_name = s.item_name
        t.trans_price = s.item_price
        return t
    }

    fun onCheckBoxClicked(s:TransSelectModel,boolean: Boolean){
        viewModelScope.launch {
            checkedSub(s.item_name,if (boolean) 1 else 0)
            if (boolean == true) insertTransDetail(s) else delete(s)
        }

    }
    private suspend fun checkedSub(name:String,bool:Int){
        withContext(Dispatchers.IO){
            database4.update_checkbox(name,bool)
        }
    }

    fun delete(s:TransSelectModel){
        viewModelScope.launch {
            _delete(s)
        }
    }
    private suspend fun _delete(s:TransSelectModel){
        withContext(Dispatchers.IO){
            database6.deleteAnItemTransDetail(s.trans_detail_id)
        }
    }

    fun insertTransDetail(s:TransSelectModel){
        viewModelScope.launch {
            var t = converter(s)
            _insertTransDetail(t)
        }
    }

    private suspend fun _insertTransDetail(transactionDetail: TransactionDetail){
       withContext(Dispatchers.IO){ database6.insert(transactionDetail)}
    }

    private suspend fun _updateTransDetail(transactionDetail: TransactionDetail){
        withContext(Dispatchers.IO){database6.update(transactionDetail)}
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
            uncheckedAllSubs_()
        }
    }
    private suspend fun uncheckedAllSubs_(){
        withContext(Dispatchers.IO){
            database4.unchecked_allCheckbox()
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
            val categoryId = if (value != "ALL") { getSelectedCategoryId(value) } else { null }
            val product = getProductByCategory(categoryId)
            _allProduct.value = product
            _unFilteredProduct.value = product
        }
    }
    private suspend fun getProductByCategory(category_id:Int?):List<Product>{
        return withContext(Dispatchers.IO){
            database2.getProductByCategory(category_id)
        }
    }

    private suspend fun getSelectedCategoryId(value: String):Int?{
        return withContext(Dispatchers.IO){
            database1.getCategoryId(value)
        }
    }
    fun setSelectedKategoriValue(selectedCategory:String){
        _selectedKategoriSpinner.value = selectedCategory
    }
    fun getKategoriEntries(){
        // Toast.makeText(getApplication(),value+" kategori",Toast.LENGTH_SHORT).show()
        viewModelScope.launch {
            var newData = withContext(Dispatchers.IO) {
                val list = database1.getAllCategoryName()
                val modifiedList = listOf("ALL") + list // Create a new list with the added value
                modifiedList // Return the modified list
            }
            _categoryEntries.value = newData
        }
    }
    fun onNavigatetoTransSelect(id:String){
        _navigateToTransSelect.value= arrayOf(date[0],id)
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedtoTransSelect(){
        _navigateToTransSelect.value =null
    }

    override fun onCleared() {
        super.onCleared()
        unCheckedAllSubs()
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
                val dataSource1 = VendibleDatabase.getInstance(application).categoryDao
                val dataSource2 = VendibleDatabase.getInstance(application).productDao
                val dataSource4 = VendibleDatabase.getInstance(application).subProductDao
                val dataSource6 = VendibleDatabase.getInstance(application).transDetailDao

                return TransactionSelectViewModel(
                    0,dataSource1, dataSource2,dataSource4, arrayOf("0","0","0"),dataSource6,application
                ) as T
            }
        }
    }

    }