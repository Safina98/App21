package com.example.app21try6.transaction.transactionproduct

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionProductViewModel(val sum_id:Int,
                                  val database1: CategoryDao,
                                  val database2: ProductDao,
                                  val database3: BrandDao,
                                  val database4: SubProductDao,
                                  val date:Array<String>,
                                  val database5: TransSumDao,
                                  val database6: TransDetailDao,
                                  application: Application
): AndroidViewModel(application) {
    private var _allProduct = MutableLiveData<List<Product>>()
    val allProduct :LiveData<List<Product>> get() = _allProduct
    private var _categoryEntries = MutableLiveData<List<String>>()
    val categoryEntries : LiveData<List<String>> get() = _categoryEntries
    private val _navigateToTransSelect = MutableLiveData<Array<String>>()
    val navigateToTransSelect: LiveData<Array<String>> get() = _navigateToTransSelect
    private val _selectedKategoriSpinner = MutableLiveData<String>()
    val selectedKategoriSpinner: LiveData<String> get() = _selectedKategoriSpinner

    init {
        getKategoriEntries()
    }

    fun updateRv(value: String){
        viewModelScope.launch {
            val categoryId = if (value != "ALL") {
                getSelectedCategoryId(value)
            } else {
                null
            }
            val product = getProductByCategory(categoryId)
            _allProduct.value = product
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


}