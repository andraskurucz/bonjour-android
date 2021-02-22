package com.akurucz.bonjour

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DiscoveryViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DiscoveryViewModel() as T
    }
}