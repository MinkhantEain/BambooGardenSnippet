package com.example.bamboogarden.authentication

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthServiceImpl {
  val currentUser: Flow<User?>
  @SuppressLint("RestrictedApi")
  get() = callbackFlow {
    val listener = FirebaseAuth.AuthStateListener { auth ->
      this.trySend(auth.currentUser?.let { User(it.uid) })
    }

    Firebase.auth.addAuthStateListener(listener)
    awaitClose { Firebase.auth.removeAuthStateListener(listener) }
  }

  val currentUserId: String get() = Firebase.auth.currentUser?.uid.orEmpty()

  fun hasUser(): Boolean {
    return Firebase.auth.currentUser != null
  }

  suspend fun signIn(email: String, password: String) : Result<AuthResult>{
    try {
      val authResult = Firebase.auth.signInWithEmailAndPassword(email, password)
        .addOnSuccessListener {
          Log.d("Auth", "signIn: is success")
        }
        .addOnFailureListener {
          Log.d("Auth", "signIn: is failure with error ${it.message}")
        }
        .await()
      return Result.success(authResult)
    } catch (e: Exception) {
      return Result.failure(e)
    }
  }

  suspend fun signUp(email: String, password: String) {
    Firebase.auth.createUserWithEmailAndPassword(email, password).await()
  }

  suspend fun signOut() : Result<Unit> {
    return withContext(Dispatchers.IO) {
      try {
        val ret = Firebase.auth.signOut()
        return@withContext Result.success(ret)
      } catch (e : Exception) {
        return@withContext Result.failure(e)
      }
    }
  }

  suspend fun deleteAccount() {

  }
}