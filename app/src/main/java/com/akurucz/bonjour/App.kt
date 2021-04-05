package com.akurucz.bonjour

import android.app.Application
import com.akurucz.bonjour.network.BonjourServiceTracker
import com.akurucz.bonjour.network.JmDNSBonjourServiceTracker
import com.akurucz.bonjour.network.WifiConnectivityTracker

class App : Application() {

    lateinit var bonjourServiceTracker: BonjourServiceTracker
        private set

    lateinit var connectivityTracker: WifiConnectivityTracker
        private set

    override fun onCreate() {
        super.onCreate()

        bonjourServiceTracker = JmDNSBonjourServiceTracker(this)
        connectivityTracker =
            WifiConnectivityTracker(this, object : WifiConnectivityTracker.Callback {
                override fun onConnected(ssid: String) {
                    bonjourServiceTracker.startTracking()
                }

                override fun onLost() {
                    bonjourServiceTracker.stopTracking()
                }

            })
        connectivityTracker.startTracking()
    }

    override fun onTerminate() {
        super.onTerminate()
        connectivityTracker.stopTracking()
    }
}