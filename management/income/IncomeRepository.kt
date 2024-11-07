package com.example.bamboogarden.management.income

import com.example.bamboogarden.common.INCOMECOLLECTION
import com.example.bamboogarden.management.income.data.Income
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IncomeRepository {
    val firebase = FirebaseFirestore.getInstance()

    private suspend fun getIncomeCollection(date: LocalDate = LocalDate.now()): CollectionReference {
        return withContext(Dispatchers.IO) {
            return@withContext firebase.collection(INCOMECOLLECTION(date))
        }
    }

  suspend fun addIncomeRecord(income: Income) {
    withContext(Dispatchers.IO) {
      val docRef = getIncomeCollection().document()
      docRef.set(income.copy(selfRef = docRef))
    }
  }
}
