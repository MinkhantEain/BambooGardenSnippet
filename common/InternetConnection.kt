package com.example.bamboogarden.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.example.bamboogarden.BambooGardenApplication

class InternetConnection {
  companion object {
    fun hasNetworkConnection(): Boolean {
      val connectivityManager =
        BambooGardenApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.let { capabilities ->
          return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            else -> false
          }
        }
      } else {
        return connectivityManager.activeNetworkInfo?.isConnected ?: false
      }
      return false
    }
  }
}