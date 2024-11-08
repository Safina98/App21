package com.example.app21try6.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.tables.Category
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.CustomerDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.DiscountTransDao
import com.example.app21try6.database.daos.ExpenseCategoryDao
import com.example.app21try6.database.daos.ExpenseDao
import com.example.app21try6.database.daos.PaymentDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.SummaryDbDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.database.tables.DiscountTable
import com.example.app21try6.database.tables.DiscountTransaction
import com.example.app21try6.database.tables.ExpenseCategory
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.Payment
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.database.tables.Summary
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.tables.TransactionSummary


@Database(entities = [Brand::class, Product::class, SubProduct::class, Category::class, TransactionSummary::class, TransactionDetail::class, Payment::class, Expenses::class, ExpenseCategory::class, Summary::class, DiscountTable::class, DiscountTransaction::class, CustomerTable::class],version=37, exportSchema = true)
@TypeConverters(DateTypeConverter::class)
abstract class VendibleDatabase:RoomDatabase(){
    abstract val brandDao : BrandDao
    abstract val productDao: ProductDao
    abstract val subProductDao: SubProductDao
    abstract val categoryDao: CategoryDao
    abstract val transDetailDao: TransDetailDao
    abstract val transSumDao: TransSumDao
    abstract val paymentDao: PaymentDao
    abstract val expenseDao: ExpenseDao
    abstract val expenseCategoryDao: ExpenseCategoryDao
    abstract val summaryDbDao: SummaryDbDao
    abstract val customerDao: CustomerDao
    abstract val discountDao: DiscountDao
    abstract val discountTransDao: DiscountTransDao

    companion object{
        @Volatile
        private var INSTANCE: VendibleDatabase?=null
        fun getInstance(context: Context):VendibleDatabase{
            synchronized(this) {

                val MIGRATION_36_37 = object : Migration(36, 37) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        // Create the new table with the added 'date' column
                        database.execSQL("ALTER TABLE expense_category_table ADD COLUMN repeat_period TEXT")
                        database.execSQL("ALTER TABLE expense_category_table ADD COLUMN repeat_date TEXT") // Store date as TEXT with the format yyyy-MM-dd
                    }
                }

                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            VendibleDatabase::class.java,
                            "vendible_table"
                    )
                        .addMigrations(MIGRATION_36_37)

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