package com.example.secquralsetask.UtilAndSystemServices

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatteryStatesInfo {
    private var batteryStates : Intent? = null

    fun initBatteryStates(context : Context) : BatteryStatesInfo{
        batteryStates = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            context.registerReceiver(null , it)
        }
        return this
    }

    fun isCharging(): Boolean? {
       return batteryStates?.let {
            val state = it.getIntExtra(BatteryManager.EXTRA_STATUS,-1)
           (state == BatteryManager.BATTERY_STATUS_CHARGING || state == BatteryManager.BATTERY_STATUS_FULL)
        }
    }

    fun getChargedAmountPercentage() : Float?{
        return batteryStates?.let {
            val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL,-1)
            val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE,-1)
            level*100/scale.toFloat()
        }
    }
}