package com.example.bamboogarden.common


import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


fun Modifier.devBoarder(): Modifier {
  return this.border(width=1.dp, color = Color.Red)
}