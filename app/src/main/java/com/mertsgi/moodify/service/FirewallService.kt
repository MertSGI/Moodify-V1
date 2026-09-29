package com.mertsgi.moodify.service

import com.mertsgi.moodify.model.FirewallDecision
import com.mertsgi.moodify.model.MemoryItem
import com.mertsgi.moodify.model.PrivacySettings
import com.mertsgi.moodify.model.SensitivityLevel

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
            val levelAllowed = when (settings.maxAllowedSensitivity) {
                SensitivityLevel.NORMAL -> m.sensitivity == SensitivityLevel.NORMAL
                SensitivityLevel.PERSONAL -> m.sensitivity == SensitivityLevel.NORMAL || m.sensitivity == SensitivityLevel.PERSONAL
                SensitivityLevel.SENSITIVE -> m.sensitivity != SensitivityLevel.HIGHLY_SENSITIVE
                SensitivityLevel.HIGHLY_SENSITIVE -> m.allowedForPersonalization // Must be explicitly permitted even if ceiling is HIGHLY_SENSITIVE
            }

            // Invariant: Highly Sensitive information is NEVER admitted unless explicitly permitted for the purpose
            val sensitiveSafe = if (m.sensitivity == SensitivityLevel.HIGHLY_SENSITIVE) {
                m.allowedForPersonalization && settings.maxAllowedSensitivity == SensitivityLevel.HIGHLY_SENSITIVE
            } else {
                true
            }

            val domainRelevant = when (targetDomain.uppercase()) {
                "RECOMMENDER" -> m.allowedForPersonalization
                "EXTERNAL_TOOL" -> m.allowedForExternalTools
                else -> true
            }

            if (levelAllowed && sensitiveSafe && domainRelevant && !settings.isPrivateSession) {
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
                "Admitted ${admitted.size} items matching sensitivity <= ${settings.maxAllowedSensitivity}; withheld ${blocked.size} items (including ${memories.count { it.sensitivity == SensitivityLevel.HIGHLY_SENSITIVE }} highly sensitive/unrelated records)."
            }
        )

        auditLogs.add(0, decision)
        if (auditLogs.size > 20) auditLogs.removeAt(auditLogs.lastIndex)

        return FirewallFilterResult(admitted, blocked, decision)
    }

    fun getAuditLogs(): List<FirewallDecision> = auditLogs.toList()
    fun clearLogs() { auditLogs.clear() }
}
