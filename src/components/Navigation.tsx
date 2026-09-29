import React from 'react';
import { useApp } from '../context/AppContext';
import { Sparkles, MessageSquare, Compass, BookmarkCheck, User, Shield, Info, Flame } from 'lucide-react';

export const Navigation: React.FC = () => {
  const { currentTab, setCurrentTab, context, privacySettings } = useApp();

  const tabs = [
    { id: 'NOW' as const, label: 'Now', icon: Sparkles },
    { id: 'CHAT' as const, label: 'Chat', icon: MessageSquare },
    { id: 'DISCOVER' as const, label: 'Discover', icon: Compass },
    { id: 'PLANS' as const, label: 'Plans', icon: BookmarkCheck },
    { id: 'YOU' as const, label: 'You', icon: User },
  ];

  // Subtle ambient hue based on primary context state
  const getAmbientBadgeColor = () => {
    switch (context.primaryState) {
      case 'LOW_BATTERY':
        return 'bg-amber-500/15 text-amber-300 border-amber-500/30';
      case 'ANTICIPATING_WEEKEND':
        return 'bg-indigo-500/15 text-indigo-300 border-indigo-500/30';
      case 'WINDING_DOWN':
        return 'bg-purple-500/15 text-purple-300 border-purple-500/30';
      default:
        return 'bg-stone-800 text-stone-300 border-stone-700';
    }
  };

  return (
    <header className="sticky top-0 z-40 w-full bg-stone-950/85 backdrop-blur-md border-b border-stone-850">
      <div className="max-w-6xl mx-auto px-4 h-16 flex items-center justify-between">
        {/* Brand & Ambient Status */}
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2">
            <span className="text-xl font-bold tracking-tight text-stone-100 flex items-center gap-1.5">
              <span className="w-2.5 h-2.5 rounded-full bg-amber-400 animate-pulse" />
              Moodify
            </span>
            <span className="text-[10px] font-medium px-2 py-0.5 rounded-full bg-stone-800 text-stone-400 border border-stone-750">
              Working Title
            </span>
          </div>

          {/* Context Ambient Pill */}
          <div className={`hidden md:flex items-center gap-1.5 px-3 py-1 rounded-full text-xs font-medium border ${getAmbientBadgeColor()}`}>
            <span className="w-1.5 h-1.5 rounded-full bg-current" />
            <span className="capitalize">{context.primaryState.replace(/_/g, ' ').toLowerCase()}</span>
            <span className="text-stone-400 text-[10px]">· E: {Math.round(context.dimensions.energy * 100)}%</span>
          </div>
        </div>

        {/* Desktop Navigation Tabs */}
        <nav className="hidden sm:flex items-center gap-1">
          {tabs.map(tab => {
            const Icon = tab.icon;
            const isActive = currentTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setCurrentTab(tab.id)}
                className={`relative px-4 py-2 rounded-2xl text-sm font-medium transition-all flex items-center gap-2 ${
                  isActive
                    ? 'text-stone-100 bg-stone-850 shadow-sm border border-stone-750'
                    : 'text-stone-400 hover:text-stone-200 hover:bg-stone-900'
                }`}
              >
                <Icon className={`w-4 h-4 ${isActive ? 'text-amber-400' : 'text-stone-400'}`} />
                <span>{tab.label}</span>
                {tab.id === 'YOU' && privacySettings.isPrivateSession && (
                  <span className="w-2 h-2 rounded-full bg-purple-400 ring-2 ring-stone-900" />
                )}
              </button>
            );
          })}
        </nav>

        {/* Right Info / Security Badges */}
        <div className="flex items-center gap-2">
          {privacySettings.isPrivateSession ? (
            <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium bg-purple-500/15 text-purple-300 border border-purple-500/30">
              <Shield className="w-3.5 h-3.5" />
              <span className="hidden sm:inline">Private Session Active</span>
            </div>
          ) : (
            <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs text-stone-400 bg-stone-900 border border-stone-800">
              <Shield className="w-3.5 h-3.5 text-emerald-400" />
              <span className="hidden sm:inline">Firewall Active</span>
            </div>
          )}
        </div>
      </div>

      {/* Mobile Bottom Navigation Bar */}
      <div className="sm:hidden fixed bottom-0 left-0 right-0 z-50 bg-stone-950/95 backdrop-blur-lg border-t border-stone-850 px-2 py-1.5 flex justify-around items-center">
        {tabs.map(tab => {
          const Icon = tab.icon;
          const isActive = currentTab === tab.id;
          return (
            <button
              key={tab.id}
              onClick={() => setCurrentTab(tab.id)}
              className={`flex flex-col items-center justify-center py-1 px-3 rounded-xl transition-colors ${
                isActive ? 'text-amber-400 font-semibold' : 'text-stone-400 hover:text-stone-200'
              }`}
            >
              <Icon className="w-5 h-5 mb-0.5" />
              <span className="text-[11px]">{tab.label}</span>
            </button>
          );
        })}
      </div>
    </header>
  );
};
