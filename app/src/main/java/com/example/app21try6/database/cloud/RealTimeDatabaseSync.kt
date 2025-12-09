// RealtimeDatabaseSync.kt
package com.example.app21try6.database.cloud

import android.content.Context
import android.util.Log
import com.example.app21try6.Constants
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.Category
import com.example.app21try6.database.tables.Product
import com.example.app21try6.scheduleImmediateSync
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.*
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
        productDao: ProductDao
    ) {
        Log.d("SyncManager", "StartSyncAllTablesStarted")
        if (!isInitialized) return

        // BRAND TABLE SYNC
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

        // CATEGORY TABLE SYNC
        syncTable(
            tableName = Constants.TABLENAMES.CATEGORY,
            clazz = CategoryCloud::class.java,
            convertAndSave = { cloud, key ->
                Log.i("SyncManager","StartSync ${cloud.categoryName} ${cloud.isDeleted} ")
                if (cloud.isDeleted==false){
                    val category = Category(
                        categoryCloudId = key.toLong(),
                        category_name = cloud.categoryName
                    )
                    Executors.newSingleThreadExecutor().execute {
                        categoryDao.insert(category)
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

        syncProduct(productDao)


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

                    // **NEW LOGIC FOR SOFT-DELETE CHECK**
                    // You need to figure out how to check the 'isDeleted' flag in the generic T
                    // Since this is generic, you'll need to cast or use a common interface/abstract class.
                    // Assuming T is CategoryCloud (for CATEGORY_TABLE):
                    if (data is CategoryCloud) {
                        if (data.isDeleted) {
                            Log.d("SyncManager", "Soft-deleted in cloud: $tableName → $key. Deleting locally.")
                            deleteLocal(key) // Hard-delete from Room
                            // OPTIONAL: Clean up the cloud data by REMOVING the node after
                            // all devices have synced the soft-delete marker.
                            // This is complex and often skipped. For simplicity, just leave
                            // the soft-deleted node in the cloud.

                        } else {
                            // Regular update/change
                            convertAndSave(data, key)
                        }
                    } else {
                        // Fallback for non-soft-delete tables or generic T
                        //convertAndSave(data, key)
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
