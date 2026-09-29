import React, { createContext, useContext, useState, useEffect, useRef } from 'react';
import {
  SEED_USER,
  INITIAL_CONTEXT,
  SEED_MEMORIES,
  SEED_TASTE_NODES,
  SEED_TASTE_EDGES,
  SEED_RECOMMENDATIONS,
  SEED_PLANS,
  SEED_ACTIONS,
  SEED_INTEGRATIONS,
  INITIAL_CHAT_MESSAGES,
  DEFAULT_PROACTIVE_SETTINGS,
  DEFAULT_PRIVACY_SETTINGS,
} from '../data/seedUser';
import { MemoryItem, CandidateMemory } from '../types/memory';
import { ContextSnapshot, ContextualDimensions, PrimaryContextState } from '../types/context';
import { TasteNode, TasteEdge } from '../types/taste';
import { RecommendationItem, RecommendationFeedbackType } from '../types/recommendation';
import { PlanItem, ActionPlan } from '../types/actions';
import { IntegrationProvider } from '../types/integrations';
import { ChatMessage, ProactiveSetting } from '../types/chat';
import { PrivacySettings, FirewallDecision } from '../types/privacy';
import { AgentOrchestrator } from '../services/agentOrchestrator';
import { TasteGraphService } from '../services/tasteService';
import { ContextEngine } from '../services/contextService';
import { ActionService } from '../services/actionService';
import { PersonalContextFirewall } from '../services/firewallService';
import { ProactiveService } from '../services/proactiveService';

interface AppContextType {
  // Navigation & View
  currentTab: 'NOW' | 'CHAT' | 'DISCOVER' | 'PLANS' | 'YOU';
  setCurrentTab: (tab: 'NOW' | 'CHAT' | 'DISCOVER' | 'PLANS' | 'YOU') => void;

  // User & Context
  user: typeof SEED_USER;
  context: ContextSnapshot;
  updateContextDimensions: (dims: Partial<ContextualDimensions>, state?: PrimaryContextState) => void;
  resetContext: () => void;

  // Memories
  memories: MemoryItem[];
  addMemory: (memory: Partial<MemoryItem>) => void;
  updateMemory: (id: string, updates: Partial<MemoryItem>) => void;
  deleteMemory: (id: string) => void;
  commitCandidateMemory: (candidate: CandidateMemory) => void;
  clearAllMemories: () => void;

  // Taste Graph
  tasteNodes: TasteNode[];
  tasteEdges: TasteEdge[];
  applyRecommendationFeedback: (item: RecommendationItem, feedback: RecommendationFeedbackType) => void;

  // Recommendations & Exploration
  recommendations: RecommendationItem[];
  explorationFactor: number;
  setExplorationFactor: (factor: number) => void;
  inspectingWhyThisItem: RecommendationItem | null;
  setInspectingWhyThisItem: (item: RecommendationItem | null) => void;

  // Chat
  chatMessages: ChatMessage[];
  isThinking: boolean;
  sendMessage: (text: string) => Promise<void>;
  clearChat: () => void;

  // Plans & Actions
  plans: PlanItem[];
  actionPlans: ActionPlan[];
  addPlanItem: (item: Partial<PlanItem>) => void;
  togglePlanItemChecked: (planId: string, itemId: string) => void;
  pendingActionConfirmation: ActionPlan | null;
  setPendingActionConfirmation: (action: ActionPlan | null) => void;
  confirmAndExecuteAction: (actionId: string) => void;
  rejectAction: (actionId: string) => void;

  // Integrations
  integrations: IntegrationProvider[];
  toggleIntegrationConnection: (id: string) => void;

  // Privacy & Firewall
  privacySettings: PrivacySettings;
  updatePrivacySettings: (settings: Partial<PrivacySettings>) => void;
  firewallLogs: FirewallDecision[];
  exportPersonalData: () => void;

  // Proactive Settings
  proactiveSettings: ProactiveSetting;
  updateProactiveSettings: (settings: Partial<ProactiveSetting>) => void;

  // Reset all data to initial seed fixtures
  resetToSeedData: () => void;

  // Quick Demo Scenario Trigger
  triggerScenario: (scenarioId: 'A' | 'B' | 'C' | 'D' | 'E' | 'F') => void;
}

const AppContext = createContext<AppContextType | null>(null);

