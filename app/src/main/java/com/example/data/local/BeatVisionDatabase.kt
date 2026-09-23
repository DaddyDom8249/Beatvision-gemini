package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProjectEntity::class], version = 1, exportSchema = false)
abstract class BeatVisionDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao

    companion object {
        @Volatile
        private var INSTANCE: BeatVisionDatabase? = null

        fun getDatabase(context: Context): BeatVisionDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BeatVisionDatabase::class.java,
                    "beatvision_lite.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
