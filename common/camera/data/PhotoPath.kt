package com.example.bamboogarden.common.camera.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
//@TypeConverters(value = [BitmapTypeConverter::class])
data class PhotoPath(@PrimaryKey val id: Int = 0, val filePath: String)

//class BitmapTypeConverter {
//    @TypeConverter
//    fun bitmapToBase64(bitmap: Bitmap): String {
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos)
//        val byteArray = baos.toByteArray()
//        return Base64.encodeToString(byteArray, DEFAULT)
//    }
//
//    @TypeConverter
//    fun base64ToBitmap(base64String: String): Bitmap {
//        val byteArray = Base64.decode(base64String, DEFAULT)
//        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
//    }
//}
