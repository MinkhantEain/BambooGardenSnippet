package com.example.bamboogarden

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.bamboogarden.navigations.AuthenticationNavigation
import com.example.bamboogarden.ui.theme.BambooGardenTheme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
  val TAG = "MainActivity"
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    FirebaseApp.initializeApp(this)
    if (!hasRequiredPermissions()) {
      permissionsRequestLauncher.launch(PERMISSIONS)
    }
    enableEdgeToEdge()
    setContent { BambooGardenTheme { AuthenticationNavigation() } }
  }

  companion object {
    private val PERMISSIONS =
      mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,

      ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          this.addAll(
            listOf(
              Manifest.permission.BLUETOOTH_CONNECT,
              Manifest.permission.BLUETOOTH_SCAN,
            )
          )
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          this.addAll(
            listOf(
              Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
          )
        }
      }.toTypedArray()
  }

  private val enableBluetoothLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

  private val permissionsRequestLauncher =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
      permissions.forEach { (permission, isGranted) ->
        when (permission) {
          Manifest.permission.BLUETOOTH_CONNECT -> {
            val bluetoothController =
              BambooGardenApplication.appModule.bluetoothController
            if (isGranted && !bluetoothController.isBluetoothEnabled) {
              enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
          }
        }
      }
    }

  private fun hasRequiredPermissions(): Boolean {
    return PERMISSIONS.all {
      ContextCompat.checkSelfPermission(this, it) ==
        PackageManager.PERMISSION_GRANTED
    }
  }

}
