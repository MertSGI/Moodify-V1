export type MemoryDomain =
  | 'identity'
  | 'preferences'
  | 'dislikes'
  | 'relationships'
  | 'routines'
  | 'goals'
  | 'important_dates'
  | 'life_events'
  | 'work'
  | 'education'
  | 'travel'
  | 'music'
  | 'movies_tv'
  | 'books'
  | 'food'
  | 'shopping'
  | 'style'
  | 'activities'
  | 'sports'
  | 'places'
  | 'budget_preferences'
  | 'future_intentions'
  | 'ongoing_topics'
  | 'boundaries'
  | 'communication_preferences';

export type MemorySensitivity = 'NORMAL' | 'PERSONAL' | 'SENSITIVE' | 'HIGHLY_SENSITIVE';

export type MemoryStatus = 'ACTIVE' | 'OUTDATED' | 'DISPUTED' | 'REVOKED';

export type ExtractionTier =
  | 'EPHEMERAL'
  | 'POSSIBLY_USEFUL'
  | 'DURABLE'
  | 'SENSITIVE'
  | 'DO_NOT_STORE';

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
  sensitivity: MemorySensitivity;
  status: MemoryStatus;
  validFrom?: string;
  expiresAt?: string;
  userConfirmed: boolean;
  isImportant?: boolean;
  allowedForPersonalization: boolean;
  allowedForExternalTools: boolean;
  reasoningForBelief?: string;
}

export interface CandidateMemory {
  id: string;
  category: MemoryDomain;
  key: string;
  value: string;
  sourceQuote: string;
  confidence: number;
  sensitivity: MemorySensitivity;
  tier: ExtractionTier;
  recommendedAction: 'STORE_DURABLE' | 'REQUEST_CONFIRMATION' | 'KEEP_EPHEMERAL' | 'DISCARD';
  explanation: string;
}
