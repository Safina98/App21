package com.example.app21try6.statement.purchaseorder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.models.PurchaseOrderModel
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.StockRepositories
import kotlinx.coroutines.launch

class PurchaseOrderViewModel(
    private val stockRepo: StockRepositories,
    private val expensesRepository: ExpensesRepository,
    application: Application
) : AndroidViewModel(application) {
    var suplierName = MutableLiveData<String>()
    val suplierList=expensesRepository.getAllSuplier()

    private val _items = MutableLiveData<List<PurchaseOrderModel>>(emptyList())
    val items: LiveData<List<PurchaseOrderModel>> = _items

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadPurchaseOrder() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // repository switches to Dispatchers.IO internally
                _items.value = expensesRepository.getPurchaseOrders(suplierName.value?:"")
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load purchase orders"
            } finally {
                _isLoading.value = false
            }
        }
    }

}