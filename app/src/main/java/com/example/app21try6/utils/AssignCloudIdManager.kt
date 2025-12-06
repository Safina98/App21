package com.example.app21try6.utils

import android.util.Log
import com.example.app21try6.database.repositories.BookkeepingRepository
import com.example.app21try6.database.repositories.DiscountRepository
import com.example.app21try6.database.repositories.ExpensesRepository
import com.example.app21try6.database.repositories.LogsRepository
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import kotlinx.coroutines.delay

class AssignCloudIdManager(
    private val bookKeepingRepository: BookkeepingRepository,
    private val discountRepository: DiscountRepository,
    private val expensesRepository: ExpensesRepository,
    private val logsRepository: LogsRepository,
    private val stockRepository: StockRepositories,
    private val transactionRepository: TransactionsRepository,
    

    // add more repositories if needed
) {
    suspend fun getDuplicateIds(){
        //customer
        Log.i("AssignCloudIdManager","Customer table")
        val customer =discountRepository.getCustomersWithDuplicateId()
        customer.forEach {
            Log.i("AssignCloudIdManager","${it.custId}, ${it.customerName} ${it.customerCloudId}")
        }
    }
    suspend fun assignCloudIdForAllTables() {

        /*
          Log.i("AssignCloudIdManager", "assignCloudIdForAllTables() called")
          // ---------- Customer Table ----------
          val customerList = discountRepository.getAllCustomerTable()
          customerList.forEach { customer ->
              delay(1)
              val newId = System.currentTimeMillis()
              discountRepository.assignCloudIdToCustomerTable(newId, customer.custId)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning customer table")

          // ---------- Detail Warna Table ----------
          val detailWarnaList = stockRepository.getAllDetailWarnaTable()
          detailWarnaList.forEach { detailWarna ->
              delay(1)
              val newId = System.currentTimeMillis()
              stockRepository.assignCloudIdToDetailWarnaTable(newId, detailWarna.id)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning DetailWarna table")

          // ---------- Discount Table ----------
          val discountList = discountRepository.getAllDiscountTable()
          discountList.forEach { discount ->
              delay(1)
              val newId = System.currentTimeMillis()
              discountRepository.assignCloudIdToDiscountTable(newId, discount.discountId)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning Discount table")

          // ---------- Discount Transaction ----------
          val discountTransactionList = discountRepository.getAllDiscountTransactionTable()
          discountTransactionList.forEach { discountTransaction ->
              delay(1)
              val newId = System.currentTimeMillis()
              discountRepository.assignCloudIdToDiscountTransactionTable(newId, discountTransaction.discTransId)

          }
          Log.i("AssignCloudIdManager", "Finnish Assigning DiscountTransaction table")

          // ---------- Expense Category Table ----------
          val expenseCategoryList = expensesRepository.getAllExpenseCategoryTable()
          expenseCategoryList.forEach { expenseCategory ->
              delay(1)
              val newId = System.currentTimeMillis()
              expensesRepository.assignCloudIdToExpenseCategoryTable(newId, expenseCategory.id)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning ExpenseCategory table")

          // ---------- Expense Table ----------
          val expenseList = expensesRepository.getAllExpenseTable()
          expenseList.forEach { expense ->
              delay(1)
              val newId = System.currentTimeMillis()
              expensesRepository.assignCloudIdToExpenseTable(newId, expense.id)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning Expense table")

          // ---------- Inventory Log ----------
          val inventoryLogList = logsRepository.getAllInventoryLogTable()
          inventoryLogList.forEach { inventoryLog ->
              delay(1)
              val newId = System.currentTimeMillis()
              logsRepository.assignCloudIdToInventoryLogTable(newId, inventoryLog.id)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning InventoryLog table")

          // ---------- Inventory Purchase ----------

          val inventoryPurchaseList = expensesRepository.getAllInventoryPurchaseTable()

          inventoryPurchaseList?.forEach { inventoryPurchase ->
              val newId = System.currentTimeMillis()
              delay(1)
              Log.i(
                  "AssignCloudIdManager",
                  "${inventoryPurchase?.id} ${inventoryPurchase?.subProductName} ${inventoryPurchase?.purchaseDate}"
              )

              expensesRepository.assignCloudIdToInventoryPurchaseTable(newId, inventoryPurchase!!.id)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning InventoryPurchase table")


          // ---------- Merchandise Retail ----------
          val merchandiseRetailList = stockRepository.getAllMerchandiseRetailTable()
          merchandiseRetailList.forEach { merchandiseRetail ->
              delay(1)
              val newId = System.currentTimeMillis()
              stockRepository.assignCloudIdToMerchandiseRetailTable(newId, merchandiseRetail.id)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning MerchandiseRetail table")

          // ---------- payment ----------
          val paymentList = discountRepository.getAllPaymentTable()
          paymentList.forEach { payment ->
              delay(1)
              val newId = System.currentTimeMillis()
              discountRepository.assignCloudIdToPaymentTable(newId, payment.id)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning Payment table")

          // ---------- Product Table ----------
          val productList = stockRepository.getAllProductTable()
          productList.forEach { product ->
              delay(1)
              val newId = System.currentTimeMillis()
              stockRepository.assignCloudIdToProductTable(newId, product.product_id)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning product table")

          // ---------- Sub Product ----------
          val subProductList = stockRepository.getAllDSubProductTable()
          subProductList.forEach { subProduct ->
              delay(1)
              val newId = System.currentTimeMillis()
              stockRepository.assignCloudIdToSubProductTable(newId, subProduct.sub_id)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning SubProduct table")

          // ---------- Summary ----------
          val summaryList = bookKeepingRepository.getAllSummaryTable()
          summaryList.forEach { summary ->
              delay(1)
              val newId = System.currentTimeMillis()
              bookKeepingRepository.assignCloudIdToSummaryTable(newId, summary.id_m)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning Summary table")

          // ---------- Suplier Table ----------
          val suplierList = expensesRepository.getAllSuplierTable()
          suplierList.forEach { suplier ->
              delay(1)
              val newId = System.currentTimeMillis()
              expensesRepository.assignCloudIdToSuplierTable(newId, suplier.id)
          }
          Log.i("AssignCloudIdManager", "Finnish Assigning SuplierTable table")






   */
        // ---------- Transaction Summary ----------

        val transactionSummaryList = transactionRepository.getTransactionSummariesWithDuplicateCloudIds()
        transactionSummaryList.forEach { transactionSummary ->
            Log.i("AssignCloudIdManager","${transactionSummary.tSCloudId}, ${transactionSummary.cust_name} ${transactionSummary.tSCloudId}")
            delay(1)
            val newId = System.currentTimeMillis()
            transactionRepository.assignCloudIdToTransactionSummaryTable(newId, transactionSummary.tSCloudId
            )
        }
        // ---------- Transaction Detail ----------
        Log.i("AssignCloudIdManager", "Finnish Assigning TransactionSummary table")
        val transactionDetailList = transactionRepository.getTransactionDetailsWithDuplicateCloudIds()
        transactionDetailList.forEach { transactionDetail ->
            Log.i("AssignCloudIdManager","${transactionDetail.tDCloudId}, ${transactionDetail.trans_item_name} ${transactionDetail.tDCloudId}")
            delay(1)
            val newId = System.currentTimeMillis()
            transactionRepository.assignCloudIdToTransactionDetailTable(
                newId,
                transactionDetail.tDCloudId
            )
        }
        Log.i("AssignCloudIdManager", "Finnish Assigning TransactionDetail table")
         
    }
}





        // ---------- Sub Table ----------


