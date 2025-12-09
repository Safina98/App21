package com.example.app21try6.database.cloud


import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class BrandCloud(
    var brandName: String = "",
    var cathCode: Long = 0L,
    var lastUpdated: Long = System.currentTimeMillis(),
    var isDeleted: Boolean = false,
    var needsSyncs: Int=0,

    // This field is only used on device, never uploaded, never saved to Firebase
    @get:Exclude
    var cloudId: String = ""
)

@IgnoreExtraProperties
data class CategoryCloud(
    @get:Exclude var cloudId: String ="",
    var categoryName: String = "",
    var lastUpdated: Long = System.currentTimeMillis(),
    var isDeleted: Boolean = false,
)

@IgnoreExtraProperties
data class ProductCloud(
    var productName: String = "",
    var productPrice: Int = 0,
    var productCapital: Int = 0,
    var checkBoxBoolean: Boolean = false,
    var bestSelling: Boolean = false,
    var defaultNet: Double = 0.0,
    var alternatePrice: Double = 0.0,
    var brandCode: Long = 0L,
    var cathCode: Long = 0L,
    var discountId: Int? = null,
    var purchasePrice: Int? = null,
    var puchaseUnit: String? = null,
    var alternateCapital: Double = 0.0,
    var isDeleted: Boolean = false,
    var needsSyncs: Int = 1,
    var lastUpdated: Long = System.currentTimeMillis(),

    @get:Exclude
    var cloudId: String = ""
)

@IgnoreExtraProperties
data class SubProductCloud(
    var subName: String = "",
    var rollU: Int = 0,
    var warna: String = "",
    var ket: String = "",
    var productCloudId: Long = 0,
    var brandCode: Long = 0L,
    var cathCode: Long = 0L,
    var isChecked: Boolean = false,
    var discountId: Int? = null,
    var isDeleted: Boolean = false,
    var needsSyncs: Int = 1,
    var lastUpdated: Long = System.currentTimeMillis(),

    @get:Exclude
    var cloudId: String = ""
)

@IgnoreExtraProperties
data class DetailWarnaCloud(
    var sPCloudId: Long = 0,
    var batchCount: Double = 0.0,
    var net: Double = 0.0,
    var ket: String = "",
    var ref: String = "",
    var isDeleted: Boolean = false,
    var needsSyncs: Int = 1,
    var lastUpdated: Long = System.currentTimeMillis(),

    @get:Exclude
    var cloudId: String = ""
)

@IgnoreExtraProperties
data class MerchandiseRetailCloud(
    var sPCloudId: Long = 0,
    var net: Double = 0.0,
    var ref: String = "",
    var date: Long = System.currentTimeMillis(),
    var isDeleted: Boolean = false,
    var needsSyncs: Int = 1,
    var lastUpdated: Long = System.currentTimeMillis(),

    @get:Exclude
    var cloudId: String = ""
)

@IgnoreExtraProperties
data class TransactionSummaryCloud(
    var custName: String = "",
    var totalTrans: Double = 0.0,
    var totalAfterDiscount: Double = 0.0,
    var paid: Int = 0,
    var transDate: Long? = System.currentTimeMillis(),
    var isTaken: Boolean = false,
    var isPaidOff: Boolean = false,
    var isKeeped: Boolean = false,
    var isLogged: Boolean = false,
    var ref: String = "",
    var sumNote: String? = null,
    var custId: Int? = null,
    var isDeleted: Boolean = false,
    var needsSyncs: Int = 1,
    var lastUpdated: Long = System.currentTimeMillis(),

    @get:Exclude
    var cloudId: String = ""
)

@IgnoreExtraProperties
data class TransactionDetailCloud(
    var tSCloudId: Long = 0,
    var transItemName: String = "",
    var qty: Double = 0.0,
    var transPrice: Int = 0,
    var totalPrice: Double = 0.0,
    var isPrepared: Boolean = false,
    var isCutted: Boolean = false,
    var transDetailDate: Long? = null,
    var unit: String? = null,
    var unitQty: Double = 1.0,
    var itemPosition: Int = 0,
    var sPCloudId: Long? = null,
    var productCapital: Int = 0,
    var isDeleted: Boolean = false,
    var needsSyncs: Int = 1,
    var lastUpdated: Long = System.currentTimeMillis(),

    @get:Exclude
    var cloudId: String = ""
)

@IgnoreExtraProperties
data class SummaryCloud(
    var year: Int = 2030,
    var month: String = "empty",
    var monthNumber: Int = 0,
    var day: Int = 0,
    var dayName: String = "empty",
    var date: Long? = System.currentTimeMillis(),
    var itemName: String = "empty",
    var itemSold: Double = 0.0,
    var price: Double = 0.0,
    var totalIncome: Double = 0.0,
    var productCloudId: Long? = null,
    var sPCloudId: Long? = null,
    var productCapital: Int = 0,
    var isDeleted: Boolean = false,
    var needsSyncs: Int = 1,
    var lastUpdated: Long = System.currentTimeMillis(),

    @get:Exclude
    var cloudId: String = ""
)


// Add the rest – I’ll give you all 18 right now if you want, or continue with the main ones first
// Just say “give me all 18 cloud classes” if you want them all at once
