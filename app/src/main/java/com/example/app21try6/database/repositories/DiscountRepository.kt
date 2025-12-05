package com.example.app21try6.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.DiscountTransDao
import com.example.app21try6.database.daos.PaymentDao
import com.example.app21try6.database.models.PaymentModel
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.database.tables.DiscountTable
import com.example.app21try6.database.tables.DiscountTransaction
import com.example.app21try6.database.tables.Payment
import com.example.app21try6.utils.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiscountRepository(
    application: Application
) {

    private val customerDao=VendibleDatabase.getInstance(application).customerDao
    private val discountTransDao=VendibleDatabase.getInstance(application).discountTransDao
    private val discountDao=VendibleDatabase.getInstance(application).discountDao
    private val paymentDao=VendibleDatabase.getInstance(application).paymentDao

    fun getAllDicountName(): LiveData<List<String>> {
        return discountDao.getAllDiscountName()
    }
    suspend fun getAllDicountNameList(): List<DiscountTable> {
        return withContext(Dispatchers.IO){ discountDao.getAllDiscountList()}
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
    suspend fun selectExistingDiscount(sumId: Int,discName: String):DiscountTransaction?{
        return withContext(Dispatchers.IO){discountTransDao.selectDiscTransBySumIdAndDiscName(sumId ,discName)}
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


    //..................................CustomerDao.............................................../
    fun getAllCustomers():LiveData<List<CustomerTable>>{
        return customerDao.allCustomer()
    }

    suspend fun getCustomerByName(name:String):CustomerTable?{
        return withContext(Dispatchers.IO){customerDao.getCustomerByName(name)}

    }
    suspend fun getCustomerId(name:String):Int?{
        return withContext(Dispatchers.IO){
            customerDao.getIdByName(name)
        }
    }

}