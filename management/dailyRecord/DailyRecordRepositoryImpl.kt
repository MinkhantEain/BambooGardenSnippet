package com.example.bamboogarden.management.dailyRecord

import com.example.bamboogarden.common.ACCOUNTEDFIELD
import com.example.bamboogarden.common.CARRYONFIELD
import com.example.bamboogarden.common.DAILYRECORDDOCUMENTREFERENCE
import com.example.bamboogarden.common.DAILYRECORDOFDATE
import com.example.bamboogarden.common.EXPENSECOLLECTION
import com.example.bamboogarden.common.INCOMECOLLECTION
import com.example.bamboogarden.common.LASTMODIFIEDFIELD
import com.example.bamboogarden.management.expense.data.Expense
import com.example.bamboogarden.management.income.data.Income
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate

class DailyRecordRepositoryImpl(private val firebase: FirebaseFirestore): DailyRecordRepository {
  override suspend fun getIncome(date: LocalDate): List<Income> {
    return withContext(Dispatchers.IO) {
      return@withContext firebase.collection(INCOMECOLLECTION(date)).get()
        .await().documents.map { it.toObject<Income>()!! }
    }
  }

  override suspend fun getClosingBalance(date: LocalDate): Int {
    return withContext(Dispatchers.IO) {
      val doc = firebase.document(DAILYRECORDOFDATE(date)).get().await()
      if (!doc.exists()) {
        throw Exception("Document doesn't exists")
      } else {
        return@withContext (doc.data!![CARRYONFIELD] as Long).toInt()
      }
    }
  }

  override suspend fun getExpense(date: LocalDate): List<Expense> {
    return withContext(Dispatchers.IO) {
      return@withContext firebase.collection(EXPENSECOLLECTION(date)).get()
        .await().documents.map { it.toObject<Expense>()!! }
    }
  }

  override suspend fun updateCarryOn() {
    withContext(Dispatchers.IO) {
      val lastModified = firebase.document(DAILYRECORDDOCUMENTREFERENCE)
        .get().await().data!![LASTMODIFIEDFIELD] as String
      var lastModifiedDate = LocalDate.parse(lastModified)
      var carryOn = (firebase.document(DAILYRECORDOFDATE(lastModifiedDate)).get()
        .await().data!![CARRYONFIELD] as Long).toInt()

      while (!lastModifiedDate.isAfter(LocalDate.now())) {
        val unaccountedIncomes = getIncome(lastModifiedDate).filter { income -> !income.accounted }
        val incomeAmt = unaccountedIncomes
          .fold(0) { acc: Int, income: Income -> acc + income.amount }
        val unaccountedExpense =
          getExpense(lastModifiedDate).filter { expense -> !expense.accounted }
        val expenseAmt = unaccountedExpense
          .fold(0) { acc: Int, expense: Expense -> acc + expense.amount }
        unaccountedExpense.forEach { it.selfRef.update(ACCOUNTEDFIELD, true) }
        unaccountedIncomes.forEach { it.selfRef.update(ACCOUNTEDFIELD, true) }
        carryOn += incomeAmt
        carryOn -= expenseAmt
        firebase.document(DAILYRECORDOFDATE(lastModifiedDate)).set(hashMapOf(CARRYONFIELD to carryOn))
        firebase.document(DAILYRECORDDOCUMENTREFERENCE)
          .set(hashMapOf(LASTMODIFIEDFIELD to lastModifiedDate.toString()))
        lastModifiedDate = lastModifiedDate.plusDays(1)
      }
    }
  }
}