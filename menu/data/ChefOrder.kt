package com.example.bamboogarden.menu.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDateTime
import java.time.LocalTime

data class ChefOrder(
  val time: String = LocalTime.now().toString(),
  val selfRef: DocumentReference = FirebaseFirestore.getInstance().document(""),
  val orderList: List<DishOrder> = listOf(),
  val tableId: String = "",
) {
  fun withWrapper() : ChefOrderWithWrapper {
    val temp = orderList.groupBy { it.dish.id + it.comment }
    return ChefOrderWithWrapper(time = time,
      selfRef = selfRef,
      tableId = tableId,
      orderList = temp.values.toList())
  }
}


data class ChefOrderWithWrapper(
  val time: String = LocalTime.now().toString(),
  val selfRef: DocumentReference = FirebaseFirestore.getInstance().document(""),
  val orderList: List<List<DishOrder>> = listOf(),
  val tableId: String = "",
) {
  fun withoutWrapper() : ChefOrder {
    return ChefOrder(
      time = time,
      selfRef = selfRef,
      tableId = tableId,
      orderList = orderList.flatten()
    )
  }
}
