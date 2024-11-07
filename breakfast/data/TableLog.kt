package com.example.bamboogarden.breakfast.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

data class TableLog(
  val tableId: String = "",
  val orderPersonnel: String = "",
  val orders: Map<String, BreakfastOrder> = mapOf(),
  val people: Int = 0,
  val selfRef: DocumentReference = FirebaseFirestore.getInstance().document(""),
  val time: String = ""
)