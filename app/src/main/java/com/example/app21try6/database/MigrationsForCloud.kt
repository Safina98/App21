package com.example.app21try6.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MigrationsForCloud {

    val MIGRATION_50_51 = object : Migration(50, 51) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // ----------------------------------- 
            // 1. MIGRATE trans_sum_table FIRST
            // ----------------------------------- 
            database.execSQL("ALTER TABLE trans_sum_table RENAME TO old_trans_sum_table")
            database.execSQL("""
                CREATE TABLE trans_sum_table (
                    tSCloudId INTEGER NOT NULL PRIMARY KEY,
                    cust_name TEXT NOT NULL,
                    total_trans REAL NOT NULL,
                    total_after_discount REAL NOT NULL,
                    paid INTEGER NOT NULL,
                    trans_date TEXT,
                    is_taken INTEGER NOT NULL,
                    is_paid_off INTEGER NOT NULL,
                    is_keeped INTEGER NOT NULL,
                    is_logged INTEGER NOT NULL,
                    ref TEXT NOT NULL,
                    sum_note TEXT,
                    custId INTEGER,
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(custId) REFERENCES customer_table(custId) ON UPDATE CASCADE ON DELETE SET NULL
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO trans_sum_table (
                    tSCloudId, cust_name, total_trans, total_after_discount, paid, trans_date, 
                    is_taken, is_paid_off, is_keeped, is_logged, ref, sum_note, custId, is_deleted, needs_syncs
                )
                SELECT 
                    tSCloudId, cust_name, total_trans, total_after_discount, paid, trans_date, 
                    is_taken, is_paid_off, is_keeped, is_logged, ref, sum_note, custId, is_deleted,  needs_syncs
                FROM old_trans_sum_table
            """.trimIndent())

            // ----------------------------------- 
            // 2. MIGRATE paymen_table
            // ----------------------------------- 
            database.execSQL("ALTER TABLE paymen_table RENAME TO old_paymen_table")
            database.execSQL("""
                CREATE TABLE payment_table (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    tSCloudId INTEGER NOT NULL,
                    payment_ammount INTEGER NOT NULL,
                    payment_date TEXT,
                    ref TEXT NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    paymentCloudId INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(tSCloudId) REFERENCES trans_sum_table(tSCloudId) ON DELETE CASCADE ON UPDATE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO payment_table (id, tSCloudId, payment_ammount, payment_date, ref, is_deleted, paymentCloudId,needs_syncs)
                SELECT p.id, ots.tSCloudId, p.payment_ammount, p.payment_date, p.ref, p.is_deleted, paymentCloudId,p.needs_syncs
                FROM old_paymen_table AS p
                JOIN old_trans_sum_table AS ots ON p.sum_id = ots.sum_id
            """.trimIndent())
            database.execSQL("DROP TABLE old_paymen_table")

            // ----------------------------------- 
            // 3. MIGRATE trans_detail_table
            // ----------------------------------- 
            database.execSQL("ALTER TABLE trans_detail_table RENAME TO old_trans_detail_table")
            database.execSQL("""
                CREATE TABLE trans_detail_table (
                    tDCloudId INTEGER NOT NULL PRIMARY KEY,
                    tSCloudId INTEGER NOT NULL,
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
                    FOREIGN KEY(tSCloudId) REFERENCES trans_sum_table(tSCloudId) ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY(sub_id) REFERENCES sub_table(sub_id) ON DELETE SET NULL ON UPDATE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO trans_detail_table (tDCloudId, tSCloudId, trans_item_name, qty, trans_price, total_price, is_prepared, is_cutted, trans_detail_date, unit, unit_qty, item_position, sub_id, product_capital, is_deleted, needs_syncs)
                SELECT td.tDCloudId, ots.tSCloudId, td.trans_item_name, td.qty, td.trans_price, td.total_price, td.is_prepared, td.is_cutted, td.trans_detail_date, td.unit, td.unit_qty, td.item_position, td.sub_id, td.product_capital, td.is_deleted, td.needs_syncs
                FROM old_trans_detail_table AS td
                JOIN old_trans_sum_table AS ots ON td.sum_id = ots.sum_id
            """.trimIndent())
            database.execSQL("DROP TABLE old_trans_detail_table")

            // ----------------------------------- 
            // 4. MIGRATE discout_transaction_table
            // ----------------------------------- 
            database.execSQL("ALTER TABLE discout_transaction_table RENAME TO old_discout_transaction_table")
            database.execSQL("""
                CREATE TABLE discout_transaction_table (
                    discTransId INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    discTransRef TEXT NOT NULL,
                    discountId INTEGER,
                    tSCloudId INTEGER NOT NULL,
                    discTransDate TEXT NOT NULL,
                    discTransName TEXT NOT NULL,
                    discountAppliedValue REAL NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    dTCloudId INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(discountId) REFERENCES discount_table(discountId) ON UPDATE CASCADE ON DELETE SET NULL,
                    FOREIGN KEY(tSCloudId) REFERENCES trans_sum_table(tSCloudId) ON UPDATE CASCADE ON DELETE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO discout_transaction_table (discTransId, discTransRef, discountId, tSCloudId, discTransDate, discTransName, discountAppliedValue, is_deleted, dTCloudId, needs_syncs)
                SELECT dt.discTransId, dt.discTransRef, dt.discountId, ots.tSCloudId, dt.discTransDate, dt.discTransName, dt.discountAppliedValue, dt.is_deleted, dTCloudId,dt.needs_syncs
                FROM old_discout_transaction_table AS dt
                JOIN old_trans_sum_table AS ots ON dt.sum_id = ots.sum_id
            """.trimIndent())
            database.execSQL("DROP TABLE old_discout_transaction_table")

            // -----------------------------------
            // 5. MIGRATE expenses_table
            // -----------------------------------
            database.execSQL("ALTER TABLE expenses_table RENAME TO old_expenses_table")
            database.execSQL("""
                CREATE TABLE expenses_table (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    tSCloudId INTEGER,
                    expense_category_id INTEGER NOT NULL,
                    expense_name TEXT NOT NULL,
                    expense_ammount INTEGER,
                    expense_date TEXT,
                    expense_ref TEXT NOT NULL,
                    is_keeped INTEGER NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    expenseCloudId INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(tSCloudId) REFERENCES trans_sum_table(tSCloudId) ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY(expense_category_id) REFERENCES expense_category_table(id) ON DELETE CASCADE ON UPDATE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO expenses_table (id, tSCloudId, expense_category_id, expense_name, expense_ammount, expense_date, expense_ref, is_keeped, is_deleted, expenseCloudId, needs_syncs)
                SELECT e.id, ots.tSCloudId, e.expense_category_id, e.expense_name, e.expense_ammount, e.expense_date, e.expense_ref, e.is_keeped, e.is_deleted, e.expenseCloudId, e.needs_syncs
                FROM old_expenses_table AS e
                LEFT JOIN old_trans_sum_table AS ots ON e.sum_id = ots.sum_id
            """.trimIndent())
            database.execSQL("DROP TABLE old_expenses_table")

            // ----------------------------------- 
            // 6. DROP old_trans_sum_table
            // ----------------------------------- 
            database.execSQL("DROP TABLE old_trans_sum_table")
        }
    }
}
