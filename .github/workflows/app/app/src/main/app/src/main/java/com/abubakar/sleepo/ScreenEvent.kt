package com.abubakar.sleepo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "screen_events")
data class ScreenEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val type: String // "ON" or "OFF"
)
