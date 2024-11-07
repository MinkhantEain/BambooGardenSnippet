package com.example.bamboogarden.common.receiptPrinter

import android.graphics.Bitmap
import android.util.Log
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection
import com.dantsu.escposprinter.textparser.PrinterTextParserImg
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.common.bluetooth.BluetoothController
import com.example.bamboogarden.common.bluetooth.toBluetoothDeviceDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ThermalPrinter private constructor(
  private val bluetoothController: BluetoothController = BambooGardenApplication.appModule.bluetoothController
) {
  companion object {
    private var instance: ThermalPrinter? = null
    const val TAG = "ThermalPrinter"
    fun getInstance(): ThermalPrinter {
      if (instance == null) {
        instance = ThermalPrinter()
      }
      return instance!!
    }
  }

  private var bluetoothPrinter: EscPosPrinter? = null

  private suspend fun reconnectIfConnectionIsLost() {
    withContext(Dispatchers.IO) {
      bluetoothController.clientSocket.value?.let { socket ->
        if (!socket.isConnected) {
          try {
            bluetoothController.reconnectSocket()
          } catch (e: Exception) {
            Log.d(TAG, "reconnectIfConnectionIsLost: ${e.message}")
          }
        }
      } ?: bluetoothController.connectedDevice.value?.let { bluetoothDevice ->
        bluetoothController.selectDevice(bluetoothDevice.toBluetoothDeviceDomain())
      } ?: throw Exception("Bluetooth device is not connected")
    }
  }

  fun isConnectedToPrinterServiceSocket(): Boolean {
    return bluetoothPrinter != null || bluetoothController.connectedDevice.value != null
  }

  private suspend fun initializeBluetoothPrinter() {
    withContext(Dispatchers.IO) {
      try {
        if (bluetoothPrinter == null) {
          bluetoothController.connectedDevice.value?.let {
            bluetoothPrinter = EscPosPrinter(BluetoothConnection(it), 203, 70f, 48)
          }
        }
      } catch (e: Exception) {
        throw e
      }
    }
  }

  suspend fun printImageReceipt(bitmap: Bitmap) {
    return withContext(Dispatchers.IO) {
      try {
        initializeBluetoothPrinter()
        val width = bitmap.width
        val height = bitmap.height
        val textToPrint = StringBuilder()
        for (y in 0 until height step 256) {
          val tempBitmap =
            Bitmap.createBitmap(bitmap, 0, y, width, if (y + 256 > height) height - y else 256)
          textToPrint.append(
            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(
              bluetoothPrinter, tempBitmap
            ) + "</img>\n"
          )
        }
        textToPrint.append("[C]")
        bluetoothPrinter!!.printFormattedText(textToPrint.toString(), 10f)

      } catch (e: Exception) {
        throw e
      }
    }
  }
}