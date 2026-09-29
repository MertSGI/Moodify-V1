import React from 'react';
import { useApp } from '../context/AppContext';
import { Play, Sparkles, Moon, Calendar, ShoppingBag, MessageSquare, Film, Music2 } from 'lucide-react';

export const ScenarioDrawer: React.FC = () => {
  const { triggerScenario } = useApp();

  const scenarios = [
    {
      id: 'A' as const,
      title: 'Scenario A: Rough Day',
      subtitle: '"Today was awful. I don\'t really want to think."',
      icon: Moon,
      tag: 'Low Energy & Stress',
      color: 'from-amber-500/20 to-rose-500/20 text-amber-300 border-amber-500/30',
    },
    {
      id: 'B' as const,
      title: 'Scenario B: Friday Night',
      subtitle: 'Free evening + intimate indie rock concert + calendar',
      icon: Calendar,
      tag: 'Concert & Calendar Hold',
      color: 'from-indigo-500/20 to-sky-500/20 text-indigo-300 border-indigo-500/30',
    },
    {
      id: 'C' as const,
      title: 'Scenario C: Product Discovery',
      subtitle: 'Savory workday snacks under $25 budget',
      icon: ShoppingBag,
      tag: 'Taste & Budget Guardrails',
      color: 'from-emerald-500/20 to-teal-500/20 text-emerald-300 border-emerald-500/30',
    },
    {
      id: 'D' as const,
      title: 'Scenario D: Meeting Follow-Up',
      subtitle: 'Gentle check-in on the 2 PM design review with Marcus',
      icon: MessageSquare,
      tag: 'Explainable Provenance',
      color: 'from-orange-500/20 to-amber-500/20 text-orange-300 border-orange-500/30',
    },
    {
      id: 'E' as const,
      title: 'Scenario E: Movie Night',
      subtitle: '3 differentiated films for tired but curious mood',
      icon: Film,
      tag: 'Multi-Choice Curation',
      color: 'from-purple-500/20 to-pink-500/20 text-purple-300 border-purple-500/30',
    },
    {
      id: 'F' as const,
      title: 'Scenario F: Concert Watch',
      subtitle: 'Track Japanese Breakfast intimate tour radar',
      icon: Music2,
      tag: 'Live Event Tracking',
      color: 'from-rose-500/20 to-red-500/20 text-rose-300 border-rose-500/30',
    },
  ];

  return (
    <div className="w-full bg-stone-900/90 border-b border-stone-800 px-4 py-3 backdrop-blur-md">
      <div className="max-w-6xl mx-auto">
        <div className="flex items-center justify-between mb-2">
          <div className="flex items-center gap-2">
            <span className="inline-flex items-center justify-center p-1 rounded-md bg-amber-500/10 text-amber-400">
              <Sparkles className="w-3.5 h-3.5" />
            </span>
            <span className="text-xs font-semibold uppercase tracking-wider text-stone-300">
              Interactive Test Scenarios
            </span>
            <span className="text-[11px] text-stone-400 hidden sm:inline">
              (1-Click triggers for evaluation)
            </span>
          </div>
          <span className="text-[11px] text-amber-400/80 font-mono">6 Scenarios Ready</span>
        </div>

        {/* Scrollable scenario buttons */}
        <div className="flex gap-2.5 overflow-x-auto pb-1 no-scrollbar">
          {scenarios.map(sc => {
            const Icon = sc.icon;
            return (
              <button
                key={sc.id}
                onClick={() => triggerScenario(sc.id)}
                className={`shrink-0 flex items-center gap-2.5 px-3 py-2 rounded-2xl border bg-gradient-to-r text-left transition-all hover:scale-[1.02] active:scale-[0.98] ${sc.color}`}
              >
                <div className="p-1.5 rounded-xl bg-stone-900/80 shrink-0">
                  <Icon className="w-3.5 h-3.5" />
                </div>
                <div>
                  <div className="flex items-center gap-1.5">
                    <span className="text-xs font-semibold text-stone-100">{sc.title}</span>
                    <Play className="w-2.5 h-2.5 fill-current opacity-70" />
                  </div>
                  <span className="text-[10px] text-stone-400 block line-clamp-1 max-w-[200px]">
                    {sc.subtitle}
                  </span>
                </div>
              </button>
            );
          })}
        </div>
      </div>
    </div>
  );
};
