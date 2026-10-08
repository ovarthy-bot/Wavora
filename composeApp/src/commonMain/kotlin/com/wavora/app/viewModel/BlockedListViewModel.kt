package com.wavora.app.viewModel

import androidx.lifecycle.viewModelScope
import com.wavora.domain.model.entities.ArtistEntity
import com.wavora.domain.model.entities.SongEntity
import com.wavora.domain.repository.ArtistRepository
import com.wavora.domain.repository.SongRepository
import com.wavora.app.viewModel.base.BaseViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class BlockedListState(
    val blockedSongs: List<SongEntity> = emptyList(),
    val blockedSongIds: List<String> = emptyList(), // IDs for songs not in DB
    val blockedArtists: List<ArtistEntity> = emptyList(),
    val blockedArtistIds: List<String> = emptyList(), // IDs for artists not in DB
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
class BlockedListViewModel(
    private val songRepository: SongRepository,
    private val artistRepository: ArtistRepository,
) : BaseViewModel() {

    private val _state = MutableStateFlow(BlockedListState())
    val state: StateFlow<BlockedListState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Collect blocked song IDs → map to SongEntity from DB
            launch {
                songRepository.getBlockedSongIdsFlow()
                    .flatMapLatest { ids ->
                        songRepository.getSongsByListVideoId(ids).map { songs ->
                            Pair(ids, songs)
                        }
                    }
                    .collect { (ids, songs) ->
                        _state.value = _state.value.copy(
                            blockedSongs = songs,
                            blockedSongIds = ids.filter { id -> songs.none { it.videoId == id } },
                            isLoading = false,
                        )
                    }
            }

            // Collect blocked artist IDs → map to ArtistEntity from DB
            launch {
                songRepository.getBlockedArtistIdsFlow()
                    .flatMapLatest { ids ->
                        artistRepository.getAllArtists(10000).map { allArtists ->
                            val found = allArtists.filter { it.channelId in ids }
                            Pair(ids, found)
                        }
                    }
                    .collect { (ids, artists) ->
                        _state.value = _state.value.copy(
                            blockedArtists = artists,
                            blockedArtistIds = ids.filter { id -> artists.none { it.channelId == id } },
                        )
                    }
            }
        }
    }

    fun unblockSong(videoId: String) {
        viewModelScope.launch {
            songRepository.unblockSong(videoId)
        }
    }

    fun unblockArtist(channelId: String) {
        viewModelScope.launch {
            songRepository.unblockArtist(channelId)
        }
    }
}