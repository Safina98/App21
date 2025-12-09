package com.example.app21try6.database.cloud


import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class BrandCloud(
    var brandName: String = "",
    var cathCode: Long = 0L,
    var lastUpdated: Long = System.currentTimeMillis(),
    var isDeleted: Boolean = false,

    // This field is only used on device, never uploaded, never saved to Firebase
    @get:Exclude
    var cloudId: String = ""
)

@IgnoreExtraProperties
data class CategoryCloud(
    @get:Exclude var cloudId: String ="",
    var categoryName: String = "",
    var lastUpdated: Long = System.currentTimeMillis()
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

// Add the rest – I’ll give you all 18 right now if you want, or continue with the main ones first
// Just say “give me all 18 cloud classes” if you want them all at once
