package com.example.bamboogarden.management.checkTotal

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.breakfast.data.BreakfastPayment
import com.example.bamboogarden.breakfast.repository.RemoteBreakfastRepository
import com.example.bamboogarden.common.BREAKFASTPAYMENTCOLLECTIONGROUP
import com.example.bamboogarden.common.MENUPAYMENTCOLLECTIONGROUP
import com.example.bamboogarden.menu.data.MenuPayment
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters.firstDayOfMonth
import java.time.temporal.TemporalAdjusters.lastDayOfMonth

class CheckTotalScreenViewModel(
  val firebase: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ViewModel() {
  val checkOption = mutableStateOf(CheckTotalType.Daily)
  val date = mutableStateOf(LocalDate.now())
  val showDatePicker = mutableStateOf(false)

  val menuTotal = mutableIntStateOf(0)
  val menuCustomer = mutableIntStateOf(0)
  val menuDishAndCount = mutableStateOf<Map<String, Int>>(emptyMap())

  val breakfastTotal = mutableIntStateOf(0)
  val breakfastCustomer = mutableIntStateOf(0)

  fun changeDate(newDate: LocalDate) {
    date.value = newDate
    showDatePicker.value = false
    if (checkOption.value == CheckTotalType.Daily) {
      getBreakfastPayments(date.value, date.value)
      getMenuPayments(date.value, date.value)
    } else {
      getBreakfastPayments(date.value.with(firstDayOfMonth()), date.value.with(lastDayOfMonth()))
      getMenuPayments(date.value.with(firstDayOfMonth()), date.value.with(lastDayOfMonth()))
    }

  }

  private fun getBreakfastPayments(startDate: LocalDate, endDate: LocalDate) {
    viewModelScope.launch(Dispatchers.IO) {
      val breakfastPayment = firebase.collectionGroup(BREAKFASTPAYMENTCOLLECTIONGROUP)
        .whereGreaterThanOrEqualTo("date", startDate.toString())
        .whereLessThanOrEqualTo("date", endDate.toString())
        .get().await().documents.map { it.toObject<BreakfastPayment>()!! }

      breakfastTotal.intValue = breakfastPayment.fold(0) {acc, bp -> acc + bp.totalCost }
      breakfastCustomer.intValue = breakfastPayment.fold(0) {acc, bp -> acc + bp.people}
    }
  }

  private fun getMenuPayments(startDate: LocalDate, endDate: LocalDate) {
    viewModelScope.launch(Dispatchers.IO) {
      val menuPayment = firebase.collectionGroup(MENUPAYMENTCOLLECTIONGROUP)
        .whereGreaterThanOrEqualTo("date", startDate.toString())
        .whereLessThanOrEqualTo("date", endDate.toString())
        .get().await().documents.map { it.toObject<MenuPayment>()!! }


      menuTotal.intValue = menuPayment.fold(0) {acc, bp -> acc + bp.totalCost }
      menuCustomer.intValue = menuPayment.size

      val temp = mutableMapOf<String, Int>()

      menuPayment.forEach {mp ->
        mp.wrappers.forEach { mpw ->
          if (temp[mpw.dish.name] == null) {
            temp[mpw.dish.name] = mpw.count
          } else {
            temp[mpw.dish.name] = temp[mpw.dish.name]!! + mpw.count
          }
        }
      }
      menuDishAndCount.value = temp
    }
  }
}