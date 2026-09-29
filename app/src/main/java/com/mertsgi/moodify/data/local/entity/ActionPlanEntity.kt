package com.mertsgi.moodify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mertsgi.moodify.model.ActionParameter
import com.mertsgi.moodify.model.ActionPlan
import com.mertsgi.moodify.model.ActionStatus

@Entity(tableName = "action_plans")
data class ActionPlanEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val targetProvider: String,
    val actionName: String,
    val riskLevel: String,
    val requiresExplicitConfirmation: Boolean,
    val serializedParameters: String = "",
    val status: String,
    val createdAt: String,
    val executedAt: String?,
    val resultSummary: String?
) {
    fun toDomain(): ActionPlan {
        val params = if (serializedParameters.isBlank()) emptyList() else {
            serializedParameters.split(";;").mapNotNull { entry ->
                val p = entry.split("::")
                if (p.size >= 4) ActionParameter(name = p[0], label = p[1], value = p[2], type = p[3]) else null
            }
        }
        return ActionPlan(
            id = id,
            title = title,
            description = description,
            targetProvider = targetProvider,
            actionName = actionName,
            riskLevel = riskLevel,
            requiresExplicitConfirmation = requiresExplicitConfirmation,
            parameters = params,
            status = try { ActionStatus.valueOf(status) } catch (e: Exception) { ActionStatus.AWAITING_CONFIRMATION },
            createdAt = createdAt,
            executedAt = executedAt,
            resultSummary = resultSummary
        )
    }

    companion object {
        fun fromDomain(action: ActionPlan): ActionPlanEntity {
            val paramsStr = action.parameters.joinToString(";;") { "${it.name}::${it.label}::${it.value}::${it.type}" }
            return ActionPlanEntity(
                id = action.id,
                title = action.title,
                description = action.description,
                targetProvider = action.targetProvider,
                actionName = action.actionName,
                riskLevel = action.riskLevel,
                requiresExplicitConfirmation = action.requiresExplicitConfirmation,
                serializedParameters = paramsStr,
                status = action.status.name,
                createdAt = action.createdAt,
                executedAt = action.executedAt,
                resultSummary = action.resultSummary
            )
        }
    }
}
