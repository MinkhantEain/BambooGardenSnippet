package com.example.bamboogarden.common.camera.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CameraCacheDao {
  @Upsert
  suspend fun cacheImageBitmap(photoPath: PhotoPath)

  @Query("select * from PhotoPath where id = 0 limit 1")
  fun getCachedImageBitmap() : Flow<List<PhotoPath>?>

  @Query("Delete from PhotoPath")
  suspend fun clearCameraCache()
}
