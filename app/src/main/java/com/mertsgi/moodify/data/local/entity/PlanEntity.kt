package com.mertsgi.moodify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mertsgi.moodify.model.PlanItem
import com.mertsgi.moodify.model.PlanSubItem

@Entity(tableName = "plans")
data class PlanEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String,
    val category: String,
    val notes: String,
    val status: String,
    val isCalendarSynced: Boolean = false,
    val serializedSubItems: String = "",
    val date: String? = null,
    val venueOrPlatform: String? = null,
    val associatedRecommendationId: String? = null
) {
    fun toDomain(): PlanItem {
        val subItems = if (serializedSubItems.isBlank()) {
            emptyList()
        } else {
            serializedSubItems.split(";;").mapNotNull { entry ->
                val parts = entry.split("::")
                if (parts.size >= 3) {
                    PlanSubItem(id = parts[0], title = parts[1], checked = parts[2].toBoolean())
                } else null
            }
        }
        return PlanItem(
            id = id,
            title = title,
            type = type,
            category = category,
            notes = notes,
            status = status,
            isCalendarSynced = isCalendarSynced,
            items = subItems,
            date = date,
            venueOrPlatform = venueOrPlatform,
            associatedRecommendationId = associatedRecommendationId
        )
    }

    companion object {
        fun fromDomain(plan: PlanItem): PlanEntity {
            val serialized = plan.items.joinToString(";;") { "${it.id}::${it.title}::${it.checked}" }
            return PlanEntity(
                id = plan.id,
                title = plan.title,
                type = plan.type,
                category = plan.category,
                notes = plan.notes,
                status = plan.status,
                isCalendarSynced = plan.isCalendarSynced,
                serializedSubItems = serialized,
                date = plan.date,
                venueOrPlatform = plan.venueOrPlatform,
                associatedRecommendationId = plan.associatedRecommendationId
            )
        }
    }
}
