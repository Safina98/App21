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
import com.example.app21try6.database.models.PaymentModel
import com.example.app21try6.database.models.SubWithPriceModel
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.LogsRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.ExpenseCategory
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.InventoryPurchase
import com.example.app21try6.database.tables.PurchaseDiscount
import com.example.app21try6.formatRupiah
import com.example.app21try6.getMonthNumber
import com.example.app21try6.statement.DiscountAdapterModel
import com.example.app21try6.stock.brandstock.CategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.onFailure

val tagp="PURCHASEPROBS"
@RequiresApi(Build.VERSION_CODES.O)
class PurchaseViewModel(application: Application,
                        val id:Int,
                        val stockRepo: StockRepositories,
                        val expenseRepo: ExpensesRepository,
                        val logRepo: LogsRepository
): AndroidViewModel(application) {

    //expenses
    var ecId = MutableLiveData<Int?>(null)
    val allExpenseCategory=expenseRepo.getExpenseCateroryModel()//expenseCategoryDao.getAllExpenseCategoryModel()
    val _allExpenseCategorName = MutableLiveData<List<String>>()
    val allExpenseCategoryName:LiveData<List<String>> get() =_allExpenseCategorName
    val _allExpenseFromDb = MutableLiveData<List<DiscountAdapterModel>>()
    val allExpensesFromDB :LiveData<List<DiscountAdapterModel>> get() = _allExpenseFromDb
    val _unfilteredExpesne=MutableLiveData<List<DiscountAdapterModel>>()

    val expenseIsKeeped=expenseRepo.getExpenseIsKeeped(id)

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
    val suplierDummy= expenseRepo.getAllSuplier()//suplierDao.getAllSuplier()

    var inventoryList= mutableListOf<InventoryPurchase>()
    //val allSubProductFromDb=subProductDao.getSubProductWithPrice()
    private val searchQuery = MutableLiveData<String>()
    val allSubProductFromDb:
            LiveData<List<SubWithPriceModel>?> = searchQuery.switchMap{ query ->
        //subProductDao.getSubProductWithPrice(query)
                stockRepo.getSubProductWithPrice(query)
    }
    var inventoryPurchaseId:Int=0
    private val _inventoryPurchaseList=MutableLiveData<List<InventoryPurchase>>()
    val inventoryPurchaseList:LiveData<List<InventoryPurchase>> get() = _inventoryPurchaseList


    val pDiscountList=expenseRepo.getPDiscountByExpense(id)

    val totalTransSum: LiveData<String> = MediatorLiveData<String>().apply {
        var purchaseSum = 0.0
        var discountSum = 0.0

        fun update() {
            value = formatRupiah(purchaseSum + discountSum)
        }

        addSource(inventoryPurchaseList) { list ->
            purchaseSum = list.sumOf { it.totalPrice }
            update()
        }
        addSource(pDiscountList) { list ->
            discountSum = list.sumOf { it.payment_ammount?.toDouble() ?: 0.0}
            update()
        }
    }

    private val _isAddItemClick=MutableLiveData<Boolean>(false)
    val isAddItemClick:LiveData<Boolean> get() = _isAddItemClick

    private val _isCrudSucsess=MutableLiveData<String?>(null)
    val isCrudSucsess:LiveData<String?> get() = _isCrudSucsess

    private val _isNavigateToExpense=MutableLiveData<Boolean>(false)
    val isNavigateToExpense:LiveData<Boolean> get() = _isNavigateToExpense

    private val _isRvClick=MutableLiveData<Boolean>(false)
    val isRvClick:LiveData<Boolean> get() = _isRvClick

    val logList= mutableListOf<InventoryLog>()
    val subDetailList= mutableListOf<DetailWarnaTable>()

    val inventoryPurchase=MutableLiveData<InventoryPurchase?>()
    val expenseMutable=MutableLiveData<Expenses>()

    val subProductName=MutableLiveData<String>("")
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
                val list= expenseRepo.getInventoryPurchaseList(id)//withContext(Dispatchers.IO){invetoryPurchaseDao.selectPurchaseList(id)}
                inventoryList=list.toMutableList()
                _inventoryPurchaseList.value=list
              //  if (inventoryList!=null) inventoryPurchaseId= inventoryList.last().id*-1
                val inP = expenseRepo.getPurchaseById()
            }
        }
    }

    fun getNewInventoryList(id:Int){
        viewModelScope.launch {
            if (id!=-1){
                val list= expenseRepo.getInventoryPurchaseList(id)
                _inventoryPurchaseList.value=list
               // expenseRepo.insertSupliers()
            }
        }
    }

    fun updateExpenseName(){
        viewModelScope.launch {
            expenseRepo.updateExpenseName(id,suplierName.value?:"")
        }
    }

    fun getExpense(id:Int){
        viewModelScope.launch {
            if (id!=-1){
                val expense=expenseRepo.getExpensesById(id)//getExpensesById(id)
                expenseMutable.value=expense
                suplierName.value = expense.expense_name.replace("Bayar ","")
            }
        }
    }

    fun searchProduct(query: String) {
        if (inventoryPurchase.value==null){
            searchQuery.value = query
            val selectedSubProduct = allSubProductFromDb.value?.find { it.subProduct.sub_name == query }
            productPrice.value = selectedSubProduct?.purchasePrice?.toDouble() ?: 0.0
            if((selectedSubProduct?.purchasePrice ?: 0) > 231000){
                productNet.value = 1.0
            }else{
                productNet.value = selectedSubProduct?.default_net?:0.0
            }
        }
    }

    fun onBtnSimppanClick(){
        _isNavigateToExpense.value=true
    }
    fun getAutoIncrementId(){
        inventoryPurchaseId-=1
    }
    fun insertPurchase(){
        viewModelScope.launch{
            val item=InventoryPurchase()
            item.subProductName=subProductName.value?:""
            item.net=productNet.value?.toDouble() ?: 0.0
            item.batchCount=productQty.value?.toDouble() ?: 0.0
            item.price=productPrice.value?.toInt() ?: 0
            item.totalPrice=totalPrice.value?.toDouble() ?: 0.0
            item.id= System.currentTimeMillis()
            item.status="PENDING"
            item.expensesId=id
            item.needsSyncs=1
            item.id=inventoryPurchase.value?.id ?: 0L
            expenseRepo.insertPurchase(item,subProductName.value).onSuccess {
                clearAllButName()
                _isAddItemClick.value=true
                if (inventoryPurchase.value!=null) clearAllMutables()
                _isCrudSucsess.value = Constants.CRUDSTATUS.SUCSESS
                getNewInventoryList(id)
            }.onFailure {
                    throwable ->
                Log.e("PurchaseProbs", "failed: ${throwable.message}", throwable)
                _isCrudSucsess.value = Constants.CRUDSTATUS.FAILED
                //show snackbar that said insert failed
            }
        }
    }

    fun rvClick(item: InventoryPurchase){
        subProductName.value=item.subProductName
        productPrice.value=item.price.toDouble()
        productQty.value=item.batchCount.toInt()
        productNet.value=item.net
        totalPrice.value=item.totalPrice
        inventoryPurchase.value=item
    }
    fun clearAllMutables(){
        subProductName.value=""
        productPrice.value=0.0
        productQty.value=1
        productNet.value=0.0
        totalPrice.value=0.0
        inventoryPurchase.value=null
    }
    fun addInventoryLog(){
        viewModelScope.launch {
            stockRepo.insertDetailWarnaFromPurchase(inventoryPurchaseList.value,Date(),id).onSuccess {
                _isCrudSucsess.value= Constants.CRUDSTATUS.SUCSESS
            }.onFailure {throwable ->
                _isCrudSucsess.value= Constants.CRUDSTATUS.FAILED
                Log.e(tagp,"$throwable")
            }
        }
    }


    fun deletePurchase(purchase: InventoryPurchase){
        viewModelScope.launch {
            Log.i(tagp,"view model deletePurchaseCalled")
            expenseRepo.deletePurchase(purchase).onSuccess {
                getNewInventoryList(id)
            }.onFailure { throwable ->
            }
        }
        _isAddItemClick.value=true
    }

    fun onClearClick(){
        subProductName.value=""
        clearAllMutables()
        //clearAllButName()
    }
    fun clearAllButName(){
        productQty.value=1
    }
    fun updateLongClickedDate(date:Date){
        viewModelScope.launch {
            if (id!=-1) {
                val expense=expenseRepo.getExpensesById(id)
                expense.expense_date = date
                expenseRepo.updateExpense(expense)
                expenseMutable.value=expense
            }
        }
    }
    fun addDiscount(price:Int){
        viewModelScope.launch {
            val purchaseDiscount= PurchaseDiscount()
            purchaseDiscount.id= System.currentTimeMillis()
            purchaseDiscount.discountName="Diskon"
            purchaseDiscount.discountValue=price*-1
            purchaseDiscount.expenseId=id
            expenseRepo.insertPurchaseDiscount(purchaseDiscount).onSuccess {
                getNewInventoryList(id)
            }   .onFailure {

            }
        }
    }

    fun deleteDiscount(paymentModel: PaymentModel){
        viewModelScope.launch {
            expenseRepo.deletePurchaseDiscount(paymentModel)
        }
    }
    fun updateDiscount(item:InventoryPurchase){

    }
