package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.app21try6.bookkeeping.summary.ListModel
import com.example.app21try6.transaction.transactionselect.TransSelectModel

@Dao
interface ProductDao {
    @Insert
    fun insert(product: Product)
    @Update
    fun update(product: Product)
    @Query("SELECT * FROM product_table WHERE brand_code = :brand_id_")
    fun getAll(brand_id_:Int): LiveData<List<Product>>


    //@Query("SELECT year as year_n,month as month_n,month_number as month_nbr, month as nama,day as day_n,day_name as day_name,SUM(total_income) as total FROM SUMMARY_TABLE  WHERE year = :year_  GROUP BY month ORDER BY month_nbr ASC")

    @Query("SELECT * FROM product_table WHERE cath_code = :c_id_")
    fun getCategoriedProduct(c_id_:Int): LiveData<List<Product>>
    //@Query("SELECT * FROM product_table WHERE cath_code = :c_id_")
    @Query("SELECT * FROM product_table WHERE :c_id_ IS NULL OR cath_code = :c_id_")
    fun getProductByCategory(c_id_:Int?): List<Product>
    @Query("SELECT * FROM product_table")
    fun getAllProducts(): List<Product>
    @Query("SELECT * FROM product_table")
    fun getAllProduct(): LiveData<List<Product>>
    @Query("DELETE FROM product_table WHERE product_id= :id_")
    fun delete(id_:Int)
    @Query("INSERT INTO product_table (product_name,product_price,checkBoxBoolean,best_selling,brand_code, cath_code) VALUES (:product_name_,:product_price_,0,:best_selling_,(SELECT brand_id FROM brand_table WHERE brand_name = :brand_code_ LIMIT 1),(SELECT category_id FROM category_table WHERE category_name = :cath_code_ LIMIT 1))")
    fun inserProduct(product_name_:String,product_price_:Int, best_selling_:Boolean,brand_code_:String,cath_code_:String)
   // @Query("INSERT INTO brand_table (brand_name,cath_code) SELECT :brand_name_ as brand_name, (SELECT category_id FROM category_table WHERE category_name = :caht_name_ limit 1) as cath_code FROM brand_table WHERE NOT EXISTS(SELECT brand_name,cath_code FROM brand_table WHERE brand_name =:brand_name_ AND cath_code = (SELECT category_id FROM category_table WHERE category_name = :caht_name_ limit 1)) LIMIT 1 ")
   // fun insertIfNotExist(brand_name_: String, caht_name_: String)
   // @Query("INSERT INTO product_table (product_name,product_price,checkBoxBoolean,best_selling,brand_code,cath_code) SELECT :product_name_ as product_name,:product_price_ as product_price, 0 as checkBoxBoolean,:best_selling_ as best_selling,(SELECT brand_id FROM brand_table WHERE brand_name = :brand_code_ limit 1) as brand_code,(SELECT category_id FROM category_table WHERE category_name = :cath_code_ limit 1) as cath_code FROM product_table WHERE NOT EXISTS(SELECT product_name,product_price,checkBoxBoolean,best_selling,brand_code,cath_code FROM product_table WHERE product_name =:product_name_ AND brand_code = (SELECT brand_id FROM brand_table WHERE brand_name = :brand_code_ limit 1) AND cath_code = (SELECT category_id FROM category_table WHERE category_name = :cath_code_ limit 1) LIMIT 1)LIMIT 1")
    @Query("INSERT INTO product_table (product_name,product_price,checkBoxBoolean,best_selling,brand_code,cath_code) SELECT :product_name_ as product_name,:product_price_ as product_price, 0 as checkBoxBoolean,:best_selling_ as best_selling,(SELECT brand_id FROM brand_table WHERE brand_name = :brand_code_ limit 1) as brand_code,(SELECT category_id FROM category_table WHERE category_name = :cath_code_ limit 1) as cath_code WHERE NOT EXISTS (SELECT 1 FROM product_table WHERE product_name = :product_name_)")
    fun insertIfNotExist(product_name_:String,product_price_:Int, best_selling_:Boolean,brand_code_:String,cath_code_:String)

}