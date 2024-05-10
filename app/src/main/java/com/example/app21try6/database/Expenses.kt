package com.example.app21try6.database

data class  Expenses (
    var id : Int,
    var kategori:String,
    var expense_name: String,
    var expense_ammount:Int?,
)

data class  ExpenseCategory (
    var id : Int,
    var kategoriName:String,
    var isPeriodic:Boolean,

)