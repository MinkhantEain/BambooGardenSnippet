package com.example.bamboogarden.menu.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

data class MenuPaymentWrapper(
  val dish: MenuDish = MenuDish(),
  val count: Int = 0,
  val totalCost: Int = 0,
)
