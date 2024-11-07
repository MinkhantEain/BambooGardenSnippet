package com.example.bamboogarden.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.bamboogarden.common.bluetooth.BluetoothSelectionScreen
import com.example.bamboogarden.menu.menuBill.MenuBillScreen
import com.example.bamboogarden.menu.menuHistory.MenuHistoryScreen
import com.example.bamboogarden.menu.menuOrder.MenuOrderScreen
import com.example.bamboogarden.menu.menuSelection.MenuSelectionScreen
import com.example.bamboogarden.menu.menuTable.MenuTablesScreen
import kotlinx.serialization.Serializable

@Composable
fun MenuNavigation(
    onBackToHomeClick: () -> Unit,
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = MenuTableScreenNav) {
        composable<MenuTableScreenNav> {
            MenuTablesScreen(
                onBackButtonClick = onBackToHomeClick,
                onTableClick = { table ->
                    if (table.present) {
                        navController.navigate(MenuBillScreenNav(table.tableId))
                    } else {
                        navController.navigate(MenuSelectionScreenNav(table.tableId))
                    }
                },
                onHistoryClick = {
                    navController.navigate(MenuHistoryScreenNav)
                }
            )
        }

        composable<MenuSelectionScreenNav> {
            val tableId: String = it.toRoute<MenuSelectionScreenNav>().tableId

            MenuSelectionScreen(
                onBackButtonClick = { navController.navigateUp() },
                tableId = tableId,
                onCheckButtonClick = {
                    navController.popBackStack().also {
                        navController.navigate(MenuOrderScreenNav(tableId))
                    }
                }
            )
        }

        composable<MenuOrderScreenNav> {
            val tableId = it.toRoute<MenuOrderScreenNav>().tableId
            MenuOrderScreen(
                tableId = tableId,
                onBackButtonClick = {
                    navController.popBackStack().also {
                        navController.navigate(MenuSelectionScreenNav(tableId))
                    }
                },
                onConfirmButtonClick = {
                    navController.popBackStack().also {
                        navController.navigate(MenuBillScreenNav(tableId))
                    }
                }
            )
        }



        composable<MenuBillScreenNav> {
            val tableId = it.toRoute<MenuBillScreenNav>().tableId
            MenuBillScreen(
                tableId = tableId,
                onBackClick = {
                    navController.popBackStack().also { navController.navigate(MenuTableScreenNav) }
                },
                onAddMoreDish = {
                    navController.popBackStack().also {
                        navController.navigate(MenuSelectionScreenNav(tableId))
                    }
                },
                showBluetoothConnectionScreen = {
                    navController.navigate(BluetoothScreenNav)
                }
            )
        }

        composable<MenuHistoryScreenNav> {
            MenuHistoryScreen(onBackClick = {
                navController.navigateUp()
            })
        }

        composable<BluetoothScreenNav> {
            BluetoothSelectionScreen(onBackClick = {
                navController.navigateUp()
            })
        }
    }
}

@Serializable object MenuTableScreenNav

@Serializable data class MenuSelectionScreenNav(val tableId: String)

@Serializable data class MenuOrderScreenNav(val tableId: String)

@Serializable data class MenuBillScreenNav(val tableId: String)

@Serializable object MenuHistoryScreenNav

@Serializable object BluetoothScreenNav
