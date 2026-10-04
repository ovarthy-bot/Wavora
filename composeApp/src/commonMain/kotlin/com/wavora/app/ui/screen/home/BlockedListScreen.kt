package com.wavora.app.ui.screen.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.RemoveCircleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.wavora.app.ui.theme.LocalAppTypography
import com.wavora.app.ui.theme.wavoraPrimary
import com.wavora.app.viewModel.BlockedListViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import wavora.composeapp.generated.resources.Res
import wavora.composeapp.generated.resources.holder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlockedListScreen(
    navController: NavController,
    viewModel: BlockedListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf("Şarkılar", "Sanatçılar")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Engellenenler Listesi",
                        style = LocalAppTypography.current.titleMedium,
                        color = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = wavoraPrimary,
                    )
                },
            ) {
                tabs.forEachIndexed { index, title ->
                    val count = when (index) {
                        0 -> state.blockedSongs.size + state.blockedSongIds.size
                        else -> state.blockedArtists.size + state.blockedArtistIds.size
                    }
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = if (count > 0) "$title ($count)" else title,
                                style = LocalAppTypography.current.bodyMedium,
                                color = if (selectedTab == index) Color.White else Color.Gray,
                            )
                        },
                    )
                }
            }

            AnimatedContent(
                targetState = selectedTab,
                modifier = Modifier.fillMaxSize(),
            ) { tab ->
                when (tab) {
                    0 -> BlockedSongsTab(
                        songs = state.blockedSongs,
                        songOnlyIds = state.blockedSongIds,
                        isLoading = state.isLoading,
                        onUnblock = { videoId ->
                            viewModel.unblockSong(videoId)
                            scope.launch {
                                snackbarHostState.showSnackbar("Şarkı engeli kaldırıldı")
                            }
                        },
                    )
                    1 -> BlockedArtistsTab(
                        artists = state.blockedArtists,
                        artistOnlyIds = state.blockedArtistIds,
                        onUnblock = { channelId ->
                            viewModel.unblockArtist(channelId)
                            scope.launch {
                                snackbarHostState.showSnackbar("Sanatçı engeli kaldırıldı")
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun BlockedSongsTab(
    songs: List<com.wavora.domain.model.entities.SongEntity>,
    songOnlyIds: List<String>,
    isLoading: Boolean,
    onUnblock: (String) -> Unit,
) {
    if (!isLoading && songs.isEmpty() && songOnlyIds.isEmpty()) {
        EmptyBlockedList(message = "Engellenen şarkı yok")
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(songs, key = { it.videoId }) { song ->
            BlockedSongRow(
                title = song.title,
                artist = song.artistName?.joinToString(", "),
                thumbnailUrl = song.thumbnails,
                onUnblock = { onUnblock(song.videoId) },
            )
        }
        // Songs that are blocked but not in library (show by ID)
        items(songOnlyIds, key = { "id_$it" }) { id ->
            BlockedSongRow(
                title = id,
                artist = "Kütüphanede bulunmuyor",
                thumbnailUrl = null,
                onUnblock = { onUnblock(id) },
            )
        }
    }
}

@Composable
private fun BlockedArtistsTab(
    artists: List<com.wavora.domain.model.entities.ArtistEntity>,
    artistOnlyIds: List<String>,
    onUnblock: (String) -> Unit,
) {
    if (artists.isEmpty() && artistOnlyIds.isEmpty()) {
        EmptyBlockedList(message = "Engellenen sanatçı yok")
        return
    }

    LazyColumn(
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items(artists, key = { it.channelId }) { artist ->
            BlockedArtistRow(
                name = artist.name,
                thumbnailUrl = artist.thumbnails,
                onUnblock = { onUnblock(artist.channelId) },
            )
        }
        items(artistOnlyIds, key = { "id_$it" }) { id ->
            BlockedArtistRow(
                name = id,
                thumbnailUrl = null,
                onUnblock = { onUnblock(id) },
            )
        }
    }
}

@Composable
private fun BlockedSongRow(
    title: String,
    artist: String?,
    thumbnailUrl: String?,
    onUnblock: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Thumbnail or placeholder
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            if (thumbnailUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = null,
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.MusicNote,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = LocalAppTypography.current.labelMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!artist.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = artist,
                    style = LocalAppTypography.current.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        IconButton(onClick = onUnblock) {
            Icon(
                imageVector = Icons.Rounded.RemoveCircleOutline,
                contentDescription = "Engeli kaldır",
                tint = wavoraPrimary,
            )
        }
    }
}

@Composable
private fun BlockedArtistRow(
    name: String,
    thumbnailUrl: String?,
    onUnblock: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            if (thumbnailUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalPlatformContext.current)
                        .data(thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    error = null,
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = name,
            style = LocalAppTypography.current.labelMedium,
            color = Color.White,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )

        IconButton(onClick = onUnblock) {
            Icon(
                imageVector = Icons.Rounded.RemoveCircleOutline,
                contentDescription = "Engeli kaldır",
                tint = wavoraPrimary,
            )
        }
    }
}

@Composable
private fun EmptyBlockedList(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Block,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(56.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = LocalAppTypography.current.bodyMedium,
                color = Color.Gray,
            )
        }
    }
}
