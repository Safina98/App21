package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.TransactionDetail

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(product: Product)
    @Update
    fun update(product: Product)
    @Query("SELECT productCloudId as id,product_name as name,brand_code as parentId from product_table WHERE (:brandCloudId_ IS NULL OR brand_code = :brandCloudId_)")
    fun getAll(brandCloudId_:Long?): List<BrandProductModel>

    @Query("UPDATE product_table SET needs_syncs = 0 WHERE productCloudId = :cloudId")
    suspend fun markAsSynced(cloudId: Long)
// todo delete later

    @Query("""
    SELECT *
    FROM product_table
    WHERE productCloudId IN (
        SELECT productCloudId
        FROM product_table
        GROUP BY productCloudId
        HAVING COUNT(productCloudId) > 1
    )
    ORDER BY productCloudId
""")
    fun getPriductsWithDuplicateCloudIds(): List<Product>
    @Transaction
    fun updateSubForeignKeysFromProduct() {
       // updateSubCathCodeFromProduct()
        //updateSubBrandCodeFromProduct()
    }
    //@Query("SELECT year as year_n,month as month_n,month_number as month_nbr, month as nama,day as day_n,day_name as day_name,SUM(total_income) as total FROM SUMMARY_TABLE  WHERE year = :year_  GROUP BY month ORDER BY month_nbr ASC")

    @Query("SELECT * FROM product_table WHERE productCloudId =:id")
    fun getProductById(id:Long):Product?


    @Query("SELECT brand_code FROM product_table WHERE productCloudId =:id")
    fun getBrandIdByProductId(id:Long?):Long?

    @Query("SELECT * FROM product_table p join brand_table b on p.brand_code = b.brandCloudId WHERE b.cath_code = :c_id_")
    fun getCategoriedProduct(c_id_:Long): LiveData<List<Product>>

    //@Query("SELECT * FROM product_table WHERE cath_code = :c_id_")
    @Query("SELECT * FROM product_table p join brand_table b on p.brand_code = b.brandCloudId WHERE :c_id_ IS NULL OR b.cath_code = :c_id_")
    fun getProductByCategory(c_id_: Long?): List<Product>

    @Query("""
        SELECT p.product_name FROM product_table p 
        join brand_table b on p.brand_code = b.brandCloudId
        JOIN category_table c ON b.cath_code = c.categoryCloudId
        WHERE c.category_name = :name
    """)
    fun getProductNameByCategoryName(name:String):List<String>


    @Query("SELECT * FROM product_table")
    fun getAllProduct(): LiveData<List<Product>>

    @Query("DELETE FROM product_table WHERE productCloudId= :id_")
    fun delete(id_:Long)

    @Query("INSERT INTO product_table (product_name,product_price,checkBoxBoolean,best_selling,product_capital,default_net,alternate_capital,alternate_price,brand_code) SELECT :product_name_ as product_name,:product_price_ as product_price, 0 as checkBoxBoolean,:best_selling_ as best_selling,:product_capital as product_capital,:defaultNet as default_net,:alternate_capital as alternate_capital,:alternate_price as alternate_price,(SELECT brandCloudId FROM brand_table WHERE brand_name = :brand_code_ limit 1) as brand_code WHERE NOT EXISTS (SELECT 1 FROM product_table WHERE product_name = :product_name_)")
    fun insertIfNotExist(product_name_:String,product_price_:Int, product_capital:Int,defaultNet:Double,alternate_capital:Double,alternate_price:Double,best_selling_:Boolean,brand_code_:String)
    
    @Query("""
        UPDATE product_table
        SET productCloudId =:cloudId
        WHERE productCloudId=:id
    """)
    fun assignProductCloudID(cloudId:Long,id: Long)

    @Query("SELECT * FROM product_table")
    fun selectAllProductTable(): List<Product>

    @Query("SELECT productCloudId FROM product_table WHERE product_name=:name")
    fun getProductIdByProductName(name:String):Long

}