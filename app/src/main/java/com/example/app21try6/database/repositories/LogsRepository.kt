package com.example.app21try6.database.repositories

import android.app.Application
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.tables.InventoryLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogsRepository(application: Application) {
    private val inventoryLogDao = VendibleDatabase.getInstance(application).inventoryLogDao

    suspend fun assignCloudIdToInventoryLogTable(cloudId: Long, id: Int){
        withContext(Dispatchers.IO){
            inventoryLogDao.assignInventoryLogCloudID(cloudId, id)
        }
    }

    suspend fun getAllInventoryLogTable(): List<InventoryLog> {
        return withContext(Dispatchers.IO) {
            inventoryLogDao.selectAllInventoryLogTable()
        }
    }
}