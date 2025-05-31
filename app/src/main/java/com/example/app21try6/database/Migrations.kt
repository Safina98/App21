package com.example.app21try6.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {


    val MIGRATION_43_44 = object : Migration(43, 44) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Step 1: Migrate `merchandise_table` (rename subProductNet to net)
            database.execSQL("""
            CREATE TABLE merchandise_table_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                sub_id INTEGER NOT NULL,
                net REAL NOT NULL,
                ref TEXT NOT NULL,
                date TEXT NOT NULL,
                FOREIGN KEY(sub_id) REFERENCES sub_table(sub_id) ON DELETE CASCADE ON UPDATE CASCADE
            )
        """.trimIndent())

            database.execSQL("""
            INSERT INTO merchandise_table_new (id, sub_id, net, ref, date)
            SELECT id, sub_id, subProductNet, ref, date FROM merchandise_table
        """.trimIndent())

            database.execSQL("DROP TABLE merchandise_table")
            database.execSQL("ALTER TABLE merchandise_table_new RENAME TO merchandise_table")

            // Step 2: Migrate `detail_warna_table` (rename detailId to id)
            database.execSQL("""
            CREATE TABLE detail_warna_table_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                subId INTEGER NOT NULL,
                batchCount REAL NOT NULL,
                net REAL NOT NULL,
                ket TEXT NOT NULL,
                ref TEXT NOT NULL,
                FOREIGN KEY(subId) REFERENCES sub_table(sub_id) ON DELETE CASCADE ON UPDATE CASCADE
            )
        """.trimIndent())

            database.execSQL("""
            INSERT INTO detail_warna_table_new (id, subId, batchCount, net, ket, ref)
            SELECT detailId, subId, batchCount, net, ket, ref FROM detail_warna_table
        """.trimIndent())

            database.execSQL("DROP TABLE detail_warna_table")
            database.execSQL("ALTER TABLE detail_warna_table_new RENAME TO detail_warna_table")

            // Recreate the unique index on `ref`
            database.execSQL("""
            CREATE UNIQUE INDEX index_detail_warna_table_ref ON detail_warna_table(ref)
        """.trimIndent())
        }
    }


    //note
    // Migration 41_41 is the same. Just forgot to add entity and dao to vendible databse hence increment the version
    val MIGRATION_42_43 = object : Migration(42, 43) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create the new merchandise_table
            database.execSQL("""
            CREATE TABLE IF NOT EXISTS merchandise_table (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                sub_id INTEGER NOT NULL,
                subProductNet REAL NOT NULL,
                ref TEXT NOT NULL,
                date TEXT NOT NULL,
                FOREIGN KEY(sub_id) REFERENCES sub_table(sub_id) 
                    ON UPDATE CASCADE ON DELETE CASCADE
            )
        """.trimIndent())

            // Add is_logged column to trans_sum_table
           /*
            database.execSQL("""
            ALTER TABLE trans_sum_table ADD COLUMN is_logged INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            */
        }
    }


    val MIGRATION_40_41 = object : Migration(40, 41) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add the new column with a default value of 0.0
            database.execSQL("ALTER TABLE trans_sum_table ADD COLUMN total_after_discount REAL NOT NULL DEFAULT 0.0")
        }
    }
    val MIGRATION_39_TO_40 = object : Migration(39, 40) { // Replace X and Y with the appropriate schema version numbers
        override fun migrate(database: SupportSQLiteDatabase) {
            // Step 1: Add the new column `alternate_capital` to the product_table
            database.execSQL("ALTER TABLE product_table ADD COLUMN alternate_capital REAL NOT NULL DEFAULT 0.0")

            // Step 2: Copy the value of `alternate_price` to `alternate_capital`
            database.execSQL("UPDATE product_table SET alternate_capital = alternate_price")

            // Step 3: Set the value of `alternate_price` to 0
            database.execSQL("UPDATE product_table SET alternate_price = 0.0")
        }
    }


    val MIGRATION_38_39 = object : Migration(38, 39) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add the purchasePrice and purchaseUnit columns to product_table
            database.execSQL("ALTER TABLE product_table ADD COLUMN purchasePrice INTEGER")
            database.execSQL("ALTER TABLE product_table ADD COLUMN purchaseUnit TEXT")

            // Create new trans_sum_table with updated schema
            database.execSQL(
                """
            CREATE TABLE trans_sum_table_new (
                sum_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                cust_name TEXT NOT NULL,
                total_trans REAL NOT NULL,
                paid INTEGER NOT NULL,
                trans_date TEXT,
                is_taken INTEGER NOT NULL,
                is_paid_off INTEGER NOT NULL,
                is_keeped INTEGER NOT NULL,
                ref TEXT NOT NULL,
                sum_note TEXT,
                custId INTEGER,
                FOREIGN KEY(custId) REFERENCES customer_table(custId) ON UPDATE CASCADE ON DELETE SET NULL
            )
            """.trimIndent()
            )

            // Copy data to the new trans_sum_table
            database.execSQL(
                """
            INSERT INTO trans_sum_table_new (
                sum_id, cust_name, total_trans, paid, trans_date, 
                is_taken, is_paid_off, is_keeped, ref, sum_note, custId
            )
            SELECT 
                sum_id, cust_name, total_trans, paid, trans_date, 
                is_taken, is_paid_off, is_keeped, ref, sum_note, custId
            FROM trans_sum_table
            """
            )

            // Replace old trans_sum_table with the new one
            database.execSQL("DROP TABLE trans_sum_table")
            database.execSQL("ALTER TABLE trans_sum_table_new RENAME TO trans_sum_table")

            // Create new summary_table with updated schema
            database.execSQL(
                """
            CREATE TABLE summary_table_new (
                id_m INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                year INTEGER NOT NULL,
                month TEXT NOT NULL,
                month_number INTEGER NOT NULL,
                day INTEGER NOT NULL,
                day_name TEXT NOT NULL,
                date TEXT,
                item_name TEXT NOT NULL,
                item_sold REAL NOT NULL,
                price REAL NOT NULL,
                total_income REAL NOT NULL,
                product_id INTEGER,
                sub_id INTEGER,
                product_capital INTEGER NOT NULL,
                FOREIGN KEY(product_id) REFERENCES product_table(product_id) ON UPDATE SET NULL ON DELETE CASCADE,
                FOREIGN KEY(sub_id) REFERENCES sub_table(sub_id) ON UPDATE SET NULL ON DELETE CASCADE
            )
            """.trimIndent()
            )

            // Copy data to the new summary_table
            database.execSQL(
                """
            INSERT INTO summary_table_new (
                id_m, year, month, month_number, day, day_name, 
                date, item_name, item_sold, price, total_income, 
                product_id, sub_id, product_capital
            )
            SELECT 
                id_m, year, month, month_number, day, day_name, 
                date, item_name, item_sold, price, total_income, 
                product_id, sub_id, product_capital
            FROM summary_table
            """
            )

            // Replace old summary_table with the new one
            database.execSQL("DROP TABLE summary_table")
            database.execSQL("ALTER TABLE summary_table_new RENAME TO summary_table")

            // Add indices for product_id and sub_id
            database.execSQL("CREATE INDEX index_summary_table_product_id ON summary_table(product_id)")
            database.execSQL("CREATE INDEX index_summary_table_sub_id ON summary_table(sub_id)")
        }
    }


}