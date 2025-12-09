package com.example.app21try6.database.repositories

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.app21try6.Constants
import com.example.app21try6.Constants.TABLENAMES
import com.example.app21try6.database.VendibleDatabase
import com.example.app21try6.database.cloud.CategoryCloud
import com.example.app21try6.database.cloud.RealtimeDatabaseSync
import com.example.app21try6.database.cloud.UploadInventories
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
import com.example.app21try6.stock.brandstock.ExportModel
import com.example.app21try6.stock.brandstock.StockCategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockRepositories (
   application: Application
                            ){

    private val uploadInventory= UploadInventories()
    private val categoryDao= VendibleDatabase.getInstance(application).categoryDao
    private val brandDao=VendibleDatabase.getInstance(application).brandDao
    private val productDao=VendibleDatabase.getInstance(application).productDao
    private val subProductDao=VendibleDatabase.getInstance(application).subProductDao
    private val detailWarnaDao=VendibleDatabase.getInstance(application).detailWarnaDao

    //category rv data
    fun getCategoryModelLiveData(): LiveData<List<StockCategoryModel>> {
        return categoryDao.getCategoryModelList()
    }
    //spinner catogory entires
    fun getCategoryNameLiveData(): LiveData<List<String>> {
        return categoryDao.getName()
    }
    suspend fun getCategoryNameById(id:Long):String{
        return withContext(Dispatchers.IO){
            categoryDao.getCategoryNameById(id)
        }
    }
    suspend fun getCategoryById(id:Long):Category?{
        return withContext(Dispatchers.IO){
            categoryDao.getById(id)
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
    suspend fun getCategoryIdByName(id: String): Long{
        return withContext(Dispatchers.IO){
            categoryDao.getCategoryId(id)
        }
    }
    suspend fun getCategoryIdByBrandId(brandId: Long?):Long?{
        return withContext(Dispatchers.IO){
            brandDao.getCtgIdByBrandId(brandId)
        }
    }
    suspend fun insertCategory(category: Category){
        withContext(Dispatchers.IO){
            val newId= categoryDao.insert(category).toInt()
            uploadInventory.uploadCategory(category)

        }
    }
    suspend fun updateCategory(category: Category){
        withContext(Dispatchers.IO){
            categoryDao.update(category)
            try {
                val cloudObject = CategoryCloud(
                    categoryName = category.category_name)

                // 1. CALL THE SUSPENDING FUNCTION
                RealtimeDatabaseSync.uploadSuspended( // <-- Using the new function
                    tableName = "category_table",
                    cloudId = category.categoryCloudId.toString(),
                    cloudObject = cloudObject
                )

                // 2. ONLY MARK AS SYNCED IF THE UPLOAD SUCCEEDED
                categoryDao.markAsSynced(category.categoryCloudId)

            } catch (e: Exception) {
                // If the upload failed (e.g., no internet), the exception is caught here.
                // We just log it and move to the next item. The Worker will catch this failure
                // and return Result.retry() to re-schedule the entire job.
                Log.w("SyncManager", "Upload failed for category ${category.categoryCloudId}: ${e.message}")
            }

        }
    }
    //delete category by id
    suspend fun deleteCategory(id: Long){
        withContext(Dispatchers.IO){
            categoryDao.delete(id)
           // RealtimeDatabaseSync.deleteById("category_table", id)
        }
    }
    suspend fun deleteCategory(category: Category){
        withContext(Dispatchers.IO){
            val softDeletedCategory = category.copy(isDeleted = true, needsSyncs = 1)
            categoryDao.update(softDeletedCategory)
            Log.i("SyncManager","Delete Category Repository")
            Log.i("SyncManager","category ${softDeletedCategory.categoryCloudId},${softDeletedCategory.needsSyncs},${softDeletedCategory.isDeleted}")
            val cloudObject = CategoryCloud(
                categoryName = category.category_name,
                lastUpdated = System.currentTimeMillis(),
                isDeleted = true // <--- THIS IS THE KEY CHANGE
            )

            // Use your uploadSuspended method
            RealtimeDatabaseSync.uploadSuspended(
                tableName = Constants.TABLENAMES.CATEGORY,
                cloudId = category.categoryCloudId.toString(), // Assuming localId is used as cloudId
                cloudObject = cloudObject
            )


        }
    }


    ////////////////////////////////////////Brand////////////////////////////////////////////////////////

    suspend fun insertBrand(brand: Brand){
        withContext(Dispatchers.IO){
           val newId= brandDao.insert(brand).toInt()
            uploadInventory.uploadBrand(brand)
        }
    }
    // get brand recycler view data by categoryId
    suspend fun getBrandByCategoryId(id:Long):List<BrandProductModel>{ return withContext(Dispatchers.IO){ brandDao.getBrandModelByCatId(null) } }
    suspend fun getAllBrand():List<Brand>{ return withContext(Dispatchers.IO){ brandDao.getAllBrand() } }
    suspend fun getBrandNameListByCategoryName(cat:Long):List<String>{ return withContext(Dispatchers.IO){ brandDao.getBrandNameListByCatName(cat) } }
    //update brand
    suspend fun updateBrand(brand: Brand){ withContext(Dispatchers.IO){
        brandDao.update(brand)
        try {
            val brandCloud= uploadInventory.convertBrandtoBrandCloud(brand)
            RealtimeDatabaseSync.uploadSuspended( // <-- Using the new function
                tableName = Constants.TABLENAMES.BRAND,
                cloudId = brand.brandCloudId.toString(),
                cloudObject = brandCloud
            )
            brandDao.markAsSynced(brand.brandCloudId)

        } catch (e: Exception) {
            Log.w("SyncManager", "Upload failed for category ${brand.brandCloudId}: ${e.message}")
        }


    }
    }
    //delete brand
    suspend fun deleteBrand(id:Long){ withContext(Dispatchers.IO){ brandDao.deleteBrand(id) } }
    //get brand id by product id
    suspend fun getBrandId(productId: Long?):Long?{ return withContext(Dispatchers.IO){ productDao.getBrandIdByProductId(productId) } }
    suspend fun getBrandIdByName(name:String,catCode:Long):Long?{return withContext(Dispatchers.IO){brandDao.getBrandIdbyName(name,catCode)} }
    suspend fun getBrandNameyId(id:Long):String{return withContext(Dispatchers.IO){brandDao.getBrandNameById(id)} }

    //////////////////////////////////////Product///////////////////////////////////////
    //get product recyclerview data
    suspend fun getProductModel(brandId:Long?):List<BrandProductModel>{
        return withContext(Dispatchers.IO){
            productDao.getAll(brandId)
        }
    }

    suspend fun assignCloudIdToProductTable(cloudId: Long, id: Long){
        withContext(Dispatchers.IO){
            productDao.assignProductCloudID(cloudId, id)
        }
    }

    suspend fun getAllProductTable(): List<Product> {
        return withContext(Dispatchers.IO) {
            productDao.selectAllProductTable()
        }
    }

    suspend fun assignCloudIdToDetailWarnaTable(cloudId: Long, id: Long){
        withContext(Dispatchers.IO){
            detailWarnaDao.assignDetailWarnaCloudID(cloudId, id)
        }
    }

    suspend fun getAllDetailWarnaTable(): List<DetailWarnaTable> {
        return withContext(Dispatchers.IO) {
            detailWarnaDao.selectAllDetailWarnaTable()
        }
    }

    suspend fun assignCloudIdToSubProductTable(cloudId: Long, id: Long){
        withContext(Dispatchers.IO){
            subProductDao.assignSubProductCloudID(cloudId, id)
        }
    }

    suspend fun getAllDSubProductTable(): List<SubProduct> {
        return withContext(Dispatchers.IO) {
            subProductDao.selectAllSubProductTable()
        }
    }

    suspend fun assignCloudIdToMerchandiseRetailTable(cloudId: Long, id: Long){
        withContext(Dispatchers.IO){
            detailWarnaDao.assignMerchandiseRetailCloudID(cloudId, id)
        }
    }
    suspend fun getProductsWithDuplicateCloudIds(): List<Product> {
        return withContext(Dispatchers.IO) {
            productDao.getPriductsWithDuplicateCloudIds()
        }
    }
    suspend fun getSubProductWithDuplicateId(): List<SubProduct> {
        return withContext(Dispatchers.IO) {
            subProductDao.getSubProductsWithDuplicateCloudIds()
        }
    }
    suspend fun getAllMerchandiseRetailTable(): List<MerchandiseRetail> {
        return withContext(Dispatchers.IO) {
            detailWarnaDao.selectAllMerchandiseRetailTable()
        }
    }


    suspend fun getProductById(id:Long):Product{
        return withContext(Dispatchers.IO){productDao.getProductById(id)}
    }

    suspend fun getProductBySubId(subId: Long): Product?{
        return withContext(Dispatchers.IO){
            subProductDao.getProduct(subId)
        }
    }
    fun getProductLiveDataByCategoryId(id: Long):LiveData<List<Product>>{
        return productDao.getCategoriedProduct(id)
    }
    suspend fun getProductListByCategoryId(categoryCloudId:Long?):List<Product>{
        return withContext(Dispatchers.IO){
            productDao.getProductByCategory(categoryCloudId)
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
    suspend fun getProdutId(subId:Long):Long?{ return withContext(Dispatchers.IO){ subProductDao.getProductIdBySubId(subId) } }
    suspend fun updateProduct(product: Product){
        withContext(Dispatchers.IO){
            productDao.update(product)
            try {
                val productCloud= uploadInventory.convertProductToProcuctClout(product)
                RealtimeDatabaseSync.uploadSuspended( // <-- Using the new function
                    tableName = Constants.TABLENAMES.PRODUCT,
                    cloudId = product.productCloudId.toString(),
                    cloudObject = productCloud
                )
                productDao.markAsSynced(product.productCloudId)

            } catch (e: Exception) {
                Log.w("SyncManager", "Upload failed for category ${product.productCloudId}: ${e.message}")
            }
        } }
    suspend fun deleteProduct(id:Long){ withContext(Dispatchers.IO){ productDao.delete(id) } }
    suspend fun insertProduct(product: Product){
        withContext(Dispatchers.IO){
            product.productCloudId=System.currentTimeMillis()
            productDao.insert(product)
            uploadInventory.uploadProduct(product)
        }
    }
////////////////////////////////////////////////SubProduct//////////////////////////////////////////
    fun getSubProductLiveData(id:Long?,brandId: Long?): LiveData<List<SubProduct>> {
        return  subProductDao.getAll(id,brandId)
    }
    suspend fun updateSubProduct(subProduct: SubProduct){
        withContext(Dispatchers.IO){
            subProductDao.update(subProduct)
            try {
                val sPCloud= uploadInventory.convertSubProductToSubProductCloud(subProduct)
                RealtimeDatabaseSync.uploadSuspended( // <-- Using the new function
                    tableName = Constants.TABLENAMES.SUB_PRODUCT,
                    cloudId = subProduct.sPCloudId.toString(),
                    cloudObject = sPCloud
                )
                productDao.markAsSynced(subProduct.sPCloudId)

            } catch (e: Exception) {
                Log.w("SyncManager", "Upload failed for sub product ${subProduct.sPCloudId}: ${e.message}")
            }
        }
    }
    suspend fun deleteSubProduct(subProduct: SubProduct){
        withContext(Dispatchers.IO){
            subProductDao.delete(subProduct.sPCloudId)
        }
    }
    suspend fun insertSubProduct(subProduct: SubProduct){
        withContext(Dispatchers.IO){
            subProduct.sPCloudId= System.currentTimeMillis()
            val sPCloud=UploadInventories().convertSubProductToSubProductCloud(subProduct)
            RealtimeDatabaseSync.upload(TABLENAMES.SUB_PRODUCT, sPCloud.cloudId, sPCloud)
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
    suspend fun getsubProductById(spId: Long): SubProduct{
        return withContext(Dispatchers.IO){
            subProductDao.getSubProductIdBySubId(spId)
        }
    }
    //////////////////////////////////////Detail Warna////////////////////////////////////////////////

    suspend fun getDetailWarnaList(id: Long):List<DetailMerchandiseModel>{
        return withContext(Dispatchers.IO){
            detailWarnaDao.getDetailWarnaBySubId(id)
        }
    }
    suspend fun insertDetailWarna(detailWarnaTable: DetailWarnaTable, inventoryLog: InventoryLog){
        withContext(Dispatchers.IO){
            detailWarnaDao.insertDetailWarnaAndLog(detailWarnaTable,inventoryLog)
            val dWCloud=UploadInventories().convertDetailWarnaToDetailWarnaCloud(detailWarnaTable)
            RealtimeDatabaseSync.upload(TABLENAMES.DETAIL_WARNA, dWCloud.cloudId, dWCloud)
        }
    }
    suspend fun updateDetailWarna(detailWarnaTable: DetailWarnaTable, inventoryLog: InventoryLog,merchandiseRetail: MerchandiseRetail?){
        withContext(Dispatchers.IO){
            detailWarnaTable.needsSyncs=1
            detailWarnaDao.updateDetailWarnaAndInsertLog(detailWarnaTable,inventoryLog,merchandiseRetail)

            try {
                val  dWCloud=UploadInventories().convertDetailWarnaToDetailWarnaCloud(detailWarnaTable)
                RealtimeDatabaseSync.uploadSuspended( // <-- Using the new function
                    tableName = Constants.TABLENAMES.DETAIL_WARNA,
                    cloudId = detailWarnaTable.dWCloudId.toString(),
                    cloudObject = dWCloud
                )
                detailWarnaDao.markDetailWarnaAsSynced(detailWarnaTable.dWCloudId)
                val mRCLoud=UploadInventories().convertMerchandiseRetailToMerchandiseRetailCloud(merchandiseRetail!!)
                RealtimeDatabaseSync.upload( // <-- Using the new function
                    tableName = Constants.TABLENAMES.MERCHANDISE_RETAIL,
                    cloudId = merchandiseRetail.mRCloudId.toString(),
                    cloudObject = mRCLoud
                )

                detailWarnaDao.markMerhcandiseRetailAsSynced(merchandiseRetail.mRCloudId)

            } catch (e: Exception) {
                Log.w("SyncManager", "Upload failed for detailwarna ${detailWarnaTable.dWCloudId}: ${e.message}")
                Log.w("SyncManager", "Upload failed for merchadise reteil ${merchandiseRetail?.mRCloudId}: ${e.message}")
            }

        }
    }
    suspend fun updateDetailWarna(detailWarnaTable: DetailWarnaTable, inventoryLog: InventoryLog,merchandiseRetailList: List<MerchandiseRetail?>){
        withContext(Dispatchers.IO){
            detailWarnaDao.updateDetailWarnaAndInsertLog(detailWarnaTable,inventoryLog,merchandiseRetailList)
            try {
                val  dWCloud=UploadInventories().convertDetailWarnaToDetailWarnaCloud(detailWarnaTable)
                RealtimeDatabaseSync.uploadSuspended( // <-- Using the new function
                    tableName = Constants.TABLENAMES.DETAIL_WARNA,
                    cloudId = detailWarnaTable.dWCloudId.toString(),
                    cloudObject = dWCloud
                )
                detailWarnaDao.markDetailWarnaAsSynced(detailWarnaTable.dWCloudId)

                merchandiseRetailList.forEach {merchandiseRetail ->
                    val mRCLoud=UploadInventories().convertMerchandiseRetailToMerchandiseRetailCloud(merchandiseRetail!!)
                    RealtimeDatabaseSync.upload( // <-- Using the new function
                        tableName = Constants.TABLENAMES.MERCHANDISE_RETAIL,
                        cloudId = merchandiseRetail.mRCloudId.toString(),
                        cloudObject = mRCLoud
                    )

                   // detailWarnaDao.markMerhcandiseRetailAsSynced(merchandiseRetail.mRCloudId)
                }


            } catch (e: Exception) {
                Log.w("SyncManager", "Upload failed for detailwarna ${detailWarnaTable.dWCloudId}: ${e.message}")
                Log.w("SyncManager", "Upload failed for merchadise reteil ${merchandiseRetailList}: ${e.message}")
            }

        }
    }
    suspend fun deleteDetailWarna(detailWarnaTable: DetailWarnaTable,inventoryLog: InventoryLog,merchandiseRetail: MerchandiseRetail?){
        withContext(Dispatchers.IO){
            detailWarnaDao.deleteDetailWarnaAndInsertLog(detailWarnaTable.dWCloudId,inventoryLog,merchandiseRetail)
        }
    }
    suspend fun deleteDetailWarna(detailWarnaTable: DetailWarnaTable,inventoryLog: InventoryLog,merchandiseRetail: List<MerchandiseRetail?>){
        withContext(Dispatchers.IO){
            detailWarnaDao.deleteDetailWarnaAndInsertLog(detailWarnaTable.dWCloudId,inventoryLog,merchandiseRetail)
        }
    }

    suspend fun deleteDetailWarnaBySubId(subId: Long){
        withContext(Dispatchers.IO){
            detailWarnaDao.deleteDetailWarnaBySubId(subId)
        }
    }

    suspend fun selectRetailBySumId(subId: Long):List<DetailMerchandiseModel>?{
        return withContext(Dispatchers.IO){
             detailWarnaDao.getRetaiBySubId(subId)
        }
    }

    suspend fun deleteRetail(id:Long){
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



    suspend fun isDetailWarnaExist(subId: Long, net:Double):DetailWarnaTable?{
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
            val batchSize = 100 // Define your batch size here
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
            roll_u = token[6].toInt()
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
             product.product_name, brandName, categoryName
        )
    }

}