package com.example.app21try6.utils



import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.app21try6.Constants.DETAILED_DATE_FORMATTER
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.models.WorkerModel

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
            //AUTOLEDER CITROEN
            id =productDao.getProductIdByProductName("AUTOLEDER CITROON")
            model= WorkerModel().apply {
                productId=id
                productCapital=55000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            //AUTOLEDER DODGE
            list.add(model)
            id =productDao.getProductIdByProductName("AUTOLEDER DODGE")
            model= WorkerModel().apply {
                productId=id
                productCapital=55000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //AUTOLEDER HUMMER
            id =productDao.getProductIdByProductName("AUTOLEDER HUMMER")
            model= WorkerModel().apply {
                productId=id
                productCapital=54500
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BARANG KECIL
            //BENANG B
            id =productDao.getProductIdByProductName("BENANG B")
            model= WorkerModel().apply {
                productId=id
                productCapital=17000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BENANG COATS
            id =productDao.getProductIdByProductName("BENANG COATS")
            model= WorkerModel().apply {
                productId=id
                productCapital=53000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BENANG GODAM
            id =productDao.getProductIdByProductName("BENANG GODAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=5500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BENANG K
            id =productDao.getProductIdByProductName("BENANG K")
            model= WorkerModel().apply {
                productId=id
                productCapital=6500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BENANG K ROLL BESAR
            id =productDao.getProductIdByProductName("BENANG K ROLL BESAR")
            model= WorkerModel().apply {
                productId=id
                productCapital=17000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BENANG PENITI K
            id =productDao.getProductIdByProductName("BENANG PENITI K")
            model= WorkerModel().apply {
                productId=id
                productCapital=5000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //CUTTER BESAR
            id =productDao.getProductIdByProductName("CUTTER BESAR")
            model= WorkerModel().apply {
                productId=id
                productCapital=7000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //CUTTER KECIL
            id =productDao.getProductIdByProductName("CUTTER KECIL")
            model= WorkerModel().apply {
                productId=id
                productCapital=5500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //DAKRON
            id =productDao.getProductIdByProductName("DAKRON")
            model= WorkerModel().apply {
                productId=id
                productCapital=47000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //GUNTING
            id =productDao.getProductIdByProductName("GUNTING")
            model= WorkerModel().apply {
                productId=id
                productCapital=18000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //GUNTING HITAM
            id =productDao.getProductIdByProductName("GUNTING HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=70000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //HEKTER
            id =productDao.getProductIdByProductName("HEKTER")
            model= WorkerModel().apply {
                productId=id
                productCapital=8000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //JARUM KARUNG
            id =productDao.getProductIdByProductName("JARUM KARUNG")
            model= WorkerModel().apply {
                productId=id
                productCapital=2500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //JARUM PENTUL
            id =productDao.getProductIdByProductName("JARUM PENTUL")
            model= WorkerModel().apply {
                productId=id
                productCapital=1800
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //JARUM SUM
            id =productDao.getProductIdByProductName("JARUM SUM")
            model= WorkerModel().apply {
                productId=id
                productCapital=4500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //KANCING B
            id =productDao.getProductIdByProductName("KANCING B")
            model= WorkerModel().apply {
                productId=id
                productCapital=67500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //KANCING BERLIAN DATAR
            id =productDao.getProductIdByProductName("KANCING BERLIAN DATAR")
            model= WorkerModel().apply {
                productId=id
                productCapital=50000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //KANCING K
            id =productDao.getProductIdByProductName("KANCING K")
            model= WorkerModel().apply {
                productId=id
                productCapital=26000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //KARET PANDA
            id =productDao.getProductIdByProductName("KARET PANDA")
            model= WorkerModel().apply {
                productId=id
                productCapital=50000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //KARET PANDA NO 12
            id =productDao.getProductIdByProductName("KARET PANDA NO 12")
            model= WorkerModel().apply {
                productId=id
                productCapital=55000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //KARET TOPI 2MM
            id =productDao.getProductIdByProductName("KARET TOPI 2MM")
            model= WorkerModel().apply {
                productId=id
                productCapital=21000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //KARET TOPI 2MM PUTIH
            id =productDao.getProductIdByProductName("KARET TOPI 2MM PUTIH")
            model= WorkerModel().apply {
                productId=id
                productCapital=21000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //KARET TOPI 3MM
            id =productDao.getProductIdByProductName("KARET TOPI 3MM")
            model= WorkerModel().apply {
                productId=id
                productCapital=28500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //KARET TOPI 4MM
            id =productDao.getProductIdByProductName("KARET TOPI 4MM")
            model= WorkerModel().apply {
                productId=id
                productCapital=38000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //KARET TOPI 5MM
            id =productDao.getProductIdByProductName("KARET TOPI 5MM")
            model= WorkerModel().apply {
                productId=id
                productCapital=47500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //KAWAT
            id =productDao.getProductIdByProductName("KAWAT")
            model= WorkerModel().apply {
                productId=id
                productCapital=18000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //KEPALA RES
            id =productDao.getProductIdByProductName("KEPALA RES")
            model= WorkerModel().apply {
                productId=id
                productCapital=850
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //KUAS
            id =productDao.getProductIdByProductName("KUAS")
            model= WorkerModel().apply {
                productId=id
                productCapital=4500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //LEM FOX BIRU B
            id =productDao.getProductIdByProductName("LEM FOX BIRU B")
            model= WorkerModel().apply {
                productId=id
                productCapital=155000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //LEM FOX BIRU K
            id =productDao.getProductIdByProductName("LEM FOX BIRU K")
            model= WorkerModel().apply {
                productId=id
                productCapital=44000
                fromDate= DETAILED_DATE_FORMATTER.parse("24-04-2026 00:00")
            }
            list.add(model)
            //LEM FOX MERAH B
            id =productDao.getProductIdByProductName("LEM FOX MERAH B")
            model= WorkerModel().apply {
                productId=id
                productCapital=165000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //LEM FOX MERAH K
            id =productDao.getProductIdByProductName("LEM FOX MERAH K")
            model= WorkerModel().apply {
                productId=id
                productCapital=45000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //MINYAK MESIN
            id =productDao.getProductIdByProductName("MINYAK MESIN")
            model= WorkerModel().apply {
                productId=id
                productCapital=25000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //MINYAK MESIN K
            id =productDao.getProductIdByProductName("MINYAK MESIN K")
            model= WorkerModel().apply {
                productId=id
                productCapital=7000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //PEN SILVER
            id =productDao.getProductIdByProductName("PEN SILVER")
            model= WorkerModel().apply {
                productId=id
                productCapital=1500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //PEREKAT B
            id =productDao.getProductIdByProductName("PEREKAT B")
            model= WorkerModel().apply {
                productId=id
                productCapital=5500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //PEREKAT K
            id =productDao.getProductIdByProductName("PEREKAT K")
            model= WorkerModel().apply {
                productId=id
                productCapital=2500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //RES
            id =productDao.getProductIdByProductName("RES")
            model= WorkerModel().apply {
                productId=id
                productCapital=2500
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //RING C
            id =productDao.getProductIdByProductName("RING C")
            model= WorkerModel().apply {
                productId=id
                productCapital=4000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //TALI KUR HITAM
            id =productDao.getProductIdByProductName("TALI KUR HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=20000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //TALI KUR PUTIH
            id =productDao.getProductIdByProductName("TALI KUR PUTIH")
            model= WorkerModel().apply {
                productId=id
                productCapital=22000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //TANG RING C HIJAU
            id =productDao.getProductIdByProductName("TANG RING C HIJAU")
            model= WorkerModel().apply {
                productId=id
                productCapital=125000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //TANG RING C MERAH
            id =productDao.getProductIdByProductName("TANG RING C MERAH")
            model= WorkerModel().apply {
                productId=id
                productCapital=80000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)

            //BIAYA
            //BIAYA ANTAR
            id =productDao.getProductIdByProductName("BIAYA ANTAR")
            model= WorkerModel().apply {
                productId=id
                productCapital=60000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BIAYA EKSPEDISI
            id =productDao.getProductIdByProductName("BIAYA EKSPEDISI")
            model= WorkerModel().apply {
                productId=id
                productCapital=100000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BIAYA TUKAR
            id =productDao.getProductIdByProductName("BIAYA TUKAR")
            model= WorkerModel().apply {
                productId=id
                productCapital=30000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)

            //BUSA
            //BUSA 1CM KLB
            id =productDao.getProductIdByProductName("BUSA 1CM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=38500
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA 1CM PRS HITAM
            id =productDao.getProductIdByProductName("BUSA 1CM PRS HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=39500
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA 1CM PRS PUTIH
            id =productDao.getProductIdByProductName("BUSA 1CM PRS PUTIH")
            model= WorkerModel().apply {
                productId=id
                productCapital=51000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA 1CM KLB
            id =productDao.getProductIdByProductName("BUSA 3MM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=18000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA 4MM KLB
            id =productDao.getProductIdByProductName("BUSA 4MM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=19000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA 4MM PRS HITAM
            id =productDao.getProductIdByProductName("BUSA 4MM PRS HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=21000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA 4MM PRS PUTIH
            id =productDao.getProductIdByProductName("BUSA 4MM PRS PUTIH")
            model= WorkerModel().apply {
                productId=id
                productCapital=26000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA 5MM KLB
            id =productDao.getProductIdByProductName("BUSA 5MM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=21000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA 8MM KLB
            id =productDao.getProductIdByProductName("BUSA 8MM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=38500
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA 8MM PRS
            id =productDao.getProductIdByProductName("BUSA 8MM PRS")
            model= WorkerModel().apply {
                productId=id
                productCapital=39000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER 1CM KLB
            id =productDao.getProductIdByProductName("BUSA SUPER 1CM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=59000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER 1CM PRS
            id =productDao.getProductIdByProductName("BUSA SUPER 1CM PRS")
            model= WorkerModel().apply {
                productId=id
                productCapital=64000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER 1CM PRS TIPIS
            id =productDao.getProductIdByProductName("BUSA SUPER 1CM PRS TIPIS")
            model= WorkerModel().apply {
                productId=id
                productCapital=60500
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER 8MM KLB
            id =productDao.getProductIdByProductName("BUSA SUPER 8MM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=51000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER 8MM PRS
            id =productDao.getProductIdByProductName("BUSA SUPER 8MM PRS")
            model= WorkerModel().apply {
                productId=id
                productCapital=53000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER KLB HIJAU
            id =productDao.getProductIdByProductName("BUSA SUPER KLB HIJAU")
            model= WorkerModel().apply {
                productId=id
                productCapital=51000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER PRS HIJAU
            id =productDao.getProductIdByProductName("BUSA SUPER PRS HIJAU")
            model= WorkerModel().apply {
                productId=id
                productCapital=53000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //BUSA LEMBARAN
            //BUSA LEMBARAN 1 CM PINK
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1 CM PINK")
            model= WorkerModel().apply {
                productId=id
                productCapital=18000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 1CM HIJAU
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1CM HIJAU")
            model= WorkerModel().apply {
                productId=id
                productCapital=19000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            // BUSA LEMBARAN 1CM HITAM
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1CM HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=43000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 1CM KUNING
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1CM KUNING")
            model= WorkerModel().apply {
                productId=id
                productCapital=32000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 1CM MERAH
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1CM MERAH")
            model= WorkerModel().apply {
                productId=id
                productCapital=43000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 2 CM PINK
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2 CM PINK")
            model= WorkerModel().apply {
                productId=id
                productCapital=40000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 2CM HIJAU
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM HIJAU")
            model= WorkerModel().apply {
                productId=id
                productCapital=42000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 2CM HITAM
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=90000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 2CM KUNING
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM KUNING")
            model= WorkerModel().apply {
                productId=id
                productCapital=63000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 2CM MAGENTA
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM MAGENTA")
            model= WorkerModel().apply {
                productId=id
                productCapital=83000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 2CM MERAH
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM MERAH")
            model= WorkerModel().apply {
                productId=id
                productCapital=90000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 3 CM HITAM
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 3 CM HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=140000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 3CM HIJAU
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 3CM HIJAU")
            model= WorkerModel().apply {
                productId=id
                productCapital=63000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 3CM KUNING
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 3CM KUNING")
            model= WorkerModel().apply {
                productId=id
                productCapital=18000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 3CM MAGENTA
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 3CM MAGENTA")
            model= WorkerModel().apply {
                productId=id
                productCapital=124000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 3CM PINK
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 3CM PINK")
            model= WorkerModel().apply {
                productId=id
                productCapital=64000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 4 CM HIJAU
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 4 CM HIJAU")
            model= WorkerModel().apply {
                productId=id
                productCapital=85000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 4 CM PINK
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 4 CM PINK")
            model= WorkerModel().apply {
                productId=id
                productCapital=85000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 4CM HITAM
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 4CM HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=172000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 4CM KUNING
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 4CM KUNING")
            model= WorkerModel().apply {
                productId=id
                productCapital=128000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 4CM MAGENTA
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 4CM MAGENTA")
            model= WorkerModel().apply {
                productId=id
                productCapital=166000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 5 CM KUNING
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 5 CM KUNING")
            model= WorkerModel().apply {
                productId=id
                productCapital=165000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 5 CM PINK
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 5 CM PINK")
            model= WorkerModel().apply {
                productId=id
                productCapital=106000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 5CM HITAM
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 5CM HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=215000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 5CM MAGENTA
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 5CM MAGENTA")
            model= WorkerModel().apply {
                productId=id
                productCapital=207000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 5CM MERAH
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 5CM MERAH")
            model= WorkerModel().apply {
                productId=id
                productCapital=215000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN HIJAU 5 CM
            id =productDao.getProductIdByProductName("BUSA LEMBARAN HIJAU 5 CM")
            model= WorkerModel().apply {
                productId=id
                productCapital=106000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)

            //CARDOVA
            id =productDao.getProductIdByProductName("CARDOVA")
            model= WorkerModel().apply {
                productId=id
                productCapital=66000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)

            //KARPET CARVIERRO
            id =productDao.getProductIdByProductName("KARPET CARVIERRO")
            model= WorkerModel().apply {
                productId=id
                productCapital=106000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //EXCELENT
            id =productDao.getProductIdByProductName("EXCELENT")
            model= WorkerModel().apply {
                productId=id
                productCapital=34000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //EXCELENT CATUR
            id =productDao.getProductIdByProductName("EXCELENT CATUR")
            model= WorkerModel().apply {
                productId=id
                productCapital=45000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //KAIN TENDA
            id =productDao.getProductIdByProductName("KAIN TENDA")
            model= WorkerModel().apply {
                productId=id
                productCapital=30000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //IMITASI
            id =productDao.getProductIdByProductName("IMITASI")
            model= WorkerModel().apply {
                productId=id
                productCapital=20000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //IONIC
            id =productDao.getProductIdByProductName("IONIC")
            model= WorkerModel().apply {
                productId=id
                productCapital=54000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //ITALIANO
            id =productDao.getProductIdByProductName("ITALIANO")
            model= WorkerModel().apply {
                productId=id
                productCapital=40000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //KARPET JAGUAR
            id =productDao.getProductIdByProductName("KARPET JAGUAR")
            model= WorkerModel().apply {
                productId=id
                productCapital=36000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //JOK MOTOR AUTO PRESS 85
            //CARDOVA
            id =productDao.getProductIdByProductName("CARDOVA")
            model= WorkerModel().apply {
                productId=id
                productCapital=66000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)

            //KARPET CARVIERRO
            id =productDao.getProductIdByProductName("KARPET CARVIERRO")
            model= WorkerModel().apply {
                productId=id
                productCapital=106000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //EXCELENT
            id =productDao.getProductIdByProductName("EXCELENT")
            model= WorkerModel().apply {
                productId=id
                productCapital=34000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //EXCELENT CATUR
            id =productDao.getProductIdByProductName("EXCELENT CATUR")
            model= WorkerModel().apply {
                productId=id
                productCapital=45000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //KAIN TENDA
            id =productDao.getProductIdByProductName("KAIN TENDA")
            model= WorkerModel().apply {
                productId=id
                productCapital=30000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //IMITASI
            id =productDao.getProductIdByProductName("IMITASI")
            model= WorkerModel().apply {
                productId=id
                productCapital=20000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //IONIC
            id =productDao.getProductIdByProductName("IONIC")
            model= WorkerModel().apply {
                productId=id
                productCapital=54000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //ITALIANO
            id =productDao.getProductIdByProductName("ITALIANO")
            model= WorkerModel().apply {
                productId=id
                productCapital=40000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //JOK MOTOR
            //JOK MOTOR AUTO PRESS 85
            id =productDao.getProductIdByProductName("JOK MOTOR AUTO PRESS 85")
            model= WorkerModel().apply {
                productId=id
                productCapital=75000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            // JOK MOTOR JAHIT 40K
            id =productDao.getProductIdByProductName("JOK MOTOR JAHIT 40K")
            model= WorkerModel().apply {
                productId=id
                productCapital=35000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //JOK MOTOR JAHIT 50
            id =productDao.getProductIdByProductName("JOK MOTOR JAHIT 50")
            model= WorkerModel().apply {
                productId=id
                productCapital=45000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //JOK MOTOR JAHIT 55
            id =productDao.getProductIdByProductName("JOK MOTOR JAHIT 55")
            model= WorkerModel().apply {
                productId=id
                productCapital=50000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //JOK MOTOR JAHIT KEPALA 25
            id =productDao.getProductIdByProductName("JOK MOTOR JAHIT KEPALA 25")
            model= WorkerModel().apply {
                productId=id
                productCapital=22000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //JOK MOTOR LEMBARAN 15
            id =productDao.getProductIdByProductName("JOK MOTOR LEMBARAN 15")
            model= WorkerModel().apply {
                productId=id
                productCapital=13000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //JOK MOTOR LEMBARAN 20
            id =productDao.getProductIdByProductName("JOK MOTOR LEMBARAN 20")
            model= WorkerModel().apply {
                productId=id
                productCapital=18000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //JOK MOTOR LEMBARAN SABLON 22
            id =productDao.getProductIdByProductName("JOK MOTOR LEMBARAN SABLON 22")
            model= WorkerModel().apply {
                productId=id
                productCapital=20000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //JOK MOTOR ZEUS PRESS 100
            id =productDao.getProductIdByProductName("JOK MOTOR ZEUS PRESS 100")
            model= WorkerModel().apply {
                productId=id
                productCapital=77000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //ZEUS PRESS LEMBARAN 60
            id =productDao.getProductIdByProductName("ZEUS PRESS LEMBARAN 60")
            model= WorkerModel().apply {
                productId=id
                productCapital=50000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)

            //KAIN IMPORT 3D
            id =productDao.getProductIdByProductName("KAIN IMPORT 3D")
            model= WorkerModel().apply {
                productId=id
                productCapital=45000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //KAIN PARIS
            id =productDao.getProductIdByProductName("KAIN PARIS")
            model= WorkerModel().apply {
                productId=id
                productCapital=4500
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //KAIN PARIS PUTIH
            id =productDao.getProductIdByProductName("KAIN PARIS PUTIH")
            model= WorkerModel().apply {
                productId=id
                productCapital=5500
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //KARPET AUTOLEDER
            id =productDao.getProductIdByProductName("KARPET AUTOLEDER")
            model= WorkerModel().apply {
                productId=id
                productCapital=64000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //KARPET FORCE
            id =productDao.getProductIdByProductName("KARPET FORCE")
            model= WorkerModel().apply {
                productId=id
                productCapital=61000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //LEDERLUX
            //EUROLEDER
            id =productDao.getProductIdByProductName("EUROLEDER")
            model= WorkerModel().apply {
                productId=id
                productCapital=200000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //LEDERLUX ALTRO
            id =productDao.getProductIdByProductName("LEDERLUX ALTRO")
            model= WorkerModel().apply {
                productId=id
                productCapital=112000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //LEDERLUX PRIMO
            id =productDao.getProductIdByProductName("LEDERLUX PRIMO")
            model= WorkerModel().apply {
                productId=id
                productCapital=86000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //MOGENLEDER
            id =productDao.getProductIdByProductName("MOGENLEDER")
            model= WorkerModel().apply {
                productId=id
                productCapital=130000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)

            //MBTECH
            //MB CAMARO
            id =productDao.getProductIdByProductName("MB CAMARO")
            model= WorkerModel().apply {
                productId=id
                productCapital=118000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //MB CARRERA
            id =productDao.getProductIdByProductName("MB CARRERA")
            model= WorkerModel().apply {
                productId=id
                productCapital=171000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //MB FIESTA
            id =productDao.getProductIdByProductName("MB FIESTA")
            model= WorkerModel().apply {
                productId=id
                productCapital=118000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //MB GIORGIO
            id =productDao.getProductIdByProductName("MB GIORGIO")
            model= WorkerModel().apply {
                productId=id
                productCapital=103000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //MB PICASSO
            id =productDao.getProductIdByProductName("MB PICASSO")
            model= WorkerModel().apply {
                productId=id
                productCapital=121000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //MB RYDER
            id =productDao.getProductIdByProductName("MB RYDER")
            model= WorkerModel().apply {
                productId=id
                productCapital=116000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //MB SUPERIOR
            id =productDao.getProductIdByProductName("MB SUPERIOR")
            model= WorkerModel().apply {
                productId=id
                productCapital=116000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //MH KING
            id =productDao.getProductIdByProductName("MH KING")
            model= WorkerModel().apply {
                productId=id
                productCapital=87000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)

            //MORGAN
            id =productDao.getProductIdByProductName("MORGAN")
            model= WorkerModel().apply {
                productId=id
                productCapital=60000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)

            //MURANO BLAST
            id =productDao.getProductIdByProductName("MURANO BLAST")
            model= WorkerModel().apply {
                productId=id
                productCapital=125000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)

            //MURANO NAPPA
            id =productDao.getProductIdByProductName("MURANO NAPPA")
            model= WorkerModel().apply {
                productId=id
                productCapital=121000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)

            //MYO
            id =productDao.getProductIdByProductName("MYO")
            model= WorkerModel().apply {
                productId=id
                productCapital=46000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //NAPPA PRIME
            id =productDao.getProductIdByProductName("NAPPA PRIME")
            model= WorkerModel().apply {
                productId=id
                productCapital=89000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //NAPPA SILK
            id =productDao.getProductIdByProductName("NAPPA SILK")
            model= WorkerModel().apply {
                productId=id
                productCapital=89000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //NAVARO
            id =productDao.getProductIdByProductName("NAVARO")
            model= WorkerModel().apply {
                productId=id
                productCapital=94000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //KAIN JOK MOTOR REVO
            id =productDao.getProductIdByProductName("KAIN JOK MOTOR REVO")
            model= WorkerModel().apply {
                productId=id
                productCapital=28000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)

            //SBTECH
            id =productDao.getProductIdByProductName("SBTECH AMPLAS")
            model= WorkerModel().apply {
                productId=id
                productCapital=45000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //VISION
            //BUGGATI
            id =productDao.getProductIdByProductName("BUGGATI")
            model= WorkerModel().apply {
                productId=id
                productCapital=167000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //KARBON VISION
            id =productDao.getProductIdByProductName("KARBON VISION")
            model= WorkerModel().apply {
                productId=id
                productCapital=82000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //LEVANTE
            id =productDao.getProductIdByProductName("LEVANTE")
            model= WorkerModel().apply {
                productId=id
                productCapital=207000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //LUGANO
            id =productDao.getProductIdByProductName("LUGANO")
            model= WorkerModel().apply {
                productId=id
                productCapital=145000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)
            //SAFFIANO
            id =productDao.getProductIdByProductName("SAFFIANO")
            model= WorkerModel().apply {
                productId=id
                productCapital=107000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //ZAVIER
            id =productDao.getProductIdByProductName("ZAVIER")
            model= WorkerModel().apply {
                productId=id
                productCapital=140000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)

            //ZEUS
            id =productDao.getProductIdByProductName("ZEUS")
            model= WorkerModel().apply {
                productId=id
                productCapital=65000
                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
            }
            list.add(model)















            list.forEach {
                Log.i("UpdateProductCapitalWorker","${it.fromDate}, ${it.productCapital}, ${it.productId}")
               // transactionDetailDao.updateProductCapitalBeforeDate(it.fromDate, it.productCapital, it.productId)
            }
            val brandList=transactionDetailDao.getTransactionDetailsByBrandName("BENANG B")
            brandList.forEach {
                Log.i("UpdateProductCapitalWorkerr","${it.trans_item_name}, ${it.product_capital}, ${it.trans_detail_date}")
            }

            //transactionDetailDao.updateProductCapitalBeforeDate(targetDate, newCapitalValue,productId)
        }catch (e: Exception){

        }
    }

}