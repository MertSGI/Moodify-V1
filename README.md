# Moodify — Personal Context, Taste & Life Action Agent (Android)

> **Platform:** Android 14+ (API 26–35, CompileSdk 35)  
> **Tech Stack:** Kotlin 2.1.0, Jetpack Compose, Material Design 3, Gradle Kotlin DSL, StateFlow, Coroutines.  
> **Original Project:** Ported from React web prototype into native Android Jetpack Compose.

Moodify is a functional prototype for a personal context, taste, and life action companion. It is **not** a generic chatbot or an ad feed: it is an agent designed to understand you gradually to make everyday life easier, richer, and less cognitively demanding.

---

## The Core Loop

```
KNOW ME ➔ UNDERSTAND CURRENT CONTEXT ➔ CURATE ➔ HELP ME ACT ➔ LEARN ➔ KNOW ME BETTER
```

---

## 5 Architectural Pillars (Ported to Android)

1. **Personal Memory Vault ("What I Know About You"):** Structured memory records across domains with sensitivity classification (`NORMAL`, `PERSONAL`, `STRICTLY_CONFIDENTIAL`), candidate extraction tiering, provenance tracking, and full user inspection and editing.
2. **Mood & Context Engine:** Real-time awareness of valence (-1.0 to 1.0), energy (0.0 to 1.0), stress (0.0 to 1.0), focus need, and schedule availability. Self-reported state always takes precedent over weak inferences.
3. **Personal Taste Graph:** Multi-domain affinity model (music, artists, movies, food, places, products, events) connecting entities with contextual conditions (*"ambient when stressed; indie rock at small live rooms"*).
4. **Action & Discovery Engine:** Multi-domain curation with transparent **"Why this?"** breakdowns and risk-tiered tool execution (`READ_ONLY`, `EXTERNAL_WRITE`). Consequential external writes require explicit user confirmation.
5. **Personal Context Firewall:** Inspectable privacy gate that enforces **Minimal-Purpose Context Assembly**, stripping sensitive or unrelated memory records before agent reasoning or tool invocation.

---

## Android Project Structure

```
app/
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/example/moodify/
│   │   ├── MainActivity.kt                # Main activity with Edge-to-Edge & Theme
│   │   ├── model/                         # Data classes
│   │   │   ├── ContextModels.kt           # ContextualDimensions, ContextSnapshot, UserProfile
│   │   │   ├── MemoryModels.kt            # MemoryItem, CandidateMemory, SensitivityLevel
│   │   │   ├── TasteModels.kt             # TasteNode, TasteEdge, TasteRelation
│   │   │   ├── RecommendationModels.kt    # RecommendationItem, WhyThisExplanation, Feedback
│   │   │   ├── ActionModels.kt            # ActionPlan, ActionParameter, PlanItem
│   │   │   ├── PrivacyModels.kt           # PrivacySettings, FirewallDecision, ProactiveSetting
│   │   │   ├── ChatModels.kt              # ChatMessage, ChatCardPayload, MessageSender
│   │   │   └── IntegrationModels.kt       # IntegrationProvider
│   │   ├── data/
│   │   │   └── SeedData.kt                # Comprehensive seed fixtures (Alex Chen, initial context)
│   │   ├── service/                       # Business logic services
│   │   │   ├── ContextEngine.kt           # Context dimension & mood state calculations
│   │   │   ├── MemoryVaultService.kt      # Extraction of candidate memories from dialogue
│   │   │   ├── TasteGraphService.kt       # Feedback adjustment & affinity weighting
│   │   │   ├── FirewallService.kt         # Personal Context Firewall filtration & audit log
│   │   │   ├── ActionService.kt           # Consequential action dispatch & simulation
│   │   │   └── AgentOrchestrator.kt       # Primary agent reasoning loop & scenario matching
│   │   ├── viewmodel/
│   │   │   └── MoodifyViewModel.kt        # StateFlow & coroutine-driven MVVM state manager
│   │   └── ui/
│   │       ├── MoodifyApp.kt              # Root Scaffold with M3 NavigationBar & Global Dialogs
│   │       ├── theme/                     # Design System (Dark Stone 950 + Warm Amber 500)
│   │       │   ├── Color.kt
│   │       │   ├── Theme.kt
│   │       │   └── Type.kt
│   │       ├── components/                # Reusable UI components & dialogs
│   │       │   ├── ScenarioBar.kt         # 1-Click Interactive Test Scenarios Bar
│   │       │   ├── WhyThisDialog.kt       # Transparent reasoning breakdown modal
│   │       │   ├── ActionConfirmDialog.kt # Consequential action authorization modal
│   │       │   └── MemoryEditDialog.kt    # Memory Vault record edit / add dialog
│   │       └── screens/                   # Top-level navigation destinations
│   │           ├── NowScreen.kt           # Alive greeting, context assessment, top intervention
│   │           ├── ChatScreen.kt          # Agent dialogue, candidate discovery, cards
│   │           ├── DiscoverScreen.kt      # Exploration factor slider, category filters, recs
│   │           ├── PlansScreen.kt         # Action plans, checklists & watchlists
│   │           └── YouScreen.kt           # Memory vault, taste graph, firewall, settings
│   └── res/
│       ├── values/
│       │   ├── strings.xml                # app_name = "Moodify"
│       │   ├── colors.xml
│       │   └── themes.xml
│       ├── drawable/
│       │   ├── ic_launcher_background.xml
│       │   └── ic_launcher_foreground.xml
│       └── mipmap-*/                      # Custom adaptive app icons across all densities
├── build.gradle.kts                       # App Gradle build configuration (Compose, M3)
settings.gradle.kts                        # Root project setup
build.gradle.kts                           # Root plugins
gradle/libs.versions.toml                  # Version catalog (AGP 8.8.2, Kotlin 2.1.0, Compose BOM)
```

---

## 6 Interactive Demo Scenarios (1-Click Evaluation)

The top of the app features a horizontal **Scenario Bar** allowing evaluators to trigger all 6 canonical test flows with one tap:

| Scenario | Trigger / User Context | What Moodify Demonstrates |
|---|---|---|
| **Scenario A: Rough Day** | *"Today was awful. I don't really want to think."* | Recognizes depleted battery (energy 0.15, stress 0.8), skips long lectures, and returns 3 low-friction options: 1 ambient music piece, 1 gentle cinema watch, and 1 offline bath/stretch reset. Feedback updates taste graph. |
| **Scenario B: Friday Night** | Free calendar evening detected. | Identifies user's passion for intimate indie rock, recommends a Japanese Breakfast secret show at Thalia Hall, explains the venue and acoustic match, and proposes an **Add to Google Calendar** action requiring explicit confirmation. |
| **Scenario C: Product Discovery** | *"I need snacks for work but trying not to eat junk all day."* | Enforces $25 workday food budget constraint, respects dislike of sugary crash bars, and returns a high-protein savory snack haul saving to Plans. |
| **Scenario D: Meeting Follow-Up** | Follow-up on 2:00 PM design review with VP Marcus. | Proactively checks in on a high-stakes meeting previously flagged in memory, validates emotional closure, and suggests an authentic celebratory ramen dinner. |
| **Scenario E: Movie Night** | *"Pick something for tonight. I'm tired but don't want something boring."* | Returns 3 genuinely differentiated options matching current cognitive load: Japanese slow cinema (*After the Storm*), sharp dark satire (*Severance*), and poignant drama (*Past Lives*). |
| **Scenario F: Concert Watch** | Artist radar check for Japanese Breakfast. | Explains tour tracking, venue acoustic rating, and places presale alerts into Plans > Tracked Events. |
