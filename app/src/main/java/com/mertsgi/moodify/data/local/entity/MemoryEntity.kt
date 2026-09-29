package com.mertsgi.moodify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mertsgi.moodify.model.MemoryItem
import com.mertsgi.moodify.model.SensitivityLevel

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey val id: String,
    val category: String,
    val key: String,
    val value: String,
    val source: String,
    val sourceQuote: String?,
    val createdAt: String,
    val updatedAt: String,
    val confidence: Float,
    val sensitivity: String,
    val status: String,
    val userConfirmed: Boolean,
    val isImportant: Boolean,
    val allowedForPersonalization: Boolean,
    val allowedForExternalTools: Boolean,
    val reasoningForBelief: String
) {
    fun toDomain(): MemoryItem = MemoryItem(
        id = id,
        category = category,
        key = key,
        value = value,
        source = source,
        sourceQuote = sourceQuote,
        createdAt = createdAt,
        updatedAt = updatedAt,
        confidence = confidence,
        sensitivity = try { SensitivityLevel.valueOf(sensitivity) } catch (e: Exception) { SensitivityLevel.NORMAL },
        status = status,
        userConfirmed = userConfirmed,
        isImportant = isImportant,
        allowedForPersonalization = allowedForPersonalization,
        allowedForExternalTools = allowedForExternalTools,
        reasoningForBelief = reasoningForBelief
    )

    companion object {
        fun fromDomain(item: MemoryItem): MemoryEntity = MemoryEntity(
            id = item.id,
            category = item.category,
            key = item.key,
            value = item.value,
            source = item.source,
            sourceQuote = item.sourceQuote,
            createdAt = item.createdAt,
            updatedAt = item.updatedAt,
            confidence = item.confidence,
            sensitivity = item.sensitivity.name,
            status = item.status,
            userConfirmed = item.userConfirmed,
            isImportant = item.isImportant,
            allowedForPersonalization = item.allowedForPersonalization,
            allowedForExternalTools = item.allowedForExternalTools,
            reasoningForBelief = item.reasoningForBelief
        )
    }
}
