// RealtimeDatabaseSync.kt
package com.example.app21try6.database.cloud

import android.content.Context
import android.util.Log
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.Category
import com.google.firebase.database.*
import com.google.firebase.database.DataSnapshot
import java.util.concurrent.Executors

object RealtimeDatabaseSync {
    private lateinit var database: DatabaseReference
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized) return

        // FIRST: Enable persistence BEFORE any getInstance() call
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // SECOND: Now safe to get the reference
        val correctUrl="https://onlineapp21-2679d-default-rtdb.asia-southeast1.firebasedatabase.app/"
        database = FirebaseDatabase.getInstance(correctUrl).reference
        isInitialized = true
        Log.e("RDBSync", "Firebase forced to: $correctUrl")
    //Log.d("RDBSync", "Firebase initialized with persistence")
    }

    // Upload function – call this after every local insert/update
    fun <T> upload(tableName: String, localId: Int, cloudObject: T) {
        if (!isInitialized || localId <= 0) return

        Log.e("FIREBASE_UPLOAD", "Trying to upload $tableName / $localId")

        database.child(tableName).child(localId.toString()).setValue(cloudObject)
            .addOnSuccessListener { Log.e("FIREBASE_UPLOAD", "SUCCESS → $tableName/$localId") }
            .addOnFailureListener { e -> Log.e("FIREBASE_UPLOAD", "FAILED → $tableName/$localId", e) }
    }
    fun deleteById(tableName: String, localId: Int) {
        if (!isInitialized || localId <= 0) return
        database.child(tableName).child(localId.toString()).removeValue()
    }
    // Start listening for changes from other devices
    fun startListening() {
        if (!isInitialized) return

        // Example for Brand table – add more later
        listenToTable("brand_table", BrandCloud::class.java) { cloud, key ->
            // Convert cloud → local Brand and save to Room
            val localBrand = Brand(
                brand_id = key.toInt(),
                brand_name = cloud.brandName,
                cath_code = cloud.cathCode
            )

            // TODO: Call your BrandDao.insertOrUpdate(localBrand)
            Log.d("RDBSync", "Received brand: ${localBrand.brand_name}")
        }

        // Add listeners for other tables here later
    }

    private fun <T> listenToTable(
        tableName: String,
        clazz: Class<T>,
        onChildChanged: (T, String) -> Unit
    ) {
        database.child(tableName).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val obj = snapshot.getValue(clazz)
                obj?.let { onChildChanged(it, snapshot.key!!) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val obj = snapshot.getValue(clazz)
                obj?.let { onChildChanged(it, snapshot.key!!) }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Handle deletion if needed
                Log.d("RDBSync", "Child removed from $tableName: ${snapshot.key}")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("RDBSync", "Listener cancelled for $tableName", error.toException())
            }
        })
    }
    // RealtimeDatabaseSync.kt  ← add inside the object

    fun startSyncAllFourTables(brandDao: BrandDao, categoryDao: CategoryDao) {

        syncTable("brand_table", BrandCloud::class.java) { cloud, key ->
            val brand = Brand(
                brand_id = key.toInt(),
                brand_name = cloud.brandName,
                cath_code = cloud.cathCode
            )
            Executors.newSingleThreadExecutor().execute {
                brandDao.insert(brand)
            }
                // create or update
        }

        syncTable("category_table", CategoryCloud::class.java) { cloud, key ->
            val category = Category(
                category_id = key.toInt(),
                category_name = cloud.categoryName
            )

            Executors.newSingleThreadExecutor().execute {
                categoryDao.insert(category)
            }

        }



    }

    private fun <T> syncTable(tableName: String, clazz: Class<T>, convert: (T, String) -> Unit) {
        database.child(tableName).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(clazz)?.let { convert(it, snapshot.key!!) }
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(clazz)?.let { convert(it, snapshot.key!!) }
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Optional: delete locally too
                // val id = snapshot.key!!.toInt()
                // when(tableName) { "brands" -> brandDao.deleteById(id) ... }
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("RDBSync", "Sync failed $tableName: ${error.message}")
            }
        })
    }
}