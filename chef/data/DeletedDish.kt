package com.example.bamboogarden.chef.data

import com.example.bamboogarden.menu.data.MenuDish
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

data class DeletedDish(
  val tableId: String = "",
  val time: String = "",
  val dish: MenuDish = MenuDish(),
  val selfRef: DocumentReference = FirebaseFirestore.getInstance().document(""),
  val date: String = "",
)
