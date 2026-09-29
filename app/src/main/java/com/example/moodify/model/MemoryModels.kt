package com.example.moodify.model

enum class SensitivityLevel {
    NORMAL,
    PERSONAL,
    STRICTLY_CONFIDENTIAL
}

data class MemoryItem(
    val id: String,
    val category: String,
    val key: String,
    val value: String,
    val source: String = "USER_STATED",
    val sourceQuote: String? = null,
    val createdAt: String = "2026-08-16T10:00:00Z",
    val updatedAt: String = "2026-08-16T10:00:00Z",
    val confidence: Float = 0.95f,
    val sensitivity: SensitivityLevel = SensitivityLevel.NORMAL,
    val status: String = "ACTIVE",
    val userConfirmed: Boolean = true,
    val isImportant: Boolean = false,
    val allowedForPersonalization: Boolean = true,
    val allowedForExternalTools: Boolean = true,
    val reasoningForBelief: String = "Stated directly in chat or settings."
)

data class CandidateMemory(
    val id: String,
    val category: String,
    val key: String,
    val value: String,
    val sourceQuote: String,
    val confidence: Float,
    val sensitivity: SensitivityLevel,
    val explanation: String
)
