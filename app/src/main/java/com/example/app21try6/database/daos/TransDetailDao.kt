package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.database.models.BarChartModel
import com.example.app21try6.database.models.ProfitDebugModel
import com.example.app21try6.database.models.TracketailWarnaModel
import com.example.app21try6.database.tables.TransactionDetail
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
    SELECT td.*, p.productCloudId, p.product_name,p.discountId as discount_id, p.product_price as product_price
    FROM trans_detail_table td
    INNER JOIN sub_table sp ON td.sPCloudId = sp.sPCloudId
    INNER JOIN product_table p ON sp.productCloudId = p.productCloudId
    WHERE td.tSCloudId = :transactionSummaryId
""")
    fun getTransactionDetailsWithProductList(transactionSummaryId: Long): List<TransactionDetailWithProduct>?

    @Query("SELECT * FROM trans_detail_table WHERE tSCloudId =:tSCloudId_  order BY item_position asc")
    fun selectATransDetail(tSCloudId_:Long):LiveData<List<TransactionDetail>>
    //@Query("INSERT INTO brand_table (brand_name,cath_code) SELECT :brand_name_ as brand_name, (SELECT categoryCloudId FROM category_table WHERE category_name = :caht_name_ limit 1) as cath_code WHERE NOT EXISTS (SELECT 1 FROM brand_table WHERE brand_name = :brand_name_)")
    @Query("INSERT OR IGNORE INTO trans_detail_table (tSCloudId, trans_item_name, qty, trans_price, total_price, is_prepared,trans_detail_date,unit,unit_qty,item_position) " +
            "SELECT tSCloudId, :transItemName, :qty, :transPrice, :totalPrice, :isPrepared,:trans_detail_date,:unit,:unit_qty,:item_position FROM trans_sum_table WHERE ref = :ref")
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

    @Query("SELECT  IFNULL(SUM(total_price),0.0)  FROM TRANS_DETAIL_TABLE WHERE tSCloudId =:tSCloudId_ ")
    fun getTotalTrans(tSCloudId_: Long):LiveData<Double>
    @Query("SELECT  IFNULL(SUM(total_price),0.0)  FROM TRANS_DETAIL_TABLE WHERE tSCloudId =:tSCloudId_ ")
    fun getTotalTransaction(tSCloudId_: Long):Double

    @Query("DELETE FROM trans_detail_table WHERE is_deleted = 1")
    fun deteleTheIsDeleted()


    @Query("DELETE FROM trans_detail_table WHERE tDCloudId=:tDCloudId ")
    fun deleteAnItemTransDetail(tDCloudId:Long)

    @Query("DELETE FROM trans_detail_table WHERE tSCloudId = :tSCloudId and trans_item_name =:name ")
    fun deteleAnItemTransDetailSub(tSCloudId:Long,name:String)


    @Query("SELECT s.sPCloudId AS sPCloudId," +
            "s.sub_name AS item_name," +
            "s.is_checked AS selected ," +
            "p.product_price AS item_price," +
            "p.product_price AS item_default_price," +
            "t.qty as qty,t.tDCloudId as trans_detail_id, " +
            "p.product_capital AS trans_capital "+
            " FROM sub_table s" +
            " JOIN product_table p ON (S.productCloudId=P.productCloudId) " +
            " LEFT OUTER JOIN trans_detail_table t ON (S.sub_name = T.trans_item_name and T.tSCloudId =:tSCloudId_) WHERE s.productCloudId =:productId" +
            " ORDER BY s.sub_name ASC,t.tDCloudId ASC")
    fun getSubProductMLive(productId:Long,tSCloudId_: Long):LiveData<List<TransSelectModel>>


    @Query("""
    UPDATE trans_detail_table 
    SET product_capital = :newCapitalValue 
    WHERE trans_detail_date >= :targetDate 
    AND sPCloudId IN (
        SELECT sPCloudId FROM sub_table 
        WHERE productCloudId = :productId
    )
    AND unit IS null
""")
    suspend fun updateProductCapitalAfterDate(
        targetDate: Date,
        newCapitalValue: Int,
        productId: Long
    )
    @Query("""
    SELECT td.* FROM trans_detail_table td
    JOIN sub_table s ON td.sPCloudId = s.sPCloudId
    JOIN product_table p  ON s.productCloudId = p.productCloudId
    JOIN brand_table b ON p.brand_code = b.brandCloudId
    WHERE b.brand_name = :brandName
