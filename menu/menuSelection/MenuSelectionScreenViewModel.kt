package com.example.bamboogarden.menu.menuSelection

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.menu.data.DishOrder
import com.example.bamboogarden.menu.data.MenuDish
import com.example.bamboogarden.menu.repository.MenuRepositoryImpl
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MenuSelectionScreenViewModel(
    private val menuRepository: MenuRepositoryImpl,
    private val tableId: String,
) : ViewModel() {
    private val TAG = "MenuSelectionScreenModel"
    private lateinit var listenerRegistration: ListenerRegistration
    val customerSearchInput = mutableStateOf("")

    private val _menuDishes = mutableStateOf<List<MenuDish>>(listOf())
    val menuDishes: State<List<MenuDish>> = _menuDishes

    private val _categoryList = mutableStateOf<List<String>>(listOf())
    val categoryList: State<List<String>> = _categoryList

    private val _selectedDishes = mutableStateOf<List<DishOrder>>(listOf())
    val selectedDishes: State<List<DishOrder>> = _selectedDishes

    private val _commentDialogController: MutableState<CommentDialogController> =
        mutableStateOf(CommentDialogController())
    val commentDialogController: State<CommentDialogController> = _commentDialogController

    init {
        viewModelScope.launch(Dispatchers.IO) {
            menuRepository.syncDatabases()
            _menuDishes.value = menuRepository.getMenuDishes().filter { it.name.length > 3 }
            _categoryList.value = menuRepository.getMenuDishData().categoryList
        }

        subscribeToSelectedDishes()
    }

    override fun onCleared() {
        if (this::listenerRegistration.isInitialized) listenerRegistration.remove()
        super.onCleared()
    }

    fun updateTablePresence() {
        viewModelScope.launch { menuRepository.updateTablePresence(tableId) }
    }

    fun searchFilter(userInput: String) {
        viewModelScope.launch { _menuDishes.value = menuRepository.getMenuDishContains(userInput) }
    }

    fun getFilteredMenuDishByType(chosenType: String) {
        viewModelScope.launch { _menuDishes.value = menuRepository.getMenuDishOfType(chosenType) }
    }

    fun getPopularMenuDishes() {
        viewModelScope.launch { _menuDishes.value = menuRepository.getPopularMenuDishes() }
    }

    fun selectMenuDish(menuDish: MenuDish, comment: String) {
        viewModelScope.launch {
            val dishOrder = DishOrder(dish = menuDish)
            menuRepository.selectMenuDish(tableId, dishOrder, comment)
        }
    }

    fun clearCommentDialogController() {
        _commentDialogController.value = CommentDialogController()
    }

    private fun subscribeToSelectedDishes() {
        listenerRegistration =
            menuRepository
                .getOrderedDishCollection(tableId)
                .addSnapshotListener(
                    EventListener { snapshot, error ->
                        error?.let { Log.d(TAG, "subscribeToSelectedCount: ${error.message}") }

                        snapshot?.let {
                            _selectedDishes.value =
                                it.documents.map { docSnap -> docSnap.toObject<DishOrder>()!! }
                        }
                    }
                )
    }
}
