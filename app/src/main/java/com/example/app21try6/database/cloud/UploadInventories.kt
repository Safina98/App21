package com.example.app21try6.database.cloud

import com.example.app21try6.Constants.TABLENAMES
import com.example.app21try6.database.cloud.RealtimeDatabaseSync
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.Category
import com.example.app21try6.database.tables.Product

class UploadInventories() {
    fun uploadCategory(category: Category){
        val cloudCategory = CategoryCloud(
            categoryName = category.category_name,
            lastUpdated = System.currentTimeMillis()
        ).apply { cloudId=category.categoryCloudId.toString() }
        RealtimeDatabaseSync.upload(TABLENAMES.CATEGORY, cloudCategory.cloudId, cloudCategory)
    }
    fun uploadCategoryN(category: Category){
        val cloudCategory = CategoryCloud(
            categoryName = category.category_name,
            lastUpdated = System.currentTimeMillis()
        ).apply { cloudId=category.categoryCloudId.toString() }
        RealtimeDatabaseSync.upload(TABLENAMES.CATEGORY, cloudCategory.cloudId, cloudCategory)
    }
    fun uploadBrand(brand: Brand){
        val cloudBrand = BrandCloud(
            brandName = brand.brand_name,
            cathCode = brand.cath_code,
            lastUpdated = System.currentTimeMillis(),
            needsSyncs = 1
        ).apply {cloudId=brand.brandCloudId.toString() }

        RealtimeDatabaseSync.upload(TABLENAMES.BRAND, cloudBrand.cloudId, cloudBrand)
    }
    fun convertBrandtoBrandCloud(brand: Brand): BrandCloud{
        val cloudBrand = BrandCloud(
            brandName = brand.brand_name,
            cathCode = brand.cath_code,
            lastUpdated = System.currentTimeMillis(),
            needsSyncs = brand.needsSyncs
        ).apply {cloudId=brand.brandCloudId.toString() }
        return cloudBrand
    }
    fun uploadProduct(product: Product){
        val cloudProduct = ProductCloud(
            productName = product.product_name,
            productPrice = product.product_price,
            productCapital = product.product_capital,
            checkBoxBoolean = product.checkBoxBoolean,
            bestSelling = product.bestSelling,
            defaultNet = product.default_net,
            alternatePrice = product.alternate_price,
            brandCode = product.brand_code,
            cathCode = product.cath_code,
            discountId = product.discountId,
            purchasePrice = product.purchasePrice,
            puchaseUnit = product.puchaseUnit,
            alternateCapital = product.alternate_capital,
            isDeleted = product.isDeleted,
            needsSyncs = product.needsSyncs,
            lastUpdated = System.currentTimeMillis()
        ).apply {cloudId=product.productCloudId.toString() }

        RealtimeDatabaseSync.upload(TABLENAMES.PRODUCT, cloudProduct.cloudId, cloudProduct)
    }
    fun convertProductToProcuctClout(product: Product): ProductCloud{
        val cloudProduct = ProductCloud(
            productName = product.product_name,
            productPrice = product.product_price,
            productCapital = product.product_capital,
            checkBoxBoolean = product.checkBoxBoolean,
            bestSelling = product.bestSelling,
            defaultNet = product.default_net,
            alternatePrice = product.alternate_price,
            brandCode = product.brand_code,
            cathCode = product.cath_code,
            discountId = product.discountId,
            purchasePrice = product.purchasePrice,
            puchaseUnit = product.puchaseUnit,
            alternateCapital = product.alternate_capital,
            isDeleted = product.isDeleted,
            needsSyncs = product.needsSyncs,
            lastUpdated = System.currentTimeMillis()
        ).apply {cloudId=product.productCloudId.toString() }
        return cloudProduct


    }
}