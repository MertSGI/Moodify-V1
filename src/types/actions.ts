export type ActionRiskLevel =
  | 'READ_ONLY'
  | 'REVERSIBLE'
  | 'EXTERNAL_WRITE'
  | 'PURCHASE_OR_BOOKING'
  | 'SENSITIVE_ACTION';

export type ActionStatus =
  | 'PROPOSED'
  | 'AWAITING_CONFIRMATION'
  | 'CONFIRMED'
  | 'MOCK_EXECUTION'
  | 'SUCCEEDED'
  | 'FAILED'
  | 'CANCELLED';

export interface ActionParameter {
  name: string;
  label: string;
  value: any;
  type: 'string' | 'number' | 'boolean' | 'date' | 'list';
}

export interface ActionPlan {
  id: string;
  title: string;
  description: string;
  targetProvider: 'CALENDAR' | 'SHOPPING' | 'TICKETS' | 'PLACES' | 'SAVED_LISTS';
  actionName: string;
  riskLevel: ActionRiskLevel;
  requiresExplicitConfirmation: boolean;
  parameters: ActionParameter[];
  status: ActionStatus;
  createdAt: string;
  executedAt?: string;
  resultSummary?: string;
}

export interface PlanItem {
  id: string;
  title: string;
  type: 'ACTIVITY' | 'WATCH_LATER' | 'SHOPPING_LIST' | 'EVENT' | 'IDEA';
  category: string;
  items?: { id: string; name: string; checked: boolean; price?: string; category?: string }[];
  date?: string;
  venueOrPlatform?: string;
  notes?: string;
  isCalendarSynced?: boolean;
  status: 'PENDING' | 'IN_PROGRESS' | 'DONE';
  associatedRecommendationId?: string;
}
