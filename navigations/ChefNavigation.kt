package com.example.bamboogarden.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bamboogarden.chef.ChefScreen
import com.example.bamboogarden.chef.chefHistory.ChefHistoryScreen
import kotlinx.serialization.Serializable

@Composable
fun ChefNavigation(
  onBackButtonClick: () -> Unit,
) {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = ChefScreenNav) {
    composable<ChefScreenNav> {
      ChefScreen(onBackButtonClick = onBackButtonClick,
        onHistoryClick = { navController.navigate(ChefDeleteHistoryScreenNav) })
    }
    composable<ChefDeleteHistoryScreenNav> { ChefHistoryScreen(onBackButtonClick = onBackButtonClick) }
  }
}

@Serializable
object ChefScreenNav
@Serializable
object ChefDeleteHistoryScreenNav
