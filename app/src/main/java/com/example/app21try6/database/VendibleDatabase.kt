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
import com.example.app21try6.database.daos.DetailWarnaDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.DiscountTransDao
import com.example.app21try6.database.daos.ExpenseCategoryDao
import com.example.app21try6.database.daos.ExpenseDao
import com.example.app21try6.database.daos.InventoryLogDao
import com.example.app21try6.database.daos.PaymentDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.SummaryDbDao
import com.example.app21try6.database.daos.SuplierDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.DiscountTable
import com.example.app21try6.database.tables.DiscountTransaction
import com.example.app21try6.database.tables.ExpenseCategory
import com.example.app21try6.database.tables.Expenses
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.InventoryPurchase
import com.example.app21try6.database.tables.Payment
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.database.tables.Summary
import com.example.app21try6.database.tables.SuplierTable
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.tables.TransactionSummary


@Database(entities = [Brand::class, Product::class, SubProduct::class, Category::class,
    TransactionSummary::class, TransactionDetail::class, Payment::class, Expenses::class,
    ExpenseCategory::class, Summary::class, DiscountTable::class, DiscountTransaction::class,
    CustomerTable::class,DetailWarnaTable::class,InventoryLog::class,
    SuplierTable::class,InventoryPurchase::class],version=38, exportSchema = true)
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
    abstract val detailWarnaDao:DetailWarnaDao
    abstract val inventoryLogDao:InventoryLogDao
    abstract val suplierDao:SuplierDao


    companion object{
        @Volatile
        private var INSTANCE: VendibleDatabase?=null
        fun getInstance(context: Context):VendibleDatabase{
            synchronized(this) {

                val MIGRATION_37_38 = object : Migration(37, 38) { // Replace X with the old version and Y with the new version.
                    override fun migrate(database: SupportSQLiteDatabase) {
                        // Add the new column `is_keeped` with a default value of 0 (false)
                        database.execSQL("ALTER TABLE expenses_table ADD COLUMN is_keeped INTEGER NOT NULL DEFAULT 0")

                        database.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS detail_warna_table (
                                detailId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                subId INTEGER NOT NULL,
                                batchCount REAL NOT NULL DEFAULT 0.0,
                                net REAL NOT NULL DEFAULT 0.0,
                                ket TEXT NOT NULL DEFAULT '',
                                ref TEXT NOT NULL DEFAULT '',
                                FOREIGN KEY(subId) REFERENCES sub_table(sub_id) ON DELETE CASCADE ON UPDATE CASCADE
                            )
                            """.trimIndent()
                                        )
                        database.execSQL("CREATE UNIQUE INDEX index_detail_warna_table_ref ON detail_warna_table(ref)")
                        database.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS inventory_log_table (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                brandId INTEGER,
                                productId INTEGER,
                                subProductId INTEGER,
                                detailWarnaRef TEXT,
                                isi REAL NOT NULL DEFAULT 0.0,
                                pcs INTEGER NOT NULL DEFAULT 0,
                                barangLogDate TEXT NOT NULL DEFAULT '1970-01-01 00:00',
                                barangLogRef TEXT NOT NULL DEFAULT '',
                                barangLogKet TEXT NOT NULL DEFAULT '',
                                FOREIGN KEY(brandId) REFERENCES brand_table(brand_id) ON DELETE SET NULL ON UPDATE SET NULL,
                                FOREIGN KEY(productId) REFERENCES product_table(product_id) ON DELETE SET NULL ON UPDATE SET NULL,
                                FOREIGN KEY(subProductId) REFERENCES sub_table(sub_id) ON DELETE SET NULL ON UPDATE SET NULL,
                                FOREIGN KEY(detailWarnaRef) REFERENCES detail_warna_table(ref) ON DELETE SET NULL ON UPDATE SET NULL
                            )
                            """.trimIndent()
                        )
                        database.execSQL("CREATE UNIQUE INDEX index_inventory_log_table_barangLogRef ON inventory_log_table(barangLogRef)")
                        database.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS suplier_table (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                suplierName TEXT NOT NULL DEFAULT '',
                                suplierLocation TEXT NOT NULL DEFAULT ''
                            )
                            """.trimIndent()
                        )
                        database.execSQL(
                            """
                            CREATE TABLE IF NOT EXISTS inventory_purchase_table (
                                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                                subProductId INTEGER,
                                expensesId INTEGER,
                                suplierId INTEGER,
                                suplierName TEXT NOT NULL DEFAULT '',
                                subProductName TEXT NOT NULL DEFAULT '',
                                purchaseDate TEXT NOT NULL DEFAULT '1970-01-01 00:00',
                                batchCount REAL NOT NULL DEFAULT 0.0,
                                net REAL NOT NULL DEFAULT 0.0,
                                price INTEGER NOT NULL DEFAULT 0,
                                totalPrice REAL NOT NULL DEFAULT 0.0,
                                status TEXT NOT NULL DEFAULT '',
                                ref TEXT NOT NULL DEFAULT '',
                                FOREIGN KEY(subProductId) REFERENCES sub_table(sub_id) ON DELETE SET NULL ON UPDATE SET NULL,
                                FOREIGN KEY(expensesId) REFERENCES expenses_table(id) ON DELETE SET NULL ON UPDATE SET NULL,
                                FOREIGN KEY(suplierId) REFERENCES suplier_table(id) ON DELETE SET NULL ON UPDATE SET NULL
                            )
                            """.trimIndent()
                        )
                        database.execSQL(
                            """
            CREATE UNIQUE INDEX index_inventory_purchase_table_ref ON inventory_purchase_table(ref)
            """.trimIndent()
                        )

                    }
                }

                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            VendibleDatabase::class.java,
                            "vendible_table"
                    )
                        .addMigrations(MIGRATION_37_38)

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