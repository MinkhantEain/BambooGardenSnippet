package com.example.bamboogarden.common.camera.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PhotoPath::class], version = 4, exportSchema = false)
abstract class CameraDatabase: RoomDatabase() {
  abstract val cameraCache : CameraCacheDao
}
