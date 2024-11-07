package com.example.bamboogarden.common.dialogs.errorDialog

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.bamboogarden.common.dialogs.DialogController

class ErrorDialogController : DialogController() {
  private val _title = mutableStateOf("")
  val title: State<String> = _title
  private val _text = mutableStateOf("")
  val text: State<String> = _text

  fun showError(title: String, text: String) {
    this._text.value = title
    this._title.value = text
    this.show()
  }
}