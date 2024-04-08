package com.example.movieapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.movieapp.api.models.Media

@Dao
interface MediaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(media: Media) : Long

    @Query("SELECT * FROM medias")
    fun getAllFavorite() : LiveData<List<Media>>

    @Delete
    suspend fun delete(media: Media)

    @Query("DELETE FROM medias WHERE mediaId = :mediaId")
    suspend fun deleteById(mediaId: String)
}