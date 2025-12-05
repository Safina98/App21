package com.example.app21try6.database.repositories

import android.app.Application
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.database.tables.InventoryLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LogsRepository(application: Application)  {
    private val inventoryLog= VendibleDatabase.getInstance(application).inventoryLogDao

}