package com.example.bamboogarden.common.bluetooth

data class BluetoothUiState(
  val scannedDevices: List<BluetoothDevice> = emptyList(),
  val pairedDevices: List<BluetoothDevice> = emptyList(),
  val isConnected: Boolean = false,
  val isConnecting: Boolean = false,
  val error: String? = null,
)
