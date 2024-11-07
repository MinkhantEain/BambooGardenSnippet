package com.example.bamboogarden.common.bluetooth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.BambooGardenApplication
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class BluetoothViewModel(
    private val bluetoothController: BluetoothController = BambooGardenApplication.appModule.bluetoothController,
) : ViewModel() {
    val TAG = "BluetoothViewModel"
    val isBluetoothEnabled: Boolean
        get() = bluetoothController.isBluetoothEnabled

    private val _state = MutableStateFlow(BluetoothUiState())
    val state =
        combine(bluetoothController.connectableDevices, bluetoothController.pairedDevices, _state) {
                scannedDevices,
                pairedDevices,
                state ->
                state.copy(
                    scannedDevices = scannedDevices,
                    pairedDevices = pairedDevices,
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                _state.value,
            )

    private var deviceConnectionJob: Job? = null

    init {
        bluetoothController.isConnected
            .onEach { isConnected -> _state.update { it.copy(isConnected = isConnected) } }
            .launchIn(viewModelScope)

        bluetoothController.errors
            .onEach { error -> _state.update { it.copy(error = error) } }
            .launchIn(viewModelScope)
    }

    fun getConnectedDevice() : BluetoothDevice? {
        return bluetoothController.connectedDevice.value?.toBluetoothDeviceDomain()
    }

    fun startScan() {
        bluetoothController.startDiscovery()
    }

    fun stopScan() {
        bluetoothController.stopDiscovery()
    }

    fun connectToDevice(device: BluetoothDeviceDomain) {
        _state.update { it.copy(isConnecting = true) }
        deviceConnectionJob = bluetoothController.selectDevice(device).listen()
    }

    fun disconnectFromDevice() {
        deviceConnectionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update { it.copy(isConnecting = false, isConnected = false) }
    }


    override fun onCleared() {
        bluetoothController.release()
        super.onCleared()
    }

    private fun Flow<ConnectionResult>.listen(): Job {
        return onEach { result ->
                when (result) {
                    ConnectionResult.ConnectionEstablished -> {
                        _state.update {
                            it.copy(
                                isConnecting = false,
                                isConnected = true,
                                error = null,
                            )
                        }
                    }
                    is ConnectionResult.Error -> {
                        _state.update {
                            it.copy(
                                isConnecting = false,
                                isConnected = false,
                                error = result.message,
                            )
                        }
                    }
                }
            }
            .catch { throwable ->
                Log.d(TAG, "listen: $throwable")
                bluetoothController.closeConnection()
                _state.update { it.copy(isConnected = false, isConnecting = false) }
            }
            .launchIn(viewModelScope)
    }
}
