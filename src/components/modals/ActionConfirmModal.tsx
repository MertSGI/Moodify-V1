import React from 'react';
import { ActionPlan } from '../../types/actions';
import { AlertTriangle, Calendar, CheckCircle2, ShieldAlert, X } from 'lucide-react';

interface ActionConfirmModalProps {
  action: ActionPlan | null;
  onConfirm: () => void;
  onReject: () => void;
}

export const ActionConfirmModal: React.FC<ActionConfirmModalProps> = ({ action, onConfirm, onReject }) => {
  if (!action) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="relative w-full max-w-lg bg-stone-900 border border-stone-800 rounded-3xl shadow-2xl overflow-hidden flex flex-col">
        {/* Header */}
        <div className="p-6 border-b border-stone-800 flex items-start justify-between bg-stone-900">
          <div className="flex items-center gap-3">
            <div className="p-2.5 rounded-2xl bg-amber-500/10 text-amber-400 border border-amber-500/20">
              <ShieldAlert className="w-5 h-5" />
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span className="text-xs font-semibold px-2 py-0.5 rounded uppercase tracking-wider bg-rose-500/10 text-rose-400 border border-rose-500/20">
                  {action.riskLevel.replace('_', ' ')}
                </span>
                <span className="text-xs text-stone-400">Target: {action.targetProvider}</span>
              </div>
              <h3 className="text-lg font-semibold text-stone-100 mt-1">{action.title}</h3>
            </div>
          </div>
          <button
            onClick={onReject}
            className="p-1.5 text-stone-400 hover:text-stone-200 hover:bg-stone-800 rounded-full transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 space-y-4 text-sm text-stone-300">
          <p className="text-stone-300 leading-relaxed">{action.description}</p>

          <div className="p-4 rounded-2xl bg-stone-850 border border-stone-800 space-y-2.5">
            <span className="text-xs font-semibold text-stone-400 uppercase tracking-wider block">
              Execution Parameters
            </span>
            {action.parameters.map((param, idx) => (
              <div key={idx} className="flex justify-between items-center text-xs py-1 border-b border-stone-800/60 last:border-0">
                <span className="text-stone-400">{param.label}:</span>
                <span className="font-mono text-stone-200 font-medium">{String(param.value)}</span>
              </div>
            ))}
          </div>

          <div className="p-3.5 rounded-xl bg-amber-950/20 border border-amber-900/30 flex items-start gap-2.5 text-amber-200/90 text-xs">
            <AlertTriangle className="w-4 h-4 text-amber-400 shrink-0 mt-0.5" />
            <span>
              <strong>Agency Guardrail:</strong> Moodify will never perform writes without your explicit confirmation. In this prototype, execution is simulated locally—no external provider write occurs.
            </span>
          </div>
        </div>

        {/* Footer */}
        <div className="p-5 border-t border-stone-800 bg-stone-900 flex justify-end gap-3">
          <button
            onClick={onReject}
            className="px-4 py-2.5 rounded-xl bg-stone-800 hover:bg-stone-750 text-stone-300 text-sm font-medium transition-colors"
          >
            Cancel Action
          </button>
          <button
            onClick={onConfirm}
            className="px-5 py-2.5 rounded-xl bg-amber-500 hover:bg-amber-400 text-stone-950 font-semibold text-sm transition-colors shadow-lg shadow-amber-500/20 flex items-center gap-2"
          >
            <CheckCircle2 className="w-4 h-4" />
            <span>Authorize Simulation</span>
          </button>
        </div>
      </div>
    </div>
  );
};
