package com.example.bamboogarden.breakfast.data


data class BreakfastOrder(
  val price: Int = 0,
  val count: Int = 0,

) {
  val totalCost get() = price * count
}