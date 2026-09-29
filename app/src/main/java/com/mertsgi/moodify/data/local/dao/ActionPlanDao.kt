package com.mertsgi.moodify.data.local.dao

import androidx.room.*
import com.mertsgi.moodify.data.local.entity.ActionPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionPlanDao {
    @Query("SELECT * FROM action_plans ORDER BY createdAt DESC")
    fun getAllActions(): Flow<List<ActionPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: ActionPlanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(actions: List<ActionPlanEntity>)

    @Update
    suspend fun updateAction(action: ActionPlanEntity)

    @Query("DELETE FROM action_plans")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM action_plans")
    suspend fun count(): Int
}
