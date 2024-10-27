package com.example.app21try6.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app21try6.database.TransactionSummary
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.formatRupiah

class UpdateSummaryProductIdWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) { override suspend fun doWork(): Result {
    Log.i("WorkerProbs", "UpdateSummaryProductId starting")
    return try {
        performUpdate() // Directly call the suspend function
        Log.i("WorkerProbs", "Finnised")
        Result.success()

    } catch (e: Exception) {
        e.printStackTrace()
        Result.retry()
    }
}
    private suspend fun performUpdate() {
        val database = VendibleDatabase.getInstance(applicationContext)

        val summaryDao = database.summaryDbDao
        val transDetailDao = database.transDetailDao
        val productDao=database.productDao
        val list =transDetailDao.getMonthlyProfit()
        Log.i("WorkerProbs","${list.size}")

        list.forEach {
           Log.i("WorkerProbs","${it.trans_detail_date}\n,price ${formatRupiah(it.trans_price.toDouble())} " +
                   "qty: ${it.qty}; unit:${it.unit}; unitqty ${it.unit_qty} total_price ${it.total_price}")
           // Log.i("WorkerProbs","${it.month} ${formatRupiah(it.monthly_profit)}")
        }



        /*
        productDao.updateProductCapital(5500,54)
        productDao.updateProductCapital(2500,55)
        productDao.updateProductCapital(2500,56)
        productDao.updateProductCapital(14000,58)
        productDao.updateProductCapital(13000,59)
        productDao.updateProductCapital(125000,60)
        productDao.updateProductCapital(33000,68)
        productDao.updateProductCapital(43000,69)
        productDao.updateProductCapital(63000,70)
        productDao.updateProductCapital(90000,71)
        productDao.updateProductCapital(109000,72)
        productDao.updateProductCapital(26000,90)
        productDao.updateProductCapital(85000,98)
        productDao.updateProductCapital(170000,100)
        productDao.updateProductCapital(135000,101)
        productDao.updateProductCapital(21000,102)


        summaryDao.updateSummaryItemName("BENANG C","BENANG COATS")
        summaryDao.updateSummaryItemName("BUSA 1CM PARIS","BUSA 1CM PRS")
        summaryDao.updateSummaryItemName("BUSA 4 MM PRS","BUSA 4MM PRS")
        summaryDao.updateSummaryItemName("BUSA SUPER 1 CM KLB","BUSA SUPER 1CM KLB")
        summaryDao.updateSummaryItemName("BUSA SUPER 1CM PRS ABU","BUSA SUPER 1CM PRS")
        summaryDao.updateSummaryItemName("PARIS","KAIN PARIS")
        summaryDao.updateSummaryItemName("BUSA 8 MM KLB","BUSA 8MM KLB")
        summaryDao.updateSummaryItemName("BUSA SUPER 8MM","BUSA SUPER 8MM KLB")
        summaryDao.updateSummaryItemName("BUSA SUPER 1CM PRS HIJAU","BUSA SUPER PRS HIJAU")
        summaryDao.updateSummaryItemName("KARPET AUTOLEDER GELAP","KARPET AUTOLEDER")
        summaryDao.updateSummaryItemName("BUSA LEMBARAN 3 CM","BUSA LEMBARAN 3CM")
        summaryDao.updateSummaryItemName("PEREKAT BESAR","PEREKAT B")
        summaryDao.updateSummaryItemName(" TALI KUR PUTIH","TALI KUR PUTIH")
        summaryDao.updateSummaryItemName("MINYAK MESIN ","MINYAK MESIN")
        summaryDao.updateSummaryItemName("BUSA LEMBARAN 5 CM","BUSA LEMBARAN 5CM")
        summaryDao.updateSummaryItemName("BUSA LEMBARAN 5 CM","BUSA LEMBARAN HIJAU 5CM")
        summaryDao.updateSummaryItemName("AUTOLEDER HAMMER","AUTOLEDER HUMMER")
        summaryDao.updateSummaryItemName("BUSA 8 MM PARIS","BUSA 8MM PRS")
        summaryDao.updateSummaryItemName("BUSA 8 MM PARIS","BUSA 8MM PRS")
        summaryDao.updateSummaryItemName("BUSA 1 CM KLB","BUSA 1CM KLB")
        summaryDao.updateSummaryItemName("ACCURA LIZIO","LIZIO")
        summaryDao.updateSummaryItemName("BUSA 4MM PARIS","BUSA 4MM PRS")
        summaryDao.updateSummaryItemName("BUSA SUPER PRS","BUSA SUPER 1CM PRS")
        summaryDao.updateSummaryItemName("BUSA SUPER KLB","BUSA SUPER 1CM KLB")
        summaryDao.updateSummaryItemName("BUSA 8MM PARIS","BUSA 8MM PRS")
        summaryDao.updateSummaryItemName("KARET TOPI ","KARET TOPI 2MM")
        summaryDao.updateSummaryItemName("KAIN JOK MOTOR","KAIN JOK MOTOR EXCELENT")
        summaryDao.updateSummaryItemName("BUSA LEMBARAN 1CM","BUSA LEMBARAN 1CM KUNING")
        summaryDao.updateSummaryItemName("JOK MOTOR ZEUS","JOK MOTOR ZEUS PRESS")
        summaryDao.updateSummaryItemName("TANG RING C","TANG RING C MERAH")
        summaryDao.updateSummaryItemName("BUSA SUPER PARIS TIPIS","BUSA SUPER 1CM PRS TIPIS")
        summaryDao.updateSummaryItemName("IMITASI KULIT","KAIN JOK MOTOR EXCELENT")
        summaryDao.updateSummaryItemName("BUSA 1 CM PARIS","BUSA 1CM PRS")
        summaryDao.updateSummaryItemName("KAIN JOK MOTOR VARIO","KAIN JOK MOTOR REVO")
        summaryDao.updateSummaryItemName("BUSA 5 MM KLB","BUSA 5MM KLB")
        summaryDao.updateSummaryItemName("KARPET AUTOLEDER TERANG","KARPET AUTOLEDER")
        summaryDao.updateSummaryItemName("BUSA 4MM  KLB","BUSA 4MM KLB")
        summaryDao.updateSummaryItemName("BUSA LEMBARAN 1CM","BUSA LEMBARAN 1CM KUNING")
        summaryDao.updateSummaryItemName("AUTOLEDER BINTIK","AUTOLEDER DODGE")
        summaryDao.updateSummaryItemName("BUSA LEMBARAN 2 CM","BUSA LEMBARAN 2CM")
        summaryDao.updateSummaryItemName("BUSA LEMBARAN 1 CM","BUSA LEMBARAN 1CM KUNING")
        summaryDao.updateSummaryItemName("PEREKAT KECIL","PEREKAT K")
        summaryDao.updateSummaryItemName("MINYAK  MESIN","MINYAK MESIN")
        summaryDao.updateSummaryItemName("EURROLEDER","EUROLEDER")

 */
       // summaryDao.updateSummaryProductId()
        //summaryDao.updateSummaryProductCapital()

        //transDetailDao.updateTransDetailProductCapital()
        
// Log the size of duplicates

    }

}