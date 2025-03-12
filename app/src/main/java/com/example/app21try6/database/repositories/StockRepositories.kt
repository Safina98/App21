package com.example.app21try6.database.repositories

import androidx.lifecycle.LiveData
import com.example.app21try6.database.daos.BrandDao
import com.example.app21try6.database.daos.CategoryDao
import com.example.app21try6.database.daos.DetailWarnaDao
import com.example.app21try6.database.daos.ProductDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.models.BrandProductModel
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.Category
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.stock.brandstock.CategoryModel
import com.example.app21try6.stock.brandstock.ExportModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StockRepositories (
    private val categoryDao: CategoryDao,
    private val brandDao: BrandDao,
    private val productDao: ProductDao,
    private val subProductDao: SubProductDao,
    private val detailWarnaDao: DetailWarnaDao
                            ){

    //category rv data
    fun getCategoryModelLiveData(): LiveData<List<CategoryModel>> {
        return categoryDao.getCategoryModelList()
    }
    //spinner catogory entires
    fun getCategoryNameLiveData(): LiveData<List<String>> {
        return categoryDao.getName()
    }
    //get exported data
    fun getExportedStockData(): LiveData<List<ExportModel>> {
        return brandDao.getExportedData()
    }

    // get category id by category name
    suspend fun getCategoryIdByName(id: String):Int{
        return withContext(Dispatchers.IO){
            categoryDao.getCategoryId(id)
        }

    }
    suspend fun insertCategory(category: Category){ withContext(Dispatchers.IO){ categoryDao.insert(category) } }
    suspend fun updateCategory(category: Category){withContext(Dispatchers.IO){ categoryDao.update(category) } }
    //delete category by id
    suspend fun deleteCategory(id:Int){
        withContext(Dispatchers.IO){
            categoryDao.delete(id)
        }
    }

    suspend fun insertBrand(brand: Brand){
        withContext(Dispatchers.IO){
            brandDao.insert(brand)
        }
    }
    // get brand recycler view data by categoryId
    suspend fun getBrandByCategoryId(id:Int):List<BrandProductModel>{
        return withContext(Dispatchers.IO){
           brandDao.getBrandModelByCatId(id)
        }
    }
    //update brand
    suspend fun updateBrand(brand: Brand){ withContext(Dispatchers.IO){ brandDao.update(brand) } }
    //delete brand
    suspend fun deleteBrand(id:Int){ withContext(Dispatchers.IO){ brandDao.deleteBrand(id) } }

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
    suspend fun updateProduct(product: Product){ withContext(Dispatchers.IO){ productDao.update(product) } }
    suspend fun deleteProduct(id:Int){ withContext(Dispatchers.IO){ productDao.delete(id) } }
    suspend fun insertProduct(product: Product){ withContext(Dispatchers.IO){ productDao.insert(product) } }

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