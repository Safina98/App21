package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.database.tables.Payment
import com.example.app21try6.database.models.PaymentModel


@Dao
interface PaymentDao{

    @Insert
    fun insert(payment: Payment)
    @Update
    fun update(payment: Payment)

    @Query("DELETE FROM payment_table WHERE id =:id")
    fun deletePayment(id:Int)


    @Query("SELECT SUM(payment_ammount) FROM payment_table WHERE tSCloudId =:sumId")
    fun selectSumFragmentBySumId(sumId:Long):Int

    @Query("""
        UPDATE payment_table
        SET paymentCloudId =:cloudId
        WHERE id=:id
    """)
    fun assignPaymentCloudID(cloudId:Long,id:Int)

    @Query("SELECT * FROM payment_table")
    fun selectAllPaymentTable(): List<Payment>

    @Query("SELECT\n" +
            "    p.id,\n" +
            "    p.tSCloudId,\n" +
            "    p.payment_ammount,\n" +
            "    p.payment_date,\n" +
            "    p.ref,\n" +
            "    t.total_trans,\n" +
            "    'Bayar: ' as name,\n"+
        "    t.paid\n" +
            "FROM\n" +
            "    payment_table p\n" +
            "INNER JOIN\n" +
            "    trans_sum_table t\n" +
            "ON\n" +
            "    p.tSCloudId = t.tSCloudId\n" +
            "WHERE\n" +
            "    p.tSCloudId = :sumId")
    fun selectPaymentModelBySumId(sumId: Long):LiveData<List<PaymentModel>>


}