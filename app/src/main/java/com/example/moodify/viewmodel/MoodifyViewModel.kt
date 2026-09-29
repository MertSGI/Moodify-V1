package com.example.moodify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodify.data.SeedData
import com.example.moodify.model.*
import com.example.moodify.service.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab {
    NOW,
    CHAT,
    DISCOVER,
    PLANS,
    YOU
}

data class MoodifyUiState(
    val currentTab: AppTab = AppTab.NOW,
    val user: UserProfile = SeedData.user,
    val context: ContextSnapshot = SeedData.initialContext,
    val memories: List<MemoryItem> = SeedData.seedMemories,
    val tasteNodes: List<TasteNode> = SeedData.seedTasteNodes,
    val recommendations: List<RecommendationItem> = SeedData.seedRecommendations,
    val explorationFactor: Float = 0.35f,
    val inspectingWhyThisItem: RecommendationItem? = null,
    val chatMessages: List<ChatMessage> = SeedData.initialChatMessages,
    val isThinking: Boolean = false,
    val plans: List<PlanItem> = SeedData.seedPlans,
    val actionPlans: List<ActionPlan> = SeedData.seedActions,
    val pendingActionConfirmation: ActionPlan? = null,
    val editingMemory: MemoryItem? = null,
    val isAddingNewMemory: Boolean = false,
    val integrations: List<IntegrationProvider> = SeedData.seedIntegrations,
    val privacySettings: PrivacySettings = SeedData.defaultPrivacySettings,
    val proactiveSettings: ProactiveSetting = SeedData.defaultProactiveSettings,
    val firewallLogs: List<FirewallDecision> = emptyList(),
    val activeCategoryFilter: String = "ALL"
)

class MoodifyViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MoodifyUiState())
    val uiState: StateFlow<MoodifyUiState> = _uiState.asStateFlow()

    private var prePrivateContext: ContextSnapshot? = null

    init {
        _uiState.update { it.copy(firewallLogs = FirewallService.getAuditLogs()) }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setCategoryFilter(category: String) {
        _uiState.update { it.copy(activeCategoryFilter = category) }
    }

    fun updateContextDimensions(dims: ContextualDimensions, state: String? = null) {
        _uiState.update { current ->
            val updated = ContextEngine.setSelfReportedMood(current.context, dims, state)
            current.copy(context = updated)
        }
    }

    fun resetContext() {
        _uiState.update { it.copy(context = SeedData.initialContext) }
    }

    fun addMemory(item: MemoryItem) {
        if (_uiState.value.privacySettings.isPrivateSession) return
        _uiState.update { current ->
            current.copy(memories = listOf(item) + current.memories)
        }
    }

    fun updateMemory(id: String, updatedItem: MemoryItem) {
        _uiState.update { current ->
            current.copy(memories = current.memories.map { if (it.id == id) updatedItem else it })
        }
    }

    fun deleteMemory(id: String) {
        _uiState.update { current ->
            current.copy(memories = current.memories.filter { it.id !== id })
        }
    }

    fun commitCandidateMemory(candidate: CandidateMemory) {
        if (_uiState.value.privacySettings.isPrivateSession) return
        val newMem = MemoryItem(
            id = "mem_${System.currentTimeMillis()}",
            category = candidate.category,
            key = candidate.key,
            value = candidate.value,
            source = "CONVERSATION_EXTRACTED",
            sourceQuote = candidate.sourceQuote,
            confidence = candidate.confidence,
            sensitivity = candidate.sensitivity,
            reasoningForBelief = candidate.explanation
        )
        addMemory(newMem)
    }

    fun clearAllMemories() {
        _uiState.update { it.copy(memories = emptyList()) }
    }

    fun applyRecommendationFeedback(item: RecommendationItem, feedback: RecommendationFeedbackType) {
        val currentPrivacy = _uiState.value.privacySettings
        _uiState.update { current ->
            val updatedRecs = current.recommendations.map {
                if (it.id == item.id) it.copy(feedbackGiven = feedback) else it
            }
            val updatedTaste = if (!currentPrivacy.isPrivateSession) {
                TasteGraphService.applyFeedback(current.tasteNodes, item.title, item.domain, feedback)
            } else {
                current.tasteNodes
            }
            val updatedPlans = if (feedback == RecommendationFeedbackType.SAVE_FOR_LATER) {
                listOf(
                    PlanItem(
                        id = "plan_${System.currentTimeMillis()}",
                        title = item.title,
                        type = if (item.category == "movies_tv") "WATCH_LATER" else "ACTIVITY",
                        category = item.metadata.genreOrCuisine ?: item.category,
                        notes = item.description,
                        associatedRecommendationId = item.id
                    )
                ) + current.plans
            } else {
                current.plans
            }

            current.copy(
                recommendations = updatedRecs,
                tasteNodes = updatedTaste,
                plans = updatedPlans
            )
        }
    }

    fun setExplorationFactor(factor: Float) {
        _uiState.update { it.copy(explorationFactor = factor) }
    }

    fun inspectWhyThis(item: RecommendationItem?) {
        _uiState.update { it.copy(inspectingWhyThisItem = item) }
    }

    fun setPendingActionConfirmation(action: ActionPlan?) {
        _uiState.update { it.copy(pendingActionConfirmation = action) }
    }

    fun setEditingMemory(memory: MemoryItem?) {
        _uiState.update { it.copy(editingMemory = memory) }
    }

    fun setIsAddingNewMemory(isAdding: Boolean) {
        _uiState.update { it.copy(isAddingNewMemory = isAdding) }
    }

    fun confirmAndExecuteAction(actionId: String) {
        val action = _uiState.value.actionPlans.find { it.id == actionId } ?: return
        val result = ActionService.executeAction(action)

        _uiState.update { current ->
            val updatedActions = current.actionPlans.map {
                if (it.id == actionId) it.copy(
                    status = result.executionStatus,
                    executedAt = java.time.Instant.now().toString(),
                    resultSummary = result.resultSummary
                ) else it
            }
            val updatedPlans = if (result.newPlanItem != null) {
                listOf(result.newPlanItem) + current.plans
            } else current.plans

            val systemNotice = ChatMessage(
                id = "msg_${System.currentTimeMillis()}_sys",
                sender = MessageSender.ASSISTANT,
                text = "[Local Simulation] ${result.resultSummary}",
                timestamp = java.time.Instant.now().toString()
            )

            current.copy(
                actionPlans = updatedActions,
                plans = updatedPlans,
                pendingActionConfirmation = null,
                chatMessages = current.chatMessages + systemNotice
            )
        }
    }

    fun rejectAction(actionId: String) {
        _uiState.update { current ->
            current.copy(
                actionPlans = current.actionPlans.map {
                    if (it.id == actionId) it.copy(status = ActionStatus.CANCELLED) else it
                },
                pendingActionConfirmation = null
            )
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val userMsg = ChatMessage(
            id = "msg_u_${System.currentTimeMillis()}",
            sender = MessageSender.USER,
            text = text,
            timestamp = java.time.Instant.now().toString()
        )

        _uiState.update { it.copy(chatMessages = it.chatMessages + userMsg, isThinking = true) }

        viewModelScope.launch {
            delay(400) // Brief simulated thinking
            val state = _uiState.value
            val result = AgentOrchestrator.processUserMessage(
                userText = text,
                currentContext = state.context,
                memories = state.memories,
                tasteNodes = state.tasteNodes,
                allRecommendations = state.recommendations,
                privacySettings = state.privacySettings
            )

            var pendingAction: ActionPlan? = null
            result.replyMessage.cards?.forEach { card ->
                card.actionPlan?.let { act ->
                    if (act.requiresExplicitConfirmation) {
                        pendingAction = act
                    }
                }
            }

            _uiState.update { current ->
                val newActions = if (pendingAction != null) {
                    listOf(pendingAction!!) + current.actionPlans
                } else current.actionPlans

                current.copy(
                    context = result.updatedContext,
                    chatMessages = current.chatMessages + result.replyMessage,
                    actionPlans = newActions,
                    pendingActionConfirmation = pendingAction ?: current.pendingActionConfirmation,
                    firewallLogs = FirewallService.getAuditLogs(),
                    isThinking = false
                )
            }
        }
    }

    fun clearChat() {
        _uiState.update { it.copy(chatMessages = SeedData.initialChatMessages) }
    }

    fun addPlanItem(title: String, type: String, category: String, notes: String) {
        val newPlan = PlanItem(
            id = "plan_${System.currentTimeMillis()}",
            title = title,
            type = type,
            category = category,
            notes = notes,
            status = "PENDING"
        )
        _uiState.update { it.copy(plans = listOf(newPlan) + it.plans) }
    }

    fun togglePlanItemChecked(planId: String, itemId: String) {
        _uiState.update { current ->
            current.copy(
                plans = current.plans.map { p ->
                    if (p.id == planId) {
                        p.copy(
                            items = p.items.map { item ->
                                if (item.id == itemId) item.copy(checked = !item.checked) else item
                            }
                        )
                    } else p
                }
            )
        }
    }

    fun toggleIntegration(id: String) {
        _uiState.update { current ->
            current.copy(
                integrations = current.integrations.map {
                    if (it.id == id) it.copy(isConnected = !it.isConnected) else it
                }
            )
        }
    }

    fun updatePrivacySettings(newSettings: PrivacySettings) {
        val previous = _uiState.value.privacySettings
        if (newSettings.isPrivateSession != previous.isPrivateSession) {
            if (newSettings.isPrivateSession) {
                // Entering private session: save durable snapshot
                prePrivateContext = _uiState.value.context
                _uiState.update { it.copy(privacySettings = newSettings) }
            } else {
                // Exiting private session: atomically restore previous durable context
                val restored = prePrivateContext ?: SeedData.initialContext
                prePrivateContext = null
                _uiState.update {
                    it.copy(
                        context = restored,
                        privacySettings = newSettings
                    )
                }
            }
        } else {
            _uiState.update { it.copy(privacySettings = newSettings) }
        }
    }

    fun triggerScenario(scenarioId: String) {
        selectTab(AppTab.CHAT)
        val prompt = when (scenarioId) {
            "A" -> "Today was awful. I don't really want to think."
            "B" -> "I have no plans this Friday night. What should I do?"
            "C" -> "I need snacks for work but I'm trying not to eat junk all day."
            "D" -> "How did my design review with Marcus go? Let's recap."
            "E" -> "Pick something for tonight. I'm tired but don't want something boring."
            "F" -> "Track Japanese Breakfast concerts for me and show upcoming tour dates."
            else -> "Give me a calm evening recommendation."
        }
        sendMessage(prompt)
    }

    fun resetToSeedData() {
        prePrivateContext = null
        _uiState.value = MoodifyUiState()
    }
}
