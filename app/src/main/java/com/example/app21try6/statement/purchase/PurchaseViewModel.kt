package com.example.app21try6.statement.purchase

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.InventoryLog
import com.example.app21try6.database.InventoryPurchase
import com.example.app21try6.database.SuplierTable
import com.example.app21try6.database.daos.ExpenseCategoryDao
import com.example.app21try6.database.daos.ExpenseDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.DetailWarnaTable
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.formatRupiah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

val tagp="PURCHASEPROBS"
class PurchaseViewModel(application: Application,
                        val id:Int,
                        val expenseDao: ExpenseDao,
                        val expenseCategoryDao: ExpenseCategoryDao,
                        val subProductDao:SubProductDao,
                        val productDao:ProductDao

): AndroidViewModel(application) {

    val suplierDummy= listOf<SuplierTable>(
        SuplierTable(1,"Mitra Jaya","Jakarta"),
        SuplierTable(2,"Polystar","Jakarta"),
        SuplierTable(3,"Busa Yerry","Surabaya"),
        SuplierTable(4,"Vision","Jakarta"),
        SuplierTable(5,"PT. SIMNU","Surabaya"),
        SuplierTable(6,"Aneka Lancar","Makassar")
    )
    var inventoryList= mutableListOf<InventoryPurchase>()
    val allSubProductFromDb=subProductDao.getSubProductWithPrice()
    private val _inventoryPurchaseList=MutableLiveData<List<InventoryPurchase>>()
    val inventoryPurchaseList:LiveData<List<InventoryPurchase>> get() = _inventoryPurchaseList

    val totalTransSum: LiveData<String> = Transformations.map(inventoryPurchaseList) { list ->
        var sum = list.sumOf { it.totalPrice } ?:0.0
        formatRupiah(sum)
    }

    private val _isAddItemClick=MutableLiveData<Boolean>(false)
    val isAddItemClick:LiveData<Boolean> get() = _isAddItemClick

    private val _isNavigateToExpense=MutableLiveData<Boolean>(false)
    val isNavigateToExpense:LiveData<Boolean> get() = _isNavigateToExpense

    private val _isRvClick=MutableLiveData<Boolean>(false)
    val isRvClick:LiveData<Boolean> get() = _isRvClick

    val logList= mutableListOf<InventoryLog>()
    val subDetailList= mutableListOf<DetailWarnaTable>()

    val inventoryPurchase=MutableLiveData<InventoryPurchase?>()
    val productName=MutableLiveData<String>("")
    val suplierName=MutableLiveData<String>("")
    val productPrice=MutableLiveData<Double>(0.0)
    val productNet=MutableLiveData<Double>(0.0)
    val productQty=MutableLiveData<Int>(0)
    val totalPrice = MediatorLiveData<Double>().apply {
        addSource(productPrice) { calculateTotalPrice() }
        addSource(productNet) { calculateTotalPrice() }
        addSource(productQty) { calculateTotalPrice() }
    }

    private fun calculateTotalPrice() {
        val price = productPrice.value ?: 0.0
        val net = productNet.value ?: 0.0
        val qty = productQty.value ?: 0
        totalPrice.value = price * net * qty
    }
    fun setProductPriceAndNet(name:String){
        viewModelScope.launch {
            val selectedSubProduct = allSubProductFromDb.value?.find { it.subProduct.sub_name == name }
            productPrice.value = selectedSubProduct?.productPrice?.toDouble() ?: 0.0
            productNet.value = selectedSubProduct?.default_net?:0.0
        }
    }
    fun onAddClick(){
        if (_isRvClick.value==true){
            updateItem()
        }else{
            addItemToList()
        }
    }
    fun updateItem(){
        if (inventoryPurchase.value!=null){
            val index = inventoryList.indexOfFirst { it.id == inventoryPurchase.value?.id}
            val item = inventoryPurchase.value!!
            item.name=productName.value?:""
            item.supName=suplierName.value?:""
            item.netQty=productNet.value?.toDouble() ?: 0.0
            item.batchCount=productQty.value?.toInt() ?: 0
            item.price=productPrice.value?.toInt() ?: 0
            item.totalPrice=totalPrice.value?.toDouble() ?: 0.0
            item.ref=inventoryPurchase.value!!.ref
            item.status="PENDING"
            item.subProductId=allSubProductFromDb.value?.find { it.subProduct.sub_name ==productName.value }?.subProduct?.sub_id ?: inventoryPurchase.value!!.subProductId
            item.suplierId=suplierDummy.find { it.suplierName==suplierName.value }?.id ?: inventoryPurchase.value!!.suplierId
            item.purchaseDate= Date()
            if (index != -1) {
                inventoryList[index] = item
            }
            _isAddItemClick.value=true
        }
    }

    fun savePurchase(){
        viewModelScope.launch {
            if (id==-1){
                val expenses=Expenses()
                val cleanedText = totalTransSum.value?.replace("[Rp,.\\s]".toRegex(), "")
                expenses.expense_ammount=cleanedText?.toInt()?: 0
                expenses.expense_ref=UUID.randomUUID().toString()
                expenses.expense_category_id=getCategoryIdByName("BELI BARANG")?:0
                expenses.expense_name="Bayar ${suplierName.value}"
                expenses.expense_date=Date()
                val id=insertExpense(expenses)
            }
            else{
                val expenses=getExpensesById(id)
                expenses.expense_ammount=totalTransSum.value?.toInt()?: 0
                expenses.expense_name="Bayar ${suplierName.value}"
                update(expenses)
            }
            _isNavigateToExpense.value=true
        }
    }

    fun addItemToList(){
        val item=InventoryPurchase()
        item.id=0
        item.name=productName.value?:""
        item.supName=suplierName.value?:""
        item.netQty=productNet.value?.toDouble() ?: 0.0
        item.batchCount=productQty.value?.toInt() ?: 0
        item.price=productPrice.value?.toInt() ?: 0
        item.totalPrice=totalPrice.value?.toDouble() ?: 0.0
        item.ref=UUID.randomUUID().toString()
        item.status="PENDING"
        item.subProductId=allSubProductFromDb.value?.find { it.subProduct.sub_name ==productName.value }?.subProduct?.sub_id ?: 0
        item.suplierId=suplierDummy.find { it.suplierName==suplierName.value }?.id ?: 0
        item.purchaseDate= Date()
        inventoryList.add(item)
        _inventoryPurchaseList.value=inventoryList
        _isAddItemClick.value=true
    }
    fun rvClick(item: InventoryPurchase){
        productName.value=item.name
        productPrice.value=item.price.toDouble()
        productQty.value=item.batchCount
        productNet.value=item.netQty
        totalPrice.value=item.totalPrice
        inventoryPurchase.value=item
        Log.i(tagp,"${productNet.value}")
        _isRvClick.value=true
    }
    fun addInventoryLog(){
        viewModelScope.launch {

            for (i in inventoryList){
                val subDetail= DetailWarnaTable()
                subDetail.subId=i.subProductId
                subDetail.batchCount=i.batchCount
                subDetail.netCount=i.netQty
                subDetail.ket
                subDetail.ref=UUID.randomUUID().toString()
                subDetailList.add(subDetail)
                //update or insert detail
                val inventoryLog=InventoryLog()
                inventoryLog.detailWarnaRef=subDetail.ref
                inventoryLog.barangLogDate=i.purchaseDate
                inventoryLog.subProductId=i.subProductId?:0
                inventoryLog.productId=getProductId(i.subProductId)
                inventoryLog.merkId=getBrandId(inventoryLog.productId)
                inventoryLog.barangLogDate=i.purchaseDate
                inventoryLog.barangLogRef=UUID.randomUUID().toString()
                inventoryLog.isi=i.netQty
                inventoryLog.pcs=i.batchCount
                inventoryLog.barangLogKet="PENDING"
                logList.add(inventoryLog)
            }

            subDetailList.forEach {
                Log.i(tagp,it.toString())
            }
            Log.i(tagp,"InventoryLog")
            logList.forEach {
                Log.i(tagp,it.toString())
            }
        }
    }
    fun updateInventoryStock(){

    }

    fun onItemAdded(){
        _isAddItemClick.value=false
        _isRvClick.value=false
    }
    fun onNavigatedToExpense(){
        _isNavigateToExpense.value=false
    }

    fun onClearClick(){
        productName.value=""
        productQty.value=0
        productNet.value=0.0
        productPrice.value=0.0
    }

    private suspend fun insertExpense(expenses: Expenses):Int{
        return withContext(Dispatchers.IO){
            expenseDao.insert(expenses).toInt()
        }
    }
    private suspend fun update(expenses: Expenses){
        withContext(Dispatchers.IO){
            expenseDao.update(expenses)
        }
    }
    private suspend fun getExpensesById(id:Int):Expenses{
        return withContext(Dispatchers.IO){
            expenseDao.getExpenseById(id)
        }
    }
    private suspend fun getCategoryIdByName(name:String):Int?{
        return withContext(Dispatchers.IO){
            expenseCategoryDao.getECIdByName(name)
        }
    }
    private suspend fun getBrandId(productId:Int):Int{
        return withContext(Dispatchers.IO){
            productDao.getBrandIdByProductId(productId)
        }
    }
    private suspend fun getProductId(subId:Int):Int{
        return withContext(Dispatchers.IO){
            subProductDao.getProductIdBySubId(subId)
        }
    }

}