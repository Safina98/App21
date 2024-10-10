package com.example.app21try6.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app21try6.database.CustomerTable
import com.example.app21try6.database.VendibleDatabase
import java.util.Collections

class UpdateCustomerIdWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    val custList= listOf<CustomerTable>(
        CustomerTable(0,"Ajat,","Asia Jok","Makassar & Sekitarnya","Barombong"),
        CustomerTable(1,"","Alyka Jok","Makassar & Sekitarnya","Kandea"),
        CustomerTable(2,"","Auto 354","Makassar & Sekitarnya","Gowa"),
        CustomerTable(3,"Wahyu","AMV","Daerah","Soppeng"),
        CustomerTable(4,"Akbar","Toko AM Variasi","Daerah","Sengkang"),
        CustomerTable(5,"Asep Ramlan","Asep Ramlan","Makassar & Sekitarnya",""),
        CustomerTable(6,"","Anugrah Mebel","Daerah","Bantaeng"),
        CustomerTable(7,"","AT Jok","Makassar & Sekitarnya",""),
        CustomerTable(8,"","New Bandung Jok","Makassar & Sekitarnya",""),
        CustomerTable(9,"","Bandung Jok gowa","Makassar & Sekitarnya",""),
        CustomerTable(10,"","Bagus Jok","Daerah","Jl Mekongga Indah Baypass kolut"),
        CustomerTable(11,"","Beo","Makassar & Sekitarnya",""),
        CustomerTable(12,"","Berkah Variasi","Makassar & Sekitarnya",""),
        CustomerTable(13,"","Cahaya Variasi","Makassar & Sekitarnya",""),
        CustomerTable(14,"","Dr Jok","Makassar & Sekitarnya",""),
        CustomerTable(15,"Heri","Dyna Jok","Makassar & Sekitarnya","Jl. Sunu"),
        CustomerTable(16,"","D'Fun Variasi","Daerah","Kendari"),
        CustomerTable(17,"","Densus 99","Makassar & Sekitarnya",""),
        CustomerTable(18,"Bu Titi","Eka Jok","Makassar & Sekitarnya","Jl Sungai Limboto",null,"BON"),
        CustomerTable(19,"","Evolution","Makassar & Sekitarnya",""),
        CustomerTable(20,"","Fiesta Jok","Makassar & Sekitarnya","Jl Sungai Limboto",null,"BON"),
        CustomerTable(21,"","Fakhri Jok","Daerah","Sidrap"),
        CustomerTable(22,"","Green Design","Makassar & Sekitarnya",""),
        CustomerTable(23,"","HSR Auto","Makassar & Sekitarnya","",null,"BON"),
        CustomerTable(24,"","HARAJUKU","Makassar & Sekitarnya","",null,"BON"),
        CustomerTable(25,"","Jabbal","Makassar & Sekitarnya",""),
        CustomerTable(26,"","Jok 88","Makassar & Sekitarnya","",null,"BON"),
        CustomerTable(27,"","King Variasi","Makassar & Sekitarnya",""),
        CustomerTable(28,"","Karya Jok","Makassar & Sekitarnya",""),
        CustomerTable(29,"","Kubis Mebel","Makassar & Sekitarnya",""),
        CustomerTable(30,"","Makassar Variasi","Makassar & Sekitarnya","Jl Kumala"),
        CustomerTable(31,"","Mega Buana","Makassar & Sekitarnya","Dayak"),
        CustomerTable(32,"","Mas Tono","Makassar & Sekitarnya",""),
        CustomerTable(33,"","Laquna","Makassar & Sekitarnya","Hertasning"),
        CustomerTable(34,"","Pak Maliang","Makassar & Sekitarnya",""),
        CustomerTable(35,"","Pak Ilham Gowa","Makassar & Sekitarnya","gowa"),
        CustomerTable(36,"","Pak Ramli","Daerah","Sidrap"),
        CustomerTable(37,"","Pak Ibet","Makassar & Sekitarnya",""),
        CustomerTable(38,"","Pak Agus Saputra","Makassar & Sekitarnya",""),
        CustomerTable(39,"","Pattalassang Variasi","Makassar & Sekitarnya",""),
        CustomerTable(40,"","Prima Leather","Makassar & Sekitarnya",""),
        CustomerTable(41,"","Pak Alim","Makassar & Sekitarnya",""),
        CustomerTable(42,"","Rajawali Morot Timka","Daerah","Timika"),
        CustomerTable(43,"","Rumah Kursi","Makassar & Sekitarnya",""),
        CustomerTable(44,"","Rumah Sofa","Daerah","Kolaka"),
        CustomerTable(45,"","RGARAGE","Makassar & Sekitarnya",""),
        CustomerTable(46,"","Rezky Jok","Makassar & Sekitarnya",""),
        CustomerTable(47,"","Riski Jok","Daerah","Barru"),
        CustomerTable(48,"","Beo","Makassar & Sekitarnya",""),
        CustomerTable(49,"","Sun Variasi","Makassar & Sekitarnya",""),
        CustomerTable(50,"","Susan Jok","Makassar & Sekitarnya",""),
        CustomerTable(51,"","Sumber Jok","Daerah","Sengkang"),
        CustomerTable(52,"","Surabaya Motor","Makassar & Sekitarnya",""),
        CustomerTable(53,"","Selayar","Makassar & Sekitarnya",""),
        CustomerTable(54,"","SKAAD Audio Bintuni","Daerah","Bintuni"),
        CustomerTable(55,"","Sam & Sons","Makassar & Sekitarnya",""),
        CustomerTable(56,"","Terminal","Makassar & Sekitarnya",""),
        CustomerTable(57,"","Unnang","Makassar & Sekitarnya",""),
        CustomerTable(58,"","Variasi 77","Makassar & Sekitarnya",""),
        CustomerTable(59,"","Wendy","Makassar & Sekitarnya",""),
        CustomerTable(60,"","Yusdar Motor","Makassar & Sekitarnya",""),
        CustomerTable(61,"","Chessy","Makassar & Sekitarnya","",null,"BON"),
        CustomerTable(62,"","Global","Makassar & Sekitarnya","",null,"BON"),
    )
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
        val customerDao = database.customerDao

        val customers = custList.map { customer ->
            CustomerTable(
                customerName = customer.customerName,
                customerBussinessName = customer.customerBussinessName,
                customerLocation = customer.customerLocation,
                customerAddress = customer.customerAddress,
                customerLevel = customer.customerLevel,
                customerTag1 = customer.customerTag1
            )
        }
        customerDao.insertAll(customers)

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