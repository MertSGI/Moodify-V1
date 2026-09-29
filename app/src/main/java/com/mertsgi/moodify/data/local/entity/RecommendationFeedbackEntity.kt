package com.mertsgi.moodify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recommendation_feedback")
data class RecommendationFeedbackEntity(
    @PrimaryKey val recommendationId: String,
    val feedback: String,
    val timestamp: String
)
