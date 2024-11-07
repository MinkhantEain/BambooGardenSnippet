package com.example.bamboogarden.menu.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(tableName = "MenuDishData")
@TypeConverters(value = [CategoryListConverter::class])
data class MenuDishData(
  @PrimaryKey val id: Int = 0,
  val lastModified: String = LocalDateTime.now().toString(),
  val categoryList: List<String> = listOf(),
  val chefExclude: List<String> = listOf()
)

class CategoryListConverter {
  @TypeConverter
  fun toRoomCategoryList(categoryList: List<String>): String {
    return categoryList.joinToString(separator = "/")
  }

  @TypeConverter
  fun fromRoomCategoryList(categoryList: String): List<String> {
    return categoryList.split("/")
  }
}
