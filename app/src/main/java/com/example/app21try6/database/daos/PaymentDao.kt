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

    @Query("DELETE FROM paymen_table WHERE id =:id")
    fun deletePayment(id:Int)


    @Query("SELECT SUM(payment_ammount) FROM paymen_table WHERE sum_id =:sumId")
    fun selectSumFragmentBySumId(sumId:Int):Int


    @Query("SELECT\n" +
            "    p.id,\n" +
            "    p.sum_id,\n" +
            "    p.payment_ammount,\n" +
            "    p.payment_date,\n" +
            "    p.ref,\n" +
            "    t.total_trans,\n" +
            "    'Bayar: ' as name,\n"+
        "    t.paid\n" +
            "FROM\n" +
            "    paymen_table p\n" +
            "INNER JOIN\n" +
            "    trans_sum_table t\n" +
            "ON\n" +
            "    p.sum_id = t.sum_id\n" +
            "WHERE\n" +
            "    p.sum_id = :sumId")
    fun selectPaymentModelBySumId(sumId:Int):LiveData<List<PaymentModel>>


}