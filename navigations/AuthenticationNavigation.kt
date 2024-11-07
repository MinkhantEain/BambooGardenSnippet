package com.example.bamboogarden.navigations

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bamboogarden.authentication.LoginScreen
import com.example.bamboogarden.authentication.AuthenticationViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.Serializable

@Composable
fun AuthenticationNavigation() {
  val navController = rememberNavController()
  NavHost(navController = navController, startDestination = LoginScreenNav) {
    composable<LoginScreenNav> {
      val viewModel: AuthenticationViewModel = viewModel()
      if (viewModel.state.value.authResult != null || FirebaseAuth.getInstance().currentUser != null)
        ApplicationNavigation(
          onLogoutButtonClick = {navController.navigate(LoginScreenNav)}
        )
      else
        LoginScreen(
          viewModel = viewModel,
          registerClick = {
            navController.navigate(RegisterScreenNav)
          }, loginClick = { email: String, password: String->
            viewModel.login(email= email, password= password)
          }
        )
    }

    composable<RegisterScreenNav> {

    }
  }
}

@Serializable
object LoginScreenNav

@Serializable
object RegisterScreenNav


