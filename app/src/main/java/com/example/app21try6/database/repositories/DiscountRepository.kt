package com.example.app21try6.database.repositories

import androidx.lifecycle.LiveData
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.DiscountTransDao
import com.example.app21try6.database.daos.PaymentDao
import com.example.app21try6.database.models.PaymentModel
import com.example.app21try6.database.tables.DiscountTransaction
import com.example.app21try6.database.tables.Payment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiscountRepository(
    private val discountTransDao: DiscountTransDao,
    private val discountDao: DiscountDao,
    private val paymentDao: PaymentDao
) {
    fun getAllDicountName(): LiveData<List<String>> {
        return discountDao.getAllDiscountName()
    }

    suspend fun getDiscountNameById(id:Int?):String?{
        return withContext(Dispatchers.IO){discountDao.getDiscountNameById(id)}
    }
    suspend fun getDiscountIdByName(discName:String):Int?{
        return withContext(Dispatchers.IO){
            discountDao.getDiscountIdByName(discName)
        }
    }
    //.................................DiscountTrans/................................................
    fun getTransactionTotalDiscounts(id:Int): LiveData<Double> {
        return discountTransDao.getTotalDiscountBySumId(id)
    }
    fun getTransactionDiscounts(id:Int): LiveData<List<PaymentModel>> {
        return discountTransDao.selectDiscAsPaymentModel(id)
    }
    suspend fun deleteTransactionDiscount(id: Int){
        withContext(Dispatchers.IO){
            discountTransDao.delete(id)
        }
    }
    suspend fun insertTransactionDiscount(discount:DiscountTransaction){
        withContext(Dispatchers.IO){
            discountTransDao.insert(discount)
        }
    }
   suspend fun updateTransactionDiscount(id:Int,ammount:Double){
        withContext(Dispatchers.IO){
            discountTransDao.updateById(id,ammount)
        }
    }
    suspend fun getDiscountTransactionList(id:Int):List<DiscountTransaction>{
        return withContext(Dispatchers.IO) { discountTransDao.getDiscountListBySumId(id) }
    }
    //..................................Payment....................................................../
    fun getTransactionPayments(id:Int):LiveData<List<PaymentModel>>{
        return paymentDao.selectPaymentModelBySumId(id)
    }
    suspend fun getTransactionTotalPayment(id:Int):Int{
        return  withContext(Dispatchers.IO){
            paymentDao.selectSumFragmentBySumId(id)
        }
    }

    suspend fun updatePayment(payment: Payment) {
        withContext(Dispatchers.IO) {
            paymentDao.update(payment)
        }
    }
    suspend fun deletePayment(id:Int){
        withContext(Dispatchers.IO){
            paymentDao.deletePayment(id)
        }
    }
    suspend fun insertPayment(payment: Payment){
        withContext(Dispatchers.IO){
            paymentDao.insert(payment)
        }
    }

}