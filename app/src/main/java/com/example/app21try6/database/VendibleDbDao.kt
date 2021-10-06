package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
/*
@Dao
interface VendibleDbDao {
    @Insert
    fun insert(vendible: Vendible)

    @Update
    fun update(vendible: Vendible)

    @Query("DELETE FROM vendible_table")
    fun deleteAll()

    @Query("DELETE FROM vendible_table WHERE brand = :brand_")
    fun deleteBrand(brand_:String)

    @Query("DELETE FROM vendible_table WHERE product = :product_")
    fun deleteProduct(product_:String)


    @Query("DELETE FROM vendible_table WHERE sub_product = :subProduct_")
    fun deleteSubProduct(subProduct_:String)

    @Query("SELECT * FROM vendible_table WHERE  sub_product = 'empty' ")
    fun getAll():LiveData<List<Vendible>>

    @Query("SELECT * FROM vendible_table GROUP BY brand ")
    fun getAllBrand():LiveData<List<Vendible>>

    @Query("SELECT * FROM vendible_table WHERE brand =:brand_ AND product = 'empty'")
    fun getAllBrand(brand_:String):LiveData<List<Vendible>>

    @Query("SELECT * FROM vendible_table WHERE brand = :brand_ AND product =:product_ AND sub_product = 'empty'")
    fun getAllProduct(brand_:String,product_: String) :LiveData<List<Vendible>>



}*/