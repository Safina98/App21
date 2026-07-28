package com.example.app21try6.grafik.grafikmain


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// ... your other existing imports ...

class TabStateViewModel(/* your existing constructor params */) : ViewModel() {

    private val _activeGraphTab = MutableLiveData<Int>()
    val activeGraphTab: LiveData<Int> = _activeGraphTab

    fun setActiveGraphTab(position: Int) {
        _activeGraphTab.value = position
    }
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TabStateViewModel::class.java)) {
                return TabStateViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

}