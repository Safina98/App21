package com.example.app21try6.statement.purchase

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.app21try6.BARANGLOGKET
import com.example.app21try6.database.daos.ExpenseCategoryDao
import com.example.app21try6.database.daos.ExpenseDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.DetailWarnaDao
import com.example.app21try6.database.daos.InventoryLogDao
import com.example.app21try6.database.daos.InventoryPurchaseDao
import com.example.app21try6.database.daos.SuplierDao
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.InventoryPurchase
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
                        val productDao:ProductDao,
                        val invetoryPurchaseDao: InventoryPurchaseDao,
                        val inventoryLogDao: InventoryLogDao,
                        val detailWarnaDao: DetailWarnaDao,
                        val suplierDao:SuplierDao

): AndroidViewModel(application) {

    val suplierDummy= suplierDao.getAllSuplier()
    var inventoryList= mutableListOf<InventoryPurchase>()
    val allSubProductFromDb=subProductDao.getSubProductWithPrice()
    private val _inventoryPurchaseList=MutableLiveData<List<InventoryPurchase>>()
    val inventoryPurchaseList:LiveData<List<InventoryPurchase>> get() = _inventoryPurchaseList
    var inventoryPurchaseId:Int=0

    val totalTransSum: LiveData<String> = Transformations.map(inventoryPurchaseList) { list ->
        val sum = list.sumOf { it.totalPrice } ?:0.0
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
    val expenseMutable=MutableLiveData<Expenses>()

    val productName=MutableLiveData<String>("")
    val suplierName=MutableLiveData<String>("")
    val productPrice=MutableLiveData<Double>(0.0)
    val productNet=MutableLiveData<Double>(0.0)
    val productQty=MutableLiveData<Int>(1)

    private val _transSumDateLongClick = MutableLiveData<Boolean>()
    val transSumDateLongClick:LiveData<Boolean> get() = _transSumDateLongClick

    private val _isDiscountClicked=MutableLiveData<InventoryPurchase?>()
    val isDiscountClicked:LiveData<InventoryPurchase?> get() = _isDiscountClicked

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

    fun getInventoryList(id:Int){
        viewModelScope.launch {
            if (id!=-1){
                val list= withContext(Dispatchers.IO){invetoryPurchaseDao.selectPurchaseList(id)}
                inventoryList=list.toMutableList()
                _inventoryPurchaseList.value=list
                if (inventoryList!=null) inventoryPurchaseId= inventoryList.last().id*-1
            }
        }
    }
    fun getExpense(id:Int){
        viewModelScope.launch {
            if (id!=-1){
                val expense=getExpensesById(id)
                expenseMutable.value=expense
            }
        }
    }

    fun setProductPriceAndNet(name:String){
        viewModelScope.launch {
            val selectedSubProduct = allSubProductFromDb.value?.find { it.subProduct.sub_name == name }
            productPrice.value = selectedSubProduct?.purchasePrice?.toDouble() ?: 0.0
            if((selectedSubProduct?.purchasePrice ?: 0) > 220000){
                productNet.value = 1.0
            }else{
                productNet.value = selectedSubProduct?.default_net?:0.0
            }
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
            item.subProductName=productName.value?:""
            item.suplierName=suplierName.value?:""
            item.net=productNet.value?.toDouble() ?: 0.0
            item.batchCount=productQty.value?.toDouble() ?: 0.0
            item.price=productPrice.value?.toInt() ?: 0
            item.totalPrice=totalPrice.value?.toDouble() ?: 0.0
            item.ref=inventoryPurchase.value!!.ref
            item.status=BARANGLOGKET.masuk
            item.subProductId=allSubProductFromDb.value?.find { it.subProduct.sub_name ==productName.value }?.subProduct?.sub_id ?: inventoryPurchase.value!!.subProductId
            item.suplierId=suplierDummy.value?.find { it.suplierName==suplierName.value }?.id ?: inventoryPurchase.value!!.suplierId
            item.purchaseDate= Date()
            if (index != -1) {
                inventoryList[index] = item
                _inventoryPurchaseList.value=inventoryList
            }
            onClearClick()

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
                insertPurchase(expenses,inventoryPurchaseList.value!!)
            }
            else{
                val expenses=getExpensesById(id)
                val cleanedText = totalTransSum.value?.replace("[Rp,.\\s]".toRegex(), "")
                expenses.expense_ammount=cleanedText?.toInt()?: 0
                expenses.expense_name="Bayar ${suplierName.value}"
                updatePurchasesAndExpense(expenses,inventoryPurchaseList.value!!)
            }
            _isNavigateToExpense.value=true
        }
    }
    fun getAutoIncrementId(){
        inventoryPurchaseId-=1
    }

    fun addItemToList(){
        val item=InventoryPurchase()
        getAutoIncrementId()
        item.id=inventoryPurchaseId
        item.subProductName=productName.value?:""
        item.suplierName=suplierName.value?:""
        item.net=productNet.value?.toDouble() ?: 0.0
        item.batchCount=productQty.value?.toDouble() ?: 0.0
        item.price=productPrice.value?.toInt() ?: 0
        item.totalPrice=totalPrice.value?.toDouble() ?: 0.0
        item.ref=UUID.randomUUID().toString()
        item.status="PENDING"
        item.subProductId=allSubProductFromDb.value?.find { it.subProduct.sub_name ==productName.value }?.subProduct?.sub_id ?: 0
        item.suplierId=suplierDummy.value?.find { it.suplierName==suplierName.value }?.id ?: null
        item.purchaseDate= Date()
        inventoryList.add(item)
        _inventoryPurchaseList.value=inventoryList
        _isAddItemClick.value=true
        //onClearClick()
        clearAllButName()
    }
    fun rvClick(item: InventoryPurchase){
        productName.value=item.subProductName
        productPrice.value=item.price.toDouble()
        productQty.value=item.batchCount.toInt()
        productNet.value=item.net
        totalPrice.value=item.totalPrice
        inventoryPurchase.value=item
        _isRvClick.value=true
    }
    fun addInventoryLog(){
        viewModelScope.launch {
            for (i in inventoryList){
                if (i.status!="DISCOUNT"){
                    val subDetail= DetailWarnaTable()
                    subDetail.subId=i.subProductId!!
                    subDetail.batchCount=i.batchCount
                    subDetail.net=i.net
                    subDetail.ket
                    subDetail.ref=UUID.randomUUID().toString()
                    subDetailList.add(subDetail)
                    //update or insert detail
                    val inventoryLog=InventoryLog()
                    inventoryLog.detailWarnaRef=subDetail.ref
                    inventoryLog.barangLogDate=i.purchaseDate
                    inventoryLog.subProductId=i.subProductId
                    inventoryLog.productId=getProductId(i.subProductId!!)
                    inventoryLog.brandId=getBrandId(inventoryLog.productId?:0)
                    inventoryLog.barangLogDate=i.purchaseDate
                    inventoryLog.barangLogRef=UUID.randomUUID().toString()
                    inventoryLog.isi=i.net
                    inventoryLog.pcs=i.batchCount.toInt()
                    inventoryLog.barangLogKet="PENDING"
                    logList.add(inventoryLog)
                }
            }
            upsertDetailWarnaAndLog(subDetailList,logList)
        }
    }

    fun getDetailWarnaANdInventoryLog(){
        viewModelScope.launch {
            val detailWarnaList= withContext(Dispatchers.IO){inventoryLogDao.selectAllDetailWarna()}
            val inventoryLog= withContext(Dispatchers.IO){inventoryLogDao.selectAllDetailWarna()}
            Log.i(tagp,"$detailWarnaList")
            Log.i(tagp,"$inventoryLog")
        }
    }

    fun deletePurchase(purchase: InventoryPurchase){
        inventoryList.remove(purchase)
        _inventoryPurchaseList.value=inventoryList
        _isAddItemClick.value=true
    }



    fun onClearClick(){
        productName.value=""
        clearAllButName()
    }
    fun clearAllButName(){
        productQty.value=1
        productNet.value=0.0
        productPrice.value=0.0
    }
    fun updateLongClickedDate(date:Date){
        viewModelScope.launch {
            if (id!=-1) {
                val expense=getExpensesById(id)
                expense.expense_date = date
                updateExpense(expense)
                expenseMutable.value=expense
            }
        }
    }
    fun addDiscount(price:Int){
        val purchase=InventoryPurchase()
        getAutoIncrementId()
        purchase.id=inventoryPurchaseId
        purchase.net=1.0
        purchase.status="DISCOUNT"
        purchase.ref=UUID.randomUUID().toString()
        purchase.purchaseDate=Date()
        purchase.price=price*-1
        purchase.batchCount=1.0
        purchase.subProductName="DISCOUNT"
        purchase.totalPrice=price.toDouble() *-1
        Log.i("DiskProbs","$price")
        getAutoIncrementId()
        inventoryList.add(purchase)
        _inventoryPurchaseList.value=inventoryList
    }
    fun updateDiscount(item:InventoryPurchase){

    }



    private suspend fun insertPurchase(expenses: Expenses,list:List<InventoryPurchase>){
        withContext(Dispatchers.IO){
            invetoryPurchaseDao.insertPurchaseAndExpense(expenses,list)
        }
    }
    private suspend fun updatePurchasesAndExpense(expenses: Expenses, list:List<InventoryPurchase>){
        withContext(Dispatchers.IO){
            invetoryPurchaseDao.updatePurchasesAndExpense(expenses,list)
        }
    }

    private suspend fun updateExpense(expenses: Expenses){
        withContext(Dispatchers.IO){
            invetoryPurchaseDao.updateExpense(expenses)
        }
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
    private suspend fun getBrandId(productId:Int?):Int?{
        return withContext(Dispatchers.IO){
            productDao.getBrandIdByProductId(productId)
        }
    }
    private suspend fun getProductId(subId:Int?):Int?{
        return withContext(Dispatchers.IO){
            subProductDao.getProductIdBySubId(subId)
        }
    }
    private suspend fun upsertDetailWarnaAndLog(detailWarnaList:List<DetailWarnaTable>,
                                                 inventoryLogList: MutableList<InventoryLog>){
        withContext(Dispatchers.IO){
            inventoryLogDao.updateInsertDetailWarnaAndLog(detailWarnaList,inventoryLogList)
        }
    }
    fun onTxtTransSumLongClikc(){
        _transSumDateLongClick.value = true
    }
    fun onTxtTransSumLongClikced(){_transSumDateLongClick.value = false}
    fun onItemAdded(){
        _isAddItemClick.value=false
        _isRvClick.value=false
    }
    fun onNavigatedToExpense(){
        _isNavigateToExpense.value=false
    }
    fun onDiscountClick(){
        val purchase=InventoryPurchase()
        _isDiscountClicked.value=purchase
    }
    fun onDiscountClicked(){
        _isDiscountClicked.value=null
    }

}