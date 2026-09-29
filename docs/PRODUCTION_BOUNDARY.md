# Moodify Production Boundary Specification

*Document Version: Phase 2-R4 Atomic Privacy & Local-Day Closure*  
*Standard: Boundary definition between current prototype client authority and required production server-side architecture.*

---

## 1. Current Client-Side Authority (Prototype Only)

In the current Vision Prototype, all application state, business logic, and security rules execute client-side within the browser. 

The authoritative persistent records currently reside exclusively in browser `window.localStorage` under seven keys:

### A. User Settings & Domain Data Storage
| Storage Key | Current Prototype Authority | Scope & Lifecycle |
|---|---|---|
| `moodify_memories` | Complete authority over user memory vault, including creation, editing, deletion, sensitivity tags, and personalization flags. | Local browser only; persists across reloads on single device/browser. |
| `moodify_taste_nodes` | Complete authority over the taste graph entities, relations (`LOVES`, `LIKES`, `AVOIDS`), and strength weights. | Local browser only; mutated by recommendation feedback. |
| `moodify_plans` | Authority over saved intentions, shopping checklists, watchlists, and simulated calendar holds. | Local browser only; mutated by user plan actions. |
| `moodify_privacy_settings` | Authority over user privacy settings (private session toggle, master personalization toggle, sensitive data permissions, category blocks). | Local browser only; read by Context Firewall. |
| `moodify_proactive_settings` | **User Settings Storage**: Authority over user-configured companion proactivity settings (intensity mode `QUIET`, `BALANCED`, `COMPANION`, quiet hours schedule, user max pings per day, category alert toggles). | Local browser only; persists user preferences across reloads. |
| `moodify_context` | Authority over durable contextual dimensions (`energy`, `stress`, `valence`, `focusNeed`), weather, and primary state. | Local browser only; updated by self-report and conversation. (During Private Session, context updates are isolated in memory and barred from writing to this key; private session exit is atomic with a restore guard, preventing transient durable writes and safely restoring durable context across reloads). |

### B. Proactivity Runtime Counter Storage
| Storage Key | Current Prototype Authority | Scope & Lifecycle |
|---|---|---|
| `moodify_proactive_runtime` | **Proactivity Runtime Counter Storage**: Ephemeral runtime dispatch tracker storing `{ date: "YYYY-MM-DD", count: number }`. Enforces the true daily limit across page reloads (cannot be bypassed by refreshing). Date authority is browser-local (`PROACTIVE_DATE_AUTHORITY = LOCAL_BROWSER_CALENDAR_DATE`) using `getLocalDateKey()`, automatically resetting `count: 0` when the local calendar day changes at midnight. Cleared on factory reset. | Local browser only; local date-aware operational runtime state. |

### Prototype Authority Warning
These client-side storage keys are **strictly temporary prototype authorities**. They are subject to local device clearing, lack cross-device synchronization, have no encryption-at-rest beyond host OS security, and provide no team or multi-user isolation.

---

## 2. Required Production Server-Side Migrations

To transition Moodify from a functional vision prototype into a production-grade, enterprise-trustworthy application, the following capabilities **MUST move server-side**:

### 1. Authentication & Identity Management
- **Current**: Hardcoded seed user (`Alex Chen`) without credential verification.
- **Production Requirement**: Robust server-side authentication (e.g., Firebase Authentication, Auth0, or Supabase Auth) issuing cryptographically signed JWT sessions with multi-factor authentication (MFA) and revocation support.

### 2. Durable & Sensitive Memory Storage
- **Current**: Raw JSON strings in `localStorage`.
- **Production Requirement**: Relational or document database (PostgreSQL with Drizzle ORM or Google Cloud Firestore) with:
  - Row-Level Security (RLS) enforcing tenant isolation.
  - **Envelope Encryption**: Field-level encryption for records graded `SENSITIVE` and `HIGHLY_SENSITIVE` using keys managed by Google Cloud KMS or AWS KMS.
  - Vector indexing (pgvector) for semantic retrieval.

### 3. Provider Tokens & Secrets Handling
- **Current**: No secrets used; integration adapters run in simulated `MOCK` mode.
- **Production Requirement**: Zero secrets or third-party OAuth access/refresh tokens stored on the client. Tokens must reside in encrypted server-side token vaults with automatic refresh rotation and scope isolation.

### 4. Gemini & LLM API Secrets
- **Current**: `@google/genai` is installed but no API calls are made from the client.
- **Production Requirement**: Server-side proxy endpoints (`/api/chat`, `/api/memory/extract`, `/api/curation`) where the `GEMINI_API_KEY` is injected strictly into container environment variables without exposing keys or raw system prompts to the client browser.

### 5. Tool Authorization & Action Execution Gate
- **Current**: Client-side Action Engine executes `MOCK_EXECUTION` and writes local state.
- **Production Requirement**: Cryptographically verifiable authorization pipeline:
  - Server issues a short-lived execution challenge (nonce) for consequential actions (`EXTERNAL_WRITE`, `PURCHASE_OR_BOOKING`).
  - Client submits user confirmation signature.
  - Server validates policy permissions, idempotency tokens, and executes the external transaction via official provider APIs.

### 6. Notification Scheduling & Proactivity Daemon
- **Current**: In-session evaluation during active browser runtime only.
- **Production Requirement**: Cloud-scheduled background worker (Google Cloud Tasks, Cloud Scheduler, or Celery) that:
  - Listens to calendar webhooks and context triggers while the app is closed.
  - Evaluates user-configured quiet hours and daily frequency caps.
  - Dispatches standard Web Push (VAPID) and APNs/FCM mobile notifications.

### 7. Cross-Device Synchronization & Conflict Resolution
- **Current**: Isolated per-browser storage.
- **Production Requirement**: Real-time database subscription or WebSocket feed with operational transformation or CRDTs for multi-device sync across desktop, tablet, and mobile.

### 8. Immutable Privacy Audit Retention
- **Current**: In-memory ring buffer of the last 20 firewall decisions.
- **Production Requirement**: Append-only audit log store in an immutable database table for compliance (GDPR/CCPA access and accountability audits).

### 9. Account Deletion Authority (Right to be Forgotten)
- **Current**: `clearAllMemories` empties local state array.
- **Production Requirement**: Server-authoritative cascading deletion pipeline purging all relational records, vector embeddings, cloud backups, and third-party synced caches within statutory deadlines.

---

## 3. Explicit Non-Implementation Note

Per the prototype scope boundaries, **no backend, cloud database, or server routes have been implemented in this phase**. 

The application remains intentionally a **proven, honest, client-side vision prototype** with transparent boundaries.
