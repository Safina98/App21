package com.example.app21try6.statement

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.app21try6.database.DiscountTable

class StatementHSViewModel(application: Application):AndroidViewModel(application) {
    val dummyDiscList= mutableListOf<DiscountTable>()
    var id = 0

    fun getautoIncrementId():Int{
        id = id+1
        return id
    }

    fun insertDiscount(value:Double,name:String,minQty:Double?,tipe:String){
        val discountTable=DiscountTable()
        discountTable.discountId=getautoIncrementId()
        discountTable.discountValue = value
        discountTable.discountName=name
        discountTable.minimumQty=minQty
        discountTable.discountType=tipe
        dummyDiscList.add(discountTable)
        Log.i("Disc","$dummyDiscList")
    }
}