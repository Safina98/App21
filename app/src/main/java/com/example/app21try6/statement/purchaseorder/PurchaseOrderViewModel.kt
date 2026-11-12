package com.example.app21try6.statement.purchaseorder

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.models.PurchaseOrderModel
import com.example.app21try6.database.models.PurchaseOrderRaw
import com.example.app21try6.database.models.SelectableItem
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.tables.SuplierTable
import com.example.app21try6.stock.exportprice.espTag
import com.example.app21try6.toTitleCase
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

    var exportText= MutableLiveData<String>("")
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    val interval= MutableLiveData<Int>(45)

    private val _categoryOptions = MutableLiveData<List<SelectableItem>>(emptyList())
    val categoryOptions: LiveData<List<SelectableItem>> = _categoryOptions

    private val _productOptions = MutableLiveData<List<SelectableItem>?>(emptyList())
    val productOptions: LiveData<List<SelectableItem>?> = _productOptions

    init {
        loadCategoryOptions()

    }

    private fun loadCategoryOptions() {
        viewModelScope.launch {
            try {
                // repository switches to Dispatchers.IO internally
                _categoryOptions.value = stockRepo.getCategoryNameListWithAll().map { SelectableItem(it) }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load categories"
            }
        }
    }

    private fun loadProductOptions() {
        viewModelScope.launch {
            try {
                // repository switches to Dispatchers.IO internally
                _productOptions.value = stockRepo.getBProductNameListBySuplierName(suplierName.value?:"")
                Log.i(pOTag,"viewmodel brand ${_productOptions.value}")
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to brand"
                Log.i(pOTag,"viewmodel brand ${e}")
            }
        }
    }

    private var selectedCategories: Set<String> = emptySet()

    private var selectedProducts: Set<String> = emptySet()

    fun setSelectedCategories(names: Set<String>) {
        Log.i(pOTag,"setSelectedCategories ${names}")
        selectedCategories = names
        recalculateOrders()
    }
    fun setSelectedBrands(names: Set<String>) {
        Log.i(pOTag,"setSelectedCategories ${names}")
        selectedProducts = names
        recalculateOrders()
    }
    fun draftText() {
        Log.i(espTag,"draftTextCalled")
        val text = _items.value?.sortedWith(
            compareBy<PurchaseOrderModel> { it.productId }
                .thenByDescending { it.purcahseOrder }
        )
            ?.filter { it.purcahseOrder!=0 && it.purcahseOrder!=null}
            ?.joinToString("\n") { "${it.subName.toTitleCase()} ${it.purcahseOrder} Roll" }
            ?: ""
        exportText.value = text
        Log.i(espTag,"${text}")
        exportText.value = text
    }

private var rawCache: List<PurchaseOrderRaw> = emptyList()

    fun loadPurchaseOrder() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // repository switches to Dispatchers.IO internally
//                val products=stockRepo.getAllProductTable()
//                products.forEach {
//                    Log.i(pOTag,"${it.product_name}, suplierID: ${it.suplierId}")
//                }
                rawCache = expensesRepository.fetchPurchaseOrderRaw(suplierName.value ?: "")
                _items.value = expensesRepository.calculateOrders(rawCache, interval.value ?: 45)
                loadProductOptions()
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load purchase orders"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun recalculateOrders() {
        val days = interval.value ?: return
        if (days <= 0) return
        val filtered = rawCache
            .filter { selectedCategories.isEmpty() || it.categoryName in selectedCategories }
            .filter { selectedProducts.isEmpty() || it.productName in selectedProducts }

        _items.value = expensesRepository.calculateOrders(filtered, days)
    }

    fun updateProduct(){
        viewModelScope.launch {
            //expensesRepository.insertSuplier(SuplierTable(14L,"Sinyoung","Surabaya"))
           // expensesRepository.updateProductSuppliersFromPurchases()
        }
    }

}