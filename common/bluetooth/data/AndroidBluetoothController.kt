package com.example.bamboogarden.common.bluetooth.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.ParcelUuid
import android.util.Log
import com.example.bamboogarden.common.bluetooth.BluetoothController
import com.example.bamboogarden.common.bluetooth.BluetoothDeviceDomain
import com.example.bamboogarden.common.bluetooth.ConnectionResult
import com.example.bamboogarden.common.bluetooth.toBluetoothDeviceDomain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission")
class AndroidBluetoothController(private val context: Context) : BluetoothController {
  val TAG = "AndroidBluetoothController"
  private val bluetoothManager by lazy { context.getSystemService(BluetoothManager::class.java) }
  private val bluetoothAdapter by lazy { bluetoothManager?.adapter }

  private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())

  private val _connectableDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
  override val connectableDevices: StateFlow<List<BluetoothDeviceDomain>> get() = _connectableDevices.asStateFlow()

  private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
  override val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
    get() = _pairedDevices.asStateFlow()

  override val isConnected: StateFlow<Boolean>
    get() = _isConnected.asStateFlow()

  override val errors: SharedFlow<String>
    get() = _errors.asSharedFlow()

  private val _errors = MutableSharedFlow<String>()

  private val _isConnected = MutableStateFlow(false)

  override val isBluetoothEnabled: Boolean
    get() = bluetoothAdapter?.isEnabled == true

  private val _connectedDevice = MutableStateFlow<BluetoothDevice?>(null)
  override val connectedDevice = _connectedDevice.asStateFlow()

  private val _clientSocket = MutableStateFlow<BluetoothSocket?>(null)
  override val clientSocket = _clientSocket.asStateFlow()

  private val foundDeviceReceiver = FoundDeviceReceiver { device ->
    _scannedDevices.update { devices ->
      Log.d(TAG, "devices: $devices")
      if (device in devices) {
        devices
      } else {
        devices + device
      }
    }

  }

  private val uuidReceiver = UuidReceiver { device ->
    _connectableDevices.update { devices ->
      Log.d(TAG, "devices: $devices")
      val newDevice = device.toBluetoothDeviceDomain()
      if (newDevice in devices) {
        devices
      } else {
        devices + newDevice
      }
    }

  }

  private val bluetoothStateReceiver = BluetoothStateReceiver { isConnected, bluetoothDevice ->
    if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true) {
      _isConnected.update { isConnected }
    } else {
      CoroutineScope(Dispatchers.IO).launch {
        _errors.emit("Cannot connect to a non paired device")
      }
    }
  }

  init {
    updatePairedDevices()
    context.registerReceiver(
      bluetoothStateReceiver,
      IntentFilter().apply {
        addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
      }
    )

    context.registerReceiver(
      uuidReceiver,
      IntentFilter(BluetoothDevice.ACTION_UUID)
    )
  }

  override fun startDiscovery() {
    if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
      return
    }

    context.registerReceiver(
      foundDeviceReceiver,
      IntentFilter(
        BluetoothDevice.ACTION_FOUND,
      )
    )

    updatePairedDevices()
    Log.d(TAG, "adapter enabled: ${bluetoothAdapter?.isEnabled}")

    val discoveryReturn = bluetoothAdapter?.startDiscovery()
    Log.d(TAG, "startDiscovery: $discoveryReturn")
  }

  override fun stopDiscovery() {
    if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) {
      return
    }
    bluetoothAdapter?.cancelDiscovery()

  }

  override fun release() {
    try {
      context.unregisterReceiver(foundDeviceReceiver)
    } catch (e: Exception) {
      Log.d(TAG, "release: ${e.message}")
    }
    try {
      context.unregisterReceiver(bluetoothStateReceiver)
    } catch (e: Exception) {
      Log.d(TAG, "release: ${e.message}")
    }
    try {
      context.unregisterReceiver(uuidReceiver)
    } catch (e: Exception) {
      Log.d(TAG, "release: ${e.message}")
    }

    bluetoothAdapter?.isDiscovering?.apply {
      if (this) bluetoothAdapter?.cancelDiscovery()
    }
  }

  override fun reconnectSocket() {
    _clientSocket.value?.let { socket ->
      try {
        socket.connect()
      } catch (e: Exception) {
        throw e
      }
    }
  }


  private fun updatePairedDevices() {
    if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
      Log.d(TAG, "updatePairedDevices: No permission")
      return
    }
    Log.d(TAG, "updatePairedDevices: has permission")
    bluetoothAdapter
      ?.bondedDevices
      ?.map { it.toBluetoothDeviceDomain() }
      ?.filter { it.uuids.contains(ParcelUuid.fromString("00001101-0000-1000-8000-00805f9b34fb")) }
      ?.also { devices -> _pairedDevices.update { devices } }
    Log.d(TAG, "updatePairedDevices: paired devices updated")
  }

  private fun hasPermission(permission: String): Boolean {
    return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
  }

  override fun closeConnection() {
    _clientSocket.value?.close()
    _clientSocket.update { null }
    _connectedDevice.update { null }
  }

  override fun selectDevice(device: BluetoothDeviceDomain): Flow<ConnectionResult> {
    return flow {
      if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) {
        throw SecurityException("No bluetooth connect permission")
      }
      stopDiscovery()
      val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(device.address)
      _connectedDevice.update { bluetoothDevice }
      emit(ConnectionResult.ConnectionEstablished)
    }
      .flowOn(Dispatchers.IO)
  }
}
