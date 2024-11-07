package com.example.bamboogarden.breakfast.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "BreakfastData")
data class BreakfastData(
  @PrimaryKey val key : Int = 0,
  val lastModified: String = LocalDate.of(2024, 1, 1).toString()
)
