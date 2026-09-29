import { RecommendationItem } from './recommendation';
import { ActionPlan } from './actions';
import { CandidateMemory } from './memory';

export type MessageSender = 'USER' | 'ASSISTANT' | 'SYSTEM_PROACTIVE';

export interface ChatCardPayload {
  type: 'RECOMMENDATION' | 'ACTION_PROPOSAL' | 'MEMORY_CONFIRMATION' | 'SCENARIO_SUMMARY' | 'SAFETY_REASSURANCE';
  recommendations?: RecommendationItem[];
  actionPlan?: ActionPlan;
  candidateMemory?: CandidateMemory;
  title?: string;
  body?: string;
}

export interface ChatMessage {
  id: string;
  sender: MessageSender;
  text: string;
  timestamp: string;
  suggestedReplies?: string[];
  cards?: ChatCardPayload[];
  isProactive?: boolean;
  proactiveReason?: string;
  sourceMemoryTrigger?: string;
  extractedCandidateMemories?: CandidateMemory[];
  firewallTaskId?: string;
}

export interface ProactiveSetting {
  mode: 'QUIET' | 'BALANCED' | 'COMPANION';
  quietHoursStart: string; // e.g. "22:00"
  quietHoursEnd: string;   // e.g. "08:00"
  maxPingsPerDay: number;
  allowMeetingFollowUps: boolean;
  allowConcertAlerts: boolean;
  allowWindDownSuggestions: boolean;
  allowGoalCheckIns: boolean;
  isPaused: boolean;
  pausedUntil?: string;
}
