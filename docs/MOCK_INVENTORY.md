# Moodify Mock & Simulation Inventory

*Document Version: Phase 2-R4 Atomic Privacy & Local-Day Closure*  
*Standard: Complete architectural disclosure of all synthetic data, fixtures, simulated delays, and local abstractions.*

---

## Overview

The Moodify functional prototype relies on carefully structured client-side mocks and deterministic heuristics to demonstrate the complete user experience—from context detection and memory extraction to taste graph learning and action authorization—without requiring live third-party API accounts, paid LLM credits, or cloud database provisioning during testing.

Below is an exhaustive inventory of every mock, fixture, simulated delay, and local storage state in the codebase.

---

## 1. User Identity & Authentication Mock

- **What is Mocked**: User account (`Alex Chen`, `usr_alex_chen_92`), profile photo, time zone, and role (`Staff Product Designer`).
- **Why it Exists**: Enables immediate, realistic personalization on first load without forcing the evaluator through a sign-up/login gate.
- **Where Implemented**: `src/data/seedUser.ts` (`SEED_USER`), rendered in `src/components/Navigation.tsx` and `src/components/YouView.tsx`.
- **Production Replacement**: Firebase Authentication (or OAuth2 with Google/Apple) storing user records in a secure cloud database (e.g., Cloud SQL PostgreSQL or Firestore) with session JWTs.

---

## 2. Seed Personal Memory Vault

- **What is Mocked**: 13 initial structured memory records covering identity, lakefront running routines, savory food preferences, snack budget ($25 limit), tonkotsu ramen fondness, ambient focus music, intimate indie venue aversion to arenas, cinema taste, partner Maya, Marcus quarterly review, evening quiet boundary, family recovery in Portland, and Kyoto travel memories.
- **Why it Exists**: Provides an immediate baseline so the user does not have to spend 30 days chatting before experiencing the Personal Context Firewall, memory filtering, and recommendations.
- **Where Implemented**: `src/data/seedUser.ts` (`SEED_MEMORIES`), managed in `src/context/AppContext.tsx` with browser `localStorage` persistence under key `moodify_memories`.
- **Production Replacement**: A dedicated Memory Microservice with encrypted vector and relational storage (e.g., PostgreSQL with pgvector or Firestore), supporting automated asynchronous memory formation, confidence decay over time, and user confirmation workflows.

---

## 3. Seed Context & Mood Snapshot

- **What is Mocked**: The initial contextual vector: `valence: 0.1`, `energy: 0.35` (low battery), `stress: 0.65`, `socialNeed: 0.25`, `focusNeed: 0.2`, `primaryState: 'LOW_BATTERY'`, `secondaryState: 'Cognitive fatigue after 4-hour design review'`, `weatherSummary: '62°F, overcast with gentle rain'`, free evening hours: 4.5.
- **Why it Exists**: Instantiates the "Friday Evening Wind-Down" state, immediately testing the system's sensitivity to cognitive depletion.
- **Where Implemented**: `src/data/seedUser.ts` (`INITIAL_CONTEXT`), managed in `src/context/AppContext.tsx` with browser `localStorage` persistence under `moodify_context`.
- **Production Replacement**: Real-time context aggregator ingesting calendar events, local weather APIs (e.g. OpenWeatherMap), activity/sleep telemetry from health platforms (Apple HealthKit / Google Health Connect), and explicit user check-ins.

---

## 4. LLM & Conversational Response Generation

- **What is Mocked**: Generative conversational output (`LIVE_GEMINI_CALL_COUNT = 0`). Responses for scenarios (Rough Day, Friday Night Live Show, Savory Snacks, Marcus Review Follow-up, Movie Night, Concert Radar, Comfort Food Under Budget) are orchestrated through rule-based heuristic routing in `AgentOrchestrator`.
- **Why it Exists**: Guarantees deterministic, reliable demonstration of the agent's tone, structure, why-this citations, and firewall gating during evaluation, without risk of API latency, rate limits, or non-deterministic hallucinations.
- **Where Implemented**: `src/services/agentOrchestrator.ts` (`AgentOrchestrator.processUserMessage`).
- **Production Replacement**: Server-side Gemini 2.5 Flash / Pro API calls wrapped with system prompts enforcing minimal-purpose context assembly, zero-manipulation companionship rules, and strict JSON output schemas.

---

## 5. Simulated Agent "Thinking" Latency

- **What is Mocked**: A 500ms delay (`await new Promise(res => setTimeout(res, 500))`) before dispatching agent chat replies.
- **Why it Exists**: Emulates realistic network and neural inference pacing so the user interface transitions smoothly through the `isThinking` state with animated typing indicators.
- **Where Implemented**: `src/context/AppContext.tsx` (`sendMessage`).
- **Production Replacement**: Real network round-trip time of streamed Server-Sent Events (SSE) from the backend LLM service.

---

## 6. Seed Taste Graph (Nodes & Edges)

