<div align="center">
  <img src="composeApp/appimage/wavora.png" alt="Wavora logo" width="140"/>

  # Wavora

  **A free, open-source, cross-platform YouTube Music client — built with Kotlin Multiplatform & Compose.**

  [![Version](https://img.shields.io/badge/version-1.1.0-00D4FF?style=flat-square)](https://github.com/Wavora-dev/Wavora/releases)
  [![License](https://img.shields.io/badge/license-GPL--3.0-blue?style=flat-square)](LICENSE)
  [![Kotlin](https://img.shields.io/badge/Kotlin-2.4.0-7F52FF?style=flat-square&logo=kotlin&logoColor=white)](https://kotlinlang.org)
  [![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.11.1-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white)](https://www.jetbrains.com/lp/compose-multiplatform/)
  [![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin%20Multiplatform-Android%20%7C%20Desktop-7F52FF?style=flat-square)](https://kotlinlang.org/docs/multiplatform.html)
  [![Platform: Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=flat-square&logo=android&logoColor=white)](https://github.com/Wavora-dev/Wavora/releases)
  [![Platform: Desktop](https://img.shields.io/badge/Desktop-Windows%20%7C%20macOS%20%7C%20Linux-0078D4?style=flat-square&logo=windows&logoColor=white)](https://github.com/Wavora-dev/Wavora/releases)
  [![GitHub Repo](https://img.shields.io/badge/GitHub-Wavora--dev%2FWavora-181717?style=flat-square&logo=github)](https://github.com/Wavora-dev/Wavora)
  [![Stars](https://img.shields.io/github/stars/Wavora-dev/Wavora?style=flat-square&color=yellow)](https://github.com/Wavora-dev/Wavora/stargazers)
  [![Forks](https://img.shields.io/github/forks/Wavora-dev/Wavora?style=flat-square)](https://github.com/Wavora-dev/Wavora/network/members)
  [![Issues](https://img.shields.io/github/issues/Wavora-dev/Wavora?style=flat-square)](https://github.com/Wavora-dev/Wavora/issues)

  [![Descargas Android](https://img.shields.io/endpoint?url=https://wavora-badges.wavora-lyrics.workers.dev/badge/android&style=flat-square)](https://github.com/Wavora-dev/Wavora/releases)
  [![Descargas Windows](https://img.shields.io/endpoint?url=https://wavora-badges.wavora-lyrics.workers.dev/badge/windows&style=flat-square)](https://github.com/Wavora-dev/Wavora/releases)
  [![Descargas Android TV](https://img.shields.io/endpoint?url=https://wavora-badges.wavora-lyrics.workers.dev/badge/androidtv&style=flat-square)](https://github.com/Wavora-dev/Wavora/releases)
  [![Descargas totales](https://img.shields.io/endpoint?url=https://wavora-badges.wavora-lyrics.workers.dev/badge/total&style=flat-square)](https://github.com/Wavora-dev/Wavora/releases)

  [Releases](https://github.com/Wavora-dev/Wavora/releases) · [Report a bug](https://github.com/Wavora-dev/Wavora/issues) · [Español ↓](#-wavora-en-español)

</div>

---

<div align="center">
  <sub>🇺🇸 English</sub>
</div>

## Hero

Wavora is a music player that streams directly from YouTube Music — no ads, no subscription, no account required to start listening. It runs natively on **Android**, **Windows**, **macOS**, and **Linux** from a single Kotlin Multiplatform codebase, sharing virtually all of its business logic, data layer, and UI between every target.

The project's philosophy is simple: **one shared brain, native everywhere.** Instead of a web wrapper or a Java-to-JS bridge, Wavora's playback engine, database, networking layer, and 20+ full screens of Compose UI are the *same Kotlin code* running against ExoPlayer/Media3 on Android and libVLC on desktop — with only the thin platform edges (`expect`/`actual`) written per target.

Wavora's goal isn't to be "yet another YouTube Music wrapper." It exists to push a self-hosted, community-driven alternative to paid streaming: real crossfade with DJ-style mixing, a synced-lyrics ecosystem with AI translation, a genuinely native desktop app (not Electron), and a codebase that's continuously audited and hardened rather than left to bit-rot.

## Why Wavora?

**The problem:** most "free YouTube Music" clients are either Android-only, poorly maintained forks that break every few months, or Electron wrappers on desktop that eat gigabytes of RAM to play an MP3.

**What Wavora does differently:**

- **Genuinely native on every platform.** The desktop build isn't a browser in a box — it's a Compose Desktop application backed by libVLC through JNA/vlcj, with a real system tray, a floating always-on-top miniplayer window, and OS-level media integrations (macOS Now Playing Center, Windows protocol handler for `wavora://` deep links). The one deliberate exception: on Windows, logging into YouTube Music opens a real, lazily-loaded embedded Chromium browser (via [KCEF](https://github.com/DatL4g/KCEF)) purely to capture the session cookie — it's disposed right after a successful login, so it isn't a persistent Electron-style engine sitting in the background.
- **A real crossfade engine**, not a fade-in/fade-out hack — equal-power volume curves, an optional biquad-filter DJ Mode, and BPM/key-aware AutoMix, implemented in parallel on both the ExoPlayer (Android) and VLC (Desktop) backends.
- **A community lyrics ecosystem** with its own Cloudflare Workers backend, AI-powered translation, and a voting system — instead of only scraping a single third-party source.
- **No telemetry by default**, an optional non-Sentry crash-reporting build flavor (`crashlytics-empty`), proxy support, and a Piped-instance fallback for restricted networks.
- **A codebase that's actively being hardened**, not just feature-added: memory leaks, race conditions, main-thread database access, and startup performance have all been systematically found and fixed (see [What's new compared to SimpMusic](#whats-new-compared-to-simpmusic)).

## Features

### 🎵 Playback
- Stream any song, album, playlist, or podcast from YouTube Music
- Background playback with a persistent media-session notification (Android) and system tray (Desktop)
- **Crossfade** between tracks, configurable from 1–30 seconds or fully automatic
  - **DJ Mode** — biquad low-pass/high-pass filter sweep layered on top of the volume fade
  - **AutoMix** — reads BPM/key metadata to adjust crossfade duration and blend tempo
- Next-track precaching for gapless-style transitions
- Repeat (off / one / all), shuffle, and an endless queue (auto-radio once the queue ends)
- Sleep timer (fixed minutes or "end of current song")
- Skip-silence and SponsorBlock (automatic sponsor-segment skipping on music videos)
- **Android**: optional toggle to keep playing through other apps' audio (ignores transient/permanent audio focus loss) — for when Instagram, a game, or any other app grabs the system's audio focus and you don't want Wavora to pause
- **Android**: **Google Cast (Chromecast)** support — cast to any TV or Cast-ready speaker, with automatic switching between local and remote playback and crossfade disabled while casting
- Loudness normalization across tracks
- Selectable audio/video quality tiers
- Offline downloads for songs, videos, full albums, and full playlists, with an "audio-only for video tracks" toggle
- Playback state (track, queue, shuffle/repeat) is restored across app restarts

### 🎤 Lyrics
- **Wavora community lyrics** — synced lyrics served by Wavora's own Cloudflare Workers backend, with an opt-in "help build the database" contribution flow
- **LRCLIB** and **BetterLyrics** as additional open fallback providers
- **Spotify** synced lyrics via the user's own `sp_dc` cookie
- **YouTube** auto-generated captions as a last-resort fallback
- **AI translation** of any synced lyric set to any language, via OpenAI, Gemini, or a custom model endpoint
- Word-level highlight for rich synced lyrics, with binary-search line lookup
- Upvote/downvote voting on original and translated lyrics
- Blurred, fullscreen lyrics view; lyrics are cached for offline access

### ❤️ Library
- Favorites (liked songs), downloaded tracks, followed artists
- Local and YouTube-synced albums and playlists — create, edit, reorder, delete
- Recently played, most played, and top tracks/artists/albums with a date-range filter (analytics)
- Podcasts
- Two-way sync of liked music and playlists with your YouTube Music account
- Offline "keep" — cache YouTube playlists for offline browsing without downloading every track

### 🔍 Search & Discovery
- Search songs, albums, artists, playlists, videos, and podcasts, filterable by type
- Search history shown as quick-tap chips
- Real-time YouTube Music search suggestions
- Moods & genres browser, personalized recommendations on the home feed
- YouTube-style radio ("start radio" from any track)

### 🤖 AI
- AI-powered lyrics translation (OpenAI, Gemini, or self-hosted/custom model endpoint), configurable per-provider with your own API key
- Community-sourced BPM/key metadata feeding the AutoMix crossfade engine

### ☁ Cloud & Social Integration
- **YouTube / Google account** login (cookie-based, no OAuth app registration needed), with multi-account support and listening-history upload for better recommendations — on Android via the system WebView, on Windows via a lazily-loaded embedded Chromium browser that's disposed right after a successful login
- **Discord Rich Presence** — shows the current track, artist, and elapsed time in your Discord status via a direct WebSocket gateway connection (uses your own user token, not a bot)
- **Spotify integration** — `sp_dc` cookie login for synced lyrics and animated Spotify Canvas backgrounds on the Now Playing screen

### 🎨 UI & Personalization
- Material 3 (Expressive) design system with full light/dark theming that follows the OS
- Liquid Glass blur/refraction effect on supported Android versions
- Animated, album-art-driven blurred background on the Now Playing screen
- Translucent, configurable bottom navigation bar
- Animated 3-page onboarding flow shown once on first launch
- Skeleton loading states and retry-capable error states across Artist/Album/Library/Playlist screens
- Haptic feedback on every playback control (Android)
- Configurable app language and content region

### 📱 Platform
- **Android** — background service with media-session notification, home-screen widget (Glance), Discord/Spotify/YouTube login flows, **Google Cast (Chromecast)** support — cast to any TV or Cast-ready speaker, with automatic switching between local and remote playback and crossfade disabled while casting
- **Android TV** — separate build (own product flavor and APK) with a Leanback launcher entry and remote-control-friendly focus navigation across the app
- **Desktop (Windows / macOS / Linux)** — standalone windowed app, floating draggable miniplayer window, full-screen player, custom title bar, scrollbars, VLC-based playback (libVLC)
  - macOS Now Playing Center + Remote Command Center (media keys, lock-screen widget)
  - Windows custom protocol handler (`wavora://`) for deep links
  - System tray integration
  - YouTube Music login on Windows via a lazily-loaded embedded Chromium browser (KCEF), just to capture the session cookie — no persistent browser engine left running afterward

### 💾 Data
- Full backup and restore of library, playlists, and settings (zip export/import)
- Automatic backups on a configurable schedule (daily / weekly / monthly)
- Granular cache management: player cache, thumbnail cache, lyrics-Canvas cache, downloaded-files cache
- Backward-compatible with pre-existing SimpMusic local databases

### 🛡 Privacy & Network
- No analytics and no telemetry by default
- A separate build flavor (`crashlytics-empty`) that removes Sentry crash reporting entirely — for users who want zero outbound diagnostic traffic
- HTTP/SOCKS5 proxy support with authentication, for restricted network environments
- Optional Piped-instance backend as an alternative streaming data source
- Opt-in-only contribution to the community lyrics database

### ⚙ Configuration
- Manual and automatic update checks with a toggle for the auto-check-on-launch behavior
- Optional Android "keep background service alive" setting to reduce playback interruptions from battery optimization
- Per-feature toggles for crossfade, SponsorBlock, normalization, silence-skip, quality tiers, and more, all under Settings

## What's new compared to SimpMusic

Wavora started as a fork of [SimpMusic](https://github.com/maxrave-dev/SimpMusic) — the original Android YouTube Music client this project owes its playback and data foundations to. But over the course of development, the codebase went through a sustained audit-and-rewrite cycle rather than a simple rebrand, and a large share of the app has since been rewritten, replaced, or newly built.

**What was inherited from SimpMusic:** the original architectural approach (Compose + Media3 + Room + Koin + a YouTube Music scraping layer), the core screen set, and the general product concept (ad-free YouTube Music streaming with downloads and a library).

**What was rebuilt into a genuinely multiplatform app:**
- A full **Kotlin Multiplatform / Compose Multiplatform desktop target** was added from scratch — Windows, macOS, and Linux, with a VLC-based playback backend (`core/media/media-jvm`) written in parallel to the Android ExoPlayer/Media3 backend (`core/media/media3`), behind a shared `PlayerSession`/`PlayerController` abstraction so the rest of the app (ViewModels, screens) talks to a single unified playback contract regardless of platform.
- The **crossfade engine** (equal-power volume curves, DJ Mode's biquad filter sweep, and BPM/key-aware AutoMix) was implemented twice, independently, once per platform's native player.
- The **lyrics backend** was migrated off a dead first-party API to a new Cloudflare Workers service (D1 + KV + Queues + a Durable Object), with a clean provider-registry architecture (`core/service/lyricsService`) so Wavora's own lyrics, LRCLIB, and BetterLyrics are pluggable fallbacks instead of hardcoded calls.
- The monolithic ~2000-line `SharedViewModel` was split into focused, single-responsibility ViewModels (`PlayerViewModel`, `NowPlayingViewModel`, `AppViewModel`) coordinated by a thin backward-compatible `SharedViewModel` facade, so every existing screen kept working unchanged.
- Discord Rich Presence, Spotify integration, and YouTube account login all gained **guided, in-app, step-by-step setup flows** (cookie copy helpers, connection diagnostics) instead of requiring manual browser DevTools work.
- A from-scratch **onboarding flow**, skeleton loading states, retry-capable error states, and a consistent haptic-feedback layer were added across the UI.
- A **home-screen Android widget** (Glance) and a **desktop floating miniplayer window** were both added as new surfaces that didn't exist before.

**What was fixed, hardened, and optimized:**
- A production out-of-memory leak in the Koin-singleton ViewModel graph (fixed with an explicit `forceClear()` lifecycle hook)
- Multiple `LazyColumn`/`LazyRow` duplicate-key crashes on both platforms
- Database access moved off the main thread across all repositories
- A cross-platform race condition where the next track in a crossfade could start already paused
- ExoPlayer buffer sizing halved (was buffering up to ~800 s of audio across 4 simultaneous player instances)
- Startup-time work audited and deferred/lazily-loaded where it wasn't needed on the critical path
- Typography and color-token duplication consolidated into single sources of truth
- Room indices added for frequently-filtered columns (`liked`, `downloadState`, `totalPlayTime`, `inLibrary`)

**What was newly added, with no SimpMusic equivalent:** the entire desktop application, the AI lyrics translation pipeline, the community lyrics voting system, SponsorBlock integration, backup/restore with scheduled auto-backup, analytics-style top tracks/artists/albums with date filtering, Discord/Spotify guided setup, and the `wavora://` deep-link protocol handler on Windows.

None of this is meant to diminish SimpMusic — it's the foundation this project was built on, and its architectural choices (Compose, Media3, Koin, Room) are still visible throughout Wavora today. The goal of this section is simply to be transparent about how far the codebase has moved since the fork.

## Architecture

Wavora is a **Kotlin Multiplatform** project. Shared Kotlin code targets both `android` and `jvm` (desktop) from a single source tree, with platform-specific code isolated behind `expect`/`actual` declarations only where truly unavoidable (media playback backend, file pickers, clipboard, window chrome, etc.).
