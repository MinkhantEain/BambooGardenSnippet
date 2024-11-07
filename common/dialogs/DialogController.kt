package com.example.bamboogarden.common.dialogs

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

open class DialogController {
  private val _isVisible = mutableStateOf(false)
  val isVisible: State<Boolean> = _isVisible

  fun show() {
    _isVisible.value = true
  }

  fun hide() {
    _isVisible.value = false
  }
}