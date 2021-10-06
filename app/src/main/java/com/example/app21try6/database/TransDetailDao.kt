package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TransDetailDao {


    @Insert
    fun insert(transactionDetail: TransactionDetail)
    @Update
    fun update(transactionDetail: TransactionDetail)

    @Query("SELECT * FROM trans_detail_table WHERE sum_id =:sum_id_")
    fun selectATransDetail(sum_id_:Int):LiveData<List<TransactionDetail>>

    @Query("DELETE  FROM trans_detail_table WHERE sum_id =:sum_id_")
    fun deleteATransDetail(sum_id_:Int)

    @Query("DELETE FROM trans_detail_table WHERE trans_detail_id=:trans_detail_id ")
    fun deteleAnItemTransDetail(trans_detail_id:Int)

    @Query("DELETE FROM trans_detail_table WHERE sum_id = :sum_id and trans_item_name =:name ")
    fun deteleAnItemTransDetailSub(sum_id:Int,name:String)





}