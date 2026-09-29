package com.mertsgi.moodify.data.local.dao

import androidx.room.*
import com.mertsgi.moodify.data.local.entity.RecommendationFeedbackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecommendationFeedbackDao {
    @Query("SELECT * FROM recommendation_feedback")
    fun getAllFeedback(): Flow<List<RecommendationFeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: RecommendationFeedbackEntity)

    @Query("DELETE FROM recommendation_feedback")
    suspend fun clearAll()
}
