import React, { useState, useRef, useEffect } from 'react';
import { useApp } from '../context/AppContext';
import {
  Send,
  Sparkles,
  Info,
  Calendar,
  BookmarkPlus,
  Shield,
  Brain,
  CheckCircle2,
  Trash2,
  Mic,
  Clock,
  ArrowRight,
} from 'lucide-react';
import { ChatMessage, ChatCardPayload } from '../types/chat';
import { RecommendationItem } from '../types/recommendation';
import { ActionPlan } from '../types/actions';
import { CandidateMemory } from '../types/memory';

export const ChatView: React.FC = () => {
  const {
    chatMessages,
    sendMessage,
    isThinking,
    setInspectingWhyThisItem,
    setPendingActionConfirmation,
    addPlanItem,
    commitCandidateMemory,
    clearChat,
    context,
    privacySettings,
    setCurrentTab,
  } = useApp();

  const [input, setInput] = useState('');
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [chatMessages, isThinking]);

  const handleSend = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!input.trim() || isThinking) return;
    const text = input;
    setInput('');
    await sendMessage(text);
  };

  const handleSuggestedClick = async (reply: string) => {
    if (isThinking) return;
    await sendMessage(reply);
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-6 flex flex-col h-[calc(100vh-8rem)]">
      {/* Top Chat Bar */}
      <div className="flex items-center justify-between pb-3 border-b border-stone-850 shrink-0 mb-4">
        <div className="flex items-center gap-2.5">
          <div className="w-8 h-8 rounded-full bg-amber-500/20 text-amber-400 flex items-center justify-center font-bold text-xs border border-amber-500/30">
            M
          </div>
          <div>
            <h2 className="text-sm font-semibold text-stone-200">Moodify Companion</h2>
            <div className="flex items-center gap-1.5 text-[11px] text-stone-400">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400" />
              <span>
                {privacySettings.isPrivateSession ? 'Private Session (No Vault Writes)' : 'Attuned to Current Context'}
              </span>
            </div>
          </div>
        </div>

        <button
          onClick={clearChat}
          className="text-xs text-stone-400 hover:text-stone-300 p-2 rounded-xl hover:bg-stone-900 transition-colors"
          title="Reset conversation"
        >
          <Trash2 className="w-4 h-4" />
        </button>
      </div>

      {/* Messages Stream */}
      <div className="flex-1 overflow-y-auto space-y-6 pr-2">
        {chatMessages.map(msg => (
          <div
            key={msg.id}
            className={`flex flex-col ${msg.sender === 'USER' ? 'items-end' : 'items-start'}`}
          >
            {/* Proactive / Origin Pill */}
            {msg.isProactive && (
              <div className="mb-1.5 flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-[11px] font-medium bg-amber-500/10 text-amber-300 border border-amber-500/20">
                <Sparkles className="w-3 h-3" />
                <span>Proactive Context: {msg.proactiveReason}</span>
              </div>
            )}

            {/* Bubble */}
            <div
              className={`max-w-[85%] sm:max-w-[75%] rounded-3xl p-4 sm:p-5 text-sm leading-relaxed shadow-md ${
                msg.sender === 'USER'
                  ? 'bg-amber-500 text-stone-950 font-medium rounded-tr-sm'
                  : 'bg-stone-900 text-stone-200 border border-stone-800 rounded-tl-sm'
              }`}
            >
              <p className="whitespace-pre-wrap">{msg.text}</p>

              {/* Firewall / Provenance footnote */}
              {msg.sender === 'ASSISTANT' && msg.firewallTaskId && (
                <div className="mt-3 pt-2.5 border-t border-stone-800/80 flex items-center justify-between text-[11px] text-stone-400">
                  <button
                    onClick={() => setCurrentTab('YOU')}
                    className="flex items-center gap-1 text-emerald-400 hover:text-emerald-300 transition-colors"
                    title="Click to inspect this task's decision in Personal Context Firewall"
                  >
                    <Shield className="w-3 h-3 text-emerald-400" />
                    <span className="underline underline-offset-2">Context Firewall: Minimal-Purpose Assembly</span>
                  </button>
                  <span className="font-mono text-[10px] text-stone-400">{msg.firewallTaskId}</span>
                </div>
              )}
            </div>

            {/* Extracted Candidate Memory Banner (if any) */}
            {msg.extractedCandidateMemories && msg.extractedCandidateMemories.length > 0 && (
              <div className="mt-2 max-w-[85%] sm:max-w-[75%] space-y-2">
                {msg.extractedCandidateMemories.map(cand => (
                  <div
                    key={cand.id}
                    className="p-3.5 rounded-2xl bg-stone-900/90 border border-emerald-900/40 text-xs text-stone-300 flex items-start justify-between gap-3 shadow-sm"
                  >
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <Brain className="w-3.5 h-3.5 text-emerald-400" />
                        <span className="font-semibold text-emerald-300">
                          Noticed Candidate Memory ({cand.tier})
                        </span>
                      </div>
                      <p className="text-stone-300 font-medium">"{cand.value}"</p>
                      <p className="text-[11px] text-stone-400">{cand.explanation}</p>
                    </div>

                    {cand.tier !== 'DO_NOT_STORE' && (
                      <button
                        onClick={() => commitCandidateMemory(cand)}
                        className="px-3 py-1.5 rounded-xl bg-emerald-950/80 hover:bg-emerald-900 text-emerald-300 text-xs font-medium border border-emerald-800 shrink-0 transition-colors"
                      >
                        Keep in Vault
                      </button>
                    )}
                  </div>
                ))}
              </div>
            )}

            {/* Interactive Cards (Recommendations, Actions, etc.) */}
            {msg.cards && msg.cards.length > 0 && (
              <div className="mt-3 w-full max-w-[90%] sm:max-w-[85%] space-y-3">
                {msg.cards.map((card, idx) => (
                  <div key={idx} className="space-y-3">
                    {/* Recommendations Carousel / Grid */}
                    {card.recommendations && card.recommendations.length > 0 && (
                      <div className="space-y-2.5">
                        {card.title && (
                          <span className="text-xs uppercase tracking-wider font-semibold text-stone-400 block px-1">
                            {card.title}
                          </span>
                        )}
                        <div className="grid grid-cols-1 gap-3">
                          {card.recommendations.map(rec => (
                            <div
                              key={rec.id}
                              className="p-4 rounded-2xl bg-stone-900 border border-stone-850 hover:border-stone-750 transition-all shadow-md flex flex-col sm:flex-row gap-4"
                            >
                              {rec.imageUrl && (
                                <img
                                  src={rec.imageUrl}
                                  alt={rec.title}
                                  className="w-full sm:w-28 h-28 object-cover rounded-xl bg-stone-800 shrink-0"
                                />
                              )}
                              <div className="flex-1 flex flex-col justify-between">
                                <div>
                                  <div className="flex items-center gap-2 mb-1">
                                    <span className="text-[10px] font-mono uppercase text-amber-400 font-semibold px-2 py-0.5 rounded bg-amber-500/10">
                                      {rec.category}
                                    </span>
                                    {rec.metadata.duration && (
                                      <span className="text-[11px] text-stone-400">
                                        {rec.metadata.duration}
                                      </span>
                                    )}
                                    {rec.metadata.price && (
                                      <span className="text-[11px] text-emerald-400 font-medium">
                                        {rec.metadata.price}
                                      </span>
                                    )}
                                  </div>
                                  <h4 className="text-sm font-bold text-stone-100">{rec.title}</h4>
                                  <p className="text-xs text-stone-400 mt-0.5 line-clamp-2">
                                    {rec.description}
                                  </p>
                                </div>

                                <div className="mt-3 pt-2.5 border-t border-stone-850 flex items-center justify-between gap-2">
                                  <button
                                    onClick={() => setInspectingWhyThisItem(rec)}
                                    className="text-xs text-stone-400 hover:text-stone-200 flex items-center gap-1 font-medium transition-colors"
                                  >
                                    <Info className="w-3.5 h-3.5 text-amber-400" />
                                    <span>Why this?</span>
                                  </button>

                                  <button
                                    onClick={() =>
                                      addPlanItem({
                                        title: rec.title,
                                        type: rec.category === 'movies_tv' ? 'WATCH_LATER' : 'ACTIVITY',
                                        category: rec.category,
                                        notes: rec.description,
                                      })
                                    }
                                    className="px-3 py-1 rounded-xl bg-stone-800 hover:bg-stone-750 text-stone-200 text-xs font-medium border border-stone-700 transition-colors flex items-center gap-1"
                                  >
                                    <BookmarkPlus className="w-3.5 h-3.5" />
                                    <span>Save</span>
                                  </button>
                                </div>
                              </div>
                            </div>
                          ))}
                        </div>
                      </div>
                    )}

                    {/* Action Proposal Card */}
                    {card.actionPlan && (
                      <div className="p-4 rounded-2xl bg-indigo-950/30 border border-indigo-900/50 shadow-md space-y-3">
                        <div className="flex items-center justify-between">
                          <div className="flex items-center gap-2">
                            <Calendar className="w-4 h-4 text-indigo-400" />
                            <span className="text-xs font-semibold text-indigo-200 uppercase tracking-wider">
                              Action Requires Consent
                            </span>
                          </div>
                          <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-indigo-900/50 text-indigo-300">
                            {card.actionPlan.riskLevel}
                          </span>
                        </div>
                        <h4 className="text-sm font-semibold text-stone-100">{card.actionPlan.title}</h4>
                        <p className="text-xs text-stone-300">{card.actionPlan.description}</p>
                        <div className="pt-2 flex justify-end">
                          <button
                            onClick={() => setPendingActionConfirmation(card.actionPlan!)}
                            className="px-4 py-2 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-medium text-xs shadow-md transition-colors"
                          >
                            Review & Authorize
                          </button>
                        </div>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}

            {/* Suggested Replies */}
            {msg.suggestedReplies && msg.suggestedReplies.length > 0 && (
              <div className="mt-3 flex flex-wrap gap-2 max-w-[90%]">
                {msg.suggestedReplies.map((reply, sIdx) => (
                  <button
                    key={sIdx}
                    onClick={() => handleSuggestedClick(reply)}
                    className="px-3.5 py-1.5 rounded-full text-xs font-medium bg-stone-900 hover:bg-stone-850 text-stone-300 hover:text-stone-100 border border-stone-800 transition-all hover:scale-[1.01]"
                  >
                    {reply}
                  </button>
                ))}
              </div>
            )}
          </div>
        ))}

        {/* Thinking Indicator */}
        {isThinking && (
          <div className="flex items-center gap-2 text-stone-400 text-xs p-3 rounded-2xl bg-stone-900/60 max-w-xs border border-stone-850">
            <span className="w-2 h-2 rounded-full bg-amber-400 animate-ping" />
            <span>Consulting context engine & privacy firewall...</span>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* Input Box */}
      <form onSubmit={handleSend} className="mt-4 pt-3 border-t border-stone-850 shrink-0">
        <div className="relative flex items-center">
          <input
            type="text"
            value={input}
            onChange={e => setInput(e.target.value)}
            placeholder="Talk naturally to Moodify (or pick an interactive scenario above)..."
            className="w-full pl-4 pr-24 py-3.5 rounded-2xl bg-stone-900 border border-stone-800 text-stone-100 placeholder-stone-400 text-sm focus:outline-none focus:border-amber-400 transition-colors"
          />
          <div className="absolute right-2 flex items-center gap-1">
            <button
              type="button"
              title="Voice Companion (Gemini Live API architecture ready)"
              className="p-2 text-stone-400 hover:text-stone-300 hover:bg-stone-800 rounded-xl transition-colors"
            >
              <Mic className="w-4 h-4" />
            </button>
            <button
              type="submit"
              disabled={!input.trim() || isThinking}
              className="p-2 rounded-xl bg-amber-500 hover:bg-amber-400 text-stone-950 font-bold disabled:opacity-40 disabled:hover:bg-amber-500 transition-colors shadow-md shadow-amber-500/20"
            >
              <Send className="w-4 h-4" />
            </button>
          </div>
        </div>
      </form>
    </div>
  );
};
