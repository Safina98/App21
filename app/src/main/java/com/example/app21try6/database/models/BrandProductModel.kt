package com.example.app21try6.database.models

data class BrandProductModel (
    var id:Int,
    var brandId:Long?,
    var parentId:Int?,
    var name:String
)