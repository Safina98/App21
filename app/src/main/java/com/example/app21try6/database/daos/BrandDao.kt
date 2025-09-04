package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.stock.brandstock.ExportModel

@Dao
interface BrandDao {
    @Insert
    fun insert(brand: Brand)

    @Update
    fun update(brand: Brand)

    @Query("SELECT brand_id as id, brand_name as name,cath_code as parentId from brand_table WHERE cath_code IS null OR cath_code=:catId")
    fun getBrandModelByCatId(catId:Int?):List<BrandProductModel>
    @Query("SELECT  brand_name  from brand_table WHERE  cath_code=:catId")
    fun getBrandNameListByCatName(catId:Int):List<String>

    @Query("SELECT brand_id FROM brand_table WHERE brand_name =:name AND cath_code=:cath_code")
    fun getBrandIdbyName(name:String,cath_code:Int):Int?
    @Query("SELECT brand_name FROM brand_table WHERE brand_id=:id")
    fun getBrandNameById(id:Int):String

    @Query("SELECT sub_name as subProduct, " +
            "warna as warna, " +
            "roll_u as  roll_u," +
            " roll_b_t as roll_b_t, " +
            "roll_s_t as roll_s_t," +
            "roll_k_t as roll_k_t," +
            "roll_b_g as roll_b_g," +
            "roll_s_g as roll_s_g," +
            "roll_k_g as roll_k_g," +
            "product_name as product, " +
            "product_price as price, product_capital as capital," +
            "best_selling as bestSelling," +
            "default_net as defaultNet,alternate_capital as alternate_capital,alternate_price as alternate_price,  "+

            "brand_name as brand, category_name as category FROM sub_table INNER JOIN PRODUCT_TABLE ON product_id = sub_table.product_code INNER JOIN brand_table ON brand_id = sub_table.brand_code INNER JOIN category_table ON category_id = sub_table.cath_code")
    fun getExportedData():LiveData<List<ExportModel>>

    @Query("DELETE FROM brand_table WHERE brand_id = :id_")
    fun deleteBrand(id_:Int)

  //  @Query("INSERT INTO brand_table (brand_name,cath_code) SELECT :brand_name_ as brand_name, (SELECT category_id FROM category_table WHERE category_name = :caht_name_ limit 1) as cath_code FROM brand_table WHERE NOT EXISTS(SELECT brand_name,cath_code FROM brand_table WHERE brand_name =:brand_name_ AND cath_code = (SELECT category_id FROM category_table WHERE category_name = :caht_name_ limit 1)) LIMIT 1 ")
    @Query("INSERT INTO brand_table (brand_name,cath_code) SELECT :brand_name_ as brand_name, (SELECT category_id FROM category_table WHERE category_name = :caht_name_ limit 1) as cath_code WHERE NOT EXISTS (SELECT 1 FROM brand_table WHERE brand_name = :brand_name_)")
    fun insertIfNotExist(brand_name_: String, caht_name_: String)
}