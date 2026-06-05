package com.example.app21try6.statement

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.database.tables.DiscountTable
import com.example.app21try6.database.repositories.DiscountRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    val allCustomer=discountRepo.getAllCustomersFlow()

    var searchQuery = mutableStateOf("")
    val filtered = snapshotFlow { searchQuery.value }
        .flatMapLatest { query ->
            allCustomer.map { list ->
                list.filter { it.customerBussinessName.contains(query, ignoreCase = true) }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    var id = 0
    val isAddExpense=MutableLiveData<Boolean>(true)



 ////////////////////////////////////////////Customer////////////////////////////////////////////


    fun updateCustomer(customerTable: CustomerTable){
        viewModelScope.launch {
            discountRepo.updateCustomerToDB(customerTable)

        }
    }


    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
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