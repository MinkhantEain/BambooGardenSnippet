package com.example.bamboogarden.management.dailyRecord

import com.example.bamboogarden.management.expense.data.Expense
import com.example.bamboogarden.management.income.data.Income
import java.time.LocalDate

interface DailyRecordRepository {
  suspend fun getIncome(date: LocalDate = LocalDate.now()): List<Income>

  suspend fun getClosingBalance(date: LocalDate): Int

  suspend fun getExpense(date: LocalDate = LocalDate.now()): List<Expense>

  suspend fun updateCarryOn()
}