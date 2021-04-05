package com.akurucz.bonjour.network

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import java.net.Inet4Address
import java.net.InetAddress
import java.util.*
import java.util.concurrent.Executors
import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import javax.jmdns.JmDNS
import javax.jmdns.NetworkTopologyDiscovery
import javax.jmdns.ServiceEvent
import javax.jmdns.ServiceListener
import kotlin.collections.HashMap

private const val SERVICE_TYPE = "_http._tcp.local."
private const val TAG = "BonjourServiceTracker"

class JmDNSBonjourServiceTracker(
    context: Context
) :
    BonjourServiceTracker {

    private val _discoveredServices: MutableLiveData<List<BonjourService>> = MutableLiveData()
    override val discoveredServices: LiveData<List<BonjourService>> = _discoveredServices

    private val discoveredMoneyBoxUrls: MutableMap<String, String> =
        Collections.synchronizedMap(HashMap<String, String>())

    private val jmDNSDispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()

    private val ongoingResolutions = Collections.synchronizedSet(HashSet<String>())

    private val wifiManager by lazy { context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager }

    private val multicastLock: WifiManager.MulticastLock by lazy {
        wifiManager.createMulticastLock(javaClass.name).also { it.setReferenceCounted(true) }
    }

    private var jmdnsManager: JmDNS? = null

    private val serviceListener = object : ServiceListener {

        override fun serviceAdded(event: ServiceEvent?) {
            event ?: return

            Log.d(TAG, "Service added ${event.name}")
            ongoingResolutions.add(event.name)
            jmdnsManager?.requestServiceInfo(event.type, event.name)
        }

        override fun serviceRemoved(event: ServiceEvent?) {
            event ?: return

            val name = event.name

            discoveredMoneyBoxUrls.remove(name)?.let { url ->
                Log.d(TAG, "Service removed $name $url")
                _discoveredServices.postValue(discoveredMoneyBoxUrls.map {
                    BonjourService(
                        name = it.key,
                        address = it.value
                    )
                })
            }
        }

        override fun serviceResolved(event: ServiceEvent?) {
            event ?: return

            val name = event.name

            if (ongoingResolutions.remove(name)) {
                val url = event.resolveUrl()
                Log.d(TAG, "Service resolved $name $url")
                discoveredMoneyBoxUrls[name] = url
                _discoveredServices.postValue(discoveredMoneyBoxUrls.map {
                    BonjourService(
                        name = it.key,
                        address = it.value
                    )
                })
            }
        }
    }

    override fun startTracking() {
        GlobalScope.launch { startDiscovery() }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun startDiscovery() = withContext(jmDNSDispatcher) {
        Log.d(TAG, "startDiscovery")
        val address = getLocalHostAddress() ?: return@withContext
        multicastLock.acquire()

        jmdnsManager = JmDNS.create(address)

        val logger = Logger.getLogger(JmDNS::class.java.name).apply { level = Level.FINER }
        val handler = ConsoleHandler().apply { level = Level.FINER }
        logger.addHandler(handler)

        jmdnsManager?.addServiceListener(SERVICE_TYPE, serviceListener)
    }

    override fun stopTracking() {
        GlobalScope.launch { stopDiscovery() }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun stopDiscovery() = withContext(jmDNSDispatcher) {
        jmdnsManager?.apply {
            removeServiceListener(SERVICE_TYPE, serviceListener)
            close()
        }

        jmdnsManager = null

        multicastLock.releaseSafely()
    }

    private fun getLocalHostAddress(): InetAddress? =
        try {
            InetAddress.getLocalHost().takeUnless { it.isLoopbackAddress }
                ?: NetworkTopologyDiscovery.Factory.getInstance().inetAddresses.firstOrNull { it is Inet4Address }
        } catch (e: Exception) {
            Log.w(TAG, "Could not resolve address", e)
            null
        }
}

private fun WifiManager.MulticastLock.releaseSafely() {
    try {
        if (isHeld) release()
    } catch (e: Exception) {
        Log.w(TAG, "couldn't release multicast lock $e")
    }
}

private fun ServiceEvent.resolveUrl() = "https:/${info.address}:${info.port}"