@file:Suppress("UNCHECKED_CAST")

package com.example.bamboogarden.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras

fun <vm: ViewModel> viewModelFactory(initializer: (CreationExtras) -> vm) : ViewModelProvider.Factory {
  return object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
      return initializer(extras) as T
    }
  }
}