- **What is Mocked**: 9 initial entity nodes (Jon Hopkins, Brian Eno, Severance, Past Lives, Spicy Tonkotsu Ramen, Edamame snacks, etc.) with pre-established strengths and directed affinity edges (`COMPLEMENTS`, `SIMILAR_TO`).
- **Why it Exists**: Demonstrates the cross-domain taste graph visualization and allows immediate testing of feedback adaptation (e.g. clicking "Not for me" updates a node to `AVOIDS`).
- **Where Implemented**: `src/data/seedUser.ts` (`SEED_TASTE_NODES`, `SEED_TASTE_EDGES`), updated dynamically by `src/services/tasteService.ts` and persisted in `localStorage` under `moodify_taste_nodes`.
- **Production Replacement**: A graph database (Neo4j or PostgreSQL graph extension) trained on collaborative filtering, latent entity embeddings, and explicit user preference nodes.

---

## 7. Curated Recommendation Candidates & Why-This Narratives

- **What is Mocked**: 
  - Recommendation candidates across 7 domains are pre-curated archetypes.
  - While the **mathematical scoring breakdown** (`tasteMatch`, `contextMatch`, `constraintMatch`, `noveltyScore`, `recencyAdjustment`, `repetitionPenalty`) is computed dynamically at runtime (`FUNCTIONAL_LOCAL_ONLY`), the **narrative text blocks** (`whyThis.summary`, bullet lists) are partly seeded fixtures (`PARTIAL`).
- **Why it Exists**: Ensures that all recommendations presented during founder inspection meet the aesthetic and architectural standards described in the product thesis without requiring external web scraping.
- **Where Implemented**: `src/data/seedUser.ts` (`SEED_RECOMMENDATIONS`), `src/services/recommendationService.ts`.
- **Production Replacement**: Live catalog ingestion with runtime LLM synthesis of narrative explanations directly grounded in admitted memory IDs and mathematical score components.

---

## 8. Provider Integration Adapters

- **What is Mocked**: Status indicators for 5 external integration providers:
  - Google Calendar (`MOCK` — confirmed in `src/data/seedUser.ts`)
  - Spotify (`MOCK`)
  - Apple Music (`MOCK`)
  - Instacart (`MOCK`)
  - Bandsintown / Songkick (`MOCK`)
- **Why it Exists**: Demonstrates the Integrations Hub UI and the progressive disclosure/permission model while explicitly labeling each adapter as `MOCK` rather than falsely claiming live connectivity.
- **Where Implemented**: `src/data/seedUser.ts` (`SEED_INTEGRATIONS`), `src/components/YouView.tsx` (Integrations tab), `src/types/integrations.ts`.
- **Production Replacement**: Real OAuth 2.0 integration clients (Google Workspace OAuth with incremental scopes, Spotify Web API PKCE auth, Apple MusicKit tokenization, etc.) executing authenticated read/write transactions.

---

## 9. Calendar Write Execution (Action Engine)

- **What is Mocked**: Action execution mode is strictly `MOCK_EXECUTION`. External calendar synchronization is strictly false (`isCalendarSynced: false`).
- **Why it Exists**: Demonstrates the 5-tier Action Risk hierarchy, the mandatory Action Confirmation Modal for `EXTERNAL_WRITE`, and the subsequent creation of an internal plan item without falsely reporting external calendar writes.
- **Where Implemented**: `src/services/actionService.ts` (`executeAction`), `src/components/modals/ActionConfirmModal.tsx`, `src/types/actions.ts`.
- **Result Output**: Explicitly states: `"Mock calendar action completed locally. No external Google Calendar event was created."`
- **Production Replacement**: Google Calendar API `events.insert` endpoint called from an authenticated backend proxy using the user's delegated OAuth token.

---

## 10. Proactive Follow-Up Generator & Frequency Runtime

- **What is Mocked**: In-session proactive message generation and companion settings.
- **Why it Exists**: Demonstrates non-manipulative proactive check-in UI (`isProactive: true`, origin pill, suggested replies) within a single browser session.
- **Daily Frequency Limit & Runtime State**:
  - Runtime dispatch tracker persisted under `moodify_proactive_runtime` storing `{ date: "YYYY-MM-DD", count: number }`.
  - **Local Calendar Day Date Authority**: `PROACTIVE_DATE_AUTHORITY = LOCAL_BROWSER_CALENDAR_DATE`. Uses `ProactiveService.getLocalDateKey()` (browser-local year, month, and day) instead of UTC `toISOString()`, unifying the temporal authority of Quiet Hours and daily notification limit reset at local midnight.
  - Date-aware reset on local calendar day change (resets `count: 0` if stored date != current local date).
  - Strictly enforces `effectiveMax = min(settings.maxPingsPerDay, modeSafetyCap)` (Quiet: 0, Balanced: 3, Companion: 5).
  - **Survives Browser Reload**: Cannot be bypassed by refreshing the page.
