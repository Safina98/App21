// RealtimeDatabaseSync.kt
package com.example.app21try6.database.cloud

import android.content.Context
import android.util.Log
import com.example.app21try6.Constants
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.DetailWarnaDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.SummaryDbDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.Category
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.MerchandiseRetail
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.scheduleImmediateSync
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.*
import java.util.Date
import java.util.concurrent.Executors

object RealtimeDatabaseSync {

    private lateinit var database: DatabaseReference
    private var isInitialized = false

    // --------------------------------------------------------------
    // 1️⃣ Initialize Firebase Realtime Database
    // --------------------------------------------------------------
    fun init(context: Context) {
        if (isInitialized) return
        // Enable offline caching BEFORE getInstance()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        val correctUrl =
            "https://onlineapp21-2679d-default-rtdb.asia-southeast1.firebasedatabase.app/"

        database = FirebaseDatabase.getInstance(correctUrl).reference
        isInitialized = true

        Log.e("RDBSync", "Firebase initialized using URL: $correctUrl")
    }

    // --------------------------------------------------------------
    // 2️⃣ Upload data to Firebase (local → cloud)
    // --------------------------------------------------------------
    fun <T> upload(tableName: String, cloudId: String, cloudObject: T) {
        if (!isInitialized || cloudId.isEmpty()) return

        database.child(tableName)
            .child(cloudId)
            .setValue(cloudObject)
            .addOnSuccessListener {
                Log.e("FIREBASE_UPLOAD", "SUCCESS → $tableName/$cloudId")
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE_UPLOAD", "FAILED → $tableName/$cloudId", e)
            }
    }

    suspend fun <T> uploadSuspended(tableName: String, cloudId: String, cloudObject: T) {
        if (!isInitialized || cloudId.isEmpty()) return
        try {
            // Tasks.await() is the key: it suspends the coroutine until the operation completes.

            Tasks.await(
                database.child(tableName)
                    .child(cloudId)
                    .setValue(cloudObject)
            )

            // If we reach here, Firebase has successfully written the data.
            Log.e("SyncManager", "CONFIRMED SUCCESS → $tableName/$cloudId")
        } catch (e: Exception) {
            // If Tasks.await() throws an exception (e.g., security denied, or network failure),
            // we log it and throw it again.
            Log.e("FIREBASE_UPLOAD", "CONFIRMED FAILED → $tableName/$cloudId", e)
            throw e // Re-throw to signal failure to the SyncManager/Worker
        }
    }

    // Delete record in Firebase
    fun deleteById(tableName: String, localId: Long) {
        if (!isInitialized || localId <= 0) return
        database.child(tableName).child(localId.toString()).removeValue()
    }

    // --------------------------------------------------------------
    // 3️⃣ Start syncing all cloud → local tables
    // --------------------------------------------------------------
    fun startSyncAllTables(
        brandDao: BrandDao,
        categoryDao: CategoryDao,
        productDao: ProductDao,
        sPDao: SubProductDao,
        dWDao: DetailWarnaDao,
        tSDao: TransSumDao,
        tdDao: TransDetailDao,
        summaryDao: SummaryDbDao
    ) {
        Log.d("SyncManager", "StartSyncAllTablesStarted")
        if (!isInitialized) return
        // category TABLE SYNC
        syncCategory(categoryDao)
        // BRAND TABLE SYNC
        syncBrand(brandDao)
        syncProduct(productDao)
        syncSubProduct(sPDao)
        syncDetailWarna(dWDao)
        syncMerchandiseRetail(dWDao)
        syncTransactionDetail(tdDao)
        syncTransactionSummary(tSDao)

    }
    fun syncBrand(brandDao: BrandDao){
        syncTable(
            tableName = Constants.TABLENAMES.BRAND,
            clazz = BrandCloud::class.java,

            convertAndSave = { cloud, key ->
                if(cloud.isDeleted==false){
                    val brand = Brand(
                        brandCloudId = key.toLong(),     // <-- cloud key is primary ID
                        brand_name = cloud.brandName,    // <-- correct for Brand
                        cath_code = cloud.cathCode.toLong()       // <-- if your Brand table has this
                    )
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            brandDao.insert(brand)           // <-- correct DAO
                        }catch (ex:Exception){
                            Log.e("SyncManager", "Upload failed for brand ${brand.brandCloudId}: ${ex.message}")
                        }

                    }
                }
            },

