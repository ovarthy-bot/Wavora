package com.wavora.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playback_session")
data class PlaybackSessionEntity(
    @PrimaryKey val id: Int = 1,
    val videoId: String,
    val positionMs: Long,
    val timestamp: Long
)