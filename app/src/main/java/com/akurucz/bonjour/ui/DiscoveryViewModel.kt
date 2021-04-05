package com.akurucz.bonjour.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akurucz.bonjour.network.BonjourService
import com.akurucz.bonjour.network.BonjourServiceTracker

class DiscoveryViewModel(private val bonjourServiceTracker: BonjourServiceTracker) : ViewModel() {

    val discoveredServices: LiveData<List<BonjourService>> =
        bonjourServiceTracker.discoveredServices

    class Factory(private val bonjourServiceTracker: BonjourServiceTracker) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return DiscoveryViewModel(bonjourServiceTracker) as T
        }
    }
}