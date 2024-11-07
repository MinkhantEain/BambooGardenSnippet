package com.example.bamboogarden.breakfast.repository

import android.util.Log
import com.example.bamboogarden.breakfast.data.Table
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

class BreakfastRepositoryImpl(
  private val localBreakfastRepository: LocalBreakfastRepository = LocalBreakfastRepository.instance,
  private val remoteBreakfastRepository: RemoteBreakfastRepository = RemoteBreakfastRepository.instance,
) {

  suspend fun syncDatabases() {
    withContext(Dispatchers.IO) {
      val localBreakfastData = localBreakfastRepository.getBreakfastData()
      val remoteBreakfastData = remoteBreakfastRepository.getBreakfastData()
      if (localBreakfastData.lastModified != remoteBreakfastData.lastModified) {
        localBreakfastRepository.updateBreakfastData(remoteBreakfastData)
        val prices = remoteBreakfastRepository.getPrices()
        Log.d("price", "syncDatabases: $prices")
        localBreakfastRepository.updatePrices(prices)
      }
    }
  }

  suspend fun logBreakfastSave(table: Table) {
    remoteBreakfastRepository.logBreakfastSave(table)
  }

  suspend fun getTablesCollection() : CollectionReference {
    return remoteBreakfastRepository.getTablesCollection()
  }

  suspend fun getTable(tableId: String): Table {
    return remoteBreakfastRepository.getTable(tableId)
  }

  suspend fun togglePresence(table: Table): Unit {
    remoteBreakfastRepository.togglePresence(table)
  }

  suspend fun getPrices(): ArrayList<Int> {
    return withContext(Dispatchers.IO) {
      return@withContext localBreakfastRepository.getPrices()
    }
  }

  suspend fun collectPayment(table: Table) {
    remoteBreakfastRepository.collectPayment(table)
  }

  suspend fun getPaymentCollection(date : LocalDate) : CollectionReference {
    return remoteBreakfastRepository.getPaymentCollection(date)
  }
}