package com.example.app21try6.transaction.transactiondetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao
import com.example.app21try6.formatRupiah

class TransactionDetailViewModel (application: Application,
                                  private val datasource1: TransSumDao,
                                  private val datasource2: TransDetailDao
                                  , var id:Int):AndroidViewModel(application){

    val transDetail = datasource2.selectATransDetail(id)
    private val _navigateToEdit = MutableLiveData<Int>()
    val navigateToEdit: LiveData<Int>
        get() = _navigateToEdit
    val trans_sum = datasource1.getTransSum(id)
    val trans_total_ = datasource2.getTotalTrans(id)
    val trans_total: LiveData<String> =
        Transformations.map(trans_total_) { formatRupiah(it).toString() }
    //val trans_detail = datasource2.deleteATransDetail(id)
    fun onNavigateToEdit(){_navigateToEdit.value = id}
    fun onNavigatedToEdit(){this._navigateToEdit.value = null}

}