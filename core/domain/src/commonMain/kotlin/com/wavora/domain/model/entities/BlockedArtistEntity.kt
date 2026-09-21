package com.wavora.domain.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime

@Entity(tableName = "blocked_artist")
data class BlockedArtistEntity(
    @PrimaryKey val channelId: String,
    val blockedAt: LocalDateTime
)
