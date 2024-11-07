package com.example.bamboogarden.authentication

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.google.android.gms.common.api.Response
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthenticationViewModel : ViewModel() {
  private val auth = AuthServiceImpl()
  private val _state = mutableStateOf(AuthenticationState(
    isLoading = false,
    authResult = null,
    error = null,
  ))
  val state: State<AuthenticationState> get() = _state

  suspend fun getCurrentUser() {
    return auth.currentUser.collect(
      collector = FlowCollector {
        it
      }
    )
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  fun login(email: String, password: String) {
    _state.value = _state.value.copy(isLoading = true)
    val result = viewModelScope.async(Dispatchers.IO) {
      auth.signIn(email = email, password = password)
    }

    result.invokeOnCompletion {
      result.getCompleted().onSuccess {
        _state.value = _state.value.copy(
          isLoading = false,
          authResult = it
        )
      }
        .onFailure {
          _state.value = _state.value.copy(
            isLoading = false,
            error = it.message,
            authResult = null,
          )
        }
    }
  }

  fun logout() {
    viewModelScope.launch {
      auth.signOut()
    }
  }

  fun acknowledgeAuthError() {
    _state.value = _state.value.copy(error = null)
  }
}