import { MemoryItem, CandidateMemory, ExtractionTier, MemoryDomain, MemorySensitivity } from '../types/memory';

export class MemoryService {
  /**
   * Analyzes an incoming user message and decides whether any statement
   * should be extracted as a memory candidate, and assigns the appropriate tier:
   * EPHEMERAL, POSSIBLY_USEFUL, DURABLE, SENSITIVE, or DO_NOT_STORE.
   */
  public static extractCandidateMemories(userText: string): CandidateMemory[] {
    const textLower = userText.toLowerCase();
    const candidates: CandidateMemory[] = [];

    // Health/Clinical check: NEVER store clinical or diagnosis labels
    if (
      textLower.includes('diagnosed') ||
      textLower.includes('bipolar') ||
      textLower.includes('antidepressant') ||
      textLower.includes('prescription')
    ) {
      candidates.push({
        id: `cand_${Date.now()}_health`,
        category: 'boundaries',
        key: 'medical_statement',
        value: 'Medical condition or pharmaceutical mention',
        sourceQuote: userText,
        confidence: 0.99,
        sensitivity: 'HIGHLY_SENSITIVE',
        tier: 'DO_NOT_STORE',
        recommendedAction: 'DISCARD',
        explanation: 'Moodify safety policy prohibits storing medical, psychiatric, or clinical diagnostic data.',
      });
      return candidates;
    }

    // Dislike detection
    if (
      textLower.includes("hate ") ||
      textLower.includes("can't stand") ||
      textLower.includes("dislike") ||
      textLower.includes("no sugary") ||
      textLower.includes("refuse to")
    ) {
      let key = 'user_dislike';
      let value = userText;
      let category: MemoryDomain = 'dislikes';

      if (textLower.includes('snack') || textLower.includes('sugar') || textLower.includes('food')) {
        key = 'food_dislike';
        value = 'Prefers savory foods; avoids sugary or high-crash items';
      } else if (textLower.includes('arena') || textLower.includes('crowd')) {
        key = 'venue_dislike';
        value = 'Dislikes large crowded arenas; prefers intimate settings';
      }

      candidates.push({
        id: `cand_${Date.now()}_dislike`,
        category,
        key,
        value,
        sourceQuote: userText,
        confidence: 0.93,
        sensitivity: 'NORMAL',
        tier: 'DURABLE',
        recommendedAction: 'STORE_DURABLE',
        explanation: 'Explicit statement of preference boundary or aversion. Durable for future recommendations.',
      });
    }

    // Explicit Favorites & Comfort Preferences (e.g., "My favorite comfort food is ramen")
    if (
      textLower.includes('favorite') ||
      textLower.includes('comfort food') ||
      textLower.includes('my comfort') ||
      textLower.includes('i love ') ||
      textLower.includes('really love ') ||
      textLower.includes('obsessed with ')
    ) {
      let key = 'favorite_preference';
      let value = userText;
      let category: MemoryDomain = 'preferences';

      if (
        textLower.includes('comfort food') ||
        textLower.includes('ramen') ||
        textLower.includes('udon') ||
        textLower.includes('sushi') ||
        textLower.includes('food') ||
        textLower.includes('eat')
      ) {
        category = 'food';
        key = 'comfort_food';
        if (textLower.includes('ramen')) {
          value = 'Favorite comfort food: ramen (especially authentic counter spots)';
        } else if (textLower.includes('udon')) {
          value = 'Favorite comfort food: udon noodles';
        } else {
          value = `Favorite food preference: "${userText}"`;
        }
      } else if (textLower.includes('music') || textLower.includes('band') || textLower.includes('artist') || textLower.includes('genre')) {
        category = 'music';
        key = 'favorite_music';
        value = userText;
      } else if (textLower.includes('travel') || textLower.includes('city') || textLower.includes('destination')) {
        category = 'travel';
        key = 'favorite_destination';
        value = userText;
      }

      candidates.push({
        id: `cand_${Date.now()}_fav`,
        category,
        key,
        value,
        sourceQuote: userText,
        confidence: 0.95,
        sensitivity: 'NORMAL',
        tier: 'DURABLE',
        recommendedAction: 'STORE_DURABLE',
        explanation: 'User explicitly stated an enduring personal favorite. Stored as durable taste context for personalization.',
      });
    }

    // Budget preference
    if (
      textLower.includes('budget') ||
      textLower.includes('under $') ||
      textLower.includes('bucks') ||
      textLower.includes('cheap')
    ) {
      candidates.push({
        id: `cand_${Date.now()}_budget`,
        category: 'budget_preferences',
        key: 'spending_boundary',
        value: 'Conscious of casual spending; prefers modest price points where noted',
        sourceQuote: userText,
        confidence: 0.88,
        sensitivity: 'NORMAL',
        tier: 'DURABLE',
        recommendedAction: 'STORE_DURABLE',
        explanation: 'Financial comfort boundary helpful for filtering restaurant and product discovery.',
      });
    }

    // Ephemeral daily state (e.g. today was awful / meeting at 7)
    if (
      textLower.includes('today was') ||
      textLower.includes("i'm meeting") ||
      textLower.includes('right now') ||
      textLower.includes('tonight')
    ) {
      candidates.push({
        id: `cand_${Date.now()}_ephemeral`,
        category: 'ongoing_topics',
        key: 'daily_feeling',
        value: `Current situation: "${userText.substring(0, 70)}..."`,
        sourceQuote: userText,
        confidence: 0.82,
        sensitivity: 'NORMAL',
        tier: 'EPHEMERAL',
        recommendedAction: 'KEEP_EPHEMERAL',
        explanation: 'Immediate situational context that will expire after today. Not stored permanently in the vault.',
      });
    }

    // Intention / goal
    if (
      textLower.includes('trying to') ||
      textLower.includes('want to learn') ||
      textLower.includes('hoping to') ||
      textLower.includes('planning to')
    ) {
      candidates.push({
        id: `cand_${Date.now()}_intention`,
        category: 'future_intentions',
        key: 'active_intention',
        value: userText,
        sourceQuote: userText,
        confidence: 0.86,
        sensitivity: 'PERSONAL',
        tier: 'POSSIBLY_USEFUL',
        recommendedAction: 'REQUEST_CONFIRMATION',
        explanation: 'Future intention detected. Best to keep as candidate until repeated or confirmed by user.',
      });
    }

    return candidates;
  }

  /**
   * Converts an approved candidate into a durable MemoryItem
   */
  public static commitCandidateToVault(candidate: CandidateMemory): MemoryItem {
    return {
      id: `mem_${Date.now()}_${Math.random().toString(36).substring(2, 6)}`,
      category: candidate.category,
      key: candidate.key,
      value: candidate.value,
      source: 'CONVERSATION_EXTRACTED',
      sourceQuote: candidate.sourceQuote,
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      confidence: candidate.confidence,
      sensitivity: candidate.sensitivity,
      status: 'ACTIVE',
      userConfirmed: true,
      allowedForPersonalization: true,
      allowedForExternalTools: candidate.sensitivity === 'NORMAL',
      reasoningForBelief: candidate.explanation,
    };
  }
}
