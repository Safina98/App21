package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.grafik.StockModel
import com.example.app21try6.transaction.transactionactive.TransExportModel
import com.example.app21try6.transaction.transactiondetail.TransactionDetailWithProduct
import com.example.app21try6.transaction.transactionselect.TransSelectModel
import java.util.Date

@Dao
interface TransDetailDao {


    @Insert
    fun insert(transactionDetail: TransactionDetail)
    @Update
    fun update(transactionDetail: TransactionDetail)

    @Update
    suspend fun updateItemPosition(item: TransactionDetail)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertN(transactionDetail: TransactionDetail):Long

    @Query("""
        UPDATE trans_detail_table
        SET sub_id = (
            SELECT sub_id FROM sub_table
            WHERE sub_name = trans_detail_table.trans_item_name
        )
        WHERE trans_item_name IN (SELECT sub_name FROM sub_table)
    """)
    suspend fun updateSubIdBasedOnItemName()

    @Query("""
        UPDATE trans_detail_table
        SET trans_item_name =:updatedName
        WHERE trans_item_name =:name
    """)
    suspend fun updateTransItemName(name:String,updatedName:String)

    @Transaction
    @Query("""
    SELECT td.*, p.product_id, p.product_name
    FROM trans_detail_table td
    INNER JOIN sub_table sp ON td.trans_item_name = sp.sub_name
    INNER JOIN product_table p ON sp.product_code = p.product_id
    WHERE td.sum_id = :transactionSummaryId
""")
    fun getTransactionDetailsWithProduct(transactionSummaryId: Int): LiveData<List<TransactionDetailWithProduct>>


    @Query("SELECT * FROM trans_detail_table WHERE sum_id =:sum_id_  order BY item_position asc")
    fun selectATransDetail(sum_id_:Int):LiveData<List<TransactionDetail>>
    //@Query("INSERT INTO brand_table (brand_name,cath_code) SELECT :brand_name_ as brand_name, (SELECT category_id FROM category_table WHERE category_name = :caht_name_ limit 1) as cath_code WHERE NOT EXISTS (SELECT 1 FROM brand_table WHERE brand_name = :brand_name_)")
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


    @Query("DELETE  FROM trans_detail_table WHERE sum_id =:sum_id_")
    fun deleteATransDetail(sum_id_:Int)

    @Query("DELETE FROM trans_detail_table WHERE trans_detail_id=:trans_detail_id ")
    fun deleteAnItemTransDetail(trans_detail_id:Long)

    @Query("DELETE FROM trans_detail_table WHERE sum_id = :sum_id and trans_item_name =:name ")
    fun deteleAnItemTransDetailSub(sum_id:Int,name:String)

    @Query("SELECT trans_detail_id from trans_detail_table order by sum_id DESC limit 1")
    suspend fun getLastInsertedId():Int?

    @Query("SELECT * FROM trans_detail_table WHERE sub_id IS null")
    fun selectAllNullId():List<TransactionDetail>

    //@Query("SELECT year as year_n,month as month_n,month_number as month_nbr, month as nama,day as day_n,day_name as day_name,SUM(total_income) as total FROM SUMMARY_TABLE  WHERE year = :year_  GROUP BY month ORDER BY month_nbr ASC")
   // @Query("SELECT sub_id as sub_product_id, sub_name as item_name FROM sub_table where product_code = :productId  ")
    @Query("SELECT s.sub_id AS sub_product_id,s.sub_name AS item_name,s.is_checked AS is_selected ,p.product_price AS item_price,t.qty as qty,t.trans_detail_id as trans_detail_id FROM sub_table s JOIN product_table p ON (S.product_code=P.product_id)  LEFT OUTER JOIN trans_detail_table t ON (S.sub_name = T.trans_item_name and T.sum_id =:sum_id_) WHERE s.product_code =:productId")
    fun getSubProduct(productId:Int,sum_id_: Int):LiveData<List<TransSelectModel>>

    @Query("SELECT s.sub_id AS sub_product_id,s.sub_name AS item_name,s.is_checked AS is_selected ,p.product_price AS item_price,t.qty as qty,t.trans_detail_id as trans_detail_id FROM sub_table s JOIN product_table p ON (S.product_code=P.product_id)  LEFT OUTER JOIN trans_detail_table t ON (S.sub_name = T.trans_item_name and T.sum_id =:sum_id_) WHERE s.product_code =:productId")
    fun getSubProductM(productId:Int,sum_id_: Int):List<TransSelectModel>

    @Query("SELECT COUNT(*) FROM trans_detail_table WHERE sum_id = :sumId;")
    fun getNumberOfData(sumId:Int):LiveData<Int>

   // @Query("SELECT trans_item_name as trans_item_name, qty as qty, trans_price as  trans_price, total_price as total_price, is_prepared as is_prepared, c as cust_name, product_price as price,best_selling as bestSelling,brand_name as brand, category_name as category FROM trans_detail_table INNER JOIN summary_table ON sum_id = trans_detail_table.sum_id")
   @Query("SELECT * FROM trans_sum_table LEFT JOIN trans_detail_table ON trans_sum_table.sum_id = trans_detail_table.sum_id")
    fun getExportedData():List<TransExportModel>
    @Query("SELECT * FROM trans_sum_table LEFT JOIN trans_detail_table ON trans_sum_table.sum_id = trans_detail_table.sum_id")
    fun getExportedDataNew():LiveData<List<TransExportModel>>


    @Query("""
        SELECT
            COALESCE(strftime('%Y', trans_detail_table.trans_detail_date), '2024') AS year,
            COALESCE(strftime('%m', trans_detail_table.trans_detail_date), '05') AS month,
            trans_detail_table.trans_item_name AS item_name,
            trans_detail_table.qty AS itemCount,
            category_table.category_name AS category_name,
            product_table.product_name AS product_name
        FROM trans_detail_table
        JOIN sub_table ON trans_detail_table.trans_item_name = sub_table.sub_name
        JOIN category_table ON sub_table.cath_code = category_table.category_id
        JOIN product_table ON sub_table.product_code = product_table.product_id
    """)
    fun getTransactionDetails(): LiveData<List<StockModel>>


/*
SELECT u.user_name,c.name,i.name,used.frequency
FROM users u
LEFT OUTER JOIN category c
 ON(u.id = c.user_id)
LEFT OUTER JOIN item i
 ON(i.category_id = c.id)
INNER JOIN Used
 ON(i.id = used.item_id)
WHERE u.user_id = YOUR_USER_HERE
 */






}