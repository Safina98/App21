package com.example.app21try6.database.repositories

import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.daos.TransSumDao
import com.example.app21try6.database.tables.TransactionDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TransactionsRepository(
    private val transDetailDao:TransDetailDao,
    private val transSumDao:TransSumDao
) {
    suspend fun insertTransDetail(transDetail: TransactionDetail){
        withContext(Dispatchers.IO){
            transDetailDao.insert(transDetail)
        }
    }
    suspend fun deleteTransDetail(sum_id:Int,name:String){
        withContext(Dispatchers.IO){
           transDetailDao.deteleAnItemTransDetailSub(sum_id,name)
        }
    }
}