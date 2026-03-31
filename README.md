# NovaDrive – Futuristic In-Vehicle Navigation App

A futuristic **Android in-vehicle navigation app** built with **Jetpack Compose**, **MapLibre**, and **Android MediaSession**. Designed for a centre-console display with a dark glossy UI, neon-blue accents, and a 3D map perspective.

---

## Screenshots / Layout

```
┌────────────────────────────────────────────────────────┐
│  🤖  AI Navigator  ·  "Turn right on Omotesando…"     │   ← frosted-glass top bar
├────────────────────────────────────────────────────────┤
│                                                        │
│                                                        │
│           MapLibre Map (3D camera tilt 50°)            │
│           animating along fake Tokyo route             │
│                                                        │
│  ┌──────┐ ┌─────────┐                    [ Start Demo ]│
│  │  60  │ │ETA 12:38│                                  │
│  │ km/h │ │2.4 km   │                                  │
│  └──────┘ └─────────┘                                  │
├────────────────────────────────────────────────────────┤
│  🎵  Midnight Drive  ·  Nova Synth  ·  Neon Roads      │   ← media card
│  ◀◀   ▶  ▶▶                          ━━━━━━━━━━○────  │
└────────────────────────────────────────────────────────┘
```

---

## Project Structure

```
app/src/main/java/com/example/navdemo/
├── NovaDriveApp.kt                   # Application – MapLibre init
├── MainActivity.kt                   # Single activity entry point
│
├── data/
│   ├── model/
│   │   ├── NavState.kt               # Navigation snapshot (position, speed, ETA…)
│   │   ├── MediaState.kt             # Media playback snapshot
│   │   └── RoutePoint.kt             # LatLng + bearing + instruction per waypoint
│   └── FakeRouteData.kt              # Demo route (Shinjuku → Daikanyama, Tokyo)
│
├── viewmodel/
│   ├── NavViewModel.kt               # Drives route animation, exposes NavState flow
│   └── MediaViewModel.kt             # MediaSession bridge, exposes MediaState flow
│
├── service/
│   └── MediaPlaybackService.kt       # MediaBrowserServiceCompat + MediaSessionCompat
│
└── ui/
    ├── theme/
    │   ├── Color.kt                  # Neon-blue dark palette
    │   ├── Theme.kt                  # MaterialTheme wrapper
    │   └── Type.kt                   # Typography scale
    ├── screen/
    │   └── NavScreen.kt              # Full-screen Compose layout
    └── components/
        ├── MapLibreView.kt           # AndroidView wrapper with lifecycle & camera anim
        ├── AiAssistantBar.kt         # Top frosted-glass AI message bar
        ├── MediaCard.kt              # Bottom media card (YouTube Music style)
        └── SpeedHud.kt               # Speed + ETA HUD overlay
```

---

## Key Design Decisions

| Requirement | Decision |
|---|---|
| No / minimal API keys | **MapLibre** open-source SDK + [demotiles.maplibre.org](https://demotiles.maplibre.org) (free, no key) |
| MapLibre over Mapbox | ✅ `org.maplibre.gl:android-sdk:11.0.0` |
| 3D camera tilt | `CameraPosition.Builder().tilt(50.0)` + smooth `animateCamera()` |
| MVVM architecture | `NavViewModel` + `MediaViewModel` expose `StateFlow` collected in Compose |
| Media integration | `MediaBrowserServiceCompat` + `MediaSessionCompat` in `MediaPlaybackService` |
| Frosted-glass UI | Semi-transparent `Brush.verticalGradient` + neon-blue `border` on `RoundedCornerShape` |
| Fake route demo | 16-waypoint Tokyo route in `FakeRouteData`; coroutine advances position every 3 s |
| Extensibility | AI bar, MediaViewModel, and NavViewModel have clear extension points for voice/STT and driver monitoring |

---

## Gradle Dependencies

```kotlin
// MapLibre – open-source, no API key required
implementation("org.maplibre.gl:android-sdk:11.0.0")

// Jetpack Compose BOM
implementation(platform("androidx.compose:compose-bom:2024.06.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.material:material-icons-extended")

// ViewModel + Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")

// MediaSession
implementation("androidx.media:media:1.7.0")
```

---

## How to Run

1. Open the project root in **Android Studio Hedgehog (2023.1+)** or later.
2. Sync Gradle (`File → Sync Project with Gradle Files`).
3. Run on a **physical device or API 26+ emulator**.
4. Tap **"Start Demo"** (bottom-right FAB) to begin the fake Tokyo route animation.
5. Tap **▶** on the media card to start/pause the fake track.

> **Landscape orientation** is enforced via `AndroidManifest.xml` for the in-vehicle centre-display feel.

---

## Extending the App

### Voice Assistant
- Collect a `Flow<String>` from a speech-to-text library (e.g. Google ASR, Whisper) and pass it to `AiAssistantBar(navInstruction = …)`.
- `NavViewModel` already exposes `nextInstruction` for the current waypoint instruction.

### Driver Monitoring
- Add a `DriverMonitorViewModel` that processes a camera feed (MediaPipe FaceMesh) and emits `drowsinessLevel: Float` as a `StateFlow`.
- Overlay a warning banner in `NavScreen` when `drowsinessLevel > 0.7`.

### Real Maps / Routing
- Replace `MAP_STYLE_URL` in `MapLibreMapView.kt` with any MapLibre-compatible style URL (e.g. OpenFreeMap, self-hosted Tileserver GL, or MapTiler free tier).
- Swap `FakeRouteData` with a call to OSRM / Valhalla / GraphHopper (all free, self-hosted routing engines) to get real turn-by-turn routes.

---

## Architecture Diagram

```
MainActivity
    │
    ├─── NovaDriveTheme
    │        └─── NavScreen (Composable)
    │                 ├─── MapLibreMapView    ← observes NavState
    │                 ├─── AiAssistantBar     ← observes NavState.nextInstruction
    │                 ├─── SpeedHud           ← observes NavState.speedKmh / eta
    │                 └─── MediaCard          ← observes MediaState
    │
    ├─── NavViewModel (MVVM)
    │        └─── FakeRouteData → coroutine → NavState (StateFlow)
    │
    └─── MediaViewModel (MVVM)
             └─── MediaBrowserCompat ──→ MediaPlaybackService
                                              └─── MediaSessionCompat
```
