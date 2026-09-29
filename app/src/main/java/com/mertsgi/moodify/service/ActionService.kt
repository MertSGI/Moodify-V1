package com.mertsgi.moodify.service

import com.mertsgi.moodify.model.ActionPlan
import com.mertsgi.moodify.model.ActionStatus
import com.mertsgi.moodify.model.PlanItem

data class ActionExecutionResult(
    val executionStatus: ActionStatus,
    val resultSummary: String,
    val newPlanItem: PlanItem? = null
)

object ActionService {
    const val REAL_EXTERNAL_INTEGRATION_COUNT: Int = 0

    fun executeAction(action: ActionPlan): ActionExecutionResult {
        return when (action.targetProvider.uppercase()) {
            "CALENDAR" -> {
                val titleParam = action.parameters.find { it.name == "summary" }?.value ?: action.title
                val timeParam = action.parameters.find { it.name == "start" }?.value ?: "2026-10-01T09:55:00"

                ActionExecutionResult(
                    executionStatus = ActionStatus.MOCK_EXECUTION,
                    resultSummary = "Mock calendar action completed locally. No external Google Calendar event was created.",
                    newPlanItem = PlanItem(
                        id = "plan_${System.currentTimeMillis()}",
                        title = titleParam,
                        type = "CALENDAR_HOLD",
                        category = "Events",
                        notes = "Mock calendar hold recorded locally. External sync is false.",
                        status = "PENDING",
                        isCalendarSynced = false, // Strictly false: no external API exists
                        date = timeParam
                    )
                )
            }
            "GROCERY", "SHOPPING" -> {
                ActionExecutionResult(
                    executionStatus = ActionStatus.MOCK_EXECUTION,
                    resultSummary = "Mock grocery action simulated locally. No real external delivery or cart write occurred.",
                    newPlanItem = PlanItem(
                        id = "plan_${System.currentTimeMillis()}",
                        title = action.title,
                        type = "SHOPPING_LIST",
                        category = "Groceries",
                        notes = action.description,
                        status = "PENDING",
                        isCalendarSynced = false
                    )
                )
            }
            else -> {
                ActionExecutionResult(
                    executionStatus = ActionStatus.MOCK_EXECUTION,
                    resultSummary = "Mock simulated action completed locally for \"${action.title}\". No external provider call was made."
                )
            }
        }
    }
}
