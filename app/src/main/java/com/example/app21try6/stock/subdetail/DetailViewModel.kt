package com.example.app21try6.stock.subdetail

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app21try6.database.SubProduct
import com.example.app21try6.database.SubProductDao
import kotlinx.coroutines.*

class DetailViewModel (
    val database2: SubProductDao,
    application: Application,
    val sub_id:Array<Int>
): AndroidViewModel(application){

    private var viewModelJob = Job()
    //ui scope for coroutines
    private val uiScope = CoroutineScope(Dispatchers.Main +  viewModelJob)
    /////////////////////////
    val item_ = database2.getAnItem(sub_id[0])
    val nama = database2.getNama(sub_id[0])
    val warna = database2.getWarna(sub_id[0])
    val ket = database2.getKet(sub_id[0])
    val stokU = database2.getStokU(sub_id[0])
    val stokBT = database2.getStokBT(sub_id[0])
    val stokUST = database2.getStokST(sub_id[0])
    val stokUKT = database2.getStokKT(sub_id[0])
    val stokUBG = database2.getStokBG(sub_id[0])
    val stokUSG = database2.getStokSG(sub_id[0])
    val stokUKG = database2.getStokKG(sub_id[0])

    ////////////////////////
    fun onSubsClicked(code:Int){
        uiScope.launch {

            var sub = item_.value

            if (sub!=null){
                Toast.makeText(getApplication(),"subs clicked",Toast.LENGTH_SHORT).show()
                if (code==1) {
                    sub.roll_u = sub.roll_u - 1
                }
                else if (code==2) {
                    sub.roll_bt = sub.roll_bt - 1
                }
                else if (code==3) {
                    sub.roll_st = sub.roll_st - 1
                }
                else if (code==4) {
                    sub.roll_kt = sub.roll_kt - 1
                }
                else if (code==5) {
                    sub.roll_bg = sub.roll_bg - 1
                }
                else if (code==6) {
                    sub.roll_sg = sub.roll_sg - 1
                }
                else if (code==7) {
                    sub.roll_kg = sub.roll_kg - 1
                }
                update(sub)
            }
        }



    }
    fun onAddClicked(code:Int){
        uiScope.launch {
            var sub = item_.value

            if (sub!=null){
                if (code==1) sub.roll_u = sub.roll_u+1
                else if (code==2) sub.roll_bt = sub.roll_bt+1
                else if (code==3) sub.roll_st = sub.roll_st+1
                else if (code==4) sub.roll_kt = sub.roll_kt+1
                else if (code==5) sub.roll_bg = sub.roll_bg+1
                else if (code==6) sub.roll_sg = sub.roll_sg+1
                else if (code==7) sub.roll_kg = sub.roll_kg+1
                update(sub)
            }

        }



    }

    fun updateSubProduct(subProduct: SubProduct, text: String, i: Int
    ){
        uiScope.launch {
            if (i==1){subProduct.sub_name = text

            }
            else if(i==2){subProduct.warna =text }
            else if(i==3){subProduct.ket=text}
            update(subProduct)
        }
    }
    private suspend fun update(subProduct: SubProduct){
        withContext(Dispatchers.IO){
            database2.update(subProduct)
        }
    }

    fun deleteSubProduct(subProduct: SubProduct){
        uiScope.launch {
            delete(subProduct)
        }
    }
    private suspend fun delete(subProduct: SubProduct){
        withContext(Dispatchers.IO){
            database2.delete(subProduct.sub_id)
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
    /////////////////////////////////////////////////////////////////////////////////

    //logic goes here
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}