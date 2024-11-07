package com.example.bamboogarden.menu.menuCommon

import androidx.compose.ui.graphics.Color
import com.example.bamboogarden.menu.data.DishOrderStatus

fun colorOnStatus(status: DishOrderStatus) : Color {
  return when (status) {
    DishOrderStatus.Deciding -> Color(208, 113, 106, 255)
    DishOrderStatus.Cooking -> Color(141, 139, 142, 255)
    DishOrderStatus.Completed -> Color(12, 192, 223, 255)
    DishOrderStatus.Paid -> Color.Black
  }
}