package com.example.moodify.service

import com.example.moodify.model.ContextSnapshot
import com.example.moodify.model.ContextualDimensions

object ContextEngine {
    fun setSelfReportedMood(
        current: ContextSnapshot,
        newDimensions: ContextualDimensions,
        newState: String? = null
    ): ContextSnapshot {
        val calculatedState = newState ?: when {
            newDimensions.energy < 0.35f && newDimensions.stress > 0.5f -> "LOW_BATTERY"
            newDimensions.focusNeed > 0.7f -> "DEEP_FOCUS"
            newDimensions.socialNeed > 0.6f -> "SOCIAL_RECHARGE"
            newDimensions.stress > 0.7f -> "ELEVATED_STRESS"
            else -> current.primaryState
        }

        return current.copy(
            dimensions = newDimensions,
            primaryState = calculatedState,
            contextSource = "USER_DIRECT_UPDATE",
            timestamp = java.time.Instant.now().toString()
        )
    }

    fun inferContextFromText(current: ContextSnapshot, text: String): ContextSnapshot {
        val lower = text.lowercase()
        var dims = current.dimensions

        val (newDims, state, intent) = when {
            lower.contains("awful") || lower.contains("exhausted") || lower.contains("don't really want to think") || lower.contains("tired") -> {
                Triple(
                    dims.copy(energy = (dims.energy - 0.2f).coerceAtLeast(0.1f), stress = (dims.stress + 0.15f).coerceAtMost(0.95f), focusNeed = 0.1f),
                    "LOW_BATTERY",
                    "Decompress with minimal cognitive load"
                )
            }
            lower.contains("friday") || lower.contains("weekend") || lower.contains("concert") || lower.contains("celebrate") -> {
                Triple(
                    dims.copy(socialNeed = 0.8f, energy = (dims.energy + 0.25f).coerceAtMost(0.9f), valence = 0.6f),
                    "WEEKEND_EXPLORATION",
                    "Explore intimate live events or dining"
                )
            }
            lower.contains("snack") || lower.contains("junk") || lower.contains("budget") -> {
                Triple(
                    dims.copy(focusNeed = 0.5f),
                    current.primaryState,
                    "Discover high-protein savory snacks under budget"
                )
            }
            lower.contains("marcus") || lower.contains("review") -> {
                Triple(
                    dims.copy(stress = (dims.stress - 0.2f).coerceAtLeast(0.3f), valence = 0.5f),
                    "POST_MILESTONE_UNWIND",
                    "Reflect on design review and transition to rest"
                )
            }
            else -> Triple(dims, current.primaryState, current.userIntent)
        }

        return current.copy(
            dimensions = newDims,
            primaryState = state,
            userIntent = intent,
            contextSource = "CONVERSATION_INFERRED",
            timestamp = java.time.Instant.now().toString()
        )
    }
}
