package com.example.bamboogarden.common.camera

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.common.camera.data.CameraDatabase
import com.example.bamboogarden.common.camera.data.PhotoPath
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

class CameraScreenViewModel(
  private val cameraDatabase: CameraDatabase = BambooGardenApplication.appModule.cameraDatabase,
) : ViewModel() {
  val TAG = "CameraScreenViewModel"
  private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
  val bitmaps = _bitmaps.asStateFlow()

  private val _isLoading = mutableStateOf(false)
  val isLoading: State<Boolean> = _isLoading

  fun takePhoto(
    controller: LifecycleCameraController,
    popCallBack: () -> Unit,
  ) {
    _isLoading.value = true
    val outputFileOption = ImageCapture.OutputFileOptions.Builder(
      File(
        BambooGardenApplication.instance.cacheDir,
        LocalDateTime.now().toString()
      ),
    ).build()
    controller
      .takePicture(
        ContextCompat.getMainExecutor(BambooGardenApplication.instance),
        object : ImageCapture.OnImageCapturedCallback() {
          override fun onCaptureSuccess(image: ImageProxy) {
            super.onCaptureSuccess(image)
            viewModelScope.launch(Dispatchers.IO) {
              val file =
                File(BambooGardenApplication.instance.cacheDir, LocalDateTime.now().toString())
              FileOutputStream(file).use { fos ->
                image.toBitmap().compress(Bitmap.CompressFormat.PNG, 60, fos)
              }
              cameraDatabase.cameraCache.cacheImageBitmap(photoPath = PhotoPath(filePath = file.absolutePath))
            }.invokeOnCompletion {

              Log.d(TAG, "Photo taken")
              viewModelScope.launch(Dispatchers.Main) {
                popCallBack()
              }
              _isLoading.value = false
            }
          }

          override fun onError(exception: ImageCaptureException) {
            _isLoading.value = false
            super.onError(exception)
            Log.d(TAG, "Couldn't take photo: $exception")
          }
        }
      )
  }

  fun takePhoto2(
    popCallBack: () -> Unit,
    imageCapture: ImageCapture
  ) {
    _isLoading.value = true
    val outputFileOption = ImageCapture.OutputFileOptions.Builder(
      File(
        BambooGardenApplication.instance.cacheDir,
        "temp"
      ),
    ).build()
    imageCapture
      .takePicture(
        outputFileOption,
        BambooGardenApplication.instance.executor,
        object : ImageCapture.OnImageSavedCallback {
          override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
            viewModelScope.launch(Dispatchers.IO) {
              cameraDatabase.cameraCache.cacheImageBitmap(photoPath = PhotoPath(filePath = outputFileResults.savedUri!!.path!!))
            }.invokeOnCompletion {
              _isLoading.value = false
              viewModelScope.launch(Dispatchers.Main) {
                popCallBack()
              }
            }
          }

          override fun onError(exception: ImageCaptureException) {
            Log.d(TAG, "Couldn't take photo: $exception")
            _isLoading.value = false
          }
        }
      )
  }
}
