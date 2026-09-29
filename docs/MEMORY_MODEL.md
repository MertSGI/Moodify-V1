# Moodify — Personal Memory Model

## 1. Core Principle: Anti-Transcript Storage

Traditional conversational AI stores unstructured chat transcripts and searches them using unbounded vector embeddings. This approach has fatal flaws for personal assistants:
- It hoards sensitive, private, or irrelevant chatter.
- It confuses transient states (*"I have a headache today"*) with durable traits.
- The user cannot easily inspect, correct, or revoke what the model "knows."

**Moodify rejects the chat-log-as-memory model.**

Instead, Moodify operates a **Structured Personal Memory Vault** with explicit schemas, provenance tracking, sensitivity classifications, and candidate extraction tiering.

---

## 2. Memory Domains

Moodify organizes memory records across 25 distinct life domains:

| Category | Example Stored Key | Example Value |
|---|---|---|
| `identity` | `profession` | Staff Product Designer at software studio |
| `preferences` | `aesthetic_preference` | Mid-century minimalist, muted earth tones |
| `dislikes` | `food_texture` | Dislikes overly sugary snacks; prefers savory crunch |
| `relationships` | `partner_maya` | Partner Maya is an architect who loves vegetarian food |
| `routines` | `morning_run` | 5k-8k lakefront run on Tuesday and Saturday mornings |
| `goals` | `running_goal` | Training for Chicago half marathon in autumn |
| `important_dates`| `birthday_maya` | Maya's birthday on November 14 |
| `life_events` | `family_health_care` | Mom recovering from knee surgery in Portland (Highly Sensitive) |
| `work` | `design_review_marcus` | Critical Q3 design review with VP Marcus |
| `music` | `ambient_focus` | Ambient minimalism (Jon Hopkins, Brian Eno) for focus |
| `movies_tv` | `comfort_cinema` | Kore-eda and gentle slow cinema when exhausted |
| `food` | `ramen_preference` | Spicy tonkotsu ramen at authentic small counter spots |
| `budget_preferences` | `workday_food_budget` | Prefers keeping weekday food and snacks under $20-$25 |
| `boundaries` | `quiet_evening_rule` | No high-energy alerts or work reminders after 9:00 PM |

---

## 3. Metadata & Schema

Every memory record in the vault maintains rigorous metadata:

```typescript
export interface MemoryItem {
  id: string;
  category: MemoryDomain;
  key: string;
  value: string;
  source: 'USER_STATED' | 'CONVERSATION_EXTRACTED' | 'FEEDBACK_INFERRED' | 'CALENDAR_DERIVED';
  sourceMessageId?: string;
  sourceQuote?: string;
  createdAt: string;
  updatedAt: string;
  confidence: number; // 0.0 to 1.0
  sensitivity: 'NORMAL' | 'PERSONAL' | 'SENSITIVE' | 'HIGHLY_SENSITIVE';
  status: 'ACTIVE' | 'OUTDATED' | 'DISPUTED' | 'REVOKED';
  validFrom?: string;
  expiresAt?: string;
  userConfirmed: boolean;
  isImportant?: boolean;
  allowedForPersonalization: boolean;
  allowedForExternalTools: boolean;
  reasoningForBelief?: string;
}
```

---

## 4. Extraction Tiering

Incoming conversational statements are passed through a candidate classification layer that assigns one of five extraction tiers:

1. **`EPHEMERAL`:** Immediate, temporary context (e.g. *"I'm stuck in traffic right now"*, *"I'm meeting Sarah at 7"*). Used for immediate response; discarded after session.
2. **`POSSIBLY_USEFUL`:** Stored as an unconfirmed candidate. Displayed in UI for user verification or reinforced upon repeated mentions.
3. **`DURABLE`:** Clear, explicit statements of preference, boundary, routine, or relationship (e.g. *"I never drink sugary soda"*). Committed to vault.
4. **`SENSITIVE`:** Personal life details (finances, family events). Admitted only with strict firewall controls.
5. **`DO_NOT_STORE`:** Medical diagnoses, pharmaceutical prescriptions, passwords, psychiatric labels. **Strictly banned from storage by policy.**

---

## 5. User Agency & Inspection UX

Under the **"What I Know About You"** screen in the **You** tab, users possess complete agency:
- **Inspect:** View every record, its confidence score, and the exact source quote.
- **Why Moodify Believes This:** Transparent reasoning explains how the system formed the belief.
- **Edit & Correct:** Modify any memory directly in the UI.
- **Mark as Outdated:** Keep historical context without letting it skew recommendations.
- **Permissions:** Toggle whether a specific memory can be used for recommendations or shared with external tool adapters.
- **Wipe:** Erase individual records or wipe the entire vault in 1 click.
