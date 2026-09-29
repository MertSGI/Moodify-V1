import { TasteDomain } from './taste';

export type RecommendationCategory =
  | 'music'
  | 'movies_tv'
  | 'activities'
  | 'food'
  | 'places'
  | 'products'
  | 'events';

export type RecommendationFeedbackType =
  | 'LOVE_IT'
  | 'LIKE_IT'
  | 'NOT_NOW'
  | 'NOT_FOR_ME'
  | 'ALREADY_KNOW_IT'
  | 'ALREADY_TRIED_IT'
  | 'SAVE_FOR_LATER';

export interface RecommendationScoringBreakdown {
  tasteMatch: number;        // Contribution from taste graph (0.0 to 0.35)
  contextMatch: number;      // Contribution from context & battery alignment (0.0 to 0.30)
  constraintMatch: number;   // Contribution from dietary/time/budget constraints (0.0 to 0.20)
  noveltyScore: number;      // Contribution based on exploration factor (0.0 to 0.15)
  recencyAdjustment: number; // Boost or decay based on temporal freshness (-0.05 to +0.05)
  repetitionPenalty: number; // Penalty if recently recommended or seen (0.0 to 0.15)
  totalScore: number;        // Final composite score (0.0 to 1.0)
  formulaDescription: string;
}

export interface WhyThisExplanation {
  summary: string;
  matchedMemories: {
    memoryId?: string;
    key: string;
    snippet: string;
    domain: string;
  }[];
  contextAlignment: {
    dimension: string;
    reason: string;
  }[];
  tasteFactor: {
    tasteNodeName: string;
    relation: string;
    weightReason: string;
  }[];
  constraintsRespected: string[];
  noveltyScore: number; // 0.0 (safe classic) to 1.0 (novel discovery)
  scoringBreakdown?: RecommendationScoringBreakdown;
}

export interface RecommendationItem {
  id: string;
  title: string;
  subtitle: string;
  category: RecommendationCategory;
  domain: TasteDomain;
  description: string;
  imageUrl?: string;
  badge?: string;
  metadata: {
    duration?: string;
    genreOrCuisine?: string;
    venueOrSource?: string;
    dateOrTime?: string;
    price?: string;
    rating?: string;
    effortLevel?: 'VERY_LOW' | 'LOW' | 'MEDIUM' | 'ACTIVE';
    url?: string;
  };
  whyThis: WhyThisExplanation;
  actionPrompt?: string;
  actionType?: 'SAVE_PLAN' | 'ADD_CALENDAR' | 'CREATE_LIST' | 'WATCH_LATER' | 'LISTEN_NOW';
  score: number;
  feedbackGiven?: RecommendationFeedbackType;
}
