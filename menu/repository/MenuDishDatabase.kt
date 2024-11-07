package com.example.bamboogarden.menu.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.bamboogarden.menu.data.MenuDish
import com.example.bamboogarden.menu.data.MenuDishData

@Database(entities = [MenuDish::class, MenuDishData::class], version = 3, exportSchema = false)
abstract class MenuDishDatabase : RoomDatabase() {
  abstract val menuDishDao: MenuDishDao
  abstract val menuDishDataDao: MenuDishDataDao
}
