package com.example.bamboogarden.breakfast.breakfastBill

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.breakfast.data.Table
import com.example.bamboogarden.breakfast.repository.BreakfastRepositoryImpl
import com.example.bamboogarden.common.dialogs.loading.LoadingController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class BreakfastBillScreenViewModel(
  private val remoteRepo: BreakfastRepositoryImpl,
  private val tableId: String
) : ViewModel() {
  private val _state = mutableStateOf<Table>(Table())
  val state: State<Table> = _state
  val loadingController = LoadingController()

  init {
    initTable()
  }

  override fun onCleared() {
    onBillCleared()
    super.onCleared()
  }

  private fun initTable() {
    viewModelScope.launch {
      _state.value = remoteRepo.getTable(tableId)
    }
  }

  fun collectPayment() {
    loadingController.show()
    viewModelScope.launch {
      if (_state.value.orders.values.isNotEmpty()) {
        remoteRepo.collectPayment(_state.value)
      }
      _state.value = _state.value.copy(orders= mapOf(), people = 0)
      _state.value.selfRef.set(_state.value).await()
    }.invokeOnCompletion { loadingController.hide() }
  }

  private fun onBillCleared() {
    runBlocking(Dispatchers.IO) {
      if (_state.value.orders.values.isEmpty()) {
        _state.value = _state.value.copy(people = 0)
        _state.value.selfRef.set(_state.value)
      }
    }
  }
}
