package com.example.moodify.model

enum class ActionStatus {
    AWAITING_CONFIRMATION,
    EXECUTED,
    CANCELLED
}

data class ActionParameter(
    val name: String,
    val label: String,
    val value: String,
    val type: String
)

data class ActionPlan(
    val id: String,
    val title: String,
    val description: String,
    val targetProvider: String, // CALENDAR, SPOTIFY, GROCERY, etc.
    val actionName: String,
    val riskLevel: String = "EXTERNAL_WRITE",
    val requiresExplicitConfirmation: Boolean = true,
    val parameters: List<ActionParameter> = emptyList(),
    val status: ActionStatus = ActionStatus.AWAITING_CONFIRMATION,
    val createdAt: String = "2026-09-28T19:30:00Z",
    val executedAt: String? = null,
    val resultSummary: String? = null
)

data class PlanSubItem(
    val id: String,
    val title: String,
    val checked: Boolean = false
)

data class PlanItem(
    val id: String,
    val title: String,
    val type: String = "ACTIVITY", // WATCH_LATER, SHOPPING_LIST, ACTIVITY, CALENDAR_HOLD
    val category: String = "General",
    val notes: String = "",
    val status: String = "PENDING",
    val isCalendarSynced: Boolean = false,
    val items: List<PlanSubItem> = emptyList(),
    val date: String? = null,
    val venueOrPlatform: String? = null,
    val associatedRecommendationId: String? = null
)
