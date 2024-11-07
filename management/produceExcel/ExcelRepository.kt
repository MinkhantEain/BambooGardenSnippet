package com.example.bamboogarden.management.produceExcel

import com.example.bamboogarden.common.dialogs.loading.LoadingController
import com.example.bamboogarden.menu.repository.MenuRepositoryImpl
import java.time.LocalDate

interface ExcelRepository {
  suspend fun produceDailyRecordReport(date: LocalDate, loadingController: LoadingController)
  suspend fun produceMenuDishesSheet(menuRepositoryImpl: MenuRepositoryImpl, loadingController: LoadingController)
}