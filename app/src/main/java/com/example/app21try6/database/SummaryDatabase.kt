package com.example.app21try6.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Summary::class,TransactionSummary::class,TransactionDetail::class],version = 6,exportSchema = false)
abstract class SummaryDatabase :RoomDatabase(){
    abstract val summaryDbDao:SummaryDbDao
    abstract val transDetailDao:TransDetailDao
    abstract val transSumDao:TransSumDao
    companion object{

        @Volatile
        private var INSTANCE: SummaryDatabase?=null
        fun getInstance(context: Context):SummaryDatabase{
            synchronized(this) {
                val d_s=""

                val MIGRATION_1_2 = object : Migration(4, 5) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL(  "CREATE TABLE IF NOT EXISTS trans_sum_table (sum_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,cust_name TEXT NOT NULL, total_trans REAL NOT NULL,paid INTEGER NOT NULL,trans_date TEXT NOT NULL, is_taken INTEGER NOT NULL, is_paid_off INTEGER NOT NULL)")
                        database.execSQL(  "CREATE TABLE IF NOT EXISTS trans_detail_table (trans_detail_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,sum_id INTEGER NOT NULL,trans_item_name TEXT NOT NULL,qty REAL NOT NULL,trans_price INTEGER NOT NULL,total_price REAL NOT NULL,FOREIGN KEY(sum_id) REFERENCES trans_sum_table(sum_id)ON UPDATE CASCADE ON DELETE CASCADE)")
                    }
                }

                val MIGRATION_5_6 = object : Migration(5, 6) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("DROP TABLE trans_sum_table")
                        database.execSQL("DROP TABLE trans_detail_table")
                        database.execSQL(  "CREATE TABLE IF NOT EXISTS trans_sum_table (sum_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ,cust_name TEXT NOT NULL, total_trans REAL NOT NULL,paid INTEGER NOT NULL,trans_date TEXT NOT NULL, is_taken INTEGER NOT NULL, is_paid_off INTEGER NOT NULL)")
                        database.execSQL(  "CREATE TABLE IF NOT EXISTS trans_detail_table (trans_detail_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,sum_id INTEGER NOT NULL,trans_item_name TEXT NOT NULL,qty REAL NOT NULL,trans_price INTEGER NOT NULL,total_price REAL NOT NULL,FOREIGN KEY(sum_id) REFERENCES trans_sum_table(sum_id)ON UPDATE CASCADE ON DELETE CASCADE)")
                    }
                }
                val MIGRATION_6_7 = object : Migration(6, 7) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("DROP TABLE trans_sum_table")
                        database.execSQL("DROP TABLE trans_detail_table")
                        database.execSQL(  "CREATE TABLE IF NOT EXISTS trans_sum_table (sum_id LONG NOT NULL PRIMARY KEY ,cust_name TEXT NOT NULL, total_trans REAL NOT NULL,paid INTEGER NOT NULL,trans_date TEXT NOT NULL, is_taken INTEGER NOT NULL, is_paid_off INTEGER NOT NULL)")
                        database.execSQL(  "CREATE TABLE IF NOT EXISTS trans_detail_table (trans_detail_id LONG NOT NULL PRIMARY KEY ,sum_id LONG NOT NULL,trans_item_name TEXT NOT NULL,qty REAL NOT NULL,trans_price INTEGER NOT NULL,total_price REAL NOT NULL,FOREIGN KEY(sum_id) REFERENCES trans_sum_table(sum_id)ON UPDATE CASCADE ON DELETE CASCADE)")
                    }
                }
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SummaryDatabase::class.java,
                        "summary_table"
                    ).addMigrations( MIGRATION_5_6 ).build()
                    INSTANCE = instance

                }
                return instance
            }
        }
    }

}