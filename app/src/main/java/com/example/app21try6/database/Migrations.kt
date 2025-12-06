package com.example.app21try6.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

   /*
 val MIGRATION_49_50 = object : Migration(49, 50) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // -----------------------------------
            // 1. DiscountTransaction Migration
            // -----------------------------------
            database.execSQL("ALTER TABLE discout_transaction_table RENAME TO old_discout_transaction_table")
            database.execSQL("""
                CREATE TABLE discout_transaction_table (
                    dTCloudId INTEGER NOT NULL PRIMARY KEY,
                    discTransRef TEXT NOT NULL,
                    discountId INTEGER,
                    sum_id INTEGER NOT NULL,
                    discTransDate TEXT NOT NULL,
                    discTransName TEXT NOT NULL,
                    discountAppliedValue REAL NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(discountId) REFERENCES discount_table(discountId) ON UPDATE CASCADE ON DELETE SET NULL,
                    FOREIGN KEY(sum_id) REFERENCES trans_sum_table(sum_id) ON UPDATE CASCADE ON DELETE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO discout_transaction_table (dTCloudId, discTransRef, discountId, sum_id, discTransDate, discTransName, discountAppliedValue, is_deleted, needs_syncs)
                SELECT dTCloudId, discTransRef, discountId, sum_id, discTransDate, discTransName, discountAppliedValue, is_deleted, needs_syncs
                FROM old_discout_transaction_table
            """.trimIndent())
            database.execSQL("DROP TABLE old_discout_transaction_table")

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

            // -----------------------------------
            // 3. Payment Migration
            // -----------------------------------
            database.execSQL("ALTER TABLE paymen_table RENAME TO old_paymen_table")
            database.execSQL("""
                CREATE TABLE paymen_table (
                    paymentCloudId INTEGER NOT NULL PRIMARY KEY,
                    sum_id INTEGER NOT NULL,
                    payment_ammount INTEGER NOT NULL,
                    payment_date TEXT,
                    ref TEXT NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(sum_id) REFERENCES trans_sum_table(sum_id) ON DELETE CASCADE ON UPDATE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO paymen_table (paymentCloudId, sum_id, payment_ammount, payment_date, ref, is_deleted, needs_syncs)
                SELECT paymentCloudId, sum_id, payment_ammount, payment_date, ref, is_deleted, needs_syncs
                FROM old_paymen_table
            """.trimIndent())
            database.execSQL("DROP TABLE old_paymen_table")

            // -----------------------------------
            // 4. InventoryLog Migration
            // -----------------------------------
            database.execSQL("ALTER TABLE inventory_log_table RENAME TO old_inventory_log_table")
            database.execSQL("""
                CREATE TABLE inventory_log_table (
                    iLCloudId INTEGER NOT NULL PRIMARY KEY,
                    brandId INTEGER,
                    productId INTEGER,
                    subProductId INTEGER,
                    detailWarnaRef TEXT,
                    isi REAL NOT NULL,
                    pcs INTEGER NOT NULL,
                    barangLogDate TEXT NOT NULL,
                    barangLogRef TEXT NOT NULL,
                    barangLogKet TEXT NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(brandId) REFERENCES brand_table(brandCloudId) ON DELETE SET NULL ON UPDATE SET NULL,
                    FOREIGN KEY(productId) REFERENCES product_table(product_id) ON DELETE SET NULL ON UPDATE SET NULL,
                    FOREIGN KEY(subProductId) REFERENCES sub_table(sub_id) ON DELETE SET NULL ON UPDATE SET NULL,
                    FOREIGN KEY(detailWarnaRef) REFERENCES detail_warna_table(ref) ON DELETE SET NULL ON UPDATE SET NULL
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO inventory_log_table (iLCloudId, brandId, productId, subProductId, detailWarnaRef, isi, pcs, barangLogDate, barangLogRef, barangLogKet, is_deleted, needs_syncs)
                SELECT iLCloudId, brandId, productId, subProductId, detailWarnaRef, isi, pcs, barangLogDate, barangLogRef, barangLogKet, is_deleted, needs_syncs
                FROM old_inventory_log_table
            """.trimIndent())
            database.execSQL("DROP TABLE old_inventory_log_table")

            // -----------------------------------
            // 5. InventoryPurchase Migration
            // -----------------------------------
            database.execSQL("ALTER TABLE inventory_purchase_table RENAME TO old_inventory_purchase_table")
            database.execSQL("""
                CREATE TABLE inventory_purchase_table (
                    iPCloudId INTEGER NOT NULL PRIMARY KEY,
                    subProductId INTEGER,
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
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(expensesId) REFERENCES expenses_table(id) ON DELETE SET NULL ON UPDATE SET NULL,
                    FOREIGN KEY(subProductId) REFERENCES sub_table(sub_id) ON DELETE SET NULL ON UPDATE SET NULL,
                    FOREIGN KEY(suplierId) REFERENCES suplier_table(id) ON DELETE SET NULL ON UPDATE SET NULL
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO inventory_purchase_table (iPCloudId, subProductId, expensesId, suplierId, suplierName, subProductName, purchaseDate, batchCount, net, price, totalPrice, status, ref, is_deleted, needs_syncs)
                SELECT iPCloudId, subProductId, expensesId, suplierId, suplierName, subProductName, purchaseDate, batchCount, net, price, totalPrice, status, ref, is_deleted, needs_syncs
                FROM old_inventory_purchase_table
            """.trimIndent())
            database.execSQL("DROP TABLE old_inventory_purchase_table")

            // -----------------------------------
            // 6. MerchandiseRetail Migration
            // -----------------------------------
            database.execSQL("ALTER TABLE merchandise_table RENAME TO old_merchandise_table")
            database.execSQL("""
                CREATE TABLE merchandise_table (
                    mRCloudId INTEGER NOT NULL PRIMARY KEY,
                    sub_id INTEGER NOT NULL,
                    net REAL NOT NULL,
                    ref TEXT NOT NULL,
                    date TEXT NOT NULL,
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(sub_id) REFERENCES sub_table(sub_id) ON DELETE CASCADE ON UPDATE CASCADE
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO merchandise_table (mRCloudId, sub_id, net, ref, date, is_deleted, needs_syncs)
                SELECT mRCloudId, sub_id, net, ref, date, is_deleted, needs_syncs
                FROM old_merchandise_table
            """.trimIndent())
            database.execSQL("DROP TABLE old_merchandise_table")

            // -----------------------------------
            // 7. Summary Migration
            // -----------------------------------
            database.execSQL("ALTER TABLE summary_table RENAME TO old_summary_table")
            database.execSQL("""
                CREATE TABLE summary_table (
                    summaryCloudId INTEGER NOT NULL PRIMARY KEY,
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
                    is_deleted INTEGER NOT NULL,
                    needs_syncs INTEGER NOT NULL,
                    FOREIGN KEY(product_id) REFERENCES product_table(product_id) ON DELETE CASCADE ON UPDATE SET NULL,
                    FOREIGN KEY(sub_id) REFERENCES sub_table(sub_id) ON DELETE CASCADE ON UPDATE SET NULL
                )
            """.trimIndent())
            database.execSQL("""
                INSERT INTO summary_table (summaryCloudId, year, month, month_number, day, day_name, date, item_name, item_sold, price, total_income, product_id, sub_id, product_capital, is_deleted, needs_syncs)
                SELECT summaryCloudId, year, month, month_number, day, day_name, date, item_name, item_sold, price, total_income, product_id, sub_id, product_capital, is_deleted, needs_syncs
                FROM old_summary_table
            """.trimIndent())
            database.execSQL("DROP TABLE old_summary_table")
        }
    }

   * */

    val MIGRATION_48_49 = object : Migration(48, 49) {
        override fun migrate(database: SupportSQLiteDatabase) {
            //CUSTOMER TABLE
            // 1. Add 'is_deleted' column (Boolean, defaults to FALSE)
            database.execSQL("""
            ALTER TABLE customer_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE customer_table ADD COLUMN customerCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE customer_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //DETAIL WARNA TABLE
            database.execSQL("""
            ALTER TABLE detail_warna_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE detail_warna_table ADD COLUMN dWCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE detail_warna_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //DISCOUNT TABLE
            database.execSQL("""
            ALTER TABLE discount_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE discount_table ADD COLUMN discountCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE discount_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //DISCOUNT TRANSACTION TABLE
            database.execSQL("""
            ALTER TABLE discout_transaction_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE discout_transaction_table ADD COLUMN dTCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE discout_transaction_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //ExpenseCategoryTable
            database.execSQL("""
            ALTER TABLE expense_category_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE expense_category_table ADD COLUMN eCCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE expense_category_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //EXPENSE TABLE
            database.execSQL("""
            ALTER TABLE expenses_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE expenses_table ADD COLUMN expenseCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE expenses_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //INVENTORY LOG TABLE
            // 1. Add 'is_deleted' column (Boolean, defaults to FALSE)
            database.execSQL("""
            ALTER TABLE inventory_log_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE inventory_log_table ADD COLUMN iLCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE inventory_log_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //INVENTORY PURCHASE TABLE
            // 1. Add 'is_deleted' column (Boolean, defaults to FALSE)
            database.execSQL("""
            ALTER TABLE inventory_purchase_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE inventory_purchase_table ADD COLUMN iPCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE inventory_purchase_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //MERCHANDISE RETAIL
            // 1. Add 'is_deleted' column (Boolean, defaults to FALSE)
            database.execSQL("""
            ALTER TABLE merchandise_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE merchandise_table ADD COLUMN mRCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE merchandise_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //PAYMENT
            // 1. Add 'is_deleted' column (Boolean, defaults to FALSE)
            database.execSQL("""
            ALTER TABLE paymen_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE paymen_table ADD COLUMN paymentCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE paymen_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //PRODUCT TABLE
            // 1. Add 'is_deleted' column (Boolean, defaults to FALSE)
            database.execSQL("""
            ALTER TABLE product_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE product_table ADD COLUMN productCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE product_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //SUB TABLE
            // 1. Add 'is_deleted' column (Boolean, defaults to FALSE)
            database.execSQL("""
            ALTER TABLE sub_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE sub_table ADD COLUMN sPCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE sub_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //SUMMARY TABLE
            // 1. Add 'is_deleted' column (Boolean, defaults to FALSE)
            database.execSQL("""
            ALTER TABLE summary_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE summary_table ADD COLUMN summaryCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE summary_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //SUPLIER TABLE
            // 1. Add 'is_deleted' column (Boolean, defaults to FALSE)
            database.execSQL("""
            ALTER TABLE suplier_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE suplier_table ADD COLUMN suplierCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE suplier_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //TRANSACTIONDETAIL TABLE
            // 1. Add 'is_deleted' column (Boolean, defaults to FALSE)
            database.execSQL("""
            ALTER TABLE trans_detail_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE trans_detail_table ADD COLUMN tDCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE trans_detail_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

            //TRANSACTION SUMMARY TABLE
            database.execSQL("""
            ALTER TABLE trans_sum_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())
            // 2. Add 'customerCloudId' column (String?, nullable)
            database.execSQL("ALTER TABLE trans_sum_table ADD COLUMN tSCloudId INTEGER NOT NULL DEFAULT 0".trimIndent())
            // 3. Add 'needs_syncs' column (Int, defaults to 1)
            database.execSQL("""
            ALTER TABLE trans_sum_table
            ADD COLUMN needs_syncs INTEGER NOT NULL DEFAULT 1
        """.trimIndent())

//            //brand
//            database.execSQL("""
//            ALTER TABLE brand_table
//            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
//        """.trimIndent())

            //category
            database.execSQL("""
            ALTER TABLE category_table
            ADD COLUMN is_deleted INTEGER NOT NULL DEFAULT 0
        """.trimIndent())

        }
    }

    val MIGRATION_47_48 = object : Migration(47, 48) {
        override fun migrate(database: SupportSQLiteDatabase) {

            // -------------------------
            // 1. CATEGORY_TABLE
            // -------------------------
            database.execSQL("""
            CREATE TABLE category_table_new (
                categoryCloudId INTEGER NOT NULL,
                category_name TEXT NOT NULL,
                needs_syncs INTEGER NOT NULL,
                PRIMARY KEY(categoryCloudId)
            )
        """.trimIndent())

            database.execSQL("""
            INSERT INTO category_table_new (categoryCloudId, category_name, needs_syncs)
            SELECT cloud_id, category_name, needs_syncs
            FROM category_table
        """.trimIndent())

            database.execSQL("DROP TABLE category_table")
            database.execSQL("ALTER TABLE category_table_new RENAME TO category_table")


            // -------------------------
            // 2. BRAND_TABLE
            // -------------------------
            database.execSQL("""
            CREATE TABLE brand_table_new (
                brandCloudId INTEGER NOT NULL,
                brand_name TEXT NOT NULL,
                cath_code INTEGER NOT NULL,
                needs_syncs INTEGER NOT NULL,
                PRIMARY KEY(brandCloudId),
                FOREIGN KEY(cath_code) REFERENCES category_table(categoryCloudId)
                    ON DELETE CASCADE ON UPDATE CASCADE
            )
        """.trimIndent())

            database.execSQL("""
            INSERT INTO brand_table_new (brandCloudId, brand_name, cath_code, needs_syncs)
            SELECT cloud_id, brand_name, cath_code, needs_syncs
            FROM brand_table
        """.trimIndent())

            database.execSQL("DROP TABLE brand_table")
            database.execSQL("ALTER TABLE brand_table_new RENAME TO brand_table")


            // -------------------------
            // 3. PRODUCT_TABLE
            // -------------------------
            database.execSQL("""
            CREATE TABLE product_table_new (
                product_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
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
                FOREIGN KEY(brand_code) REFERENCES brand_table(brandCloudId)
                    ON DELETE CASCADE ON UPDATE CASCADE,
                FOREIGN KEY(cath_code) REFERENCES category_table(categoryCloudId)
                    ON DELETE CASCADE ON UPDATE CASCADE,
                FOREIGN KEY(discountId) REFERENCES discount_table(discountId)
                    ON DELETE SET NULL ON UPDATE CASCADE
            )
        """.trimIndent())

            database.execSQL("""
            INSERT INTO product_table_new (
                product_id, product_name, product_price, product_capital,
                checkBoxBoolean, best_selling, default_net, alternate_price,
                brand_code, cath_code, discountId, purchasePrice, purchaseUnit, alternate_capital
            )
            SELECT 
                product_id, product_name, product_price, product_capital,
                checkBoxBoolean, best_selling, default_net, alternate_price,
                brand_code, cath_code, discountId, purchasePrice, purchaseUnit, alternate_capital
            FROM product_table
            """.trimIndent())
            database.execSQL("DROP TABLE product_table")
            database.execSQL("ALTER TABLE product_table_new RENAME TO product_table")

            // -------------------------
            // 4. SUB_TABLE
            // -------------------------
            database.execSQL("""
                CREATE TABLE sub_table_new (
                    sub_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                    sub_name TEXT NOT NULL,
                    roll_u INTEGER NOT NULL,
                    roll_b_t INTEGER NOT NULL,
                    roll_s_t INTEGER NOT NULL,
                    roll_k_t INTEGER NOT NULL,
                    roll_b_g INTEGER NOT NULL,
                    roll_s_g INTEGER NOT NULL,
                    roll_k_g INTEGER NOT NULL,
                    warna TEXT NOT NULL,
                    ket TEXT NOT NULL,
                    product_code INTEGER NOT NULL,
                    brand_code INTEGER NOT NULL,
                    cath_code INTEGER NOT NULL,
                    is_checked INTEGER NOT NULL,
                    discountId INTEGER,
                    FOREIGN KEY(product_code) REFERENCES product_table(product_id)
                        ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY(brand_code) REFERENCES brand_table(brandCloudId)
                        ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY(cath_code) REFERENCES category_table(categoryCloudId)
                        ON DELETE CASCADE ON UPDATE CASCADE,
                    FOREIGN KEY(discountId) REFERENCES discount_table(discountId)
                        ON DELETE SET NULL ON UPDATE CASCADE
                )
            """.trimIndent())

            database.execSQL("""
                INSERT INTO sub_table_new (
                    sub_id, sub_name, roll_u, roll_b_t, roll_s_t, roll_k_t, 
                    roll_b_g, roll_s_g, roll_k_g, warna, ket, product_code, 
                    brand_code, cath_code, is_checked, discountId
                )
                SELECT 
                    sub_id, sub_name, roll_u, roll_b_t, roll_s_t, roll_k_t, 
                    roll_b_g, roll_s_g, roll_k_g, warna, ket, product_code, 
                    brand_code, cath_code, is_checked, discountId
                FROM sub_table
            """.trimIndent())

            database.execSQL("DROP TABLE sub_table")
            database.execSQL("ALTER TABLE sub_table_new RENAME TO sub_table")

        }
    }
    val MIGRATION_46_47 = object : Migration(46, 47) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add the new column to the product_table
            database.execSQL("ALTER TABLE product_table ADD COLUMN alternate_capital REAL NOT NULL DEFAULT 0.0")
        }
    }
    val MIGRATION_45_46 = object:Migration(45,46){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE inventory_log_table ADD COLUMN brandId INTEGER REFERENCES brand_table(brandCloudId) ON UPDATE SET NULL ON DELETE SET NULL")
            database.execSQL("ALTER TABLE inventory_log_table ADD COLUMN productId INTEGER REFERENCES product_table(product_id) ON UPDATE SET NULL ON DELETE SET NULL")
            database.execSQL("ALTER TABLE inventory_log_table ADD COLUMN subProductId INTEGER REFERENCES sub_table(sub_id) ON UPDATE SET NULL ON DELETE SET NULL")
            database.execSQL("ALTER TABLE inventory_log_table ADD COLUMN detailWarnaRef TEXT REFERENCES detail_warna_table(ref) ON UPDATE SET NULL ON DELETE SET NULL")
        }
    }

    val MIGRATION_44_45 = object:Migration(44,45){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `inventory_purchase_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `subProductId` INTEGER, `expensesId` INTEGER, `suplierId` INTEGER, `suplierName` TEXT NOT NULL, `subProductName` TEXT NOT NULL, `purchaseDate` INTEGER NOT NULL, `batchCount` REAL NOT NULL, `net` REAL NOT NULL, `price` INTEGER NOT NULL, `totalPrice` REAL NOT NULL, `status` TEXT NOT NULL, `ref` TEXT NOT NULL, FOREIGN KEY(`expensesId`) REFERENCES `expenses_table`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`subProductId`) REFERENCES `sub_table`(`sub_id`) ON UPDATE NO ACTION ON DELETE SET NULL , FOREIGN KEY(`suplierId`) REFERENCES `suplier_table`(`id`) ON UPDATE NO ACTION ON DELETE SET NULL )")
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_inventory_purchase_table_ref` ON `inventory_purchase_table` (`ref`)")
        }
    }
    val MIGRATION_43_44 = object:Migration(43,44){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `suplier_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `suplierName` TEXT NOT NULL, `suplierLocation` TEXT NOT NULL)")
        }
    }
    val MIGRATION_42_43 = object:Migration(42,43){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `merchandise_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sub_id` INTEGER NOT NULL, `net` REAL NOT NULL, `ref` TEXT NOT NULL, `date` INTEGER NOT NULL, FOREIGN KEY(`sub_id`) REFERENCES `sub_table`(`sub_id`) ON UPDATE NO ACTION ON DELETE CASCADE )")

        }
    }
    val MIGRATION_41_42 = object:Migration(41,42){
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `inventory_log_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `isi` REAL NOT NULL, `pcs` INTEGER NOT NULL, `barangLogDate` INTEGER NOT NULL, `barangLogRef` TEXT NOT NULL,`barangLogKet` TEXT NOT NULL)")
            database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_inventory_log_table_barangLogRef` ON `inventory_log_table` (`barangLogRef`)")

        }
    }

} 