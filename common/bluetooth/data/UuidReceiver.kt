package com.example.bamboogarden.common.bluetooth.data

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import java.util.UUID

class UuidReceiver(private val onDeviceFound: (BluetoothDevice) -> Unit) : BroadcastReceiver() {

  val TAG = "UuidReceiver"

  override fun onReceive(context: Context?, intent: Intent?) {
    val sspUuid = "00001101-0000-1000-8000-00805f9b34fb"
    val action = intent?.action
    if (BluetoothDevice.ACTION_UUID == action) {
      val device =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          intent.getParcelableExtra(
            BluetoothDevice.EXTRA_DEVICE,
            BluetoothDevice::class.java
          )
        } else {
          intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }

      val uuids = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID, ParcelUuid::class.java)
      } else {
        intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID) as Array<ParcelUuid>?
      }
      if (device != null && uuids != null) {
        for (uuid in uuids) {
          if (uuid.uuid == UUID.fromString(sspUuid)) {
            onDeviceFound(device)
            break
          }
        }
      }
      Log.d(TAG, "onReceive: $device")
    }
  }
}