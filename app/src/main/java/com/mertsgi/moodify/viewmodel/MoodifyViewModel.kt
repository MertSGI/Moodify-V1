package com.mertsgi.moodify.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mertsgi.moodify.data.SeedData
import com.mertsgi.moodify.data.repository.MoodifyRepository
import com.mertsgi.moodify.model.*
import com.mertsgi.moodify.service.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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
    val memories: List<MemoryItem> = emptyList(),
    val tasteNodes: List<TasteNode> = emptyList(),
    val recommendations: List<RecommendationItem> = SeedData.seedRecommendations,
    val explorationFactor: Float = 0.35f,
    val inspectingWhyThisItem: RecommendationItem? = null,
    val chatMessages: List<ChatMessage> = SeedData.initialChatMessages,
    val isThinking: Boolean = false,
    val plans: List<PlanItem> = emptyList(),
    val actionPlans: List<ActionPlan> = emptyList(),
    val pendingActionConfirmation: ActionPlan? = null,
    val editingMemory: MemoryItem? = null,
    val isAddingNewMemory: Boolean = false,
    val integrations: List<IntegrationProvider> = SeedData.seedIntegrations,
    val privacySettings: PrivacySettings = SeedData.defaultPrivacySettings,
    val proactiveSettings: ProactiveSetting = SeedData.defaultProactiveSettings,
    val firewallLogs: List<FirewallDecision> = emptyList(),
    val activeCategoryFilter: String = "ALL"
)

