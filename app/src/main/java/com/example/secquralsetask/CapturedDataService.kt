package com.example.secquralsetask

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.ConnectivityManager
import android.net.Uri
import android.os.IBinder
import android.os.SystemClock
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.secquralsetask.UtilAndSystemServices.*
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream

class CapturedDataService : LifecycleService(){

    override fun onCreate() {
        super.onCreate()
        val notification = NotificationCompat.Builder(this , "CHANNEL_1")
            .setContentTitle("Data Upload")
            .setContentText("Sending previously known data to server")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(1212,notification)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val locationGetter = LocationGetter().initLocationManager(this)

        if (locationGetter.isLocationProvidersEnabled()!!){
            locationGetter.updateCurrentLocation()

            val hasInternetAccess = InternetStatesInfo().initConnectivityManager(this).hasInternetAccess()
            val (batteryIsCharging,batteryPercentage) = BatteryStatesInfo().initBatteryStates(this)
                .run { Pair(isCharging(),getChargedAmountPercentage()) }
            val timestamp = TimeGetter().getFormattedTimeStamp()
            latestCaptureDay = TimeGetter().getFormattedDay()

            CustomCamera().capturePicture(this , this,object :ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                    object : CustomCountDownTimer(5){
                        override fun onFinish() {
                            val (lat,lon) = Pair(locationGetter.lat,locationGetter.lon)
                            val capturedData = CapturedData(batteryIsCharging?:false,batteryPercentage?:0f,
                                hasInternetAccess,timestamp,outputFileResults.savedUri!!,lat!! , lon!!)

                            latestCapturedData.value = capturedData
                            sendDataToServer(capturedData)
                        }
                    }.start()
                }

                override fun onError(exception: ImageCaptureException) {
                    println("Didn't work due to ${exception.message}")
                    if (failedAttempts!=3){
                        onStartCommand(intent,flags,startId)
                    }else{
                        failedAttempts = 0
                    }
                }
            })
        }else{
            Toast.makeText(this , "GPS is not enabled",Toast.LENGTH_SHORT).show()
        }



        return START_STICKY
    }

    private fun sendDataToServer(data : CapturedData){
        if (failedAttempts==3){
            failedAttempts = 0
            return
        }
        val hasInternetAccess = InternetStatesInfo().initConnectivityManager(this).hasInternetAccess()
        if (hasInternetAccess){

            val firebaseStorage = FirebaseStorage.getInstance()
            val firebaseDatabase = FirebaseDatabase.getInstance()

            val picName = System.currentTimeMillis().toString()+".${getMimeType(data.pictureUri!!)}"
            val newRef = firebaseStorage.getReference(latestCaptureDay).child(picName)

            val source = ImageDecoder.createSource(contentResolver , data.pictureUri!!)
            val asBitmap = ImageDecoder.decodeBitmap(source)

            val reducedImage = ByteArrayOutputStream().run {
                asBitmap.compress(Bitmap.CompressFormat.JPEG,50,this)
                toByteArray()
            }

            println("before uploading")

            newRef.putBytes(reducedImage).addOnSuccessListener { snapshot->
                snapshot.storage.downloadUrl.addOnSuccessListener {downloadUri->
                    println(downloadUri.toString())
                    data.pictureUri = downloadUri
                    firebaseDatabase.getReference(latestCaptureDay).child(data.timestamp!!).setValue(data.toHashMap())
                }
            }.addOnFailureListener{
                failedAttempts++
                sendDataToServer(data)
            }

        }else{
            failedAttempts++
            val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            registerReceiver(connectivityBroadCast , filter)
        }
    }

    private val connectivityBroadCast = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            p1?.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false).also {
                if (it!!.not()){
                    sendDataToServer(latestCapturedData.value!!)
                    this@CapturedDataService.unregisterReceiver(this)
                }
            }
        }
    }

    private fun getMimeType(uri : Uri) = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri))


    companion object{
        var latestCapturedData = MutableLiveData<CapturedData>()
        private var latestCaptureDay : String = ""
        private var failedAttempts = 0

    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

}