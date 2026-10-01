package com.example.app21try6.stock.exportprice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.tables.Product
import kotlinx.coroutines.launch
import java.util.Locale

class ExportStockPriceViewModel(
    private val stockRepo: StockRepositories,
    private val discountRepository:DiscountRepository,
    application: Application
) : AndroidViewModel(application){
    private var _allProduct = MutableLiveData<List<Product>>()
    val allProduct :LiveData<List<Product>> get() = _allProduct
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
        _allProduct.value = filteredList
    }

    fun updateRv(){
        viewModelScope.launch {
            val product = stockRepo.getProductListByCategoryId(null)
            _allProduct.value = product
            _unFilteredProduct.value = product
        }
    }
}