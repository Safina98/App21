package com.example.app21try6.stock.subproductstock

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.models.DetailMerchandiseModel
import com.example.app21try6.database.repositories.StockRepositories
import com.example.app21try6.database.repositories.TransactionsRepository
import com.example.app21try6.database.tables.DetailWarnaTable
import com.example.app21try6.database.tables.InventoryLog
import com.example.app21try6.database.tables.MerchandiseRetail
import com.example.app21try6.database.tables.SubProduct
import com.example.app21try6.database.tables.TransactionDetail
import kotlinx.coroutines.*
import java.util.Date
import java.util.UUID

class SubViewModel (
    private val stockRepo:StockRepositories,
    private val transRepository: TransactionsRepository,
    private val product_id:Long,
    private val parameterBrandId:Long,
    private val ctgId:Long,
    private val tSId: Long,
    private val parameterSubId:Int,
    val sum_id: Long,
    application: Application
): AndroidViewModel(application){

    private var viewModelJob = Job()
    //ui scope for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)

    val allProductFromDb = stockRepo.getSubProductLiveData(product_id,null)
    private val _brandId=MutableLiveData<Long?>(null)
    val brandId:LiveData<Long?>get() = _brandId

    val subProductFromDb :LiveData<List<SubProduct>> = _brandId.switchMap { brandId ->
        if (brandId==null){
            stockRepo.getSubProductLiveData(product_id,null)
        }else{
            stockRepo.getSubProductLiveData(null,brandId)
        }
    }

    private val _addItem = MutableLiveData<Boolean>()
    val addItem: LiveData<Boolean> get() = _addItem

    private val _navigateProduct = MutableLiveData<Array<String>>()
    val navigateProduct:LiveData<Array<String>> get() = _navigateProduct

    private var checkedItemList = mutableListOf<SubProduct>()

    private val _detailWarnaList=MutableLiveData<List<DetailMerchandiseModel>?>()
    val detailWarnaList:LiveData<List<DetailMerchandiseModel>?>get() = _detailWarnaList

    private val _retailList=MutableLiveData<List<DetailMerchandiseModel>?>()
    val retailList:LiveData<List<DetailMerchandiseModel>?>get() = _retailList

    val _selectedSubProduct=MutableLiveData<SubProduct?>()
    val selectedSubProduct:LiveData<SubProduct?>get() = _selectedSubProduct


    fun toggleSelectedSubProductId(subProduct: SubProduct?) {
        _selectedSubProduct.value = if (_selectedSubProduct.value?.sub_id ==subProduct?.sub_id) null else subProduct
    }
    fun getSubProductById(spId:Int){
        viewModelScope.launch {
           val subProduct= stockRepo.getsubProductById(spId)
            toggleSelectedSubProductId(subProduct)
        }
    }
    fun onCheckBoxClicked(subProduct: SubProduct, bool:Boolean){
        uiScope.launch {
            val transDetail = TransactionDetail()
            transDetail.tSCloudId = tSId
            transDetail.trans_item_name = subProduct.sub_name
            transDetail.tDCloudId= System.currentTimeMillis()
            transDetail.needsSyncs=1
            if(bool){
                checkedItemList.add(subProduct)
                transRepository.insertTransDetail(transDetail)
            }
            else{
                checkedItemList.remove(subProduct)
                transRepository.deleteTransDetail( sum_id,subProduct.sub_name)
            }
        }
    }

    fun updateSubProduct(subProduct: SubProduct, text_: String, i: Int){
        uiScope.launch {
            var text = text_
            subProduct.needsSyncs=1
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

    fun addStockClicked(subProduct: SubProduct){
        uiScope.launch {
            subProduct.roll_u = subProduct.roll_u+1
            subProduct.needsSyncs=1
            stockRepo.updateSubProduct(subProduct)
        }
    }

    fun subsStockClicked(subProduct: SubProduct){
        uiScope.launch {
            subProduct.roll_u = subProduct.roll_u-1
            subProduct.needsSyncs=1
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
                p.roll_u=0
                stockRepo.updateSubProduct(p)
                stockRepo.deleteDetailWarnaBySubId(p.sub_id)
            }
        }
    }
    fun insertAnItemSubProductStock(subProduct_name:String){
        uiScope.launch {
            if (subProduct_name!=""){
                val subProduct= SubProduct()
                subProduct.productCloudId = product_id
                subProduct.sub_name = subProduct_name
                subProduct.brand_code = parameterBrandId
                subProduct.cath_code = ctgId
                subProduct.needsSyncs=1
                subProduct.sPCloudId=System.currentTimeMillis()
                stockRepo.insertSubProduct(subProduct)
            }
        }
    }

    fun getDetailWarnaList(id:Int?){
        uiScope.launch {
            val list= if (id!=null)stockRepo.getDetailWarnaList(id) else listOf()
            _detailWarnaList.value=list
        }
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
                    ref = UUID.randomUUID().toString(),
                    dWCloudId = System.currentTimeMillis().toLong(),

                )
                stockRepo.insertDetailWarna(newDetailWarna, createInventoryLog(newDetailWarna, batchCount,productId, brandId,"Masuk"))
            } else {
                // Update existing record
                existingDetailWarna.batchCount += batchCount  // Accumulate batch count
                existingDetailWarna.needsSyncs=1
                stockRepo.updateDetailWarna(existingDetailWarna, createInventoryLog(existingDetailWarna, batchCount,productId, brandId,"Masuk"),null)
            }
            getDetailWarnaList(subId) // Refresh UI
        }
    }
    fun deleteDetailWarna(detailWarnaModel: DetailMerchandiseModel){
        uiScope.launch {
            var detailWarnaTable=detailWarnaModel.toDetailWarnaTable()
            val productId = stockRepo.getProdutId(detailWarnaTable.subId) ?: return@launch
            val brandId = stockRepo.getBrandId(productId) ?: return@launch
            stockRepo.deleteDetailWarna(detailWarnaTable,createInventoryLog(detailWarnaTable, detailWarnaTable.batchCount,productId, brandId,"Dihapus"),null)
            getDetailWarnaList(detailWarnaTable.subId)
        }
    }
    fun trackDetailWarna(detailWarnaModel: DetailMerchandiseModel){
        uiScope.launch {
            val detailWarnaTable=detailWarnaModel.toDetailWarnaTable()
            detailWarnaTable.batchCount -=1
            val productId = stockRepo.getProdutId(detailWarnaTable.subId) ?: return@launch
            val brandId = stockRepo.getBrandId(productId) ?: return@launch
            val inventoryLog =createInventoryLog(detailWarnaTable,detailWarnaTable.batchCount,productId,brandId,"Keluar Retail")
            val merchandiseRetail=createMerchandiseRetail(detailWarnaModel)
            merchandiseRetail.mRCloudId=System.currentTimeMillis()
            if (detailWarnaTable.batchCount>0)
                stockRepo.updateDetailWarna(detailWarnaTable,inventoryLog,merchandiseRetail)
            else
                stockRepo.deleteDetailWarna(detailWarnaTable,inventoryLog,merchandiseRetail)
            getDetailWarnaList(detailWarnaTable.subId)
            getRetailList(detailWarnaTable.subId)
        }

    }

    fun getRetailList(subId:Int?){
        uiScope.launch {
            val list= if (subId!=null)stockRepo.selectRetailBySumId(subId) else listOf()
            _retailList.value=list
        }
    }
    fun deleteRetail(model: DetailMerchandiseModel){
        uiScope.launch {
            stockRepo.deleteRetail(model.id)
            getRetailList(model.sub_id)
        }
    }
    fun updateRetail( detailWarnaModel: DetailMerchandiseModel){
        uiScope.launch {
            val merchandiseRetail = createMerchandiseRetail(detailWarnaModel)
            merchandiseRetail.id = detailWarnaModel.id
            stockRepo.updateDetailRetail(merchandiseRetail)
            getRetailList(detailWarnaModel.sub_id)
        }
    }
    fun DetailMerchandiseModel.toDetailWarnaTable(): DetailWarnaTable {
        return DetailWarnaTable(
            id = this.id,
            subId = this.sub_id,
            ref = this.ref,
            net = this.net,
            batchCount = this.batchCount ?: 0.0, // Handle null with default value
            ket = this.ket ?: "", // Handle null with empty string
            // Note: date is not part of DetailWarnaTable so it's omitted
        )
    }
    fun createInventoryLog(detailWarnaTable: DetailWarnaTable,batchCount:Double,productId:Long,brandId:Long,ket:String):InventoryLog{
        val inventoryLog=InventoryLog()
        inventoryLog.detailWarnaRef=detailWarnaTable.ref
        inventoryLog.subProductId=detailWarnaTable.subId
        inventoryLog.isi=detailWarnaTable.net
        inventoryLog.pcs=batchCount.toInt()
        inventoryLog.barangLogKet=ket
        inventoryLog.barangLogDate= Date()
        inventoryLog.barangLogRef=UUID.randomUUID().toString()
        inventoryLog.productCloudId =productId
        inventoryLog.brandId=brandId
        inventoryLog.iLCloudId=System.currentTimeMillis()
        inventoryLog.needsSyncs=1
        return inventoryLog
    }

    fun createMerchandiseRetail(detailWarnaTable: DetailMerchandiseModel):MerchandiseRetail{
        Log.i("Check","${detailWarnaTable.id}")
        val merchandiseRetail=MerchandiseRetail()
        merchandiseRetail.sub_id=detailWarnaTable.sub_id
        merchandiseRetail.net=detailWarnaTable.net
        merchandiseRetail.ref=UUID.randomUUID().toString()
        merchandiseRetail.date=detailWarnaTable.date?:Date()
        return merchandiseRetail
    }
    /////////////////////////////////////////////////////////////////////////////////

    fun onAddItem(){ _addItem.value = true }

    fun onItemAdded(){ _addItem.value = false}

    fun onLongClick(v: View): Boolean { return true}

    fun onShowByBrandId(){
        if (brandId.value==null){
            _brandId.value=parameterBrandId
        }else{
            _brandId.value=null
        }
    }

    fun onBrandCLick(id: Array<String>){ _navigateProduct.value = id }
    @SuppressLint("NullSafeMutableLiveData")
    fun onBrandNavigated() { _navigateProduct.value  = null }
    //logic goes here
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}