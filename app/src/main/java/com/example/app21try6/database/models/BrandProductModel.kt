package com.example.app21try6.database.models

data class BrandProductModel (
    var id:Int,
    var brandId:Long?,
    var parentId:Long?,
    var name:String
)