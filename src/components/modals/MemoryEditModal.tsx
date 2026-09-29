import React, { useState } from 'react';
import { MemoryItem, MemorySensitivity, MemoryDomain } from '../../types/memory';
import { X, Shield, Lock, Eye, Trash2, CheckCircle2, History } from 'lucide-react';

interface MemoryEditModalProps {
  memory: MemoryItem | null;
  onClose: () => void;
  onSave: (id: string, updates: Partial<MemoryItem>) => void;
  onDelete: (id: string) => void;
}

export const MemoryEditModal: React.FC<MemoryEditModalProps> = ({
  memory,
  onClose,
  onSave,
  onDelete,
}) => {
  if (!memory) return null;

  const [value, setValue] = useState(memory.value);
  const [sensitivity, setSensitivity] = useState<MemorySensitivity>(memory.sensitivity);
  const [isImportant, setIsImportant] = useState(memory.isImportant || false);
  const [allowedForPersonalization, setAllowedForPersonalization] = useState(
    memory.allowedForPersonalization
  );
  const [allowedForExternalTools, setAllowedForExternalTools] = useState(
    memory.allowedForExternalTools
  );
  const [status, setStatus] = useState(memory.status);

  const handleSave = () => {
    onSave(memory.id, {
      value,
      sensitivity,
      isImportant,
      allowedForPersonalization,
      allowedForExternalTools,
      status,
    });
    onClose();
  };

  const handleDelete = () => {
    if (confirm('Permanently remove this memory from your personal vault?')) {
      onDelete(memory.id);
      onClose();
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/70 backdrop-blur-sm animate-in fade-in duration-200">
      <div className="relative w-full max-w-lg bg-stone-900 border border-stone-800 rounded-3xl shadow-2xl overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="p-6 pb-4 border-b border-stone-800 flex items-start justify-between bg-stone-900 sticky top-0">
          <div>
            <div className="flex items-center gap-2 mb-1.5">
              <span className="text-xs font-mono font-medium px-2 py-0.5 rounded bg-emerald-950/60 text-emerald-400 border border-emerald-900/50">
                {memory.key}
              </span>
              <span className="text-xs text-stone-400 uppercase tracking-wider">
                Domain: {memory.category}
              </span>
            </div>
            <h2 className="text-lg font-semibold text-stone-100">Inspect & Edit Memory</h2>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-stone-400 hover:text-stone-200 hover:bg-stone-800 rounded-full transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form Body */}
        <div className="p-6 overflow-y-auto space-y-5 text-sm text-stone-300">
          {/* Memory Text */}
          <div>
            <label className="block text-xs font-semibold text-stone-400 uppercase tracking-wider mb-2">
              Memory Content
            </label>
            <textarea
              value={value}
              onChange={e => setValue(e.target.value)}
              rows={3}
              className="w-full p-3.5 rounded-2xl bg-stone-800 border border-stone-700 text-stone-100 focus:outline-none focus:border-amber-400 text-sm leading-relaxed"
            />
          </div>

          {/* Provenance & Belief Reasoning */}
          <div className="p-4 rounded-2xl bg-stone-850 border border-stone-800 space-y-2">
            <span className="text-xs font-semibold text-stone-400 uppercase tracking-wider flex items-center gap-1.5">
              <History className="w-3.5 h-3.5 text-amber-400" />
              Provenance & Source
            </span>
            <div className="text-xs text-stone-400 space-y-1">
              <p>
                <strong className="text-stone-300">Source:</strong> {memory.source} (Confidence:{' '}
                {Math.round(memory.confidence * 100)}%)
              </p>
              {memory.sourceQuote && (
                <p className="italic text-stone-400">"{memory.sourceQuote}"</p>
              )}
              {memory.reasoningForBelief && (
                <p className="text-stone-400 pt-1 border-t border-stone-800">
                  <strong className="text-stone-300">Why Moodify believes this:</strong>{' '}
                  {memory.reasoningForBelief}
                </p>
              )}
            </div>
          </div>

          {/* Sensitivity Tier */}
          <div>
            <label className="block text-xs font-semibold text-stone-400 uppercase tracking-wider mb-2">
              Sensitivity Classification
            </label>
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
              {(['NORMAL', 'PERSONAL', 'SENSITIVE', 'HIGHLY_SENSITIVE'] as MemorySensitivity[]).map(
                level => (
                  <button
                    key={level}
                    type="button"
                    onClick={() => setSensitivity(level)}
                    className={`py-2 px-2.5 rounded-xl text-xs font-medium border transition-colors ${
                      sensitivity === level
                        ? 'bg-amber-500/20 text-amber-300 border-amber-500/50'
                        : 'bg-stone-800 text-stone-400 border-stone-750 hover:bg-stone-750'
                    }`}
                  >
                    {level.replace('_', ' ')}
                  </button>
                )
              )}
            </div>
          </div>

          {/* Toggles */}
          <div className="space-y-3 pt-2">
            <label className="flex items-center justify-between p-3.5 rounded-2xl bg-stone-800/60 border border-stone-800 cursor-pointer">
              <div className="pr-4">
                <span className="text-sm font-medium text-stone-200 block">
                  Allowed for Personalization
                </span>
                <span className="text-xs text-stone-400">
                  Permit Moodify to shape recommendations using this knowledge.
                </span>
              </div>
              <input
                type="checkbox"
                checked={allowedForPersonalization}
                onChange={e => setAllowedForPersonalization(e.target.checked)}
                className="w-4 h-4 rounded text-amber-500 focus:ring-amber-400"
              />
            </label>

            <label className="flex items-center justify-between p-3.5 rounded-2xl bg-stone-800/60 border border-stone-800 cursor-pointer">
              <div className="pr-4">
                <span className="text-sm font-medium text-stone-200 block">
                  Allowed for External Tools
                </span>
                <span className="text-xs text-stone-400">
                  Allow passing to approved external providers (e.g. calendar/maps).
                </span>
              </div>
              <input
                type="checkbox"
                checked={allowedForExternalTools}
                onChange={e => setAllowedForExternalTools(e.target.checked)}
                className="w-4 h-4 rounded text-amber-500 focus:ring-amber-400"
              />
            </label>

            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => setIsImportant(!isImportant)}
                className={`p-3 rounded-2xl border text-xs font-medium text-left transition-colors ${
                  isImportant
                    ? 'bg-rose-500/10 border-rose-500/30 text-rose-300'
                    : 'bg-stone-800 border-stone-750 text-stone-400 hover:text-stone-200'
                }`}
              >
                ★ {isImportant ? 'Marked as Important' : 'Mark as Important'}
              </button>

              <button
                type="button"
                onClick={() => setStatus(status === 'ACTIVE' ? 'OUTDATED' : 'ACTIVE')}
                className={`p-3 rounded-2xl border text-xs font-medium text-left transition-colors ${
                  status === 'OUTDATED'
                    ? 'bg-amber-500/10 border-amber-500/30 text-amber-300'
                    : 'bg-stone-800 border-stone-750 text-stone-400 hover:text-stone-200'
                }`}
              >
                {status === 'OUTDATED' ? 'Marked as Outdated' : 'Mark Outdated'}
              </button>
            </div>
          </div>
        </div>

        {/* Footer */}
        <div className="p-5 border-t border-stone-800 bg-stone-900 flex justify-between items-center">
          <button
            onClick={handleDelete}
            className="flex items-center gap-1.5 text-xs text-rose-400 hover:text-rose-300 hover:bg-rose-950/30 px-3 py-2 rounded-xl transition-colors"
          >
            <Trash2 className="w-4 h-4" />
            <span>Delete Memory</span>
          </button>
          <div className="flex gap-2">
            <button
              onClick={onClose}
              className="px-4 py-2 rounded-xl bg-stone-800 hover:bg-stone-750 text-stone-300 text-sm font-medium transition-colors"
            >
              Cancel
            </button>
            <button
              onClick={handleSave}
              className="px-5 py-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-stone-950 font-semibold text-sm transition-colors shadow-lg shadow-amber-500/20"
            >
              Save Changes
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
