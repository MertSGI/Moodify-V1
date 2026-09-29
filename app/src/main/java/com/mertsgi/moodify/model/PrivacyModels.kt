package com.mertsgi.moodify.model

data class PrivacySettings(
    val isPrivateSession: Boolean = false,
    val maxAllowedSensitivity: SensitivityLevel = SensitivityLevel.PERSONAL,
    val allowProactiveSuggestions: Boolean = true,
    val allowExternalIntegrations: Boolean = true,
    val logAllDecisions: Boolean = true
)

data class FirewallDecision(
    val id: String,
    val timestamp: String,
    val taskId: String,
    val userRequestSnippet: String,
    val admittedMemoryKeys: List<String>,
    val blockedMemoryKeys: List<String>,
    val explanation: String
)
