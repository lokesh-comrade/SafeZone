# SafeZone: Real-Time Women Safety Monitoring & Emergency Response System

A production-grade, two-tier autonomous women’s safety and emergency response ecosystem following the **Jagruthi Design Paradigm**. 

SafeZone delivers real-time breadcrumb tracking, multi-modal emergency dispatch (5-Shake, Acoustic Keyword, Covert Steganographic Calculator, and 3-Second Hold), WebRTC ambient live video/audio streaming via PeerJS, and a desktop tactical dispatch console for police units and guardians.

---

## 🌟 Key Architecture & Capabilities

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                            SafeZone Ecosystem                               │
├──────────────────────────────────────┬──────────────────────────────────────┤
│               Tier 1:                │               Tier 2:                │
│    Universal Mobile Safety Client    │      Tactical Dispatch Console       │
│              (app.html)              │            (monitor.html)            │
├──────────────────────────────────────┼──────────────────────────────────────┤
│ • Jagruthi Design Paradigm           │ • High-Contrast Dark Tactical Theme  │
│ • PWA / iOS Safari Standalone        │ • 10-Digit Mobile Interception Bar   │
│ • Native Android Studio APK Bridge   │ • Full-Screen Leaflet Tactical Map   │
│ • 4 Multi-Modal Emergency Triggers   │ • Pulsating Marker Radar Beacons     │
│ • Dedicated 3s Hold SOS Screen       │ • Breadcrumb Polyline Route History  │
│ • Live PeerJS Ambient Surveillance   │ • Surveillance PiP WebRTC Player     │
│ • Active Corridor & Safe Zones       │ • Haversine Velocity Telemetry (km/h)│
│ • Offline / Firestore Backplane      │ • 1-Click Google Maps Intercept Nav  │
└──────────────────────────────────────┴──────────────────────────────────────┘
```

---

## 🎨 Visual & UX Specifications (Jagruthi Design Paradigm)
- **Palette**:
  - Background: Soft-Rose `#FFF5F7`
  - Cards: Pure White `#FFFFFF` with `#FFE4E8` borders
  - Primary Accent: Deep Crimson `#E11D48` (dark hover `#BE123C`)
  - Status Indicators: Emerald Green `#059669` (bg `#ECFDF5`, border `#A7F3D0`)
- **Typography**: Plus Jakarta Sans & Inter; 20px–24px rounded corners; clean elevation shadows.
- **Multi-Screen Views**:
  1. **Welcome Screen:** Insignia, tagline (*"Safer Steps • Brighter Tomorrow"*), hero graphics, headline (*"Your Safety, Our Priority"*), and *"Get Started →"* action.
  2. **Home Dashboard:** Top greeting bar with avatar, unread alert bell, covert calculator stealth icon, voice listener pill, live GPS status banner, current journey mini-route visualizer with dynamic ETA, and 4-Card Action Grid:
     - 🚨 **Emergency SOS** (direct to dispatch screen)
     - 🔗 **Share Live Location** (instant Google Maps coordinates link)
     - 🧭 **Journey Monitor** (active route deviation tracking)
     - 🏰 **Safe Zones** (Home, College, Hostel geofenced perimeters: 200m–500m)
  3. **Dedicated Emergency SOS Screen:** Layered pulsating crimson target button with **3-second hold gesture** and circular SVG progress ring, accompanied by real-time checklist verification:
     - [✔️] Live GPS coordinates streaming
     - [✔️] SMS sent to emergency contacts
     - [✔️] Incident alert dispatched to police monitoring unit
     - [✔️] Audio/Video recording stream initiated (PeerJS `safezone-{userPhone}`)
     - Auxiliary controls: Loud Emergency Siren (85dB synthesized), Strobe Flashlight, and Stand Down PIN dialogue (default `1234`).
  4. **Journey Screen:** Interactive Leaflet route map, speed diagnostics, and route corridor deviation alarm.
  5. **Contacts Screen:** Emergency contacts manager with direct dialing and SMS hooks.
  6. **Safe Zones Screen:** Geofenced perimeters with customizable radiuses (200m–500m).
  7. **Profile & Settings:** Personal safety details, hardware sensor diagnostics, and cloud sync settings.
  8. **Covert Steganographic Calculator:** A functional arithmetic calculator that secretly triggers emergency dispatch when evaluating `9999=`.

