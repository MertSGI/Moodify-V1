package com.example.moodify.service

import com.example.moodify.model.FirewallDecision
import com.example.moodify.model.MemoryItem
import com.example.moodify.model.PrivacySettings
import com.example.moodify.model.SensitivityLevel

data class FirewallFilterResult(
    val admitted: List<MemoryItem>,
    val blocked: List<MemoryItem>,
    val decision: FirewallDecision
)

object FirewallService {
    private val auditLogs = mutableListOf<FirewallDecision>()

    fun filterContextForTask(
        taskDescription: String,
        targetDomain: String,
        memories: List<MemoryItem>,
        settings: PrivacySettings
    ): FirewallFilterResult {
        val admitted = mutableListOf<MemoryItem>()
        val blocked = mutableListOf<MemoryItem>()

        for (m in memories) {
            val levelCheck = when (settings.maxAllowedSensitivity) {
                SensitivityLevel.NORMAL -> m.sensitivity == SensitivityLevel.NORMAL
                SensitivityLevel.PERSONAL -> m.sensitivity != SensitivityLevel.STRICTLY_CONFIDENTIAL
                SensitivityLevel.STRICTLY_CONFIDENTIAL -> true
            }

            val domainRelevant = when (targetDomain.uppercase()) {
                "RECOMMENDER" -> m.allowedForPersonalization
                "EXTERNAL_TOOL" -> m.allowedForExternalTools
                else -> true
            }

            if (levelCheck && domainRelevant && !settings.isPrivateSession) {
                admitted.add(m)
            } else {
                blocked.add(m)
            }
        }

        val decision = FirewallDecision(
            id = "fw_dec_${System.currentTimeMillis()}",
            timestamp = java.time.Instant.now().toString(),
            taskId = "fw_task_${System.currentTimeMillis() % 10000}",
            userRequestSnippet = taskDescription.take(60),
            admittedMemoryKeys = admitted.map { it.key },
            blockedMemoryKeys = blocked.map { it.key },
            explanation = if (settings.isPrivateSession) {
                "Private session active: All durable context withheld from inference."
            } else {
                "Admitted ${admitted.size} items matching sensitivity <= ${settings.maxAllowedSensitivity}; withheld ${blocked.size} unrelated/confidential items."
            }
        )

        auditLogs.add(0, decision)
        if (auditLogs.size > 20) auditLogs.removeAt(auditLogs.lastIndex)

        return FirewallFilterResult(admitted, blocked, decision)
    }

    fun getAuditLogs(): List<FirewallDecision> = auditLogs.toList()
}
