package com.hendraanggrian.plano

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val validData = MutableLiveData(false)
    val emptyData = MutableLiveData(true)
}