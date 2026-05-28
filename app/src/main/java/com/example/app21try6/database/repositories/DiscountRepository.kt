package com.example.app21try6.database.repositories

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.models.PaymentModel
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.database.tables.DiscountTable
import com.example.app21try6.database.tables.DiscountTransaction
import com.example.app21try6.database.tables.Payment
import com.example.app21try6.statement.DiscountAdapterModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class DiscountRepository(
    application: Application
) {

    private val customerDao=VendibleDatabase.getInstance(application).customerDao
    private val discountTransDao=VendibleDatabase.getInstance(application).discountTransDao
    private val discountDao=VendibleDatabase.getInstance(application).discountDao
    private val paymentDao=VendibleDatabase.getInstance(application).paymentDao

    suspend fun insertDiscount(discountTable: DiscountTable){
        withContext(Dispatchers.IO){
            discountDao.insert(discountTable)
        }
    }
    suspend fun deleteDiscountFromDB(id:Int){
        withContext(Dispatchers.IO){
            discountDao.delete(id)
        }
    }

    suspend fun updateDiscountFromDB(discountTable: DiscountTable){
        withContext(Dispatchers.IO){
            discountDao.update(discountTable)
        }
    }
    fun getAllDicountName(): LiveData<List<String>> {
        return discountDao.getAllDiscountName()
    }
    suspend fun getAllDicountNameList(): List<DiscountTable> {
        return withContext(Dispatchers.IO){ discountDao.getAllDiscountList()}
    }

    fun getAllDiscount(): LiveData<List<DiscountAdapterModel>> { return discountDao.getAllDiscount()}

    suspend fun getDiscountNameById(id:Int?):String?{
        return withContext(Dispatchers.IO){discountDao.getDiscountNameById(id)}
    }
    suspend fun getDiscountIdByName(discName:String):Int?{
        return withContext(Dispatchers.IO){
            discountDao.getDiscountIdByName(discName)
        }
    }
    suspend fun assignCloudIdToDiscountTable(cloudId: Long, id: Int){
        withContext(Dispatchers.IO){
            discountDao.assignDiscountCloudID(cloudId, id)
        }
    }

    suspend fun getAllDiscountTable(): List<DiscountTable> {
        return withContext(Dispatchers.IO) {
            discountDao.selectAllDiscountTable()
        }
    }

    //.................................DiscountTrans/................................................
    fun getTransactionTotalDiscounts(id: Long): LiveData<Double> {
        return discountTransDao.getTotalDiscountBySumId(id)
    }
    fun getTransactionDiscounts(id:Long): LiveData<List<PaymentModel>> {
        return discountTransDao.selectDiscAsPaymentModel(id)
    }
    suspend fun deleteTransactionDiscount(id: Int){
        withContext(Dispatchers.IO){
            discountTransDao.delete(id)
        }
    }
    suspend fun selectExistingDiscount(sumId: Long,discName: String):DiscountTransaction?{
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

    suspend fun getDiscountTransactionList(id:Long):List<DiscountTransaction>{
        return withContext(Dispatchers.IO) { discountTransDao.getDiscountListBySumId(id) }
    }

    suspend fun assignCloudIdToDiscountTransactionTable(cloudId: Long, id: Int){
        withContext(Dispatchers.IO){
            discountTransDao.assignDiscountTransactionCloudID(cloudId, id)
        }
    }

    suspend fun getAllDiscountTransactionTable(): List<DiscountTransaction> {
        return withContext(Dispatchers.IO) {
            discountTransDao.selectAllDiscountTransactionTable()
        }
    }

    //..................................Payment....................................................../
    fun getTransactionPayments(id:Long):LiveData<List<PaymentModel>>{
        return paymentDao.selectPaymentModelBySumId(id)
    }
    suspend fun getTransactionTotalPayment(id:Long):Int{
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


    suspend fun assignCloudIdToPaymentTable(cloudId: Long, id: Int){
        withContext(Dispatchers.IO){
            paymentDao.assignPaymentCloudID(cloudId, id)
        }
    }

    suspend fun getAllPaymentTable(): List<Payment> {
        return withContext(Dispatchers.IO) {
            paymentDao.selectAllPaymentTable()
        }
    }


    //..................................CustomerDao.............................................../
    fun getAllCustomers():LiveData<List<CustomerTable>>{
        return customerDao.allCustomer()
    }
    fun getAllCustomersFlow(): Flow<List<CustomerTable>>{
        return customerDao.allCustomerFlow()
    }

    suspend fun getCustomerByName(name:String):CustomerTable?{
        return withContext(Dispatchers.IO){customerDao.getCustomerByName(name)}

    }
    suspend fun getCustomerId(name:String):Int?{
        return withContext(Dispatchers.IO){
            customerDao.getIdByName(name)
        }
    }

    suspend fun assignCloudIdToCustomerTable(cloudId: Long, id: Int){
        withContext(Dispatchers.IO){
            customerDao.assignCustomerCloudID(cloudId, id)
        }
    }

    suspend fun getAllCustomerTable(): List<CustomerTable> {
        return withContext(Dispatchers.IO) {
            customerDao.selectAllCustomerTable()
        }
    }
    suspend fun getAllCustomerNameList(): List<String> {
        return withContext(Dispatchers.IO) {
           val list= customerDao.selectAllCustomerNameList()
            val modifiedList = listOf("ALL") + list // Create a new list with the added value
            modifiedList // Return the modified lis
        }
    }
    suspend fun getCustomersWithDuplicateId(): List<CustomerTable> {
        return withContext(Dispatchers.IO) {
            customerDao.getCustomersWithDuplicateCloudId()
        }
    }

    suspend fun insertCustomerToDB(customerTable: CustomerTable){
        withContext(Dispatchers.IO){
            customerDao.insert(customerTable)
        }
    }
    suspend fun updateCustomerToDB(customerTable: CustomerTable){
        withContext(Dispatchers.IO){
            if (customerTable.custId!=0){
                Log.i("InsertProbs","Update")
                customerDao.update(customerTable)
            }else{
                Log.i("InsertProbs","Insert")
                customerDao.insert(customerTable)
            }

        }
    }
    suspend fun deleteCustomerToDB(customerTable: CustomerTable){
        withContext(Dispatchers.IO){
            customerDao.delete(customerTable)
        }
    }
    suspend fun getCustomerPhoneNumber(id:Int?):String?{
        return withContext(Dispatchers.IO){
            customerDao.getCustomerPhoneNumber(id)
        }

    }

}