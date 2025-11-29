package com.example.app21try6.database.repositories

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.cloud.BrandCloud
import com.example.app21try6.database.cloud.CategoryCloud
import com.example.app21try6.database.cloud.RealtimeDatabaseSync
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.database.models.DetailMerchandiseModel
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.Category
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.MerchandiseRetail
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.stock.brandstock.CategoryModel
import com.example.app21try6.stock.brandstock.ExportModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockRepositories (
   application: Application
                            ){
    private val categoryDao= VendibleDatabase.getInstance(application).categoryDao
    private val brandDao=VendibleDatabase.getInstance(application).brandDao
    private val productDao=VendibleDatabase.getInstance(application).productDao
    private val subProductDao=VendibleDatabase.getInstance(application).subProductDao
    private val detailWarnaDao=VendibleDatabase.getInstance(application).detailWarnaDao

    //category rv data
    fun getCategoryModelLiveData(): LiveData<List<CategoryModel>> {
        return categoryDao.getCategoryModelList()
    }
    //spinner catogory entires
    fun getCategoryNameLiveData(): LiveData<List<String>> {
        return categoryDao.getName()
    }
    suspend fun getCategoryNameById(id:Int):String{
        return withContext(Dispatchers.IO){
            categoryDao.getCategoryNameById(id)
        }
    }
    //get exported data
    fun getExportedStockData(): LiveData<List<ExportModel>> {
        return brandDao.getExportedData()
    }
    suspend fun getCategoryNameListWithAll():List<String>{
        return withContext(Dispatchers.IO) {
            val list = categoryDao.getAllCategoryName()
            val modifiedList = listOf("ALL") + list // Create a new list with the added value
            modifiedList // Return the modified list
        }
    }
    // get category id by category name
    suspend fun getCategoryIdByName(id: String):Int{
        return withContext(Dispatchers.IO){
            categoryDao.getCategoryId(id)
        }
    }
    suspend fun getCategoryIdByBrandId(brandId: Int?):Int?{
        return withContext(Dispatchers.IO){
            brandDao.getCtgIdByBrandId(brandId)
        }
    }
    suspend fun insertCategory(category: Category){
        withContext(Dispatchers.IO){
            val newId= categoryDao.insert(category).toInt()
            val cloudCategory = CategoryCloud(
                categoryName = category.category_name,
                lastUpdated = System.currentTimeMillis()
            ).apply { cloudId=category.cloudId }
            RealtimeDatabaseSync.upload("category_table", cloudCategory.cloudId, cloudCategory)
        }
    }
    suspend fun updateCategory(category: Category){
        withContext(Dispatchers.IO){
            categoryDao.update(category)
            val cloudCategory = CategoryCloud(
                categoryName = category.category_name,
                lastUpdated = System.currentTimeMillis()
            ).apply { cloudId = category.cloudId }
            RealtimeDatabaseSync.upload("category_table", cloudCategory.cloudId, cloudCategory)
        }
    }
    //delete category by id
    suspend fun deleteCategory(id:Int){
        withContext(Dispatchers.IO){
            categoryDao.delete(id)
            RealtimeDatabaseSync.deleteById("category_table", id)
        }
    }
    ////////////////////////////////////////Brand////////////////////////////////////////////////////////

    suspend fun insertBrand(brand: Brand){
        withContext(Dispatchers.IO){
           val newId= brandDao.insert(brand).toInt()
            val cloudBrand = BrandCloud(
                brandName = brand.brand_name,
                cathCode = brand.cath_code,
                lastUpdated = System.currentTimeMillis()
            ).apply {cloudId=brand.cloudId }

            RealtimeDatabaseSync.upload("brand_table", cloudBrand.cloudId, cloudBrand)
        }
    }
    // get brand recycler view data by categoryId
    suspend fun getBrandByCategoryId(id:Int):List<BrandProductModel>{ return withContext(Dispatchers.IO){ brandDao.getBrandModelByCatId(id) } }
    suspend fun getBrandNameListByCategoryName(cat:Int):List<String>{ return withContext(Dispatchers.IO){ brandDao.getBrandNameListByCatName(cat) } }
    //update brand
    suspend fun updateBrand(brand: Brand){ withContext(Dispatchers.IO){ brandDao.update(brand) } }
    //delete brand
    suspend fun deleteBrand(id:Int){ withContext(Dispatchers.IO){ brandDao.deleteBrand(id) } }
    //get brand id by product id
    suspend fun getBrandId(productId:Int?):Int?{ return withContext(Dispatchers.IO){ productDao.getBrandIdByProductId(productId) } }
    suspend fun getBrandIdByName(name:String,catCode:Int):Int?{return withContext(Dispatchers.IO){brandDao.getBrandIdbyName(name,catCode)} }
    suspend fun getBrandNameyId(id:Int):String{return withContext(Dispatchers.IO){brandDao.getBrandNameById(id)} }

    //////////////////////////////////////Product///////////////////////////////////////
    //get product recyclerview data
    suspend fun getProductModel(brandId:Int?):List<BrandProductModel>{
        return withContext(Dispatchers.IO){
            productDao.getAll(brandId)
        }
    }
    suspend fun getProductById(id:Int):Product{
        return withContext(Dispatchers.IO){productDao.getProductById(id)}
    }

    suspend fun getProductBySubId(subId:Int): Product?{
        return withContext(Dispatchers.IO){
            subProductDao.getProduct(subId)
        }
    }
    fun getProductLiveDataByCategoryId(id:Int):LiveData<List<Product>>{
        return productDao.getCategoriedProduct(id)
    }
    suspend fun getProductListByCategoryId(category_id:Int?):List<Product>{
        return withContext(Dispatchers.IO){
            productDao.getProductByCategory(category_id)
        }
    }
    suspend fun getProductNameListByCategoryName(name:String):List<String>{
        return withContext(Dispatchers.IO) {
            val list = productDao.getProductNameByCategoryName(name)
            val modifiedList = listOf("Off","ALL") + list // Create a new list with the added value
            modifiedList // Return the modified list
        }
    }

    fun getAllProduct(): LiveData<List<Product>> {
        return productDao.getAllProduct()
    }
   suspend fun insertTry(product: Product, brand: String, cath: String){ withContext(Dispatchers.IO){ productDao.inserProduct(product.product_name,product.product_price,product.bestSelling,brand,cath) } }
    //get product id by sub  id
    suspend fun getProdutId(subId:Int):Int?{ return withContext(Dispatchers.IO){ subProductDao.getProductIdBySubId(subId) } }
    suspend fun updateProduct(product: Product){ withContext(Dispatchers.IO){ productDao.update(product) } }
    suspend fun deleteProduct(id:Int){ withContext(Dispatchers.IO){ productDao.delete(id) } }
    suspend fun insertProduct(product: Product){ withContext(Dispatchers.IO){ productDao.insert(product) } }
////////////////////////////////////////////////SubProduct//////////////////////////////////////////
    fun getSubProductLiveData(id:Int?,brandId:Int?): LiveData<List<SubProduct>> {
        return  subProductDao.getAll(id,brandId)
    }
    suspend fun updateSubProduct(subProduct: SubProduct){
        withContext(Dispatchers.IO){
            subProductDao.update(subProduct)
        }
    }
    suspend fun deleteSubProduct(subProduct: SubProduct){
        withContext(Dispatchers.IO){
            subProductDao.delete(subProduct.sub_id)
        }
    }
    suspend fun insertSubProduct(subProduct: SubProduct){
        withContext(Dispatchers.IO){
            subProductDao.insert(subProduct)
        }
    }
    suspend fun updateSubName(subProduct: SubProduct){
        withContext(Dispatchers.IO){
            subProductDao.updateSubProductAndTransDetail(subProduct)
        }
    }
   suspend fun checkedSub(name:String,bool:Int){
        withContext(Dispatchers.IO){
            subProductDao.update_checkbox(name,bool)
        }
    }
    suspend fun uncheckedAllSubs(){
        withContext(Dispatchers.IO){
            subProductDao.unchecked_allCheckbox()
        }
    }
    suspend fun getsubProductById(spId:Int): SubProduct{
        return withContext(Dispatchers.IO){
            subProductDao.getSubProductIdBySubId(spId)
        }
    }
    //////////////////////////////////////Detail Warna////////////////////////////////////////////////

    suspend fun getDetailWarnaList(id:Int):List<DetailMerchandiseModel>{
        return withContext(Dispatchers.IO){
            detailWarnaDao.getDetailWarnaBySubId(id)
        }
    }
    suspend fun insertDetailWarna(detailWarnaTable: DetailWarnaTable, inventoryLog: InventoryLog){
        withContext(Dispatchers.IO){
            detailWarnaDao.insertDetailWarnaAndLog(detailWarnaTable,inventoryLog)
        }
    }
    suspend fun updateDetailWarna(detailWarnaTable: DetailWarnaTable, inventoryLog: InventoryLog,merchandiseRetail: MerchandiseRetail?){
        withContext(Dispatchers.IO){
            detailWarnaDao.updateDetailWarnaAndInsertLog(detailWarnaTable,inventoryLog,merchandiseRetail)
        }
    }
    suspend fun updateDetailWarna(detailWarnaTable: DetailWarnaTable, inventoryLog: InventoryLog,merchandiseRetail: List<MerchandiseRetail?>){
        withContext(Dispatchers.IO){
            detailWarnaDao.updateDetailWarnaAndInsertLog(detailWarnaTable,inventoryLog,merchandiseRetail)
        }
    }
    suspend fun deleteDetailWarna(detailWarnaTable: DetailWarnaTable,inventoryLog: InventoryLog,merchandiseRetail: MerchandiseRetail?){
        withContext(Dispatchers.IO){
            detailWarnaDao.deleteDetailWarnaAndInsertLog(detailWarnaTable.id,inventoryLog,merchandiseRetail)
        }
    }
    suspend fun deleteDetailWarna(detailWarnaTable: DetailWarnaTable,inventoryLog: InventoryLog,merchandiseRetail: List<MerchandiseRetail?>){
        withContext(Dispatchers.IO){
            detailWarnaDao.deleteDetailWarnaAndInsertLog(detailWarnaTable.id,inventoryLog,merchandiseRetail)
        }
    }

    suspend fun deleteDetailWarnaBySubId(subId:Int){
        withContext(Dispatchers.IO){
            detailWarnaDao.deleteDetailWarnaBySubId(subId)
        }
    }

    suspend fun selectRetailBySumId(subId:Int):List<DetailMerchandiseModel>?{
        return withContext(Dispatchers.IO){
             detailWarnaDao.getRetaiBySubId(subId)
        }
    }

    suspend fun deleteRetail(id:Int){
        withContext(Dispatchers.IO){
            detailWarnaDao.deleteMerchandise(id)
        }
    }
    suspend fun updateDetailRetail(merchandiseRetail: MerchandiseRetail){
        withContext(Dispatchers.IO){
            detailWarnaDao.updateRetail(merchandiseRetail)
        }
    }
    suspend fun updateDetaiAndlRetail(merchandiseRetail: MerchandiseRetail,transactionDetail: TransactionDetail){
        withContext(Dispatchers.IO){
            detailWarnaDao.updateTransDetailAndRetail(merchandiseRetail,transactionDetail)
        }
    }



    suspend fun isDetailWarnaExist(subId:Int,net:Double):DetailWarnaTable?{
        return withContext(Dispatchers.IO){
            detailWarnaDao.getDetailBySubIdAndNet(subId,net)
        }
    }
    suspend fun insertIfNotExist(brandName:String,categoryName:String){
        withContext(Dispatchers.IO){
            brandDao.insertIfNotExist(brandName,categoryName)
        }
    }
    ////////////////////////////CSV///////////////////////////////////////////////////
    suspend fun insertCSVBatch(tokensList: List<List<String>>) {
        categoryDao.performTransaction {
            val batchSize = 100 // Define batch size
            for (i in 0 until tokensList.size step batchSize) {
                val batch = tokensList.subList(i, minOf(i + batchSize, tokensList.size))
                insertBatch(batch)
            }
        }
    }

    private suspend fun insertBatch(batch: List<List<String>>) {
        batch.forEach { tokens ->
            insertCSVN(tokens)
        }
    }

    private suspend fun insertCSVN(token: List<String>) {
      //  Log.i("Import Csv", "brand token: $token")

        val product = Product(
            product_name = token[2].uppercase().trim(),
            product_price = token[3].toInt(),
            bestSelling = token[4] == "TRUE",
            product_capital = token[13].toInt(),
            default_net = token.getOrNull(14)?.toDoubleOrNull() ?: 0.0,
            alternate_capital = token.getOrNull(15)?.toDoubleOrNull() ?: 0.0,
            alternate_price = token.getOrNull(16)?.toDoubleOrNull() ?: 0.0
        )

        val subProduct = SubProduct(
            sub_name = token[5].uppercase().trim(),
            roll_u = token[6].toInt(),
            roll_bt = token[7].toInt(),
            roll_st = token[8].toInt(),
            roll_kt = token[9].toInt(),
            roll_bg = token[10].toInt(),
            roll_sg = token[11].toInt(),
            roll_kg = token[12].toInt()
        )

        val brandName = token[1].uppercase().trim()
        val categoryName = token[0].uppercase().trim()

        categoryDao.insertIfNotExist(categoryName)
        brandDao.insertIfNotExist(brandName, categoryName)
        productDao.insertIfNotExist(
            product.product_name, product.product_price, product.product_capital,
            product.default_net, product.alternate_capital, product.alternate_price,
            product.bestSelling, brandName, categoryName
        )
        subProductDao.insertIfNotExist(
            subProduct.sub_name, subProduct.warna, subProduct.ket, subProduct.roll_u,
            subProduct.roll_bt, subProduct.roll_st, subProduct.roll_kt, subProduct.roll_bg,
            subProduct.roll_sg, subProduct.roll_kg, product.product_name, brandName, categoryName
        )
    }

}