- **User Settings Persistence**: User adjustments to proactivity intensity mode (`QUIET`, `BALANCED`, `COMPANION`), quiet hours, and category toggles persist separately in browser storage under `moodify_proactive_settings`.
- **Where Implemented**: `src/services/proactiveService.ts`, `src/context/AppContext.tsx`.
- **Production Replacement**: A cloud-scheduled background worker (e.g. Cloud Tasks or Celery cron) evaluating calendar webhooks, checking quiet hours schedules, and dispatching Web Push Notifications or mobile push notifications.

---

## 11. Synthetic ID Generation

- **What is Mocked**: Pseudo-random client IDs for memories, actions, and audit logs using `Date.now() + Math.random().toString(36)`.
- **Why it Exists**: Generates collision-free identifiers for client state management without database auto-incrementing sequences.
- **Where Implemented**: `src/services/memoryService.ts`, `src/services/firewallService.ts`, `src/services/tasteService.ts`, `src/context/AppContext.tsx`.
- **Production Replacement**: Server-generated UUIDv4 or KSUID keys assigned upon database write.

---

## 12. Local Browser Storage Persistence

- **What is Mocked**: Persistence mechanism. Data is persisted to client-side `window.localStorage` under seven keys, categorized into:
  - **User Settings & Domain Data Storage (6 keys)**:
    1. `moodify_memories`: Memory vault items, sensitivities, and personalization flags.
    2. `moodify_taste_nodes`: Taste graph entities and affinity relations.
    3. `moodify_plans`: Saved action plans and checklists.
    4. `moodify_privacy_settings`: User privacy configurations.
    5. `moodify_proactive_settings`: Companion proactivity intensity and schedule preferences.
    6. `moodify_context`: Durable context snapshot (shielded from private session mutations; pre-private context is restored upon session exit atomically with zero transient durable writes via restore guard, and survives reload).
  - **Proactivity Runtime Counter Storage (1 key)**:
    7. `moodify_proactive_runtime`: Runtime frequency state `{ date: string, count: number }` enforcing the true daily dispatch cap across browser reloads using local calendar day authority (`getLocalDateKey()`).
- **Why it Exists**: Guarantees that evaluator actions (adding a memory, editing preferences, toggling proactivity settings, deleting records, saving plans) survive page refreshes and browser reloads on the same machine, without requiring a remote database server.
- **Where Implemented**: `src/context/AppContext.tsx` (`useEffect` sync and state initializers), `src/services/proactiveService.ts`.
- **Production Replacement**: Remote authenticated database backend (PostgreSQL with Drizzle ORM or Firebase Firestore) with optimistic client cache and real-time syncing.

---

## Summary of Mock Authenticity Invariants

1. **`GOOGLE_CALENDAR_RUNTIME_STATUS = MOCK`**: No authenticated Google Calendar OAuth/API connection exists.
2. **`REAL_CALENDAR_API_CALL_PRESENT = false`**: No network call to Google Calendar API.
3. **`CALENDAR_ACTION_EXECUTION_MODE = MOCK_EXECUTION`**: Confirmed actions are tagged `MOCK_EXECUTION`.
4. **`CALENDAR_PLAN_IS_CALENDAR_SYNCED = false`**: Plan items have `isCalendarSynced: false`.
5. **`LIVE_GEMINI_CALL_COUNT = 0`**: No live LLM inference in the prototype.
6. **`REAL_EXTERNAL_INTEGRATION_COUNT = 0`**: All external providers operate in local mock mode.
7. **`APPLICATION_LEVEL_ENCRYPTION_IMPLEMENTED = false`**: Application-level envelope encryption is not implemented; UI honestly displays NOT IMPLEMENTED IN LOCAL PROTOTYPE.
8. **`IMMUTABLE_AUDIT_LOG_IMPLEMENTED = false`**: Firewall audit logs are local in-memory bounded ring buffer (last 20 entries).
9. **`SERVER_SECRET_VAULT_IMPLEMENTED = false`**: No server backend exists in local prototype.
10. **`CRISIS_RESPONSE_FLOW_IMPLEMENTED = false`**: Clinical terms filtered via regex; no automated crisis escalation pipeline.
11. **`PROACTIVE_RUNTIME_PERSISTED = true`**: Frequency cap persists in `moodify_proactive_runtime` across browser refreshes.
12. **`PROACTIVE_DATE_AUTHORITY = LOCAL_BROWSER_CALENDAR_DATE`**: Daily limit reset uses local browser calendar day via `getLocalDateKey()`, matching local quiet hours.
13. **`PRIVATE_SESSION_RELOAD_SAFE = true`**: Pre-private context snapshot is preserved across reload; private session context never leaks into durable storage.
14. **`PRIVATE_SESSION_EXIT_ATOMIC = true`**: Private session exit atomically restores pre-private context snapshot and disables private session in a single coordinated transition.
15. **`PRIVATE_CONTEXT_TRANSIENT_DURABLE_WRITE = false`**: Generic context persistence is guarded from ever writing private context to durable storage during session exit.
