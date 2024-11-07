package com.example.bamboogarden.management.produceExcel

import android.os.Environment
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.common.CARRYONFIELD
import com.example.bamboogarden.common.DAILYRECORDOFDATE
import com.example.bamboogarden.common.EXPENSECOLLECTION
import com.example.bamboogarden.common.INCOMECOLLECTION
import com.example.bamboogarden.common.dialogs.loading.LoadingController
import com.example.bamboogarden.management.dailyRecord.DailyRecordRepository
import com.example.bamboogarden.management.expense.data.Expense
import com.example.bamboogarden.management.income.data.Income
import com.example.bamboogarden.menu.data.MenuDish
import com.example.bamboogarden.menu.repository.MenuRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter


class ExcelRepositoryImpl(
  val fireStore: FirebaseFirestore,
  private val dailyRecordRepo: DailyRecordRepository = BambooGardenApplication.appModule.dailyRecordRepository,
) : ExcelRepository {
  private suspend fun getWholeMonthIncome(date: LocalDate): List<Income> {
    return withContext(Dispatchers.IO) {
      val incomes = mutableListOf<Income>()
      val days = YearMonth.of(date.year, date.month).lengthOfMonth()
      for (i in 1..days) {
        val temp =
          fireStore.collection(INCOMECOLLECTION(LocalDate.of(date.year, date.month, i))).get()
            .await().documents.map { it.toObject<Income>()!! }
        incomes.addAll(temp)
      }
      return@withContext incomes
    }
  }

  private suspend fun getWholeMonthExpense(date: LocalDate): List<Expense> {
    return withContext(Dispatchers.IO) {
      val expenses = mutableListOf<Expense>()
      val days = YearMonth.of(date.year, date.month).lengthOfMonth()
      for (i in 1..days) {
        val temp =
          fireStore.collection(EXPENSECOLLECTION(LocalDate.of(date.year, date.month, i))).get()
            .await().documents.map { it.toObject<Expense>()!! }
        expenses.addAll(temp)
      }
      return@withContext expenses
    }
  }

  override suspend fun produceDailyRecordReport(date: LocalDate, loadingController: LoadingController) {
    withContext(Dispatchers.IO) {
      val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
      val myDir = File("$root/BambooGarden")
      if (!myDir.exists()) {
        myDir.mkdirs()
      }
      val file = File(
        myDir.path,
        "${date.format(DateTimeFormatter.ofPattern("MMM yyyy"))} ${
          LocalTime.now().format(DateTimeFormatter.ofPattern("hh,mm,ss"))
        }.xls"
      )
      val fos = FileOutputStream(file)
      val incomes = getWholeMonthIncome(date)
      val expenses = getWholeMonthExpense(date)
      loadingController.setTotal(incomes.size + expenses.size + 20)
      dailyRecordRepo.updateCarryOn()
      loadingController.progress(20)

      val title = "Bamboo ${date.format(DateTimeFormatter.ofPattern("MMM"))} Excel, Produced at ${
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"))
      }"
      val workbook = XSSFWorkbook()
      val sheet1 = workbook.createSheet("Daily Record Excel")
      writeTitle(title = title, sheet = sheet1)
      writeDailyRecordOverView(sheet = sheet1, expenses = expenses, incomes = incomes, date = date)
      writeDailyRecordData(
        sheet = sheet1,
        incomes = incomes,
        expenses = expenses,
        loadingController = loadingController
      )
      workbook.write(fos)
      fos.flush()
      fos.close()
    }
  }

  override suspend fun produceMenuDishesSheet(menuRepositoryImpl: MenuRepositoryImpl, loadingController: LoadingController) {
    withContext(Dispatchers.IO) {
      val root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
      val myDir = File("$root/BambooGarden")
      if (!myDir.exists()) {
        myDir.mkdirs()
      }
      val file = File(
        myDir.path,
        "MenuDishes${LocalDate.now().format(DateTimeFormatter.ofPattern("MMM yyyy"))}.xls"
      )
      val fos = FileOutputStream(file)
      val dishes = menuRepositoryImpl.getAllDishes()
      loadingController.setTotal(dishes.size)


      val workbook = XSSFWorkbook()
      val sheet1 = workbook.createSheet("Daily Record Excel")
      writeTitle(title = "MenuDishes", sheet = sheet1)
      writeMenuDishHeaders(sheet1)
      writeMenuDishData(sheet1, dishes, loadingController)
      workbook.write(fos)
      fos.flush()
      fos.close()
    }
  }

  private suspend fun writeMenuDishData(sheet: XSSFSheet, dishes: List<MenuDish>, loadingController: LoadingController) {
    withContext(Dispatchers.IO) {
      for (index in dishes.indices) {
        val rowIndex = index + 2
        val dish = dishes[index]
        sheet.createRow(rowIndex).createCell(0).setCellValue(dish.id)
        sheet.getRow(rowIndex).createCell(1).setCellValue(dish.name)
        sheet.getRow(rowIndex).createCell(2).setCellValue(dish.price.toString())
        sheet.getRow(rowIndex).createCell(3).setCellValue(dish.acronym)
        sheet.getRow(rowIndex).createCell(4).setCellValue(dish.type)
        sheet.getRow(rowIndex).createCell(5).setCellValue(dish.popular)
        loadingController.progress()
      }
    }
  }

  private suspend fun writeMenuDishHeaders(sheet: XSSFSheet) {
    withContext(Dispatchers.IO) {
      val index = 1
      sheet.createRow(index).createCell(0).setCellValue("id")
      sheet.getRow(index).createCell(1).setCellValue("name")
      sheet.getRow(index).createCell(2).setCellValue("price")
      sheet.getRow(index).createCell(3).setCellValue("acronym")
      sheet.getRow(index).createCell(4).setCellValue("type")
      sheet.getRow(index).createCell(5).setCellValue("popular")
    }
  }

  private suspend fun writeTitle(title: String, sheet: XSSFSheet) {
    withContext(Dispatchers.IO) {
      sheet.createRow(0).createCell(0).setCellValue(title)
    }
  }

  private suspend fun writeDailyRecordOverView(
    sheet: XSSFSheet,
    date: LocalDate,
    incomes: List<Income>,
    expenses: List<Expense>,
  ) {
    withContext(Dispatchers.IO) {
      val incomeTotal = incomes.fold(0) { acc: Int, income: Income -> acc + income.amount }
      val expenseTotal = expenses.fold(0) { acc: Int, expense: Expense -> acc + expense.amount }
      val closingBalance = (fireStore.document(DAILYRECORDOFDATE(date)).get()
        .await().data!![CARRYONFIELD] as Long).toInt()
      sheet.createRow(1).createCell(0).setCellValue("Total Income")
      sheet.getRow(1).createCell(1).setCellValue(incomeTotal.toDouble())
      sheet.createRow(2).createCell(0).setCellValue("Total Expense")
      sheet.getRow(2).createCell(1).setCellValue(expenseTotal.toDouble())
      sheet.createRow(3).createCell(0).setCellValue("Profit")
      sheet.getRow(3).createCell(1).setCellValue((incomeTotal - expenseTotal).toDouble())
      sheet.createRow(4).createCell(0).setCellValue("Closing Balance")
      sheet.getRow(4).createCell(1).setCellValue(closingBalance.toDouble())
    }
  }

  private suspend fun writeDailyRecordData(
    sheet: XSSFSheet,
    incomes: List<Income>,
    expenses: List<Expense>,
    loadingController: LoadingController
  ) {
    withContext(Dispatchers.IO) {
      val titleRow = sheet.createRow(5)
      titleRow.createCell(2).setCellValue("Income")
      titleRow.createCell(7).setCellValue("Expense")
      sheet.addMergedRegion(CellRangeAddress(5, 5, 0, 4))
      sheet.addMergedRegion(CellRangeAddress(5, 5, 5, 10))
      val headerRow = sheet.createRow(6)
      headerRow.createCell(0).setCellValue("Date")
      headerRow.createCell(1).setCellValue("Time")
      headerRow.createCell(2).setCellValue("About")
      headerRow.createCell(3).setCellValue("Description")
      headerRow.createCell(4).setCellValue("ရောင်းရငွေ(ကျပ်)")
      headerRow.createCell(5).setCellValue("Date")
      headerRow.createCell(6).setCellValue("Time")
      headerRow.createCell(7).setCellValue("About")
      headerRow.createCell(8).setCellValue("Description")
      headerRow.createCell(9).setCellValue("Kg/Qty")
      headerRow.createCell(10).setCellValue("ကျသင့်ငွေ(ကျပ်)")
      val dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

      for (index in incomes.indices) {
        val row = sheet.createRow(7 + index)
        val income = incomes[index]
        row.createCell(0).setCellValue(
          if (income.date == "") "" else LocalDate.parse(income.date).format(dateTimeFormatter)
        )
        row.createCell(1).setCellValue(income.time.substring(0, 4))
        row.createCell(2).setCellValue(income.about)
        row.createCell(3).setCellValue(income.comment)
        row.createCell(4).setCellValue(income.amount.toDouble())
        loadingController.progress()
      }
      val incomeSize = incomes.size
      for (index in expenses.indices) {
        val row = if (index >= incomeSize) {
          sheet.createRow(7 + index)
        } else {
          sheet.getRow(7 + index)
        }
        val expense = expenses[index]
        row.createCell(5).setCellValue(
          if (expense.date == "") "" else LocalDate.parse(expense.date).format(dateTimeFormatter)
        )
        row.createCell(6).setCellValue(expense.time.substring(0, 4))
        row.createCell(7).setCellValue(expense.about)
        row.createCell(8).setCellValue(expense.comment)
        row.createCell(9).setCellValue(expense.quantity.toString())
        row.createCell(10).setCellValue(expense.amount.toDouble())
        loadingController.progress()
      }
    }
  }
}