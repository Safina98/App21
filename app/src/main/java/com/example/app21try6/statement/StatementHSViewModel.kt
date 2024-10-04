package com.example.app21try6.statement

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.CustomerDao
import com.example.app21try6.database.CustomerTable
import com.example.app21try6.database.DiscountDao
import com.example.app21try6.database.DiscountTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatementHSViewModel(application: Application,
    val discountDao: DiscountDao,
    val customerDao: CustomerDao):AndroidViewModel(application) {
    val allDiscountFromDB= discountDao.getAllDiscount()
    val allCustomerFromDb=customerDao.allCustomer()
    var id = 0
    val custList= listOf<CustomerTable>(
        CustomerTable(0,"Ajat,","Asia Jok","Makassar & Sekitarnya","Barombong"),
        CustomerTable(1,"","Alyka Jok","Makassar & Sekitarnya","Kandea"),
        CustomerTable(2,"","Auto 354","Makassar & Sekitarnya","Gowa"),
        CustomerTable(3,"Wahyu","AMV","Soppeng","Soppeng"),
        CustomerTable(4,"Akbar","Toko AM Variasi","Sengkang","Sengkang"),
        CustomerTable(5,"Asep Ramlan","Asep Ramlan","Makassar & Sekitarnya",""),
        CustomerTable(6,"","Anugrah Mebel","Bantaeng","Bantaeng"),
        CustomerTable(7,"","AT Jok","Makassar & Sekitarnya",""),
        CustomerTable(8,"","New Bandung Jok","Makassar & Sekitarnya",""),
        CustomerTable(9,"","Bandung Jok gowa","Makassar & Sekitarnya",""),
        CustomerTable(10,"","Bagus Jok","Kolaka","Jl Mekongga Indah Baypass kolut"),
        CustomerTable(11,"","Beo","Makassar & Sekitarnya",""),
        CustomerTable(12,"","Berkah Variasi","Makassar & Sekitarnya",""),
        CustomerTable(13,"","Cahaya Variasi","Makassar & Sekitarnya",""),
        CustomerTable(14,"","Dr Jok","Makassar & Sekitarnya",""),
        CustomerTable(15,"Heri","Dyna Jok","Makassar & Sekitarnya","Jl. Sunu"),
        CustomerTable(16,"","D'Fun Variasi","Kendari",""),
        CustomerTable(17,"","Densus 99","Makassar & Sekitarnya",""),
        CustomerTable(18,"Bu Titi","Eka Jok","Makassar & Sekitarnya","Jl Sungai Limboto",null,"BON"),
        CustomerTable(19,"","Evolution","Makassar & Sekitarnya",""),
        CustomerTable(20,"","Fiesta Jok","Makassar & Sekitarnya","Jl Sungai Limboto",null,"BON"),
        CustomerTable(21,"","Fakhri Jok","Sidrap",""),
        CustomerTable(22,"","Green Design","Makassar & Sekitarnya",""),
        CustomerTable(23,"","HSR Auto","Makassar & Sekitarnya","",null,"BON"),
        CustomerTable(24,"","HARAJUKU","Makassar & Sekitarnya","",null,"BON"),
        CustomerTable(25,"","Jabbal","Makassar & Sekitarnya",""),
        CustomerTable(26,"","Jok 88","Makassar & Sekitarnya","",null,"BON"),
        CustomerTable(27,"","King Variasi","Makassar & Sekitarnya",""),
        CustomerTable(28,"","Karya Jok","Makassar & Sekitarnya",""),
        CustomerTable(29,"","Kubis Mebel","Makassar & Sekitarnya",""),
        CustomerTable(30,"","Makassar Variasi","Makassar & Sekitarnya","Jl Kumala"),
        CustomerTable(31,"","Mega Buana","Makassar & Sekitarnya","Dayak"),
        CustomerTable(32,"","Mas Tono","Makassar & Sekitarnya",""),
        CustomerTable(33,"","Laquna","Makassar & Sekitarnya","Hertasning"),
        CustomerTable(34,"","Pak Maliang","Makassar & Sekitarnya",""),
        CustomerTable(35,"","Pak Ilham Gowa","Makassar & Sekitarnya","gowa"),
        CustomerTable(36,"","Pak Ramli","Sidrap",""),
        CustomerTable(37,"","Pak Ibet","Makassar & Sekitarnya",""),
        CustomerTable(38,"","Pak Agus Saputra","Makassar & Sekitarnya",""),
        CustomerTable(39,"","Pattalassang Variasi","Makassar & Sekitarnya",""),
        CustomerTable(40,"","Prima Leather","Makassar & Sekitarnya",""),
        CustomerTable(41,"","Pak Alim","Makassar & Sekitarnya",""),
        CustomerTable(42,"","Rajawali Morot Timka","Timika",""),
        CustomerTable(43,"","Rumah Kursi","Makassar & Sekitarnya",""),
        CustomerTable(44,"","Rumah Sofa","Kolaka",""),
        CustomerTable(45,"","RGARAGE","Makassar & Sekitarnya",""),
        CustomerTable(46,"","Rezky Jok","Makassar & Sekitarnya",""),
        CustomerTable(47,"","Riski Jok","Barru",""),
        CustomerTable(48,"","Beo","Makassar & Sekitarnya",""),
        CustomerTable(49,"","Sun Variasi","Makassar & Sekitarnya",""),
        CustomerTable(50,"","Susan Jok","Makassar & Sekitarnya",""),
        CustomerTable(51,"","Sumber Jok","Sengkang",""),
        CustomerTable(52,"","Surabaya Motor","Makassar & Sekitarnya",""),
        CustomerTable(53,"","Selayar","Makassar & Sekitarnya",""),
        CustomerTable(54,"","SKAAD Audio Bintuni","Bintuni",""),
        CustomerTable(55,"","Sam & Sons","Makassar & Sekitarnya",""),
        CustomerTable(56,"","Terminal","Makassar & Sekitarnya",""),
        CustomerTable(57,"","Unnang","Makassar & Sekitarnya",""),
        CustomerTable(58,"","Variasi 77","Makassar & Sekitarnya",""),
        CustomerTable(59,"","Wendy","Makassar & Sekitarnya",""),
        CustomerTable(60,"","Yusdar Motor","Makassar & Sekitarnya",""),
        CustomerTable(61,"","Chessy","Makassar & Sekitarnya","",null,"BON"),
        CustomerTable(62,"","Global","Makassar & Sekitarnya","",null,"BON"),
    )
    fun getautoIncrementId():Int{
        id = id+1
        return id
    }
    fun insertBatch(){
        viewModelScope.launch{
            /*
            for (i in custList){
                insertCusomer(i.customerName,i.customerBussinessName,i.customerLocation,i.customerAddress,i.customerLevel,i.customerTag1)
            }

             */
            allCustomer()
        }

    }
    private suspend fun allCustomer(){
        withContext(Dispatchers.IO){
            val list = customerDao.selectAll()
            for (i in list){
                Log.i("CUSTPROBS","$i")
            }

        }
    }

    fun insertCusomer(name:String,businessName:String,location:String?,address:String,level:String?,tag1:String?){
        viewModelScope.launch {
            val customerTable = CustomerTable()
            customerTable.customerName=name
            customerTable.customerBussinessName=businessName
            customerTable.customerLocation=location
            customerTable.customerAddress=address
            customerTable.customerTag1=tag1
            insertCustomerToDB(customerTable)
        }
    }

    fun insertDiscount(value:Double,name:String,minQty:Double?,tipe:String,location:String){
        viewModelScope.launch {
            val discountTable=populateDiscount(null,value,name, minQty, tipe, location)
            insertDiscountToDB(discountTable)
            Log.i("Disc","$allDiscountFromDB")
        }
    }
    fun updateDiscount(id:Int,value:Double,name:String,minQty:Double?,tipe:String,location:String){
        viewModelScope.launch {
            val discountTable=populateDiscount(id,value,name, minQty, tipe, location)
            updateDiscountFromDB(discountTable)
        }
    }
    fun populateDiscount(id:Int?,value:Double,name:String,minQty:Double?,tipe:String,location:String):DiscountTable{
        val discountTable=DiscountTable()
        //discountTable.discountId=getautoIncrementId()
        if (id!=null) discountTable.discountId=id
        discountTable.discountValue = value
        discountTable.discountName=name
        discountTable.minimumQty=minQty
        discountTable.discountType=tipe
        discountTable.custLocation= if(location.isNotEmpty()) location else null
        return discountTable
    }
    fun deleteDiscountTable(discountTable: DiscountTable){viewModelScope.launch { deleteDiscountFromDB(discountTable) }}
    private suspend fun insertDiscountToDB(discountTable: DiscountTable){
        withContext(Dispatchers.IO){
            discountDao.insert(discountTable)
        }
    }
    private suspend fun deleteDiscountFromDB(discountTable: DiscountTable){
        withContext(Dispatchers.IO){
            discountDao.delete(discountTable)
        }
    }
    private suspend fun updateDiscountFromDB(discountTable: DiscountTable){
        withContext(Dispatchers.IO){
            discountDao.update(discountTable)
        }
    }
    private suspend fun insertCustomerToDB(customerTable: CustomerTable){
        withContext(Dispatchers.IO){
            customerDao.insert(customerTable)
        }
    }
}