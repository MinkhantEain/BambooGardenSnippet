package com.example.bamboogarden.common.bluetooth

import android.os.ParcelUuid


typealias BluetoothDeviceDomain = BluetoothDevice
data class BluetoothDevice(
  val name: String?,
  val address: String,
  val uuids: Array<ParcelUuid>
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as BluetoothDevice

    if (name != other.name) return false
    if (address != other.address) return false
    if (!uuids.contentEquals(other.uuids)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = name?.hashCode() ?: 0
    result = 31 * result + address.hashCode()
    result = 31 * result + uuids.contentHashCode()
    return result
  }
}