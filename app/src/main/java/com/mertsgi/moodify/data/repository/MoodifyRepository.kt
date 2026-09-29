package com.mertsgi.moodify.data.repository

import com.mertsgi.moodify.data.SeedData
import com.mertsgi.moodify.data.local.MoodifyDatabase
import com.mertsgi.moodify.data.local.datastore.UserPreferencesRepository
import com.mertsgi.moodify.data.local.entity.*
import com.mertsgi.moodify.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface MoodifyRepository {
    val memories: Flow<List<MemoryItem>>
    val tasteNodes: Flow<List<TasteNode>>
    val plans: Flow<List<PlanItem>>
    val actionPlans: Flow<List<ActionPlan>>
    val privacySettings: Flow<PrivacySettings>
    val proactiveSettings: Flow<ProactiveSetting>
    val explorationFactor: Flow<Float>
    val activeCategoryFilter: Flow<String>

    suspend fun checkAndSeedInitialData()
    suspend fun addMemory(memory: MemoryItem)
    suspend fun updateMemory(memory: MemoryItem)
    suspend fun deleteMemory(id: String)
    suspend fun clearMemories()

    suspend fun saveTasteNode(node: TasteNode)
    suspend fun clearTasteNodes()

    suspend fun insertPlan(plan: PlanItem)
    suspend fun updatePlan(plan: PlanItem)
    suspend fun deletePlan(id: String)
    suspend fun togglePlanSubItem(planId: String, subItemId: String)

    suspend fun insertAction(action: ActionPlan)
    suspend fun updateAction(action: ActionPlan)

    suspend fun recordRecommendationFeedback(recommendationId: String, feedbackType: RecommendationFeedbackType)

    suspend fun setPrivateSession(isPrivate: Boolean)
    suspend fun setMaxAllowedSensitivity(level: SensitivityLevel)
    suspend fun setExplorationFactor(factor: Float)
    suspend fun setActiveCategoryFilter(filter: String)
    suspend fun updateProactiveSettings(settings: ProactiveSetting)
    suspend fun canSendProactivePing(settings: ProactiveSetting): Boolean
    suspend fun resetAllToSeed()
}

class MoodifyRepositoryImpl(
    private val database: MoodifyDatabase,
    private val preferencesRepository: UserPreferencesRepository
) : MoodifyRepository {

    override val memories: Flow<List<MemoryItem>> = database.memoryDao().getAllMemories()
        .map { entities -> entities.map { it.toDomain() } }

    override val tasteNodes: Flow<List<TasteNode>> = database.tasteDao().getAllTasteNodes()
        .map { entities -> entities.map { it.toDomain() } }

    override val plans: Flow<List<PlanItem>> = database.planDao().getAllPlans()
        .map { entities -> entities.map { it.toDomain() } }

    override val actionPlans: Flow<List<ActionPlan>> = database.actionPlanDao().getAllActions()
        .map { entities -> entities.map { it.toDomain() } }

    override val privacySettings: Flow<PrivacySettings> = preferencesRepository.privacySettingsFlow
    override val proactiveSettings: Flow<ProactiveSetting> = preferencesRepository.proactiveSettingsFlow
    override val explorationFactor: Flow<Float> = preferencesRepository.explorationFactorFlow
    override val activeCategoryFilter: Flow<String> = preferencesRepository.activeCategoryFilterFlow

    override suspend fun checkAndSeedInitialData() {
        if (database.memoryDao().count() == 0) {
            database.memoryDao().insertAll(SeedData.seedMemories.map { MemoryEntity.fromDomain(it) })
        }
        if (database.tasteDao().count() == 0) {
            database.tasteDao().insertAll(SeedData.seedTasteNodes.map { TasteNodeEntity.fromDomain(it) })
        }
        if (database.planDao().count() == 0) {
            database.planDao().insertAll(SeedData.seedPlans.map { PlanEntity.fromDomain(it) })
        }
        if (database.actionPlanDao().count() == 0) {
            database.actionPlanDao().insertAll(SeedData.seedActions.map { ActionPlanEntity.fromDomain(it) })
        }
    }

    override suspend fun addMemory(memory: MemoryItem) {
        database.memoryDao().insertMemory(MemoryEntity.fromDomain(memory))
    }

    override suspend fun updateMemory(memory: MemoryItem) {
        database.memoryDao().updateMemory(MemoryEntity.fromDomain(memory))
    }

    override suspend fun deleteMemory(id: String) {
        database.memoryDao().deleteMemoryById(id)
    }

    override suspend fun clearMemories() {
        database.memoryDao().clearAll()
    }

    override suspend fun saveTasteNode(node: TasteNode) {
        database.tasteDao().insertTasteNode(TasteNodeEntity.fromDomain(node))
    }

    override suspend fun clearTasteNodes() {
        database.tasteDao().clearAll()
    }

    override suspend fun insertPlan(plan: PlanItem) {
        database.planDao().insertPlan(PlanEntity.fromDomain(plan))
    }

    override suspend fun updatePlan(plan: PlanItem) {
        database.planDao().updatePlan(PlanEntity.fromDomain(plan))
    }

    override suspend fun deletePlan(id: String) {
        database.planDao().deleteById(id)
    }

    override suspend fun togglePlanSubItem(planId: String, subItemId: String) {
        // Handled via plan update
    }

    override suspend fun insertAction(action: ActionPlan) {
        database.actionPlanDao().insertAction(ActionPlanEntity.fromDomain(action))
    }

    override suspend fun updateAction(action: ActionPlan) {
        database.actionPlanDao().updateAction(ActionPlanEntity.fromDomain(action))
    }

    override suspend fun recordRecommendationFeedback(
        recommendationId: String,
        feedbackType: RecommendationFeedbackType
    ) {
        database.recommendationFeedbackDao().insertFeedback(
            RecommendationFeedbackEntity(
                recommendationId = recommendationId,
                feedback = feedbackType.name,
                timestamp = java.time.Instant.now().toString()
            )
        )
    }

    override suspend fun setPrivateSession(isPrivate: Boolean) {
        preferencesRepository.setPrivateSession(isPrivate)
    }

    override suspend fun setMaxAllowedSensitivity(level: SensitivityLevel) {
        preferencesRepository.setMaxAllowedSensitivity(level)
    }

    override suspend fun setExplorationFactor(factor: Float) {
        preferencesRepository.setExplorationFactor(factor)
    }

    override suspend fun setActiveCategoryFilter(filter: String) {
        preferencesRepository.setActiveCategoryFilter(filter)
    }

    override suspend fun updateProactiveSettings(settings: ProactiveSetting) {
        preferencesRepository.updateProactiveSettings(settings)
    }

    override suspend fun canSendProactivePing(settings: ProactiveSetting): Boolean {
        return preferencesRepository.canSendProactivePing(settings)
    }

    override suspend fun resetAllToSeed() {
        database.memoryDao().clearAll()
        database.tasteDao().clearAll()
        database.planDao().clearAll()
        database.actionPlanDao().clearAll()
        database.recommendationFeedbackDao().clearAll()
        preferencesRepository.resetPreferences()
        checkAndSeedInitialData()
    }
}
