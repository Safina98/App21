// RealtimeDatabaseSync.kt
package com.example.app21try6.database.cloud

import android.content.Context
import android.util.Log
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.Category
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
        categoryDao: CategoryDao
    ) {
        if (!isInitialized) return

        // BRAND TABLE SYNC
        syncTable(
            tableName = "brand_table",
            clazz = BrandCloud::class.java
        ) { cloud, key ->

            val brand = Brand(
                brandCloudId = key.toLong(),
                brand_name = cloud.brandName,
                cath_code = cloud.cathCode
            )

            Executors.newSingleThreadExecutor().execute {
                brandDao.insert(brand)  // insert or update
            }
        }

        // CATEGORY TABLE SYNC
        syncTable(
            tableName = "category_table",
            clazz = CategoryCloud::class.java
        ) { cloud, key ->

            val category = Category(
                categoryCloudId = key.toLong(),
                category_name = cloud.categoryName
            )

            Executors.newSingleThreadExecutor().execute {
                categoryDao.insert(category)  // insert or update
            }
        }
    }

    // --------------------------------------------------------------
    // 4️⃣ Generic table listener (cloud → local)
    // --------------------------------------------------------------
    private fun <T> syncTable(
        tableName: String,
        clazz: Class<T>,
        convertAndSave: (T, String) -> Unit
    ) {
        database.child(tableName).addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(clazz)?.let { data ->
                    convertAndSave(data, snapshot.key!!)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                snapshot.getValue(clazz)?.let { data ->
                    convertAndSave(data, snapshot.key!!)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("RDBSync", "Deleted from cloud: $tableName → ${snapshot.key}")
                // Optional: delete locally too if needed
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("RDBSync", "Sync cancelled for $tableName", error.toException())
            }
        })
    }
}
