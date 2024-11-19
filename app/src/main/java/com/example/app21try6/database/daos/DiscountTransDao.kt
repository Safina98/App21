package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.database.tables.DiscountTransaction
import com.example.app21try6.database.models.PaymentModel

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

    @Query("DELETE  FROM discout_transaction_table WHERE discTransId=:id")
    fun delete(id:Int)
    @Query("UPDATE discout_transaction_table SET discountAppliedValue = :newValue WHERE discTransId = :id")
    fun updateById(id: Int, newValue: Double)
    @Query("DELETE  FROM discout_transaction_table")
    fun deleteAll()

    @Query("SELECT * FROM discout_transaction_table WHERE sum_id=:id")
    fun selectAllDiscTrans(id:Int):LiveData<List<DiscountTransaction>>
    @Query("SELECT * FROM discout_transaction_table WHERE sum_id=:sumId AND discTransName=:name")
    fun selectDiscTransBySumIdAndDiscName(sumId:Int,name:String): DiscountTransaction?
    @Query("SELECT * FROM discout_transaction_table WHERE sum_id=:sumId")
    fun selectDiscTransBySumId(sumId:Int):List<DiscountTransaction>?


    @Query("SELECT\n" +
            "    d.discTransId as id,\n" +
            "    d.sum_id as sum_id,\n" +
            "    d.discountAppliedValue as payment_ammount,\n" +
            "    d.discountAppliedValue as paid,\n" +
            "    d.discTransDate as payment_date,\n" +
            "    d.discTransRef as ref,\n" +
            "    d.discTransName as name,\n" +
            "    dt.discountType as discountType,\n" +
            "    t.total_trans\n" +
            "FROM\n" +
            "    discout_transaction_table d\n" +
            "INNER JOIN\n" +
            "    trans_sum_table t\n" +
            "ON\n" +
            "    d.sum_id = t.sum_id\n" +
            "LEFT JOIN\n" +
            "    discount_table dt ON d.discountId = dt.discountId\n" +
            "WHERE\n" +
            "    d.sum_id = :sumId")
    fun selectDiscAsPaymentModel(sumId:Int):LiveData<List<PaymentModel>>

    @Query("SELECT SUM(d.discountAppliedValue) FROM discout_transaction_table d WHERE d.sum_id = :sumId")
    fun getTotalDiscountBySumId(sumId: Int): LiveData<Double>

}