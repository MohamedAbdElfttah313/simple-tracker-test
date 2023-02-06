package com.example.secquralsetask

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.secquralsetask.UtilAndSystemServices.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    lateinit var alarmManager: AlarmManager
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissions()
        alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        sharedPreferences = getSharedPreferences("CAPTURE_INTERVAL", MODE_PRIVATE)
        if (sharedPreferences.contains("interval").not()){
            sharedPreferences.edit().putInt("interval",15).apply()
        }

        IntervalEdittext.setText(sharedPreferences.getInt("interval",0).toString())

        CapturedDataService.latestCapturedData.observe(this){
            PictureImageView.setImageURI(it.pictureUri)
            TimeTextView.text = it.timestamp
            BatteryChargingView.text = it.isCharging.toString()
            BatteryPercentageView.text = it.BatteryPercentage.toString()
            InternetStateView.text = it.hasInternetAccess.toString()
            LocationView.text = "Lat:${it.lat}\nLon:${it.lon}"
        }

    }

    private fun checkPermissions(){
        if (ContextCompat.checkSelfPermission(this , Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this , Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this ,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.CAMERA ,
                Manifest.permission.ACCESS_COARSE_LOCATION , Manifest.permission.ACCESS_FINE_LOCATION),
                9090
            )
        }
    }

    fun start(view: View) {
        val interval = sharedPreferences.getInt("interval",15).toLong()
        val servicePendingIntent = Intent(applicationContext ,CapturedDataService::class.java).run {
            PendingIntent.getService(applicationContext , 4444,this,0 or PendingIntent.FLAG_IMMUTABLE)
        }
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP , SystemClock.elapsedRealtime() ,1000*60*interval ,servicePendingIntent)

    }
    fun setNewInterval(view: View) {
        if (IntervalEdittext.text.toString().isNotBlank() && IntervalEdittext.text.toString().toInt()>=3){
            val newInterval = IntervalEdittext.text.toString().toInt()
            sharedPreferences.edit().putInt("interval",newInterval).apply()
            val currentPendingIntent = Intent(applicationContext ,CapturedDataService::class.java).run {
                PendingIntent.getService(applicationContext , 4444,this,0 or PendingIntent.FLAG_IMMUTABLE)
            }
            alarmManager.cancel(currentPendingIntent)
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP , SystemClock.elapsedRealtime() ,1000*60*newInterval.toLong(),currentPendingIntent)
        }else{
            Toast.makeText(this , "Interval must be greater than 3",Toast.LENGTH_SHORT).show()
        }


    }

    private fun startServiceOneTime(){
        Intent(applicationContext , CapturedDataService::class.java).also {
            startService(it)
        }
    }

    fun capture(view: View) {
        startServiceOneTime()
    }


}