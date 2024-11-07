package com.example.bamboogarden.breakfast.breakfastCollected

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class BreakfastCollectedScreenViewModel(
  callBack: () -> Unit,
) : ViewModel() {

  init {
    executeDelayedBack {
      callBack()
    }
  }
  private fun executeDelayedBack(callBack: () -> Unit) {
    viewModelScope.launch {
      delay(1000L * 3)
      callBack()
    }
  }
}
