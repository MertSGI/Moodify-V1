import { MemoryItem, MemorySensitivity } from '../types/memory';
import { FirewallDecision, PrivacySettings } from '../types/privacy';

export class PersonalContextFirewall {
  private static auditLogs: FirewallDecision[] = [];

  /**
   * Evaluates task intent and user privacy settings to filter candidate memories
   * according to the principle of Minimal-Purpose Context Assembly.
   */
  public static filterContextForTask(
    taskIntent: string,
    targetService: 'GEMINI_LLM' | 'EXTERNAL_TOOL' | 'RECOMMENDER' | 'CALENDAR_ADAPTER',
    candidateMemories: MemoryItem[],
    privacySettings: PrivacySettings
  ): { admitted: MemoryItem[]; decision: FirewallDecision } {
    const admitted: MemoryItem[] = [];
    const admittedSummaries: FirewallDecision['admittedMemories'] = [];
    const redactedSummaries: FirewallDecision['redactedMemories'] = [];

    // If master personalization is disabled or in private session with strict rules
    if (!privacySettings.masterPersonalizationEnabled && targetService !== 'CALENDAR_ADAPTER') {
      const decision: FirewallDecision = {
        taskId: `fw_${Date.now()}_${Math.random().toString(36).substring(2, 6)}`,
        taskIntent,
        timestamp: new Date().toISOString(),
        targetService,
        evaluatedMemoriesCount: candidateMemories.length,
        task: taskIntent,
        requestedContextCategories: [],
        selectedMemories: [],
        excludedMemories: candidateMemories.map(m => ({
          id: m.id,
          key: m.key,
          category: m.category,
          exclusionReason: 'Master personalization is disabled in Privacy Center',
        })),
        sensitiveDataBlocked: candidateMemories
          .filter(m => m.sensitivity === 'HIGHLY_SENSITIVE' || m.sensitivity === 'SENSITIVE')
          .map(m => ({
            id: m.id,
            key: m.key,
            category: m.category,
            sensitivity: m.sensitivity,
            reason: 'Master personalization is disabled in Privacy Center',
          })),
        admittedMemories: [],
        redactedMemories: candidateMemories.map(m => ({
          id: m.id,
          key: m.key,
          sensitivity: m.sensitivity,
          redactionReason: 'Master personalization is disabled in Privacy Center',
        })),
        wasSanitized: true,
        privacyConfidenceScore: 1.0,
      };
      this.auditLogs.unshift(decision);
      return { admitted: [], decision };
    }

    const intentLower = taskIntent.toLowerCase();

    // Determine target requested categories based on task classification
    let requestedCategories: string[] = [];
    if (intentLower.includes('snack') || intentLower.includes('food') || intentLower.includes('eat') || intentLower.includes('ramen') || intentLower.includes('dinner') || intentLower.includes('lunch') || intentLower.includes('inexpensive')) {
      requestedCategories = ['food', 'dislikes', 'budget_preferences'];
    } else if (intentLower.includes('movie') || intentLower.includes('film') || intentLower.includes('watch') || intentLower.includes('music') || intentLower.includes('song') || intentLower.includes('listen') || intentLower.includes('concert') || intentLower.includes('show')) {
      requestedCategories = ['music', 'movies_tv', 'places', 'routines'];
    } else if (intentLower.includes('awful') || intentLower.includes('tired') || intentLower.includes('exhausted') || intentLower.includes('stress') || intentLower.includes('rough')) {
      requestedCategories = ['music', 'movies_tv', 'boundaries', 'routines', 'dislikes'];
    } else if (intentLower.includes('work') || intentLower.includes('meeting') || intentLower.includes('review') || intentLower.includes('marcus') || intentLower.includes('calendar') || intentLower.includes('friday')) {
      requestedCategories = ['work', 'important_dates', 'identity', 'boundaries'];
    } else {
      requestedCategories = ['identity', 'communication_preferences', 'boundaries'];
    }

    const selectedMemories: FirewallDecision['selectedMemories'] = [];
    const excludedMemories: FirewallDecision['excludedMemories'] = [];
    const sensitiveDataBlocked: FirewallDecision['sensitiveDataBlocked'] = [];

    for (const memory of candidateMemories) {
      // 1. Check if category is blocked by user
      if (privacySettings.blockedCategories.includes(memory.category)) {
        const reason = `Category "${memory.category}" is blocked by user privacy rule`;
        redactedSummaries.push({
          id: memory.id,
          key: memory.key,
          sensitivity: memory.sensitivity,
          redactionReason: reason,
        });
        excludedMemories.push({
          id: memory.id,
          key: memory.key,
          category: memory.category,
          exclusionReason: reason,
        });
        continue;
      }

      // 2. Check memory status
      if (memory.status !== 'ACTIVE') {
        const reason = `Memory status is ${memory.status}`;
        redactedSummaries.push({
          id: memory.id,
          key: memory.key,
          sensitivity: memory.sensitivity,
          redactionReason: reason,
        });
        excludedMemories.push({
          id: memory.id,
          key: memory.key,
          category: memory.category,
          exclusionReason: reason,
        });
        continue;
      }

      // 3. Check tool/recommender authorization
      if (targetService === 'EXTERNAL_TOOL' && !memory.allowedForExternalTools) {
        const reason = 'External tool export disallowed on this memory record';
        redactedSummaries.push({
          id: memory.id,
          key: memory.key,
          sensitivity: memory.sensitivity,
          redactionReason: reason,
        });
        excludedMemories.push({
          id: memory.id,
          key: memory.key,
          category: memory.category,
          exclusionReason: reason,
        });
        continue;
      }

      if (targetService === 'RECOMMENDER' && !memory.allowedForPersonalization) {
        const reason = 'Recommendation personalization disallowed on this record';
        redactedSummaries.push({
          id: memory.id,
          key: memory.key,
          sensitivity: memory.sensitivity,
          redactionReason: reason,
        });
        excludedMemories.push({
          id: memory.id,
          key: memory.key,
          category: memory.category,
          exclusionReason: reason,
        });
        continue;
      }

      // 4. Sensitivity gating
      if (memory.sensitivity === 'HIGHLY_SENSITIVE') {
        const isExplicitlyRequested =
          intentLower.includes('family') ||
          intentLower.includes('surgery') ||
          intentLower.includes('mom') ||
          intentLower.includes(memory.key.toLowerCase());

        if (!isExplicitlyRequested) {
          const reason = 'Highly sensitive item omitted: not strictly necessary for this task';
          redactedSummaries.push({
            id: memory.id,
            key: memory.key,
            sensitivity: memory.sensitivity,
            redactionReason: reason,
          });
          excludedMemories.push({
            id: memory.id,
            key: memory.key,
            category: memory.category,
            exclusionReason: reason,
          });
          sensitiveDataBlocked.push({
            id: memory.id,
            key: memory.key,
            category: memory.category,
            sensitivity: memory.sensitivity,
            reason,
          });
          continue;
        }
      }

      if (memory.sensitivity === 'SENSITIVE' && !privacySettings.allowSensitiveMemoriesForRecommendations) {
        if (targetService === 'RECOMMENDER' || targetService === 'EXTERNAL_TOOL') {
          const reason = 'Sensitive memories for recommendations is toggled off in Privacy Center';
          redactedSummaries.push({
            id: memory.id,
            key: memory.key,
            sensitivity: memory.sensitivity,
            redactionReason: reason,
          });
          excludedMemories.push({
            id: memory.id,
            key: memory.key,
            category: memory.category,
            exclusionReason: reason,
          });
          sensitiveDataBlocked.push({
            id: memory.id,
            key: memory.key,
            category: memory.category,
            sensitivity: memory.sensitivity,
            reason,
          });
          continue;
        }
      }

      // 5. Minimal-Purpose Relevance Assessment
      const isDomainRelevant = this.checkDomainRelevance(memory, intentLower, targetService);

      if (isDomainRelevant.relevant) {
        admitted.push(memory);
        admittedSummaries.push({
          id: memory.id,
          key: memory.key,
          domain: memory.category,
          sensitivity: memory.sensitivity,
          justification: isDomainRelevant.justification,
        });
        selectedMemories.push({
          id: memory.id,
          key: memory.key,
          category: memory.category,
          value: memory.value,
          justification: isDomainRelevant.justification,
        });
      } else {
        redactedSummaries.push({
          id: memory.id,
          key: memory.key,
          sensitivity: memory.sensitivity,
          redactionReason: isDomainRelevant.justification,
        });
        excludedMemories.push({
          id: memory.id,
          key: memory.key,
          category: memory.category,
          exclusionReason: isDomainRelevant.justification,
        });
      }
    }

    const decision: FirewallDecision = {
      taskId: `fw_${Date.now()}_${Math.random().toString(36).substring(2, 6)}`,
      taskIntent,
      timestamp: new Date().toISOString(),
      targetService,
      evaluatedMemoriesCount: candidateMemories.length,
      task: taskIntent,
      requestedContextCategories: requestedCategories,
      selectedMemories,
      excludedMemories,
      sensitiveDataBlocked,
      admittedMemories: admittedSummaries,
      redactedMemories: redactedSummaries,
      wasSanitized: redactedSummaries.length > 0,
      privacyConfidenceScore: Math.min(1.0, 0.85 + (redactedSummaries.length > 0 ? 0.15 : 0.05)),
    };

    this.auditLogs.unshift(decision);
    // Keep max 20 logs
    if (this.auditLogs.length > 20) {
      this.auditLogs.pop();
    }

    return { admitted, decision };
  }

