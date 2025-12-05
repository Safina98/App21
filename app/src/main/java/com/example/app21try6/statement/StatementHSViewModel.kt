package com.example.app21try6.statement

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    var id = 0



    val isAddExpense=MutableLiveData<Boolean>(true)

   ////////////////////////////////////Expenses/////////////////////////////////////////////////

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

    //filter data from database by date

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
        if (id==null) customerTable.customerCloudId=System.currentTimeMillis()
        customerTable.needsSyncs= 1
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
        if (id==null) discountTable.discountCloudId=System.currentTimeMillis()
        discountTable.needsSyncs=1
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
    //moved
    private suspend fun updateExpenseToDao(expenses: Expenses){
        withContext(Dispatchers.IO){
            expenseDao.update(expenses)
        }
    }

}