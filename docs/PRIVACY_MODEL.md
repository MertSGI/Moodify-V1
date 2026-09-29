# Moodify — Privacy & Trust Model

*Document Version: Phase 2-R4 Atomic Privacy & Local-Day Closure*  
*Standard: Explicit architectural separation between current prototype truth and target production architecture.*

---

## PART 1: CURRENT PROTOTYPE IMPLEMENTATION (TRUTH DISCLOSURE)

### 1. Architectural Truth Invariants

```
LIVE_GEMINI_CALL_COUNT = 0
REAL_EXTERNAL_INTEGRATION_COUNT = 0
IMMUTABLE_AUDIT_LOG_IMPLEMENTED = false
APPLICATION_LEVEL_ENCRYPTION_IMPLEMENTED = false
SERVER_SECRET_VAULT_IMPLEMENTED = false
CRISIS_RESPONSE_FLOW_IMPLEMENTED = false
PRIVATE_SESSION_EXIT_ATOMIC = true
PRIVATE_CONTEXT_TRANSIENT_DURABLE_WRITE = false
PROACTIVE_DATE_AUTHORITY = LOCAL_BROWSER_CALENDAR_DATE
```

The current Moodify build is a **client-side functional vision prototype**. It operates entirely within the user's browser, utilizing deterministic TypeScript engines, heuristic pattern matching, and browser `localStorage`. No cloud backend, database, or remote server routes currently exist.

---

### 2. The Personal Context Firewall (Current Prototype)

The **Personal Context Firewall** (`src/services/firewallService.ts`) is functional locally as an inspectable demonstration of minimal-purpose context assembly:

1. **Task Intent Classification**: Matches user message intents to relevant functional domains (e.g., dining, music, calendar hold).
2. **Minimal-Purpose Context Assembly**: Evaluates active candidate memories and admits only domain-essential records (e.g., for grocery hauls: admitting `food_texture` and `snack_budget`, while stripping `profession`, `design_review_marcus`, and `kyoto_japan_trips`).
3. **Sensitivity Gating**: Records tagged `HIGHLY_SENSITIVE` (e.g., family surgery) are automatically blocked from general recommendation ingestion.
4. **Audit Logging (Local & Bounded)**:
   - **Current Reality**: Firewall audit logs are **local, in-memory, bounded (retains only the 20 most recent decisions), and non-immutable**.
   - Audit logs are NOT cryptographically signed or written to an immutable append-only ledger; they re-populate ephemerally as interactions occur in the active session.

---

### 3. Ephemeral Private Session Boundary, Atomic Exit & Reload Safety (Current Prototype)

Moodify provides a 1-tap "Private Session" toggle in the navigation header and Privacy Center:

- **Candidate Memory Extraction Blocked**: Any preference, dislike, or routine stated during a Private Session is ignored by `MemoryService`.
- **Durable Vault Commits Suppressed**: No memories can be added or committed to the permanent memory store while active.
- **Taste Learning Suppressed**: Recommendation feedback (`LIKE_IT`, `LOVE_IT`, `NOT_FOR_ME`) does not modify `TasteNode` weights or edge relations.
- **Durable Context Isolation & Reload Safety**:
  - Upon activating a Private Session, the pre-private durable context snapshot is preserved in memory.
  - While active, context state may adapt ephemerally for in-session conversational continuity, but **private session context is strictly barred from being written to durable `localStorage['moodify_context']`**.
  - **Browser Reload Safety**: If the page is reloaded while Private Session is active (`isPrivateSession: true`), the pre-private durable context snapshot is safely re-initialized from the untouched durable context in `moodify_context`.
- **Atomic Private Session Exit (`PRIVATE_SESSION_EXIT_ATOMIC = true`)**:
  - Private session exit is handled atomically inside `updatePrivacySettings` rather than across competing asynchronous effects.
  - Upon exiting Private Session (`isPrivateSession: false`):
    1. The pre-private durable context A is resolved from `prePrivateContextRef` (or durable fallback).
    2. A restore guard ref (`isRestoringFromPrivateRef = true`) is armed.
    3. Context A is immediately written to durable `moodify_context`.
    4. React context state is updated to A.
    5. `isPrivateSession` is set to `false` in the same logical transition.
    6. Generic context persistence is guarded by `isRestoringFromPrivateRef` and never receives or writes private context C to durable storage (`PRIVATE_CONTEXT_TRANSIENT_DURABLE_WRITE = false`).
  - Guarantees that private session context **NEVER becomes durable or leaks into localStorage**, including during the exact millisecond of session exit or across browser reloads.

