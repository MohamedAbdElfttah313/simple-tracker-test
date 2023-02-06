package com.example.secquralsetask

import android.net.Uri

class CapturedData constructor() {

    var isCharging: Boolean? = null
    var BatteryPercentage: Float? = null
    var hasInternetAccess: Boolean? = null
    var timestamp: String? = null
    var pictureUri: Uri? = null
    var lat : Double? = null
    var lon : Double? = null

    constructor(isCharging: Boolean, BatteryPercentage: Float, hasInternetAccess: Boolean, timestamp: String,
        pictureUri: Uri, lat : Double, lon : Double
    ) : this() {
        this.isCharging = isCharging
        this.BatteryPercentage = BatteryPercentage
        this.hasInternetAccess = hasInternetAccess
        this.timestamp = timestamp
        this.pictureUri = pictureUri
        this.lat = lat
        this.lon = lon
    }

    fun toHashMap(): HashMap<String, String?> {
        return hashMapOf(
            "isCharging" to isCharging.toString(),
            "BatteryPercentage" to BatteryPercentage.toString(),
            "hasInternetAccess" to hasInternetAccess.toString(),
            "timestamp" to timestamp,
            "pictureUri" to pictureUri.toString(),
            "lat" to lat.toString(),
            "lon" to lon.toString()
            )
    }

}