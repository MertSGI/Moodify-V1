package com.example.moodify.model

data class ContextualDimensions(
    val valence: Float = 0.1f,
    val energy: Float = 0.35f,
    val stress: Float = 0.65f,
    val socialNeed: Float = 0.25f,
    val focusNeed: Float = 0.2f,
    val noveltyNeed: Float = 0.3f,
    val confidence: Float = 0.88f
)

data class ContextSnapshot(
    val id: String = "ctx_snapshot_01",
    val dimensions: ContextualDimensions = ContextualDimensions(),
    val primaryState: String = "LOW_BATTERY",
    val secondaryState: String = "Cognitive fatigue after 4-hour design review",
    val userIntent: String = "Decompress without having to make complicated decisions",
    val contextSource: String = "CONVERSATION_INFERRED",
    val timestamp: String = "2026-09-28T19:30:00Z",
    val timeOfDay: String = "EVENING",
    val dayOfWeek: String = "Friday",
    val weatherSummary: String = "62°F, overcast with gentle rain",
    val freeHoursRemainingToday: Float = 4.5f,
    val notes: String = "Mock calendar confirms no evening commitments past 5:30 PM."
)

data class UserProfile(
    val id: String = "usr_alex_chen_92",
    val name: String = "Alex Chen",
    val preferredName: String = "Alex",
    val role: String = "Product Designer",
    val timezone: String = "America/Chicago",
    val joinedDate: String = "2026-08-15"
)
