package com.example.bamboogarden.menu.menuOrder

import com.example.bamboogarden.menu.data.DishOrder
import com.example.bamboogarden.menu.data.DishOrderStatus

data class MenuOrderWrapper(
  val count: Int = 0,
  val collectiveStatus: DishOrderStatus = DishOrderStatus.Deciding,
  val dishList: List<DishOrder> = listOf(),
)
