package com.example.bamboogarden.management.income.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


data class Income(
  val about: String = "Other",
  val amount: Int = 0,
  val comment: String = "",
  val date: String = "",
  val time: String = "",
  val accounted: Boolean= false,
  val selfRef: DocumentReference = FirebaseFirestore.getInstance().document(""),
) {
  fun commaDelimited(): String {
    return "$about,$amount,$comment,$time"
  }
}
