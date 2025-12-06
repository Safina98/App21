package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.bookkeeping.summary.MonthlyProfit
import com.example.app21try6.database.models.TracketailWarnaModel
import com.example.app21try6.database.tables.DiscountTable
import com.example.app21try6.database.tables.MerchandiseRetail
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.tables.TransactionSummary
import com.example.app21try6.grafik.StockModel
import com.example.app21try6.transaction.transactionactive.TransExportModel
import com.example.app21try6.transaction.transactiondetail.TransactionDetailWithProduct
import com.example.app21try6.transaction.transactionselect.TransSelectModel
import java.util.Date

@Dao
interface TransDetailDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(transactionDetail: TransactionDetail):Long
    @Update
    fun update(transactionDetail: TransactionDetail)
    @Update
    suspend fun updateItemPosition(item: TransactionDetail)



    @Query("""
    SELECT td.*, p.product_id, p.product_name,p.discountId as discount_id, p.product_price as product_price
    FROM trans_detail_table td
    INNER JOIN sub_table sp ON td.sub_id = sp.sub_id
    INNER JOIN product_table p ON sp.product_code = p.product_id
    WHERE td.sum_id = :transactionSummaryId
""")
    fun getTransactionDetailsWithProductList(transactionSummaryId: Int): List<TransactionDetailWithProduct>?

    @Query("SELECT * FROM trans_detail_table WHERE sum_id =:sum_id_  order BY item_position asc")
    fun selectATransDetail(sum_id_:Int):LiveData<List<TransactionDetail>>
    //@Query("INSERT INTO brand_table (brand_name,cath_code) SELECT :brand_name_ as brand_name, (SELECT categoryCloudId FROM category_table WHERE category_name = :caht_name_ limit 1) as cath_code WHERE NOT EXISTS (SELECT 1 FROM brand_table WHERE brand_name = :brand_name_)")
    @Query("INSERT OR IGNORE INTO trans_detail_table (sum_id, trans_item_name, qty, trans_price, total_price, is_prepared,trans_detail_date,unit,unit_qty,item_position) " +
            "SELECT sum_id, :transItemName, :qty, :transPrice, :totalPrice, :isPrepared,:trans_detail_date,:unit,:unit_qty,:item_position FROM trans_sum_table WHERE ref = :ref")
    suspend fun insertTransactionDetailWithRef(
        ref: String,
        transItemName: String,
        qty: Double,
        transPrice: Int,
        totalPrice: Double,
        isPrepared: Boolean,
        trans_detail_date: Date?,
        unit:String?,
        unit_qty:Double,
        item_position:Int
    )

    @Query("SELECT  IFNULL(SUM(total_price),0.0)  FROM TRANS_DETAIL_TABLE WHERE sum_id =:sum_id_ ")
    fun getTotalTrans(sum_id_: Int):LiveData<Double>
    @Query("SELECT  IFNULL(SUM(total_price),0.0)  FROM TRANS_DETAIL_TABLE WHERE sum_id =:sum_id_ ")
    fun getTotalTransaction(sum_id_: Int):Double


    @Query("DELETE FROM trans_detail_table WHERE trans_detail_id=:trans_detail_id ")
    fun deleteAnItemTransDetail(trans_detail_id:Long)

    @Query("DELETE FROM trans_detail_table WHERE sum_id = :sum_id and trans_item_name =:name ")
    fun deteleAnItemTransDetailSub(sum_id:Int,name:String)


    @Query("SELECT s.sub_id AS sub_product_id," +
            "s.sub_name AS item_name," +
            "s.is_checked AS selected ," +
            "p.product_price AS item_price," +
            "p.product_price AS item_default_price," +

            "t.qty as qty,t.trans_detail_id as trans_detail_id" +
            " FROM sub_table s" +
            " JOIN product_table p ON (S.product_code=P.product_id) " +
            " LEFT OUTER JOIN trans_detail_table t ON (S.sub_name = T.trans_item_name and T.sum_id =:sum_id_) WHERE s.product_code =:productId" +
            " ORDER BY s.sub_name ASC,t.trans_detail_id ASC")
    fun getSubProductMLive(productId:Int,sum_id_: Int):LiveData<List<TransSelectModel>>


    @Query("""
        SELECT
           COALESCE(strftime('%Y', trans_detail_table.trans_detail_date), '1990') AS year,
            COALESCE(strftime('%m', trans_detail_table.trans_detail_date), '05') AS month,
            trans_detail_table.trans_item_name AS item_name,
            (trans_detail_table.qty * trans_detail_table.unit_qty) AS itemCount,
            category_table.category_name AS category_name,
            product_table.product_name AS product_name,
            sub_table.sub_name AS sub_name,
            product_table.product_id AS product_id,
            trans_detail_table.sub_id AS sub_id,
            trans_detail_table.product_capital AS product_capital,
            trans_detail_table.trans_price AS price,
            trans_detail_table.total_price AS total_income
        FROM trans_detail_table
        JOIN sub_table ON trans_detail_table.sub_id = sub_table.sub_id
        JOIN category_table ON sub_table.cath_code = category_table.categoryCloudId
        JOIN product_table ON sub_table.product_code = product_table.product_id
    """)
    fun getTransactionDetails(): LiveData<List<StockModel>>

    @Query("""
        SELECT
            COALESCE(strftime('%Y', trans_detail_table.trans_detail_date), '1990') AS year,
            COALESCE(strftime('%m', trans_detail_table.trans_detail_date), '05') AS month,
            trans_detail_table.trans_item_name AS item_name,
            (trans_detail_table.qty * trans_detail_table.unit_qty) AS itemCount,
            category_table.category_name AS category_name,
            product_table.product_name AS product_name,
            sub_table.sub_name AS sub_name,
            product_table.product_id AS product_id,
            trans_detail_table.sub_id AS sub_id,
            trans_detail_table.product_capital AS product_capital,
            trans_detail_table.trans_price AS price,
            trans_detail_table.total_price AS total_income
        FROM trans_detail_table
        JOIN sub_table ON trans_detail_table.sub_id = sub_table.sub_id
        JOIN category_table ON sub_table.cath_code = category_table.categoryCloudId
        JOIN product_table ON sub_table.product_code = product_table.product_id
    """)
    fun getTransactionDetailsList(): List<StockModel>

    @Query("""
    SELECT 
        d.trans_detail_id,
        d.sum_id,
        d.trans_item_name,
        d.qty,
        d.trans_detail_date AS tans_detail_date,
        d.sub_id,
        d.unit_qty,
        d.is_cutted,
        s.cust_name,
        s.sum_note
    FROM trans_detail_table AS d
    INNER JOIN trans_sum_table AS s ON d.sum_id = s.sum_id
    WHERE
     trans_item_name LIKE '%' || :query || '%' 
    AND (:startDate IS NULL OR d.trans_detail_date >= :startDate)
             AND (:endDate IS NULL OR d.trans_detail_date <= :endDate)
    """)
    fun getTracketailWarnaModels(query: String,startDate:Date?,endDate:Date?): List<TracketailWarnaModel>

    @Query("""
        UPDATE trans_detail_table
        SET tDCloudId =:cloudId
        WHERE trans_detail_id=:id
    """)
    fun assignTransactionDetailCloudID(cloudId:Long,id:Long)

    @Query("SELECT * FROM trans_detail_table")
    fun selectAllTransactionDetailTable(): List<TransactionDetail>

    @Query("""
    SELECT *
    FROM trans_detail_table
    WHERE tDCloudId IN (
        SELECT tDCloudId
        FROM trans_detail_table
        GROUP BY tDCloudId
        HAVING COUNT(tDCloudId) > 1
    )
    ORDER BY tDCloudId
""")
    fun getTransactionDetailsWithDuplicateCloudIds(): List<TransactionDetail>

}