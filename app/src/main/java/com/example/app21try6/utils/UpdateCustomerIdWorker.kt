package com.example.app21try6.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app21try6.database.VendibleDatabase
import java.util.Collections

class UpdateCustomerIdWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.i("WorkerProbs", "UpdateCustName starting")
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
        val transDetailDao=database.transDetailDao
        val transSumDao = database.transSumDao
        transDetailDao.updateTransItemName("CITROEN BLACK","AUTOLEDER CITROON BLACK")
        transDetailDao.updateTransItemName("CITROEN SADDLE","AUTOLEDER CITROON SADDLE")
        transDetailDao.updateTransItemName("CITROEN OCEAN BLUE","AUTOLEDER CITROON OCEAN BLUE")
        transDetailDao.updateTransItemName("BUSA 4MM PARIS","BUSA 4MM PRS")
        transDetailDao.updateTransItemName("BURGUNDY -002","BENANG B BURGUNDY -002")
        transDetailDao.updateTransItemName("KAIN PARIS HITAM","KAIN PARIS")
        transDetailDao.updateTransItemName("BUSA 1 CM PARIS","BUSA 1 CM PRS")
        transDetailDao.updateTransItemName("MB 6001","MB 6001 BLACK")
        transDetailDao.updateTransItemName("MB 6027","MB 6027 SCARLET")
        transDetailDao.updateTransItemName("KARPET A IGUANA","KARPET AUTOLEDER IGUANA")
        transDetailDao.updateTransItemName("KARPET A DARK GREY","KARPET AUTOLEDER GREY")
        transDetailDao.updateTransItemName("7010-GOGO BLUE","ZEUS 7010-GOGO BLUE")
        transDetailDao.updateTransItemName("BBENANG K ROLL B EIGI-148","BENANG K ROLL B BEIGI-148")
        transDetailDao.updateTransItemName("9012 - SADDLE","ZEUS 9012 - SADDLE")
        transDetailDao.updateTransItemName("KANCING BERLIAN BESAR","KANCING BERLIAN B")
        transDetailDao.updateTransItemName("KARPET F BLACK","KARPET FORCE BLACK")
        transDetailDao.updateTransItemName("KARPTER F BEIGI","KARPET FORCE BEIGI")
        transDetailDao.updateTransItemName("LL 8901","LL 8901 BLACK")
        transDetailDao.updateTransItemName("LL 8985","LL 8901 PURPLE")
        transDetailDao.updateTransItemName("AUTOLEDER DODGE","AUTOLEDER DODGE BLACK")
        transDetailDao.updateTransItemName("BENANG K ROLL BESAR ROADSTAR/SADDLE-016","BENANG K ROLL BESAR SADDLE-016")
        transDetailDao.updateTransItemName("IONIC BIRU","IONIC BLUE")
        transSumDao.updateCustIdBasedOnCustName()
        transDetailDao.updateSubIdBasedOnItemName()


        Log.i("WorkerProbs", "finnished updating warna")
    }
}