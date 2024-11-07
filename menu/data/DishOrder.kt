package com.example.bamboogarden.menu.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

enum class DishOrderStatus {
  Deciding, Cooking, Completed, Paid
}
data class DishOrder(
  val comment: String = "",
  val status: DishOrderStatus = DishOrderStatus.Deciding,
  val dish : MenuDish = MenuDish(),
  val selfRef: DocumentReference = FirebaseFirestore.getInstance().document("")
)
