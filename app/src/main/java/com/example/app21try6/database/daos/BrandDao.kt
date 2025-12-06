package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.stock.brandstock.ExportModel

@Dao
interface BrandDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(brand: Brand):Long
    @Update
    fun update(brand: Brand)
    @Query("SELECT * FROM brand_table WHERE brandCloudId = :id LIMIT 1")
    suspend fun getById(id: Long): Brand?
    @Query("SELECT * FROM brand_table WHERE needs_syncs = 1")
    fun getPendingSync(): List<Brand>

    @Query("UPDATE brand_table SET needs_syncs = 0 WHERE brandCloudId = :id")
    fun markSynced(id: Long)

    @Query("""
    SELECT brandCloudId AS id,
           brand_name AS name,
           cath_code AS parentId
    FROM brand_table
    WHERE (:catId IS NULL OR cath_code = :catId)
""")
    fun getBrandModelByCatId(catId:Long?):List<BrandProductModel>

    // to be deleted
    @Query("SELECT * FROM brand_table")
    fun getAllBrand():List<Brand>

    @Query("SELECT  brand_name  from brand_table WHERE  cath_code=:catId")
    fun getBrandNameListByCatName(catId:Long):List<String>

    @Query("SELECT brandCloudId FROM brand_table WHERE brand_name =:name AND cath_code=:cath_code")
    fun getBrandIdbyName(name:String,cath_code:Long):Long?
    @Query("SELECT brand_name FROM brand_table WHERE brandCloudId=:id")
    fun getBrandNameById(id:Long):String

    @Query("SELECT cath_code FROM brand_table WHERE brandCloudId=:id")
    fun getCtgIdByBrandId(id:Long?): Long?

    @Query("""
    SELECT 
        sub_table.sub_name AS subProduct,
        sub_table.warna AS warna,
        sub_table.roll_u AS roll_u,
        product_table.product_name AS product,
        product_table.product_price AS price,
        product_table.product_capital AS capital,
        product_table.best_selling AS bestSelling,
        product_table.default_net AS defaultNet,
        product_table.alternate_capital AS alternate_capital,
        product_table.alternate_price AS alternate_price,
        brand_table.brand_name AS brand,
        category_table.category_name AS category
    FROM sub_table
    INNER JOIN product_table 
        ON product_table.productCloudId = sub_table.productCloudId
    INNER JOIN brand_table 
        ON brand_table.brandCloudId = sub_table.brand_code
    INNER JOIN category_table 
        ON category_table.categoryCloudId = sub_table.cath_code
""")
    fun getExportedData(): LiveData<List<ExportModel>>

    @Query("DELETE FROM brand_table WHERE brandCloudId = :id_")
    fun deleteBrand(id_: Long)

  //  @Query("INSERT INTO brand_table (brand_name,cath_code) SELECT :brand_name_ as brand_name, (SELECT categoryCloudId FROM category_table WHERE category_name = :caht_name_ limit 1) as cath_code FROM brand_table WHERE NOT EXISTS(SELECT brand_name,cath_code FROM brand_table WHERE brand_name =:brand_name_ AND cath_code = (SELECT categoryCloudId FROM category_table WHERE category_name = :caht_name_ limit 1)) LIMIT 1 ")
    @Query("INSERT INTO brand_table (brand_name,cath_code) SELECT :brand_name_ as brand_name, (SELECT categoryCloudId FROM category_table WHERE category_name = :caht_name_ limit 1) as cath_code WHERE NOT EXISTS (SELECT 1 FROM brand_table WHERE brand_name = :brand_name_)")
    fun insertIfNotExist(brand_name_: String, caht_name_: String)
}