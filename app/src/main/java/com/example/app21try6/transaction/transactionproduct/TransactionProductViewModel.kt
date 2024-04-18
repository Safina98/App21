package com.example.app21try6.transaction.transactionproduct

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.app21try6.database.*

class TransactionProductViewModel(val sum_id:Int,
                                  val database1: CategoryDao,
                                  val database2: ProductDao,
                                  val database3: BrandDao,
                                  val database4: SubProductDao,
                                  val date:Array<String>,
                                  val database5: TransSumDao,
                                  val database6: TransDetailDao,
                                  application: Application
): AndroidViewModel(application) {
    val all_product_from_db  = database2.getAllProduct()
    private val _navigateToTransSelect = MutableLiveData<Array<String>>()
    val navigateToTransSelect: LiveData<Array<String>>
        get() = _navigateToTransSelect

    fun onNavigatetoTransSelect(id:String){
        _navigateToTransSelect.value= arrayOf(date[0],id)
    }
    @SuppressLint("NullSafeMutableLiveData")
    fun onNavigatedtoTransSelect(){
        _navigateToTransSelect.value =null
    }


}