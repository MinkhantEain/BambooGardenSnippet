package com.example.bamboogarden.menu.data

import com.example.bamboogarden.menu.menuOrder.MenuOrderWrapper
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalTime

data class MenuPayment(
  val time: String = LocalTime.now().toString(),
  val date: String = LocalDate.now().toString(),
  val wrappers: List<MenuPaymentWrapper> = listOf(),
  val cost: Int = 0,
  val tax: Int = 0,
  val serviceCharge: Int = 0,
  val totalCost: Int = 0,
  val tableId: String = "",
  val selfRef: DocumentReference = FirebaseFirestore.getInstance().document(""),
) {
  fun includeTax(predicate: Boolean): MenuPayment {
    val tax = if (predicate) this.cost.times(0.05).toInt() else 0
    return this.copy(
      tax = tax,
      totalCost = this.cost + this.tax + this.serviceCharge
    )
  }

  fun includeServiceCharge(predicate: Boolean): MenuPayment {
    val serviceCharge = if (predicate) this.cost.times(0.05).toInt() else 0
    return this.copy(
      serviceCharge= serviceCharge,
      totalCost = this.cost + this.tax + serviceCharge
    )
  }
}

fun List<MenuOrderWrapper>.toMenuPayment() = this.map {
  MenuPaymentWrapper(
    it.dishList.first().dish,
    it.count,
    it.count * it.dishList.first().dish.price
  )
}