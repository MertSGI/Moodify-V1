package com.example.moodify.service

import com.example.moodify.model.ActionPlan
import com.example.moodify.model.ActionStatus
import com.example.moodify.model.PlanItem

data class ActionExecutionResult(
    val executionStatus: ActionStatus,
    val resultSummary: String,
    val newPlanItem: PlanItem? = null
)

object ActionService {
    fun executeAction(action: ActionPlan): ActionExecutionResult {
        return when (action.targetProvider.uppercase()) {
            "CALENDAR" -> {
                val titleParam = action.parameters.find { it.name == "summary" }?.value ?: action.title
                val timeParam = action.parameters.find { it.name == "start" }?.value ?: "2026-10-01T09:55:00"

                ActionExecutionResult(
                    executionStatus = ActionStatus.EXECUTED,
                    resultSummary = "Simulated Calendar event created for \"$titleParam\" at $timeParam.",
                    newPlanItem = PlanItem(
                        id = "plan_${System.currentTimeMillis()}",
                        title = titleParam,
                        type = "CALENDAR_HOLD",
                        category = "Events",
                        notes = "Simulated external write to Google Calendar fixture.",
                        status = "SCHEDULED",
                        isCalendarSynced = true,
                        date = timeParam
                    )
                )
            }
            "GROCERY", "SHOPPING" -> {
                ActionExecutionResult(
                    executionStatus = ActionStatus.EXECUTED,
                    resultSummary = "Snack bundle items dispatched to mock grocery queue.",
                    newPlanItem = PlanItem(
                        id = "plan_${System.currentTimeMillis()}",
                        title = action.title,
                        type = "SHOPPING_LIST",
                        category = "Groceries",
                        notes = action.description,
                        status = "PENDING"
                    )
                )
            }
            else -> {
                ActionExecutionResult(
                    executionStatus = ActionStatus.EXECUTED,
                    resultSummary = "Action \"${action.title}\" executed successfully in local environment."
                )
            }
        }
    }
}
