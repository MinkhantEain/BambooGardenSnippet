package com.example.bamboogarden.management.expense.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

data class Expense(
  val photoPath : String = "",
  val about: String = "",
  val comment: String = "",
  val date : String = "",
  val time: String = "",
  val amount: Int = 0,
  val quantity: Int = 0,
  val accounted: Boolean = false,
  val selfRef: DocumentReference = FirebaseFirestore.getInstance().document(""),
) {
  fun commaDelimited(): String {
    return "$about,$quantity,$amount,$time"
  }
}
