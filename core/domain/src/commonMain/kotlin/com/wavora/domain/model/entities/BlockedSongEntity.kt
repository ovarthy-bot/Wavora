package com.wavora.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "blocked_song")
data class BlockedSongEntity(
    @PrimaryKey val videoId: String,
    val blockedAt: LocalDateTime
)
