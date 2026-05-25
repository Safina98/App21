package com.example.app21try6.statement

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.database.tables.DiscountTable
import com.example.app21try6.database.repositories.DiscountRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
class StatementHSViewModel(application: Application,
                           val discountRepo: DiscountRepository

    ):AndroidViewModel(application) {

    val allDiscountFromDB= discountRepo.getAllDiscount()

    val allCustomerFromDb=discountRepo.getAllCustomers()

    val allCustomer=discountRepo.getAllCustomersFlow()

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
            discountRepo.insertCustomerToDB(customerTable)


        }
    }
    fun updateCustomer(id:Int?,name:String?,businessName:String,location:String?,address:String?,level:String?,tag1:String?){
        viewModelScope.launch {
            val customerTable=populateCustomer(id, name, businessName, location, address, level, tag1)
            discountRepo.updateCustomerToDB(customerTable)

        }
    }
    fun updateCustomer(customerTable: CustomerTable){
        viewModelScope.launch {
            discountRepo.updateCustomerToDB(customerTable)

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
            discountRepo.insertDiscount(discountTable)

        }
    }
    fun updateDiscount(id:Int,value:Double,name:String,minQty:Double?,tipe:String,location:String){
        viewModelScope.launch {
            val discountTable=populateDiscount(id,value,name, minQty, tipe, location)
           discountRepo.updateDiscountFromDB(discountTable)
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
        discountRepo.deleteDiscountFromDB(id) }
    }
    fun deleteCustomerTable(customerTable: CustomerTable){viewModelScope.launch {
        discountRepo.deleteCustomerToDB(customerTable) }
    }

}