""")
    suspend fun getTransactionDetailsByBrandName(brandName: String): List<TransactionDetail>

    @Query("""
        SELECT
            p.product_name AS label,
            SUM(td.qty * td.unit_qty) AS value
        FROM trans_detail_table td
        JOIN sub_table s ON td.sPCloudId = s.sPCloudId
        JOIN product_table p ON s.productCloudId = p.productCloudId
        JOIN brand_table b ON p.brand_code = b.brandCloudId
        JOIN category_table c ON b.cath_code = c.categoryCloudId
        
        WHERE
        (:month IS NULL OR substr(td.trans_detail_date, 6, 2) = :month)
      AND (:year IS NULL OR substr(td.trans_detail_date, 1, 4) = :year)
      AND (:product IS NULL OR p.product_name = :product)
      AND (:category IS NULL OR c.category_name = :category)
      GROUP BY p.product_name
    ORDER BY value DESC
    """)
    fun getFilteredProductBarChartList(year:String?,month:String?,product:String?,category:String?): List<BarChartModel>


    @Query("""
        SELECT
            substr(ts.trans_date, 6, 2) AS label,
            SUM(ts.total_after_discount) AS value
        FROM trans_sum_table ts
        WHERE
        (:year IS NULL OR substr(ts.trans_date, 1, 4) = :year)
        AND (:customerName is null OR ts.cust_name= :customerName)
      GROUP BY substr(ts.trans_date, 6, 2)
    ORDER BY substr(ts.trans_date, 6, 2) ASC
    """)
    fun getFilteredOmzetBarChartList(year:String?, customerName:String?): List<BarChartModel>

    @Query("""
         SELECT
            substr(ts.trans_date, 6, 2) AS label,
            SUM((td.trans_price - td.product_capital)*td.qty*td.unit_qty) AS value
        FROM trans_detail_table td
        JOIN trans_sum_table ts ON td.tSCloudId=ts.tSCloudId
        JOIN sub_table s ON td.sPCloudId = s.sPCloudId
        JOIN product_table p ON s.productCloudId = p.productCloudId
        JOIN brand_table b ON p.brand_code = b.brandCloudId
        JOIN category_table c ON b.cath_code = c.categoryCloudId
        WHERE
      (:year IS NULL OR substr(td.trans_detail_date, 1, 4) = :year)
      AND (:product IS NULL OR p.product_name = :product)
      AND (:category IS NULL OR c.category_name = :category)
      AND td.unit IS NULL
      AND td.total_price!=0
      GROUP BY substr(ts.trans_date, 6, 2)
    ORDER BY substr(ts.trans_date, 6, 2) ASC
    """)
    fun getFilteredProfitBarChartList(year:String?,product:String?,category:String?): List<BarChartModel>

    @Query("""
        SELECT
            s.sub_name AS label,
            SUM(td.qty * td.unit_qty) AS value
        FROM trans_detail_table td
        JOIN sub_table s ON td.sPCloudId = s.sPCloudId
        JOIN product_table p ON s.productCloudId = p.productCloudId
        JOIN brand_table b ON p.brand_code = b.brandCloudId
        JOIN category_table c ON b.cath_code = c.categoryCloudId
        
        WHERE
        (:month IS NULL OR substr(td.trans_detail_date, 6, 2) = :month)
      AND (:year IS NULL OR substr(td.trans_detail_date, 1, 4) = :year)
      AND (:product IS NULL OR p.product_name = :product)
      AND (:category IS NULL OR c.category_name = :category)
      GROUP BY s.sub_name
    ORDER BY value DESC
    """)
    fun getFilteredSubBarChartList(year:String?,month:String?,product:String?,category:String?): List<BarChartModel>

    //TODO DELETE LATER
    @Query("SELECT" +
            " SUM (total_price) " +
            "FROM trans_detail_table " +
            "WHERE unit IS NOT NULL " +
            "AND (:year IS NULL OR substr(trans_detail_date, 1, 4) = :year) " +
            "AND substr(trans_detail_date, 6, 2) BETWEEN '01' AND '03'")
    fun sumOfTransDetailWithNonNullUnnit(year: String?): Double

    //TODO DELETE LATER
    @Query("SELECT " +
            "COUNT (*) " +
            "FROM trans_detail_table WHERE product_capital==0")
    fun selectProductCapital():Int

    @Query("""
        SELECT
            s.sub_name AS label,
            SUM(td.qty * td.unit_qty) AS value
        FROM trans_detail_table td
        JOIN sub_table s ON td.sPCloudId = s.sPCloudId
        JOIN product_table p ON s.productCloudId = p.productCloudId
        JOIN brand_table b ON p.brand_code = b.brandCloudId
        JOIN category_table c ON b.cath_code = c.categoryCloudId
        
        WHERE
        (:month IS NULL OR substr(td.trans_detail_date, 6, 2) = :month)
      AND (:year IS NULL OR substr(td.trans_detail_date, 1, 4) = :year)
      AND (:category IS NULL OR c.category_name = :category)
      GROUP BY s.sub_name
    ORDER BY value DESC
    """)
    fun getFilteredSubBarChartList(year:String?,month:String?,category:String?): List<BarChartModel>



//    @Query("""
//    SELECT
//    td.tDCloudId AS transDetailId,
//    td.tSCloudId AS transSumId,
//    p.product_name AS itemName,
//    td.trans_detail_date AS date,
//    td.qty AS qty,
//    td.unit_qty AS unitQty,
//    td.unit AS unit,
//    ts.cust_name AS customerName,
//    (td.trans_price -td.product_capital) AS totalProfit,
//    td.trans_price AS totalTrans
//    FROM trans_detail_table td
//    JOIN trans_sum_table ts ON td.tSCloudId = ts.tSCloudId
//    JOIN sub_table s ON td.sPCloudId=s.sPCloudId
//    JOIN product_table p ON s.productCloudId=p.productCloudId
//    WHERE
//    (:month IS NULL OR substr(td.trans_detail_date, 6, 2) = :month)
//    AND (:year IS NULL OR substr(td.trans_detail_date, 1, 4) = :year)
//    AND unit is null
//    AND td.qty>=1
//""")
@Query("""
       SELECT
            substr(td.trans_detail_date, 6, 2) AS label,
            SUM((td.trans_price-td.product_capital)*td.qty*td.unit_qty) AS value
        FROM trans_detail_table td
             WHERE
                (:year IS NULL OR substr(td.trans_detail_date, 1, 4) = :year)
                AND (:month IS NULL OR substr(td.trans_detail_date, 6, 4) = :month)
                AND td.unit IS NOT NULL
      GROUP BY substr(td.trans_detail_date, 6, 2)
    ORDER BY value DESC
    """)
    fun getBigProfitTransDetail(year: String?, month: String?): List<BarChartModel>

    @Query("""
       SELECT
            substr(td.trans_detail_date, 6, 2) AS label,
            SUM(td.qty*td.unit_qty) AS value
        FROM trans_detail_table td
         JOIN sub_table s ON td.sPCloudId = s.sPCloudId
         JOIN product_table p ON s.productCloudId = p.productCloudId
        JOIN brand_table b ON p.brand_code = b.brandCloudId
        JOIN category_table c ON b.cath_code = c.categoryCloudId
        WHERE
       (:year IS NULL OR substr(td.trans_detail_date, 1, 4) = :year)
       AND (:category IS NULL OR c.category_name= :category)
        AND (:product IS NULL OR p.product_name= :product)
        AND (:sp IS NULL OR s.sub_name= :sp)
      GROUP BY substr(td.trans_detail_date, 6, 2)
    ORDER BY substr(td.trans_detail_date, 6, 2) ASC
    """)
    fun getMonthlyProductTrendList(year:String?,category: String?,product: String?,sp:String?): List<BarChartModel>

    @Query("""
       SELECT
            COALESCE(substr(td.trans_detail_date, 1, 4), 'Unknown') AS label,
            SUM(td.qty*td.unit_qty) AS value
        FROM trans_detail_table td
            JOIN sub_table s ON td.sPCloudId = s.sPCloudId
            JOIN product_table p ON s.productCloudId = p.productCloudId
            JOIN brand_table b ON p.brand_code = b.brandCloudId            
            JOIN category_table c ON b.cath_code = c.categoryCloudId
             WHERE
        (:category IS NULL OR c.category_name= :category)
        AND (:product IS NULL OR p.product_name= :product)
        AND (:sp IS NULL OR s.sub_name= :sp)
      GROUP BY substr(td.trans_detail_date, 1, 4)
    ORDER BY substr(td.trans_detail_date, 1, 4) ASC
    """)
    fun getYearlyProductTrendList(category: String?,product: String?,sp:String?): List<BarChartModel>

    @Query("""
       SELECT
            cs.customerBussinessName AS label,
            SUM(td.qty*td.unit_qty) AS value
        FROM trans_detail_table td
        JOIN trans_sum_table ts  ON td.tSCloudId = ts.tSCloudId
        INNER JOIN customer_table cs ON ts.custId = cs.custId
            JOIN sub_table s ON td.sPCloudId = s.sPCloudId
            JOIN product_table p ON s.productCloudId = p.productCloudId
            JOIN brand_table b ON p.brand_code = b.brandCloudId
            JOIN category_table c ON b.cath_code = c.categoryCloudId
             WHERE
              (:year IS NULL OR substr(td.trans_detail_date, 1, 4) = :year) 
        AND (:category IS NULL OR c.category_name= :category)
        AND (:product IS NULL OR p.product_name= :product)
        AND (:sp IS NULL OR s.sub_name= :sp)
      GROUP BY cs.customerBussinessName
    ORDER BY value DESC
    """)
    fun getCustomerCountList(year:String?,category: String?,product: String?,sp:String?): List<BarChartModel>


    @Query("SELECT SUM(qty*unit_qty) " +
            "FROM trans_detail_table td " +
            "JOIN sub_table s ON td.sPCloudId = s.sPCloudId " +
            "WHERE " +
            "s.productCloudId =:productId" +
            " AND (:startDate IS NULL OR td.trans_detail_date >= :startDate)\n" +
            "             AND (:endDate IS NULL OR td.trans_detail_date <= :endDate)")
    fun getQtyPerProduct(productId:Long,startDate: Date?,endDate: Date?): Double

    @Query("SELECT COUNT(td.tDCloudId) " +
            "FROM trans_detail_table td " +
            "JOIN sub_table s ON td.sPCloudId = s.sPCloudId " +
            "WHERE " +
            "s.productCloudId =:productId" +
            " AND (:startDate IS NULL OR td.trans_detail_date >= :startDate)\n" +
            "             AND (:endDate IS NULL OR td.trans_detail_date <= :endDate)")
    fun getTransCountPerProduct(productId:Long,startDate: Date?,endDate: Date?): Int

    @Query("""
    SELECT COUNT(DISTINCT ts.cust_name)
    FROM trans_detail_table td
    JOIN trans_sum_table ts ON td.tSCloudId = ts.tSCloudId
     JOIN sub_table s ON td.sPCloudId = s.sPCloudId  
            WHERE
            s.productCloudId =:productId
             AND (:startDate IS NULL OR td.trans_detail_date >= :startDate)
                        AND (:endDate IS NULL OR td.trans_detail_date <= :endDate)
""")

    fun getCustomerCountPerProduct(productId:Long,startDate: Date?,endDate: Date?): Int

    @Query("""
    SELECT 
        d.tDCloudId,
        d.tsCloudId,
        d.trans_item_name,
        d.qty,
        d.trans_detail_date AS tans_detail_date,
        d.unit_qty,
        d.is_cutted,
        s.cust_name,
        s.sum_note
    FROM trans_detail_table AS d
    INNER JOIN trans_sum_table AS s ON d.tSCloudId = s.tSCloudId
    WHERE
     trans_item_name LIKE '%' || :query || '%' 
    AND (:startDate IS NULL OR d.trans_detail_date >= :startDate)
             AND (:endDate IS NULL OR d.trans_detail_date <= :endDate)
              LIMIT :limit OFFSET :offset
    """)
    fun getTracketailWarnaModels(query: String,startDate:Date?,endDate:Date?,limit: Int, offset: Int): List<TracketailWarnaModel>

    @Query("""
        UPDATE trans_detail_table
        SET tDCloudId =:cloudId
        WHERE tDCloudId=:id
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
    @Query("UPDATE trans_detail_table SET needs_syncs = 0 WHERE tDCloudId = :cloudId")
    suspend fun markAsSynced(cloudId: Long)

    @Query("SELECT * FROM trans_detail_table WHERE tDCloudId=:id")
    fun selectTransDetailById(id:Long):TransactionDetail

}