package com.example.app21try6.utils

import androidx.lifecycle.LiveData

/**
 * A LiveData that doesn't emit any values (used as null placeholder)
 */
class AbsentLiveData<T : Any?> private constructor() : LiveData<T>() {
    init {
        postValue(null)
    }

    companion object {
        fun <T> create(): LiveData<T> {
            return AbsentLiveData()
        }
    }
}