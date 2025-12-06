package com.example.app21try6.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MigrationsForCloud {

    val MIGRATION_53_54 = object : Migration(53, 54) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // ----------------------------------- 
            // 1. MIGRATE DetailWarnaTable
            // ----------------------------------- 
            database.execSQL("ALTER TABLE detail_warna_table RENAME TO old_detail_warna_table")
            database.execSQL("""
                CREATE TABLE detail_warna_table (
                    dWCloudId INTEGER NOT NULL PRIMARY KEY,
                    sPCloudId INTEGER NOT NULL,
                    batchCount REAL NOT NULL,
                    net REAL NOT NULL,
                    ket TEXT NOT NULL,
                    ref TEXT NOT NULL UNIQUE,
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(sPCloudId) REFERENCES sub_table(sPCloudId) ON UPDATE CASCADE ON DELETE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO detail_warna_table (dWCloudId, sPCloudId, batchCount, net, ket, ref, is_deleted, needs_syncs)
                SELECT dWCloudId, sPCloudId, batchCount, net, ket, ref, is_deleted, needs_syncs
                FROM old_detail_warna_table
            """.trimIndent())
            database.execSQL("DROP TABLE old_detail_warna_table")
            database.execSQL("""
                CREATE UNIQUE INDEX index_detail_warna_table_ref
                ON detail_warna_table (ref)
            """)

            // ----------------------------------- 
            // 2. MIGRATE MerchandiseRetail
            // ----------------------------------- 
            database.execSQL("ALTER TABLE merchandise_table RENAME TO old_merchandise_table")
            database.execSQL("""
                CREATE TABLE merchandise_table (
                    mRCloudId INTEGER NOT NULL PRIMARY KEY,
                    sPCloudId INTEGER NOT NULL,
                    net REAL NOT NULL,
                    ref TEXT NOT NULL,
                    date TEXT NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(sPCloudId) REFERENCES sub_table(sPCloudId) ON DELETE CASCADE ON UPDATE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO merchandise_table (mRCloudId, sPCloudId, net, ref, date, is_deleted, needs_syncs)
                SELECT mRCloudId, sPCloudId, net, ref, date, is_deleted, needs_syncs
                FROM old_merchandise_table
            """.trimIndent())
            database.execSQL("DROP TABLE old_merchandise_table")
            database.execSQL("""
                CREATE UNIQUE INDEX index_merchandise_table_table_ref
                ON merchandise_table (ref)
            """)
            

        }
    }
    
    // ... other migrations
}
