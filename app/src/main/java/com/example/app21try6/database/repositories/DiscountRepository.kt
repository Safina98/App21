package com.example.app21try6.database.repositories

import androidx.lifecycle.LiveData
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.DiscountDao
import com.example.app21try6.database.daos.DiscountTransDao
import com.example.app21try6.stock.brandstock.CategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DiscountRepository(
    private val discountTransDao: DiscountTransDao,
    private val discountDao: DiscountDao
) {
    fun getAllDicountName(): LiveData<List<String>> {
        return discountDao.getAllDiscountName()
    }

    suspend fun getDiscountNameById(id:Int?):String?{
        return withContext(Dispatchers.IO){discountDao.getDiscountNameById(id)}
    }
    suspend fun getDiscountIdByName(discName:String):Int?{
        return withContext(Dispatchers.IO){
            discountDao.getDiscountIdByName(discName)
        }
    }

}