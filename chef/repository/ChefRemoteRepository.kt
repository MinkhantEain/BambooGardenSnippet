package com.example.bamboogarden.chef.repository

import com.example.bamboogarden.chef.data.DeletedDish
import com.example.bamboogarden.common.CHEFCOLLECTION
import com.example.bamboogarden.common.CHEF_DELETE_COLLECTION
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate

class ChefRemoteRepository {
  val firebase = FirebaseFirestore.getInstance()

  suspend fun getChefCollection(): CollectionReference {
    return withContext(Dispatchers.IO) {
      return@withContext firebase.collection(CHEFCOLLECTION)
    }
  }

  fun getDeleteDocRef(): DocumentReference {
    return firebase.collection(CHEF_DELETE_COLLECTION).document()
  }

  suspend fun getDeletedDish(date: LocalDate): List<DeletedDish> {
    return firebase.collection(CHEF_DELETE_COLLECTION).whereEqualTo("date", date.toString()).get()
      .await()
      .map { it.toObject<DeletedDish>() }.sortedBy { it.time }
  }
}
