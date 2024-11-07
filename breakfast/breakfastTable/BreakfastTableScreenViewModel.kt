package com.example.bamboogarden.breakfast.breakfastTable

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.breakfast.data.Table
import com.example.bamboogarden.breakfast.repository.BreakfastRepositoryImpl
import com.example.bamboogarden.common.BREAKFASTORDERSCREENTABLEKEY
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.launch

class BreakfastTableScreenViewModel(
    private val repo: BreakfastRepositoryImpl,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

  var tables = mutableStateListOf<Table>()
  private lateinit var listenerRegistration: ListenerRegistration

  init {
    subscribeToTableList()
  }

  override fun onCleared() {
    listenerRegistration.remove()
    super.onCleared()
  }

  private fun subscribeToTableList() {
    viewModelScope.launch {
      listenerRegistration = repo.getTablesCollection().addSnapshotListener { querySnapshot, firebaseError ->
        firebaseError?.let {
          Log.d("BreakfastTableScreenVM", "subscribeToTableList: ${firebaseError.message}")
        }
        querySnapshot?.let {
          val tempList = mutableStateListOf<Table>()
          for (docSnap in it) {
            try {
              tempList.add(docSnap.toObject<Table>())
            } catch (e: Exception) {
              Log.d("", "subscribeToTableList: ${docSnap.id}")
            }
          }
          tables.clear()
          tables.addAll(tempList)
        }
      }
    }
  }

  fun onTableClicked(table: Table) {
    savedStateHandle[BREAKFASTORDERSCREENTABLEKEY] = table.tableId
  }
}
