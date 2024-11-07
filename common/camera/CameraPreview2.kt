package com.example.bamboogarden.common.camera

import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch

@Composable
fun CameraPreview2(
  modifier: Modifier = Modifier,
  scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
  cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
  imageCapture: ImageCapture,
) {
  val TAG = "CameraPreview2"
  val coroutineScope = rememberCoroutineScope()
  val lifecycleOwner = LocalLifecycleOwner.current
  AndroidView(
    modifier = modifier,
    factory = { context ->
      val previewView = PreviewView(context).apply {
        this.scaleType = scaleType
        layoutParams = ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT
        )
      }

      // CameraX Preview UseCase
      val previewUseCase = Preview.Builder()
        .build()
        .also {
          it.setSurfaceProvider(previewView.surfaceProvider)
        }

      coroutineScope.launch {
        val cameraProvider = context.getCameraProvider()

        try {
          // Must unbind the use-cases before rebinding them.
          cameraProvider.unbindAll()
          cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector, previewUseCase, imageCapture
          )
        } catch (ex: Exception) {
          Log.e(TAG, "Use case binding failed", ex)
        }
      }

      previewView
    }
  )
}