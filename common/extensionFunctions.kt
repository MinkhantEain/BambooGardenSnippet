package com.example.bamboogarden.common

import android.content.BroadcastReceiver
import com.example.bamboogarden.menu.data.DishOrder
import com.example.bamboogarden.menu.menuOrder.MenuOrderWrapper
import java.text.NumberFormat
import java.util.Locale

fun List<DishOrder>.toMenuOrderWrappers(): List<MenuOrderWrapper> {
  val grouping = this.groupBy { it.status.name + it.dish.id + it.comment }
  val ret: MutableList<MenuOrderWrapper> = mutableListOf()
  grouping.values.forEach {
    val wrapper = MenuOrderWrapper(it.size, it.first().status, it)
    ret.add(wrapper)
  }
  return ret
}

fun Int.toCurrency(): String {
  return NumberFormat.getInstance(Locale.US).apply {
    maximumFractionDigits = 0
    isGroupingUsed = true
  }.format(this)
}

fun String.isNumber(): Boolean {
  val s = this.filter { char -> char != ',' }
  return s.toIntOrNull() != null
}

fun String.toNumber() : Int {
  val s = this.filter { char -> char != ',' }
  return s.toInt()
}