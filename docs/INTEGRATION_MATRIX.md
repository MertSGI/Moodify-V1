# Moodify — Integration Matrix & Tool Adapters

## 1. Provider Transparency Policy

**Moodify never misrepresents the status of an external provider.**

Every integration adapter explicitly publishes its live runtime status to both the system architecture and the user interface:
- **`LIVE`:** Real, authenticated API connection active.
- **`MOCK`:** Realistic local mock adapter simulating live schema, rate limits, and payloads for prototype testing without vendor lock-in.
- **`NOT_CONNECTED`:** Available adapter currently unlinked by user choice.
- **`UNAVAILABLE`:** Provider offline or network restricted.

---

## 2. Integration Catalog

| Provider | Category | Current Status | Auth Model | Risk Level | Capabilities |
|---|---|---|---|---|---|
| **Google Calendar** | Calendar & Timing | **MOCK** | Simulated Adapter (OAuth 2.0 in target production) | `READ_ONLY` / `EXTERNAL_WRITE` | Read schedule gaps, detect free evenings, schedule event holds (simulated locally as `MOCK_EXECUTION` with `isCalendarSynced: false`). |
| **Universal Music Adapter** | Audio & Streaming | **MOCK** | Provider Agnostic (Apple Music / Spotify) | `READ_ONLY` | Query track acoustic features, match tempo/energy, export playlists. |
| **TMDB Catalog** | Cinema & Television | **MOCK** | REST API Token | `READ_ONLY` | Runtime constraints, streaming provider availability, visual tone metadata. |
| **Ticketmaster Discovery** | Live Events & Concerts | **MOCK** | Developer API Key | `READ_ONLY` | 500-cap venue filtering, indie tour announcements, ticket presale alerts. |
| **Local Places & Dining** | Places & Food | **MOCK** | Geocoding & Places API | `READ_ONLY` | Authentic ramen counters, quiet work spots, walkability radius checks. |
| **Product & Grocery Catalog** | Shopping & Supplies | **MOCK** | E-commerce Search API | `READ_ONLY` | Budget constraints (<$25), clean ingredient checks, savory workday snack curation. |
| **Health Connect / Apple Health** | Physical Vitals | **NOT_CONNECTED** | Device Native SDK | `READ_ONLY` | **Strictly optional, off by default.** Sleep recovery context only. Zero medical diagnosis. |

---

## 3. Action Engine Risk Hierarchy

All actions proposed by Moodify are classified under a strict 5-tier risk taxonomy:

```
[ READ_ONLY ]
  - Read calendar availability slots
  - Query public movie or concert catalog
  - Action is purely observational; zero user footprint

[ REVERSIBLE ]
  - Save item to internal Watch Later list
  - Add snack item to local Shopping List
  - Can be undone with 1 tap; internal state change only

[ EXTERNAL_WRITE ] ⚠️ REQUIRES EXPLICIT USER CONFIRMATION
  - Create event or hold on Google Calendar
  - Send email or notification to contact
  - Modifies external user accounts; requires confirmation modal

[ PURCHASE_OR_BOOKING ] 🚨 STRICT CONFIRMATION & AUTH GATE
  - Buy concert ticket
  - Reserve restaurant table
  - Initiate food delivery order
  - Autonomous purchasing strictly forbidden in V1

[ SENSITIVE_ACTION ] 🛡️ HIGH-SECURITY DISCLOSURE
  - Export full memory vault
  - Disclose real-time physical GPS coordinates
```

---

## 4. Why Universal Music over Spotify Monopoly

Modern consumer agents must not tie their core architecture to a single music vendor:
- Spotify developer policies have restricted data portability and prohibited using metadata for external AI training.
- Apple Music / MusicKit offers privacy-friendly client-side playback.
- Moodify utilizes an abstract `MusicProvider` interface allowing the underlying streaming provider to be swapped without rewriting taste models.
