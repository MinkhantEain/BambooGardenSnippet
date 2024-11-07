package com.example.bamboogarden.breakfast.repository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalBreakfast::class, BreakfastData::class], version = 2,)
abstract class LocalBreakfastDatabase: RoomDatabase() {
  abstract val dao: BreakfastDao
}