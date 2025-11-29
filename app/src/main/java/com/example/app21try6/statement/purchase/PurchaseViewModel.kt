package com.example.app21try6.statement.purchase

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.*
import androidx.lifecycle.viewModelScope
import com.example.app21try6.Constants
import com.example.app21try6.database.daos.ExpenseCategoryDao
import com.example.app21try6.database.daos.ExpenseDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.DetailWarnaDao
import com.example.app21try6.database.daos.InventoryLogDao
import com.example.app21try6.database.daos.InventoryPurchaseDao
import com.example.app21try6.database.daos.SuplierDao
import com.example.app21try6.database.models.SubWithPriceModel
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.ExpenseCategory
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.InventoryPurchase
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.formatRupiah
import com.example.app21try6.getMonthNumber
import com.example.app21try6.statement.DiscountAdapterModel
import com.example.app21try6.statement.expenses.tagg
import com.example.app21try6.stock.brandstock.CategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale
import java.util.UUID

val tagp="PURCHASEPROBS"
@RequiresApi(Build.VERSION_CODES.O)
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

    //expenses
    var ecId = MutableLiveData<Int?>(null)
    val allExpenseCategory=expenseCategoryDao.getAllExpenseCategoryModel()
    val _allExpenseCategorName = MutableLiveData<List<String>>()
    val allExpenseCategoryName:LiveData<List<String>> get() =_allExpenseCategorName
    val _allExpenseFromDb = MutableLiveData<List<DiscountAdapterModel>>()
    val allExpensesFromDB :LiveData<List<DiscountAdapterModel>> get() = _allExpenseFromDb
    val _unfilteredExpesne=MutableLiveData<List<DiscountAdapterModel>>()

    private val _selectedECSpinner = MutableLiveData<String>()
    val selectedECSpinner: LiveData<String> get() = _selectedECSpinner
    private val _selectedYearSpinner=MutableLiveData<String>()
    val selectedYearSpinner:LiveData<String>get()=_selectedYearSpinner
    private val _selectedMonthSpinner=MutableLiveData<String>()
    val selectedMonthSpinner:LiveData<String>get()=_selectedMonthSpinner
    private val _isNavigateToPurchase=MutableLiveData<Int?>(null)
    val isNavigateToPurchase:LiveData<Int?> get()=_isNavigateToPurchase
    val expenseSum = allExpensesFromDB.map{items->
        //val total = items.sumOf { it.expense_ammount?:0 }
        val total = items.sumOf { it.expense_ammount?.toLong() ?: 0L }
        formatRupiah(total.toDouble())
    }
    //purchase
    val suplierDummy= suplierDao.getAllSuplier()
    var inventoryList= mutableListOf<InventoryPurchase>()
    //val allSubProductFromDb=subProductDao.getSubProductWithPrice()
    private val searchQuery = MutableLiveData<String>()
    val allSubProductFromDb: LiveData<List<SubWithPriceModel>?> = searchQuery.switchMap { query ->
        subProductDao.getSubProductWithPrice(query)
    }
    private val _inventoryPurchaseList=MutableLiveData<List<InventoryPurchase>>()
    val inventoryPurchaseList:LiveData<List<InventoryPurchase>> get() = _inventoryPurchaseList
    var inventoryPurchaseId:Int=0

    val totalTransSum: LiveData<String> = inventoryPurchaseList.map{ list ->
        val sum = list.sumOf { it.totalPrice } ?:0.0
        formatRupiah(sum)?:""
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
                val inP = getPurchaseById()
            }
        }
    }

    fun getExpense(id:Int){
        viewModelScope.launch {
            if (id!=-1){
                val expense=getExpensesById(id)
                expenseMutable.value=expense
                suplierName.value = expense.expense_name.replace("Bayar ","")
            }
        }
    }

    fun searchProduct(query: String) {
        searchQuery.value = query
        val selectedSubProduct = allSubProductFromDb.value?.find { it.subProduct.sub_name == query }
        productPrice.value = selectedSubProduct?.purchasePrice?.toDouble() ?: 0.0
        if((selectedSubProduct?.purchasePrice ?: 0) > 220000){
            productNet.value = 1.0
        }else{
            productNet.value = selectedSubProduct?.default_net?:0.0
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
            item.status=Constants.BARANGLOGKET.masuk
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
                if (inventoryPurchaseList.value!=null){
                    insertPurchase(expenses,inventoryPurchaseList.value!!)
                }
            }
            else{
                val expenses=getExpensesById(id)
                val cleanedText = totalTransSum.value?.replace("[Rp,.\\s]".toRegex(), "")
                expenses.expense_ammount=cleanedText?.toInt()?: 0
                expenses.expense_name="Bayar ${suplierName.value}"
                updatePurchasesAndExpense(expenses,inventoryPurchaseList.value!!)
            }

        }
    }
    fun onBtnSimppanClick(){
        _isNavigateToExpense.value=true
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
        item.expensesId=id
        inventoryList.add(item)
        _inventoryPurchaseList.value=inventoryList
        Log.i("SavePruchaseProbs","addItemToList pId: $inventoryPurchaseId")
        Log.i("SavePruchaseProbs","item: $item")
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
//expenses
    fun deleteExpense(model: DiscountAdapterModel){
        viewModelScope.launch {
            val id=model.id
            deleteExpensesToDao(id!!)
            updateRv4()
        }
    }
    fun setSelectedECValue(value: String) {
        viewModelScope.launch {
            val id = getCEIdByName(value)
            ecId.value = id
            _selectedECSpinner.value = value
        }
    }
    fun setSelectedYearValue(value: String){
        viewModelScope.launch {
            _selectedYearSpinner.value=value
            updateRv4()
        }
    }

    fun setSelectedMonthValue(value: String){
        viewModelScope.launch {
            _selectedMonthSpinner.value=value
            updateRv4()
        }
    }
    fun filterProduct(query: String?) {
        viewModelScope.launch {
            val list = mutableListOf<DiscountAdapterModel>()
            if(!query.isNullOrEmpty()) {
                val month =getMonthNumber(_selectedMonthSpinner.value)
                val lislistByName=getExpenseByQuery(query,month)
                list.addAll(_allExpenseFromDb.value!!.filter {
                    it.expense_name?.lowercase(Locale.getDefault())!!.contains(query.toString().lowercase(
                        Locale.getDefault()))})
                list.addAll(lislistByName)
                val distinctList = list.distinctBy { it.id}
                _allExpenseFromDb.value =distinctList
            } else {
                list.addAll(_unfilteredExpesne.value!!)
                _allExpenseFromDb.value =list
            }

        }

    }
    fun insertExpenseCategory(categoryName:String){
        viewModelScope.launch {
            val expenseCategory= ExpenseCategory()
            expenseCategory.expense_category_name=categoryName
            insertExpensesCategory(expenseCategory)
            getAllexpenseCategory()
        }
    }
    fun updateExpenseCategory(category: CategoryModel){
        viewModelScope.launch {
            val expenseCategory= ExpenseCategory()
            expenseCategory.id =category.id
            expenseCategory.expense_category_name=category.categoryName
            updateExpensesCategory(expenseCategory)
        }
    }
    fun deleteExpenseCategory(categoryModel: CategoryModel){
        viewModelScope.launch{
            deleteExpensesCategoryToDao(categoryModel.id)
            getAllexpenseCategory()
        }
    }
    fun getAllexpenseCategory(){
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO){
                expenseCategoryDao.getAllExpenseCategoryName()
            }

            val l =list.toMutableList()
            l.add(0,"ALL")
            _allExpenseCategorName.value=l

        }
    }
    fun insertExpense(expenseNamed:String, expenseAmmount:Int?, expenseDate: Date, expenseCatName:String){
        viewModelScope.launch{
            val expenses= Expenses()
            val catId =getECIdByName(expenseCatName)
            expenses.expense_name=expenseNamed
            expenses.expense_ammount=expenseAmmount
            expenses.expense_date=expenseDate
            expenses.expense_ref=UUID.randomUUID().toString()
            expenses.expense_category_id=catId?:0
            //Log.i(tagg,"date $expenseDate")
            insertExpense(expenses)

            updateRv4()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateExpenses(expensesM: DiscountAdapterModel){
        viewModelScope.launch {
            val expenses= Expenses()
            val catId =getECIdByName(expensesM.expense_category_name!!)
            expenses.id=expensesM.id!!
            expenses.expense_name=expensesM.expense_name!!
            expenses.expense_category_id= catId?:0
            expenses.expense_ammount=expensesM.expense_ammount
            expenses.expense_ref=expensesM.expense_ref!!
            expenses.expense_date=expensesM.date
            updateExpenseToDao(expenses)
            updateRv4()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateRv4(){
        viewModelScope.launch {
            val month: String?
            val year: String?
            month = getMonthNumber(_selectedMonthSpinner.value)
            year = if(_selectedYearSpinner.value=="ALL") null else _selectedYearSpinner.value
            performDataFiltering(month, year)
        }
    }
    private suspend fun performDataFiltering(startDate: String?, endDate: String?) {
        val filteredData = withContext(Dispatchers.IO) { expenseDao.getAllExpense(startDate,endDate,ecId.value)}
        _allExpenseFromDb.value = filteredData
        _unfilteredExpesne.value=filteredData

    }
    private suspend fun getPurchaseById():InventoryPurchase?{
        return withContext(Dispatchers.IO){
            invetoryPurchaseDao.getPurchaseById(0)
        }
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
    private suspend fun getBrandId(productId:Int?):Long?{
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
    //expenses
    private suspend fun insertExpensesCategory(expenseCategory: ExpenseCategory){
        withContext(Dispatchers.IO){
            expenseCategoryDao.insert(expenseCategory)
        }
    }
    private suspend fun updateExpensesCategory(expenseCategory: ExpenseCategory){
        withContext(Dispatchers.IO){
            expenseCategoryDao.update(expenseCategory)
        }
    }
    private suspend fun deleteExpensesCategoryToDao(id:Int){
        withContext(Dispatchers.IO){
            expenseCategoryDao.delete(id)
        }
    }

    private suspend fun updateExpenseToDao(expenses: Expenses){
        withContext(Dispatchers.IO){
            expenseDao.update(expenses)
        }
    }
    private suspend fun getECIdByName(name:String):Int?{
        return withContext(Dispatchers.IO){
            expenseCategoryDao.getECIdByName(name)
        }
    }
    private suspend fun deleteExpensesToDao(id:Int){
        withContext(Dispatchers.IO){
            expenseDao.delete(id)
        }
    }
    private suspend fun getCEIdByName(name: String):Int?{
        return withContext(Dispatchers.IO){
            expenseCategoryDao.getECIdByName(name)
        }
    }
    private suspend fun getExpenseByQuery(query: String,month:String?):List<DiscountAdapterModel>{
        return withContext(Dispatchers.IO){
            Log.i("FilterProbs","month: ${_selectedMonthSpinner.value}; year:${_selectedYearSpinner.value}")
            expenseDao.getExpenseByQuery(query,month,_selectedYearSpinner.value)
        }
    }
    fun onTxtTransSumLongClikc(){
        _transSumDateLongClick.value = true
    }
    fun onTxtTransSumLongClikced(){_transSumDateLongClick.value = false}
    fun onItemAdded(){
        _isAddItemClick.value=false
    }
    fun onRvClick(){
        _isRvClick.value=true
    }
    fun onRvClicked(){
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

    fun onNavigateToPurcase(id:Int){
        _isNavigateToPurchase.value=id
    }
    fun onNavigatedToPurchase(){
        _isNavigateToPurchase.value=null
    }

}