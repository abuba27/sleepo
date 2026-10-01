package com.abubakar.sleepo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScreenEventDao {
    @Insert
    suspend fun insert(event: ScreenEvent)

    @Query("SELECT * FROM screen_events ORDER BY timestamp DESC LIMIT 200")
    fun getAll(): Flow<List<ScreenEvent>>

    @Query("DELETE FROM screen_events")
    suspend fun clearAll()
}
