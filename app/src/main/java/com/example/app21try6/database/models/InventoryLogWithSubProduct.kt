package com.example.app21try6.database.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.SubProduct

data class InventoryLogWithSubProduct (

    @Embedded val inventoryLog: InventoryLog,
    @ColumnInfo(name="sub_name") var sub_name:String = "",
)