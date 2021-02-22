package com.akurucz.bonjour

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DiscoveryViewModel : ViewModel() {

    val discoveredServices: LiveData<List<BonjourService>> =
        MutableLiveData(listOf(BonjourService("first", "192.168.2.1")))
}