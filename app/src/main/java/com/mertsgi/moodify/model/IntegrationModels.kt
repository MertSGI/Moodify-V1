package com.mertsgi.moodify.model

enum class IntegrationStatus {
    MOCK,
    NOT_CONNECTED,
    LIVE
}

data class IntegrationProvider(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val iconName: String,
    val status: IntegrationStatus = IntegrationStatus.MOCK,
    val permissionLevel: String = "READ_ONLY",
    val lastSynced: String? = null // Truthful: null or explicit mock notice
) {
    val syncDisplayText: String
        get() = when (status) {
            IntegrationStatus.MOCK -> "Mock fixture — no live sync"
            IntegrationStatus.NOT_CONNECTED -> "Not connected"
            IntegrationStatus.LIVE -> lastSynced ?: "Connected"
        }
}
