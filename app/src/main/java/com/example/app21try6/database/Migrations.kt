package com.example.app21try6.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
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