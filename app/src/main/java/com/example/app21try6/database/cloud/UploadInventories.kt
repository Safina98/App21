package com.example.app21try6.database.cloud

import com.example.app21try6.Constants.TABLENAMES
import com.example.app21try6.database.tables.Brand
import com.example.app21try6.database.tables.Category
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.MerchandiseRetail
import com.example.app21try6.database.tables.Product
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.database.tables.TransactionDetail
import com.example.app21try6.database.tables.TransactionSummary

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

    fun convertSubProductToSubProductCloud(subProduct: SubProduct): SubProductCloud {
        val cloudSubProduct = SubProductCloud(
            subName = subProduct.sub_name,
            rollU = subProduct.roll_u,
            warna = subProduct.warna,
            ket = subProduct.ket,
            productCloudId = subProduct.productCloudId,
            brandCode = subProduct.brand_code,
            cathCode = subProduct.cath_code,
            isChecked = subProduct.is_checked,
            discountId = subProduct.discountId,
            isDeleted = subProduct.isDeleted,
            needsSyncs = subProduct.needsSyncs,
            lastUpdated = System.currentTimeMillis()
        ).apply { cloudId = subProduct.sPCloudId.toString() }
        return cloudSubProduct
    }

    fun convertDetailWarnaToDetailWarnaCloud(detailWarna: DetailWarnaTable): DetailWarnaCloud {
        val cloudDetailWarna = DetailWarnaCloud(
            sPCloudId = detailWarna.sPCloudId,
            batchCount = detailWarna.batchCount,
            net = detailWarna.net,
            ket = detailWarna.ket,
            ref = detailWarna.ref,
            isDeleted = detailWarna.isDeleted,
            needsSyncs = detailWarna.needsSyncs,
            lastUpdated = System.currentTimeMillis()
        ).apply { cloudId = detailWarna.dWCloudId.toString() }
        return cloudDetailWarna
    }

    fun convertMerchandiseRetailToMerchandiseRetailCloud(merchandiseRetail: MerchandiseRetail): MerchandiseRetailCloud {
        val cloudMerchandiseRetail = MerchandiseRetailCloud(
            sPCloudId = merchandiseRetail.sPCloudId,
            net = merchandiseRetail.net,
            ref = merchandiseRetail.ref,
            date = merchandiseRetail.date.time,
            isDeleted = merchandiseRetail.isDeleted,
            needsSyncs = merchandiseRetail.needsSyncs,
            lastUpdated = System.currentTimeMillis()
        ).apply { cloudId = merchandiseRetail.mRCloudId.toString() }
        return cloudMerchandiseRetail
    }

    fun convertTransactionDetailToTransactionDetailCloud(transactionDetail: TransactionDetail): TransactionDetailCloud {
        val cloudTransactionDetail = TransactionDetailCloud(
            tSCloudId = transactionDetail.tSCloudId,
            transItemName = transactionDetail.trans_item_name,
            qty = transactionDetail.qty,
            transPrice = transactionDetail.trans_price,
            totalPrice = transactionDetail.total_price,
            isPrepared = transactionDetail.is_prepared,
            isCutted = transactionDetail.is_cutted,
            transDetailDate = transactionDetail.trans_detail_date?.time,
            unit = transactionDetail.unit,
            unitQty = transactionDetail.unit_qty,
            itemPosition = transactionDetail.item_position,
            sPCloudId = transactionDetail.sPCloudId,
            productCapital = transactionDetail.product_capital,
            isDeleted = transactionDetail.isDeleted,
            needsSyncs = transactionDetail.needsSyncs,
            lastUpdated = System.currentTimeMillis()
        ).apply { cloudId = transactionDetail.tDCloudId.toString() }
        return cloudTransactionDetail
    }

    fun convertTransactionSummaryToTransactionSummaryCloud(transactionSummary: TransactionSummary): TransactionSummaryCloud {
        val cloudTransactionSummary = TransactionSummaryCloud(
            custName = transactionSummary.cust_name,
            totalTrans = transactionSummary.total_trans,
            totalAfterDiscount = transactionSummary.total_after_discount,
            paid = transactionSummary.paid,
            transDate = transactionSummary.trans_date?.time,
            isTaken = transactionSummary.is_taken_,
            isPaidOff = transactionSummary.is_paid_off,
            isKeeped = transactionSummary.is_keeped,
            isLogged = transactionSummary.is_logged,
            ref = transactionSummary.ref,
            sumNote = transactionSummary.sum_note,
            custId = transactionSummary.custId,
            isDeleted = transactionSummary.isDeleted,
            needsSyncs = transactionSummary.needsSyncs,
            lastUpdated = System.currentTimeMillis()
        ).apply { cloudId = transactionSummary.tSCloudId.toString() }
        return cloudTransactionSummary
    }
}