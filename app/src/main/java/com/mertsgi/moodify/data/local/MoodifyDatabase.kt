package com.mertsgi.moodify.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mertsgi.moodify.data.local.dao.*
import com.mertsgi.moodify.data.local.entity.*

@Database(
    entities = [
        MemoryEntity::class,
        TasteNodeEntity::class,
        PlanEntity::class,
        ActionPlanEntity::class,
        RecommendationFeedbackEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MoodifyDatabase : RoomDatabase() {
    abstract fun memoryDao(): MemoryDao
    abstract fun tasteDao(): TasteDao
    abstract fun planDao(): PlanDao
    abstract fun actionPlanDao(): ActionPlanDao
    abstract fun recommendationFeedbackDao(): RecommendationFeedbackDao

    companion object {
        @Volatile
        private var INSTANCE: MoodifyDatabase? = null

        fun getInstance(context: Context): MoodifyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MoodifyDatabase::class.java,
                    "moodify_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
