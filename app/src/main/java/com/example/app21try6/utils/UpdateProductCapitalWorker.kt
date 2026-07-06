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

            //TO FIX BEFORE
            //BUSA 3CM KUNING
            //KARPET CARVIERRO
            //Accura
//            id=productDao.getProductIdByProductName("ACCURA")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=102000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //Alonzo
//            id=productDao.getProductIdByProductName("ALONZO")
//           model= WorkerModel().apply {
//                productId=id
//                productCapital=90500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //AGGERA
//            id=productDao.getProductIdByProductName("AGGERA")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=58500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //Accura Karbon
//            id =productDao.getProductIdByProductName("ACCURA KARBON")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=102000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //BENTLEY
//            id =productDao.getProductIdByProductName("BENTLEY")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=30000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //CLAZZIO
//            id =productDao.getProductIdByProductName("CARVIERRO CLAZZIO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=75000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //CARIVERRO UNO
//            id =productDao.getProductIdByProductName("CARVIERRO UNO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=75000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //LIZIO
//            id =productDao.getProductIdByProductName("LIZIO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=167000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //SALUTE
//            id =productDao.getProductIdByProductName("SALUTE")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=57500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //XFORCE
//            id =productDao.getProductIdByProductName("XFORCE")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=30000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //AUTOLEDER CITROEN
//            id =productDao.getProductIdByProductName("AUTOLEDER CITROON")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=61000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            //AUTOLEDER DODGE
//            list.add(model)
//            id =productDao.getProductIdByProductName("AUTOLEDER DODGE")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=61500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //AUTOLEDER HUMMER
//            id =productDao.getProductIdByProductName("AUTOLEDER HUMMER")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=61500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BARANG KECIL
//
//            //BENANG COATS
//            id =productDao.getProductIdByProductName("BENANG COATS")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=58000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//
//            //KEPALA RES
//            id =productDao.getProductIdByProductName("KEPALA RES")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=1000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //LEM FOX BIRU B
//            id =productDao.getProductIdByProductName("LEM FOX BIRU B")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=178000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //LEM FOX BIRU K
//            id =productDao.getProductIdByProductName("LEM FOX BIRU K")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=50000
//                fromDate= DETAILED_DATE_FORMATTER.parse("24-04-2026 00:00")
//            }
//            list.add(model)
//            //LEM FOX MERAH B
//            id =productDao.getProductIdByProductName("LEM FOX MERAH B")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=195000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //LEM FOX MERAH K
//            id =productDao.getProductIdByProductName("LEM FOX MERAH K")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=55000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //MINYAK MESIN
//            id =productDao.getProductIdByProductName("MINYAK MESIN")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=35000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //PEN SILVER
//            id =productDao.getProductIdByProductName("PEN SILVER")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=2500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//
//            //BUSA
//            //BUSA 1CM KLB
//            id =productDao.getProductIdByProductName("BUSA 1CM KLB")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=53000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA 1CM PRS HITAM
//            id =productDao.getProductIdByProductName("BUSA 1CM PRS HITAM")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=53500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA 1CM PRS PUTIH
//            id =productDao.getProductIdByProductName("BUSA 1CM PRS PUTIH")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=51000
//                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
//            }
//            list.add(model)
//            //BUSA 1CM KLB
//            id =productDao.getProductIdByProductName("BUSA 3MM KLB")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=18000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA 4MM KLB
//            id =productDao.getProductIdByProductName("BUSA 4MM KLB")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=22000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA 4MM PRS HITAM
//            id =productDao.getProductIdByProductName("BUSA 4MM PRS HITAM")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=26500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA 4MM PRS PUTIH
//            id =productDao.getProductIdByProductName("BUSA 4MM PRS PUTIH")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=26500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA 5MM KLB
//            id =productDao.getProductIdByProductName("BUSA 5MM KLB")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=29000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA 8MM KLB
//            id =productDao.getProductIdByProductName("BUSA 8MM KLB")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=47500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA 8MM PRS
//            id =productDao.getProductIdByProductName("BUSA 8MM PRS")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=47000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA SUPER 1CM KLB
//            id =productDao.getProductIdByProductName("BUSA SUPER 1CM KLB")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=79500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA SUPER 1CM PRS
//            id =productDao.getProductIdByProductName("BUSA SUPER 1CM PRS")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=83000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA SUPER 1CM PRS TIPIS
//            id =productDao.getProductIdByProductName("BUSA SUPER 1CM PRS TIPIS")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=82000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA SUPER 8MM KLB
//            id =productDao.getProductIdByProductName("BUSA SUPER 8MM KLB")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=69000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA SUPER 8MM PRS
//            id =productDao.getProductIdByProductName("BUSA SUPER 8MM PRS")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=71500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA SUPER KLB HIJAU
//            id =productDao.getProductIdByProductName("BUSA SUPER KLB HIJAU")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=70500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA SUPER PRS HIJAU
//            id =productDao.getProductIdByProductName("BUSA SUPER PRS HIJAU")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=70500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //BUSA LEMBARAN
//            //BUSA LEMBARAN 1 CM PINK
//            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1 CM PINK")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=22200
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA LEMBARAN 1CM HIJAU
//            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1CM HIJAU")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=21000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            // BUSA LEMBARAN 1CM HITAM
//            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1CM HITAM")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=33000
//                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
//            }
//            list.add(model)
//            //BUSA LEMBARAN 1CM KUNING
//            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1CM KUNING")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=27000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA LEMBARAN 1CM MERAH
//            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1CM MERAH")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=43000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA LEMBARAN 2 CM PINK
//            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2 CM PINK")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=42400
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA LEMBARAN 2CM HIJAU
//            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM HIJAU")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=42000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA LEMBARAN 2CM HITAM
//            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM HITAM")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=66000
//                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
//            }
//            list.add(model)
//            //BUSA LEMBARAN 2CM KUNING
//            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM KUNING")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=70000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //BUSA LEMBARAN 2CM MAGENTA
//            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM MAGENTA")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=83000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //BUSA LEMBARAN 3 CM HITAM
//            id =productDao.getProductIdByProductName("BUSA LEMBARAN 3 CM HITAM")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=99000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //BUSA LEMBARAN 3CM KUNING
//            id =productDao.getProductIdByProductName("BUSA LEMBARAN 3CM KUNING")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=114000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//
//            //KARPET CARVIERRO
//            id =productDao.getProductIdByProductName("KARPET CARVIERRO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=67000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //EXCELENT
//            id =productDao.getProductIdByProductName("EXCELENT")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=36000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //EXCELENT CATUR
//            id =productDao.getProductIdByProductName("EXCELENT CATUR")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=50000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //KAIN TENDA
//            id =productDao.getProductIdByProductName("KAIN TENDA")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=34000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //IONIC
//            id =productDao.getProductIdByProductName("IONIC")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=60000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //ITALIANO
//            id =productDao.getProductIdByProductName("ITALIANO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=45000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //KARPET JAGUAR
//            id =productDao.getProductIdByProductName("KARPET JAGUAR")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=40000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //KAIN IMPORT 3D
//            id =productDao.getProductIdByProductName("KAIN IMPORT 3D")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=50000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //KAIN PARIS
//            id =productDao.getProductIdByProductName("KAIN PARIS")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=5500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //KAIN PARIS PUTIH
//            id =productDao.getProductIdByProductName("KAIN PARIS PUTIH")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=6500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //KARPET AUTOLEDER
//            id =productDao.getProductIdByProductName("KARPET AUTOLEDER")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=71500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //KARPET FORCE
//            id =productDao.getProductIdByProductName("KARPET FORCE")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=70000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //LEDERLUX
//            //EUROLEDER
//            id =productDao.getProductIdByProductName("EUROLEDER")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=218000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //LEDERLUX ALTRO
//            id =productDao.getProductIdByProductName("LEDERLUX ALTRO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=232000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //LEDERLUX PRIMO
//            id =productDao.getProductIdByProductName("LEDERLUX PRIMO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=102000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //MBTECH
//            //MB CAMARO
//            id =productDao.getProductIdByProductName("MB CAMARO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=145000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //MB CARRERA
//            id =productDao.getProductIdByProductName("MB CARRERA")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=211500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //MB FIESTA
//            id =productDao.getProductIdByProductName("MB FIESTA")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=145000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //MB GIORGIO
//            id =productDao.getProductIdByProductName("MB GIORGIO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=125500
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //MB PICASSO
//            id =productDao.getProductIdByProductName("MB PICASSO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=147000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //MB RYDER
//            id =productDao.getProductIdByProductName("MB RYDER")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=145000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //MB SUPERIOR
//            id =productDao.getProductIdByProductName("MB SUPERIOR")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=145000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //MYO
//            id =productDao.getProductIdByProductName("MYO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=53000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //NAPPA PRIME
//            id =productDao.getProductIdByProductName("NAPPA PRIME")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=106000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //NAPPA SILK
//            id =productDao.getProductIdByProductName("NAPPA SILK")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=106000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //NAVARO
//            id =productDao.getProductIdByProductName("NAVARO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=107000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//
//            //SBTECH
//            id =productDao.getProductIdByProductName("SBTECH AMPLAS")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=50000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //VISION
//            //BUGGATI
//            id =productDao.getProductIdByProductName("BUGGATI")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=185000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //KARBON VISION
//            id =productDao.getProductIdByProductName("KARBON VISION")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=95000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //LEVANTE
//            id =productDao.getProductIdByProductName("LEVANTE")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=233000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //LUGANO
//            id =productDao.getProductIdByProductName("LUGANO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=163000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//            //SAFFIANO
//            id =productDao.getProductIdByProductName("SAFFIANO")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=118000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //ZAVIER
//            id =productDao.getProductIdByProductName("ZAVIER")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=165000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)
//
//            //ZEUS
//            id =productDao.getProductIdByProductName("ZEUS")
//            model= WorkerModel().apply {
//                productId=id
//                productCapital=75000
//                fromDate= DETAILED_DATE_FORMATTER.parse("27-04-2026 00:00")
//            }
//            list.add(model)


            id=productDao.getProductIdByProductName("ACCURA")
            model= WorkerModel().apply {
                productId=id
                productCapital=102000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //Alonzo
            id=productDao.getProductIdByProductName("ALONZO")
            model= WorkerModel().apply {
                productId=id
                productCapital=90500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //AGGERA
            id=productDao.getProductIdByProductName("AGGERA")
            model= WorkerModel().apply {
                productId=id
                productCapital=58500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //Accura Karbon
            id =productDao.getProductIdByProductName("ACCURA KARBON")
            model= WorkerModel().apply {
                productId=id
                productCapital=102000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //BENTLEY
            id =productDao.getProductIdByProductName("BENTLEY")
            model= WorkerModel().apply {
                productId=id
                productCapital=30000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //CLAZZIO
            id =productDao.getProductIdByProductName("CARVIERRO CLAZZIO")
            model= WorkerModel().apply {
                productId=id
                productCapital=75000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //CARIVERRO UNO
            id =productDao.getProductIdByProductName("CARVIERRO UNO")
            model= WorkerModel().apply {
                productId=id
                productCapital=75000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //LIZIO
            id =productDao.getProductIdByProductName("LIZIO")
            model= WorkerModel().apply {
                productId=id
                productCapital=167000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //SALUTE
            id =productDao.getProductIdByProductName("SALUTE")
            model= WorkerModel().apply {
                productId=id
                productCapital=57500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //XFORCE
            id =productDao.getProductIdByProductName("XFORCE")
            model= WorkerModel().apply {
                productId=id
                productCapital=30000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //AUTOLEDER CITROEN
            id =productDao.getProductIdByProductName("AUTOLEDER CITROON")
            model= WorkerModel().apply {
                productId=id
                productCapital=61000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            //AUTOLEDER DODGE
            list.add(model)
            id =productDao.getProductIdByProductName("AUTOLEDER DODGE")
            model= WorkerModel().apply {
                productId=id
                productCapital=61500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //AUTOLEDER HUMMER
            id =productDao.getProductIdByProductName("AUTOLEDER HUMMER")
            model= WorkerModel().apply {
                productId=id
                productCapital=61500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BARANG KECIL

            //BENANG COATS
            id =productDao.getProductIdByProductName("BENANG COATS")
            model= WorkerModel().apply {
                productId=id
                productCapital=58000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)


            //KEPALA RES
            id =productDao.getProductIdByProductName("KEPALA RES")
            model= WorkerModel().apply {
                productId=id
                productCapital=1000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //LEM FOX BIRU B
            id =productDao.getProductIdByProductName("LEM FOX BIRU B")
            model= WorkerModel().apply {
                productId=id
                productCapital=178000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //LEM FOX BIRU K
            id =productDao.getProductIdByProductName("LEM FOX BIRU K")
            model= WorkerModel().apply {
                productId=id
                productCapital=50000
                fromDate= DETAILED_DATE_FORMATTER.parse("24-04-2026 00:00")
            }
            list.add(model)
            //LEM FOX MERAH B
            id =productDao.getProductIdByProductName("LEM FOX MERAH B")
            model= WorkerModel().apply {
                productId=id
                productCapital=195000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //LEM FOX MERAH K
            id =productDao.getProductIdByProductName("LEM FOX MERAH K")
            model= WorkerModel().apply {
                productId=id
                productCapital=55000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //MINYAK MESIN
            id =productDao.getProductIdByProductName("MINYAK MESIN")
            model= WorkerModel().apply {
                productId=id
                productCapital=35000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //PEN SILVER
            id =productDao.getProductIdByProductName("PEN SILVER")
            model= WorkerModel().apply {
                productId=id
                productCapital=2500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)


            //BUSA
            //BUSA 1CM KLB
            id =productDao.getProductIdByProductName("BUSA 1CM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=53000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA 1CM PRS HITAM
            id =productDao.getProductIdByProductName("BUSA 1CM PRS HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=53500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
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
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA 4MM KLB
            id =productDao.getProductIdByProductName("BUSA 4MM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=22000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA 4MM PRS HITAM
            id =productDao.getProductIdByProductName("BUSA 4MM PRS HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=26500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA 4MM PRS PUTIH
            id =productDao.getProductIdByProductName("BUSA 4MM PRS PUTIH")
            model= WorkerModel().apply {
                productId=id
                productCapital=26500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA 5MM KLB
            id =productDao.getProductIdByProductName("BUSA 5MM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=29000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA 8MM KLB
            id =productDao.getProductIdByProductName("BUSA 8MM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=47500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA 8MM PRS
            id =productDao.getProductIdByProductName("BUSA 8MM PRS")
            model= WorkerModel().apply {
                productId=id
                productCapital=47000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER 1CM KLB
            id =productDao.getProductIdByProductName("BUSA SUPER 1CM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=79500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER 1CM PRS
            id =productDao.getProductIdByProductName("BUSA SUPER 1CM PRS")
            model= WorkerModel().apply {
                productId=id
                productCapital=83000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER 1CM PRS TIPIS
            id =productDao.getProductIdByProductName("BUSA SUPER 1CM PRS TIPIS")
            model= WorkerModel().apply {
                productId=id
                productCapital=82000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER 8MM KLB
            id =productDao.getProductIdByProductName("BUSA SUPER 8MM KLB")
            model= WorkerModel().apply {
                productId=id
                productCapital=69000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER 8MM PRS
            id =productDao.getProductIdByProductName("BUSA SUPER 8MM PRS")
            model= WorkerModel().apply {
                productId=id
                productCapital=71500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER KLB HIJAU
            id =productDao.getProductIdByProductName("BUSA SUPER KLB HIJAU")
            model= WorkerModel().apply {
                productId=id
                productCapital=70500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA SUPER PRS HIJAU
            id =productDao.getProductIdByProductName("BUSA SUPER PRS HIJAU")
            model= WorkerModel().apply {
                productId=id
                productCapital=70500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //BUSA LEMBARAN
            //BUSA LEMBARAN 1 CM PINK
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1 CM PINK")
            model= WorkerModel().apply {
                productId=id
                productCapital=22200
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 4CM HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=180000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 1CM HIJAU
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1CM HIJAU")
            model= WorkerModel().apply {
                productId=id
                productCapital=21000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            // BUSA LEMBARAN 1CM HITAM
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1CM HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=33000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 1CM KUNING
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1CM KUNING")
            model= WorkerModel().apply {
                productId=id
                productCapital=27000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 1CM MERAH
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 1CM MERAH")
            model= WorkerModel().apply {
                productId=id
                productCapital=43000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 2 CM PINK
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2 CM PINK")
            model= WorkerModel().apply {
                productId=id
                productCapital=42400
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 2CM HIJAU
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM HIJAU")
            model= WorkerModel().apply {
                productId=id
                productCapital=42000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 2CM HITAM
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=66000
                fromDate= DETAILED_DATE_FORMATTER.parse("31-07-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 2CM KUNING
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM KUNING")
            model= WorkerModel().apply {
                productId=id
                productCapital=70000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //BUSA LEMBARAN 2CM MAGENTA
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 2CM MAGENTA")
            model= WorkerModel().apply {
                productId=id
                productCapital=83000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //BUSA LEMBARAN 3 CM HITAM
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 3 CM HITAM")
            model= WorkerModel().apply {
                productId=id
                productCapital=99000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //BUSA LEMBARAN 3CM KUNING
            id =productDao.getProductIdByProductName("BUSA LEMBARAN 3CM KUNING")
            model= WorkerModel().apply {
                productId=id
                productCapital=114000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)


            //KARPET CARVIERRO
            id =productDao.getProductIdByProductName("KARPET CARVIERRO")
            model= WorkerModel().apply {
                productId=id
                productCapital=67000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //EXCELENT
            id =productDao.getProductIdByProductName("EXCELENT")
            model= WorkerModel().apply {
                productId=id
                productCapital=36000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //EXCELENT CATUR
            id =productDao.getProductIdByProductName("EXCELENT CATUR")
            model= WorkerModel().apply {
                productId=id
                productCapital=50000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //KAIN TENDA
            id =productDao.getProductIdByProductName("KAIN TENDA")
            model= WorkerModel().apply {
                productId=id
                productCapital=34000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //IONIC
            id =productDao.getProductIdByProductName("IONIC")
            model= WorkerModel().apply {
                productId=id
                productCapital=60000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //ITALIANO
            id =productDao.getProductIdByProductName("ITALIANO")
            model= WorkerModel().apply {
                productId=id
                productCapital=45000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //KARPET JAGUAR
            id =productDao.getProductIdByProductName("KARPET JAGUAR")
            model= WorkerModel().apply {
                productId=id
                productCapital=40000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //KAIN IMPORT 3D
            id =productDao.getProductIdByProductName("KAIN IMPORT 3D")
            model= WorkerModel().apply {
                productId=id
                productCapital=50000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //KAIN PARIS
            id =productDao.getProductIdByProductName("KAIN PARIS")
            model= WorkerModel().apply {
                productId=id
                productCapital=5500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //KAIN PARIS PUTIH
            id =productDao.getProductIdByProductName("KAIN PARIS PUTIH")
            model= WorkerModel().apply {
                productId=id
                productCapital=6500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //KARPET AUTOLEDER
            id =productDao.getProductIdByProductName("KARPET AUTOLEDER")
            model= WorkerModel().apply {
                productId=id
                productCapital=71500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //KARPET FORCE
            id =productDao.getProductIdByProductName("KARPET FORCE")
            model= WorkerModel().apply {
                productId=id
                productCapital=70000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //LEDERLUX
            //EUROLEDER
            id =productDao.getProductIdByProductName("EUROLEDER")
            model= WorkerModel().apply {
                productId=id
                productCapital=218000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //LEDERLUX ALTRO
            id =productDao.getProductIdByProductName("LEDERLUX ALTRO")
            model= WorkerModel().apply {
                productId=id
                productCapital=232000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //LEDERLUX PRIMO
            id =productDao.getProductIdByProductName("LEDERLUX PRIMO")
            model= WorkerModel().apply {
                productId=id
                productCapital=102000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //MBTECH
            //MB CAMARO
            id =productDao.getProductIdByProductName("MB CAMARO")
            model= WorkerModel().apply {
                productId=id
                productCapital=145000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //MB CARRERA
            id =productDao.getProductIdByProductName("MB CARRERA")
            model= WorkerModel().apply {
                productId=id
                productCapital=211500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //MB FIESTA
            id =productDao.getProductIdByProductName("MB FIESTA")
            model= WorkerModel().apply {
                productId=id
                productCapital=145000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //MB GIORGIO
            id =productDao.getProductIdByProductName("MB GIORGIO")
            model= WorkerModel().apply {
                productId=id
                productCapital=125500
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //MB PICASSO
            id =productDao.getProductIdByProductName("MB PICASSO")
            model= WorkerModel().apply {
                productId=id
                productCapital=147000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //MB RYDER
            id =productDao.getProductIdByProductName("MB RYDER")
            model= WorkerModel().apply {
                productId=id
                productCapital=145000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //MB SUPERIOR
            id =productDao.getProductIdByProductName("MB SUPERIOR")
            model= WorkerModel().apply {
                productId=id
                productCapital=145000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //MYO
            id =productDao.getProductIdByProductName("MYO")
            model= WorkerModel().apply {
                productId=id
                productCapital=53000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //NAPPA PRIME
            id =productDao.getProductIdByProductName("NAPPA PRIME")
            model= WorkerModel().apply {
                productId=id
                productCapital=106000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //NAPPA SILK
            id =productDao.getProductIdByProductName("NAPPA SILK")
            model= WorkerModel().apply {
                productId=id
                productCapital=106000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //NAVARO
            id =productDao.getProductIdByProductName("NAVARO")
            model= WorkerModel().apply {
                productId=id
                productCapital=107000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //SBTECH
            id =productDao.getProductIdByProductName("SBTECH AMPLAS")
            model= WorkerModel().apply {
                productId=id
                productCapital=50000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //VISION
            //BUGGATI
            id =productDao.getProductIdByProductName("BUGGATI")
            model= WorkerModel().apply {
                productId=id
                productCapital=185000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //KARBON VISION
            id =productDao.getProductIdByProductName("KARBON VISION")
            model= WorkerModel().apply {
                productId=id
                productCapital=95000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //LEVANTE
            id =productDao.getProductIdByProductName("LEVANTE")
            model= WorkerModel().apply {
                productId=id
                productCapital=233000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)
            //LUGANO
            id =productDao.getProductIdByProductName("LUGANO")
            model= WorkerModel().apply {
                productId=id
                productCapital=163000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //SAFFIANO
            id =productDao.getProductIdByProductName("SAFFIANO")
            model= WorkerModel().apply {
                productId=id
                productCapital=118000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //ZAVIER
            id =productDao.getProductIdByProductName("ZAVIER")
            model= WorkerModel().apply {
                productId=id
                productCapital=165000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)

            //ZEUS
            id =productDao.getProductIdByProductName("ZEUS")
            model= WorkerModel().apply {
                productId=id
                productCapital=75000
                fromDate= DETAILED_DATE_FORMATTER.parse("15-04-2026 00:00")
            }
            list.add(model)


            list.forEach {
                Log.i("UpdateProductCapitalWorker","${it.fromDate}, ${it.productCapital}, ${it.productId}")
                transactionDetailDao.updateProductCapitalAfterDate(it.fromDate, it.productCapital, it.productId)
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