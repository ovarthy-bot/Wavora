# Wavora – 2026-10-08 playback fixes

This source tree is based on the Wavora project dump supplied for this task.

## Implemented

### Phone queue
- Queue rows after the currently playing item can be dismissed horizontally with the native `SwipeToDismissBox` gesture.
- The queue-wide long-press reorder gesture is now enabled only on Desktop, so it no longer competes with horizontal swipe-to-delete on phones.
- Existing Desktop drag-reorder remains available.

### Blocking enforcement
Blocked song/artist filtering is now enforced at multiple playback entry points on Android and Desktop:
- loading catalog items into the queue
- restoring the saved queue
- `playNext`
- direct `loadMediaItem`
- direct playback from `PlayerViewModel`
- immediately after adding a song/artist to the blocked list, matching entries are removed from the active queue
- empty-artist queue entries also resolve `SongInfo.authorId` before deciding whether an artist is blocked

Removing a blocked currently-playing item uses the existing media-player queue removal behavior so playback advances to the next remaining item instead of continuing the blocked track.

### Windows Crossfade
The Windows player uses VLC (`VlcPlayerAdapter`), so the Windows fix was applied to that path rather than relying on the Android Media3 implementation.

- Removed the pre-roll/seek/unmute race for precached secondary players.
- A crossfade transition is started only once (`isCrossfading` is set before launching the coroutine).
- The incoming VLC player must enter a real playing state before it becomes audible.
- The crossfade job no longer cancels/relaunches itself during its own animation.
- The incoming player is monitored during the fade and is immediately nudged back to `play()` if VLC transiently reports it stopped while it is still inside the track.
- Old player cleanup remains centralized in `finalizeCrossfade`, preventing the previous player from remaining audible after the transition.
- The watchdog/fallback path remains active so a failed transition falls back to normal playback instead of leaving the player silent.

### Android Crossfade
- The same structured-job correction was applied to `CrossfadeExoPlayerAdapter`: the animation no longer cancels/replaces its own `crossfadeJob`.

## Verification
- Modified Kotlin source files passed brace-balance and parser-level checks with the locally available Kotlin compiler; full Gradle build was not run in this environment.
- The supplied dump intentionally omitted local/secret configuration, binaries, generated build output, and other excluded files. This ZIP is therefore the updated source project reconstructed from that dump, not a prebuilt APK/MSI.
