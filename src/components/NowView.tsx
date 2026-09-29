import React from 'react';
import { useApp } from '../context/AppContext';
import {
  Sparkles,
  Battery,
  Flame,
  Brain,
  Moon,
  Calendar,
  Compass,
  ArrowRight,
  Info,
  Clock,
  CloudRain,
  Sliders,
  CheckCircle2,
} from 'lucide-react';

export const NowView: React.FC = () => {
  const {
    user,
    context,
    updateContextDimensions,
    recommendations,
    setInspectingWhyThisItem,
    setCurrentTab,
    applyRecommendationFeedback,
    pendingActionConfirmation,
    setPendingActionConfirmation,
    actionPlans,
  } = useApp();

  const topRec = recommendations[0] || recommendations[1];
  const upcomingAction = actionPlans.find(a => a.status === 'AWAITING_CONFIRMATION') || actionPlans[0];

  return (
    <div className="max-w-4xl mx-auto px-4 py-8 space-y-8 pb-24 sm:pb-12">
      {/* 1. Alive Greeting & Atmospheric Presence */}
      <div className="space-y-2">
        <div className="flex items-center gap-2 text-xs font-medium text-amber-400">
          <Sparkles className="w-3.5 h-3.5" />
          <span>{context.timeOfDay.replace('_', ' ')} · {context.dayOfWeek}</span>
        </div>
        <h1 className="text-3xl sm:text-4xl font-bold tracking-tight text-stone-100">
          Good evening, {user.preferredName}.
        </h1>
        <p className="text-stone-400 text-sm sm:text-base max-w-xl leading-relaxed">
          {context.secondaryState || 'Your afternoon design review is wrapped up. No further calendar obligations for the night.'}
        </p>
      </div>

      {/* 2. Contextual State Card & Subtle Check-In */}
      <div className="p-6 rounded-3xl bg-stone-900 border border-stone-800 shadow-xl space-y-6">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-stone-800/80">
          <div className="flex items-center gap-3">
            <div className="p-3 rounded-2xl bg-amber-500/10 text-amber-400 border border-amber-500/20">
              <Battery className="w-6 h-6" />
            </div>
            <div>
              <span className="text-xs uppercase tracking-wider font-semibold text-stone-400 block">
                Current Context Assessment
              </span>
              <div className="flex items-center gap-2 mt-0.5">
                <h3 className="text-lg font-semibold text-stone-100">
                  {context.primaryState.replace(/_/g, ' ')}
                </h3>
                <span className="text-[11px] font-mono px-2 py-0.5 rounded-full bg-stone-800 text-stone-400">
                  Source: {context.contextSource.replace('_', ' ')}
                </span>
              </div>
            </div>
          </div>

          <div className="flex items-center gap-2 text-xs text-stone-400">
            <Clock className="w-4 h-4 text-stone-400" />
            <span>Free time tonight: ~{context.freeHoursRemainingToday || 4.5} hrs</span>
          </div>
        </div>

        {/* Dimension Vectors */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-4">
          <div className="p-3.5 rounded-2xl bg-stone-850/60 border border-stone-800">
            <div className="flex items-center justify-between text-xs text-stone-400 mb-2">
              <span>Energy</span>
              <span className="font-mono text-stone-200">{Math.round(context.dimensions.energy * 100)}%</span>
            </div>
            <div className="w-full h-1.5 rounded-full bg-stone-800 overflow-hidden">
              <div
                className="h-full bg-amber-400 rounded-full"
                style={{ width: `${context.dimensions.energy * 100}%` }}
              />
            </div>
          </div>

          <div className="p-3.5 rounded-2xl bg-stone-850/60 border border-stone-800">
            <div className="flex items-center justify-between text-xs text-stone-400 mb-2">
              <span>Stress / Load</span>
              <span className="font-mono text-stone-200">{Math.round(context.dimensions.stress * 100)}%</span>
            </div>
            <div className="w-full h-1.5 rounded-full bg-stone-800 overflow-hidden">
              <div
                className="h-full bg-rose-400 rounded-full"
                style={{ width: `${context.dimensions.stress * 100}%` }}
              />
            </div>
          </div>

          <div className="p-3.5 rounded-2xl bg-stone-850/60 border border-stone-800">
            <div className="flex items-center justify-between text-xs text-stone-400 mb-2">
              <span>Focus Need</span>
              <span className="font-mono text-stone-200">
                {context.dimensions.focusNeed < 0.3 ? 'Low Effort' : 'Active'}
              </span>
            </div>
            <div className="w-full h-1.5 rounded-full bg-stone-800 overflow-hidden">
              <div
                className="h-full bg-sky-400 rounded-full"
                style={{ width: `${context.dimensions.focusNeed * 100}%` }}
              />
            </div>
          </div>

          <div className="p-3.5 rounded-2xl bg-stone-850/60 border border-stone-850">
            <div className="flex items-center justify-between text-xs text-stone-400 mb-2">
              <span>Weather</span>
              <CloudRain className="w-3.5 h-3.5 text-sky-400" />
            </div>
            <span className="text-xs text-stone-300 font-medium line-clamp-1">
              {context.weatherSummary || '62°F, Light Rain'}
            </span>
          </div>
        </div>

        {/* Subtle 1-Tap Mood Check-In */}
        <div className="pt-2 border-t border-stone-800/80">
          <div className="flex items-center justify-between text-xs text-stone-400 mb-2.5">
            <span className="flex items-center gap-1.5 font-medium">
              <Sliders className="w-3.5 h-3.5 text-amber-400" />
              Adjust your current state (Self-report takes highest authority)
            </span>
          </div>
          <div className="flex flex-wrap gap-2">
            {[
              { label: 'Exhausted & Drained', energy: 0.15, stress: 0.8, state: 'LOW_BATTERY' as const },
              { label: 'Peaceful & Resetting', energy: 0.5, stress: 0.2, state: 'WINDING_DOWN' as const },
              { label: 'Energized & Social', energy: 0.85, stress: 0.15, state: 'ANTICIPATING_WEEKEND' as const },
              { label: 'Curious & Exploratory', energy: 0.65, stress: 0.3, state: 'EXPLORATORY' as const },
            ].map((btn, idx) => (
              <button
                key={idx}
                onClick={() => updateContextDimensions({ energy: btn.energy, stress: btn.stress }, btn.state)}
                className={`px-3 py-1.5 rounded-xl text-xs font-medium border transition-colors ${
                  context.primaryState === btn.state
                    ? 'bg-amber-500/20 text-amber-300 border-amber-500/40'
                    : 'bg-stone-850 text-stone-400 border-stone-750 hover:bg-stone-800 hover:text-stone-200'
                }`}
              >
                {btn.label}
              </button>
            ))}
          </div>
        </div>
      </div>

      {/* 3. The One Highly Relevant Recommendation Right Now */}
      {topRec && (
        <div className="space-y-3">
          <div className="flex items-center justify-between">
            <span className="text-xs uppercase tracking-wider font-semibold text-stone-400 flex items-center gap-1.5">
              <Sparkles className="w-3.5 h-3.5 text-amber-400" />
              Curated For Right Now
            </span>
            <button
              onClick={() => setCurrentTab('DISCOVER')}
              className="text-xs font-medium text-amber-400 hover:text-amber-300 flex items-center gap-1 transition-colors"
            >
              <span>Explore all domains</span>
              <ArrowRight className="w-3.5 h-3.5" />
            </button>
          </div>

          <div className="relative rounded-3xl bg-stone-900 border border-stone-800 overflow-hidden shadow-2xl flex flex-col md:flex-row">
            {/* Image / Ambient visual */}
            {topRec.imageUrl && (
              <div className="md:w-5/12 h-48 md:h-auto relative overflow-hidden bg-stone-800">
                <img
                  src={topRec.imageUrl}
                  alt={topRec.title}
                  className="w-full h-full object-cover opacity-90 transition-transform hover:scale-105 duration-500"
                />
                <div className="absolute inset-0 bg-gradient-to-t md:bg-gradient-to-r from-black/80 via-black/30 to-transparent" />
                {topRec.badge && (
                  <span className="absolute top-4 left-4 px-3 py-1 rounded-full text-xs font-semibold bg-amber-500/90 text-stone-950 backdrop-blur-md">
                    {topRec.badge}
                  </span>
                )}
              </div>
            )}

            {/* Body */}
            <div className="p-6 md:p-8 flex-1 flex flex-col justify-between space-y-4">
              <div>
                <div className="flex items-center gap-2 mb-1.5">
                  <span className="text-xs font-mono font-medium text-amber-400 uppercase tracking-wider">
                    {topRec.category} · {topRec.metadata.genreOrCuisine || topRec.domain}
                  </span>
                  {topRec.metadata.rating && (
                    <span className="text-xs text-stone-400">· {topRec.metadata.rating}</span>
                  )}
                </div>
                <h2 className="text-xl sm:text-2xl font-bold text-stone-100">{topRec.title}</h2>
                <p className="text-xs text-stone-400 mt-1 font-medium">{topRec.subtitle}</p>
                <p className="text-stone-300 text-sm mt-3 leading-relaxed">{topRec.description}</p>
              </div>

              {/* Action and Why This Button */}
              <div className="pt-4 border-t border-stone-800 flex flex-wrap items-center justify-between gap-3">
                <button
                  onClick={() => setInspectingWhyThisItem(topRec)}
                  className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-stone-800 hover:bg-stone-750 text-stone-300 hover:text-stone-100 text-xs font-medium border border-stone-700 transition-colors"
                >
                  <Info className="w-3.5 h-3.5 text-amber-400" />
                  <span>Why this?</span>
                </button>

                <div className="flex items-center gap-2">
                  <button
                    onClick={() => applyRecommendationFeedback(topRec, 'LOVE_IT')}
                    className="px-3 py-1.5 rounded-xl bg-stone-850 hover:bg-stone-800 text-stone-300 text-xs font-medium border border-stone-800"
                  >
                    Love it
                  </button>
                  <button
                    onClick={() => applyRecommendationFeedback(topRec, 'SAVE_FOR_LATER')}
                    className="px-4 py-1.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-stone-950 font-semibold text-xs transition-colors shadow-md shadow-amber-500/20"
                  >
                    {topRec.actionPrompt || 'Save to Plans'}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 4. One Optional High-Value Action Banner */}
      {upcomingAction && (
        <div className="p-5 rounded-3xl bg-gradient-to-r from-stone-900 to-stone-850 border border-stone-800 flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-2xl bg-indigo-500/10 text-indigo-400 border border-indigo-500/20 shrink-0">
              <Calendar className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="text-[10px] font-semibold uppercase tracking-wider text-indigo-400">
                  Ready Action · {upcomingAction.targetProvider}
                </span>
                <span className="text-[10px] px-2 py-0.5 rounded bg-stone-800 text-stone-400">
                  {upcomingAction.riskLevel.replace('_', ' ')}
                </span>
              </div>
              <h4 className="text-sm font-semibold text-stone-200 mt-0.5">{upcomingAction.title}</h4>
              <p className="text-xs text-stone-400 mt-0.5 line-clamp-1">{upcomingAction.description}</p>
            </div>
          </div>

          <button
            onClick={() => setPendingActionConfirmation(upcomingAction)}
            className="px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-medium text-xs transition-colors shrink-0 shadow-lg shadow-indigo-600/20"
          >
            Review & Authorize
          </button>
        </div>
      )}

      {/* 5. Subtle Check-In Banner */}
      <div className="p-5 rounded-3xl bg-stone-900/60 border border-stone-800 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="p-2 rounded-xl bg-amber-500/10 text-amber-400">
            <Moon className="w-4 h-4" />
          </div>
          <div>
            <span className="text-xs font-semibold text-stone-200 block">
              Want to talk about how the review went?
            </span>
            <span className="text-xs text-stone-400">
              Moodify keeps your context ready without pestering.
            </span>
          </div>
        </div>

        <button
          onClick={() => setCurrentTab('CHAT')}
          className="px-4 py-2 rounded-xl bg-stone-800 hover:bg-stone-750 text-stone-200 text-xs font-medium border border-stone-700 transition-colors"
        >
          Open Chat
        </button>
      </div>
    </div>
  );
};
