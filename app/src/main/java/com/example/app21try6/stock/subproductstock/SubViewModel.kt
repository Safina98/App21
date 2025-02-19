package com.example.app21try6.stock.subproductstock

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app21try6.database.daos.DetailWarnaDao
import com.example.app21try6.database.daos.SubProductDao
import com.example.app21try6.database.daos.TransDetailDao
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.database.tables.TransactionDetail
import kotlinx.coroutines.*
import java.util.Date
import java.util.UUID

class SubViewModel (
    val database2: SubProductDao,
    application: Application,
    val product_id:Array<Int>,
    val database3 : TransDetailDao,
    val detailWarnaDao:DetailWarnaDao,
    val sum_id:Int
): AndroidViewModel(application){

    private var viewModelJob = Job()
    //ui scope for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)

    val allProductFromDb = database2.getAll(product_id[0])

    private val _addItem = MutableLiveData<Boolean>()
    val addItem: LiveData<Boolean> get() = _addItem

    private val _navigateProduct = MutableLiveData<Array<String>>()
    val navigateProduct:LiveData<Array<String>> get() = _navigateProduct

    private var checkedItemList = mutableListOf<SubProduct>()

    private val _detailWarnaList=MutableLiveData<List<DetailWarnaTable>?>()
    val detailWarnaList:LiveData<List<DetailWarnaTable>?>get() = _detailWarnaList

   val _selectedSubProduct=MutableLiveData<SubProduct?>()
    val selectedSubProduct:LiveData<SubProduct?>get() = _selectedSubProduct


    fun toggleSelectedSubProductId(subProduct: SubProduct?) {
        _selectedSubProduct.value = if (_selectedSubProduct.value?.sub_id ==subProduct?.sub_id) null else subProduct
       //if id not the same change rc background
    }
    fun insertDetailWarna(batchCount:Double,net:Double){
        uiScope.launch {
            val detailWarnaTable=DetailWarnaTable()
            detailWarnaTable.batchCount=batchCount
            detailWarnaTable.net=net
            detailWarnaTable.ket="Stok Awal"
            detailWarnaTable.subId=_selectedSubProduct.value!!.sub_id
            detailWarnaTable.ref=UUID.randomUUID().toString()
            val inventoryLog=InventoryLog()
            inventoryLog.detailWarnaRef=detailWarnaTable.ref
            inventoryLog.subProductId=detailWarnaTable.subId
            inventoryLog.isi=detailWarnaTable.net
            inventoryLog.pcs=detailWarnaTable.batchCount.toInt()
            inventoryLog.barangLogKet="Masuk"
            inventoryLog.barangLogDate= Date()
            inventoryLog.barangLogRef=UUID.randomUUID().toString()
            inventoryLog.brandId= null
            inventoryLog.productId=null

            insertDetailWarnaToDb(detailWarnaTable)
            getDetailWarnaList(selectedSubProduct.value?.sub_id)
        }
    }
    fun getDetailWarnaList(id:Int?){
        uiScope.launch {
            val list= if (id!=null)getDetailWarnaListFromDb(id) else listOf()
            _detailWarnaList.value=list
        }
    }
    private suspend fun getDetailWarnaListFromDb(id:Int):List<DetailWarnaTable>{
        return withContext(Dispatchers.IO){
            detailWarnaDao.getDetailWarnaBySubId(id)
        }
    }
    private suspend fun insertDetailWarnaToDb(detailWarnaTable: DetailWarnaTable){
        withContext(Dispatchers.IO){
            detailWarnaDao.insert(detailWarnaTable)
        }
    }

    fun onCheckBoxClicked(subProduct: SubProduct, bool:Boolean){
        uiScope.launch {
            val transDetail = TransactionDetail()
            transDetail.sum_id = product_id[3]
            transDetail.trans_item_name = subProduct.sub_name
            if(bool){
                checkedItemList.add(subProduct)
                insertToTrans(transDetail)
            }
            else{
                checkedItemList.remove(subProduct)
                deleteFromTrans( product_id[3],subProduct.sub_name)
            }
        }
    }
    suspend fun insertToTrans(transDetail: TransactionDetail){
        withContext(Dispatchers.IO){
           database3.insert(transDetail)
        }
    }
    suspend fun deleteFromTrans(sum_id:Int,name:String){
        withContext(Dispatchers.IO){
            database3.deteleAnItemTransDetailSub(sum_id,name)
        }
    }
    fun updateSubProduct(subProduct: SubProduct, text_: String, i: Int
    ){
        uiScope.launch {
            var text = text_
            if (text ==""){text="click to add"}
            if (i==1){
                subProduct.sub_name = text
                updateSubName(subProduct)
            }
            else if(i==2){subProduct.warna =text }
            else if(i==3){subProduct.ket=text}
            if (i!=1)update(subProduct)
        }
    }
    private suspend fun update(subProduct: SubProduct){ withContext(Dispatchers.IO){ database2.update(subProduct) } }
    fun deleteSubProduct(subProduct: SubProduct){ uiScope.launch { delete(subProduct) } }
    private suspend fun delete(subProduct: SubProduct){ withContext(Dispatchers.IO){ database2.delete(subProduct.sub_id) } }
    fun insertAnItemSubProductStock(subProduct_name:String){
        uiScope.launch {
            if (subProduct_name!=""){
            val subProduct= SubProduct()
            subProduct.product_code = product_id[0]
            subProduct.sub_name = subProduct_name
            subProduct.brand_code = product_id[1]
            subProduct.cath_code = product_id[2]
           //Log.i("SubProbs","${product_id[0]},${product_id[1]},${product_id[2]}}")
            insert(subProduct)
            }
        }
    }
    private suspend fun insert(subProduct: SubProduct){
        withContext(Dispatchers.IO){
            database2.insert(subProduct)
            //database2.insert_try(subProduct.sub_name,subProduct.product_code,subProduct.brand_code,subProduct.cath_code)
        }
    }
    fun addStockClicked(subProduct: SubProduct){
        uiScope.launch {
            subProduct.roll_u = subProduct.roll_u+1
            update(subProduct)
        }
    }
    fun subsStockClicked(subProduct: SubProduct){
        uiScope.launch {
            subProduct.roll_u = subProduct.roll_u-1
            update(subProduct)
        }
    }
    fun resetAllSubProductStock(){
        uiScope.launch {
            val pList=allProductFromDb.value
            resetSupProductSuspend(pList!!)

        }
    }
    private suspend fun updateSubName(subProduct: SubProduct){
        withContext(Dispatchers.IO){
            database2.updateSubProductAndTransDetail(subProduct)
        }
    }
    private suspend fun resetSupProductSuspend(pList:List<SubProduct>){
        withContext(Dispatchers.IO){
            for(p in pList){
                p.roll_bg=0
                p.roll_bt=0
                p.roll_kg=0
                p.roll_kt=0
                p.roll_sg=0
                p.roll_st=0
                p.roll_u=0
                update(p)
            }
        }
    }
    /////////////////////////////////////////////////////////////////////////////////

    fun onAddItem(){ _addItem.value = true }
    fun onItemAdded(){ _addItem.value = false}
    fun onLongClick(v: View): Boolean { return true}
    fun onBrandCLick(id: Array<String>){ _navigateProduct.value = id }
    @SuppressLint("NullSafeMutableLiveData")
    fun onBrandNavigated() { _navigateProduct.value  = null }
    //logic goes here
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}