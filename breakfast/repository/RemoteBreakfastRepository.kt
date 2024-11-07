package com.example.bamboogarden.breakfast.repository

import android.util.Log
import com.example.bamboogarden.breakfast.data.Table
import com.example.bamboogarden.common.BREAKFASTCUSTOMERCOLLECTION
import com.example.bamboogarden.common.BREAKFASTDATA
import com.example.bamboogarden.common.BREAKFASTORDERPRICES
import com.example.bamboogarden.common.BREAKFASTPAYMENTCOLLECTION
import com.example.bamboogarden.common.BREAKFAST_SAVE_LOG
import com.example.bamboogarden.common.PEOPLEFIELD
import com.example.bamboogarden.common.PRICEFIELD
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime

class RemoteBreakfastRepository {
  private val firebase = FirebaseFirestore.getInstance()

  companion object {
    private lateinit var _instace: RemoteBreakfastRepository
    val instance: RemoteBreakfastRepository by lazy {
      if (!this::_instace.isInitialized) {
        _instace = RemoteBreakfastRepository()
      }
      _instace
    }
  }

  suspend fun logBreakfastSave(table: Table) = withContext(Dispatchers.IO) {
    firebase.collection(BREAKFAST_SAVE_LOG).add(table.toTableLog(LocalDateTime.now())).await()
  }

  suspend fun getTables(): List<Table> {
    return withContext(Dispatchers.IO) {
      return@withContext firebase.collection(BREAKFASTCUSTOMERCOLLECTION).get().await().documents.map { docSnap ->
        docSnap.toObject<Table>() ?: Table()
      }
    }
  }

  suspend fun getBreakfastData(): BreakfastData {
    return withContext(Dispatchers.IO) {
      return@withContext firebase.document(BREAKFASTDATA).get().await().toObject<BreakfastData>()!!
    }
  }

  suspend fun getTablesCollection() : CollectionReference {
    return withContext(Dispatchers.IO) {
      return@withContext firebase.collection(BREAKFASTCUSTOMERCOLLECTION)
    }
  }

  suspend fun getTable(tableId: String): Table {
    return withContext(Dispatchers.IO) {
      try {
        val docSnap = firebase.document("$BREAKFASTCUSTOMERCOLLECTION/$tableId").get().await()
        val table = docSnap.toObject<Table>() ?: Table()
        Log.d("RemoteBreakfastRepository", "getTable: success, $table")
        return@withContext  table
      } catch (e: Exception) {
        Log.d("RemoteBreakfastRepository", "getTable: tableId: $tableId ${e.message}")
        return@withContext Table()
      }
    }
  }

  suspend fun togglePresence(table: Table): Unit {
    withContext(Dispatchers.IO) {
      table.selfRef
      .update(PEOPLEFIELD, table.people).await() }
  }

  suspend fun getPrices(): ArrayList<Int> {
    return withContext(Dispatchers.IO) {
      return@withContext firebase.document(BREAKFASTORDERPRICES).get().await().data?.get(PRICEFIELD)
          as ArrayList<Int>
    }
  }

  suspend fun collectPayment(table: Table) {
    return withContext(Dispatchers.IO) {
      val breakfastPayment = table.toBreakfastPayment(FirebaseAuth.getInstance().currentUser?.uid ?: "not logged in???")
      firebase.collection(BREAKFASTPAYMENTCOLLECTION()).add(breakfastPayment).await()
    }
  }

  suspend fun getPaymentCollection(date : LocalDate) : CollectionReference {
    return withContext(Dispatchers.IO) {
      firebase.collection(BREAKFASTPAYMENTCOLLECTION(date))
    }
  }
}
