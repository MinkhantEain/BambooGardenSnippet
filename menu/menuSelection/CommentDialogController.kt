package com.example.bamboogarden.menu.menuSelection

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

data class CommentDialogController(
  val show: MutableState<Boolean> = mutableStateOf(false),
  val userInput: MutableState<String> = mutableStateOf(""),
  val confirmationCallBack: MutableState<() -> Unit> = mutableStateOf({}),
)
