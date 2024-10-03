package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.transaction.transactiondetail.PaymentModel

@Dao
interface DiscountTransDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(discounts: List<DiscountTransaction>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(discounts: DiscountTransaction)

    @Update
    fun update(discounts: DiscountTransaction)

    @Delete
    fun delete(discounts: DiscountTransaction)

    @Query("DELETE  FROM discout_transaction_table")
    fun deleteAll()

    @Query("SELECT * FROM discout_transaction_table WHERE sum_id=:id")
    fun selectAllDiscTrans(id:Int):LiveData<List<DiscountTransaction>>
    @Query("SELECT * FROM discout_transaction_table WHERE sum_id=:sumId AND discTransName=:name")
    fun selectDiscTransBySumIdAndDiscName(sumId:Int,name:String):DiscountTransaction?
    @Query("SELECT * FROM discout_transaction_table WHERE sum_id=:sumId")
    fun selectDiscTransBySumId(sumId:Int):List<DiscountTransaction>?


    @Query("SELECT\n" +
            "    d.discountId as id,\n" +
            "    d.sum_id as sum_id,\n" +
            "    d.discountAppliedValue as payment_ammount,\n" +
            "    d.discountAppliedValue as paid,\n" +
            "    d.discTransDate as payment_date,\n" +
            "    d.discTransRef as ref,\n" +
            "    d.discTransName as name,\n" +
            "    t.total_trans\n" +
            "FROM\n" +
            "    discout_transaction_table d\n" +
            "INNER JOIN\n" +
            "    trans_sum_table t\n" +
            "ON\n" +
            "    d.sum_id = t.sum_id\n" +
            "WHERE\n" +
            "    d.sum_id = :sumId")
    fun selectDiscAsPaymentModel(sumId:Int):LiveData<List<PaymentModel>>

}