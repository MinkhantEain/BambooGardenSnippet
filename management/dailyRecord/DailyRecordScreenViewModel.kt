package com.example.bamboogarden.management.dailyRecord

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.common.dialogs.DialogController
import com.example.bamboogarden.management.expense.data.Expense
import com.example.bamboogarden.management.income.data.Income
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class DailyRecordScreenViewModel(
  val firebase: FirebaseFirestore = FirebaseFirestore.getInstance(),
  private val cloudStorage: FirebaseStorage = FirebaseStorage.getInstance(),
  private val repo: DailyRecordRepository = BambooGardenApplication.appModule.dailyRecordRepository
) : ViewModel() {
  val selectedDate =
    mutableStateOf(LocalDate.now())
  val showDatePicker =
    mutableStateOf(false)
  val incomes = mutableStateOf<List<Income>>(emptyList())
  val expenses = mutableStateOf<List<Expense>>(emptyList())
  val carryOnBalance = mutableIntStateOf(0)
  val closingBalance = mutableIntStateOf(0)

  val expenseDialogVisibility = mutableStateOf(false)
  val selectedExpense = mutableStateOf(Expense())

  val incomeDialogVisibility = mutableStateOf(false)
  val selectedIncome = mutableStateOf(Income())

  private val _isLoading = mutableStateOf(false)
  val isLoading : State<Boolean> = _isLoading

  val errorDialog = DialogController()
  val error = mutableStateOf<Exception?>(null)


  val bitmap = mutableStateOf<Bitmap?>(
    null
  )

  init {
    _isLoading.value = true
    viewModelScope.launch {
      updateCarryOn() {
        changeDate(LocalDate.now())
        _isLoading.value = false
      }
    }
  }

  fun changeDate(date: LocalDate) {
    _isLoading.value = true
    if (date.isAfter(LocalDate.now())) {
      _isLoading.value = false
      error.value = Exception("Hasn't reach the selected date")
      errorDialog.show()
      return
    }
    viewModelScope.launch {
      try {
        closingBalance.intValue = getClosingBalance(date)
      } catch (e: Exception) {
        error.value = e
        errorDialog.show()
        return@launch
      }
      selectedDate.value = date
      incomes.value = getIncome(date).sortedBy { it.time }
      expenses.value = getExpense(date).sortedBy { it.time }
      carryOnBalance.intValue =
        closingBalance.intValue + expenses.value.fold(0) { acc, expense -> acc + expense.amount } -
          incomes.value.fold(0) { acc, income -> acc + income.amount }
    }.invokeOnCompletion {
      _isLoading.value = false
    }
  }

  private suspend fun getIncome(date: LocalDate = LocalDate.now()): List<Income> {
    return repo.getIncome(date)
  }

  private suspend fun getClosingBalance(date: LocalDate): Int {
    return repo.getClosingBalance(date)
  }

  private suspend fun getExpense(date: LocalDate = LocalDate.now()): List<Expense> {
    return repo.getExpense(date)
  }

  private fun updateCarryOn(onCompletionCallBack: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      repo.updateCarryOn()
    }.invokeOnCompletion {
      onCompletionCallBack()
    }
  }

  fun selectIncome(income: Income) {
    _isLoading.value = true
    selectedIncome.value = income
    incomeDialogVisibility.value = true
    _isLoading.value = false
  }

  fun selectExpense(expense: Expense) {
    _isLoading.value = true
    selectedExpense.value = expense
    fetchPhoto(expense) {
      _isLoading.value = false
      expenseDialogVisibility.value = true
    }
  }

  private fun fetchPhoto(expense: Expense, onCompletionCallBack: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      if (expense.photoPath.isEmpty()) {
        bitmap.value = null
        onCompletionCallBack()
      } else {
        cloudStorage.reference.child(expense.photoPath).getBytes(1024 * 1024)
          .addOnSuccessListener {
            bitmap.value = BitmapFactory.decodeByteArray(it, 0, it.size)
            onCompletionCallBack()
          }
      }
    }
  }
}