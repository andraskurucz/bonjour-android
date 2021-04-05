package com.akurucz.bonjour.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager

class WifiConnectivityTracker(context: Context, private val callback: Callback) {

    private val wifiManager by lazy { context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager }
    private val connectivityManager by lazy { context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    private val networkCallback = object :
        ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            val ssid = wifiManager.getCurrentSSID()
            callback.onConnected(ssid)
        }

        override fun onLost(network: Network) {
            callback.onLost()
        }
    }

    fun startTracking() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun stopTracking() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    interface Callback {

        fun onConnected(ssid: String)

        fun onLost()
    }
}

private fun WifiManager.getCurrentSSID(): String {
    val ssid = connectionInfo.ssid
    return if (ssid.matches(Regex("^\".*\"\$"))) ssid.substring(1..ssid.length - 2) else ssid
}