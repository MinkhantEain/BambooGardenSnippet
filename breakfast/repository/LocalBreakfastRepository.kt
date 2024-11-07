package com.example.bamboogarden.breakfast.repository

import com.example.bamboogarden.BambooGardenApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalBreakfastRepository(
  private val database: LocalBreakfastDatabase = BambooGardenApplication.appModule.localBreakfastDatabase
) {

  companion object {
    private lateinit var _instance: LocalBreakfastRepository

    val instance: LocalBreakfastRepository by lazy {
        if (!this::_instance.isInitialized) {
          _instance = LocalBreakfastRepository()
        }
      _instance
    }
  }
  suspend fun getPrices() : ArrayList<Int> {
    return withContext(Dispatchers.IO) {
      return@withContext database.dao.getLocalBreakfast().prices
    }
  }

  suspend fun getBreakfastData() : BreakfastData {
    return withContext(Dispatchers.IO) {
      return@withContext database.dao.getLocalBreakfastData() ?: BreakfastData()
    }
  }

  suspend fun updateBreakfastData(breakfastData: BreakfastData) {
    withContext(Dispatchers.IO) {
      database.dao.upsertBreakfastData(breakfastData)
    }
  }

  suspend fun updatePrices(prices: ArrayList<Int>) {
    withContext(Dispatchers.IO) {
      database.dao.upsertLocalBreakfast(LocalBreakfast(prices = prices))
    }
  }
}