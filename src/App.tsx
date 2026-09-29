import React from 'react';
import { AppProvider, useApp } from './context/AppContext';
import { Navigation } from './components/Navigation';
import { ScenarioDrawer } from './components/ScenarioDrawer';
import { NowView } from './components/NowView';
import { ChatView } from './components/ChatView';
import { DiscoverView } from './components/DiscoverView';
import { PlansView } from './components/PlansView';
import { YouView } from './components/YouView';
import { WhyThisModal } from './components/modals/WhyThisModal';
import { ActionConfirmModal } from './components/modals/ActionConfirmModal';

const AppContent: React.FC = () => {
  const {
    currentTab,
    inspectingWhyThisItem,
    setInspectingWhyThisItem,
    pendingActionConfirmation,
    confirmAndExecuteAction,
    rejectAction,
  } = useApp();

  return (
    <div className="min-h-screen bg-stone-950 text-stone-100 flex flex-col font-sans selection:bg-amber-500/30 selection:text-amber-200">
      {/* Navigation Header */}
      <Navigation />

      {/* 1-Click Interactive Test Scenarios Bar */}
      <ScenarioDrawer />

      {/* Main Tab View */}
      <main className="flex-1 w-full overflow-x-hidden">
        {currentTab === 'NOW' && <NowView />}
        {currentTab === 'CHAT' && <ChatView />}
        {currentTab === 'DISCOVER' && <DiscoverView />}
        {currentTab === 'PLANS' && <PlansView />}
        {currentTab === 'YOU' && <YouView />}
      </main>

      {/* Global Transparency "Why this?" Modal */}
      {inspectingWhyThisItem && (
        <WhyThisModal
          item={inspectingWhyThisItem}
          onClose={() => setInspectingWhyThisItem(null)}
        />
      )}

      {/* Global Action Confirmation Modal for consequential writes */}
      {pendingActionConfirmation && (
        <ActionConfirmModal
          action={pendingActionConfirmation}
          onConfirm={() => confirmAndExecuteAction(pendingActionConfirmation.id)}
          onReject={() => rejectAction(pendingActionConfirmation.id)}
        />
      )}
    </div>
  );
};

export default function App() {
  return (
    <AppProvider>
      <AppContent />
    </AppProvider>
  );
}
