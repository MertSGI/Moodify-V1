export type IntegrationStatus = 'LIVE' | 'MOCK' | 'NOT_CONNECTED' | 'UNAVAILABLE';

export interface IntegrationProvider {
  id: string;
  name: string;
  category: 'calendar' | 'music' | 'movies' | 'events' | 'places' | 'shopping' | 'health';
  icon: string;
  description: string;
  status: IntegrationStatus;
  isOptional: boolean;
  permissionsRequired: string[];
  privacyNotes: string;
  lastSyncedAt?: string;
  capabilities: string[];
}
