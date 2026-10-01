package com.abubakar.sleepo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ScreenEvent::class], version = 1, exportSchema = false)
abstract class SleepoDatabase : RoomDatabase() {
    abstract fun screenEventDao(): ScreenEventDao

    companion object {
        @Volatile private var INSTANCE: SleepoDatabase? = null

        fun get(context: Context): SleepoDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    SleepoDatabase::class.java,
                    "sleepo.db"
                ).build()
                INSTANCE = db
                db
            }
        }
    }
}
