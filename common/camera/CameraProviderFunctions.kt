package com.example.bamboogarden.common.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->
  ProcessCameraProvider.getInstance(this).also { future ->
    future.addListener({
      continuation.resume(future.get())
    }, executor)
  }
}

val Context.executor: Executor
  get() = ContextCompat.getMainExecutor(this)

fun Bitmap.rotateBitmap(rotationDegree: Int) : Bitmap {
  val matrix = Matrix().apply {
    postRotate(-rotationDegree.toFloat())
    postScale(-1f, -1f)
  }
  return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}