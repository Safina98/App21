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
    fun insert(discounts: DiscountTransaction)

    @Update
    fun update(discounts: DiscountTransaction)

    @Query("DELETE  FROM discout_transaction_table WHERE discTransId=:id")
    fun delete(id:Int)

    @Query("UPDATE discout_transaction_table\n" +
            "SET discountAppliedValue = :newValue, needs_syncs = 1 \n" +
            "WHERE discTransId = :id")
    fun updateById(id: Int, newValue: Double)

    @Query("SELECT * FROM discout_transaction_table WHERE tSCloudId=:sumId AND discTransName=:name")
    fun selectDiscTransBySumIdAndDiscName(sumId:Long,name:String): DiscountTransaction?

    @Query("SELECT\n" +
            "    d.discTransId as id,\n" +
            "    d.tSCloudId,\n" +
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
            "    d.tSCloudId = t.tSCloudId\n" +
            "LEFT JOIN\n" +
            "    discount_table dt ON d.discountId = dt.discountId\n" +
            "WHERE\n" +
            "    d.tSCloudId= :sumId")
    fun selectDiscAsPaymentModel(sumId:Long):LiveData<List<PaymentModel>>

    @Query("SELECT * FROM discout_transaction_table WHERE tSCloudId =:sumId")
    fun getDiscountListBySumId(sumId: Long):List<DiscountTransaction>

    @Query("SELECT SUM(d.discountAppliedValue) FROM discout_transaction_table d WHERE d.tSCloudId = :sumId")
    fun getTotalDiscountBySumId(sumId: Long): LiveData<Double>
    @Query("""
        UPDATE discout_transaction_table
        SET dTCloudId =:cloudId
        WHERE discountId=:id
    """)
    fun assignDiscountTransactionCloudID(cloudId:Long,id:Int)
    @Query("SELECT * FROM discout_transaction_table")
    fun selectAllDiscountTransactionTable(): List<DiscountTransaction>

}