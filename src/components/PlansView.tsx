import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import {
  BookmarkCheck,
  Calendar,
  Film,
  ShoppingBag,
  Ticket,
  Plus,
  CheckCircle2,
  Clock,
  ExternalLink,
  ShieldAlert,
} from 'lucide-react';
import { PlanItem } from '../types/actions';

export const PlansView: React.FC = () => {
  const {
    plans,
    actionPlans,
    togglePlanItemChecked,
    setPendingActionConfirmation,
    addPlanItem,
  } = useApp();

  const [activeTab, setActiveTab] = useState<'ALL' | 'SHOPPING' | 'WATCH' | 'EVENTS' | 'ACTIVITIES'>('ALL');
  const [newPlanTitle, setNewPlanTitle] = useState('');

  const filteredPlans = plans.filter(p => {
    if (activeTab === 'SHOPPING') return p.type === 'SHOPPING_LIST';
    if (activeTab === 'WATCH') return p.type === 'WATCH_LATER';
    if (activeTab === 'EVENTS') return p.type === 'EVENT';
    if (activeTab === 'ACTIVITIES') return p.type === 'ACTIVITY' || p.type === 'IDEA';
    return true;
  });

  const handleQuickAdd = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newPlanTitle.trim()) return;
    addPlanItem({
      title: newPlanTitle,
      type: 'IDEA',
      category: 'Personal Goal',
      notes: 'Added from Plans tab.',
    });
    setNewPlanTitle('');
  };

  return (
    <div className="max-w-5xl mx-auto px-4 py-8 space-y-8 pb-24 sm:pb-12">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4 pb-6 border-b border-stone-850">
        <div>
          <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-amber-400 mb-1">
            <BookmarkCheck className="w-3.5 h-3.5" />
            <span>Action & Execution Layer</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-stone-100">
            Plans & Tracked Intentions
          </h1>
          <p className="text-stone-400 text-xs sm:text-sm mt-1 max-w-xl">
            Where recommendations turn into tangible life actions—groceries, watchlists, calendar holds, and concert tickets.
          </p>
        </div>

        {/* Quick Add Form */}
        <form onSubmit={handleQuickAdd} className="flex gap-2">
          <input
            type="text"
            value={newPlanTitle}
            onChange={e => setNewPlanTitle(e.target.value)}
            placeholder="Add an intention or idea..."
            className="px-3.5 py-2 rounded-xl bg-stone-900 border border-stone-800 text-xs text-stone-100 placeholder-stone-500 focus:outline-none focus:border-amber-400"
          />
          <button
            type="submit"
            className="px-3.5 py-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-stone-950 font-semibold text-xs transition-colors flex items-center gap-1 shrink-0"
          >
            <Plus className="w-3.5 h-3.5" />
            <span>Add</span>
          </button>
        </form>
      </div>

      {/* Pending Action Authorizations (Action Engine) */}
      {actionPlans.some(a => a.status === 'AWAITING_CONFIRMATION') && (
        <div className="space-y-3">
          <span className="text-xs uppercase tracking-wider font-semibold text-indigo-400 flex items-center gap-1.5">
            <ShieldAlert className="w-3.5 h-3.5" />
            <span>Actions Awaiting Your Explicit Approval</span>
          </span>
          <div className="grid grid-cols-1 gap-3">
            {actionPlans
              .filter(a => a.status === 'AWAITING_CONFIRMATION')
              .map(act => (
                <div
                  key={act.id}
                  className="p-5 rounded-3xl bg-indigo-950/20 border border-indigo-900/50 flex flex-col sm:flex-row sm:items-center justify-between gap-4"
                >
                  <div className="flex items-center gap-3">
                    <div className="p-2.5 rounded-2xl bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 shrink-0">
                      <Calendar className="w-5 h-5" />
                    </div>
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="text-[10px] font-mono uppercase tracking-wider font-bold px-2 py-0.5 rounded bg-indigo-900/40 text-indigo-300">
                          {act.riskLevel.replace('_', ' ')}
                        </span>
                        <span className="text-xs text-stone-400">Provider: {act.targetProvider}</span>
                      </div>
                      <h4 className="text-sm font-semibold text-stone-100 mt-1">{act.title}</h4>
                      <p className="text-xs text-stone-300 mt-0.5">{act.description}</p>
                    </div>
                  </div>

                  <button
                    onClick={() => setPendingActionConfirmation(act)}
                    className="px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-semibold text-xs shadow-md transition-colors shrink-0"
                  >
                    Review & Authorize
                  </button>
                </div>
              ))}
          </div>
        </div>
      )}

      {/* Tabs */}
      <div className="flex gap-2 border-b border-stone-850 pb-2 overflow-x-auto no-scrollbar">
        {[
          { id: 'ALL' as const, label: 'All Plans' },
          { id: 'SHOPPING' as const, label: 'Shopping Lists' },
          { id: 'WATCH' as const, label: 'Watch Later' },
          { id: 'EVENTS' as const, label: 'Tracked Events' },
          { id: 'ACTIVITIES' as const, label: 'Activities & Ideas' },
        ].map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            className={`px-3.5 py-1.5 rounded-xl text-xs font-medium transition-colors ${
              activeTab === tab.id
                ? 'bg-stone-800 text-stone-100 font-semibold'
                : 'text-stone-400 hover:text-stone-200'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Plans List */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {filteredPlans.map(plan => (
          <div
            key={plan.id}
            className="p-6 rounded-3xl bg-stone-900 border border-stone-800 shadow-xl flex flex-col justify-between space-y-4 hover:border-stone-750 transition-all"
          >
            <div>
              <div className="flex items-center justify-between gap-2 mb-2">
                <span className="text-[10px] font-mono uppercase tracking-wider font-semibold px-2 py-0.5 rounded bg-stone-800 text-stone-400 border border-stone-750">
                  {plan.type.replace('_', ' ')}
                </span>
                {plan.isCalendarSynced ? (
                  <span className="text-[10px] text-indigo-400 flex items-center gap-1 font-medium">
                    <Calendar className="w-3 h-3" />
                    <span>Calendar Synced</span>
                  </span>
                ) : (
                  <span className="text-[10px] text-stone-500 flex items-center gap-1 font-mono">
                    <Calendar className="w-3 h-3 text-stone-500" />
                    <span>Local Only (No External Sync)</span>
                  </span>
                )}
              </div>

              <h3 className="text-lg font-bold text-stone-100">{plan.title}</h3>
              {plan.notes && <p className="text-xs text-stone-400 mt-1">{plan.notes}</p>}

              {/* Sub-items (e.g. for shopping lists or watchlists) */}
              {plan.items && plan.items.length > 0 && (
                <div className="mt-4 pt-3 border-t border-stone-850 space-y-2">
                  {plan.items.map(item => (
                    <label
                      key={item.id}
                      className="flex items-center justify-between p-2 rounded-xl bg-stone-850/50 hover:bg-stone-850 cursor-pointer text-xs transition-colors"
                    >
                      <div className="flex items-center gap-2.5">
                        <input
                          type="checkbox"
                          checked={item.checked}
                          onChange={() => togglePlanItemChecked(plan.id, item.id)}
                          className="w-4 h-4 rounded text-amber-500 focus:ring-amber-400"
                        />
                        <span
                          className={`font-medium ${
                            item.checked ? 'line-through text-stone-500' : 'text-stone-200'
                          }`}
                        >
                          {item.name}
                        </span>
                      </div>
                      {item.price && (
                        <span className="font-mono text-emerald-400 text-[11px]">{item.price}</span>
                      )}
                    </label>
                  ))}
                </div>
              )}

              {/* Event Date or Venue */}
              {(plan.date || plan.venueOrPlatform) && (
                <div className="mt-3 pt-3 border-t border-stone-850 text-xs text-stone-400 flex items-center gap-3">
                  {plan.date && <span>📅 {new Date(plan.date).toLocaleDateString()}</span>}
                  {plan.venueOrPlatform && <span>📍 {plan.venueOrPlatform}</span>}
                </div>
              )}
            </div>

            <div className="pt-2 flex justify-between items-center text-[11px] text-stone-400">
              <span>Status: {plan.status}</span>
              <span className="capitalize">{plan.category}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
