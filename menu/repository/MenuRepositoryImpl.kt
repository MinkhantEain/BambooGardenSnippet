package com.example.bamboogarden.menu.repository

import com.example.bamboogarden.common.MENUPAYMENTCOLLECTION
import com.example.bamboogarden.menu.data.DishOrder
import com.example.bamboogarden.menu.data.MenuDish
import com.example.bamboogarden.menu.data.MenuDishData
import com.example.bamboogarden.menu.menuOrder.MenuOrderWrapper
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class MenuRepositoryImpl(
    private val localMenuRepoImpl: LocalMenuRepoImpl,
    private val remoteMenuRepoImpl: RemoteMenuRepoImpl,
) {

  suspend fun getAllDishes(): List<MenuDish> {
    return withContext(Dispatchers.IO) {
      localMenuRepoImpl.getAllDishes()
    }
  }

  suspend fun syncDatabases() {
    withContext(Dispatchers.IO) {
      val remoteMenuDishData = remoteMenuRepoImpl.getMenuDishData()
      if (!localMenuRepoImpl.isSynced(remoteMenuDishData)) {
        val menuDishes = remoteMenuRepoImpl.getMenuDishes()
        localMenuRepoImpl.updateLocalDb(menuDishes, remoteMenuDishData)
      }
    }
  }

  suspend fun getMenuDishData() : MenuDishData {
     return withContext(Dispatchers.IO) {
      return@withContext localMenuRepoImpl.getMenuDishData()
    }
  }

  suspend fun getMenuDishes(): List<MenuDish> {
    return withContext(Dispatchers.IO) {
      return@withContext localMenuRepoImpl.getMenuDishes()
    }
  }

  suspend fun updateTablePresence(tableId: String) {
    return withContext(Dispatchers.IO) {
      remoteMenuRepoImpl.updateTablePresence(tableId)
    }
  }

  suspend fun getMenuDishOfType(chosenType: String) : List<MenuDish> {
    return localMenuRepoImpl.getMenuDishOfType(chosenType)
  }

  suspend fun getPopularMenuDishes() : List<MenuDish> {
    return localMenuRepoImpl.getPopularMenuDishes()
  }

  suspend fun getMenuDishContains(userInput: String): List<MenuDish> {
    return localMenuRepoImpl.getMenuDishContains(userInput)
  }

  suspend fun selectMenuDish(tableId: String, dishOrder: DishOrder, comment: String) {
    remoteMenuRepoImpl.selectMenuDish(tableId, dishOrder, comment)
  }

  fun getOrderedDishCollection(tableId: String): CollectionReference {
    return remoteMenuRepoImpl.getOrderedDishCollection(tableId)
  }

  suspend fun sendDishOrdersToChef(tableId:String, menuOrderWrappers :List<MenuOrderWrapper>, writeBatch: WriteBatch? = null) {
    return remoteMenuRepoImpl.sendDishOrdersToChef(tableId, menuOrderWrappers, writeBatch)
  }

  suspend fun collectPayment(tableId: String, menuOrderWrappers: List<MenuOrderWrapper>, batch: WriteBatch, taxIncluded: Boolean, serviceChargeIncluded: Boolean) {
    return remoteMenuRepoImpl.collectPayment(tableId, menuOrderWrappers, batch, taxIncluded, serviceChargeIncluded)
  }

  suspend fun getPaymentCollection(date: LocalDate) : CollectionReference {
    return remoteMenuRepoImpl.getCollection(MENUPAYMENTCOLLECTION(date))
  }

}