            deleteLocal = { key ->
                Executors.newSingleThreadExecutor().execute {
                    brandDao.deleteBrand(key.toLong())   // <-- correct delete
                }
            }
        )

    }
    fun syncCategory(categoryDao: CategoryDao){
        // CATEGORY TABLE SYNC
        syncTable(
            tableName = Constants.TABLENAMES.CATEGORY,
            clazz = CategoryCloud::class.java,
            convertAndSave = { cloud, key ->
                Log.i("SyncManager","StartSync ${cloud.categoryName} ${cloud.isDeleted}  $key} ")
                if (cloud.isDeleted==false){
                    val category = Category(
                        categoryCloudId = key.toLong(),
                        category_name = cloud.categoryName,
                        isDeleted = cloud.isDeleted
                    )
                    Executors.newSingleThreadExecutor().execute {
                        categoryDao.insert(category)
                    }
                }else{
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            Log.i("SyncManager","syncCategory convertAndSave executing delete  ${cloud.categoryName} ${key.toLong()}")
                            categoryDao.delete(key.toLong())
                        }catch (e: Exception){
                            Log.i("SyncManager","failed to delete ${cloud.categoryName} ${cloud.isDeleted}  $key} ")
                            Log.i("SyncManager","because $e} ")
                        }

                    }

                }

            },
            deleteLocal = { key ->
                Log.i("SyncManager","StartSync all table syncCategory")
                Executors.newSingleThreadExecutor().execute {
                    Log.i("SyncManager","syncCategory executing delete ${key.toLong()}")
                    categoryDao.delete(key.toLong())
                }
            }
        )


    }
    fun syncProduct(productDao: ProductDao){
        syncTable(
            tableName = Constants.TABLENAMES.PRODUCT,
            clazz = ProductCloud::class.java,
            convertAndSave = { cloud, key ->
        if (cloud.isDeleted==false){
            val product = Product(
                productCloudId = key.toLong(),
                product_name = cloud.productName,
                product_price = cloud.productPrice,
                product_capital = cloud.productCapital,
                checkBoxBoolean = cloud.checkBoxBoolean,
                bestSelling = cloud.bestSelling,
                default_net = cloud.defaultNet,
                alternate_price = cloud.alternatePrice,
                brand_code = cloud.brandCode,
                cath_code = cloud.cathCode,
                discountId = cloud.discountId,
                purchasePrice = cloud.purchasePrice,
                puchaseUnit = cloud.puchaseUnit,
                alternate_capital = cloud.alternateCapital,
                isDeleted = cloud.isDeleted,
                needsSyncs = cloud.needsSyncs
            )

            Executors.newSingleThreadExecutor().execute {
                try {
                    productDao.insert(product) // insert or update
                }catch (e:Exception){
                    Log.e("SyncManager", "Upload failed for product ${product.productCloudId}: ${e.message}"    )

            } }
}

            },
            deleteLocal = { key ->
                Executors.newSingleThreadExecutor().execute {
                    productDao.delete(key.toLong())   // delete from Room
                }
            }
        )

    }
    fun syncSubProduct(subProductDao: SubProductDao) {
        syncTable(
            tableName = Constants.TABLENAMES.SUB_PRODUCT,
            clazz = SubProductCloud::class.java,

            convertAndSave = { cloud, key ->
                if (!cloud.isDeleted) {
                    val subProduct = SubProduct(
                        sPCloudId = key.toLong(),
                        sub_name = cloud.subName,
                        roll_u = cloud.rollU,
                        warna = cloud.warna,
                        ket = cloud.ket,
                        productCloudId = cloud.productCloudId,
                        brand_code = cloud.brandCode,
                        cath_code = cloud.cathCode,
                        is_checked = cloud.isChecked,
                        discountId = cloud.discountId,
                        isDeleted = cloud.isDeleted,
                        needsSyncs = cloud.needsSyncs
                    )
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            subProductDao.insert(subProduct)
                        } catch (ex: Exception) {
                            Log.e(
                                "SyncManager",
                                "Upload failed for subProduct ${subProduct.sPCloudId}: ${ex.message}"
                            )
                        }
                    }
                }else{
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            Log.i("SyncManager","syncCategory convertAndSave executing delete  ${cloud.subName} ${key.toLong()}")
                            subProductDao.delete(key.toLong())
                        }catch (e: Exception){
                            Log.i("SyncManager","failed to delete ${cloud.subName} ${cloud.isDeleted}  $key} ")
                            Log.i("SyncManager","because $e} ")
                        }

                    }

                }
            },

            deleteLocal = { key ->
                Executors.newSingleThreadExecutor().execute {
                    subProductDao.delete(key.toLong())
                }
            }
        )
    }

    fun syncDetailWarna(detailWarnaDao: DetailWarnaDao) {
        syncTable(
            tableName = Constants.TABLENAMES.DETAIL_WARNA,
            clazz = DetailWarnaCloud::class.java,
            convertAndSave = { cloud, key ->
                if (!cloud.isDeleted) {
                    val detailWarna = DetailWarnaTable(
                        dWCloudId = key.toLong(),
                        sPCloudId = cloud.sPCloudId,
                        batchCount = cloud.batchCount,
                        net = cloud.net,
                        ket = cloud.ket,
                        ref = cloud.ref,
                        isDeleted = cloud.isDeleted,
                        needsSyncs = cloud.needsSyncs
                    )
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            detailWarnaDao.insert(detailWarna)
                        } catch (ex: Exception) {
                            Log.e(
                                "SyncManager",
                                "Upload failed for detailWarna ${detailWarna.dWCloudId}: ${ex.message}"
                            )
                        }
                    }
                }else{
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            Log.i("SyncManager","syncCategory convertAndSave executing delete  ${cloud.batchCount} ${cloud.net} ${key.toLong()}")
                            detailWarnaDao.delete(key.toLong())
                        }catch (e: Exception){
                            Log.i("SyncManager","failed to delete ${cloud.batchCount} ${cloud.net} ${cloud.isDeleted}  $key} ")
                            Log.i("SyncManager","because $e} ")
                        }

                    }

                }
            },
            deleteLocal = { key ->
                Executors.newSingleThreadExecutor().execute {
                    detailWarnaDao.delete(key.toLong())
                }
            }
        )
    }

    fun syncMerchandiseRetail(detailWarnaDao: DetailWarnaDao) {
        syncTable(
            tableName = Constants.TABLENAMES.MERCHANDISE_RETAIL,
            clazz = MerchandiseRetailCloud::class.java,
            convertAndSave = { cloud, key ->
                if (!cloud.isDeleted) {
                    val merchandiseRetail = MerchandiseRetail(
                        mRCloudId = key.toLong(),
                        sPCloudId = cloud.sPCloudId,
                        net = cloud.net,
                        ref = cloud.ref,
                        date = Date(cloud.date),
                        isDeleted = cloud.isDeleted,
                        needsSyncs = cloud.needsSyncs
                    )
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            detailWarnaDao.insert(merchandiseRetail)
                        } catch (ex: Exception) {
                            Log.e(
                                "SyncManager",
                                "Upload failed for merchandiseRetail ${merchandiseRetail.mRCloudId}: ${ex.message}"
                            )
                        }
                    }
                }
            },
            deleteLocal = { key ->
                Executors.newSingleThreadExecutor().execute {
                    detailWarnaDao.deleteMerchandise(key.toLong())
                }
            }
        )
    }
    fun syncTransactionDetail(transDetailDao: TransDetailDao) {
        syncTable(
            tableName = Constants.TABLENAMES.TRANSACTION_DETAIL,
            clazz = TransactionDetailCloud::class.java,
            convertAndSave = { cloud, key ->
                if (!cloud.isDeleted) {
                    val transactionDetail = TransactionDetail(
                        tDCloudId = key.toLong(),
                        tSCloudId = cloud.tSCloudId,
                        trans_item_name = cloud.transItemName,
                        qty = cloud.qty,
                        trans_price = cloud.transPrice,
                        total_price = cloud.totalPrice,
                        is_prepared = cloud.isPrepared,
                        is_cutted = cloud.isCutted,
                        trans_detail_date = cloud.transDetailDate?.let { Date(it) },
                        unit = cloud.unit,
                        unit_qty = cloud.unitQty,
                        item_position = cloud.itemPosition,
                        sPCloudId = cloud.sPCloudId,
                        product_capital = cloud.productCapital,
                        isDeleted = cloud.isDeleted,
                        needsSyncs = cloud.needsSyncs
                    )
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            transDetailDao.insert(transactionDetail)
                        } catch (ex: Exception) {
                            Log.e(
                                "SyncManager",
                                "Upload failed for transactionDetail ${transactionDetail.tDCloudId}: ${ex.message}"
                            )
                        }
                    }
                }else{
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            Log.i("SyncManager","syncCategory convertAndSave executing delete  ${cloud.transItemName} ${key.toLong()}")
                            transDetailDao.deleteAnItemTransDetail(key.toLong())
                        }catch (e: Exception){
                            Log.i("SyncManager","failed to delete ${cloud.transItemName} ${cloud.isDeleted}  $key} ")
                            Log.i("SyncManager","because $e} ")
                        }

                    }

                }
            },
            deleteLocal = { key ->
                Executors.newSingleThreadExecutor().execute {
                    transDetailDao.deleteAnItemTransDetail(key.toLong())
                }
            }
        )
    }

    fun syncTransactionSummary(transSumDao: TransSumDao) {
        syncTable(
            tableName = Constants.TABLENAMES.TRANSACTION_SUMMARY,
            clazz = TransactionSummaryCloud::class.java,
            convertAndSave = { cloud, key ->
                if (!cloud.isDeleted) {
                    val transactionSummary = TransactionSummary(
                        tSCloudId = key.toLong(),
                        cust_name = cloud.custName,
                        total_trans = cloud.totalTrans,
                        total_after_discount = cloud.totalAfterDiscount,
                        paid = cloud.paid,
                        trans_date = cloud.transDate?.let { Date(it) },
                        is_taken_ = cloud.isTaken,
                        is_paid_off = cloud.isPaidOff,
                        is_keeped = cloud.isKeeped,
                        is_logged = cloud.isLogged,
                        ref = cloud.ref,
                        sum_note = cloud.sumNote,
                        custId = cloud.custId,
                        isDeleted = cloud.isDeleted,
                        needsSyncs = cloud.needsSyncs
                    )
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            transSumDao.insertNew(transactionSummary)
                        } catch (ex: Exception) {
                            Log.e(
                                "SyncManager",
                                "Upload failed for transactionSummary ${transactionSummary.tSCloudId}: ${ex.message}"
                            )
                        }
                    }
                }else{
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            Log.i("SyncManager","syncCategory convertAndSave executing delete  ${cloud.custName} ${key.toLong()}")
                            transSumDao.deleteById(key.toLong())
                        }catch (e: Exception){
                            Log.i("SyncManager","failed to delete ${cloud.custName} ${cloud.isDeleted}  $key} ")
                            Log.i("SyncManager","because $e} ")
                        }

                    }

                }
            },
            deleteLocal = { key ->
                Executors.newSingleThreadExecutor().execute {
                    val summaryToDelete = TransactionSummary(tSCloudId = key.toLong())
                    transSumDao.delete_(summaryToDelete)
                }
            }
        )
    }

    // --------------------------------------------------------------
    // 4️⃣ Generic table listener (cloud → local)
    // --------------------------------------------------------------
    // Inside RealtimeDatabaseSync.kt
