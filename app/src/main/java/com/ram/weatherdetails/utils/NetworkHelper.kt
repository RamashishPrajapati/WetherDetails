package com.ram.weatherdetails.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build

/*To check the internet connection in the device*/
object NetworkHelper {
    fun isNetworkConnected(context: Context): Boolean {
        var result = false
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                result = checkNetworkConnection(this, this.activeNetwork)
            } else {
                val networks = this.allNetworks
                for (network in networks) {
                    if (checkNetworkConnection(this, network)) {
                        result = true
                    }
                }
            }
        }
        return result
    }


    private fun checkNetworkConnection(
        connectivityManager: ConnectivityManager,
        activeNetwork: Network?
    ): Boolean {
        connectivityManager.getNetworkCapabilities(activeNetwork)?.also {
            if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                it.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED) &&
                it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            )
                return true
        }
        return false
    }
}