package com.example.app21try6.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SubProductDao {
    @Insert
    fun insert(subProduct: SubProduct)
    @Update
    fun update(subProduct: SubProduct)

    @Query("UPDATE sub_table SET is_checked =:bool  WHERE sub_name =:name")
    fun update_checkbox(name:String,bool:Int):Int

    @Query("UPDATE sub_table SET is_checked =0")
    fun unchecked_allCheckbox()

    @Query("SELECT * FROM sub_table WHERE product_code = :product_id_")
    fun getAll(product_id_:Int):LiveData<List<SubProduct>>
    @Query("SELECT * FROM sub_table")
    fun getAllSub():LiveData<List<SubProduct>>
    @Query("SELECT * FROM sub_table WHERE sub_id = :sub_id_")
    fun getAnItem(sub_id_:Int):LiveData<SubProduct>
    @Query("SELECT sub_name FROM sub_table WHERE sub_id = :sub_id_")
    fun getNama(sub_id_: Int):LiveData<String>
    @Query("SELECT warna FROM sub_table WHERE sub_id = :sub_id_")
    fun getWarna(sub_id_: Int):LiveData<String>
    @Query("SELECT ket FROM sub_table WHERE sub_id = :sub_id_")
    fun getKet(sub_id_: Int):LiveData<String>
    @Query("SELECT roll_u FROM sub_table WHERE sub_id = :sub_id_")
    fun getStokU(sub_id_: Int):LiveData<Int>
    @Query("SELECT roll_b_t FROM sub_table WHERE sub_id = :sub_id_")
    fun getStokBT(sub_id_: Int):LiveData<Int>
    @Query("SELECT roll_s_t FROM sub_table WHERE sub_id = :sub_id_")
    fun getStokST(sub_id_: Int):LiveData<Int>
    @Query("SELECT roll_k_t FROM sub_table WHERE sub_id = :sub_id_")
    fun getStokKT(sub_id_: Int):LiveData<Int>
    @Query("SELECT roll_b_g FROM sub_table WHERE sub_id = :sub_id_")
    fun getStokBG(sub_id_: Int):LiveData<Int>
    @Query("SELECT roll_s_g FROM sub_table WHERE sub_id = :sub_id_")
    fun getStokSG(sub_id_: Int):LiveData<Int>
    @Query("SELECT roll_k_g FROM sub_table WHERE sub_id = :sub_id_")
    fun getStokKG(sub_id_: Int):LiveData<Int>
    @Query("INSERT INTO sub_table (sub_name,product_code,brand_code,cath_code) VALUES (:product_name_,:product_price_,:brand_code_,:cath_code_)")
    fun insert_try(product_name_:String, product_price_:Int,brand_code_:Int,cath_code_:Int)
    @Query("INSERT INTO sub_table (sub_name,roll_u,roll_b_t,roll_s_t,roll_k_t,roll_b_g,roll_s_g,roll_k_g,warna,ket,product_code,brand_code, cath_code,is_checked) VALUES (:sub_name_,:u_,:bt_,:st_,:kt_,:bg_,:sg_,:kg_,:warna_,:ket_,(SELECT product_id FROM product_table WHERE product_name = :product_name_ LIMIT 1),(SELECT brand_id FROM brand_table WHERE brand_name = :brand_code_ LIMIT 1),(SELECT category_id FROM category_table WHERE category_name = :cath_code_ LIMIT 1),0)")
    fun inserSubProduct(sub_name_:String,warna_:String,ket_:String,u_:Int,bt_:Int,st_:Int,kt_:Int,bg_:Int,sg_:Int,kg_:Int,product_name_: String,brand_code_:String,cath_code_:String)

    //@Query("INSERT INTO sub_table (sub_name,roll_u,roll_b_t,roll_s_t,roll_k_t,roll_b_g,roll_s_g,roll_k_g,warna,ket,is_checked,product_code,brand_code,cath_code) SELECT :sub_name_ as sub_name,:u_ as roll_u, :bt_ as roll_b_t,:st_ as roll_s_t,:kt_ as roll_k_t,:bg_ as roll_b_g,:sg_ as roll_s_g,:kg_ as roll_k_g,:warna_ as warna,:ket_ as ket,0 as is_checked,(SELECT product_id FROM product_table WHERE product_name = :product_name_ LIMIT 1) as product_code,(SELECT brand_id FROM brand_table WHERE brand_name = :brand_code_ LIMIT 1) as brand_code,(SELECT category_id FROM category_table WHERE category_name = :cath_code_ LIMIT 1) as cath_code  WHERE NOT EXISTS (SELECT 1 FROM sub_table WHERE sub_name = :sub_name_)")
    @Query("INSERT INTO sub_table (sub_name,roll_u,roll_b_t,roll_s_t,roll_k_t,roll_b_g,roll_s_g,roll_k_g,warna,ket,is_checked,product_code,brand_code,cath_code) SELECT :sub_name_ as sub_name,:u_ as roll_u, :bt_ as roll_b_t,:st_ as roll_s_t,:kt_ as roll_k_t,:bg_ as roll_b_g,:sg_ as roll_s_g,:kg_ as roll_k_g,:warna_ as warna,:ket_ as ket,0 as is_checked,(SELECT product_id FROM product_table WHERE product_name = :product_name_ LIMIT 1) as product_code,(SELECT brand_id FROM brand_table WHERE brand_name = :brand_code_ LIMIT 1) as brand_code,(SELECT category_id FROM category_table WHERE category_name = :cath_code_ LIMIT 1) as cath_code")
    fun insertIfNotExist(sub_name_:String,warna_:String,ket_:String,u_:Int,bt_:Int,st_:Int,kt_:Int,bg_:Int,sg_:Int,kg_:Int,product_name_: String,brand_code_:String,cath_code_:String)



    @Query("DELETE FROM sub_table WHERE sub_id= :id_")
    fun delete(id_:Int)

}