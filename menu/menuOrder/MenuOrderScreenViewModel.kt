package com.example.bamboogarden.menu.menuOrder

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.menu.data.DishOrder
import com.example.bamboogarden.menu.data.DishOrderStatus
import com.example.bamboogarden.menu.repository.MenuRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuOrderScreenViewModel(
  private val menuRepositoryImpl: MenuRepositoryImpl,
  val tableId: String,
) : ViewModel() {
  val TAG = "MenuOrderScreenViewModel"
  private lateinit var listenerRegistration: ListenerRegistration
  private val _menuOrderWrappers = mutableStateOf<List<MenuOrderWrapper>>(listOf())
  val menuOrderWrappers: State<List<MenuOrderWrapper>> = _menuOrderWrappers

  private val _isLoading = mutableStateOf(false)
  val isLoading: State<Boolean> = _isLoading

  init {
    subscribeToMenuOrder()
  }

  override fun onCleared() {
    if (this::listenerRegistration.isInitialized) listenerRegistration.remove()
    super.onCleared()
  }

  fun deleteDish(menuOrderWrapper: MenuOrderWrapper): Unit {
    _isLoading.value = true
    viewModelScope.launch(Dispatchers.IO) { menuOrderWrapper.dishList.first().selfRef.delete() }
      .invokeOnCompletion { _isLoading.value = false }
  }

  fun updatePresence() {
    _isLoading.value = true
    viewModelScope.launch { menuRepositoryImpl.updateTablePresence(tableId) }
      .invokeOnCompletion { _isLoading.value = false }
  }

  private fun subscribeToMenuOrder() {
    listenerRegistration =
      menuRepositoryImpl
        .getOrderedDishCollection(tableId)
        .addSnapshotListener { querySnapshot, error ->
          error?.let { Log.d(TAG, "subscribeToMenuOrder: ${error.message}") }

          querySnapshot?.let { snapshot ->
            val tempList = mutableListOf<MenuOrderWrapper>()
            val dishOrders = snapshot.documents.map { it.toObject<DishOrder>()!! }
            val dishOrderGrouping =
              dishOrders.groupBy { it.dish.id + it.status + it.comment }
            dishOrderGrouping.values.forEach {
              tempList.add(MenuOrderWrapper(it.size, it.first().status, it))
            }
            _menuOrderWrappers.value = tempList
          }
        }
  }

  fun confirmOrder(onCompleteCallback: () -> Unit) {
    _isLoading.value = true
    viewModelScope.launch(Dispatchers.IO) {
      val menuDishData = menuRepositoryImpl.getMenuDishData()
      val confirmableOrders =
        _menuOrderWrappers.value.filter { it.dishList.first().status == DishOrderStatus.Deciding }
      val forChef =
        confirmableOrders.filter {
          !menuDishData.chefExclude.contains(it.dishList.first().dish.type)
        }
      val notForChef =
        confirmableOrders.filter {
          menuDishData.chefExclude.contains(it.dishList.first().dish.type)
        }
      val batch = FirebaseFirestore.getInstance().batch()

      forChef.forEach { wrapper ->
        wrapper.dishList.forEach { dishOrder ->
          batch.set(dishOrder.selfRef, dishOrder.copy(status = DishOrderStatus.Cooking))
        }
      }

      notForChef.forEach { wrapper ->
        wrapper.dishList.forEach { dishOrder ->
          batch.set(dishOrder.selfRef, dishOrder.copy(status = DishOrderStatus.Completed))
        }
      }
      menuRepositoryImpl.sendDishOrdersToChef(tableId, forChef, batch)
      batch.commit().addOnCompleteListener {
        viewModelScope.launch(Dispatchers.Main) {
          _isLoading.value = false
          onCompleteCallback()
        }
      }
    }
  }
}
