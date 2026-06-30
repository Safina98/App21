package com.example.app21try6.utils



import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app21try6.Constants.DETAILED_DATE_FORMATTER
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.models.WorkerModel
import java.time.format.DateTimeFormatter

class UpdateProductCapitalWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
           performUpdate()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
    private suspend fun performUpdate() {
        val database = VendibleDatabase.getInstance(applicationContext)
        val transactionDetailDao = database.transDetailDao
        val productDao = database.productDao

        try {

            var model: WorkerModel
            var id: Long

            val list=mutableListOf<WorkerModel>()
            //Accura
            id=productDao.getProductIdByProductName("ACCURA")
            model= WorkerModel().apply {
                productId=id
                productCapital=89500
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //Alonzo
            id=productDao.getProductIdByProductName("ALONZO")
           model= WorkerModel().apply {
                productId=id
                productCapital=79000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //AGGERA
            id=productDao.getProductIdByProductName("AGGERA")
            model= WorkerModel().apply {
                productId=id
                productCapital=51000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //Accura Karbon
            id =productDao.getProductIdByProductName("ACCURA KARBON")
            model= WorkerModel().apply {
                productId=id
                productCapital=89500
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //BENTLEY
            id =productDao.getProductIdByProductName("BENTLEY")
            model= WorkerModel().apply {
                productId=id
                productCapital=27000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //CLAZZIO
            id =productDao.getProductIdByProductName("CARVIERRO CLAZZIO")
            model= WorkerModel().apply {
                productId=id
                productCapital=64000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //CARIVERRO UNO
            id =productDao.getProductIdByProductName("ACCURA KARBON")
            model= WorkerModel().apply {
                productId=id
                productCapital=64000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //LIZIO
            id =productDao.getProductIdByProductName("LIZIO")
            model= WorkerModel().apply {
                productId=id
                productCapital=167000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //SALUTE
            id =productDao.getProductIdByProductName("SALUTE")
            model= WorkerModel().apply {
                productId=id
                productCapital=52500
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //XFORCE
            id =productDao.getProductIdByProductName("XFORCE")
            model= WorkerModel().apply {
                productId=id
                productCapital=26000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)



            list.forEach {
                Log.i("UpdateProductCapitalWorker","${it.fromDate}, ${it.productCapital}, ${it.productId}")
                transactionDetailDao.updateProductCapitalBeforeDate(it.fromDate, it.productCapital, it.productId)
            }
            val brandList=transactionDetailDao.getTransactionDetailsByBrandName("ACCURA KARBON")
            brandList.forEach {
                Log.i("UpdateProductCapitalWorker","${it.trans_item_name}, ${it.product_capital}, ${it.trans_detail_date}")
            }

            //transactionDetailDao.updateProductCapitalBeforeDate(targetDate, newCapitalValue,productId)
        }catch (e: Exception){

        }
    }

}