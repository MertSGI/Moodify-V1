# Moodify AI Orchestration Architecture

*Document Version: Phase 2-R4 Atomic Privacy & Local-Day Closure*  
*Standard: Truthful classification of current local implementation vs. target production architecture.*

---

## 1. Current Implementation Truth

```
LIVE_GEMINI_CALL_COUNT = 0
REAL_EXTERNAL_INTEGRATION_COUNT = 0
```

While `@google/genai` is listed in `package.json` as a project dependency, **no live Gemini API calls are executed by the running prototype**. 

All conversational workflows, candidate memory extraction, context state derivations, and recommendation ranking currently execute as **deterministic local client logic** in the browser.

---

## 2. Capability Operation Classification

| Subsystem / Operation | Classification | Current Prototype Engine | Future Production Design |
|---|---|---|---|
| **Chat Intent Routing** | `DETERMINISTIC` | Heuristic pattern matching in `AgentOrchestrator.ts` | `LLM_ASSISTED`: Gemini 2.5 Flash with structured system prompt for intent classification. |
| **Candidate Memory Extraction** | `DETERMINISTIC` | Regex and keyword parsing in `MemoryService.ts` (`extractCandidateMemories`) | `LLM_ASSISTED`: Asynchronous extraction worker identifying implicit preferences and assigning sensitivity grades. |
| **Personal Context Firewall** | `DETERMINISTIC` | Minimal-purpose category gating and sensitivity filters in `FirewallService.ts` | `DETERMINISTIC`: Code-enforced security boundary before context reaches LLM context windows. |
| **Context Snapshot Derivation** | `DETERMINISTIC` | 6-dimension contextual state calculation in `ContextEngine.ts` | `DETERMINISTIC` + `TOOL_REQUIRED`: Sensor/calendar aggregator with manual self-report override. |
| **Taste Graph Updates** | `DETERMINISTIC` | Deterministic node/edge weight adjustments in `TasteGraphService.ts` | `DETERMINISTIC`: Graph database updates following user feedback transactions. |
| **Recommendation Ranking** | `DETERMINISTIC` | Mathematical multi-factor equation in `RecommendationService.ts` | `DETERMINISTIC`: Code-level deterministic scoring over vector retrieval candidates. |
| **Recommendation Generation (Candidate Retrieval)** | `DETERMINISTIC` | Filtered queries over curated fixture items (`SEED_RECOMMENDATIONS`) | `TOOL_REQUIRED`: Live catalog search APIs (Spotify, JustWatch, Google Places, Bandsintown). |
| **Why-This Narrative Synthesis** | `DETERMINISTIC` (Seeded) | Pre-written narrative templates with runtime score breakdown | `LLM_REQUIRED`: Grounded synthesis of narrative explanation citing exact admitted memory IDs and score factors. |
| **Action Risk Classification** | `DETERMINISTIC` | Rule dictionary in `ActionService.ts` (`getRiskLevel`) | `DETERMINISTIC`: Security policy gate enforcing confirmation for high-risk tools. |
| **Action Execution** | `DETERMINISTIC` (Simulated) | Internal state creation (`MOCK_EXECUTION`) in `ActionService.ts` | `TOOL_REQUIRED`: Authenticated external API calls via backend OAuth proxies. |
| **Proactive Follow-up Triggering** | `DETERMINISTIC` | Date-aware local rules and quiet-hour checks in `ProactiveService.ts` | `DETERMINISTIC` + `LLM_ASSISTED`: Background cron worker evaluating context triggers and generating attuned pings. |

---

## 3. Current Implementation Details

### A. `AgentOrchestrator` (`src/services/agentOrchestrator.ts`)
- **Role**: Coordinates the core loop: Context interpretation -> Candidate extraction -> Context Firewall filtering -> Response payload assembly.
- **Execution Model**: Deterministic pattern matching over user text (e.g., keywords matching rough day, Friday night out, snack hauls, design review recap, movie night, concert radar, and comfort dining).
- **Latency**: Simulated 500ms delay (`setTimeout`) to provide visual continuity for the `isThinking` indicator.

### B. `MemoryService` (`src/services/memoryService.ts`)
- **Role**: Analyzes user statements for memory candidate potential.
- **Execution Model**: Deterministic keyword and boundary checks:
  - Clinical/medical terms -> immediately tagged `DO_NOT_STORE` and discarded.
  - Dislikes ("hate", "can't stand", "no sugary") -> tagged `DURABLE` dislike.
  - Favorites/comfort food ("favorite", "comfort food", "ramen", "udon") -> tagged `DURABLE` preference.
  - Budget constraints ("budget", "under $") -> tagged `DURABLE` budget boundary.
  - Situational states ("today was", "right now") -> tagged `EPHEMERAL`.
  - Intentions ("trying to", "want to learn") -> tagged `POSSIBLY_USEFUL` with confirmation request.

### C. `ContextEngine` (`src/services/contextService.ts`)
- **Role**: Manages multi-dimensional contextual vectors (`valence`, `energy`, `stress`, `socialNeed`, `focusNeed`, `noveltyNeed`).
- **Execution Model**: Deterministic state transitions from conversation triggers, with strict self-reported user override (`confidence = 1.0`).

### D. `RecommendationService` (`src/services/recommendationService.ts`)
- **Role**: Scores and ranks recommendations across categories.
- **Execution Model**: Transparent mathematical formula:
  $$\text{Score} = \text{taste\_match}(35\%) + \text{context\_match}(30\%) + \text{constraint\_match}(20\%) + \text{novelty}(15\%) + \text{recency\_adj} - \text{repetition\_penalty}$$

---

## 4. Future Production Design (Target Architecture)

When transitioning from vision prototype to production, the AI orchestration architecture will split into clean, modular tiers:

1. **Client Tier (Web/Mobile App)**:
   - Presentation layer only; no client-side Gemini API keys.
   - User inputs dispatched to backend gateway with session authentication.

2. **Personal Context Firewall (Server-Side Pre-Processor)**:
   - Runs deterministic security filters before constructing any LLM prompt.
   - Restricts context injection strictly to task-relevant memory records.
   - Redacts highly sensitive categories and medical disclosures.

3. **Inference Tier (Server-Side)**:
   - Utilizes `@google/genai` TypeScript SDK on Node.js/Express Cloud Run service.
   - **Gemini 2.5 Flash**: Fast conversational turns, query classification, and proactive ping drafts.
   - **Gemini 2.5 Pro**: Complex taste synthesis, multi-constraint schedule planning, and weekly memory consolidation.
   - Employs strict JSON schemas via `responseSchema` for structured card outputs.

4. **Action & Tool Execution Tier (Server-Side Proxy)**:
   - Tool calls from LLM are validated by the Action Risk Engine.
   - Consequential actions require user confirmation payload signed with session nonce.
   - Delegated execution through secure OAuth token manager (Google Calendar, Spotify, Instacart).
