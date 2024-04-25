package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.stock.brandstock.ExportModel

@Dao
interface BrandDao {
    @Insert
    fun insert(brand: Brand)

    @Update
    fun update(brand: Brand)

    @Query("SELECT * FROM brand_table WHERE cath_code = :caht_Code")
    fun getAll(caht_Code:Int): LiveData<List<Brand>>
    @Query("SELECT * FROM brand_table")
    fun getAllBrand(): LiveData<List<Brand>>

    @Query("SELECT category_id FROM category_table WHERE category_name =:categoryName ")
    fun getKategoriIdByName(categoryName:String):Int

    @Query("SELECT * FROM brand_table WHERE cath_code = :kat_id")
    fun getBrandByKatId(kat_id:Int):List<Brand>

    @Query("SELECT sub_name as subProduct, warna as warna, roll_u as  roll_u, roll_b_t as roll_b_t, roll_s_t as roll_s_t,roll_k_t as roll_k_t,roll_b_g as roll_b_g,roll_s_g as roll_s_g,roll_k_g as roll_k_g,product_name as product, product_price as price,best_selling as bestSelling,brand_name as brand, category_name as category FROM sub_table INNER JOIN PRODUCT_TABLE ON product_id = sub_table.product_code INNER JOIN brand_table ON brand_id = sub_table.brand_code INNER JOIN category_table ON category_id = sub_table.cath_code")
    fun getExportedData():LiveData<List<ExportModel>>

    @Query("SELECT category_name as category, brand_name as brand,product_name as product, product_price as price,best_selling as bestSelling,sub_name as subProduct, warna as warna, roll_u as  roll_u, roll_b_t as roll_b_t, roll_s_t as roll_s_t,roll_k_t as roll_k_t,roll_b_g as roll_b_g,roll_s_g as roll_s_g,roll_k_g as roll_k_g  FROM category_table INNER JOIN brand_table ON brand_table.cath_code = category_table.category_id INNER JOIN PRODUCT_TABLE ON product_table.brand_code= brand_table.brand_id INNER JOIN sub_table ON sub_table.product_code = product_table.product_id")
    fun getExportedDataNew():LiveData<List<ExportModel>>

    @Query("DELETE FROM brand_table WHERE brand_id = :id_")
    fun deleteBrand(id_:Int)

    @Query("SELECT brand_id FROM brand_table WHERE brand_name = :brand_name LIMIT 1")
    fun getCode(brand_name:String):Int

    @Query("INSERT INTO brand_table (brand_name,cath_code) VALUES (:brand_name_,(SELECT category_id FROM category_table WHERE category_name = :caht_Code_))")
    fun insert_try(brand_name_:String,caht_Code_: String)

  //  @Query("INSERT INTO brand_table (brand_name,cath_code) SELECT :brand_name_ as brand_name, (SELECT category_id FROM category_table WHERE category_name = :caht_name_ limit 1) as cath_code FROM brand_table WHERE NOT EXISTS(SELECT brand_name,cath_code FROM brand_table WHERE brand_name =:brand_name_ AND cath_code = (SELECT category_id FROM category_table WHERE category_name = :caht_name_ limit 1)) LIMIT 1 ")
    @Query("INSERT INTO brand_table (brand_name,cath_code) SELECT :brand_name_ as brand_name, (SELECT category_id FROM category_table WHERE category_name = :caht_name_ limit 1) as cath_code WHERE NOT EXISTS (SELECT 1 FROM brand_table WHERE brand_name = :brand_name_)")
    fun insertIfNotExist(brand_name_: String, caht_name_: String)
}