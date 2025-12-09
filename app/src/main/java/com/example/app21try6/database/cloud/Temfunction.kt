/*
package com.example.app21try6.database.cloud

import android.util.Log
import com.example.app21try6.Constants
import com.example.app21try6.database.daos.DetailWarnaDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.tables.*
import java.util.Date
import java.util.concurrent.Executors

class Temfunction {
    fun syncSubProduct(subProductDao: SubProductDao) {
        syncTable(
            tableName = Constants.TABLENAMES.SUB_PRODUCT,
            clazz = SubProductCloud::class.java,

            convertAndSave = { cloud, key ->
                if (!cloud.isDeleted) {
                    val subProduct = SubProduct(
                        sPCloudId = key.toLong(),
                        sub_name = cloud.subName,
                        roll_u = cloud.rollU,
                        warna = cloud.warna,
                        ket = cloud.ket,
                        productCloudId = cloud.productCloudId,
                        brand_code = cloud.brandCode,
                        cath_code = cloud.cathCode,
                        is_checked = cloud.isChecked,
                        discountId = cloud.discountId,
                        isDeleted = cloud.isDeleted,
                        needsSyncs = cloud.needsSyncs
                    )
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            subProductDao.insert(subProduct)
                        } catch (ex: Exception) {
                            Log.e(
                                "SyncManager",
                                "Upload failed for subProduct ${subProduct.sPCloudId}: ${ex.message}"
                            )
                        }
                    }
                }
            },

            deleteLocal = { key ->
                Executors.newSingleThreadExecutor().execute {
                    subProductDao.delete(key.toLong())
                }
            }
        )
    }

    fun syncDetailWarna(detailWarnaDao: DetailWarnaDao) {
        syncTable(
            tableName = Constants.TABLENAMES.DETAIL_WARNA,
            clazz = DetailWarnaCloud::class.java,
            convertAndSave = { cloud, key ->
                if (!cloud.isDeleted) {
                    val detailWarna = DetailWarnaTable(
                        dWCloudId = key.toLong(),
                        sPCloudId = cloud.sPCloudId,
                        batchCount = cloud.batchCount,
                        net = cloud.net,
                        ket = cloud.ket,
                        ref = cloud.ref,
                        isDeleted = cloud.isDeleted,
                        needsSyncs = cloud.needsSyncs
                    )
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            detailWarnaDao.insert(detailWarna)
                        } catch (ex: Exception) {
                            Log.e(
                                "SyncManager",
                                "Upload failed for detailWarna ${detailWarna.dWCloudId}: ${ex.message}"
                            )
                        }
                    }
                }
            },
            deleteLocal = { key ->
                Executors.newSingleThreadExecutor().execute {
                    detailWarnaDao.delete(key.toLong())
                }
            }
        )
    }

    fun syncMerchandiseRetail(detailWarnaDao: DetailWarnaDao) {
        syncTable(
            tableName = Constants.TABLENAMES.MERCHANDISE_RETAIL,
            clazz = MerchandiseRetailCloud::class.java,
            convertAndSave = { cloud, key ->
                if (!cloud.isDeleted) {
                    val merchandiseRetail = MerchandiseRetail(
                        mRCloudId = key.toLong(),
                        sPCloudId = cloud.sPCloudId,
                        net = cloud.net,
                        ref = cloud.ref,
                        date = Date(cloud.date),
                        isDeleted = cloud.isDeleted,
                        needsSyncs = cloud.needsSyncs
                    )
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            detailWarnaDao.insert(merchandiseRetail)
                        } catch (ex: Exception) {
                            Log.e(
                                "SyncManager",
                                "Upload failed for merchandiseRetail ${merchandiseRetail.mRCloudId}: ${ex.message}"
                            )
                        }
                    }
                }
            },
            deleteLocal = { key ->
                Executors.newSingleThreadExecutor().execute {
                    detailWarnaDao.deleteMerchandise(key.toInt())
                }
            }
        )
    }

    fun syncTransactionDetail(transDetailDao: TransDetailDao) {
        syncTable(
            tableName = Constants.TABLENAMES.TRANSACTION_DETAIL,
            clazz = TransactionDetailCloud::class.java,
            convertAndSave = { cloud, key ->
                if (!cloud.isDeleted) {
                    val transactionDetail = TransactionDetail(
                        tDCloudId = key.toLong(),
                        tSCloudId = cloud.tSCloudId,
                        trans_item_name = cloud.transItemName,
                        qty = cloud.qty,
                        trans_price = cloud.transPrice,
                        total_price = cloud.totalPrice,
                        is_prepared = cloud.isPrepared,
                        is_cutted = cloud.isCutted,
                        trans_detail_date = cloud.transDetailDate?.let { Date(it) },
                        unit = cloud.unit,
                        unit_qty = cloud.unitQty,
                        item_position = cloud.itemPosition,
                        sPCloudId = cloud.sPCloudId,
                        product_capital = cloud.productCapital,
                        isDeleted = cloud.isDeleted,
                        needsSyncs = cloud.needsSyncs
                    )
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            transDetailDao.insert(transactionDetail)
                        } catch (ex: Exception) {
                            Log.e(
                                "SyncManager",
                                "Upload failed for transactionDetail ${transactionDetail.tDCloudId}: ${ex.message}"
                            )
                        }
                    }
                }
            },
            deleteLocal = { key ->
                Executors.newSingleThreadExecutor().execute {
                    transDetailDao.deleteAnItemTransDetail(key.toLong())
                }
            }
        )
    }

    fun syncTransactionSummary(transSumDao: TransSumDao) {
        syncTable(
            tableName = Constants.TABLENAMES.TRANSACTION_SUMMARY,
            clazz = TransactionSummaryCloud::class.java,
            convertAndSave = { cloud, key ->
                if (!cloud.isDeleted) {
                    val transactionSummary = TransactionSummary(
                        tSCloudId = key.toLong(),
                        cust_name = cloud.custName,
                        total_trans = cloud.totalTrans,
                        total_after_discount = cloud.totalAfterDiscount,
                        paid = cloud.paid,
                        trans_date = cloud.transDate?.let { Date(it) },
                        is_taken_ = cloud.isTaken,
                        is_paid_off = cloud.isPaidOff,
                        is_keeped = cloud.isKeeped,
                        is_logged = cloud.isLogged,
                        ref = cloud.ref,
                        sum_note = cloud.sumNote,
                        custId = cloud.custId,
                        isDeleted = cloud.isDeleted,
                        needsSyncs = cloud.needsSyncs
                    )
                    Executors.newSingleThreadExecutor().execute {
                        try {
                            transSumDao.insertNew(transactionSummary)
                        } catch (ex: Exception) {
                            Log.e(
                                "SyncManager",
                                "Upload failed for transactionSummary ${transactionSummary.tSCloudId}: ${ex.message}"
                            )
                        }
                    }
                }
            },
            deleteLocal = { key ->
                Executors.newSingleThreadExecutor().execute {
                    val summaryToDelete = TransactionSummary(tSCloudId = key.toLong())
                    transSumDao.delete_(summaryToDelete)
                }
            }
        )
    }

    // This is a placeholder for the generic syncTable function from RealtimeDatabaseSync.kt
    // for this code to be valid in this isolated file.
    private fun <T> syncTable(
        tableName: String,
        clazz: Class<T>,
        convertAndSave: (T, String) -> Unit,
        deleteLocal: ((String) -> Unit)?
    ) {
        // The actual implementation from RealtimeDatabaseSync.kt would go here.
    }
}

 */