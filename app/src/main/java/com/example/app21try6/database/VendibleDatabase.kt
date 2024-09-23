package com.example.app21try6.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase



@Database(entities = [Brand::class,Product::class,SubProduct::class,Category::class,TransactionSummary::class,TransactionDetail::class,Payment::class,Expenses::class,ExpenseCategory::class,Summary::class,DiscountTable::class,DiscountTransaction::class,CustomerTable::class],version=33, exportSchema = true)
@TypeConverters(DateTypeConverter::class)
abstract class VendibleDatabase:RoomDatabase(){
    abstract val brandDao :BrandDao
    abstract val productDao:ProductDao
    abstract val subProductDao:SubProductDao
    abstract val categoryDao:CategoryDao
    abstract val transDetailDao:TransDetailDao
    abstract val transSumDao:TransSumDao
    abstract val paymentDao:PaymentDao
    abstract val expenseDao:ExpenseDao
    abstract val expenseCategoryDao:ExpenseCategoryDao
    abstract val summaryDbDao:SummaryDbDao
    abstract val customerDao:CustomerDao
    abstract val discountDao:DiscountDao
    abstract val discountTransDao:DiscountTransDao

    companion object{
        @Volatile
        private var INSTANCE: VendibleDatabase?=null
        fun getInstance(context: Context):VendibleDatabase{
            synchronized(this) {

                val MIGRATION_31_32 = object : Migration(31, 32) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        // Create the new table with the added 'date' column
                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS summary_table (" +
                                    "id_m INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "year INTEGER NOT NULL DEFAULT 2030, " +
                                    "month TEXT NOT NULL DEFAULT 'empty', " +
                                    "month_number INTEGER NOT NULL DEFAULT 0, " +
                                    "day INTEGER NOT NULL DEFAULT 0, " +
                                    "day_name TEXT NOT NULL DEFAULT 'empty', " +
                                    "date TEXT NOT NULL DEFAULT '1970-01-01', " +  // Default date value
                                    "item_name TEXT NOT NULL DEFAULT 'empty', " +
                                    "item_sold REAL NOT NULL DEFAULT 0.0, " +
                                    "price REAL NOT NULL DEFAULT 0.0, " +
                                    "total_income REAL NOT NULL DEFAULT 0.0" +
                                    ")"
                        )

                    }
                }
                val MIGRATION_32_33 = object : Migration(32, 33) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        // Create customer_table
                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS customer_table (" +
                                    "custId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "customerName TEXT NOT NULL, " +
                                    "customerBussinessName TEXT NOT NULL, " +
                                    "customerLocation TEXT, " +
                                    "customerAddress TEXT NOT NULL, " +
                                    "customerLevel TEXT, " +
                                    "customerTag1 TEXT, " +
                                    "customerTag2 TEXT, " +
                                    "customerPoint REAL NOT NULL DEFAULT 0.0" +
                                    ")"
                        )

                        // Create discount_table
                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS discount_table (" +
                                    "discountId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "discountName TEXT NOT NULL, " +
                                    "discountValue REAL NOT NULL, " +
                                    "discountType TEXT NOT NULL, " +
                                    "minimumQty REAL, " +
                                    "minimumTranscation REAL, " +
                                    "vendibleLevel TEXT, " +
                                    "custLevel TEXT, " +
                                    "custTag1 TEXT, " +
                                    "custTag2 TEXT, " +
                                    "discountDuration TEXT, " +  // Store Date as long
                                    "custLocation TEXT" +
                                    ")"
                        )
                        database.execSQL(
                            "CREATE UNIQUE INDEX index_discount_table_discountName ON discount_table(discountName)"
                        )

                        // Create discout_transaction_table
                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS discout_transaction_table (" +
                                    "discTransId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "discTransRef TEXT NOT NULL, " +
                                    "discountId INTEGER, " +
                                    "sum_id INTEGER NOT NULL, " +
                                    "discTransDate TEXT NOT NULL, " +  // Store Date as long
                                    "discTransName TEXT NOT NULL, " +
                                    "discountAppliedValue REAL NOT NULL, " +
                                    "FOREIGN KEY (discountId) REFERENCES discount_table(discountId) ON DELETE SET NULL ON UPDATE CASCADE, " +
                                    "FOREIGN KEY (sum_id) REFERENCES trans_sum_table(sum_id) ON DELETE CASCADE ON UPDATE CASCADE" +
                                    ")"
                        )
                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS product_table_new (" +
                                    "product_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "product_name TEXT NOT NULL, " +
                                    "product_price INTEGER NOT NULL, " +
                                    "product_capital INTEGER NOT NULL, " +
                                    "checkBoxBoolean INTEGER NOT NULL, " +
                                    "best_selling INTEGER NOT NULL, " +
                                    "brand_code INTEGER NOT NULL, " +
                                    "cath_code INTEGER NOT NULL, " +
                                    "discountId INTEGER, " +
                                    "FOREIGN KEY(brand_code) REFERENCES brand_table(brand_id) ON DELETE CASCADE  ON UPDATE CASCADE, " +
                                    "FOREIGN KEY(cath_code) REFERENCES category_table(category_id) ON DELETE CASCADE  ON UPDATE CASCADE, " +
                                    "FOREIGN KEY(discountId) REFERENCES discount_table(discountId) ON DELETE SET NULL  ON UPDATE CASCADE" +
                                    ")"
                        )

                        // 5. Copy data from old product_table to new product_table_new
                        database.execSQL("INSERT INTO product_table_new (product_id, product_name, product_price, product_capital, checkBoxBoolean, best_selling, brand_code, cath_code) SELECT product_id, product_name, product_price, product_capital, checkBoxBoolean, best_selling, brand_code, cath_code FROM product_table")

                        // 6. Drop the old product_table
                        database.execSQL("DROP TABLE product_table")

                        // 7. Rename the new product_table_new to product_table
                        database.execSQL("ALTER TABLE product_table_new RENAME TO product_table")

                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS sub_table_new (" +
                                    "sub_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "sub_name TEXT NOT NULL, " +
                                    "roll_u INTEGER NOT NULL, " +
                                    "roll_b_t INTEGER NOT NULL, " +
                                    "roll_s_t INTEGER NOT NULL, " +
                                    "roll_k_t INTEGER NOT NULL, " +
                                    "roll_b_g INTEGER NOT NULL, " +
                                    "roll_s_g INTEGER NOT NULL, " +
                                    "roll_k_g INTEGER NOT NULL, " +
                                    "warna TEXT NOT NULL, " +
                                    "ket TEXT NOT NULL, " +
                                    "product_code INTEGER NOT NULL, " +
                                    "brand_code INTEGER NOT NULL, " +
                                    "cath_code INTEGER NOT NULL, " +
                                    "is_checked INTEGER NOT NULL, " +
                                    "discountId INTEGER, " +
                                    "FOREIGN KEY(product_code) REFERENCES product_table(product_id) ON DELETE CASCADE  ON UPDATE CASCADE, " +
                                    "FOREIGN KEY(brand_code) REFERENCES brand_table(brand_id) ON DELETE CASCADE  ON UPDATE CASCADE, " +
                                    "FOREIGN KEY(cath_code) REFERENCES category_table(category_id) ON DELETE CASCADE  ON UPDATE CASCADE, " +
                                    "FOREIGN KEY(discountId) REFERENCES discount_table(discountId) ON DELETE SET NULL  ON UPDATE CASCADE" +
                                    ")"
                        )

                        // 2. Copy data from old sub_table to new sub_table_new
                        database.execSQL("INSERT INTO sub_table_new (sub_id, sub_name, roll_u, roll_b_t, roll_s_t, roll_k_t, roll_b_g, roll_s_g, roll_k_g, warna, ket, product_code, brand_code, cath_code, is_checked) SELECT sub_id, sub_name, roll_u, roll_b_t, roll_s_t, roll_k_t, roll_b_g, roll_s_g, roll_k_g, warna, ket, product_code, brand_code, cath_code, is_checked FROM sub_table")

                        // 3. Drop the old sub_table
                        database.execSQL("DROP TABLE sub_table")

                        // 4. Rename the new sub_table_new to sub_table
                        database.execSQL("ALTER TABLE sub_table_new RENAME TO sub_table")

                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS trans_detail_table_new (" +
                                    "trans_detail_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "sum_id INTEGER NOT NULL, " +
                                    "trans_item_name TEXT NOT NULL, " +
                                    "qty REAL NOT NULL, " +
                                    "trans_price INTEGER NOT NULL, " +
                                    "total_price REAL NOT NULL, " +
                                    "is_prepared INTEGER NOT NULL, " +
                                    "trans_detail_date TEXT, " + // Store Date as long
                                    "unit TEXT, " +
                                    "unit_qty REAL NOT NULL, " +
                                    "item_position INTEGER NOT NULL, " +
                                    "sub_id INTEGER, " +
                                    "FOREIGN KEY(sum_id) REFERENCES trans_sum_table(sum_id) ON DELETE CASCADE  ON UPDATE CASCADE, " +
                                    "FOREIGN KEY(sub_id) REFERENCES sub_table(sub_id) ON DELETE SET NULL  ON UPDATE CASCADE" +
                                    ")"
                        )

                        // 2. Copy data from old trans_detail_table to new trans_detail_table_new
                        database.execSQL("INSERT INTO trans_detail_table_new (trans_detail_id, sum_id, trans_item_name, qty, trans_price, total_price, is_prepared, trans_detail_date, unit, unit_qty, item_position) SELECT trans_detail_id, sum_id, trans_item_name, qty, trans_price, total_price, is_prepared, trans_detail_date, unit, unit_qty, item_position FROM trans_detail_table")

                        // 3. Drop the old trans_detail_table
                        database.execSQL("DROP TABLE trans_detail_table")

                        // 4. Rename the new trans_detail_table_new to trans_detail_table
                        database.execSQL("ALTER TABLE trans_detail_table_new RENAME TO trans_detail_table")

                        database.execSQL(
                            "CREATE TABLE IF NOT EXISTS trans_sum_table_new (" +
                                    "sum_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                                    "cust_name TEXT NOT NULL, " +
                                    "total_trans REAL NOT NULL, " +
                                    "paid INTEGER NOT NULL, " +
                                    "trans_date TEXT NOT NULL, " + // Store Date as long
                                    "is_taken INTEGER NOT NULL, " +
                                    "is_paid_off INTEGER NOT NULL, " +
                                    "is_keeped INTEGER NOT NULL, " +
                                    "ref TEXT NOT NULL, " +
                                    "sum_note TEXT, " +
                                    "custId INTEGER, " +
                                    "FOREIGN KEY(custId) REFERENCES customer_table(custId) ON DELETE SET NULL  ON UPDATE CASCADE" +
                                    ")"
                        )

                        // 2. Copy data from old trans_sum_table to new trans_sum_table_new
                        database.execSQL("INSERT INTO trans_sum_table_new (sum_id, cust_name, total_trans, paid, trans_date, is_taken, is_paid_off, is_keeped, ref, sum_note) SELECT sum_id, cust_name, total_trans, paid, trans_date, is_taken, is_paid_off, is_keeped, ref, sum_note FROM trans_sum_table")

                        // 3. Drop the old trans_sum_table
                        database.execSQL("DROP TABLE trans_sum_table")

                        // 4. Rename the new trans_sum_table_new to trans_sum_table
                        database.execSQL("ALTER TABLE trans_sum_table_new RENAME TO trans_sum_table")
                    }
                }


                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            VendibleDatabase::class.java,
                            "vendible_table"
                    )
                        .allowMainThreadQueries()
                        .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                        .enableMultiInstanceInvalidation()
                        .addMigrations(MIGRATION_32_33)

                        // .fallbackToDestructiveMigration()
                    .build()
                    INSTANCE = instance
                    //instance = Room.databaseBuilder(context.applicationContext,VendibleDatabase::class.java,"mymaindb").allowMainThreadQueries().build()
                }
                return instance
            }
        }
    }
}