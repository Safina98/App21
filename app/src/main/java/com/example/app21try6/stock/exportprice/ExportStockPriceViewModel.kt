package com.example.app21try6.stock.exportprice

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.tables.Product
import com.example.app21try6.formatRupiah
import com.example.app21try6.toTitleCase
import kotlinx.coroutines.launch
import java.util.Locale

class ExportStockPriceViewModel(
    private val stockRepo: StockRepositories,
    private val discountRepository:DiscountRepository,
    application: Application
) : AndroidViewModel(application){
    private var _allProduct = MutableLiveData<List<Product>>()

    private val _newList = MutableLiveData<List<Product>>()
    val newList: LiveData<List<Product>> get() = _newList

    private var _isPickingProduct= MutableLiveData<Boolean>(true)
    val isPickingProduct: LiveData<Boolean>get() = _isPickingProduct

    var exportText= MutableLiveData<String>("")



    fun draftText() {
        Log.i(espTag,"draftTextCalled")
        val text = _unFilteredProduct.value
            ?.filter { it.checkBoxBoolean }
            ?.joinToString("\n") { "${it.product_name.toTitleCase()} ${formatRupiah(it.product_price.toDouble())}" }
            ?: ""
        exportText.value = text
        Log.i(espTag,"${text}")
        exportText.value = text
    }
    fun onProductClicked(item: Product) {
        Log.i(espTag,"onProductClickCalled clicked")
        _unFilteredProduct.value = _unFilteredProduct.value?.map {
            if (it.productCloudId == item.productCloudId) it.copy(checkBoxBoolean = true) else it
        }
        _newList.value = _newList.value?.map {
            if (it.productCloudId == item.productCloudId) it.copy(checkBoxBoolean = true) else it
        }
    }
    fun pickProduct(){
        _isPickingProduct.value=true
    }
    fun donePickProduct(){
        _isPickingProduct.value=false
    }

    private val _unFilteredProduct = MutableLiveData<List<Product>>()
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
        _newList.value = filteredList
    }

    fun updateRv(){
        viewModelScope.launch {
            val product = stockRepo.getProductListByCategoryId(null)
            _allProduct.value = product
            _unFilteredProduct.value = product
            _newList.value = product.map { it.copy() }
        }
    }
}