  private static checkDomainRelevance(
    memory: MemoryItem,
    intentLower: string,
    targetService: string
  ): { relevant: boolean; justification: string } {
    // If it's a food task, food/dislikes/budget are relevant
    const isFoodTask =
      intentLower.includes('snack') ||
      intentLower.includes('food') ||
      intentLower.includes('eat') ||
      intentLower.includes('ramen') ||
      intentLower.includes('dinner') ||
      intentLower.includes('lunch');

    const isEntertainmentTask =
      intentLower.includes('movie') ||
      intentLower.includes('film') ||
      intentLower.includes('watch') ||
      intentLower.includes('music') ||
      intentLower.includes('song') ||
      intentLower.includes('listen') ||
      intentLower.includes('concert') ||
      intentLower.includes('show');

    const isWorkOrScheduleTask =
      intentLower.includes('work') ||
      intentLower.includes('meeting') ||
      intentLower.includes('review') ||
      intentLower.includes('marcus') ||
      intentLower.includes('calendar') ||
      intentLower.includes('friday');

    const isTiredOrResetTask =
      intentLower.includes('awful') ||
      intentLower.includes('tired') ||
      intentLower.includes('exhausted') ||
      intentLower.includes('stress') ||
      intentLower.includes('rough');

    // Matching logic
    if (isFoodTask) {
      if (['food', 'dislikes', 'budget_preferences'].includes(memory.category)) {
        return { relevant: true, justification: `Direct constraint match for food/snack curation (${memory.key})` };
      }
      return { relevant: false, justification: `Omitted non-food domain (${memory.category}) to maintain minimal purpose context` };
    }

    if (isEntertainmentTask) {
      if (['music', 'movies_tv', 'places', 'routines'].includes(memory.category)) {
        return { relevant: true, justification: `Essential taste preference for entertainment curation (${memory.key})` };
      }
      return { relevant: false, justification: `Omitted non-entertainment domain (${memory.category})` };
    }

    if (isTiredOrResetTask) {
      if (['music', 'movies_tv', 'boundaries', 'routines', 'dislikes'].includes(memory.category)) {
        return { relevant: true, justification: `Low-cognitive load preference needed for recovery recommendation` };
      }
      return { relevant: false, justification: `Omitted non-recovery domain (${memory.category})` };
    }

    if (isWorkOrScheduleTask) {
      if (['work', 'important_dates', 'identity', 'boundaries'].includes(memory.category)) {
        return { relevant: true, justification: `Direct match for schedule or professional context (${memory.key})` };
      }
      return { relevant: false, justification: `Omitted non-work domain (${memory.category})` };
    }

    // General conversational context
    if (['identity', 'communication_preferences', 'boundaries'].includes(memory.category) || memory.isImportant) {
      return { relevant: true, justification: `Core communication boundary or explicit primary preference (${memory.key})` };
    }

    return {
      relevant: false,
      justification: `Omitted: domain "${memory.category}" not strictly required for general query`,
    };
  }

  public static getRecentAuditLogs(): FirewallDecision[] {
    return this.auditLogs;
  }
}
