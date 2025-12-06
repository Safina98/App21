package com.example.app21try6.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MigrationsForCloud {

    val MIGRATION_52_53 = object : Migration(52, 53) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // ----------------------------------- 
            // 1. MIGRATE sub_table FIRST
            // ----------------------------------- 
            database.execSQL("ALTER TABLE sub_table RENAME TO old_sub_table")
            database.execSQL("""
                CREATE TABLE sub_table (
                    sPCloudId INTEGER NOT NULL PRIMARY KEY,
                    sub_name TEXT NOT NULL,
                    roll_u INTEGER NOT NULL,
                    warna TEXT NOT NULL,
                    ket TEXT NOT NULL,
                    productCloudId INTEGER NOT NULL,
                    brand_code INTEGER NOT NULL,
                    cath_code INTEGER NOT NULL,
                    is_checked INTEGER NOT NULL,
                    discountId INTEGER,
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(productCloudId) REFERENCES product_table(productCloudId) ON UPDATE CASCADE ON DELETE CASCADE,
                    FOREIGN KEY(brand_code) REFERENCES brand_table(brandCloudId) ON UPDATE CASCADE ON DELETE CASCADE,
                    FOREIGN KEY(cath_code) REFERENCES category_table(categoryCloudId) ON UPDATE CASCADE ON DELETE CASCADE,
                    FOREIGN KEY(discountId) REFERENCES discount_table(discountId) ON UPDATE CASCADE ON DELETE SET NULL
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO sub_table (
                    sPCloudId, sub_name, roll_u, warna, ket, productCloudId, brand_code, 
                    cath_code, is_checked, discountId, is_deleted, needs_syncs
                )
                SELECT 
                    sPCloudId, sub_name, roll_u, warna, ket, productCloudId, brand_code, 
                    cath_code, is_checked, discountId, is_deleted, needs_syncs
                FROM old_sub_table
            """.trimIndent())

            // ----------------------------------- 
            // 2. MIGRATE trans_detail_table
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
                    sPCloudId INTEGER,
                    product_capital INTEGER NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(tSCloudId) REFERENCES trans_sum_table(tSCloudId) ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY(sPCloudId) REFERENCES sub_table(sPCloudId) ON DELETE SET NULL ON UPDATE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO trans_detail_table (tDCloudId, tSCloudId, trans_item_name, qty, trans_price, total_price, is_prepared, is_cutted, trans_detail_date, unit, unit_qty, item_position, sPCloudId, product_capital, is_deleted, needs_syncs)
                SELECT td.tDCloudId, td.tSCloudId, td.trans_item_name, td.qty, td.trans_price, td.total_price, td.is_prepared, td.is_cutted, td.trans_detail_date, td.unit, td.unit_qty, td.item_position, os.sPCloudId, td.product_capital, td.is_deleted, td.needs_syncs
                FROM old_trans_detail_table AS td
                LEFT JOIN old_sub_table AS os ON td.sub_id = os.sub_id
            """.trimIndent())
            database.execSQL("DROP TABLE old_trans_detail_table")

            // ----------------------------------- 
            // 3. MIGRATE summary_table
            // ----------------------------------- 
            database.execSQL("ALTER TABLE summary_table RENAME TO old_summary_table")
            database.execSQL("""
                CREATE TABLE summary_table (
                    id_m INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
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
                    productCloudId INTEGER,
                    sPCloudId INTEGER,
                    product_capital INTEGER NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    summaryCloudId INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(productCloudId) REFERENCES product_table(productCloudId) 
                        ON UPDATE SET NULL ON DELETE CASCADE,
                    FOREIGN KEY(sPCloudId) REFERENCES sub_table(sPCloudId) 
                        ON UPDATE SET NULL ON DELETE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO summary_table (id_m, year, month, month_number, day, day_name, date, item_name, item_sold, price, total_income, productCloudId, sPCloudId, product_capital, is_deleted, summaryCloudId, needs_syncs)
                SELECT s.id_m, s.year, s.month, s.month_number, s.day, s.day_name, s.date, s.item_name, s.item_sold, s.price, s.total_income, s.productCloudId, os.sPCloudId, s.product_capital, s.is_deleted, s.summaryCloudId, s.needs_syncs
                FROM old_summary_table AS s
                LEFT JOIN old_sub_table AS os ON s.sub_id = os.sub_id
            """.trimIndent())
            database.execSQL("DROP TABLE old_summary_table")

            // ----------------------------------- 
            // 4. MIGRATE detail_warna_table
            // ----------------------------------- 
            database.execSQL("ALTER TABLE detail_warna_table RENAME TO old_detail_warna_table")
            database.execSQL("""
                CREATE TABLE detail_warna_table (
                    id INTEGER NOT NULL PRIMARY KEY,
                    sPCloudId INTEGER NOT NULL,
                    batchCount REAL NOT NULL,
                    net REAL NOT NULL,
                    ket TEXT NOT NULL,
                    ref TEXT NOT NULL UNIQUE,
                    is_deleted INTEGER NOT NULL,
                    dWCloudId INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(sPCloudId) REFERENCES sub_table(sPCloudId) ON UPDATE CASCADE ON DELETE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO detail_warna_table (id, sPCloudId, batchCount, net, ket, ref, is_deleted, dWCloudId,needs_syncs)
                SELECT dw.id, os.sPCloudId, dw.batchCount, dw.net, dw.ket, dw.ref, dw.is_deleted,dw.dWCloudId, dw.needs_syncs
                FROM old_detail_warna_table AS dw
                JOIN old_sub_table AS os ON dw.subId = os.sub_id
            """.trimIndent())
            database.execSQL("DROP TABLE old_detail_warna_table")

            database.execSQL("""
    CREATE UNIQUE INDEX index_detail_warna_table_ref
    ON detail_warna_table (ref)
""")
            // ----------------------------------- 
            // 5. MIGRATE inventory_log_table
            // ----------------------------------- 
            database.execSQL("ALTER TABLE inventory_log_table RENAME TO old_inventory_log_table")
            database.execSQL("""
                CREATE TABLE inventory_log_table (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    brandId INTEGER,
                    productCloudId INTEGER,
                    sPCloudId INTEGER,
                    detailWarnaRef TEXT,
                    isi REAL NOT NULL,
                    pcs INTEGER NOT NULL,
                    barangLogDate TEXT NOT NULL,
                    barangLogRef TEXT NOT NULL,
                    barangLogKet TEXT NOT NULL,
                    is_deleted INTEGER NOT NULL DEFAULT 0,
                    iLCloudId INTEGER NOT NULL DEFAULT 0,
                    needs_syncs INTEGER NOT NULL DEFAULT 1,
                    FOREIGN KEY(brandId) REFERENCES brand_table(brandCloudId) ON UPDATE SET NULL ON DELETE SET NULL,
                    FOREIGN KEY(productCloudId) REFERENCES product_table(productCloudId) ON UPDATE SET NULL ON DELETE SET NULL,
                    FOREIGN KEY(sPCloudId) REFERENCES sub_table(sPCloudId) ON UPDATE SET NULL ON DELETE SET NULL,
                    FOREIGN KEY(detailWarnaRef) REFERENCES detail_warna_table(ref) ON UPDATE SET NULL ON DELETE SET NULL
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO inventory_log_table (id, brandId, productCloudId, sPCloudId, detailWarnaRef, isi, pcs, barangLogDate, barangLogRef, barangLogKet, is_deleted, iLCloudId, needs_syncs)
                SELECT il.id, il.brandId, il.productCloudId, os.sPCloudId, il.detailWarnaRef, il.isi, il.pcs, il.barangLogDate, il.barangLogRef, il.barangLogKet, il.is_deleted, il.iLCloudId, il.needs_syncs
                FROM old_inventory_log_table AS il
                LEFT JOIN old_sub_table AS os ON il.subProductId = os.sub_id
            """.trimIndent())
            database.execSQL("DROP TABLE old_inventory_log_table")
            database.execSQL("""
    CREATE UNIQUE INDEX index_inventory_log_table_barangLogRef
    ON inventory_log_table (barangLogRef)
""")

            // ----------------------------------- 
            // 6. MIGRATE inventory_purchase_table
            // ----------------------------------- 
            database.execSQL("ALTER TABLE inventory_purchase_table RENAME TO old_inventory_purchase_table")
            database.execSQL("""
                CREATE TABLE inventory_purchase_table (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    sPCloudId INTEGER,
                    expensesId INTEGER,
                    suplierId INTEGER,
                    suplierName TEXT NOT NULL,
                    subProductName TEXT NOT NULL,
                    purchaseDate TEXT NOT NULL,
                    batchCount REAL NOT NULL,
                    net REAL NOT NULL,
                    price INTEGER NOT NULL,
                    totalPrice REAL NOT NULL,
                    status TEXT NOT NULL,
                    ref TEXT NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    iPCloudId INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(expensesId) REFERENCES expenses_table(id) ON DELETE SET NULL ON UPDATE SET NULL,
                    FOREIGN KEY(sPCloudId) REFERENCES sub_table(sPCloudId) ON DELETE SET NULL ON UPDATE SET NULL,
                    FOREIGN KEY(suplierId) REFERENCES suplier_table(id) ON DELETE SET NULL ON UPDATE SET NULL
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO inventory_purchase_table (id, sPCloudId, expensesId, suplierId, suplierName, subProductName, purchaseDate, batchCount, net, price, totalPrice, status, ref, is_deleted, iPCloudId,needs_syncs)
                SELECT ip.id, os.sPCloudId, ip.expensesId, ip.suplierId, ip.suplierName, ip.subProductName, ip.purchaseDate, ip.batchCount, ip.net, ip.price, ip.totalPrice, ip.status, ip.ref, ip.is_deleted, ip.iPCloudId,ip.needs_syncs
                FROM old_inventory_purchase_table AS ip
                LEFT JOIN old_sub_table AS os ON ip.subProductId = os.sub_id
            """.trimIndent())
            database.execSQL("DROP TABLE old_inventory_purchase_table")
            database.execSQL("""
    CREATE UNIQUE INDEX index_inventory_purchase_table_ref
    ON inventory_purchase_table (ref)
""")

            // ----------------------------------- 
            // 7. MIGRATE merchandise_table
            // ----------------------------------- 
            database.execSQL("ALTER TABLE merchandise_table RENAME TO old_merchandise_table")
            database.execSQL("""
                CREATE TABLE merchandise_table (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    sPCloudId INTEGER NOT NULL,
                    net REAL NOT NULL,
                    ref TEXT NOT NULL,
                    date TEXT NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    mRCloudId INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(sPCloudId) REFERENCES sub_table(sPCloudId) ON DELETE CASCADE ON UPDATE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO merchandise_table (id, sPCloudId, net, ref, date, is_deleted, mRCloudId,needs_syncs)
                SELECT m.id, os.sPCloudId, m.net, m.ref, m.date, m.is_deleted, m.mRCloudId,m.needs_syncs
                FROM old_merchandise_table AS m
                JOIN old_sub_table AS os ON m.sub_id = os.sub_id
            """.trimIndent())
            database.execSQL("DROP TABLE old_merchandise_table")

            // ----------------------------------- 
            // 8. DROP old_sub_table
            // ----------------------------------- 
            database.execSQL("DROP TABLE old_sub_table")
        }
    }
    
    // ... other migrations
}
