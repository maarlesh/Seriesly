# Seriesly

A polished Android app for discovering, tracking, and rating movies and TV series — built entirely with Jetpack Compose and Material 3.

---

## Screenshots

> Add your screenshots to a `screenshots/` folder in the repo root and update the paths below.

| Search & Discover | Series Detail | Episode Tracking |
|:-----------------:|:-------------:|:----------------:|
| ![Search](screenshots/search.png) | ![Detail](screenshots/series_detail.png) | ![Episodes](screenshots/episodes.png) |

| Movie Detail | Watchlist | Profile |
|:------------:|:---------:|:-------:|
| ![Movie](screenshots/movie_detail.png) | ![Watchlist](screenshots/watchlist.png) | ![Profile](screenshots/profile.png) |

---

## Features

- **Search** — Find any movie or TV series powered by the TVDB API, with content type filtering (All / Movies / Series)
- **Discovery** — Home screen surfaces your in-progress series, recently watched movies, your ratings, and watchlists — so you always have something to jump back into
- **Series Tracking** — Expand seasons, mark individual episodes watched, or mark an entire season/series complete in one tap
- **Movie Tracking** — Mark movies as watched with a satisfying animated chip
- **Progress Overview** — Animated progress bar per series shows exactly how far through you are
- **Watchlists** — Create and manage multiple named watchlists; add any movie or series to one or more lists
- **Ratings** — Rate any title out of 5 stars with an optional comment; view all your ratings in one place
- **Celebration Moments** — Particle burst + banner animation when you complete a series
- **Profile** — Animated stats (watchlist count, total ratings) with a count-up effect
- **Offline-first** — Search results and detail pages are cached locally; your watch progress and ratings are stored on-device

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM · Unidirectional Data Flow |
| DI | Hilt 2.51 |
| Navigation | Navigation Compose 2.8 |
| Database | Room 2.6 |
| Networking | Retrofit 2.11 + OkHttp 4.12 |
| JSON | Moshi 1.15 |
| Images | Coil 2.7 |
| Security | AndroidX Security Crypto (EncryptedSharedPreferences) + JBCrypt |
| Async | Kotlin Coroutines + Flow |
| Logging | Timber |
| Testing | JUnit 4 · MockK · Turbine · Google Truth |

---

## Architecture

The project follows a **multi-module clean architecture** with strict separation of concerns:

```
app/
├── core/
│   ├── core-common        # Result types, base classes, extensions
│   ├── core-database      # Room DAOs, entities, AppDatabase
│   ├── core-domain        # Domain models, repository interfaces
│   ├── core-data          # Shared data utilities
│   ├── core-network       # Retrofit, OkHttp, TVDB API service
│   ├── core-ui            # Shared Compose components, theme, tokens
│   └── core-security      # SessionManager, EncryptedPrefs
└── feature/
    ├── feature-auth       # Login & registration screens
    ├── feature-search     # Search + discovery home
    ├── feature-detail     # Series & movie detail screens
    ├── feature-watchlist  # Watchlist list & detail
    ├── feature-progress   # My Ratings screen
    └── feature-profile    # Profile & stats screen
```

Each feature module contains its own `presentation/`, `domain/`, `data/`, `di/`, and `navigation/` packages. Feature modules depend only on `core-*` modules — never on each other.

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- JDK 17
- A free [TVDB API key](https://thetvdb.com/api-information)

### Setup

1. **Clone the repo**
   ```bash
   git clone https://github.com/maarlesh/Seriesly.git
   cd Seriesly
   ```

2. **Add your TVDB API key**

   Create a `local.properties` file in the project root (it's already in `.gitignore`):
   ```properties
   TVDB_API_KEY=your_api_key_here
   ```

3. **Build & run**

   Open the project in Android Studio and run the `app` configuration on a device or emulator running Android 8.0+ (API 26+).

---

## Requirements

- **Min SDK:** Android 8.0 (API 26)
- **Target SDK:** Android 15 (API 35)
- **Language:** Kotlin 2.0
- **Build tools:** AGP 8.5, Gradle with version catalog

---

## License

```
MIT License

Copyright (c) 2025 Seriesly

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
