package com.example.bamboogarden.common.dialogs.loading

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import com.example.bamboogarden.common.dialogs.DialogController

class LoadingController: DialogController() {
  val progress = mutableFloatStateOf(0f)
  private val total = mutableIntStateOf(0)
  private val current = mutableIntStateOf(0)

  fun resetProgress() {
    progress.floatValue = 0f
    total.intValue = 0
    current.intValue = 0
  }

  fun setTotal(value: Int) {
    total.intValue = value
  }

  fun increaseTotal(value: Int) {
    total.intValue += value
  }

  fun progress(value: Int = 1) {
    current.intValue += value
    progress.floatValue = current.intValue.toFloat()/total.intValue
  }
}