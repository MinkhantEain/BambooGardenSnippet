package com.example.bamboogarden.breakfast.data

import java.time.LocalDate
import java.time.LocalTime

data class BreakfastPayment(
  val collectorId: String = "",
  val orders: Map<String, BreakfastOrder> = mapOf(),
  val tableId: String = "",
  val totalCost: Int = 0,
  val time: String = LocalTime.now().toString(),
  val date : String = LocalDate.now().toString(),
  val people: Int = 0,

)
