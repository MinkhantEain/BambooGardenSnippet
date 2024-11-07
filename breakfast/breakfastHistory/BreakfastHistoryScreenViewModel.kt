package com.example.bamboogarden.breakfast.breakfastHistory

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.breakfast.data.BreakfastPayment
import com.example.bamboogarden.breakfast.repository.BreakfastRepositoryImpl
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class BreakfastHistoryScreenViewModel(
  private val repo: BreakfastRepositoryImpl,
) : ViewModel() {
  val TAG = "BreakfastHistoryScreenViewModel"
  private lateinit var listenerRegistration: ListenerRegistration
  val showDialog = mutableStateOf(false)
  val date = mutableStateOf(LocalDate.now())

  private val _payments = mutableStateOf<List<BreakfastPayment>>(listOf())
  val payments: State<List<BreakfastPayment>> = _payments

  override fun onCleared() {
    if (this::listenerRegistration.isInitialized) listenerRegistration.remove()
    super.onCleared()
  }

  init {
    subscribeToPayment()
  }

  private fun subscribeToPayment(date: LocalDate = LocalDate.now()) {
    viewModelScope.launch(Dispatchers.IO) {
      listenerRegistration =
        repo.getPaymentCollection(date).addSnapshotListener { querySnapshot, error ->
          error?.let {
            Log.d(TAG, "subscribeToPayment: ${error.message}")
          }

          querySnapshot?.let { qSnap ->
            _payments.value = qSnap.documents.map { it.toObject<BreakfastPayment>()!! }
              .sortedBy { breakfastPayment: BreakfastPayment -> breakfastPayment.time }.reversed()
          }
        }
    }
  }

  fun changeDate(date: LocalDate) {
    subscribeToPayment(date)
  }
}
