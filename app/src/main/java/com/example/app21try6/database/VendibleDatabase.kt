package com.example.app21try6.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase



@Database(entities = [Brand::class,Product::class,SubProduct::class,Category::class,TransactionSummary::class,TransactionDetail::class,Payment::class,Expenses::class,ExpenseCategory::class,Summary::class],version=32, exportSchema = true)
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

    companion object{
        @Volatile
        private var INSTANCE: VendibleDatabase?=null
        fun getInstance(context: Context):VendibleDatabase{
            synchronized(this) {
                val MIGRATION_1_2 = object : Migration(15, 16) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE `sub_table` ADD COLUMN `is_checked`")
                    }
                }
                val MIGRATION_21_22 = object : Migration(21, 22) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE `trans_detail_table` ADD COLUMN `is_prepared` INTEGER NOT NULL DEFAULT 0")
                    }
                }
                val MIGRATION_22_23 = object : Migration(22, 23) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE `trans_sum_table` ADD COLUMN `ref` TEXT NOT NULL DEFAULT ''")
                    }
                }
                val MIGRATION_24_25 = object : Migration(24, 25) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE `trans_sum_table` ADD COLUMN `is_keeped` INTEGER NOT NULL DEFAULT 0")
                    }
                }
                val MIGRATION_26_27 = object : Migration(26, 27) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE `trans_detail_table` ADD COLUMN `trans_detail_date` TEXT")
                        database.execSQL("ALTER TABLE `trans_detail_table` ADD COLUMN `unit` TEXT")
                        database.execSQL("ALTER TABLE `trans_detail_table` ADD COLUMN `unit_qty` REAL NOT NULL DEFAULT 1.0")
                        database.execSQL("ALTER TABLE `product_table` ADD COLUMN `product_capital` INTEGER NOT NULL DEFAULT 0")
                    }
                }
                val MIGRATION_29_30 = object : Migration(29, 30) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE trans_detail_table ADD COLUMN item_position INTEGER NOT NULL DEFAULT 0")
                    }
                }
                val MIGRATION_30_31 = object : Migration(30, 31) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("ALTER TABLE trans_sum_table ADD COLUMN sum_note TEXT")
                    }
                }
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


                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            VendibleDatabase::class.java,
                            "vendible_table"
                    ).addMigrations(MIGRATION_31_32)
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