package com.example.bamboogarden.chef.chefHistory

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.chef.data.DeletedDish
import com.example.bamboogarden.chef.repository.ChefRemoteRepository
import kotlinx.coroutines.launch
import java.time.LocalDate

class ChefHistoryScreenViewModel(val repo: ChefRemoteRepository = BambooGardenApplication.appModule.chefRemoteRepository) :
  ViewModel() {
  val showDialog = mutableStateOf(false)
  val date = mutableStateOf(LocalDate.now())
  val deleted = mutableStateOf<List<DeletedDish>>(listOf())

  init {
    viewModelScope.launch {
      deleted.value = repo.getDeletedDish(date.value)
    }
  }

  fun changeDate(newDate: LocalDate) {
    viewModelScope.launch {
      date.value = newDate
      deleted.value = repo.getDeletedDish(newDate)
    }
  }
}