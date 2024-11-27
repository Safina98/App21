package com.example.app21try6.stock.inventorylog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.app21try6.database.daos.InventoryLogDao
import com.example.app21try6.database.daos.SubProductDao

class InventoryLogViewModel(
    private val subProductDao: SubProductDao,
    private val inventoryLogDao: InventoryLogDao,
    private val application: Application
): AndroidViewModel(application) {
    val allLogFromD= inventoryLogDao.selectAllInventoryLogLD()

}