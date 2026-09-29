import { ProactiveSetting, ChatMessage } from '../types/chat';
import { ContextSnapshot } from '../types/context';
import { MemoryItem } from '../types/memory';

export interface ProactiveRuntimeState {
  date: string;
  count: number;
}

export class ProactiveService {
  /**
   * Local storage key for persistent proactivity runtime frequency state.
   * Tracks daily dispatch count across page reloads without requiring a backend.
   */
  public static readonly RUNTIME_STORAGE_KEY = 'moodify_proactive_runtime';

  /**
   * Deterministic helper returning browser-local calendar date in YYYY-MM-DD format.
   * Uses local getFullYear(), getMonth() + 1, and getDate() with zero-padding.
   * Ensures proactive daily notification limits and local Quiet Hours share the same temporal authority.
   */
  public static getLocalDateKey(date: Date = new Date()): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /**
   * Loads current daily runtime frequency state from localStorage.
   * Automatically resets count to 0 if the local calendar date has changed.
   */
  private static getRuntimeState(): ProactiveRuntimeState {
    const today = this.getLocalDateKey();
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        const stored = localStorage.getItem(this.RUNTIME_STORAGE_KEY);
        if (stored) {
          const parsed = JSON.parse(stored);
          if (parsed && typeof parsed.count === 'number' && parsed.date === today) {
            return { date: today, count: parsed.count };
          }
        }
      }
    } catch {}
    const fresh: ProactiveRuntimeState = { date: today, count: 0 };
    this.saveRuntimeState(fresh);
    return fresh;
  }

  /**
   * Persists runtime frequency state to localStorage.
   */
  private static saveRuntimeState(state: ProactiveRuntimeState): void {
    try {
      if (typeof window !== 'undefined' && window.localStorage) {
        localStorage.setItem(this.RUNTIME_STORAGE_KEY, JSON.stringify(state));
      }
    } catch {}
  }

  /**
   * Atomically increments the daily ping count in localStorage.
   */
  private static incrementPingCount(): void {
    const current = this.getRuntimeState();
    const updated: ProactiveRuntimeState = {
      date: current.date,
      count: current.count + 1,
    };
    this.saveRuntimeState(updated);
  }

  /**
   * Maximum safety cap enforced per mode:
   * QUIET = 0
   * BALANCED = 3
   * COMPANION = 5
   */
  public static getModeSafetyCap(mode: ProactiveSetting['mode']): number {
    switch (mode) {
      case 'QUIET':
        return 0;
      case 'BALANCED':
        return 3;
      case 'COMPANION':
        return 5;
    }
  }

  /**
   * Authoritative effective maximum:
   * effectiveMax = min(settings.maxPingsPerDay, modeSafetyCap)
   * The user's configured maximum must never be exceeded.
   */
  public static getEffectiveMaxPings(settings: ProactiveSetting): number {
    const modeCap = this.getModeSafetyCap(settings.mode);
    const userMax = typeof settings.maxPingsPerDay === 'number' ? settings.maxPingsPerDay : modeCap;
    return Math.max(0, Math.min(userMax, modeCap));
  }

  /**
   * Determines if proactive contact is allowed right now based on settings, time, and daily limits
   */
  public static canSendProactivePing(settings: ProactiveSetting): { allowed: boolean; reason?: string } {
    const runtime = this.getRuntimeState();

    if (settings.isPaused) {
      return { allowed: false, reason: 'Proactivity is temporarily paused by user' };
    }

    if (settings.mode === 'QUIET') {
      return { allowed: false, reason: 'Mode is set to Quiet (reactive only, 0 outbound pings permitted)' };
    }

    // Daily notification frequency limit: effectiveMax = min(userConfigured, modeCap)
    const effectiveMax = this.getEffectiveMaxPings(settings);
    if (runtime.count >= effectiveMax) {
      return {
        allowed: false,
        reason: `Daily frequency limit reached (${runtime.count}/${effectiveMax} pings sent today; user max: ${settings.maxPingsPerDay}, ${settings.mode} safety cap: ${this.getModeSafetyCap(settings.mode)})`,
      };
    }

    const now = new Date();
    const currentHour = now.getHours();
    const currentMin = now.getMinutes();
    const currentFormatted = `${String(currentHour).padStart(2, '0')}:${String(currentMin).padStart(2, '0')}`;

    // Quiet hours check
    if (settings.quietHoursStart > settings.quietHoursEnd) {
      // Overnight (e.g., 22:00 to 08:00)
      if (currentFormatted >= settings.quietHoursStart || currentFormatted < settings.quietHoursEnd) {
        return { allowed: false, reason: `Within scheduled Quiet Hours (${settings.quietHoursStart}–${settings.quietHoursEnd})` };
      }
    } else {
      if (currentFormatted >= settings.quietHoursStart && currentFormatted < settings.quietHoursEnd) {
        return { allowed: false, reason: `Within scheduled Quiet Hours (${settings.quietHoursStart}–${settings.quietHoursEnd})` };
      }
    }

    return { allowed: true };
  }

  /**
   * Evaluates if a proactive trigger is appropriate for current user context
   */
  public static evaluateCandidateProactiveMessage(
    context: ContextSnapshot,
    memories: MemoryItem[],
    settings: ProactiveSetting
  ): ChatMessage | null {
    const check = this.canSendProactivePing(settings);
    if (!check.allowed) return null;

    // Trigger 1: Important meeting follow-up
    const reviewMem = memories.find(m => m.key === 'design_review_marcus');
    if (reviewMem && settings.allowMeetingFollowUps) {
      this.incrementPingCount();
      return {
        id: `proact_${Date.now()}`,
        sender: 'ASSISTANT',
        text: 'Hey Alex. I noticed your Q3 design review with Marcus wrapped up a bit ago. You mentioned feeling some pressure leading up to it. How did it end up going?',
        timestamp: new Date().toISOString(),
        isProactive: true,
        proactiveReason: 'Follow-up on your flagged 2:00 PM design review with Marcus',
        sourceMemoryTrigger: reviewMem.id,
        suggestedReplies: [
          'It went really well! He approved the core direction.',
          'Pretty exhausting. Lots of pushback on mobile navigation.',
          'I don’t want to think about work right now, let’s reset.',
        ],
      };
    }

    // Trigger 2: Evening wind-down when low battery
    if (context.primaryState === 'LOW_BATTERY' && settings.allowWindDownSuggestions) {
      this.incrementPingCount();
      return {
        id: `proact_${Date.now()}_winddown`,
        sender: 'ASSISTANT',
        text: 'Noticed your battery is running on low tonight after a long week. Would you like a quiet ambient album to reset, or should I leave you in peace?',
        timestamp: new Date().toISOString(),
        isProactive: true,
        proactiveReason: 'Low battery detected during evening hours',
        suggestedReplies: [
          'Put on something quiet and low-effort.',
          'I’m good, just heading to sleep soon.',
        ],
      };
    }

    // Trigger 3: Concert tour radar alert
    if (settings.allowConcertAlerts) {
      const concertMem = memories.find(m => m.key === 'live_shows_preference');
      if (concertMem) {
        this.incrementPingCount();
        return {
          id: `proact_${Date.now()}_concert`,
          sender: 'ASSISTANT',
          text: 'Ticket radar alert: Japanese Breakfast just added an intimate date at Thalia Hall. Presale starts tomorrow at 10 AM.',
          timestamp: new Date().toISOString(),
          isProactive: true,
          proactiveReason: 'Tracked artist intimate date announcement at favored venue',
          sourceMemoryTrigger: concertMem.id,
          suggestedReplies: [
            'Place a simulated calendar hold for the presale.',
            'Show details and ticket prices.',
          ],
        };
      }
    }

    return null;
  }

  public static getPingsSentTodayCount(): number {
    return this.getRuntimeState().count;
  }

  public static getLastPingDate(): string {
    return this.getRuntimeState().date;
  }

  public static resetPingsSentToday(): void {
    const today = this.getLocalDateKey();
    this.saveRuntimeState({ date: today, count: 0 });
  }
}
