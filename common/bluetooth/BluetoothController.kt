package com.example.bamboogarden.common.bluetooth

import android.bluetooth.BluetoothSocket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface BluetoothController {
  val connectableDevices: StateFlow<List<BluetoothDevice>>
  val pairedDevices: StateFlow<List<BluetoothDevice>>
  val isConnected: StateFlow<Boolean>
  val errors: SharedFlow<String>
  val isBluetoothEnabled: Boolean
  val connectedDevice: StateFlow<android.bluetooth.BluetoothDevice?>
  val clientSocket: StateFlow<BluetoothSocket?>

  fun startDiscovery()
  fun stopDiscovery()
  fun release()
  fun reconnectSocket()

//  fun startBluetoothServer(): Flow<ConnectionResult>

  fun selectDevice(device: BluetoothDevice) : Flow<ConnectionResult>

  fun closeConnection()
}
