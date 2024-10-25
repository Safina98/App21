package com.example.app21try6.statement

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.CustomerDao
import com.example.app21try6.database.CustomerTable
import com.example.app21try6.database.DiscountDao
import com.example.app21try6.database.DiscountTable
import com.example.app21try6.database.ExpenseCategory
import com.example.app21try6.database.ExpenseCategoryDao
import com.example.app21try6.database.ExpenseDao
import com.example.app21try6.database.Expenses
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

class StatementHSViewModel(application: Application,
    val discountDao: DiscountDao,
    val customerDao: CustomerDao,
    val expenseDao: ExpenseDao,
    val expenseCategoryDao: ExpenseCategoryDao
    ):AndroidViewModel(application) {

    val allDiscountFromDB= discountDao.getAllDiscount()
    val allCustomerFromDb=customerDao.allCustomer()
    val allExpenseCategory=expenseCategoryDao.getAllExpenseCategory()
    val allExpensesFromDB=expenseDao.getAllExpense()
    var id = 0
    val expenseCategoryName= MutableLiveData<String?>("")


   ////////////////////////////////////Expenses/////////////////////////////////////////////////
   fun getExpenseCategoryNameById(id:Int?){
       viewModelScope.launch {
           val expenseCatName = withContext(Dispatchers.IO){expenseCategoryDao.getExpenseCategoryNameById(id)}
           expenseCategoryName.value=expenseCatName
       }
   }
    fun insertExpenseCategory(categoryName:String){
        viewModelScope.launch {
            val expenseCategory=ExpenseCategory()
            expenseCategory.expense_category_name=categoryName
            insertExpensesCategory(expenseCategory)
        }
    }
    fun updateExpenseCategory(expenseCategory: ExpenseCategory){
        viewModelScope.launch {
            updateExpensesCategory(expenseCategory)
        }
    }
    fun deleteExpenseCategory(){
        viewModelScope.launch{}
    }
    fun getAllexpenseCategory(){
        viewModelScope.launch {

        }
    }

    fun insertExpense(expenseNamed:String, expenseAmmount:Int?, expenseDate: Date, expenseCatName:String){
        viewModelScope.launch{
            val expenses=Expenses()
            val catId =getECIdByName(expenseCatName)
            expenses.expense_name=expenseNamed
            expenses.expense_ammount=expenseAmmount
            expenses.expense_date=expenseDate
            expenses.expense_ref=UUID.randomUUID().toString()
            expenses.expense_category_id=catId?:0
            insertExpense(expenses)
        }
    }
    fun updateExpenses(expenses: Expenses){
        viewModelScope.launch {
            updateExpenseToDao(expenses)
        }
    }
    fun deleteExpense(){
    }
    fun getAllExpense(){}


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
    fun populateCustomer(id:Int?,name:String?,businessName:String,location:String?,address:String?,level:String?,tag1:String?):CustomerTable{
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
    fun populateDiscount(id:Int?,value:Double,name:String,minQty:Double?,tipe:String,location:String):DiscountTable{
        val discountTable=DiscountTable()
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


}