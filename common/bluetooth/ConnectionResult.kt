package com.example.bamboogarden.common.bluetooth

sealed interface ConnectionResult {
  data object ConnectionEstablished: ConnectionResult
  data class Error(val message: String) : ConnectionResult
}