// --------------------------------------------------------------
// 4️⃣ Generic table listener (cloud → local)
// --------------------------------------------------------------
    // RealtimeDatabaseSync.kt (Modified section)

    private fun <T> syncTable(
        tableName: String,
        clazz: Class<T>,
        convertAndSave: (T, String) -> Unit,
        deleteLocal: (String) -> Unit
    ) {
        database.child(tableName).addChildEventListener(object : ChildEventListener {

            // 1. MUST BE IMPLEMENTED
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(clazz)?.let { data ->
                    convertAndSave(data, snapshot.key!!)
                }
            }

            // 2. MUST BE IMPLEMENTED
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(clazz)?.let { data ->
                    val key = snapshot.key!!
                    Log.w("SyncManager", "OnChildChanged")
                    if (data is CategoryCloud) {
                        if (data.isDeleted) {
                            Log.d("SyncManager", "Soft-deleted in cloud: $tableName → $key. Deleting locally.")
                            deleteLocal(key) // Hard-delete from Room

                        } else {
                            convertAndSave(data, key)
                        }
                    } else {
                        convertAndSave(data, key)
                    }
                }
            }

            // 3. MUST BE IMPLEMENTED
            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("RDBSync", "Deleted from cloud: $tableName → ${snapshot.key}")
                snapshot.key?.let { deleteLocal(it) }
            }

            // 4. MUST BE IMPLEMENTED (can be empty)
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Leave empty if you don't use ordered data
            }

            // 5. MUST BE IMPLEMENTED (can be empty)
            override fun onCancelled(error: DatabaseError) {
                Log.e("RDBSync", "Database sync cancelled for $tableName.", error.toException())
            }
        })
    }
// 📁 RealtimeDatabaseSync.kt (Updated)

// ... existing code (init, upload, deleteById, startSyncAllTables, syncTable) ...

    // --------------------------------------------------------------
// 5️⃣ Start a Firebase Connection Status Listener (New Function)
// --------------------------------------------------------------
    fun startConnectionListener(context: Context) {
        Log.d("SyncManager", "Reconnected to Firebase! Scheduling sync.")
        if (!isInitialized) return

        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    Log.d("RDBSync", "Reconnected to Firebase! Scheduling sync.")

                    scheduleImmediateSync(context) // <-- Your code snippet is inside here
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RDBSync", "Connection listener cancelled.", error.toException())
            }
        })
    }
}
