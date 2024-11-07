package com.example.bamboogarden.management.expense

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.common.EXPENSECOLLECTION
import com.example.bamboogarden.common.camera.data.CameraDatabase
import com.example.bamboogarden.common.camera.rotateBitmap
import com.example.bamboogarden.management.expense.data.Expense
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

class AddExpenseScreenViewModel(
  private val cameraDatabase: CameraDatabase = BambooGardenApplication.appModule.cameraDatabase
) : ViewModel() {
  private val cloudStorage = Firebase.storage
  val TAG = "AddExpenseScreenViewModel"
  val hasImage = mutableStateOf(true)
  val hasQuantity = mutableStateOf(true)
  var image: Bitmap? = null
  val isLoading = mutableStateOf(false)
  val about = mutableStateOf("မနက်ပိုင်း")
  val comment = mutableStateOf("")
  val quantity = mutableStateOf("")
  val amount = mutableStateOf("")
  val error = mutableStateOf<String?>(null)
  val showConfirmationDialog = mutableStateOf(false)

  init {
    viewModelScope.launch(Dispatchers.IO) {
      listenToImage()
    }
  }

  private fun successfulSubmission() {
    Toast.makeText(BambooGardenApplication.instance, "Add Successful", Toast.LENGTH_SHORT).show()
    hasImage.value = true
    hasQuantity.value = true
    image = null
    about.value = "မနက်ပိုင်း"
    comment.value = ""
    quantity.value = ""
    amount.value = ""
  }

  private suspend fun listenToImage() {
    cameraDatabase.cameraCache.getCachedImageBitmap().collect {
      if (!it.isNullOrEmpty()) {
        it.first().let { imageBitmap ->
          imageBitmap.filePath.let { it1 ->
            val file = File(it1)
            if (file.exists()) {
              val bitmap = BitmapFactory.decodeFile(file.absolutePath)
              image = bitmap
              Log.d(TAG, "listenToImage: Image retrieved")
            }
          }
        }
      } else {
        Log.d(TAG, "listenToImage: no image")
      }
    }
  }

  fun checkSubmitability() : Boolean {
    if (comment.value.isEmpty()) {
      error.value = "အကြောင်းရာ cannot be empty"
      return false
    }
    if (amount.value.isEmpty()) {
      error.value = "ရောင်းရငွေ(ကျပ်) cannot be empty"
      return false
    }
    if (hasQuantity.value && quantity.value.isEmpty()) {
      error.value = "အရေတွက်/အလေးချိန် is empty, uncheck if there isn't any."
      return false
    }
    if (hasImage.value && image == null) {
      error.value = "no picture taken"
      return false
    }
    return true
  }

  fun submitExpenseReport() {
    isLoading.value = true
    viewModelScope.launch(Dispatchers.IO) {
      val collectionRef = FirebaseFirestore.getInstance().collection(EXPENSECOLLECTION())
      val docRef = collectionRef.document()
      if (hasImage.value && image!=null) {
        val filePathRef =
          cloudStorage.reference.child(LocalDate.now().toString()).child(LocalTime.now().toString())
        image?.let { img ->
          val baos = ByteArrayOutputStream()
          img.rotateBitmap(90).compress(Bitmap.CompressFormat.JPEG, 30, baos)
          filePathRef.putBytes(baos.toByteArray())
        }
        val expense = Expense(
          date = LocalDate.now().toString(),
          time = LocalTime.now().toString(),
          about = about.value,
          comment = comment.value,
          amount = if (amount.value.isEmpty()) 0 else amount.value.toInt(),
          quantity = if (quantity.value.isEmpty()) 0 else quantity.value.toInt(),
          photoPath = filePathRef.path
        )
        docRef.set(expense.copy(selfRef = docRef))
      } else {
        val expense = Expense(
          date = LocalDate.now().toString(),
          time = LocalTime.now().toString(),
          about = about.value,
          comment = comment.value,
          amount = if (amount.value.isEmpty()) 0 else amount.value.toInt(),
          quantity = if (quantity.value.isEmpty()) 0 else quantity.value.toInt(),
          photoPath = ""
        )
        docRef.set(expense.copy(selfRef = docRef))
      }
      cameraDatabase.cameraCache.clearCameraCache()
    }.invokeOnCompletion {
      isLoading.value = false
      showConfirmationDialog.value = false
      viewModelScope.launch(Dispatchers.Main) {
        successfulSubmission()
      }
    }
  }
}
