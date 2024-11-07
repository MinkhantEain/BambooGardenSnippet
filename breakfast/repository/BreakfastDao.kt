package com.example.bamboogarden.breakfast.repository

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface BreakfastDao {
  @Upsert
  suspend fun upsertBreakfastData(breakfastData: BreakfastData)

  @Upsert
  suspend fun upsertLocalBreakfast(localBreakfast: LocalBreakfast)

  @Query("select * from LocalBreakfast where `key` is 0")
  suspend fun getLocalBreakfast(): LocalBreakfast

  @Query("select * from BreakfastData where `key` is 0")
  suspend fun getLocalBreakfastData(): BreakfastData?
}