export const AppProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentTab, setCurrentTab] = useState<'NOW' | 'CHAT' | 'DISCOVER' | 'PLANS' | 'YOU'>('NOW');
  const [user] = useState(SEED_USER);
  const [context, setContext] = useState<ContextSnapshot>(() => {
    try {
      const stored = localStorage.getItem('moodify_context');
      return stored ? JSON.parse(stored) : INITIAL_CONTEXT;
    } catch {
      return INITIAL_CONTEXT;
    }
  });

  const [memories, setMemories] = useState<MemoryItem[]>(() => {
    try {
      const stored = localStorage.getItem('moodify_memories');
      return stored ? JSON.parse(stored) : SEED_MEMORIES;
    } catch {
      return SEED_MEMORIES;
    }
  });

  const [tasteNodes, setTasteNodes] = useState<TasteNode[]>(() => {
    try {
      const stored = localStorage.getItem('moodify_taste_nodes');
      return stored ? JSON.parse(stored) : SEED_TASTE_NODES;
    } catch {
      return SEED_TASTE_NODES;
    }
  });

  const [tasteEdges] = useState<TasteEdge[]>(SEED_TASTE_EDGES);
  const [recommendations, setRecommendations] = useState<RecommendationItem[]>(SEED_RECOMMENDATIONS);
  const [explorationFactor, setExplorationFactor] = useState<number>(0.35);
  const [inspectingWhyThisItem, setInspectingWhyThisItem] = useState<RecommendationItem | null>(null);
  const [chatMessages, setChatMessages] = useState<ChatMessage[]>(INITIAL_CHAT_MESSAGES);
  const [isThinking, setIsThinking] = useState<boolean>(false);

  const [plans, setPlans] = useState<PlanItem[]>(() => {
    try {
      const stored = localStorage.getItem('moodify_plans');
      return stored ? JSON.parse(stored) : SEED_PLANS;
    } catch {
      return SEED_PLANS;
    }
  });

  const [actionPlans, setActionPlans] = useState<ActionPlan[]>(SEED_ACTIONS);
  const [pendingActionConfirmation, setPendingActionConfirmation] = useState<ActionPlan | null>(null);
  const [integrations, setIntegrations] = useState<IntegrationProvider[]>(SEED_INTEGRATIONS);

  const [privacySettings, setPrivacySettings] = useState<PrivacySettings>(() => {
    try {
      const stored = localStorage.getItem('moodify_privacy_settings');
      return stored ? JSON.parse(stored) : DEFAULT_PRIVACY_SETTINGS;
    } catch {
      return DEFAULT_PRIVACY_SETTINGS;
    }
  });

  const [proactiveSettings, setProactiveSettings] = useState<ProactiveSetting>(() => {
    try {
      const stored = localStorage.getItem('moodify_proactive_settings');
      return stored ? JSON.parse(stored) : DEFAULT_PROACTIVE_SETTINGS;
    } catch {
      return DEFAULT_PROACTIVE_SETTINGS;
    }
  });
  const [firewallLogs, setFirewallLogs] = useState<FirewallDecision[]>([]);

  // Ref to preserve durable pre-private-session context.
  // If application initializes with isPrivateSession === true (e.g. page reload during private session),
  // initialize the preserved snapshot from the durable context stored in moodify_context.
  const initialPrePrivateContext = (() => {
    try {
      const storedPrivacy = localStorage.getItem('moodify_privacy_settings');
      const isPriv = storedPrivacy ? JSON.parse(storedPrivacy).isPrivateSession : DEFAULT_PRIVACY_SETTINGS.isPrivateSession;
      if (isPriv) {
        const storedCtx = localStorage.getItem('moodify_context');
        return storedCtx ? JSON.parse(storedCtx) : INITIAL_CONTEXT;
      }
    } catch {}
    return null;
  })();

  const prePrivateContextRef = useRef<ContextSnapshot | null>(initialPrePrivateContext);
  // Restore guard ref: explicitly prevents generic context persistence from writing private context during exit
  const isRestoringFromPrivateRef = useRef<boolean>(false);

  // Local storage synchronization (functional browser persistence)
  useEffect(() => {
    try {
      localStorage.setItem('moodify_memories', JSON.stringify(memories));
    } catch {}
  }, [memories]);

  useEffect(() => {
    try {
      localStorage.setItem('moodify_taste_nodes', JSON.stringify(tasteNodes));
    } catch {}
  }, [tasteNodes]);

  useEffect(() => {
    try {
      localStorage.setItem('moodify_plans', JSON.stringify(plans));
    } catch {}
  }, [plans]);

  useEffect(() => {
    try {
      localStorage.setItem('moodify_privacy_settings', JSON.stringify(privacySettings));
    } catch {}
  }, [privacySettings]);

  useEffect(() => {
    try {
      localStorage.setItem('moodify_proactive_settings', JSON.stringify(proactiveSettings));
    } catch {}
  }, [proactiveSettings]);

  // Context persistence: NEVER write to durable localStorage while private session is active,
  // and guard against transient execution during atomic private session exit.
  useEffect(() => {
    if (isRestoringFromPrivateRef.current) {
      isRestoringFromPrivateRef.current = false;
      return;
    }
    if (!privacySettings.isPrivateSession) {
      try {
        localStorage.setItem('moodify_context', JSON.stringify(context));
      } catch {}
    }
  }, [context, privacySettings.isPrivateSession]);

  // Keep firewall logs updated
  useEffect(() => {
    setFirewallLogs(PersonalContextFirewall.getRecentAuditLogs());
  }, [chatMessages]);

  const resetToSeedData = () => {
    try {
      localStorage.removeItem('moodify_memories');
      localStorage.removeItem('moodify_taste_nodes');
      localStorage.removeItem('moodify_plans');
      localStorage.removeItem('moodify_privacy_settings');
      localStorage.removeItem('moodify_proactive_settings');
      localStorage.removeItem('moodify_proactive_runtime');
      localStorage.removeItem('moodify_context');
    } catch {}
    prePrivateContextRef.current = null;
    isRestoringFromPrivateRef.current = false;
    ProactiveService.resetPingsSentToday();
    setMemories(SEED_MEMORIES);
    setTasteNodes(SEED_TASTE_NODES);
    setPlans(SEED_PLANS);
    setPrivacySettings(DEFAULT_PRIVACY_SETTINGS);
    setProactiveSettings(DEFAULT_PROACTIVE_SETTINGS);
    setContext(INITIAL_CONTEXT);
    setChatMessages(INITIAL_CHAT_MESSAGES);
  };

  const updateContextDimensions = (dims: Partial<ContextualDimensions>, state?: PrimaryContextState) => {
    setContext(prev => ContextEngine.setSelfReportedMood(prev, dims, state));
  };

  const resetContext = () => {
    setContext(INITIAL_CONTEXT);
  };

  const addMemory = (memory: Partial<MemoryItem>) => {
    if (privacySettings.isPrivateSession) {
      console.warn('Moodify Privacy Guard: Private session active. Memory Vault additions are blocked.');
      return;
    }

    const newMem: MemoryItem = {
      id: `mem_${Date.now()}`,
      category: memory.category || 'preferences',
      key: memory.key || 'custom_preference',
      value: memory.value || '',
      source: 'USER_STATED',
      createdAt: new Date().toISOString(),
      updatedAt: new Date().toISOString(),
      confidence: 1.0,
      sensitivity: memory.sensitivity || 'NORMAL',
      status: 'ACTIVE',
      userConfirmed: true,
      allowedForPersonalization: true,
      allowedForExternalTools: memory.allowedForExternalTools ?? true,
      reasoningForBelief: memory.reasoningForBelief || 'Manually added by user in Memory Vault.',
      isImportant: memory.isImportant ?? false,
    };
    setMemories(prev => [newMem, ...prev]);
  };

  const updateMemory = (id: string, updates: Partial<MemoryItem>) => {
    setMemories(prev =>
      prev.map(m => (m.id === id ? { ...m, ...updates, updatedAt: new Date().toISOString() } : m))
    );
  };

  const deleteMemory = (id: string) => {
    setMemories(prev => prev.filter(m => m.id !== id));
  };

  const commitCandidateMemory = (candidate: CandidateMemory) => {
    if (privacySettings.isPrivateSession) {
      console.warn('Moodify Privacy Guard: Private session active. Candidate commit blocked.');
      return;
    }

    const newMem: MemoryItem = {
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
    setMemories(prev => [newMem, ...prev]);
  };

  const clearAllMemories = () => {
    setMemories([]);
  };

  const applyRecommendationFeedback = (
    item: RecommendationItem,
    feedback: RecommendationFeedbackType
  ) => {
    // 1. Update item state
    setRecommendations(prev =>
      prev.map(r => (r.id === item.id ? { ...r, feedbackGiven: feedback } : r))
    );

    // 2. Update taste graph if not in private session
    if (!privacySettings.isPrivateSession) {
      setTasteNodes(prev => TasteGraphService.applyFeedback(prev, item.title, item.domain, feedback));
    }

    // 3. If action is SAVE_FOR_LATER, add to Plans
    if (feedback === 'SAVE_FOR_LATER') {
      addPlanItem({
        title: item.title,
        type: item.category === 'movies_tv' ? 'WATCH_LATER' : 'ACTIVITY',
        category: item.metadata.genreOrCuisine || item.category,
        notes: item.description,
        associatedRecommendationId: item.id,
      });
    }
  };

  const addPlanItem = (item: Partial<PlanItem>) => {
    const newPlan: PlanItem = {
      id: `plan_${Date.now()}`,
      title: item.title || 'Untitled Plan',
      type: item.type || 'ACTIVITY',
      category: item.category || 'General',
      notes: item.notes || '',
      status: 'PENDING',
      isCalendarSynced: item.isCalendarSynced || false,
      items: item.items || [],
      date: item.date,
      venueOrPlatform: item.venueOrPlatform,
      associatedRecommendationId: item.associatedRecommendationId,
    };
    setPlans(prev => [newPlan, ...prev]);
  };

  const togglePlanItemChecked = (planId: string, itemId: string) => {
    setPlans(prev =>
      prev.map(p => {
        if (p.id !== planId || !p.items) return p;
        return {
          ...p,
          items: p.items.map(i => (i.id === itemId ? { ...i, checked: !i.checked } : i)),
        };
      })
    );
  };

  const confirmAndExecuteAction = (actionId: string) => {
    const action = actionPlans.find(a => a.id === actionId);
    if (!action) return;

    const result = ActionService.executeAction(action);
    setActionPlans(prev =>
      prev.map(a =>
        a.id === actionId
          ? { ...a, status: result.executionStatus, executedAt: new Date().toISOString(), resultSummary: result.resultSummary }
          : a
      )
    );

    if (result.newPlanItem) {
      setPlans(prev => [result.newPlanItem!, ...prev]);
    }

    setPendingActionConfirmation(null);

    // Notify in chat with explicit mock disclaimer
    setChatMessages(prev => [
      ...prev,
      {
        id: `msg_${Date.now()}_sys`,
        sender: 'ASSISTANT',
        text: `[Local Simulation] ${result.resultSummary}`,
        timestamp: new Date().toISOString(),
      },
    ]);
  };

  const rejectAction = (actionId: string) => {
    setActionPlans(prev =>
      prev.map(a => (a.id === actionId ? { ...a, status: 'CANCELLED' } : a))
    );
    setPendingActionConfirmation(null);
  };

  const sendMessage = async (text: string) => {
    if (!text.trim()) return;

    // Add user message
    const userMsg: ChatMessage = {
      id: `msg_u_${Date.now()}`,
      sender: 'USER',
      text,
      timestamp: new Date().toISOString(),
    };
    setChatMessages(prev => [...prev, userMsg]);
    setIsThinking(true);

    try {
      // Simulate intelligent agent loop (brief 500ms realistic thinking)
      await new Promise(res => setTimeout(res, 500));

      const result = await AgentOrchestrator.processUserMessage(
        text,
        context,
        memories,
        tasteNodes,
        recommendations,
        privacySettings
      );

      setContext(result.updatedContext);
      setChatMessages(prev => [...prev, result.replyMessage]);

      // If an action was proposed that needs explicit confirmation, queue it
      if (result.replyMessage.cards) {
        for (const card of result.replyMessage.cards) {
          if (card.actionPlan && card.actionPlan.requiresExplicitConfirmation) {
            setActionPlans(prev => [card.actionPlan!, ...prev]);
            setPendingActionConfirmation(card.actionPlan);
          }
        }
      }
    } finally {
      setIsThinking(false);
    }
  };

  const clearChat = () => {
    setChatMessages(INITIAL_CHAT_MESSAGES);
  };

  const toggleIntegrationConnection = (id: string) => {
    setIntegrations(prev =>
      prev.map(item => {
        if (item.id !== id) return item;
        const newStatus = item.status === 'NOT_CONNECTED' ? 'MOCK' : 'NOT_CONNECTED';
        return { ...item, status: newStatus };
      })
    );
  };

  const updatePrivacySettings = (settings: Partial<PrivacySettings>) => {
    // Intercept Private Session transitions to guarantee atomic isolation and prevent transient durable writes
    if (settings.isPrivateSession !== undefined && settings.isPrivateSession !== privacySettings.isPrivateSession) {
      if (settings.isPrivateSession) {
        // ENTER PRIVATE SESSION
        // 1. Preserve current durable context A in prePrivateContextRef
        prePrivateContextRef.current = context;
        // 2. Set isPrivateSession = true (generic context persistence remains disabled during private mode)
        setPrivacySettings(prev => ({ ...prev, ...settings }));
      } else {
        // EXIT PRIVATE SESSION ATOMICALLY
        // 1. Resolve restored context A from prePrivateContextRef or durable moodify_context fallback
        const restored = prePrivateContextRef.current || (() => {
          try {
            const stored = localStorage.getItem('moodify_context');
            return stored ? JSON.parse(stored) : INITIAL_CONTEXT;
          } catch {
            return INITIAL_CONTEXT;
          }
        })();

        // 2. Arm restore guard ref so generic persistence cannot write private context C during transition
        isRestoringFromPrivateRef.current = true;

        // 3. Immediately and synchronously write restored context A to durable moodify_context
        try {
          localStorage.setItem('moodify_context', JSON.stringify(restored));
        } catch {}

        // 4. Clear prePrivateContextRef
        prePrivateContextRef.current = null;

        // 5. Update React context to restored A
        setContext(restored);

        // 6. Set isPrivateSession = false in the same logical transition
        setPrivacySettings(prev => ({ ...prev, ...settings }));
      }
    } else {
      setPrivacySettings(prev => ({ ...prev, ...settings }));
    }
  };

  const updateProactiveSettings = (settings: Partial<ProactiveSetting>) => {
    setProactiveSettings(prev => ({ ...prev, ...settings }));
  };

  const exportPersonalData = () => {
    const exportPayload = {
      user,
      exportDate: new Date().toISOString(),
      memories,
      tasteGraph: { nodes: tasteNodes, edges: tasteEdges },
      contextSnapshot: context,
      plans,
      privacySettings,
      proactiveSettings,
    };

    const blob = new Blob([JSON.stringify(exportPayload, null, 2)], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `moodify_personal_vault_export_${new Date().toISOString().split('T')[0]}.json`;
    a.click();
    URL.revokeObjectURL(url);
  };

  const triggerScenario = (scenarioId: 'A' | 'B' | 'C' | 'D' | 'E' | 'F') => {
    setCurrentTab('CHAT');
    switch (scenarioId) {
      case 'A':
        sendMessage('Today was awful. I don’t really want to think.');
        break;
      case 'B':
        sendMessage('I have no plans this Friday night. What should I do?');
        break;
      case 'C':
        sendMessage('I need snacks for work but I’m trying not to eat junk all day.');
        break;
      case 'D':
        sendMessage('How did my design review with Marcus go? Let’s recap.');
        break;
      case 'E':
        sendMessage('Pick something for tonight. I’m tired but don’t want something boring.');
        break;
      case 'F':
        sendMessage('Track Japanese Breakfast concerts for me and show upcoming tour dates.');
        break;
    }
  };

  return (
    <AppContext.Provider
      value={{
        currentTab,
        setCurrentTab,
        user,
        context,
        updateContextDimensions,
        resetContext,
        memories,
        addMemory,
        updateMemory,
        deleteMemory,
        commitCandidateMemory,
        clearAllMemories,
        tasteNodes,
        tasteEdges,
        applyRecommendationFeedback,
        recommendations,
        explorationFactor,
        setExplorationFactor,
        inspectingWhyThisItem,
        setInspectingWhyThisItem,
        chatMessages,
        isThinking,
        sendMessage,
        clearChat,
        plans,
        actionPlans,
        addPlanItem,
        togglePlanItemChecked,
        pendingActionConfirmation,
        setPendingActionConfirmation,
        confirmAndExecuteAction,
        rejectAction,
        integrations,
        toggleIntegrationConnection,
        privacySettings,
        updatePrivacySettings,
        firewallLogs,
        exportPersonalData,
        proactiveSettings,
        updateProactiveSettings,
        resetToSeedData,
        triggerScenario,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useApp = () => {
  const ctx = useContext(AppContext);
  if (!ctx) throw new Error('useApp must be used within AppProvider');
  return ctx;
};
