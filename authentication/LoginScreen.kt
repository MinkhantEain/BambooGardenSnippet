package com.example.bamboogarden.authentication

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.bamboogarden.R

@Composable
fun LoginScreen(
  registerClick: () -> Unit = {},
  loginClick: (String, String) -> Unit,
  viewModel: AuthenticationViewModel,
) {
  val isVisible = remember { mutableStateOf(false) }
  val password = remember { mutableStateOf("") }
  val email = remember { mutableStateOf("") }

  Box(modifier = Modifier
    .fillMaxSize()
    .padding(10.dp),
    contentAlignment = Alignment.Center
  ) {
    if (viewModel.state.value.error != null) {
      AlertDialog(
        onDismissRequest = {
          viewModel.acknowledgeAuthError()
        },
        confirmButton = {
          Button(onClick = {viewModel.acknowledgeAuthError()}) {
            Text(text = "Dismiss")
          }
        },
        text = {
          Text(viewModel.state.value.error.toString())
        },
      )
    }
    if (viewModel.state.value.isLoading)
      CircularProgressIndicator(modifier = Modifier.align(alignment = Alignment.Center))
    else
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .imePadding(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      AuthImageIcon()

      Spacer(modifier = Modifier.height(20.dp))

      EmailTextField(email = email)

      Spacer(modifier = Modifier.height(height = 10.dp))

      PasswordTextField(
        isVisible = isVisible,
        password = password,
      )

      Spacer(modifier = Modifier.height(height = 20.dp))
      Button(onClick = {loginClick(email.value, password.value)}) {
        Text(text = "Login")
      }

      Spacer(modifier = Modifier.height(height = 10.dp))
      TextButton(onClick = { registerClick() }) {
        Text(text = "Register")
      }
    }
  }
}

@Composable
fun AuthImageIcon() {
  Image(painter = painterResource(id = R.drawable.app_icon),
    contentDescription = "App Icon in Auth",
    modifier = Modifier
      .height(250.dp)
      .width(250.dp),
  )
}

@Composable
fun EmailTextField(
  email: MutableState<String>,
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Text(text = "Email", modifier = Modifier.width(80.dp))
    TextField(value = email.value,
      onValueChange = { email.value = it },
      label = { Text("email") })
  }
}


@Composable
fun PasswordTextField(
  isVisible: MutableState<Boolean>,
  password: MutableState<String>,
) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Text(text = "Password", modifier = Modifier.width(80.dp))
    TextField(
      value = password.value,
      onValueChange = { password.value = it },
      label = { Text("password") },
      trailingIcon = { TogglePasswordVisibility(isVisible = isVisible) },
      visualTransformation = if (isVisible.value) VisualTransformation.None else  PasswordVisualTransformation() )
  }

}

@Composable
fun TogglePasswordVisibility(
  isVisible : MutableState<Boolean>
) {
  val image = if (isVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
  val contentDescription = if (isVisible.value) "Visible Password" else "Invisible Password"

  Icon(imageVector = image, contentDescription = contentDescription, modifier = Modifier.clickable { isVisible.value = !isVisible.value })
}