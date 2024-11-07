package com.example.bamboogarden.menu.menuHistory

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.menu.data.MenuPayment
import com.example.bamboogarden.menu.repository.MenuRepositoryImpl
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate

class MenuHistoryScreenViewModel(
  private val repo: MenuRepositoryImpl = BambooGardenApplication.appModule.menuRepository,
): ViewModel() {
  val showDialog = mutableStateOf(false)
  val date = mutableStateOf(LocalDate.now())
  val payments = mutableStateOf<List<MenuPayment>>(listOf())

  init {
    getPayment(date.value)
  }
  fun changeDate(newDate: LocalDate) {
    date.value = newDate
    getPayment(newDate)
  }

  private fun getPayment(date: LocalDate) {
    viewModelScope.launch(Dispatchers.IO) {
      payments.value = repo.getPaymentCollection(date).get().await().documents.map {
        it.toObject<MenuPayment>()!!
      }
    }
  }
}
