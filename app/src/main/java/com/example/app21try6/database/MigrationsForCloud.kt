package com.example.app21try6.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MigrationsForCloud {

    val MIGRATION_49_50 = object : Migration(49, 50) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // -----------------------------------
            // 2. TransactionDetail Migration
            // -----------------------------------
            database.execSQL("ALTER TABLE trans_detail_table RENAME TO old_trans_detail_table")
            database.execSQL("""
                CREATE TABLE trans_detail_table (
                    tDCloudId INTEGER NOT NULL PRIMARY KEY,
                    sum_id INTEGER NOT NULL,
                    trans_item_name TEXT NOT NULL,
                    qty REAL NOT NULL,
                    trans_price INTEGER NOT NULL,
                    total_price REAL NOT NULL,
                    is_prepared INTEGER NOT NULL,
                    is_cutted INTEGER NOT NULL,
                    trans_detail_date TEXT,
                    unit TEXT,
                    unit_qty REAL NOT NULL,
                    item_position INTEGER NOT NULL,
                    sub_id INTEGER,
                    product_capital INTEGER NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(sum_id) REFERENCES trans_sum_table(sum_id) ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY(sub_id) REFERENCES sub_table(sub_id) ON DELETE SET NULL ON UPDATE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO trans_detail_table (tDCloudId, sum_id, trans_item_name, qty, trans_price, total_price, is_prepared, is_cutted, trans_detail_date, unit, unit_qty, item_position, sub_id, product_capital, is_deleted, needs_syncs)
                SELECT tDCloudId, sum_id, trans_item_name, qty, trans_price, total_price, is_prepared, is_cutted, trans_detail_date, unit, unit_qty, item_position, sub_id, product_capital, is_deleted, needs_syncs
                FROM old_trans_detail_table
            """.trimIndent())
            database.execSQL("DROP TABLE old_trans_detail_table")


        }
    }
}