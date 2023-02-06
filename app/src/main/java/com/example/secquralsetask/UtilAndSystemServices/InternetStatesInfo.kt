package com.example.secquralsetask.UtilAndSystemServices

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class InternetStatesInfo {
    private var connManager : ConnectivityManager? = null

    fun initConnectivityManager(context : Context) : InternetStatesInfo{
        connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return this
    }

    fun hasInternetAccess():Boolean{
        val activeNetwork = connManager?.activeNetwork ?:return false
        val cap = connManager?.getNetworkCapabilities(activeNetwork) ?:return false

       return arrayOf(
            NetworkCapabilities.NET_CAPABILITY_INTERNET,
            NetworkCapabilities.NET_CAPABILITY_VALIDATED,
            NetworkCapabilities.TRANSPORT_WIFI,
            NetworkCapabilities.TRANSPORT_CELLULAR
        ).map {
            cap.hasCapability(it)
        }.any {
            it
        }

    }
}