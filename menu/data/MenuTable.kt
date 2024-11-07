package com.example.bamboogarden.menu.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

data class MenuTable(
    val tableId: String = "",
    val selfRef: DocumentReference = FirebaseFirestore.getInstance().document(""),
    val present: Boolean = false,
) : Comparable<MenuTable> {

  override fun compareTo(other: MenuTable): Int {
    return if (tableId[0] < other.tableId[0]) {
      -1
    } else if (tableId[0] > other.tableId[0]) {
      1
    } else {
      val v1 = tableId.substring(1).toInt()
      val v2 = other.tableId.substring(1).toInt()
      if (v1 < v2) {
        -1
      } else if (v1 > v2) {
        1
      } else {
        0
      }
    }
  }
}
