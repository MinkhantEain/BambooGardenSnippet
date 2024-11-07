package com.example.bamboogarden.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bamboogarden.common.camera.CameraScreen
import com.example.bamboogarden.management.ManagementScreen
import com.example.bamboogarden.management.checkTotal.CheckTotalScreen
import com.example.bamboogarden.management.dailyRecord.DailyRecordScreen
import com.example.bamboogarden.management.expense.AddExpenseScreen
import com.example.bamboogarden.management.income.AddIncomeScreen
import com.example.bamboogarden.management.produceExcel.ProduceExcelScreen
import kotlinx.serialization.Serializable

@Composable
fun ManagementNavigation(onBackButtonClick: () -> Unit) {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = ManagementScreenNav) {
    composable<ManagementScreenNav> {
      ManagementScreen(
        onAddExpenseButtonClick = { navController.navigate(AddExpenseScreenNav) },
        onAddIncomeButtonClick = { navController.navigate(AddIncomeScreenNav) },
        onDailyReportButtonClick = { navController.navigate(DailyReportScreenNav) },
        onCheckTotalButtonClick = { navController.navigate(CheckTotalScreenNav) },
        onProduceExcelButtonClick = { navController.navigate(ProduceExcelScreenNav) },
        onBackButtonClick = onBackButtonClick
      )
    }
    composable<AddExpenseScreenNav> {
      AddExpenseScreen(
        onBackButtonClick = { navController.navigateUp() },
        onCameraButtonClick = { navController.navigate(CameraScreenNav) }
      )
    }
    composable<AddIncomeScreenNav> {
      AddIncomeScreen(onBackButtonClick = { navController.navigateUp() })
    }
    composable<DailyReportScreenNav> {
      DailyRecordScreen(onBackClick = {
        navController.navigateUp()
      })
    }

    composable<CheckTotalScreenNav> {
      CheckTotalScreen(onBackClick = {
        navController.navigateUp()
      })
    }
    composable<ProduceExcelScreenNav> {
      ProduceExcelScreen(onBackClick = {
        navController.navigateUp()
      })
    }

    composable<CameraScreenNav> {
      CameraScreen(popCallBack = {navController.navigateUp()} )
    }
  }
}

@Serializable
object ManagementScreenNav

@Serializable
object AddExpenseScreenNav

@Serializable
object AddIncomeScreenNav

@Serializable
object DailyReportScreenNav

@Serializable
object CheckTotalScreenNav

@Serializable
object ProduceExcelScreenNav

@Serializable
object CameraScreenNav

