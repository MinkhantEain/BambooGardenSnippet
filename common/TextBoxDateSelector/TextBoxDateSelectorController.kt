package com.example.bamboogarden.common.TextBoxDateSelector

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TextBoxDateSelectorController(
  initialDate: LocalDate = LocalDate.now()
) {
  val TAG = "TextBoxDateSelectorController"
  private val _date = mutableStateOf(initialDate)
  val date get() = _date.value

  private val _isVisible = mutableStateOf(false)
  val isVisible: State<Boolean> = _isVisible


  val textFieldValue get() = TextFieldValue(_date.value.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))


  fun showDatePicker() {
    _isVisible.value = true
  }

  fun hideDatePicker() {
    _isVisible.value = false
  }

  fun selectDate(newDate: LocalDate) {
    _date.value = newDate
    hideDatePicker()
  }
}