package com.example.bamboogarden.management.produceExcel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.common.TextBoxDateSelector.TextBoxDateSelectorController
import com.example.bamboogarden.common.dialogs.DialogController
import com.example.bamboogarden.common.dialogs.loading.LoadingController
import com.example.bamboogarden.menu.repository.MenuDishDatabase
import com.example.bamboogarden.menu.repository.MenuRepositoryImpl
import kotlinx.coroutines.launch

class ProduceExcelScreenViewModel(
  private val repository: ExcelRepository = BambooGardenApplication.appModule.excelRepository,
  private val menuRepositoryImpl: MenuRepositoryImpl = BambooGardenApplication.appModule.menuRepository,
): ViewModel() {
  val textBoxDateSelectorController = TextBoxDateSelectorController()
  val dialogController = DialogController()
  val loadingController = LoadingController()
  val completionDialogController = DialogController()

  fun produceExcel() {
    loadingController.show()
    viewModelScope.launch {
      repository.produceDailyRecordReport(textBoxDateSelectorController.date, loadingController = loadingController)
      loadingController.resetProgress()
    }.invokeOnCompletion {
      loadingController.hide()
      completionDialogController.show()
    }
  }

  fun produceMenuDishExcel() {
    loadingController.show()
    viewModelScope.launch {
      repository.produceMenuDishesSheet(menuRepositoryImpl, loadingController = loadingController)
      loadingController.resetProgress()
    }.invokeOnCompletion {
      loadingController.hide()
      completionDialogController.show()
    }
  }
}