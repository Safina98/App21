package com.example.app21try6.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MigrationsForCloud {

    val MIGRATION_51_52 = object : Migration(51, 52) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // ----------------------------------- 
            // 1. MIGRATE product_table FIRST
            // ----------------------------------- 
            database.execSQL("ALTER TABLE product_table RENAME TO old_product_table")
            database.execSQL("""
                CREATE TABLE product_table (
                    productCloudId INTEGER NOT NULL PRIMARY KEY,
                    product_name TEXT NOT NULL,
                    product_price INTEGER NOT NULL,
                    product_capital INTEGER NOT NULL,
                    checkBoxBoolean INTEGER NOT NULL,
                    best_selling INTEGER NOT NULL,
                    default_net REAL NOT NULL,
                    alternate_price REAL NOT NULL,
                    brand_code INTEGER NOT NULL,
                    cath_code INTEGER NOT NULL,
                    discountId INTEGER,
                    purchasePrice INTEGER,
                    purchaseUnit TEXT,
                    alternate_capital REAL NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(brand_code) REFERENCES brand_table(brandCloudId) ON UPDATE CASCADE ON DELETE CASCADE,
                    FOREIGN KEY(cath_code) REFERENCES category_table(categoryCloudId) ON UPDATE CASCADE ON DELETE CASCADE,
                    FOREIGN KEY(discountId) REFERENCES discount_table(discountId) ON UPDATE CASCADE ON DELETE SET NULL
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO product_table (
                    productCloudId, product_name, product_price, product_capital, checkBoxBoolean, best_selling, 
                    default_net, alternate_price, brand_code, cath_code, discountId, purchasePrice, purchaseUnit, 
                    alternate_capital, is_deleted, needs_syncs
                )
                SELECT 
                    productCloudId, product_name, product_price, product_capital, checkBoxBoolean, best_selling, 
                    default_net, alternate_price, brand_code, cath_code, discountId, purchasePrice, purchaseUnit, 
                    alternate_capital, is_deleted, needs_syncs
                FROM old_product_table
            """.trimIndent())

            // ----------------------------------- 
            // 2. MIGRATE sub_table (child of product_table)
            // ----------------------------------- 
            database.execSQL("ALTER TABLE sub_table RENAME TO old_sub_table")
            database.execSQL("""
                CREATE TABLE sub_table (
                    sub_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
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
                    sPCloudId INTEGER NOT NULL,
                    FOREIGN KEY(productCloudId) REFERENCES product_table(productCloudId) ON UPDATE CASCADE ON DELETE CASCADE,
                    FOREIGN KEY(brand_code) REFERENCES brand_table(brandCloudId) ON UPDATE CASCADE ON DELETE CASCADE,
                    FOREIGN KEY(cath_code) REFERENCES category_table(categoryCloudId) ON UPDATE CASCADE ON DELETE CASCADE,
                    FOREIGN KEY(discountId) REFERENCES discount_table(discountId) ON UPDATE CASCADE ON DELETE SET NULL
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO sub_table (
                    sub_id, sub_name, roll_u,  warna, ket, 
                    productCloudId, brand_code, cath_code, is_checked, discountId, is_deleted, needs_syncs, sPCloudId
                )
                SELECT 
                    s.sub_id, s.sub_name, s.roll_u,  s.warna, s.ket, 
                    op.productCloudId, s.brand_code, s.cath_code, s.is_checked, s.discountId, s.is_deleted, s.needs_syncs, s.sPCloudId
                FROM old_sub_table AS s
                JOIN old_product_table AS op ON s.product_code = op.product_id
            """.trimIndent())
            database.execSQL("DROP TABLE old_sub_table")

            // ----------------------------------- 
            // 3. MIGRATE summary_table (child of product_table)
            // ----------------------------------- 
            database.execSQL("ALTER TABLE summary_table RENAME TO old_summary_table")

            database.execSQL(
                """
                CREATE TABLE summary_table (
                    id_m INTEGER NOT NULL PRIMARY KEY,
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
                    sub_id INTEGER,
                    product_capital INTEGER NOT NULL,
                    summaryCloudId INTEGER NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(productCloudId) REFERENCES product_table(productCloudId) 
                        ON UPDATE SET NULL ON DELETE CASCADE,
                    FOREIGN KEY(sub_id) REFERENCES sub_table(sub_id) 
                        ON UPDATE SET NULL ON DELETE CASCADE
                )
                """.trimIndent()
            )
// Create missing indices
            // Insert migrated data
            database.execSQL(
                """
                INSERT INTO summary_table (
                    id_m, year, month, month_number, day, day_name, date, item_name, item_sold, 
                    price, total_income, productCloudId, sub_id, product_capital, is_deleted, summaryCloudId,needs_syncs
                )
                SELECT 
                    s.id_m, s.year, s.month, s.month_number, s.day, s.day_name, s.date, s.item_name, s.item_sold, 
                    s.price, s.total_income, op.productCloudId, s.sub_id, s.product_capital, s.is_deleted, s.summaryCloudId,s.needs_syncs
                FROM old_summary_table AS s
                LEFT JOIN old_product_table AS op ON s.product_id = op.product_id
                """.trimIndent()
            )

            database.execSQL("DROP TABLE old_summary_table")

            // ----------------------------------- 
            // 4. MIGRATE inventory_log_table (child of product_table)
            // ----------------------------------- 
            database.execSQL("ALTER TABLE inventory_log_table RENAME TO old_inventory_log_table")
            database.execSQL("""
                CREATE TABLE inventory_log_table (
                    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    brandId INTEGER,
                    productCloudId INTEGER,
                    subProductId INTEGER,
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
                    FOREIGN KEY(subProductId) REFERENCES sub_table(sub_id) ON UPDATE SET NULL ON DELETE SET NULL,
                    FOREIGN KEY(detailWarnaRef) REFERENCES detail_warna_table(ref) ON UPDATE SET NULL ON DELETE SET NULL
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO inventory_log_table (
                    id, brandId, productCloudId, subProductId, detailWarnaRef, isi, pcs, 
                    barangLogDate, barangLogRef, barangLogKet, is_deleted, iLCloudId,needs_syncs
                )
                SELECT 
                    il.id, il.brandId, op.productCloudId, il.subProductId, il.detailWarnaRef, il.isi, il.pcs, 
                    il.barangLogDate, il.barangLogRef, il.barangLogKet, il.is_deleted, il.iLCloudId,il.needs_syncs
                FROM old_inventory_log_table AS il
                LEFT JOIN old_product_table AS op ON il.productId = op.product_id
            """.trimIndent())
            database.execSQL("DROP TABLE old_inventory_log_table")
            database.execSQL("""
    CREATE UNIQUE INDEX index_inventory_log_table_barangLogRef
    ON inventory_log_table (barangLogRef)
""")

            // ----------------------------------- 
            // 5. DROP old_product_table
            // ----------------------------------- 
            database.execSQL("DROP TABLE old_product_table")
        }
    }

    val MIGRATION_50_51 = object : Migration(50, 51) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // ... existing migration code ...
        }
    }
}