package com.example.app21try6.utils

import android.util.Log
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.LogsRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository

class AssignCloudIdManager(
    private val bookKeepingRepository: BookkeepingRepository,
    private val discountRepository: DiscountRepository,
    private val expensesRepository: ExpensesRepository,
    private val logsRepository: LogsRepository,
    private val stockRepository: StockRepositories,
    private val transactionRepository: TransactionsRepository,
    

    // add more repositories if needed
) {

    suspend fun assignCloudIdForAllTables() {

        /*
        Log.i("AssignCloudIdManager", "assignCloudIdForAllTables() called")
        // ---------- Customer Table ----------
        val customerList = discountRepository.getAllCustomerTable()
        customerList.forEach { customer ->
            val newId = System.currentTimeMillis()
            discountRepository.assignCloudIdToCustomerTable(newId, customer.custId)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning customer table")

        // ---------- Detail Warna Table ----------
        val detailWarnaList = stockRepository.getAllDetailWarnaTable()
        detailWarnaList.forEach { detailWarna ->
            val newId = System.currentTimeMillis()
            stockRepository.assignCloudIdToDetailWarnaTable(newId, detailWarna.id)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning DetailWarna table")

        // ---------- Discount Table ----------
        val discountList = discountRepository.getAllDiscountTable()
        discountList.forEach { discount ->
            val newId = System.currentTimeMillis()
            discountRepository.assignCloudIdToDiscountTable(newId, discount.discountId)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning Discount table")

        // ---------- Discount Transaction ----------
        val discountTransactionList = discountRepository.getAllDiscountTransactionTable()
        discountTransactionList.forEach { discountTransaction ->
            val newId = System.currentTimeMillis()
            discountRepository.assignCloudIdToDiscountTransactionTable(
                newId,
                discountTransaction.discTransId
            )
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning DiscountTransaction table")

        // ---------- Expense Category Table ----------
        val expenseCategoryList = expensesRepository.getAllExpenseCategoryTable()
        expenseCategoryList.forEach { expenseCategory ->
            val newId = System.currentTimeMillis()
            expensesRepository.assignCloudIdToExpenseCategoryTable(newId, expenseCategory.id)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning ExpenseCategory table")

        // ---------- Expense Table ----------
        val expenseList = expensesRepository.getAllExpenseTable()
        expenseList.forEach { expense ->
            val newId = System.currentTimeMillis()
            expensesRepository.assignCloudIdToExpenseTable(newId, expense.id)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning Expense table")

        // ---------- Inventory Log ----------
        val inventoryLogList = logsRepository.getAllInventoryLog()
        inventoryLogList.forEach { inventoryLog ->
            val newId = System.currentTimeMillis()
            logsRepository.assignCloudIdInventoryLog(newId, inventoryLog.id)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning InventoryLog table")

        // ---------- Inventory Purchase ----------

        val inventoryPurchaseList = expensesRepository.getAllInventoryPurchaseTable()


        inventoryPurchaseList?.forEach { inventoryPurchase ->
            val newId = System.currentTimeMillis()

            Log.i(
                "AssignCloudIdManager",
                "${inventoryPurchase?.id} ${inventoryPurchase?.subProductName} ${inventoryPurchase?.purchaseDate}"
            )
            // if (inventoryPurchase!=null)
            // Log.i("AssignCloudIdManager", "Finnish Assigning InventoryPurchase table")

            expensesRepository.assignCloudIdToInventoryPurchaseTable(newId, inventoryPurchase!!.id)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning InventoryPurchase table")


        // ---------- Merchandise Retail ----------
        val merchandiseRetailList = stockRepository.getAllMerchandiseRetailTable()
        merchandiseRetailList.forEach { merchandiseRetail ->
            val newId = System.currentTimeMillis()
            stockRepository.assignCloudIdToMerchandiseRetailTable(newId, merchandiseRetail.id)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning MerchandiseRetail table")

        // ---------- payment ----------
        val paymentList = discountRepository.getAllPaymentTable()
        paymentList.forEach { payment ->
            val newId = System.currentTimeMillis()
            discountRepository.assignCloudIdToPaymentTable(newId, payment.id)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning Payment table")

        // ---------- Product Table ----------
        val productList = stockRepository.getAllProductTable()
        productList.forEach { product ->
            val newId = System.currentTimeMillis()
            stockRepository.assignCloudIdToAllData(newId, product.product_id)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning product table")

        // ---------- Sub Product ----------
        val subProductList = stockRepository.getAllSubTable()
        subProductList.forEach { subProduct ->
            val newId = System.currentTimeMillis()
            stockRepository.assignCloudIdtoSubTable(newId, subProduct.sub_id)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning SubProduct table")

        // ---------- Summary ----------
        val summaryList = bookKeepingRepository.getAllSummaryTable()
        summaryList.forEach { summary ->
            val newId = System.currentTimeMillis()
            bookKeepingRepository.assignCloudIdtoSummaryTable(newId, summary.id_m)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning Summary table")

        // ---------- Suplier Table ----------
        val suplierList = expensesRepository.getAllSuplierTable()
        suplierList.forEach { suplier ->
            val newId = System.currentTimeMillis()
            expensesRepository.assignCloudIdToSuplierTable(newId, suplier.id)
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning SuplierTable table")

        // ---------- Transaction Detail ----------
        val transactionDetailList = transactionRepository.getAllTransactionDetail()
        transactionDetailList.forEach { transactionDetail ->
            val newId = System.currentTimeMillis()
            transactionRepository.assignCloudIdtoTransactionDetail(
                newId,
                transactionDetail.trans_detail_id
            )
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning TransactionDetail table")

        // ---------- Transaction Summary ----------
        val transactionSummaryList = transactionRepository.getAllTransactionSummary()
        transactionSummaryList.forEach { transactionSummary ->
            val newId = System.currentTimeMillis()
            transactionRepository.assignCloudIdtoTransactionSummary(newId, transactionSummary.sum_id
            )
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning TransactionSummary table")

         */
    }
}





        // ---------- Sub Table ----------


