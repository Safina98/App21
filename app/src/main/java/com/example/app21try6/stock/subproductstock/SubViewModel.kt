package com.example.app21try6.stock.subproductstock

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.database.tables.TransactionDetail
import kotlinx.coroutines.*
import java.util.Date
import java.util.UUID

class SubViewModel (
    private val stockRepo:StockRepositories,
    private val transRepository: TransactionsRepository,
    val sum_id:Int,
    val product_id:Array<Int>,
    application: Application
): AndroidViewModel(application){

    private var viewModelJob = Job()
    //ui scope for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)

    val allProductFromDb = stockRepo.getSubProductLiveData(product_id[0])

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

    fun insertDetailWarna(batchCount: Double, net: Double) {
        uiScope.launch {
            val subId = _selectedSubProduct.value?.sub_id ?: return@launch
            val productId = stockRepo.getProdutId(subId) ?: return@launch
            val brandId = stockRepo.getBrandId(productId) ?: return@launch
            val existingDetailWarna = stockRepo.isDetailWarnaExist(subId, net)
            if (existingDetailWarna == null) {
                // Insert new record
                val newDetailWarna = DetailWarnaTable(
                    subId = subId,
                    net = net,
                    batchCount = batchCount,
                    ket = "Stok Awal",
                    ref = UUID.randomUUID().toString()
                )
                stockRepo.insertDetailWarna(newDetailWarna, createInventoryLog(newDetailWarna, batchCount,productId, brandId))
            } else {
                // Update existing record
                existingDetailWarna.batchCount += batchCount  // Accumulate batch count
                stockRepo.updateDetailWarna(existingDetailWarna, createInventoryLog(existingDetailWarna, batchCount,productId, brandId))
            }
            getDetailWarnaList(subId) // Refresh UI
        }
    }

    fun createInventoryLog(detailWarnaTable: DetailWarnaTable,batchCount:Double,productId:Int,brandId:Int):InventoryLog{
        val inventoryLog=InventoryLog()
        inventoryLog.detailWarnaRef=detailWarnaTable.ref
        inventoryLog.subProductId=detailWarnaTable.subId
        inventoryLog.isi=detailWarnaTable.net
        inventoryLog.pcs=batchCount.toInt()
        inventoryLog.barangLogKet="Masuk"
        inventoryLog.barangLogDate= Date()
        inventoryLog.barangLogRef=UUID.randomUUID().toString()
        inventoryLog.productId=productId
        inventoryLog.brandId=brandId
        return inventoryLog

    }

    fun getDetailWarnaList(id:Int?){
        uiScope.launch {
            val list= if (id!=null)stockRepo.getDetailWarnaList(id) else listOf()
            _detailWarnaList.value=list
        }
    }

    fun onCheckBoxClicked(subProduct: SubProduct, bool:Boolean){
        uiScope.launch {
            val transDetail = TransactionDetail()
            transDetail.sum_id = product_id[3]
            transDetail.trans_item_name = subProduct.sub_name
            if(bool){
                checkedItemList.add(subProduct)
                transRepository.insertTransDetail(transDetail)
            }
            else{
                checkedItemList.remove(subProduct)
                transRepository.deleteTransDetail( product_id[3],subProduct.sub_name)
            }
        }
    }

    fun updateSubProduct(subProduct: SubProduct, text_: String, i: Int){
        uiScope.launch {
            var text = text_
            if (text ==""){text="click to add"}
            if (i==1){
                subProduct.sub_name = text
                stockRepo.updateSubName(subProduct)
            }
            else if(i==2){subProduct.warna =text }
            else if(i==3){subProduct.ket=text}
            if (i!=1)stockRepo.updateSubProduct(subProduct)
        }
    }

    fun deleteSubProduct(subProduct: SubProduct){ uiScope.launch { stockRepo.deleteSubProduct(subProduct) } }

    fun insertAnItemSubProductStock(subProduct_name:String){
        uiScope.launch {
            if (subProduct_name!=""){
            val subProduct= SubProduct()
            subProduct.product_code = product_id[0]
            subProduct.sub_name = subProduct_name
            subProduct.brand_code = product_id[1]
            subProduct.cath_code = product_id[2]
            stockRepo.insertSubProduct(subProduct)
            }
        }
    }

    fun addStockClicked(subProduct: SubProduct){
        uiScope.launch {
            subProduct.roll_u = subProduct.roll_u+1
            stockRepo.updateSubProduct(subProduct)
        }
    }

    fun subsStockClicked(subProduct: SubProduct){
        uiScope.launch {
            subProduct.roll_u = subProduct.roll_u-1
            stockRepo.updateSubProduct(subProduct)
        }
    }

    fun resetAllSubProductStock(){
        uiScope.launch {
            val pList=allProductFromDb.value
            resetSupProductSuspend(pList!!)

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
                stockRepo.updateSubProduct(p)
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