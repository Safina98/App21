package com.example.app21try6.database.cloud


import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class BrandCloud(
    var brandName: String = "",
    var cathCode: Int = 0,
    var lastUpdated: Long = System.currentTimeMillis(),

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



// Add the rest – I’ll give you all 18 right now if you want, or continue with the main ones first
// Just say “give me all 18 cloud classes” if you want them all at once