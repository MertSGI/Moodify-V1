import { RecommendationItem, RecommendationCategory, RecommendationScoringBreakdown } from '../types/recommendation';
import { ContextSnapshot } from '../types/context';
import { MemoryItem } from '../types/memory';
import { TasteNode } from '../types/taste';

export class RecommendationService {
  /**
   * Transparent deterministic scoring model:
   * RecommendationScore =
   *   taste_match
   *   + context_match
   *   + constraint_match
   *   + novelty
   *   + recency_adjustment
   *   - repetition_penalty
   *
   * Max base contributions:
   * - taste_match: 0.35
   * - context_match: 0.30
   * - constraint_match: 0.20
   * - novelty (exploration): 0.15
   * - recency_adjustment: ±0.05
   * - repetition_penalty: 0.00 to 0.15
   */
  public static calculateDeterministicScore(
    item: RecommendationItem,
    context: ContextSnapshot,
    tasteNodes: TasteNode[],
    memories: MemoryItem[],
    explorationFactor: number = 0.35
  ): { finalScore: number; breakdown: RecommendationScoringBreakdown } {
    // 1. TASTE MATCH (Max 0.35)
    let tasteFactorScore = 0.20; // baseline
    const matchingTaste = tasteNodes.find(
      tn => tn.name.toLowerCase() === item.title.toLowerCase() ||
            item.title.toLowerCase().includes(tn.name.toLowerCase()) ||
            item.description.toLowerCase().includes(tn.name.toLowerCase()) ||
            (item.whyThis.tasteFactor && item.whyThis.tasteFactor.some(tf => tf.tasteNodeName.toLowerCase() === tn.name.toLowerCase()))
    );

    if (matchingTaste) {
      if (matchingTaste.relation === 'LOVES') {
        tasteFactorScore = 0.35 * matchingTaste.strength;
      } else if (matchingTaste.relation === 'LIKES') {
        tasteFactorScore = 0.28 * matchingTaste.strength;
      } else if (matchingTaste.relation === 'AVOIDS') {
        tasteFactorScore = 0.02; // heavily suppressed
      } else if (matchingTaste.relation === 'HAS_TRIED') {
        tasteFactorScore = 0.18;
      }
    } else if (item.whyThis.tasteFactor && item.whyThis.tasteFactor.length > 0) {
      tasteFactorScore = 0.28;
    }

    // 2. CONTEXT MATCH (Max 0.30)
    let contextMatchScore = 0.18; // baseline
    // Energy alignment
    if (context.dimensions.energy < 0.4) {
      // Depleted battery
      if (item.metadata.effortLevel === 'VERY_LOW') contextMatchScore += 0.12;
      else if (item.metadata.effortLevel === 'LOW') contextMatchScore += 0.08;
      else if (item.metadata.effortLevel === 'ACTIVE') contextMatchScore -= 0.10;
    } else if (context.dimensions.energy > 0.6) {
      // High energy
      if (item.metadata.effortLevel === 'ACTIVE' || item.category === 'events' || item.category === 'places') {
        contextMatchScore += 0.11;
      }
    }

    // Time/primaryState alignment
    if (context.primaryState === 'LOW_BATTERY' && (item.category === 'music' || item.category === 'movies_tv')) {
      contextMatchScore += 0.04;
    } else if (context.primaryState === 'ANTICIPATING_WEEKEND' && (item.category === 'events' || item.category === 'places')) {
      contextMatchScore += 0.05;
    }
    contextMatchScore = Math.min(0.30, Math.max(0.05, contextMatchScore));

    // 3. CONSTRAINT MATCH (Max 0.20)
    let constraintMatchScore = 0.15;
    const numConstraints = item.whyThis.constraintsRespected?.length || 0;
    if (numConstraints >= 3) {
      constraintMatchScore = 0.20;
    } else if (numConstraints === 2) {
      constraintMatchScore = 0.17;
    } else if (numConstraints === 1) {
      constraintMatchScore = 0.14;
    }

    // Check if any active memory constraint is violated
    const dislikesSavory = memories.some(m => m.category === 'dislikes' && m.key === 'food_texture' && m.allowedForPersonalization);
    if (dislikesSavory && item.category === 'food' && item.description.toLowerCase().includes('candy')) {
      constraintMatchScore = 0.01;
    }

    // 4. NOVELTY / EXPLORATION COMPONENT (Max 0.15)
    const itemNovelty = item.whyThis.noveltyScore ?? 0.4;
    let noveltyScore = 0.08;
    if (explorationFactor > 0.5) {
      // Reward high novelty serendipity
      noveltyScore = 0.05 + itemNovelty * 0.10 * (explorationFactor / 0.5);
    } else {
      // Reward familiar safe comfort
      noveltyScore = 0.05 + (1.0 - itemNovelty) * 0.10 * ((1.0 - explorationFactor) / 0.5);
    }
    noveltyScore = Math.min(0.15, Math.max(0.02, noveltyScore));

    // 5. RECENCY ADJUSTMENT (-0.05 to +0.05)
    let recencyAdjustment = 0.03; // Fresh recommendation
    if (matchingTaste?.recency) {
      const daysAgo = Math.floor((Date.now() - new Date(matchingTaste.recency).getTime()) / (1000 * 60 * 60 * 24));
      if (daysAgo < 3) recencyAdjustment = 0.05;
      else if (daysAgo > 30) recencyAdjustment = 0.01;
    }

    // 6. REPETITION PENALTY (0.00 to 0.15)
    let repetitionPenalty = 0.0;
    if (item.feedbackGiven === 'ALREADY_KNOW_IT' || item.feedbackGiven === 'ALREADY_TRIED_IT') {
      repetitionPenalty = 0.12;
    } else if (item.feedbackGiven === 'NOT_NOW') {
      repetitionPenalty = 0.08;
    } else if (item.feedbackGiven === 'NOT_FOR_ME') {
      repetitionPenalty = 0.40;
    }

    // Final Composite Formula
    const rawTotal =
      tasteFactorScore +
      contextMatchScore +
      constraintMatchScore +
      noveltyScore +
      recencyAdjustment -
      repetitionPenalty;

    const finalScore = Number(Math.min(0.99, Math.max(0.10, rawTotal)).toFixed(2));

    const breakdown: RecommendationScoringBreakdown = {
      tasteMatch: Number(tasteFactorScore.toFixed(2)),
      contextMatch: Number(contextMatchScore.toFixed(2)),
      constraintMatch: Number(constraintMatchScore.toFixed(2)),
      noveltyScore: Number(noveltyScore.toFixed(2)),
      recencyAdjustment: Number(recencyAdjustment.toFixed(2)),
      repetitionPenalty: Number(repetitionPenalty.toFixed(2)),
      totalScore: finalScore,
      formulaDescription: 'RecommendationScore = taste_match(35%) + context_match(30%) + constraint_match(20%) + novelty(15%) + recency_adj - repetition_penalty',
    };

    return { finalScore, breakdown };
  }

  /**
   * Generates or filters recommendations based on current context, taste graph, and exploration factor.
   */
  public static getCuration(
    category: RecommendationCategory | 'all',
    context: ContextSnapshot,
    tasteNodes: TasteNode[],
    memories: MemoryItem[],
    explorationFactor: number = 0.35,
    baseList: RecommendationItem[]
  ): RecommendationItem[] {
    const filtered = category === 'all'
      ? baseList
      : baseList.filter(item => item.category === category);

    return filtered.map(item => {
      const { finalScore, breakdown } = this.calculateDeterministicScore(
        item,
        context,
        tasteNodes,
        memories,
        explorationFactor
      );

      return {
        ...item,
        score: finalScore,
        whyThis: {
          ...item.whyThis,
          scoringBreakdown: breakdown,
        },
      };
    }).sort((a, b) => b.score - a.score);
  }
}
