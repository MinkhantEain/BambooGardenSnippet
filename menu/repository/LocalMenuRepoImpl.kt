package com.example.bamboogarden.menu.repository

import android.util.Log
import androidx.room.Room
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.menu.data.MenuDish
import com.example.bamboogarden.menu.data.MenuDishData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val TAG = "LocalMenuRepoImpl"
class LocalMenuRepoImpl {
  private val db =
      Room.databaseBuilder(
              context = BambooGardenApplication.instance.applicationContext,
              MenuDishDatabase::class.java,
              "MenuDish-database",
          ).fallbackToDestructiveMigration()
          .build()
  
  suspend fun getMenuDishes(): List<MenuDish> {
    return withContext(Dispatchers.IO) { db.menuDishDao.getMenuDishOrderByID() }
  }

  suspend fun getAllDishes(): List<MenuDish> {
    return withContext(Dispatchers.IO) { db.menuDishDao.getAllDishes() }
  }

  suspend fun isSynced(remoteMenuDishData: MenuDishData): Boolean {
    val menuDishData = db.menuDishDataDao.getMenuDishData()
    return menuDishData == remoteMenuDishData
  }

  suspend fun getMenuDishData() : MenuDishData {
    return withContext(Dispatchers.IO){
      return@withContext db.menuDishDataDao.getMenuDishData()
    }
  }

  suspend fun getMenuDishContains(userInput: String) : List<MenuDish> {
    return withContext(Dispatchers.IO){
      return@withContext db.menuDishDao.getDishMenuContains(userInput)
    }
  }

  suspend fun getMenuDishOfType(chosenType: String): List<MenuDish> {
    return withContext(Dispatchers.IO) {
      return@withContext db.menuDishDao.getDishMenuOfType(chosenType)
    }
  }

  suspend fun getPopularMenuDishes() : List<MenuDish> {
    return withContext(Dispatchers.IO) {
      return@withContext db.menuDishDao.getPopularMenuDishes()
    }
  }

  suspend fun updateLocalDb(menuDishes: List<MenuDish>, remoteMenuDishData: MenuDishData) {
    Log.d(TAG, "updateLocalDb: executed")
    withContext(Dispatchers.IO) {
      db.menuDishDao.getAllDishes().forEach {
        db.menuDishDao.deleteMenuDish(it)
      }
      db.menuDishDao.upsertMenuDishes(menuDishes)
      db.menuDishDataDao.updateMenuDishData(remoteMenuDishData)
    }
  }
}
