package com.example.moodify.model

data class IntegrationProvider(
    val id: String,
    val name: String,
    val category: String,
    val description: String,
    val iconName: String,
    val isConnected: Boolean = true,
    val permissionLevel: String = "READ_ONLY",
    val lastSynced: String = "10 minutes ago"
)
