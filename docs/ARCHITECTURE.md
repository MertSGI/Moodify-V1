# Moodify — Technical Architecture

## 1. System Overview

Moodify is built as a mobile-first full-stack TypeScript application prioritizing high client responsiveness, clean separation of concerns, and rigorous privacy boundaries.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        USER PRESENTATION LAYER                         │
│  ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐ ┌───────────┐ │
│  │    NOW    │ │   CHAT    │ │ DISCOVER  │ │   PLANS   │ │    YOU    │ │
│  └───────────┘ └───────────┘ └───────────┘ └───────────┘ └───────────┘ │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        CORE APPLICATION SERVICES                       │
│  ┌──────────────────────┐  ┌──────────────────────┐  ┌───────────────┐ │
│  │ ContextEngine        │  │ MemoryService        │  │ ActionService │ │
│  │ (Mood / Load / Time) │  │ (Candidate Extract)  │  │ (Risk & Exec) │ │
│  └──────────────────────┘  └──────────────────────┘  └───────────────┘ │
│  ┌──────────────────────┐  ┌──────────────────────┐  ┌───────────────┐ │
│  │ TasteGraphService    │  │ RecommendationEngine │  │ Proactive     │ │
│  │ (Entity Affinity)    │  │ (Why-This Generator) │  │ Service       │ │
│  └──────────────────────┘  └──────────────────────┘  └───────────────┘ │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                       PERSONAL CONTEXT FIREWALL                        │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ Task Intent Analysis ➔ Minimal Purpose Filter ➔ Redaction Engine │  │
│  └──────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                      PROVIDER & ADAPTER INTEGRATION                    │
│  ┌───────────────┐  ┌───────────────┐  ┌──────────────┐  ┌───────────┐ │
│  │ Google Cal    │  │ MusicProvider │  │ MovieProvider│  │ Places/   │ │
│  │ (Simulated)   │  │ (Agnostic)    │  │ (TMDB Mock)  │  │ Shopping  │ │
│  └───────────────┘  └───────────────┘  └──────────────┘  └───────────┘ │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Directory Structure

```
src/
├── types/
│   ├── memory.ts           # MemoryDomain, Sensitivity, CandidateStatus, ExtractionTier
│   ├── context.ts          # Dimensions, PrimaryState, ContextSnapshot
│   ├── taste.ts            # TasteNode, TasteEdge, TasteRelation, ContextCondition
│   ├── recommendation.ts   # RecommendationItem, WhyThisExplanation, Feedback
│   ├── actions.ts          # ActionPlan, ActionRiskLevel, PlanItem
│   ├── integrations.ts     # IntegrationProvider, IntegrationStatus
│   ├── privacy.ts          # FirewallDecision, PrivacySettings
│   └── chat.ts             # ChatMessage, SuggestedReplies, Cards
├── services/
│   ├── firewallService.ts  # Personal Context Firewall implementation
│   ├── memoryService.ts    # Candidate extraction & tiering
│   ├── contextService.ts   # Mood & context state transitions
│   ├── tasteService.ts     # Taste graph queries & updates
│   ├── recommendationService.ts # Multi-domain scoring & why-this builder
│   ├── actionService.ts    # Tool risk evaluation & execution
│   ├── proactiveService.ts # Quiet hours, relevance gating
│   └── agentOrchestrator.ts # Core conversational loop
├── data/
│   └── seedUser.ts         # Fictional demo user "Alex Chen" & pre-seeded vault
├── components/
│   ├── Navigation.tsx      # Top bar & bottom mobile tabs
│   ├── ScenarioDrawer.tsx  # 1-click evaluation triggers (A to F)
│   ├── NowView.tsx         # Alive daily dashboard with live context
│   ├── ChatView.tsx        # Conversation stream with rich cards & suggestions
│   ├── DiscoverView.tsx    # Multi-domain catalog with exploration slider
│   ├── PlansView.tsx       # Saved lists, calendar holds, concert radar
│   ├── YouView.tsx         # Vault inspector, Firewall logs, Privacy Center
│   └── modals/
│       ├── WhyThisModal.tsx      # Transparent recommendation breakdown
│       ├── ActionConfirmModal.tsx # Explicit approval for consequential actions
│       └── MemoryEditModal.tsx   # Inspect, edit, and revoke memory records
├── context/
│   └── AppContext.tsx      # Central reactive application state
└── App.tsx                 # Root application wrapper
```

---

## 3. Gemini Orchestration & Structured JSON (Target Production Design)

> **Prototype Implementation Note**: In the current Vision Prototype, `LIVE_GEMINI_CALL_COUNT = 0`. The conversational loop, memory candidate extraction, and context derivation execute through deterministic local logic in `src/services/agentOrchestrator.ts` and `src/services/memoryService.ts`. The design below defines the target production architecture.

In production, Generative AI is applied strictly where human conversational nuance and contextual interpretation are required. It is **never** used for deterministic state machines or critical database storage.

### Role of Gemini Models (Production):
- **Conversation & Nuance:** Translating complex user expressions (*"I had a brutal day"* vs. *"It actually went well"*) into contextual signals.
- **Candidate Extraction:** Identifying potential durable memories while rejecting ephemeral chatter and clinical/medical labels.
- **Structured Recommendation Reasoning:** Formulating natural-language explanations grounded in specific memory keys.

### Guardrail Enforcements:
- Structured JSON schemas ensure generative responses conform to `ChatCardPayload` and `ActionPlan` types.
- Fallback deterministic heuristics guarantee 100% functionality and instant latency even when offline or in restricted network conditions.
- Realtime voice capability is reserved for the Gemini Live API abstraction layer.
