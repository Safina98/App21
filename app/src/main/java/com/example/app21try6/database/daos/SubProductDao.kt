package com.example.app21try6.database.daos

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.app21try6.database.models.SubWithPriceModel
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.transaction.transactiondetail.TransactionDetailWithProduct

@Dao
interface SubProductDao {
    @Insert
    fun insert(subProduct: SubProduct)
    @Update
    fun update(subProduct: SubProduct)

    @Query("DELETE FROM sub_table WHERE sub_id= :id_")
    fun delete(id_:Int)

    @Query("UPDATE sub_table SET is_checked =:bool  WHERE sub_name =:name")
    fun update_checkbox(name:String,bool:Int):Int
    @Query("UPDATE sub_table SET is_checked =0")
    fun unchecked_allCheckbox()
    @Query("SELECT * FROM sub_table WHERE  (:product_id_ IS NULL OR product_code = :product_id_) AND (:brandId IS NULL OR brand_code=:brandId)")
    fun getAll(product_id_:Int?,brandId: Long?):LiveData<List<SubProduct>>


    @Query("SELECT * FROM sub_table WHERE sub_id = :sub_id_")
    fun getAnItem(sub_id_:Int):LiveData<SubProduct>

    @Query("SELECT product_code FROM sub_table WHERE sub_id = :sub_id_")
    fun getProductIdBySubId(sub_id_:Int?):Int?

    @Query("SELECT * FROM sub_table WHERE sub_id = :sub_id_")
    fun getSubProductIdBySubId(sub_id_:Int?): SubProduct

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



    @Query("INSERT INTO sub_table (sub_name,roll_u,roll_b_t,roll_s_t,roll_k_t,roll_b_g,roll_s_g,roll_k_g,warna,ket,is_checked,product_code,brand_code,cath_code) SELECT :sub_name_ as sub_name,:u_ as roll_u, :bt_ as roll_b_t,:st_ as roll_s_t,:kt_ as roll_k_t,:bg_ as roll_b_g,:sg_ as roll_s_g,:kg_ as roll_k_g,:warna_ as warna,:ket_ as ket,0 as is_checked,(SELECT product_id FROM product_table WHERE product_name = :product_name_ LIMIT 1) as product_code,(SELECT brandCloudId FROM brand_table WHERE brand_name = :brand_code_ LIMIT 1) as brand_code,(SELECT categoryCloudId FROM category_table WHERE category_name = :cath_code_ LIMIT 1) as cath_code")
    fun insertIfNotExist(sub_name_:String,warna_:String,ket_:String,u_:Int,bt_:Int,st_:Int,kt_:Int,bg_:Int,sg_:Int,kg_:Int,product_name_: String,brand_code_:String,cath_code_:String)


    @Query("SELECT *FROM sub_table INNER JOIN PRODUCT_TABLE ON product_id = sub_table.product_code WHERE sub_id = :id limit 1")
    fun getProduct(id:Int): Product?



    @Query("SELECT sp.*, p.purchasePrice, p.default_net " +
            "FROM sub_table sp " +
            "INNER JOIN product_table p ON sp.product_code = p.product_id " +
            "WHERE sp.sub_name  LIKE '%' || :query || '%' " +
            "LIMIT 20")
    fun getSubProductWithPrice(query: String):LiveData<List<SubWithPriceModel>?>


    @Query("""
        UPDATE trans_detail_table
        SET trans_item_name =:updatedName
        WHERE sub_id =:sub_id
    """)
    suspend fun updateTransItemName(sub_id:Int,updatedName:String)

    @Transaction
    suspend fun updateSubProductAndTransDetail(

        subProduct: SubProduct

    ) {
        update(subProduct)
        updateTransItemName(subProduct.sub_id, subProduct.sub_name)
    }
}