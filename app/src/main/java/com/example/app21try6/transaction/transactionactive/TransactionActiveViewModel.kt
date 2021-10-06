package com.example.app21try6.transaction.transactionactive

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.app21try6.database.TransDetailDao
import com.example.app21try6.database.TransSumDao
import kotlinx.coroutines.launch

class TransactionActiveViewModel(
    application: Application,
    val datasource1: TransSumDao,
    val datasource2: TransDetailDao
):AndroidViewModel(application){
    private val _navigateToTransEdit = MutableLiveData<Int>()
    val navigateToTransEdit: LiveData<Int>
        get() = _navigateToTransEdit
    private val _navigateToTransDetail = MutableLiveData<Int>()
    val navigateToTransDetail: LiveData<Int>
        get() = _navigateToTransDetail
    var active_trans = datasource1.getActiveSum()

    init {
        viewModelScope.launch {
            active_trans = datasource1.getActiveSum()
        }
    }


    fun onNavigatetoTransEdit(id:Int){
        _navigateToTransEdit.value=id
    }
    fun onNavigatedToTransEdit(){
        this._navigateToTransEdit.value=null
    }
    fun onNavigatetoTransDetail(id:Int){
        _navigateToTransDetail.value = id
    }
    fun onNavigatedToTransDetail(){
        this._navigateToTransDetail.value = null
    }


}

