package com.mertsgi.moodify.data.local.dao

import androidx.room.*
import com.mertsgi.moodify.data.local.entity.TasteNodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TasteDao {
    @Query("SELECT * FROM taste_nodes ORDER BY weight DESC")
    fun getAllTasteNodes(): Flow<List<TasteNodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasteNode(node: TasteNodeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(nodes: List<TasteNodeEntity>)

    @Query("DELETE FROM taste_nodes WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM taste_nodes")
    suspend fun clearAll()

    @Query("SELECT COUNT(*) FROM taste_nodes")
    suspend fun count(): Int
}
