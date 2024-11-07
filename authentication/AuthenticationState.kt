package com.example.bamboogarden.authentication

import com.google.firebase.auth.AuthResult

data class AuthenticationState(
  val isLoading: Boolean = false,
  val authResult: AuthResult?,
  val error: String?,
)
