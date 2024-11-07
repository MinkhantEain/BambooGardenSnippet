package com.example.bamboogarden.chef

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.chef.repository.ChefRemoteRepository
import com.example.bamboogarden.common.RingtonePlayer
import com.example.bamboogarden.menu.data.ChefOrder
import com.example.bamboogarden.menu.data.ChefOrderWithWrapper
import com.example.bamboogarden.menu.data.DishOrder
import com.example.bamboogarden.menu.data.DishOrderStatus
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChefScreenViewModel(private val repo: ChefRemoteRepository) : ViewModel() {
    private val TAG = "ChefScreenViewModel"
    private lateinit var listenerRegistration: ListenerRegistration

    private val _orders: MutableState<List<ChefOrderWithWrapper>> = mutableStateOf(listOf())
    val orders: State<List<ChefOrderWithWrapper>> = _orders

    private val ringtonePlayer = RingtonePlayer()

    private var count = 0

    init {

        subscribeToChefOrder()
    }

    override fun onCleared() {
        if (this::listenerRegistration.isInitialized) listenerRegistration.remove()
        ringtonePlayer.release()
        super.onCleared()
    }

    private fun subscribeToChefOrder() {
        viewModelScope.launch(Dispatchers.IO) {
            listenerRegistration =
                repo
                    .getChefCollection()
                    .addSnapshotListener { snapshot, error ->
                        error?.let { Log.d(TAG, "subscribeToChefOrder: ${error.message}") }

                        snapshot?.let {
                            _orders.value =
                                it.documents.map { docSnap -> docSnap.toObject<ChefOrder>()!!.withWrapper() }.sortedBy { c -> c.time }
                            if (_orders.value.size > count) {
                                ringtonePlayer.playRingtone()
                            }
                            count = _orders.value.size
                        }
                    }
        }
    }

    fun completeChefOrder(chefOrderWithWrapper: ChefOrderWithWrapper) {
        viewModelScope.launch(Dispatchers.IO) {
            chefOrderWithWrapper.orderList.forEach { dishOrder ->
                dishOrder.forEach { it.selfRef.set(it.copy(status = DishOrderStatus.Completed)) }
            }
            chefOrderWithWrapper.selfRef.delete()
        }
    }

    fun removeChefOrder(dish: DishOrder) {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }

    fun getDeleteDocRef(): DocumentReference {
        return repo.getDeleteDocRef()
    }
}
