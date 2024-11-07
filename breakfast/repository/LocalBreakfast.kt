package com.example.bamboogarden.breakfast.repository

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "LocalBreakfast")
@TypeConverters(value = [LocalBreakfastConverter::class])
data class LocalBreakfast(
  @PrimaryKey val key : Int = 0,
  val prices: ArrayList<Int> = arrayListOf()
)

class LocalBreakfastConverter() {
  @TypeConverter
  fun forRoomPrice(prices: ArrayList<Int>) : String {
    return prices.joinToString(",")
  }

  @TypeConverter
  fun fromRoomPrice(string: String): ArrayList<Int> {
    val iter = string.split(",").map { it.toInt() }
    val v = arrayListOf<Int>()
    v.addAll(iter)
    return v
  }
}