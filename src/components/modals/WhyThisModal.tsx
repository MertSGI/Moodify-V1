import React from 'react';
import { RecommendationItem } from '../../types/recommendation';
import { X, Sparkles, Brain, Compass, ShieldCheck, CheckCircle2 } from 'lucide-react';

interface WhyThisModalProps {
  item: RecommendationItem | null;
  onClose: () => void;
}

export const WhyThisModal: React.FC<WhyThisModalProps> = ({ item, onClose }) => {
  if (!item) return null;

  const { whyThis } = item;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="relative w-full max-w-xl bg-stone-900 border border-stone-800 rounded-3xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="p-6 pb-4 border-b border-stone-800 flex items-start justify-between bg-stone-900/90 sticky top-0 z-10">
          <div>
            <div className="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium bg-amber-500/10 text-amber-400 border border-amber-500/20 mb-2">
              <Sparkles className="w-3.5 h-3.5" />
              <span>Transparent Recommendation Breakdown</span>
            </div>
            <h2 className="text-xl font-semibold text-stone-100">{item.title}</h2>
            <p className="text-sm text-stone-400 mt-0.5">{item.subtitle}</p>
          </div>
          <button
            onClick={onClose}
            className="p-2 text-stone-400 hover:text-stone-200 hover:bg-stone-800 rounded-full transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content Body */}
        <div className="p-6 overflow-y-auto space-y-6 text-stone-300 text-sm">
          {/* Executive Summary */}
          <div className="p-4 rounded-2xl bg-amber-950/20 border border-amber-900/30 text-amber-200/90 leading-relaxed">
            <span className="font-semibold text-amber-300 block mb-1">Core Rationale:</span>
            {whyThis.summary}
          </div>

          {/* Matched Memories */}
          {whyThis.matchedMemories.length > 0 && (
            <div>
              <h3 className="text-xs uppercase tracking-wider font-semibold text-stone-400 flex items-center gap-2 mb-3">
                <Brain className="w-4 h-4 text-emerald-400" />
                <span>Memory Vault Provenance ({whyThis.matchedMemories.length})</span>
              </h3>
              <div className="space-y-2.5">
                {whyThis.matchedMemories.map((mem, idx) => (
                  <div
                    key={idx}
                    className="p-3.5 rounded-xl bg-stone-800/60 border border-stone-750 flex items-start justify-between gap-3"
                  >
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="text-xs font-mono font-medium text-emerald-400 bg-emerald-950/60 px-2 py-0.5 rounded-md border border-emerald-900/50">
                          {mem.key}
                        </span>
                        <span className="text-xs text-stone-400 capitalize">Domain: {mem.domain}</span>
                      </div>
                      <p className="text-stone-200 text-sm mt-1.5 font-normal">"{mem.snippet}"</p>
                    </div>
                    <span className="text-[10px] text-stone-400 uppercase tracking-wider font-medium px-2 py-0.5 rounded bg-stone-700/50 whitespace-nowrap">
                      Permitted by Vault
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Context Alignment */}
          {whyThis.contextAlignment.length > 0 && (
            <div>
              <h3 className="text-xs uppercase tracking-wider font-semibold text-stone-400 flex items-center gap-2 mb-3">
                <Compass className="w-4 h-4 text-sky-400" />
                <span>Current Context Alignment</span>
              </h3>
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-2.5">
                {whyThis.contextAlignment.map((ctx, idx) => (
                  <div key={idx} className="p-3 rounded-xl bg-stone-800/40 border border-stone-800">
                    <span className="text-xs font-medium text-sky-300 block">{ctx.dimension}</span>
                    <span className="text-xs text-stone-400 mt-1 block leading-normal">{ctx.reason}</span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Taste Graph Factor */}
          {whyThis.tasteFactor.length > 0 && (
            <div>
              <h3 className="text-xs uppercase tracking-wider font-semibold text-stone-400 flex items-center gap-2 mb-2">
                <span>Taste Graph Affinity Factor</span>
              </h3>
              <div className="space-y-2">
                {whyThis.tasteFactor.map((taste, idx) => (
                  <div key={idx} className="flex items-center justify-between p-3 rounded-xl bg-stone-800/40 border border-stone-800">
                    <div>
                      <span className="font-medium text-stone-200">{taste.tasteNodeName}</span>
                      <span className="text-xs text-stone-400 block">{taste.weightReason}</span>
                    </div>
                    <span className="px-2 py-1 text-xs font-semibold rounded-md bg-rose-500/10 text-rose-300 border border-rose-500/20">
                      {taste.relation}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Constraints Respected */}
          {whyThis.constraintsRespected.length > 0 && (
            <div>
              <h3 className="text-xs uppercase tracking-wider font-semibold text-stone-400 flex items-center gap-2 mb-2">
                <ShieldCheck className="w-4 h-4 text-emerald-400" />
                <span>Constraints Explicitly Respected</span>
              </h3>
              <div className="flex flex-wrap gap-2">
                {whyThis.constraintsRespected.map((constraint, idx) => (
                  <span
                    key={idx}
                    className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-emerald-950/30 text-emerald-300 text-xs border border-emerald-900/40"
                  >
                    <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                    {constraint}
                  </span>
                ))}
              </div>
            </div>
          )}

          {/* Deterministic Scoring Formula Breakdown */}
          {whyThis.scoringBreakdown && (
            <div className="p-4 rounded-2xl bg-stone-850/80 border border-stone-800 space-y-3">
              <div className="flex items-center justify-between">
                <span className="text-xs uppercase tracking-wider font-semibold text-amber-400">
                  Deterministic Scoring Equation
                </span>
                <span className="font-mono text-xs font-bold text-stone-100 px-2 py-0.5 rounded bg-stone-800 border border-stone-700">
                  Total: {Math.round(whyThis.scoringBreakdown.totalScore * 100)}%
                </span>
              </div>
              <p className="text-[11px] text-stone-400 font-mono leading-relaxed">
                {whyThis.scoringBreakdown.formulaDescription}
              </p>
              <div className="grid grid-cols-2 sm:grid-cols-3 gap-2 pt-1 text-xs">
                <div className="p-2.5 rounded-xl bg-stone-900 border border-stone-800">
                  <span className="text-stone-400 block text-[11px]">Taste Match (35%)</span>
                  <span className="font-mono font-bold text-rose-400">
                    +{whyThis.scoringBreakdown.tasteMatch}
                  </span>
                </div>
                <div className="p-2.5 rounded-xl bg-stone-900 border border-stone-800">
                  <span className="text-stone-400 block text-[11px]">Context Match (30%)</span>
                  <span className="font-mono font-bold text-sky-400">
                    +{whyThis.scoringBreakdown.contextMatch}
                  </span>
                </div>
                <div className="p-2.5 rounded-xl bg-stone-900 border border-stone-800">
                  <span className="text-stone-400 block text-[11px]">Constraint Match (20%)</span>
                  <span className="font-mono font-bold text-emerald-400">
                    +{whyThis.scoringBreakdown.constraintMatch}
                  </span>
                </div>
                <div className="p-2.5 rounded-xl bg-stone-900 border border-stone-800">
                  <span className="text-stone-400 block text-[11px]">Novelty / Exploration</span>
                  <span className="font-mono font-bold text-amber-400">
                    +{whyThis.scoringBreakdown.noveltyScore}
                  </span>
                </div>
                <div className="p-2.5 rounded-xl bg-stone-900 border border-stone-800">
                  <span className="text-stone-400 block text-[11px]">Recency Adjustment</span>
                  <span className="font-mono font-bold text-stone-300">
                    +{whyThis.scoringBreakdown.recencyAdjustment}
                  </span>
                </div>
                <div className="p-2.5 rounded-xl bg-stone-900 border border-stone-800">
                  <span className="text-stone-400 block text-[11px]">Repetition Penalty</span>
                  <span className="font-mono font-bold text-rose-500">
                    -{whyThis.scoringBreakdown.repetitionPenalty}
                  </span>
                </div>
              </div>
            </div>
          )}

          {/* Novelty vs Familiarity Bar */}
          <div className="pt-2 border-t border-stone-800">
            <div className="flex items-center justify-between text-xs text-stone-400 mb-1.5">
              <span>Safe Familiar Comfort</span>
              <span className="font-mono text-stone-300">
                Novelty: {Math.round(whyThis.noveltyScore * 100)}%
              </span>
              <span>Serendipitous Discovery</span>
            </div>
            <div className="w-full h-2 rounded-full bg-stone-800 overflow-hidden">
              <div
                className="h-full bg-gradient-to-r from-amber-500 to-rose-400 rounded-full"
                style={{ width: `${whyThis.noveltyScore * 100}%` }}
              />
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-4 border-t border-stone-800 bg-stone-900 flex justify-end">
          <button
            onClick={onClose}
            className="px-5 py-2.5 rounded-xl bg-stone-800 hover:bg-stone-750 text-stone-200 text-sm font-medium transition-colors"
          >
            Understood
          </button>
        </div>
      </div>
    </div>
  );
};
