package com.example.moodify.model

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

data class ProactiveSetting(
    val enabled: Boolean = true,
    val maxPingsPerDay: Int = 2,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "08:00",
    val respectLowBatteryState: Boolean = true
)
