package com.example.app21try6.stock.exportprice

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.models.ExportStockPriceModel
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
    private var _allProduct = MutableLiveData<List<ExportStockPriceModel>>()

    private val _newList = MutableLiveData<List<ExportStockPriceModel>>()
    val newList: LiveData<List<ExportStockPriceModel>> get() = _newList

    private var _isPickingProduct= MutableLiveData<Boolean>(true)
    val isPickingProduct: LiveData<Boolean>get() = _isPickingProduct

    var exportText= MutableLiveData<String>("")



    fun draftText() {
        Log.i(espTag,"draftTextCalled")
        val text = _unFilteredProduct.value
            ?.filter { it.isChecked }
            ?.joinToString("\n") { "${it.productName?.toTitleCase()} ${formatRupiah(it.productPrice?.toDouble())}" }
            ?: ""
        exportText.value = text
        Log.i(espTag,"${text}")
        exportText.value = text
    }
    fun onProductClicked(item: ExportStockPriceModel) {
        Log.i(espTag,"onProductClickCalled clicked")
        _unFilteredProduct.value = _unFilteredProduct.value?.map {
            if (it.id== item.id) it.copy(isChecked = true) else it
        }
        _newList.value = _newList.value?.map {
            if (it.id == item.id) it.copy(isChecked = true) else it
        }
    }
    fun pickProduct(){
        _isPickingProduct.value=true
    }
    fun donePickProduct(){
        _isPickingProduct.value=false
    }

    private val _unFilteredProduct = MutableLiveData<List<ExportStockPriceModel>>()
    fun filterProduct(query: String?) {
        val originalList = _unFilteredProduct.value ?: return
        val filteredList = if (!query.isNullOrEmpty()) {
            val input = query.lowercase(Locale.getDefault()).trim().replace(" ","")
            originalList.filter { merk ->
                val name = merk.productName?.lowercase(Locale.getDefault())?.trim()?.replace(" ","")
                val words = name?.split(" ")
                words?.any { word ->
                    word.contains(input)
                } ?: false
            }
        } else {
            originalList
        }
        _newList.value = filteredList
    }

    fun updateRv(){
        viewModelScope.launch {
            val espModel = stockRepo.getESPModel()
            _allProduct.value = espModel
            _unFilteredProduct.value = espModel
            _newList.value = espModel.map { it.copy() }
        }
    }
}