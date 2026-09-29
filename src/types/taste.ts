export type TasteDomain =
  | 'music'
  | 'artists'
  | 'movies'
  | 'series'
  | 'books'
  | 'games'
  | 'food'
  | 'restaurants'
  | 'activities'
  | 'travel'
  | 'places'
  | 'products'
  | 'fashion'
  | 'technology'
  | 'fitness'
  | 'events'
  | 'concerts'
  | 'visual_style';

export type TasteRelation =
  | 'LIKES'
  | 'LOVES'
  | 'DISLIKES'
  | 'AVOIDS'
  | 'CURIOUS_ABOUT'
  | 'WANTS_TO_TRY'
  | 'HAS_TRIED'
  | 'OWNS'
  | 'HAS_WATCHED'
  | 'HAS_LISTENED'
  | 'HAS_ATTENDED'
  | 'FOLLOWS'
  | 'SAVED'
  | 'NOT_INTERESTED';

export interface TasteNode {
  id: string;
  name: string;
  domain: TasteDomain;
  relation: TasteRelation;
  contextCondition?: string; // e.g. "when stressed", "during workouts", "late nights"
  strength: number; // 0.0 to 1.0
  recency: string; // ISO date
  tags: string[];
  subGenreOrCuisine?: string;
  priceSensitivity?: 'BUDGET' | 'MODERATE' | 'PREMIUM';
  notes?: string;
}

export interface TasteEdge {
  sourceNodeId: string;
  targetNodeId: string;
  relationType: 'SIMILAR_TO' | 'COMPLEMENTS' | 'OPPOSITE_OF' | 'INFLUENCED_BY';
  weight: number;
}
