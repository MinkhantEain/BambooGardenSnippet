package com.example.bamboogarden.common.camera

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.common.LoadingOverlay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
  popCallBack: () -> Unit,
) {
  val TAG = "CameraScreen"
  val scaffoldState = rememberBottomSheetScaffoldState()

  val controller = remember {
    LifecycleCameraController(BambooGardenApplication.instance).apply {
      setEnabledUseCases(CameraController.IMAGE_CAPTURE)
      cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    }
  }

  val viewModel: CameraScreenViewModel = viewModel()
  val bitmaps by viewModel.bitmaps.collectAsState()
  val isLoading by remember { viewModel.isLoading }

  val imageCapture = remember {
    ImageCapture.Builder()
      .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build()
  }

  if (isLoading) {
    LoadingOverlay()
  }

  BottomSheetScaffold(
    scaffoldState = scaffoldState,
    sheetPeekHeight = 0.dp,
    sheetContent = {
      PhotoBottomSheetContent(
        bitmaps = bitmaps,
        modifier = Modifier.fillMaxWidth(),
      )
    },
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(bottom = 50.dp)
    ) {
      CameraPreview2(
        imageCapture = imageCapture
      )
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        IconButton(
          onClick = {
//            viewModel.takePhoto(
//              controller,
//              popCallBack = popCallBack
//            )
            viewModel.takePhoto2(
              popCallBack = popCallBack,
              imageCapture = imageCapture,
            )
          },
          modifier = Modifier.size(50.dp)
        ) {
          Icon(
            imageVector = Icons.Filled.Camera,
            contentDescription = "Take Photo",
            modifier = Modifier.fillMaxSize(),
            tint = Color.White
          )
        }
      }
    }
  }
}