class MoodifyViewModel(
    private val repository: MoodifyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoodifyUiState())
    val uiState: StateFlow<MoodifyUiState> = _uiState.asStateFlow()

    // Durable context preserved before entering private session
    private var prePrivateContext: ContextSnapshot? = null

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }

        // Collect from Room & DataStore
        viewModelScope.launch {
            repository.memories.collect { memList ->
                _uiState.update { it.copy(memories = memList) }
            }
        }

        viewModelScope.launch {
            repository.tasteNodes.collect { tasteList ->
                _uiState.update { it.copy(tasteNodes = tasteList) }
            }
        }

        viewModelScope.launch {
            repository.plans.collect { planList ->
                _uiState.update { it.copy(plans = planList) }
            }
        }

        viewModelScope.launch {
            repository.actionPlans.collect { actionList ->
                _uiState.update { it.copy(actionPlans = actionList) }
            }
        }

        viewModelScope.launch {
            repository.privacySettings.collect { privacy ->
                _uiState.update { it.copy(privacySettings = privacy) }
            }
        }

        viewModelScope.launch {
            repository.proactiveSettings.collect { proactive ->
                _uiState.update { it.copy(proactiveSettings = proactive) }
            }
        }

        viewModelScope.launch {
            repository.explorationFactor.collect { factor ->
                _uiState.update { it.copy(explorationFactor = factor) }
            }
        }

        viewModelScope.launch {
            repository.activeCategoryFilter.collect { filter ->
                _uiState.update { it.copy(activeCategoryFilter = filter) }
            }
        }

        _uiState.update { it.copy(firewallLogs = FirewallService.getAuditLogs()) }
    }

    fun selectTab(tab: AppTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setCategoryFilter(category: String) {
        viewModelScope.launch {
            repository.setActiveCategoryFilter(category)
        }
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
        // Invariant: Zero durable memory creation during Private Session
        if (_uiState.value.privacySettings.isPrivateSession) return
        viewModelScope.launch {
            repository.addMemory(item)
        }
    }

    fun updateMemory(id: String, updatedItem: MemoryItem) {
        if (_uiState.value.privacySettings.isPrivateSession) return
        viewModelScope.launch {
            repository.updateMemory(updatedItem)
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            repository.deleteMemory(id)
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
        viewModelScope.launch {
            repository.clearMemories()
        }
    }

    fun applyRecommendationFeedback(item: RecommendationItem, feedback: RecommendationFeedbackType) {
        val isPrivate = _uiState.value.privacySettings.isPrivateSession

        _uiState.update { current ->
            val updatedRecs = current.recommendations.map {
                if (it.id == item.id) it.copy(feedbackGiven = feedback) else it
            }
            current.copy(recommendations = updatedRecs)
        }

        viewModelScope.launch {
            // Invariant: No durable taste learning or plan creation during Private Session
            if (!isPrivate) {
                repository.recordRecommendationFeedback(item.id, feedback)
                val updatedTaste = TasteGraphService.applyFeedback(_uiState.value.tasteNodes, item.title, item.domain, feedback)
                val existing = _uiState.value.tasteNodes.find { it.name.equals(item.title, ignoreCase = true) }
                val targetNode = updatedTaste.find { it.name.equals(item.title, ignoreCase = true) }
                if (targetNode != null) {
                    repository.saveTasteNode(targetNode)
                }

                if (feedback == RecommendationFeedbackType.SAVE_FOR_LATER) {
                    val newPlan = PlanItem(
                        id = "plan_${System.currentTimeMillis()}",
                        title = item.title,
                        type = if (item.category == "movies_tv") "WATCH_LATER" else "ACTIVITY",
                        category = item.metadata.genreOrCuisine ?: item.category,
                        notes = item.description,
                        isCalendarSynced = false,
                        associatedRecommendationId = item.id
                    )
                    repository.insertPlan(newPlan)
                }
            }
        }
    }

    fun setExplorationFactor(factor: Float) {
        viewModelScope.launch {
            repository.setExplorationFactor(factor)
        }
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

        viewModelScope.launch {
            val updatedAction = action.copy(
                status = result.executionStatus,
                executedAt = java.time.Instant.now().toString(),
                resultSummary = result.resultSummary
            )
            repository.updateAction(updatedAction)

            if (result.newPlanItem != null) {
                repository.insertPlan(result.newPlanItem)
            }

            val systemNotice = ChatMessage(
                id = "msg_${System.currentTimeMillis()}_sys",
                sender = MessageSender.ASSISTANT,
                text = "[Local Simulation] ${result.resultSummary}",
                timestamp = java.time.Instant.now().toString()
            )

            _uiState.update { current ->
                current.copy(
                    pendingActionConfirmation = null,
                    chatMessages = current.chatMessages + systemNotice
                )
            }
        }
    }

    fun rejectAction(actionId: String) {
        val action = _uiState.value.actionPlans.find { it.id == actionId } ?: return
        viewModelScope.launch {
            repository.updateAction(action.copy(status = ActionStatus.CANCELLED))
            _uiState.update { it.copy(pendingActionConfirmation = null) }
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
            delay(300) // Brief simulated thinking
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

            if (pendingAction != null && !state.privacySettings.isPrivateSession) {
                repository.insertAction(pendingAction!!)
            }

            _uiState.update { current ->
                current.copy(
                    context = result.updatedContext,
                    chatMessages = current.chatMessages + result.replyMessage,
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
            status = "PENDING",
            isCalendarSynced = false
        )
        viewModelScope.launch {
            repository.insertPlan(newPlan)
        }
    }

    fun togglePlanItemChecked(planId: String, itemId: String) {
        val plan = _uiState.value.plans.find { it.id == planId } ?: return
        val updatedItems = plan.items.map { if (it.id == itemId) it.copy(checked = !it.checked) else it }
        viewModelScope.launch {
            repository.updatePlan(plan.copy(items = updatedItems))
        }
    }

    fun toggleIntegration(id: String) {
        _uiState.update { current ->
            current.copy(
                integrations = current.integrations.map {
                    if (it.id == id) {
                        val newStatus = if (it.status == IntegrationStatus.MOCK) IntegrationStatus.NOT_CONNECTED else IntegrationStatus.MOCK
                        it.copy(status = newStatus)
                    } else it
                }
            )
        }
    }

    /**
     * Section K: Private Session and Android Lifecycle
     * On enter: snapshot durable context
     * On exit: atomically restore pre-private durable context and discard ephemeral context
     */
    fun updatePrivacySettings(newSettings: PrivacySettings) {
        val previous = _uiState.value.privacySettings
        if (newSettings.isPrivateSession != previous.isPrivateSession) {
            if (newSettings.isPrivateSession) {
                // Entering Private Session
                prePrivateContext = _uiState.value.context
                viewModelScope.launch {
                    repository.setPrivateSession(true)
                }
            } else {
                // Exiting Private Session: restore previous durable context atomically
                val restored = prePrivateContext ?: SeedData.initialContext
                prePrivateContext = null
                _uiState.update { it.copy(context = restored) }
                viewModelScope.launch {
                    repository.setPrivateSession(false)
                }
            }
        }

        if (newSettings.maxAllowedSensitivity != previous.maxAllowedSensitivity) {
            viewModelScope.launch {
                repository.setMaxAllowedSensitivity(newSettings.maxAllowedSensitivity)
            }
        }
    }

    fun updateProactiveSettings(settings: ProactiveSetting) {
        viewModelScope.launch {
            repository.updateProactiveSettings(settings)
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
        viewModelScope.launch {
            repository.resetAllToSeed()
            _uiState.update {
                it.copy(
                    context = SeedData.initialContext,
                    chatMessages = SeedData.initialChatMessages,
                    pendingActionConfirmation = null,
                    editingMemory = null,
                    isAddingNewMemory = false,
                    integrations = SeedData.seedIntegrations
                )
            }
        }
    }
}

class MoodifyViewModelFactory(
    private val repository: MoodifyRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoodifyViewModel::class.java)) {
            return MoodifyViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
