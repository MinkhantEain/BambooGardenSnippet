package com.example.bamboogarden.menu.menuTable

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.menu.data.MenuTable
import com.example.bamboogarden.menu.repository.RemoteMenuRepoImpl
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject

class MenuTableScreenViewModel(
  private val menuRepoImpl: RemoteMenuRepoImpl = BambooGardenApplication.appModule.remoteMenuRepository
) : ViewModel() {
  val TAG = "MenuTableScreenViewModel"
  private val _tables = mutableStateListOf<MenuTable>()
  val tables: List<MenuTable> = _tables
  private lateinit var listenerRegistration: ListenerRegistration


  init {
    subscribeToMenuTable()
  }

  override fun onCleared() {
    if (this::listenerRegistration.isInitialized) listenerRegistration.remove()
    super.onCleared()
  }

  private fun subscribeToMenuTable() {
    listenerRegistration = menuRepoImpl.getTablesCollection().addSnapshotListener(EventListener { snapshot, error ->
      error?.let {
        Log.d(TAG, "subscribeToMenuTable: ${error.message}")
      }

      snapshot?.let {
        _tables.clear()
        it.documents.forEach {docSnap ->
          _tables.add(docSnap.toObject<MenuTable>()!!)
        }
        _tables.sort()
      }
    })
  }

  /**
   * will set presence based on the param: present value of the table
   */
  fun menuTableTogglePresence(table: MenuTable) {
    table.selfRef.set(table)
  }
}
