package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.app21try6.database.models.PaymentModel

@Dao
interface PurchaseDiscountDao{

    @Query("SELECT " +
            "id  AS tSCloudId," +
            "expenseId AS id, " +
            "discountName AS name, " +
            "discountValue AS payment_ammount " +
            "FROM " +
            "purchase_discount_table " +
            "WHERE expenseId =:eId")
    fun getPDiscountByExpenseId(eId:Int): LiveData<List<PaymentModel>>
}