package com.akurucz.bonjour.network

import androidx.lifecycle.LiveData

interface BonjourServiceTracker {

    val discoveredServices: LiveData<List<BonjourService>>

    fun startTracking()

    fun stopTracking()
}