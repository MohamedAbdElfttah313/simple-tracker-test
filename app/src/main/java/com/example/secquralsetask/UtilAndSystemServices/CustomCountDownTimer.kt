package com.example.secquralsetask.UtilAndSystemServices

import android.os.CountDownTimer

abstract class CustomCountDownTimer(secs : Int):CountDownTimer(secs*1000L,1000) {
    override fun onTick(p0: Long) {

    }

}