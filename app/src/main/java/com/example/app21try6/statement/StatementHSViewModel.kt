package com.example.app21try6.statement

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.daos.CustomerDao
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.tables.DiscountTable
import com.example.app21try6.database.tables.ExpenseCategory
import com.example.app21try6.database.daos.ExpenseCategoryDao
import com.example.app21try6.database.daos.ExpenseDao
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.tables.Product
import com.example.app21try6.formatRupiah
import com.example.app21try6.getMonthNumber
import com.example.app21try6.statement.expenses.tagg
import com.example.app21try6.stock.brandstock.CategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
@RequiresApi(Build.VERSION_CODES.O)
class StatementHSViewModel(application: Application,
                           val discountDao: DiscountDao,
                           val customerDao: CustomerDao,
                           val expenseDao: ExpenseDao,
                           val expenseCategoryDao: ExpenseCategoryDao,
                           val transDetailDao: TransDetailDao,
                           val transSumDao: TransSumDao

    ):AndroidViewModel(application) {

    val allDiscountFromDB= discountDao.getAllDiscount()
    val allCustomerFromDb=customerDao.allCustomer()
    val _allExpenseCategorName = MutableLiveData<List<String>>()
    val allExpenseCategoryName:LiveData<List<String>> get() =_allExpenseCategorName
    val _allExpenseFromDb = MutableLiveData<List<DiscountAdapterModel>>()
    val allExpensesFromDB :LiveData<List<DiscountAdapterModel>> get() = _allExpenseFromDb
    var id = 0
    val expenseCategoryName= MutableLiveData<String?>("")
    val allExpenseCategory=expenseCategoryDao.getAllExpenseCategoryModel()
    private val _selectedECSpinner = MutableLiveData<String>()
    val selectedECSpinner: LiveData<String> get() = _selectedECSpinner
    private val _selectedYearSpinner=MutableLiveData<String>()
    val selectedYearSpinner:LiveData<String>get()=_selectedYearSpinner
    private val _selectedMonthSpinner=MutableLiveData<String>()
    val selectedMonthSpinner:LiveData<String>get()=_selectedMonthSpinner

    var ecId = MutableLiveData<Int?>(null)

   // val _expenseSum=MutableLiveData<String>("Rp.")
    //val expenseSum: LiveData<String> get() = _expenseSum

    val expenseSum = Transformations.map(allExpensesFromDB){items->
        val total = items.sumOf { it.expense_ammount?:0 }
        formatRupiah(total.toDouble())

    }
    private val _isNavigateToPurchase=MutableLiveData<Int?>(null)
    val isNavigateToPurchase:LiveData<Int?> get()=_isNavigateToPurchase


    val isAddExpense=MutableLiveData<Boolean>(true)

   ////////////////////////////////////Expenses/////////////////////////////////////////////////
   fun filterProduct(query: String?) {
       val list = mutableListOf<DiscountAdapterModel>()
       if(!query.isNullOrEmpty()) {
           list.addAll(_allExpenseFromDb.value!!.filter {
               it.expense_name?.lowercase(Locale.getDefault())!!.contains(query.toString().lowercase(Locale.getDefault()))})
       } else {
           list.addAll(_allExpenseFromDb.value!!)
       }
       _allExpenseFromDb.value =list
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

    private fun formatDate(date: Date?): String? {
        if (date != null) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return dateFormat.format(date)
        }
        return null
    }
    private fun constructMonthDate(isStart: Boolean): String? {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, if (isStart) 1 else calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return formatDate(calendar.time)
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
    //filter data from database by date
    private suspend fun performDataFiltering(startDate: String?, endDate: String?) {



        Log.i(tagg,"$startDate $endDate")
        val filteredData = withContext(Dispatchers.IO) { expenseDao.getAllExpense(startDate,endDate,ecId.value)}
        val sum = withContext(Dispatchers.IO){ expenseDao.getExpenseSum(startDate,endDate,ecId.value)}
        Log.i(tagg, "filteredData $filteredData")
            _allExpenseFromDb.value = filteredData
       /// _expenseSum.value=formatRupiah(sum.toDouble())?:"-1"
            //_unFilteredrecyclerViewData.value = filteredData

    }

    @RequiresApi(Build.VERSION_CODES.O)
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
    fun deleteExpense(model: DiscountAdapterModel){
        viewModelScope.launch {
            val id=model.id
            deleteExpensesToDao(id!!)
            updateRv4()
        }
    }
    fun getAllExpense(){}
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
    private suspend fun getCEIdByName(name: String):Int?{
        return withContext(Dispatchers.IO){
           expenseCategoryDao.getECIdByName(name)
        }
    }
    

 ////////////////////////////////////////////Customer////////////////////////////////////////////
    fun insertCustomer(name:String?,businessName:String,location:String?,address:String?,level:String?,tag1:String?){
        viewModelScope.launch {
            val customerTable=populateCustomer(null,name, businessName, location, address, level, tag1)
            insertCustomerToDB(customerTable)
        }
    }
    fun updateCustomer(id:Int?,name:String?,businessName:String,location:String?,address:String?,level:String?,tag1:String?){
        viewModelScope.launch {
            val customerTable=populateCustomer(id, name, businessName, location, address, level, tag1)
            Log.i("CUSTPROBS","$customerTable")
            udpateCustomerToDB(customerTable)
        }
    }
    fun populateCustomer(id:Int?,name:String?,businessName:String,location:String?,address:String?,level:String?,tag1:String?): CustomerTable {
        val customerTable = CustomerTable()
        if (id!=null) customerTable.custId=id
        customerTable.customerName=name ?: ""
        customerTable.customerBussinessName=businessName
        customerTable.customerLocation=location
        customerTable.customerAddress=address ?:""
        customerTable.customerTag1=tag1
        return customerTable
    }

//////////////////////////////////Discount////////////////////////////////////////////////////////
    fun insertDiscount(value:Double,name:String,minQty:Double?,tipe:String,location:String){
        viewModelScope.launch {
            val discountTable=populateDiscount(null,value,name, minQty, tipe, location)
            insertDiscountToDB(discountTable)
            Log.i("Disc","$allDiscountFromDB")
        }
    }
    fun updateDiscount(id:Int,value:Double,name:String,minQty:Double?,tipe:String,location:String){
        viewModelScope.launch {
            val discountTable=populateDiscount(id,value,name, minQty, tipe, location)
            updateDiscountFromDB(discountTable)
        }
    }
    fun populateDiscount(id:Int?,value:Double,name:String,minQty:Double?,tipe:String,location:String): DiscountTable {
        val discountTable= DiscountTable()
        //discountTable.discountId=getautoIncrementId()
        if (id!=null) discountTable.discountId=id
        discountTable.discountValue = value
        discountTable.discountName=name
        discountTable.minimumQty=minQty
        discountTable.discountType=tipe
        discountTable.custLocation= if(location.isNotEmpty()) location else null
        return discountTable
    }
    fun deleteDiscountTable(id:Int){viewModelScope.launch {
        deleteDiscountFromDB(id) }
    }
    fun deleteCustomerTable(customerTable: CustomerTable){viewModelScope.launch {
        deleteCustomerToDB(customerTable) }
    }
    private suspend fun insertDiscountToDB(discountTable: DiscountTable){
        withContext(Dispatchers.IO){
            discountDao.insert(discountTable)
        }
    }
    private suspend fun deleteDiscountFromDB(id:Int){
        withContext(Dispatchers.IO){
            discountDao.delete(id)
        }
    }
    private suspend fun updateDiscountFromDB(discountTable: DiscountTable){
        withContext(Dispatchers.IO){
            discountDao.update(discountTable)
        }
    }
    private suspend fun insertCustomerToDB(customerTable: CustomerTable){
        withContext(Dispatchers.IO){
            customerDao.insert(customerTable)
        }
    }
    private suspend fun udpateCustomerToDB(customerTable: CustomerTable){
        withContext(Dispatchers.IO){
            customerDao.update(customerTable)
        }
    }
    private suspend fun deleteCustomerToDB(customerTable: CustomerTable){
        withContext(Dispatchers.IO){
            customerDao.delete(customerTable)
        }
    }
    private suspend fun insertExpense(expenses: Expenses){
        withContext(Dispatchers.IO){
            expenseDao.insert(expenses)
        }
    }
    private suspend fun updateExpenseToDao(expenses: Expenses){
        withContext(Dispatchers.IO){
            expenseDao.update(expenses)
        }
    }
    private suspend fun deleteExpensesToDao(id:Int){
        withContext(Dispatchers.IO){
           expenseDao.delete(id)
        }
    }
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

    private suspend fun getECIdByName(name:String):Int?{
        return withContext(Dispatchers.IO){
            expenseCategoryDao.getECIdByName(name)
        }
    }
    private suspend fun deleteExpensesCategoryToDao(id:Int){
        withContext(Dispatchers.IO){
            expenseCategoryDao.delete(id)
        }
    }

    fun onNavigateToPurcase(id:Int){
        _isNavigateToPurchase.value=id
    }
    fun onNavigatedToPurchase(){
        _isNavigateToPurchase.value=null
    }

}