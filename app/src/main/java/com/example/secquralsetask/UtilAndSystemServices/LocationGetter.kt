package com.example.secquralsetask.UtilAndSystemServices

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

class LocationGetter : LocationListener {

    private var locationManager : LocationManager? = null
    var lat : Double? = 0.0
    var lon : Double? = 0.0
    private val REFRESH_TIMER : Long = 2000
    private val REFRESH_DISTANCE : Float = 1f

    fun initLocationManager(context : Context) : LocationGetter{
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return this
    }

    @SuppressLint("MissingPermission")
    fun updateCurrentLocation(){
        if (isLocationProvidersEnabled()!!){
            locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER , REFRESH_TIMER , REFRESH_DISTANCE , this)
            locationManager!!.requestLocationUpdates(LocationManager.NETWORK_PROVIDER , REFRESH_TIMER , REFRESH_DISTANCE , this)
        }
    }

      override fun onLocationChanged(p0: Location) {
        lat = p0.latitude
        lon = p0.longitude
        locationManager!!.removeUpdates(this)
    }


     fun isLocationProvidersEnabled() : Boolean?{
        return locationManager?.let {
            it.isProviderEnabled(LocationManager.NETWORK_PROVIDER) && it.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

    }

    companion object{

    }
}