---

### 4. Storage & Security Posture (Current Prototype)

- **Storage Reality**: All user data is stored as unencrypted JSON in browser `window.localStorage` across seven distinct keys:
  - **User Settings & Domain Data Storage**:
    - `moodify_memories`: Memory Vault records
    - `moodify_taste_nodes`: Taste Graph entities & relations
    - `moodify_plans`: User action plans and checklists
    - `moodify_privacy_settings`: Privacy preferences
    - `moodify_proactive_settings`: User proactivity configuration (mode, quiet hours, user max pings)
    - `moodify_context`: Durable context snapshot (shielded from private session writes; atomically protected upon exit)
  - **Proactivity Runtime Counter Storage**:
    - `moodify_proactive_runtime`: Tracks `{ date: string, count: number }` to enforce true daily frequency limits across reloads. Date authority is strictly browser-local (`PROACTIVE_DATE_AUTHORITY = LOCAL_BROWSER_CALENDAR_DATE`) using `getLocalDateKey()`, synchronizing daily reset with local midnight and quiet hours.
- **Application-Level Encryption Status**: `APPLICATION_LEVEL_ENCRYPTION_IMPLEMENTED = false`. The UI explicitly displays `NOT IMPLEMENTED IN LOCAL PROTOTYPE` for application-level encryption.
- **Secret Management Status**: `SERVER_SECRET_VAULT_IMPLEMENTED = false`. Because no backend application server exists in the prototype, external API keys and secrets are neither hosted server-side nor stored in the client. External integration adapters operate in simulated `MOCK` mode.

---

### 5. Medical & Clinical Non-Pathologization (Current Prototype)

- **Diagnostic Prohibition**: Moodify never diagnoses, classifies, or labels medical or mental health disorders.
- **Clinical Term Exclusion**: In `MemoryService.ts`, user statements mentioning clinical psychiatric, medical, or pharmacology terms are automatically tagged `DO_NOT_STORE` and discarded.
- **Crisis Response Status**: `CRISIS_RESPONSE_FLOW_IMPLEMENTED = false`. The current prototype does not implement an automated clinical distress detection or crisis redirection flow (e.g. 988 Lifeline handoff).

---

## PART 2: TARGET PRODUCTION ARCHITECTURE (FUTURE SPECIFICATION)

The following architectural specifications represent the target production design required when transitioning to a full-stack, enterprise-grade deployment:

### 1. Server-Side Context Firewall Middleware
- The Personal Context Firewall will execute as a server-side pre-inference middleware before constructing any LLM prompt payload.
- Tokenized sanitized context payloads will be injected into Gemini context windows with strict mathematical justification signatures.

### 2. Application-Level Envelope Encryption (Target Design)
- Sensitive memory records (`SENSITIVE` and `HIGHLY_SENSITIVE`) will be encrypted at the application layer prior to database persistence.
- Cryptographic envelopes will utilize client-derived keys or customer-managed keys (Google Cloud KMS / AWS KMS) to prevent unprivileged server-side or database administrator introspection.

### 3. Immutable Compliance Audit Ledger (Target Design)
- Every context filtering decision, memory access, and tool invocation will be appended to an immutable, tamper-evident log store (e.g., Cloud Audit Logs or append-only PostgreSQL table with cryptographic hash chains).
- Supports user-driven GDPR/CCPA data export and privacy auditing with full legal fidelity.

### 4. Server Secret Vault (Target Design)
- All Gemini API keys, provider OAuth client secrets, and database credentials will reside exclusively in a server-side Secret Vault (Google Secret Manager).
- Client applications will receive short-lived, scoped session JWTs; zero provider secrets or LLM keys will be exposed to the browser runtime.

### 5. Production Crisis Response Pipeline (Target Design)
- Dedicated safety classifiers running in parallel with conversation routing will detect immediate self-harm, medical emergency, or severe psychiatric distress.
- When triggered, conversational curation will be paused, and verified regional crisis intervention resources (such as the 988 Suicide & Crisis Lifeline) will be presented with dignity and empathy.
