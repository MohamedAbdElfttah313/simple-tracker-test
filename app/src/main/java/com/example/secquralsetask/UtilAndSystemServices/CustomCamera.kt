package com.example.secquralsetask.UtilAndSystemServices

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

class CustomCamera {

    fun capturePicture(context : Context,lifecycleOwner: LifecycleOwner , callBack : ImageCapture.OnImageSavedCallback) {
        val processCameraProvider = ProcessCameraProvider.getInstance(context)

        processCameraProvider.addListener({

            val provider = processCameraProvider.get()
            val outputFilesOptions = getOutPutFileOptions(context)
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val imageCapture = ImageCapture.Builder().build()

            provider.unbindAll()
            provider.bindToLifecycle(lifecycleOwner , cameraSelector , imageCapture)

            imageCapture.takePicture(outputFilesOptions , ContextCompat.getMainExecutor(context),callBack)
        }, ContextCompat.getMainExecutor(context))
    }

    private fun getContentValues(name : String = System.currentTimeMillis().toString()) : ContentValues{
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME , name)
            put(MediaStore.MediaColumns.MIME_TYPE , "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH , "Pictures/Captured-Images")
            }
        }
    }

    private fun getOutPutFileOptions(context : Context) : ImageCapture.OutputFileOptions{
        return ImageCapture.OutputFileOptions.Builder(context.contentResolver ,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,getContentValues())
            .build()
    }
}