//expenses
    fun deleteExpense(model: DiscountAdapterModel){
        viewModelScope.launch {
            val id=model.id
            expenseRepo.deleteExpensesToDao(id!!)
            updateRv4()
        }
    }
    fun setSelectedECValue(value: String) {
        viewModelScope.launch {
            val id = expenseRepo.getCategoryIdByName(value)//getCEIdByName(value)
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
                val lislistByName=expenseRepo.getExpenseByQuery(query,month,_selectedYearSpinner.value)
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
            expenseCategory.eCCloudId=System.currentTimeMillis()
            expenseRepo.insertExpensesCategory(expenseCategory)
            getAllexpenseCategory()
        }
    }
    fun updateExpenseCategory(category: CategoryModel){
        viewModelScope.launch {
            val expenseCategory= ExpenseCategory()
            expenseCategory.id =category.id
            expenseCategory.expense_category_name=category.categoryName
            expenseCategory.needsSyncs=1
            expenseRepo.updateExpensesCategory(expenseCategory)
        }
    }
    fun deleteExpenseCategory(categoryModel: CategoryModel){
        viewModelScope.launch{
            expenseRepo.deleteExpensesCategoryToDao(categoryModel.id)
            getAllexpenseCategory()
        }
    }
    fun getAllexpenseCategory(){
        viewModelScope.launch {
            val list =expenseRepo.getAllExpenseCategoryName()
            val l =list.toMutableList()
            l.add(0,"ALL")
            _allExpenseCategorName.value=l

        }
    }
    fun insertExpense(expenseNamed:String, expenseAmmount:Int?, expenseDate: Date, expenseCatName:String){
        viewModelScope.launch{
            val expenses= Expenses()
            val catId =expenseRepo.getCategoryIdByName(expenseCatName)///getECIdByName(expenseCatName)
            expenses.expense_name=expenseNamed
            expenses.expense_ammount=expenseAmmount
            expenses.expense_date=expenseDate
            expenses.expense_ref=UUID.randomUUID().toString()
            expenses.expense_category_id=catId?:0
            expenses.expenseCloudId=System.currentTimeMillis()
            expenses.needsSyncs=1
            //Log.i(tagg,"date $expenseDate")
           // insertExpense(expenses)
            expenseRepo.insertExpense(expenses)

            updateRv4()
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateExpenses(expensesM: DiscountAdapterModel){
        viewModelScope.launch {
            val expenses= Expenses()
            val catId =expenseRepo.getCategoryIdByName(expensesM.expense_category_name?:"")//getECIdByName(expensesM.expense_category_name!!)
            expenses.id=expensesM.id!!
            expenses.expense_name=expensesM.expense_name!!
            expenses.expense_category_id= catId?:0
            expenses.expense_ammount=expensesM.expense_ammount
            expenses.expense_ref=expensesM.expense_ref!!
            expenses.expense_date=expensesM.date
            expenses.needsSyncs=1
            expenseRepo.updateExpense(expenses)
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
        val filteredData = expenseRepo.getFilteredExpense(startDate,endDate,ecId.value)//withContext(Dispatchers.IO) { expenseDao.getAllExpense(startDate,endDate,ecId.value)}
        _allExpenseFromDb.value = filteredData
        _unfilteredExpesne.value=filteredData

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

    fun addNewExpense(){
         viewModelScope.launch {
             val expenses=Expenses()
             expenses.expense_ammount=0
             expenses.expense_ref=UUID.randomUUID().toString()
             expenses.expense_category_id=expenseRepo.getCategoryIdByName("BELI BARANG")?:0
             expenses.expense_name="Bayar ${suplierName.value}"
             expenses.expense_date=Date()
             expenses.expenseCloudId=System.currentTimeMillis()
             expenses.needsSyncs=1
             val eId = expenseRepo.insertExpense(expenses)
             onNavigateToPurcase(eId)
         }
    }

    fun onNavigateToPurcase(id:Int){
        _isNavigateToPurchase.value=id
    }

    fun onNavigatedToPurchase(){
        _isNavigateToPurchase.value=null
    }

}