package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.bookkeeping.summary.ListModel
import com.example.app21try6.transaction.transactionselect.TransSelectModel

@Dao
interface TransDetailDao {


    @Insert
    fun insert(transactionDetail: TransactionDetail)
    @Update
    fun update(transactionDetail: TransactionDetail)

    @Query("SELECT * FROM trans_detail_table WHERE sum_id =:sum_id_")
    fun selectATransDetail(sum_id_:Int):LiveData<List<TransactionDetail>>

    //@Query("SELECT SUM(total_income) FROM SUMMARY_TABLE  WHERE year = :year_ AND month = :month_ AND day = :day_ ")

    @Query("SELECT  IFNULL(SUM(total_price),0.0)  FROM TRANS_DETAIL_TABLE WHERE sum_id =:sum_id_ ")
    fun getTotalTrans(sum_id_: Int):LiveData<Double>

    @Query("DELETE  FROM trans_detail_table WHERE sum_id =:sum_id_")
    fun deleteATransDetail(sum_id_:Int)

    @Query("DELETE FROM trans_detail_table WHERE trans_detail_id=:trans_detail_id ")
    fun deleteAnItemTransDetail(trans_detail_id:Int)

    @Query("DELETE FROM trans_detail_table WHERE sum_id = :sum_id and trans_item_name =:name ")
    fun deteleAnItemTransDetailSub(sum_id:Int,name:String)

    //@Query("SELECT year as year_n,month as month_n,month_number as month_nbr, month as nama,day as day_n,day_name as day_name,SUM(total_income) as total FROM SUMMARY_TABLE  WHERE year = :year_  GROUP BY month ORDER BY month_nbr ASC")
   // @Query("SELECT sub_id as sub_product_id, sub_name as item_name FROM sub_table where product_code = :productId  ")
    @Query("SELECT s.sub_id AS sub_product_id,s.sub_name AS item_name,s.is_checked AS is_selected ,p.product_price AS item_price,t.qty as qty,t.trans_detail_id as trans_detail_id FROM sub_table s JOIN product_table p ON (S.product_code=P.product_id)  LEFT OUTER JOIN trans_detail_table t ON (S.sub_name = T.trans_item_name and T.sum_id =:sum_id_) WHERE s.product_code =:productId")
    fun getSubProduct(productId:Int,sum_id_: Int):LiveData<List<TransSelectModel>>
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