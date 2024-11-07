package com.example.bamboogarden.common.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.BambooGardenApplication
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BluetoothSelectionScreen(modifier: Modifier = Modifier, onBackClick: () -> Unit) {
  val TAG = "BluetoothSelectionScreen"
  val viewModel: BluetoothViewModel = viewModel()
  val state by viewModel.state.collectAsState()
  val enableBluetoothLauncher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.StartActivityForResult()
    ) {}
  val permissionLauncher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { perms ->
      val canEnableBluetooth =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          perms[Manifest.permission.BLUETOOTH_CONNECT] == true
        } else true

      if (canEnableBluetooth && !viewModel.isBluetoothEnabled) {
        enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
      }
      Log.d(
        TAG,
        "canEnableBluetooth: $canEnableBluetooth, bluetoothEnabled: ${viewModel.isBluetoothEnabled}"
      )
    }

  LaunchedEffect(key1 = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      permissionLauncher.launch(
        arrayOf(
          Manifest.permission.BLUETOOTH_CONNECT,
          Manifest.permission.BLUETOOTH_SCAN,
          Manifest.permission.ACCESS_BACKGROUND_LOCATION,
          Manifest.permission.BLUETOOTH_ADMIN,
        )
      )
    }
  }

  LaunchedEffect(key1 = state.error) {
    state.error?.let { message ->
      Toast.makeText(BambooGardenApplication.instance, message, Toast.LENGTH_LONG).show()
    }
  }

  Surface(color = MaterialTheme.colorScheme.background) {
    when {
      state.isConnecting -> {
        Column(
          modifier = modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {

          CircularProgressIndicator()
          Text(text = "Connecting...")
        }
      }

      else -> {
        DeviceScreen(
          state = state,
          onStartScan = viewModel::startScan,
          onStopScan = viewModel::stopScan,
          onDeviceClick = viewModel::connectToDevice,
          onDisconnectClick = viewModel::disconnectFromDevice,
          onBackClick = onBackClick,
          connectedDevice = viewModel.getConnectedDevice()
        )
      }
    }
  }
}
