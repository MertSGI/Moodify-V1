export type ContextSource =
  | 'SELF_REPORTED'
  | 'CONVERSATION_INFERRED'
  | 'TIME_CONTEXT'
  | 'CALENDAR_CONTEXT'
  | 'WEATHER_CONTEXT'
  | 'ACTIVITY_CONTEXT'
  | 'USER_SELECTED';

export type PrimaryContextState =
  | 'WINDING_DOWN'
  | 'DEEP_FOCUS'
  | 'OVERWHELMED'
  | 'EXPLORATORY'
  | 'REFLECTIVE'
  | 'SOCIAL_ENERGIZED'
  | 'LOW_BATTERY'
  | 'RECOVERY_NEEDED'
  | 'ANTICIPATING_WEEKEND';

export interface ContextualDimensions {
  valence: number;    // -1.0 (unpleasant) to 1.0 (pleasant)
  energy: number;     // 0.0 (drained) to 1.0 (high vigor)
  stress: number;     // 0.0 (calm) to 1.0 (high pressure)
  socialNeed: number; // 0.0 (needs solitude) to 1.0 (craves company)
  focusNeed: number;  // 0.0 (low effort) to 1.0 (deep concentration)
  noveltyNeed: number;// 0.0 (comfort/familiar) to 1.0 (discovery/surprise)
  confidence: number; // 0.0 to 1.0 confidence in this assessment
}

export interface ContextSnapshot {
  id: string;
  dimensions: ContextualDimensions;
  primaryState: PrimaryContextState;
  secondaryState?: string;
  userIntent?: string;
  contextSource: ContextSource;
  timestamp: string;
  notes?: string;
  // External context signals
  timeOfDay: 'EARLY_MORNING' | 'MORNING' | 'AFTERNOON' | 'EVENING' | 'LATE_NIGHT';
  dayOfWeek: string;
  weatherSummary?: string;
  nextCalendarEventInMinutes?: number;
  freeHoursRemainingToday?: number;
}
