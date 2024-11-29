package com.example.app21try6.stock.inventorylog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.daos.InventoryLogDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.models.InventoryLogWithSubProduct
import com.example.app21try6.database.tables.InventoryLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryLogViewModel(
    private val subProductDao: SubProductDao,
    private val inventoryLogDao: InventoryLogDao,
    private val application: Application
): AndroidViewModel(application) {
    val allLogFromD= inventoryLogDao.selectAllInventoryLogLD()

    fun deleteLog(log:InventoryLogWithSubProduct){
        viewModelScope.launch {
            deleteLogToDao(log)
        }
    }
    fun updateLog(log: InventoryLogWithSubProduct){
        viewModelScope.launch {
            updateLogToDao(log)
        }
    }
    private suspend fun updateLogToDao(log:InventoryLogWithSubProduct){
        withContext(Dispatchers.IO){
            inventoryLogDao.updateLogAndDetailWarna(log)
        }
    }
    private suspend fun deleteLogToDao(log:InventoryLogWithSubProduct){
        withContext(Dispatchers.IO){
            inventoryLogDao.deleteLogAndUpdateDetailWarna(log)
        }
    }

}