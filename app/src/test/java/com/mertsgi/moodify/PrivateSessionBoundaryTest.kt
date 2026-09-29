package com.mertsgi.moodify

import com.mertsgi.moodify.data.SeedData
import com.mertsgi.moodify.data.repository.MoodifyRepository
import com.mertsgi.moodify.model.*
import com.mertsgi.moodify.service.FirewallService
import com.mertsgi.moodify.viewmodel.MoodifyViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PrivateSessionBoundaryTest {

    private val testDispatcher = StandardTestDispatcher()

    // Fake in-memory test repository to test exact durable storage boundary
    private class FakeRepository : MoodifyRepository {
        val memoryStore = mutableListOf<MemoryItem>()
        val tasteStore = mutableListOf<TasteNode>()
        val planStore = mutableListOf<PlanItem>()
        val actionStore = mutableListOf<ActionPlan>()
        var recordedFeedback = mutableListOf<Pair<String, RecommendationFeedbackType>>()

        private val _privacyFlow = MutableStateFlow(PrivacySettings(isPrivateSession = false))
        private val _proactiveFlow = MutableStateFlow(SeedData.defaultProactiveSettings)
        private val _exploreFlow = MutableStateFlow(0.35f)
        private val _catFlow = MutableStateFlow("ALL")

        override val memories: Flow<List<MemoryItem>> = MutableStateFlow(memoryStore)
        override val tasteNodes: Flow<List<TasteNode>> = MutableStateFlow(tasteStore)
        override val plans: Flow<List<PlanItem>> = MutableStateFlow(planStore)
        override val actionPlans: Flow<List<ActionPlan>> = MutableStateFlow(actionStore)
        override val privacySettings: Flow<PrivacySettings> = _privacyFlow
        override val proactiveSettings: Flow<ProactiveSetting> = _proactiveFlow
        override val explorationFactor: Flow<Float> = _exploreFlow
        override val activeCategoryFilter: Flow<String> = _catFlow

        override suspend fun checkAndSeedInitialData() {}
        override suspend fun addMemory(memory: MemoryItem) { memoryStore.add(memory) }
        override suspend fun updateMemory(memory: MemoryItem) {}
        override suspend fun deleteMemory(id: String) {}
        override suspend fun clearMemories() { memoryStore.clear() }
        override suspend fun saveTasteNode(node: TasteNode) { tasteStore.add(node) }
        override suspend fun clearTasteNodes() { tasteStore.clear() }
        override suspend fun insertPlan(plan: PlanItem) { planStore.add(plan) }
        override suspend fun updatePlan(plan: PlanItem) {}
        override suspend fun deletePlan(id: String) {}
        override suspend fun togglePlanSubItem(planId: String, subItemId: String) {}
        override suspend fun insertAction(action: ActionPlan) { actionStore.add(action) }
        override suspend fun updateAction(action: ActionPlan) {}
        override suspend fun recordRecommendationFeedback(recommendationId: String, feedbackType: RecommendationFeedbackType) {
            recordedFeedback.add(recommendationId to feedbackType)
        }
        override suspend fun setPrivateSession(isPrivate: Boolean) {
            _privacyFlow.value = _privacyFlow.value.copy(isPrivateSession = isPrivate)
        }
        override suspend fun setMaxAllowedSensitivity(level: SensitivityLevel) {}
        override suspend fun setExplorationFactor(factor: Float) {}
        override suspend fun setActiveCategoryFilter(filter: String) {}
        override suspend fun updateProactiveSettings(settings: ProactiveSetting) {}
        override suspend fun canSendProactivePing(settings: ProactiveSetting): Boolean = true
        override suspend fun resetAllToSeed() {}
    }

    private lateinit var fakeRepo: FakeRepository
    private lateinit var viewModel: MoodifyViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepo = FakeRepository()
        viewModel = MoodifyViewModel(fakeRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun privateSession_blocksDurableMemoryCreation() = runTest(testDispatcher) {
        // Enter private session
        viewModel.updatePrivacySettings(PrivacySettings(isPrivateSession = true))
        testScheduler.advanceUntilIdle()

        val ephemeralMemory = MemoryItem(
            id = "mem_private_test",
            category = "confidential",
            key = "secret_note",
            value = "This should never be persisted to disk",
            sensitivity = SensitivityLevel.HIGHLY_SENSITIVE
        )

        viewModel.addMemory(ephemeralMemory)
        testScheduler.advanceUntilIdle()

        // Durable store must remain untouched (0 memories written)
        assertEquals(0, fakeRepo.memoryStore.size)
    }

    @Test
    fun privateSession_blocksDurableTasteAndPlanSideEffects() = runTest(testDispatcher) {
        viewModel.updatePrivacySettings(PrivacySettings(isPrivateSession = true))
        testScheduler.advanceUntilIdle()

        val rec = SeedData.seedRecommendations[0]
        viewModel.applyRecommendationFeedback(rec, RecommendationFeedbackType.SAVE_FOR_LATER)
        testScheduler.advanceUntilIdle()

        // No feedback or plan saved to repository
        assertEquals(0, fakeRepo.recordedFeedback.size)
        assertEquals(0, fakeRepo.planStore.size)
    }

    @Test
    fun privateSession_restoresDurableContextAtomicallyOnExit() = runTest(testDispatcher) {
        // Starting context
        val initialDims = viewModel.uiState.value.context.dimensions
        assertEquals(0.35f, initialDims.energy, 0.001f)

        // 1. Enter private session
        viewModel.updatePrivacySettings(PrivacySettings(isPrivateSession = true))
        testScheduler.advanceUntilIdle()

        // 2. Ephemeral context modification during private session
        viewModel.updateContextDimensions(ContextualDimensions(energy = 0.99f, stress = 0.01f), "EPHEMERAL_MODE")
        assertEquals(0.99f, viewModel.uiState.value.context.dimensions.energy, 0.001f)

        // 3. Exit private session
        viewModel.updatePrivacySettings(PrivacySettings(isPrivateSession = false))
        testScheduler.advanceUntilIdle()

        // Invariant: Context is restored to pre-private state atomically
        assertEquals(0.35f, viewModel.uiState.value.context.dimensions.energy, 0.001f)
    }

    @Test
    fun firewall_withholdsAllDurableContextDuringPrivateSession() {
        val memories = SeedData.seedMemories
        val settings = PrivacySettings(isPrivateSession = true)

        val result = FirewallService.filterContextForTask(
            taskDescription = "Recommend dinner",
            targetDomain = "RECOMMENDER",
            memories = memories,
            settings = settings
        )

        // All memories blocked
        assertEquals(0, result.admitted.size)
        assertEquals(memories.size, result.blocked.size)
        assertTrue(result.decision.explanation.contains("Private session active"))
    }
}
