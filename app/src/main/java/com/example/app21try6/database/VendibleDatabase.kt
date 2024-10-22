package com.example.app21try6.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase



@Database(entities = [Brand::class,Product::class,SubProduct::class,Category::class,TransactionSummary::class,TransactionDetail::class,Payment::class,Expenses::class,ExpenseCategory::class,Summary::class,DiscountTable::class,DiscountTransaction::class,CustomerTable::class],version=35, exportSchema = true)
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

                val MIGRATION_34_35 = object : Migration(34, 35) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        // Create the new table with the added 'date' column
                        database.execSQL("ALTER TABLE summary_table ADD COLUMN product_capital INTEGER NOT NULL DEFAULT 0")
                        database.execSQL("ALTER TABLE trans_detail_table ADD COLUMN product_capital INTEGER NOT NULL DEFAULT 0")

                    }
                }

                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            VendibleDatabase::class.java,
                            "vendible_table"
                    )
                        .addMigrations(MIGRATION_34_35)

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

//kalau cahsback bagaimana?
//harga yang masuk di pembukuan harga setelah dikurangi cahsback?
//buat table log barang
//id, ref, sub id, harga, jumlah roll, isi dalam 1 roll, tanggal
//pas kurangi stok, lacak berapa meter setiap barang keluar, kalau sambungan bagaimana?, kalau tambah 20 cm bagaimana?