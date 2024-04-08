package com.example.movieapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.movieapp.api.models.Media

@Database(
    entities = [
        Media::class
    ],
    version = 1
)
abstract class MediaDatabase : RoomDatabase() {

    abstract fun getMediaDao() : MediaDao

    companion object {
        @Volatile
        private var instance : MediaDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, MediaDatabase::class.java, "medias_db.db").build()

    }
}