---

## ⚡ Hardware Sensors & Multi-Modal Trigger Protocols

1. **5-Shake Kinematic Accelerometer Sensor:**
   - Evaluates \( |\Delta a| = \sqrt{a_x^2 + a_y^2 + a_z^2} \).
   - Detects 5 rapid kinematic shakes within a 2-second rolling window.
   - Dispatches emergency SOS via `SHAKE_5X` both natively (`MainActivity.java`) and in mobile browsers.
2. **Acoustic Keyword Detection Daemon:**
   - Continuous background speech recognition listening for distress keywords (*"Help"*, *"Emergency"*, *"Bachao"*, *"Save me"*, *"Police"*).
   - Dispatches SOS via `VOICE_KEYWORD` immediately upon recognition.
3. **Covert Steganographic Calculator (PIN `9999=`):**
   - Disguised as a standard calculator. Entering `9999` and pressing `=` silently triggers SOS with `COVERT_PIN_9999` without flashing alarms or alerting an assailant.
4. **Live Ambient Video Broadcast:**
   - PeerJS WebRTC audio/video broadcast to room `safezone-{userPhone}` (e.g. `safezone-9876543210`).
   - Latches automatically onto the control room's PiP player for real-time video surveillance.

---

## 🖥️ Tier 2: Dedicated Monitoring Console (`monitor.html`)
- **Interception Bar:** Real-time search and filter by victim's 10-digit mobile number.
- **Tactical Map Viewport:** Interactive full-screen Leaflet/OpenStreetMap engine with breadcrumb movement polylines, auto-panning, and custom pulsating marker radar beacons.
- **Surveillance PiP Window:** Resizable ambient video player with real-time video/audio playback, snapshot evidence capture with forensic watermark, and audio toggle.
- **Tactical Telemetry HUD:**
  - Real-time kinematic velocity (km/h) via Haversine calculation.
  - GPS precision accuracy radius in meters.
  - Battery percentage indicator.
  - 1-Click Google Maps intercept navigation for police units.
  - Patrol dispatch (PCR Van 07) and remote siren broadcast controls.

---

## ☁️ Cloud Backplane & Data Schema
- **Firebase Firestore:**
  - Project ID: `women-safety-platform-243f9`
  - Collection: `alerts/{victimPhone}`
  - Schema:
    - `victimPhone` (String): 10-digit mobile identifier
    - `lat` (Number): Latitude
    - `lng` (Number): Longitude
    - `accuracy` (Number): GPS precision in meters
    - `trigger` (String): `MANUAL_HOLD_3S` | `SHAKE_5X` | `VOICE_KEYWORD` | `COVERT_PIN_9999`
    - `is_alert_active` (Boolean): Active state toggle
    - `timestamp` (ServerTimestamp): Cloud synchronization time
- **Multi-Tab / Offline Fallback:** Synchronized via `BroadcastChannel('safezone_sync')` and `localStorage` for instant testing across windows.

---

## 🚀 Running the Project

### 1. Launch the Unified HTTP Server
```bash
python server.py
```
- Open Mobile App: `http://localhost:8080/app/app.html`
- Open Tactical Console: `http://localhost:8080/monitor/monitor.html`

### 2. Native Android Studio Project
- Open `safezone/android` in **Android Studio**.
- Run `gradlew assembleDebug` to build `app-debug.apk`.
- The native app packages `app.html` with hardware acceleration, `AndroidBridge` for direct SMS/Call intents, and hardware accelerometer polling.
