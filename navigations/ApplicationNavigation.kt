package com.example.bamboogarden.navigations

import HomeScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.bamboogarden.common.bluetooth.BluetoothSelectionScreen
import kotlinx.serialization.Serializable

@Composable
fun ApplicationNavigation(onLogoutButtonClick: () -> Unit) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = HomeScreenNav) {
        composable<HomeScreenNav> {
            HomeScreen(
                onLogoutButtonClick = onLogoutButtonClick,
                onBreakfastButtonClick = { navController.navigate(BreakfastNav) },
                onMenuButtonClick = { navController.navigate((MenuNav)) },
                onChefButtonClick = { navController.navigate(ChefNav) },
                onManagementButtonClick = { navController.navigate(ManagementNav) },
                onBluetoothButtonClick = {navController.navigate(BluetoothNav)}
            )
        }

        composable<BreakfastNav> {
            BreakfastNavigation(
                onBackToHomeClick = {
                    navController.popBackStack()
                    navController.navigate(HomeScreenNav)
                }
            )
        }

        composable<MenuNav> {
            MenuNavigation(
                onBackToHomeClick = {
                    navController.popBackStack()
                    navController.navigate(HomeScreenNav)
                }
            )
        }

        composable<ManagementNav> {
            ManagementNavigation(onBackButtonClick = { navController.navigateUp() })
        }

        composable<ChefNav> {
            ChefNavigation(
                onBackButtonClick = {
                    navController.popBackStack().also { navController.navigate(HomeScreenNav) }
                }
            )
        }

        composable<BluetoothNav> {
            BluetoothSelectionScreen(onBackClick = {
                navController.popBackStack().also { navController.navigate(HomeScreenNav) }
            })
        }
    }
}

@Serializable object HomeScreenNav

@Serializable object BreakfastNav

@Serializable object MenuNav

@Serializable object ManagementNav

@Serializable object ChefNav

@Serializable object BluetoothNav