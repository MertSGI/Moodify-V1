import React, { useState } from 'react';
import { useApp } from '../context/AppContext';
import { RecommendationCategory, RecommendationItem } from '../types/recommendation';
import {
  Sparkles,
  Compass,
  Sliders,
  Music,
  Film,
  Activity,
  MapPin,
  ShoppingBag,
  Ticket,
  Info,
  Heart,
  ThumbsUp,
  XCircle,
  Clock,
  BookmarkCheck,
  BookmarkPlus,
  Flame,
} from 'lucide-react';
import { RecommendationService } from '../services/recommendationService';

export const DiscoverView: React.FC = () => {
  const {
    recommendations,
    context,
    tasteNodes,
    memories,
    explorationFactor,
    setExplorationFactor,
    setInspectingWhyThisItem,
    applyRecommendationFeedback,
  } = useApp();

  const [activeCategory, setActiveCategory] = useState<RecommendationCategory | 'all'>('all');

  const categories = [
    { id: 'all' as const, label: 'For You', icon: Sparkles },
    { id: 'music' as const, label: 'Music', icon: Music },
    { id: 'movies_tv' as const, label: 'Watch', icon: Film },
    { id: 'activities' as const, label: 'Do', icon: Activity },
    { id: 'places' as const, label: 'Go', icon: MapPin },
    { id: 'products' as const, label: 'Buy', icon: ShoppingBag },
    { id: 'events' as const, label: 'Events', icon: Ticket },
  ];

  // Dynamic ranking based on exploration factor and context
  const rankedItems = RecommendationService.getCuration(
    activeCategory,
    context,
    tasteNodes,
    memories,
    explorationFactor,
    recommendations
  );

  return (
    <div className="max-w-6xl mx-auto px-4 py-8 space-y-8 pb-24 sm:pb-12">
      {/* Header & Exploration Controller */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-6 pb-6 border-b border-stone-850">
        <div>
          <div className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wider text-amber-400 mb-1">
            <Compass className="w-3.5 h-3.5" />
            <span>Cross-Domain Taste Engine</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-bold tracking-tight text-stone-100">
            Curated Discovery
          </h1>
          <p className="text-stone-400 text-xs sm:text-sm mt-1 max-w-xl">
            Every item is vetted against your personal taste graph, dietary & venue boundaries, and current cognitive bandwidth.
          </p>
        </div>

        {/* Exploration Factor Slider (Relevance vs Serendipity) */}
        <div className="p-4 rounded-2xl bg-stone-900 border border-stone-800 w-full md:w-80 shadow-md">
          <div className="flex items-center justify-between text-xs mb-2">
            <span className="font-semibold text-stone-300 flex items-center gap-1.5">
              <Sliders className="w-3.5 h-3.5 text-amber-400" />
              Exploration Bubble
            </span>
            <span className="font-mono text-[11px] text-amber-400">
              {explorationFactor < 0.35 ? 'Safe Comfort' : explorationFactor > 0.65 ? 'High Discovery' : 'Balanced'}
            </span>
          </div>
          <input
            type="range"
            min="0"
            max="1"
            step="0.05"
            value={explorationFactor}
            onChange={e => setExplorationFactor(parseFloat(e.target.value))}
            className="w-full accent-amber-400 cursor-pointer h-1.5 bg-stone-800 rounded-lg"
          />
          <div className="flex justify-between text-[10px] text-stone-400 mt-1 font-medium">
            <span>Familiar Favorites</span>
            <span>Wild Serendipity</span>
          </div>
        </div>
      </div>

      {/* Category Tabs */}
      <div className="flex gap-2 overflow-x-auto pb-1 no-scrollbar">
        {categories.map(cat => {
          const Icon = cat.icon;
          const isActive = activeCategory === cat.id;
          return (
            <button
              key={cat.id}
              onClick={() => setActiveCategory(cat.id)}
              className={`shrink-0 flex items-center gap-2 px-4 py-2.5 rounded-2xl text-xs font-semibold transition-all ${
                isActive
                  ? 'bg-amber-500 text-stone-950 shadow-md shadow-amber-500/20'
                  : 'bg-stone-900 text-stone-400 hover:text-stone-200 border border-stone-800 hover:bg-stone-850'
              }`}
            >
              <Icon className="w-4 h-4" />
              <span>{cat.label}</span>
            </button>
          );
        })}
      </div>

      {/* Grid of Recommendation Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {rankedItems.map(item => (
          <div
            key={item.id}
            className="rounded-3xl bg-stone-900 border border-stone-800 overflow-hidden shadow-xl flex flex-col justify-between group hover:border-stone-700 transition-all"
          >
            {/* Image / Header */}
            <div>
              {item.imageUrl && (
                <div className="relative h-44 w-full overflow-hidden bg-stone-800">
                  <img
                    src={item.imageUrl}
                    alt={item.title}
                    className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-stone-900 via-transparent to-black/30" />
                  {item.badge && (
                    <span className="absolute top-3 left-3 px-2.5 py-1 rounded-full text-[10px] font-semibold bg-stone-900/90 text-amber-300 backdrop-blur-md border border-stone-750">
                      {item.badge}
                    </span>
                  )}
                  <span className="absolute top-3 right-3 px-2 py-0.5 rounded-md text-[10px] font-mono font-bold bg-amber-500 text-stone-950">
                    {Math.round(item.score * 100)}% match
                  </span>
                </div>
              )}

              <div className="p-5 space-y-2">
                <div className="flex items-center gap-2 text-[11px] font-medium text-amber-400 uppercase tracking-wider">
                  <span>{item.category.replace('_', ' ')}</span>
                  {item.metadata.genreOrCuisine && <span>· {item.metadata.genreOrCuisine}</span>}
                </div>
                <h3 className="text-base font-bold text-stone-100 group-hover:text-amber-300 transition-colors">
                  {item.title}
                </h3>
                <p className="text-xs text-stone-400 font-medium">{item.subtitle}</p>
                <p className="text-xs text-stone-300 line-clamp-2 leading-relaxed pt-1">
                  {item.description}
                </p>

                {/* Metadata Pills */}
                <div className="flex flex-wrap gap-2 pt-2">
                  {item.metadata.price && (
                    <span className="text-[11px] font-medium px-2 py-0.5 rounded bg-emerald-950/50 text-emerald-300 border border-emerald-900/40">
                      {item.metadata.price}
                    </span>
                  )}
                  {item.metadata.effortLevel && (
                    <span className="text-[11px] px-2 py-0.5 rounded bg-stone-800 text-stone-300 border border-stone-750">
                      Effort: {item.metadata.effortLevel.replace('_', ' ')}
                    </span>
                  )}
                  {item.metadata.rating && (
                    <span className="text-[11px] px-2 py-0.5 rounded bg-stone-800 text-stone-400">
                      {item.metadata.rating}
                    </span>
                  )}
                </div>
              </div>
            </div>

            {/* Bottom Actions & Feedback */}
            <div className="p-5 pt-3 border-t border-stone-850 space-y-3">
              <div className="flex items-center justify-between">
                <button
                  onClick={() => setInspectingWhyThisItem(item)}
                  className="inline-flex items-center gap-1.5 text-xs text-stone-400 hover:text-stone-200 font-medium transition-colors"
                >
                  <Info className="w-3.5 h-3.5 text-amber-400" />
                  <span>Why this?</span>
                </button>

                <button
                  onClick={() => applyRecommendationFeedback(item, 'SAVE_FOR_LATER')}
                  className="inline-flex items-center gap-1 px-3 py-1 rounded-xl bg-stone-800 hover:bg-stone-750 text-stone-200 text-xs font-medium border border-stone-700 transition-colors"
                >
                  <BookmarkPlus className="w-3.5 h-3.5" />
                  <span>{item.actionPrompt || 'Save'}</span>
                </button>
              </div>

              {/* Feedback Bar (updates Taste Graph) */}
              <div className="pt-2 border-t border-stone-800/60 flex items-center justify-between text-[11px] text-stone-400">
                <span className="text-[10px] uppercase font-semibold text-stone-400">Tune taste:</span>
                <div className="flex items-center gap-1">
                  <button
                    onClick={() => applyRecommendationFeedback(item, 'LOVE_IT')}
                    className={`p-1.5 rounded-lg hover:bg-stone-800 transition-colors ${
                      item.feedbackGiven === 'LOVE_IT' ? 'text-rose-400 bg-rose-500/10' : 'text-stone-400'
                    }`}
                    title="Love it"
                  >
                    <Heart className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={() => applyRecommendationFeedback(item, 'LIKE_IT')}
                    className={`p-1.5 rounded-lg hover:bg-stone-800 transition-colors ${
                      item.feedbackGiven === 'LIKE_IT' ? 'text-amber-400 bg-amber-500/10' : 'text-stone-400'
                    }`}
                    title="Like it"
                  >
                    <ThumbsUp className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={() => applyRecommendationFeedback(item, 'NOT_FOR_ME')}
                    className={`p-1.5 rounded-lg hover:bg-stone-800 transition-colors ${
                      item.feedbackGiven === 'NOT_FOR_ME' ? 'text-rose-500 bg-rose-950/20' : 'text-stone-400'
                    }`}
                    title="Not for me (teaches taste graph to avoid)"
                  >
                    <XCircle className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={() => applyRecommendationFeedback(item, 'ALREADY_KNOW_IT')}
                    className="px-2 py-1 rounded text-[10px] hover:bg-stone-800 text-stone-400"
                    title="Already know this"
                  >
                    Already know
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
