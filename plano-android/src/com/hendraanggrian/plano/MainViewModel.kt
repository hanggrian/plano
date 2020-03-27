package com.hendraanggrian.plano

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val fillData = MutableLiveData(false)
    val thickData = MutableLiveData(false)
    val emptyData = MutableLiveData(true)
}
