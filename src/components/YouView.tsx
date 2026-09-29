import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import {
  Brain,
  Shield,
  Network,
  Radio,
  Sliders,
  Bell,
  Search,
  Plus,
  Trash2,
  Lock,
  Unlock,
  CheckCircle2,
  AlertTriangle,
  Download,
  Info,
  ExternalLink,
  Filter,
  EyeOff,
  History,
  Edit3,
} from 'lucide-react';
import { MemoryItem, MemoryDomain, MemorySensitivity } from '../types/memory';
import { MemoryEditModal } from './modals/MemoryEditModal';
import { ProactiveService } from '../services/proactiveService';

export const YouView: React.FC = () => {
  const {
    user,
    memories,
    deleteMemory,
    updateMemory,
    addMemory,
    clearAllMemories,
    tasteNodes,
    tasteEdges,
    integrations,
    toggleIntegrationConnection,
    privacySettings,
    updatePrivacySettings,
    firewallLogs,
    exportPersonalData,
    proactiveSettings,
    updateProactiveSettings,
    resetToSeedData,
  } = useApp();

  const [activeSubTab, setActiveSubTab] = useState<
    'VAULT' | 'FIREWALL' | 'TASTE' | 'INTEGRATIONS' | 'PRIVACY' | 'PROACTIVE'
  >('VAULT');

  // Vault state
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedDomain, setSelectedDomain] = useState<string>('all');
  const [selectedSensitivity, setSelectedSensitivity] = useState<string>('all');
  const [editingMemory, setEditingMemory] = useState<MemoryItem | null>(null);
  const [isAddingMemory, setIsAddingMemory] = useState(false);
  const [newKey, setNewKey] = useState('');
  const [newValue, setNewValue] = useState('');
  const [newCategory, setNewCategory] = useState<MemoryDomain>('preferences');
  const [newSensitivity, setNewSensitivity] = useState<MemorySensitivity>('NORMAL');

  // Filtered memories
  const filteredMemories = memories.filter(m => {
    if (selectedDomain !== 'all' && m.category !== selectedDomain) return false;
    if (selectedSensitivity !== 'all' && m.sensitivity !== selectedSensitivity) return false;
    if (searchQuery.trim()) {
      const q = searchQuery.toLowerCase();
      return (
        m.key.toLowerCase().includes(q) ||
        m.value.toLowerCase().includes(q) ||
        m.category.toLowerCase().includes(q)
      );
    }
    return true;
  });

  const handleAddSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newKey.trim() || !newValue.trim()) return;
    addMemory({
      key: newKey,
      value: newValue,
      category: newCategory,
      sensitivity: newSensitivity,
    });
    setNewKey('');
    setNewValue('');
    setIsAddingMemory(false);
  };

  const getSensitivityBadge = (sens: MemorySensitivity) => {
    switch (sens) {
      case 'NORMAL':
        return 'bg-stone-800 text-stone-300 border-stone-700';
      case 'PERSONAL':
        return 'bg-sky-950/60 text-sky-300 border-sky-900/50';
      case 'SENSITIVE':
        return 'bg-amber-950/60 text-amber-300 border-amber-900/50';
      case 'HIGHLY_SENSITIVE':
        return 'bg-rose-950/60 text-rose-300 border-rose-900/50';
    }
  };

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 space-y-8 pb-24 sm:pb-12">
      {/* Header Profile Summary */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-6 border-b border-stone-850">
        <div className="flex items-center gap-4">
          <img
            src={user.avatarUrl}
            alt={user.name}
            className="w-14 h-14 rounded-2xl object-cover ring-2 ring-amber-500/20 shadow-md"
          />
          <div>
            <div className="flex items-center gap-2">
              <h1 className="text-xl sm:text-2xl font-bold text-stone-100">{user.name}</h1>
              <span className="text-[11px] px-2 py-0.5 rounded-full bg-stone-800 text-stone-400 font-mono">
                {user.role}
              </span>
            </div>
            <p className="text-xs text-stone-400 mt-0.5">
              Personal Vault · {memories.length} Memories active · {tasteNodes.length} Taste Nodes
            </p>
          </div>
        </div>

        <div className="flex items-center gap-2">
          <button
            onClick={() => {
              if (confirm('Reset entire prototype state to initial seed fixtures?')) {
                resetToSeedData();
              }
            }}
            className="px-3.5 py-2 rounded-xl bg-stone-900 hover:bg-stone-850 text-stone-400 hover:text-stone-200 text-xs font-medium border border-stone-800 transition-colors"
            title="Reset memories, plans, and taste graph to factory seed defaults"
          >
            Reset to Seed Defaults
          </button>
          <button
            onClick={exportPersonalData}
            className="px-3.5 py-2 rounded-xl bg-stone-900 hover:bg-stone-850 text-stone-300 text-xs font-medium border border-stone-800 transition-colors flex items-center gap-1.5"
          >
            <Download className="w-3.5 h-3.5" />
            <span>Export JSON Vault</span>
          </button>
        </div>
      </div>

      {/* Sub-Navigation Tabs */}
      <div className="flex gap-2 border-b border-stone-850 pb-2 overflow-x-auto no-scrollbar">
        {[
          { id: 'VAULT' as const, label: 'Memory Vault', icon: Brain, count: memories.length },
          { id: 'FIREWALL' as const, label: 'Context Firewall', icon: Shield },
          { id: 'TASTE' as const, label: 'Taste Graph', icon: Network, count: tasteNodes.length },
          { id: 'INTEGRATIONS' as const, label: 'Integrations Hub', icon: Radio, count: integrations.length },
          { id: 'PRIVACY' as const, label: 'Privacy Center', icon: Lock },
          { id: 'PROACTIVE' as const, label: 'Proactivity & Settings', icon: Bell },
        ].map(tab => {
          const Icon = tab.icon;
          const isActive = activeSubTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setActiveSubTab(tab.id)}
              className={`shrink-0 flex items-center gap-2 px-4 py-2.5 rounded-2xl text-xs font-semibold transition-all ${
                isActive
                  ? 'bg-amber-500 text-stone-950 shadow-md shadow-amber-500/20'
                  : 'bg-stone-900 text-stone-400 hover:text-stone-200 border border-stone-800 hover:bg-stone-850'
              }`}
            >
              <Icon className="w-4 h-4" />
              <span>{tab.label}</span>
              {tab.count !== undefined && (
                <span
                  className={`text-[10px] px-1.5 py-0.2 rounded-full font-mono ${
                    isActive ? 'bg-stone-950/30 text-stone-950' : 'bg-stone-800 text-stone-400'
                  }`}
                >
                  {tab.count}
                </span>
              )}
            </button>
          );
        })}
      </div>

      {/* SUB-VIEW 1: MEMORY VAULT ("What I Know About You") */}
      {activeSubTab === 'VAULT' && (
        <div className="space-y-6">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
            <div>
              <h2 className="text-lg font-bold text-stone-100 flex items-center gap-2">
                <span>What I Know About You</span>
                <span className="text-xs font-normal text-stone-400">
                  (Durable, inspectable & user-controlled)
                </span>
              </h2>
              <p className="text-xs text-stone-400 mt-1 max-w-xl">
                Moodify never silently converts every sentence into permanent memory. You can inspect, correct, export, or revoke any record.
              </p>
            </div>

            <div className="flex items-center gap-2">
              <button
                onClick={() => setIsAddingMemory(!isAddingMemory)}
                className="px-3.5 py-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-stone-950 font-semibold text-xs transition-colors flex items-center gap-1.5"
              >
                <Plus className="w-4 h-4" />
                <span>Add Memory</span>
              </button>
              <button
                onClick={() => {
                  if (confirm('Clear all memories in vault? This cannot be undone.')) {
                    clearAllMemories();
                  }
                }}
                className="p-2 rounded-xl bg-stone-900 hover:bg-rose-950/40 text-stone-400 hover:text-rose-400 border border-stone-800 transition-colors"
                title="Clear all memories"
              >
                <Trash2 className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Add Memory Form */}
          {isAddingMemory && (
            <form onSubmit={handleAddSubmit} className="p-5 rounded-3xl bg-stone-900 border border-amber-500/30 space-y-4">
              <h3 className="text-xs font-semibold uppercase tracking-wider text-amber-400">
                Create New Memory Record
              </h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                <input
                  type="text"
                  placeholder="Memory Key (e.g. coffee_roast)"
                  value={newKey}
                  onChange={e => setNewKey(e.target.value)}
                  className="p-2.5 rounded-xl bg-stone-800 border border-stone-700 text-xs text-stone-100 focus:outline-none focus:border-amber-400"
                />
                <select
                  value={newCategory}
                  onChange={e => setNewCategory(e.target.value as MemoryDomain)}
                  className="p-2.5 rounded-xl bg-stone-800 border border-stone-700 text-xs text-stone-100"
                >
                  <option value="preferences">preferences</option>
                  <option value="dislikes">dislikes</option>
                  <option value="food">food</option>
                  <option value="music">music</option>
                  <option value="movies_tv">movies_tv</option>
                  <option value="budget_preferences">budget_preferences</option>
                  <option value="routines">routines</option>
                  <option value="boundaries">boundaries</option>
                </select>
              </div>
              <textarea
                placeholder="What should Moodify know?"
                value={newValue}
                onChange={e => setNewValue(e.target.value)}
                rows={2}
                className="w-full p-2.5 rounded-xl bg-stone-800 border border-stone-700 text-xs text-stone-100 focus:outline-none focus:border-amber-400"
              />
              <div className="flex justify-end gap-2">
                <button
                  type="button"
                  onClick={() => setIsAddingMemory(false)}
                  className="px-3 py-1.5 rounded-xl bg-stone-800 text-stone-300 text-xs"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-1.5 rounded-xl bg-amber-500 text-stone-950 font-semibold text-xs"
                >
                  Save to Vault
                </button>
              </div>
            </form>
          )}

          {/* Search & Filter Bar */}
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="relative flex-1">
              <Search className="w-4 h-4 text-stone-400 absolute left-3 top-3" />
              <input
                type="text"
                value={searchQuery}
                onChange={e => setSearchQuery(e.target.value)}
                placeholder="Search memories by keyword, key, or category..."
                className="w-full pl-9 pr-4 py-2.5 rounded-xl bg-stone-900 border border-stone-800 text-xs text-stone-100 placeholder-stone-500 focus:outline-none focus:border-amber-400"
              />
            </div>
            <select
              value={selectedSensitivity}
              onChange={e => setSelectedSensitivity(e.target.value)}
              className="p-2.5 rounded-xl bg-stone-900 border border-stone-800 text-xs text-stone-300"
            >
              <option value="all">All Sensitivities</option>
              <option value="NORMAL">NORMAL</option>
              <option value="PERSONAL">PERSONAL</option>
              <option value="SENSITIVE">SENSITIVE</option>
              <option value="HIGHLY_SENSITIVE">HIGHLY SENSITIVE</option>
            </select>
          </div>

          {/* Memories Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {filteredMemories.map(mem => (
              <div
                key={mem.id}
                className="p-5 rounded-3xl bg-stone-900 border border-stone-800 hover:border-stone-750 transition-all shadow-md flex flex-col justify-between space-y-3"
              >
                <div>
                  <div className="flex items-center justify-between gap-2 mb-2">
                    <div className="flex items-center gap-2">
                      <span className="text-xs font-mono font-medium text-emerald-400 px-2 py-0.5 rounded bg-emerald-950/60 border border-emerald-900/50">
                        {mem.key}
                      </span>
                      {mem.isImportant && (
                        <span className="text-xs text-amber-400 font-bold" title="Important memory">
                          ★
                        </span>
                      )}
                    </div>
                    <span
                      className={`text-[10px] font-semibold px-2 py-0.5 rounded-md border ${getSensitivityBadge(
                        mem.sensitivity
                      )}`}
                    >
                      {mem.sensitivity}
                    </span>
                  </div>

                  <p className="text-sm font-medium text-stone-200 leading-relaxed">{mem.value}</p>

                  {/* Provenance */}
                  {mem.reasoningForBelief && (
                    <div className="mt-2.5 pt-2 border-t border-stone-850 text-[11px] text-stone-400 flex items-start gap-1.5">
                      <Info className="w-3.5 h-3.5 text-stone-400 shrink-0 mt-0.5" />
                      <span>{mem.reasoningForBelief}</span>
                    </div>
                  )}
                </div>

                <div className="pt-2 border-t border-stone-850 flex items-center justify-between text-xs">
                  <div className="flex items-center gap-2 text-[10px] text-stone-400 uppercase tracking-wider">
                    <span>Domain: {mem.category}</span>
                    {mem.allowedForPersonalization ? (
                      <span className="text-emerald-400">· Personalization On</span>
                    ) : (
                      <span className="text-rose-400">· Personalization Paused</span>
                    )}
                  </div>

                  <div className="flex items-center gap-1">
                    <button
                      onClick={() => setEditingMemory(mem)}
                      className="p-1.5 rounded-lg text-stone-400 hover:text-stone-200 hover:bg-stone-800 transition-colors"
                      title="Inspect & edit"
                    >
                      <Edit3 className="w-3.5 h-3.5" />
                    </button>
                    <button
                      onClick={() => deleteMemory(mem.id)}
                      className="p-1.5 rounded-lg text-stone-400 hover:text-rose-400 hover:bg-stone-800 transition-colors"
                      title="Delete memory"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* SUB-VIEW 2: PERSONAL CONTEXT FIREWALL */}
      {activeSubTab === 'FIREWALL' && (
        <div className="space-y-6">
          <div>
            <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-emerald-400 mb-1">
              <Shield className="w-3.5 h-3.5" />
              <span>Minimal-Purpose Context Assembly</span>
            </div>
            <h2 className="text-2xl font-bold text-stone-100">Personal Context Firewall</h2>
            <p className="text-xs sm:text-sm text-stone-400 mt-1 max-w-2xl leading-relaxed">
              Before any personal data reaches an LLM or external tool, the firewall assesses: What is the task? What is minimally necessary? Which sensitive items must be redacted?
            </p>
          </div>

          {/* Architecture Diagram Card */}
          <div className="p-6 rounded-3xl bg-stone-900 border border-stone-800 space-y-4">
            <h3 className="text-xs uppercase font-semibold text-stone-300 tracking-wider">
              Firewall Pipeline in Operation
            </h3>
            <div className="grid grid-cols-1 sm:grid-cols-4 gap-3 text-xs">
              <div className="p-4 rounded-2xl bg-stone-850/80 border border-stone-800 space-y-1">
                <span className="text-amber-400 font-bold block">1. Task Intent</span>
                <span className="text-stone-300">Classifies the exact job (e.g. snack curation vs. evening reset)</span>
              </div>
              <div className="p-4 rounded-2xl bg-stone-850/80 border border-stone-800 space-y-1">
                <span className="text-sky-400 font-bold block">2. Minimal Schema</span>
                <span className="text-stone-300">Selects ONLY necessary memory categories, rejecting unrelated profile data</span>
              </div>
              <div className="p-4 rounded-2xl bg-stone-850/80 border border-stone-800 space-y-1">
                <span className="text-rose-400 font-bold block">3. Sensitivity Filter</span>
                <span className="text-stone-300">Redacts medical, clinical, or family context unless explicitly authorized</span>
              </div>
              <div className="p-4 rounded-2xl bg-stone-850/80 border border-stone-800 space-y-1">
                <span className="text-emerald-400 font-bold block">4. Sanitized Payload</span>
                <span className="text-stone-300">Dispatches stripped context without persistent leaking</span>
              </div>
            </div>
          </div>

          {/* Real-Time Audit Logs */}
          <div className="space-y-3">
            <h3 className="text-xs uppercase font-semibold text-stone-400 tracking-wider">
              Live Audit Log ({firewallLogs.length} Recent Invocations)
            </h3>
            {firewallLogs.length === 0 ? (
              <div className="p-6 rounded-3xl bg-stone-900 border border-stone-800 text-center text-xs text-stone-500">
                Send a message in Chat or trigger a scenario to generate a live firewall audit trail.
              </div>
            ) : (
              <div className="space-y-3">
                {firewallLogs.map(log => (
                  <div
                    key={log.taskId}
                    className="p-5 rounded-3xl bg-stone-900 border border-stone-800 space-y-3 shadow-md"
                  >
                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-stone-850 pb-2">
                      <div className="flex items-center gap-2">
                        <span className="font-mono text-xs text-amber-400 font-semibold">{log.taskId}</span>
                        <span className="text-xs text-stone-400">Target: {log.targetService}</span>
                      </div>
                      <div className="flex items-center gap-2 text-[11px] text-stone-500">
                        <span>{new Date(log.timestamp).toLocaleTimeString()}</span>
                        <span className="px-2 py-0.5 rounded bg-emerald-950/50 text-emerald-400 font-mono">
                          Confidence: {Math.round(log.privacyConfidenceScore * 100)}%
                        </span>
                      </div>
                    </div>

                    <div className="space-y-1.5 pt-1">
                      <div className="text-xs">
                        <span className="font-mono text-stone-400 font-semibold uppercase text-[10px] block">
                          TASK
                        </span>
                        <span className="text-stone-100 font-medium">"{log.task || log.taskIntent}"</span>
                      </div>

                      <div className="text-xs">
                        <span className="font-mono text-stone-400 font-semibold uppercase text-[10px] block">
                          REQUESTED_CONTEXT_CATEGORIES
                        </span>
                        <div className="flex flex-wrap gap-1.5 mt-0.5">
                          {log.requestedContextCategories && log.requestedContextCategories.length > 0 ? (
                            log.requestedContextCategories.map((cat, cIdx) => (
                              <span
                                key={cIdx}
                                className="px-2 py-0.5 rounded font-mono text-[10px] bg-stone-800 text-sky-300 border border-stone-700"
                              >
                                {cat}
                              </span>
                            ))
                          ) : (
                            <span className="text-[11px] text-stone-500">None (Task sanitized or blocked)</span>
                          )}
                        </div>
                      </div>
                    </div>

                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs pt-1">
                      {/* SELECTED_MEMORIES */}
                      <div className="p-3.5 rounded-2xl bg-emerald-950/20 border border-emerald-900/30 space-y-2">
                        <span className="font-semibold text-emerald-300 block text-xs">
                          SELECTED_MEMORIES ({log.selectedMemories?.length || log.admittedMemories.length})
                        </span>
                        {(!log.selectedMemories || log.selectedMemories.length === 0) && log.admittedMemories.length === 0 ? (
                          <span className="text-[11px] text-stone-500 italic">None admitted</span>
                        ) : (
                          <div className="space-y-1.5 max-h-48 overflow-y-auto pr-1">
                            {(log.selectedMemories || log.admittedMemories).map((adm, aIdx) => (
                              <div key={aIdx} className="text-[11px] text-stone-300 p-2 rounded-xl bg-stone-900/60 border border-emerald-950">
                                <div className="flex items-center justify-between mb-0.5">
                                  <strong className="text-emerald-400 font-mono text-[10px]">{adm.key}</strong>
                                  <span className="text-[10px] text-stone-400 capitalize">
                                    {(adm as any).category || (adm as any).domain}
                                  </span>
                                </div>
                                {(adm as any).value && (
                                  <p className="text-stone-300 text-[11px] line-clamp-2">"{(adm as any).value}"</p>
                                )}
                                <p className="text-[10px] text-stone-400 italic mt-0.5">{adm.justification}</p>
                              </div>
                            ))}
                          </div>
                        )}
                      </div>

                      {/* EXCLUDED_MEMORIES + SENSITIVE_DATA_BLOCKED */}
                      <div className="p-3.5 rounded-2xl bg-rose-950/20 border border-rose-900/30 space-y-2">
                        <span className="font-semibold text-rose-300 block text-xs">
                          EXCLUDED_MEMORIES ({log.excludedMemories?.length || log.redactedMemories.length})
                        </span>
                        {(!log.excludedMemories || log.excludedMemories.length === 0) && log.redactedMemories.length === 0 ? (
                          <span className="text-[11px] text-stone-500 italic">None redacted</span>
                        ) : (
                          <div className="space-y-1.5 max-h-48 overflow-y-auto pr-1">
                            {(log.excludedMemories || log.redactedMemories).map((red, rIdx) => (
                              <div key={rIdx} className="text-[11px] text-stone-300 p-2 rounded-xl bg-stone-900/60 border border-rose-950">
                                <div className="flex items-center justify-between mb-0.5">
                                  <strong className="text-rose-400 font-mono text-[10px]">{red.key}</strong>
                                  <span className="text-[10px] text-stone-400">
                                    {(red as any).category || 'domain withheld'}
                                  </span>
                                </div>
                                <p className="text-[10px] text-stone-400">
                                  <strong className="text-rose-300">EXCLUSION_REASON:</strong> {(red as any).exclusionReason || (red as any).redactionReason}
                                </p>
                              </div>
                            ))}
                          </div>
                        )}

                        {/* SENSITIVE_DATA_BLOCKED Badge */}
                        {log.sensitiveDataBlocked && log.sensitiveDataBlocked.length > 0 && (
                          <div className="pt-2 border-t border-rose-900/40">
                            <span className="text-[10px] uppercase font-mono font-bold text-rose-400 block mb-1">
                              SENSITIVE_DATA_BLOCKED ({log.sensitiveDataBlocked.length})
                            </span>
                            <div className="flex flex-wrap gap-1">
                              {log.sensitiveDataBlocked.map((sens, sIdx) => (
                                <span
                                  key={sIdx}
                                  className="px-2 py-0.5 rounded text-[10px] font-mono bg-rose-900/30 text-rose-300 border border-rose-800/40"
                                  title={sens.reason}
                                >
                                  {sens.key} ({sens.sensitivity})
                                </span>
                              ))}
                            </div>
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}

      {/* SUB-VIEW 3: PERSONAL TASTE GRAPH */}
      {activeSubTab === 'TASTE' && (
        <div className="space-y-6">
          <div>
            <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-rose-400 mb-1">
              <Network className="w-3.5 h-3.5" />
              <span>Cross-Domain Preference Model</span>
            </div>
            <h2 className="text-2xl font-bold text-stone-100">Personal Taste Graph</h2>
            <p className="text-xs sm:text-sm text-stone-400 mt-1 max-w-xl">
              Taste is contextual: you might love ambient minimalism while stressed, but crave bedroom pop and indie rock at an intimate live show.
            </p>
          </div>

          {/* Taste Nodes Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {tasteNodes.map(node => (
              <div
                key={node.id}
                className="p-5 rounded-3xl bg-stone-900 border border-stone-800 space-y-3 shadow-md flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between gap-2 mb-1.5">
                    <span className="text-[10px] uppercase font-mono px-2 py-0.5 rounded bg-stone-800 text-stone-400">
                      {node.domain}
                    </span>
                    <span
                      className={`text-xs font-bold px-2 py-0.5 rounded-md ${
                        node.relation === 'LOVES'
                          ? 'bg-rose-500/20 text-rose-300'
                          : node.relation === 'AVOIDS'
                          ? 'bg-stone-800 text-stone-500'
                          : 'bg-amber-500/20 text-amber-300'
                      }`}
                    >
                      {node.relation}
                    </span>
                  </div>

                  <h3 className="text-base font-bold text-stone-100">{node.name}</h3>

                  {node.contextCondition && (
                    <span className="text-xs text-sky-400 block mt-1">
                      Context: {node.contextCondition}
                    </span>
                  )}
                </div>

                <div className="space-y-2 pt-2 border-t border-stone-850">
                  <div className="flex justify-between items-center text-[11px] text-stone-400">
                    <span>Affinity Strength</span>
                    <span className="font-mono text-stone-200">{Math.round(node.strength * 100)}%</span>
                  </div>
                  <div className="w-full h-1.5 rounded-full bg-stone-800 overflow-hidden">
                    <div
                      className="h-full bg-gradient-to-r from-amber-400 to-rose-400 rounded-full"
                      style={{ width: `${node.strength * 100}%` }}
                    />
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* SUB-VIEW 4: INTEGRATIONS HUB */}
      {activeSubTab === 'INTEGRATIONS' && (
        <div className="space-y-6">
          <div>
            <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-sky-400 mb-1">
              <Radio className="w-3.5 h-3.5" />
              <span>Provider Adapters & Real Status</span>
            </div>
            <h2 className="text-2xl font-bold text-stone-100">Integrations Hub</h2>
            <p className="text-xs sm:text-sm text-stone-400 mt-1 max-w-xl">
              Moodify never pretends a mocked integration is live. Provider adapters follow strict risk levels and progressive permission requests.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {integrations.map(int => (
              <div
                key={int.id}
                className="p-5 rounded-3xl bg-stone-900 border border-stone-800 space-y-4 shadow-md flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-center justify-between gap-2 mb-2">
                    <h3 className="text-base font-bold text-stone-100">{int.name}</h3>
                    <span
                      className={`text-[10px] font-mono font-bold px-2 py-0.5 rounded-full uppercase tracking-wider ${
                        int.status === 'LIVE'
                          ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                          : int.status === 'MOCK'
                          ? 'bg-amber-500/20 text-amber-300 border border-amber-500/30'
                          : 'bg-stone-800 text-stone-400'
                      }`}
                    >
                      {int.status}
                    </span>
                  </div>

                  <p className="text-xs text-stone-400 leading-relaxed">{int.description}</p>

                  <div className="mt-3 p-3 rounded-2xl bg-stone-850/60 border border-stone-800 text-[11px] text-stone-400 space-y-1">
                    <span className="font-semibold text-stone-300 block">Privacy Guarantee:</span>
                    <span>{int.privacyNotes}</span>
                  </div>
                </div>

                <div className="pt-2 border-t border-stone-850 flex items-center justify-between text-xs">
                  <span className="text-[11px] text-stone-400">
                    {int.isOptional ? 'Optional Integration' : 'Core Architecture Adapter'}
                  </span>
                  <button
                    onClick={() => toggleIntegrationConnection(int.id)}
                    className="px-3 py-1.5 rounded-xl bg-stone-800 hover:bg-stone-750 text-stone-200 font-medium text-xs border border-stone-700 transition-colors"
                  >
                    {int.status === 'NOT_CONNECTED' ? 'Connect' : 'Toggle Mock'}
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* SUB-VIEW 5: PRIVACY CENTER */}
      {activeSubTab === 'PRIVACY' && (
        <div className="space-y-6">
          <div>
            <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-purple-400 mb-1">
              <Lock className="w-3.5 h-3.5" />
              <span>First-Class Trust Architecture</span>
            </div>
            <h2 className="text-2xl font-bold text-stone-100">Privacy Center</h2>
            <p className="text-xs sm:text-sm text-stone-400 mt-1 max-w-xl">
              Privacy is an essential product pillar. Control your data footprint, sensitive boundaries, and private sessions without legal boilerplate.
            </p>
          </div>

          <div className="p-6 rounded-3xl bg-stone-900 border border-stone-800 space-y-5">
            {/* Private Session Toggle */}
            <div className="flex items-center justify-between pb-4 border-b border-stone-800">
              <div className="pr-4">
                <span className="text-sm font-semibold text-stone-100 flex items-center gap-2">
                  <EyeOff className="w-4 h-4 text-purple-400" />
                  Private Session (Incognito)
                </span>
                <span className="text-xs text-stone-400 mt-0.5 block max-w-lg leading-relaxed">
                  When enabled, conversations are not converted into durable memories, recommendation learning is paused, and temporary context expires immediately.
                </span>
              </div>
              <input
                type="checkbox"
                checked={privacySettings.isPrivateSession}
                onChange={e => updatePrivacySettings({ isPrivateSession: e.target.checked })}
                className="w-5 h-5 rounded text-purple-600 focus:ring-purple-400"
              />
            </div>

            {/* Master Personalization */}
            <div className="flex items-center justify-between pb-4 border-b border-stone-800">
              <div className="pr-4">
                <span className="text-sm font-semibold text-stone-100">Master Personalization</span>
                <span className="text-xs text-stone-400 mt-0.5 block max-w-lg">
                  Allow Moodify to use approved vault memories to personalize recommendations.
                </span>
              </div>
              <input
                type="checkbox"
                checked={privacySettings.masterPersonalizationEnabled}
                onChange={e =>
                  updatePrivacySettings({ masterPersonalizationEnabled: e.target.checked })
                }
                className="w-5 h-5 rounded text-amber-500 focus:ring-amber-400"
              />
            </div>

            {/* Allow Sensitive Memories */}
            <div className="flex items-center justify-between pb-4 border-b border-stone-800">
              <div className="pr-4">
                <span className="text-sm font-semibold text-stone-100">
                  Allow Sensitive Memories for Recommendations
                </span>
                <span className="text-xs text-stone-400 mt-0.5 block max-w-lg">
                  When off, memories marked SENSITIVE or HIGHLY_SENSITIVE are strictly excluded from recommendation scoring.
                </span>
              </div>
              <input
                type="checkbox"
                checked={privacySettings.allowSensitiveMemoriesForRecommendations}
                onChange={e =>
                  updatePrivacySettings({
                    allowSensitiveMemoriesForRecommendations: e.target.checked,
                  })
                }
                className="w-5 h-5 rounded text-amber-500 focus:ring-amber-400"
              />
            </div>

            {/* Application Level Encryption */}
            <div className="p-4 rounded-2xl bg-stone-850/60 border border-stone-800 flex items-center justify-between">
              <div>
                <span className="text-xs font-semibold text-stone-200 block">
                  Application-Level Encryption
                </span>
                <span className="text-[11px] text-stone-400">
                  Target production design: field/envelope encryption for sensitive server-side memory.
                </span>
              </div>
              <span className="text-xs text-amber-400 font-mono px-2 py-0.5 rounded bg-amber-950/60 border border-amber-900/40">
                NOT IMPLEMENTED IN LOCAL PROTOTYPE
              </span>
            </div>
          </div>
        </div>
      )}

      {/* SUB-VIEW 6: PROACTIVITY & NOTIFICATION CONTROLS */}
      {activeSubTab === 'PROACTIVE' && (
        <div className="space-y-6">
          <div>
            <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-amber-400 mb-1">
              <Bell className="w-3.5 h-3.5" />
              <span>Proactive Companion Boundaries</span>
            </div>
            <h2 className="text-2xl font-bold text-stone-100">Proactive Settings</h2>
            <p className="text-xs sm:text-sm text-stone-400 mt-1 max-w-xl">
              Moodify initiates helpful contact (e.g. "How did that meeting go?") without manipulative dependency tricks or notification fatigue.
            </p>
          </div>

          <div className="p-6 rounded-3xl bg-stone-900 border border-stone-800 space-y-5">
            {/* Live Proactivity Status Card */}
            {(() => {
              const pingStatus = ProactiveService.canSendProactivePing(proactiveSettings);
              const count = ProactiveService.getPingsSentTodayCount();
              const maxCount = ProactiveService.getEffectiveMaxPings(proactiveSettings);

              return (
                <div className="p-4 rounded-2xl bg-stone-850/70 border border-stone-800 space-y-2.5">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-semibold uppercase tracking-wider text-stone-300">
                      Live Engine Status & Frequency Guardrail
                    </span>
                    <span
                      className={`text-[10px] font-mono font-bold px-2 py-0.5 rounded-full ${
                        pingStatus.allowed
                          ? 'bg-emerald-950/60 text-emerald-400 border border-emerald-900/50'
                          : 'bg-amber-950/60 text-amber-400 border border-amber-900/50'
                      }`}
                    >
                      {pingStatus.allowed ? 'PINGS PERMITTED' : 'PINGS BLOCKED'}
                    </span>
                  </div>

                  <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 text-xs">
                    <div>
                      <span className="text-stone-400 block text-[11px]">Today's Ping Frequency Cap</span>
                      <span className="font-mono text-stone-200 font-semibold">
                        {count} / {maxCount} proactive messages dispatched
                      </span>
                    </div>
                    <div>
                      <span className="text-stone-400 block text-[11px]">Engine Reason / Gate</span>
                      <span className="text-stone-300">
                        {pingStatus.allowed
                          ? 'Active within respectful engagement window'
                          : pingStatus.reason}
                      </span>
                    </div>
                  </div>
                </div>
              );
            })()}

            {/* Proactivity Intensity */}
            <div>
              <label className="block text-xs font-semibold text-stone-300 uppercase tracking-wider mb-2">
                Proactivity Intensity Setting
              </label>
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                {[
                  { id: 'QUIET' as const, label: 'Quiet', desc: 'Reactive only. Speaks when spoken to.' },
                  { id: 'BALANCED' as const, label: 'Balanced', desc: 'Max 2-3 thoughtful prompts per day.' },
                  { id: 'COMPANION' as const, label: 'Companion', desc: 'Attuned check-ins, wind-downs, and tour alerts.' },
                ].map(mode => (
                  <button
                    key={mode.id}
                    onClick={() => updateProactiveSettings({ mode: mode.id })}
                    className={`p-4 rounded-2xl border text-left transition-all ${
                      proactiveSettings.mode === mode.id
                        ? 'bg-amber-500/20 border-amber-500/50 text-stone-100'
                        : 'bg-stone-850 border-stone-800 text-stone-400 hover:text-stone-200'
                    }`}
                  >
                    <span className="font-bold text-sm block">{mode.label}</span>
                    <span className="text-xs text-stone-400 mt-1 block">{mode.desc}</span>
                  </button>
                ))}
              </div>
            </div>

            {/* Quiet Hours */}
            <div className="pt-4 border-t border-stone-800 grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs text-stone-400 mb-1">Quiet Hours Start</label>
                <input
                  type="text"
                  value={proactiveSettings.quietHoursStart}
                  onChange={e => updateProactiveSettings({ quietHoursStart: e.target.value })}
                  className="w-full p-2.5 rounded-xl bg-stone-800 border border-stone-700 text-xs text-stone-100 font-mono"
                />
              </div>
              <div>
                <label className="block text-xs text-stone-400 mb-1">Quiet Hours End</label>
                <input
                  type="text"
                  value={proactiveSettings.quietHoursEnd}
                  onChange={e => updateProactiveSettings({ quietHoursEnd: e.target.value })}
                  className="w-full p-2.5 rounded-xl bg-stone-800 border border-stone-700 text-xs text-stone-100 font-mono"
                />
              </div>
            </div>

            {/* Category Permissions */}
            <div className="pt-4 border-t border-stone-800 space-y-3">
              <label className="flex items-center justify-between p-3 rounded-2xl bg-stone-850/50 border border-stone-800 cursor-pointer">
                <span className="text-xs text-stone-200 font-medium">Important Meeting Follow-ups</span>
                <input
                  type="checkbox"
                  checked={proactiveSettings.allowMeetingFollowUps}
                  onChange={e => updateProactiveSettings({ allowMeetingFollowUps: e.target.checked })}
                  className="w-4 h-4 rounded text-amber-500"
                />
              </label>

              <label className="flex items-center justify-between p-3 rounded-2xl bg-stone-850/50 border border-stone-800 cursor-pointer">
                <span className="text-xs text-stone-200 font-medium">Concert & Venue Drops</span>
                <input
                  type="checkbox"
                  checked={proactiveSettings.allowConcertAlerts}
                  onChange={e => updateProactiveSettings({ allowConcertAlerts: e.target.checked })}
                  className="w-4 h-4 rounded text-amber-500"
                />
              </label>

              <label className="flex items-center justify-between p-3 rounded-2xl bg-stone-850/50 border border-stone-800 cursor-pointer">
                <span className="text-xs text-stone-200 font-medium">Evening Wind-Down Suggestions</span>
                <input
                  type="checkbox"
                  checked={proactiveSettings.allowWindDownSuggestions}
                  onChange={e => updateProactiveSettings({ allowWindDownSuggestions: e.target.checked })}
                  className="w-4 h-4 rounded text-amber-500"
                />
              </label>
            </div>
          </div>
        </div>
      )}

      {/* Memory Edit Modal */}
      {editingMemory && (
        <MemoryEditModal
          memory={editingMemory}
          onClose={() => setEditingMemory(null)}
          onSave={updateMemory}
          onDelete={deleteMemory}
        />
      )}
    </div>
  );
};
