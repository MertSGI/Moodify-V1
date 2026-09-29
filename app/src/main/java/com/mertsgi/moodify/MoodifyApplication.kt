package com.mertsgi.moodify

import android.app.Application
import com.mertsgi.moodify.data.local.MoodifyDatabase
import com.mertsgi.moodify.data.local.datastore.UserPreferencesRepository
import com.mertsgi.moodify.data.repository.MoodifyRepository
import com.mertsgi.moodify.data.repository.MoodifyRepositoryImpl

class MoodifyApplication : Application() {

    lateinit var repository: MoodifyRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = MoodifyDatabase.getInstance(this)
        val preferencesRepository = UserPreferencesRepository(this)
        repository = MoodifyRepositoryImpl(database, preferencesRepository)
    }
}
