package com.example.bamboogarden

import android.bluetooth.BluetoothManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.authentication.AuthServiceImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async

class HomeScreenViewModel : ViewModel() {
  private val auth = AuthServiceImpl()

  fun isBluetoothEnabled(): Boolean {
    return BambooGardenApplication.instance.getSystemService(BluetoothManager::class.java).adapter.isEnabled

  }
  @OptIn(ExperimentalCoroutinesApi::class)
  fun signOut() {
    val result = viewModelScope.async {
      auth.signOut()
    }
    result.invokeOnCompletion { it ->
      if (it == null) {
        result.getCompleted().onSuccess {
          Log.d("Auth", "signOut successful")
        }
          .onFailure {e ->
            Log.d("Auth", "signOut failed: ${e.message}")
          }
      }
    }
  }


}