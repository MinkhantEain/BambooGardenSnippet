package com.example.bamboogarden.navigations

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.breakfast.breakfastBill.BreakfastBillScreen
import com.example.bamboogarden.breakfast.breakfastBill.BreakfastBillScreenViewModel
import com.example.bamboogarden.breakfast.breakfastCollected.BreakfastCollectedScreen
import com.example.bamboogarden.breakfast.breakfastHistory.BreakfastHistoryScreen
import com.example.bamboogarden.breakfast.breakfastOrder.BreakfastOrderScreen
import com.example.bamboogarden.breakfast.breakfastOrder.BreakfastOrderScreenViewModel
import com.example.bamboogarden.breakfast.breakfastTable.BreakfastTableScreen
import com.example.bamboogarden.breakfast.breakfastTable.BreakfastTableScreenViewModel
import com.example.bamboogarden.breakfast.data.Table
import com.example.bamboogarden.common.viewModelFactory
import kotlinx.serialization.Serializable

@Composable
fun BreakfastNavigation(onBackToHomeClick: () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = BreakfastTableScreenNav) {
        composable<BreakfastTableScreenNav> {
            BreakfastTableScreen(
                onBackToHomeClick = onBackToHomeClick,
                onTableClicked = { table: Table, vm: BreakfastTableScreenViewModel ->
                    if (table.people > 0) {
                        vm.onTableClicked(table)
                        navController.navigate(BreakfastBillScreenNav(table.tableId))
                    } else navController.navigate(BreakfastOrderScreenNav(table.tableId))
                },
                onHistoryButtonClick = { navController.navigate(BreakfastHistoryScreenNav) }
            )
        }

        composable<BreakfastOrderScreenNav> { navBackStackEntry ->
            val tableId = navBackStackEntry.toRoute<BreakfastOrderScreenNav>().tableId
            val viewModel: BreakfastOrderScreenViewModel =
                viewModel<BreakfastOrderScreenViewModel>(
                    factory =
                        viewModelFactory {
                            BreakfastOrderScreenViewModel(
                              repo = BambooGardenApplication.appModule.breakfastRepository,
                              tableId = tableId,
                            )
                        }
                )
            BreakfastOrderScreen(
                viewModel,
                onBackClick = {
                    navController.popBackStack()
                    navController.navigate(BreakfastTableScreenNav)
                },
                onOrderClick = {
                    if (viewModel.validOrderState.value) {
                        navController.popBackStack()
                        navController.navigate(BreakfastBillScreenNav(tableId = tableId))
                    }
                },
            )
        }

        composable<BreakfastBillScreenNav> {
            val tableId = it.toRoute<BreakfastBillScreenNav>().tableId
            val vm: BreakfastBillScreenViewModel =
                viewModel<BreakfastBillScreenViewModel>(
                    factory =
                        viewModelFactory {
                            BreakfastBillScreenViewModel(
                                remoteRepo =
                                    BambooGardenApplication.appModule.breakfastRepository,
                                tableId = tableId,
                            )
                        }
                )
            BreakfastBillScreen(
                vm = vm,
                tableState = vm.state,
                onBackClick = {
                    navController.popBackStack()
                    navController.navigate(BreakfastTableScreenNav)
                },
                onChangeClick = {
                    navController.popBackStack()
                    navController.navigate(BreakfastOrderScreenNav(tableId))
                },
                onPaymentClick = {total ->
                    vm.collectPayment()
                    navController.navigate(BreakfastCollectedScreenNav(total, tableId))
                }
            )
        }

        composable<BreakfastHistoryScreenNav> {
            BreakfastHistoryScreen(onBackButtonClick = { navController.navigateUp() })
        }

        composable<BreakfastCollectedScreenNav> {
            val collectedAmount = it.toRoute<BreakfastCollectedScreenNav>().amount
            val tableId = it.toRoute<BreakfastCollectedScreenNav>().tableId
            BreakfastCollectedScreen(
                tableId = tableId,
                onBackClick = {
                    navController.popBackStack().also {
                        navController.navigate(BreakfastTableScreenNav)
                    }
                },
                collectedAmount = collectedAmount,
            )
        }
    }
}

@Serializable object BreakfastTableScreenNav

@Serializable data class BreakfastOrderScreenNav(val tableId: String)

@Serializable data class BreakfastBillScreenNav(val tableId: String)

@Serializable object BreakfastHistoryScreenNav

@Serializable data class BreakfastCollectedScreenNav(val amount: Int, val tableId: String)
