package com.mertsgi.moodify.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.mertsgi.moodify.model.PrivacySettings
import com.mertsgi.moodify.model.ProactiveMode
import com.mertsgi.moodify.model.ProactiveSetting
import com.mertsgi.moodify.model.SensitivityLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalDate
import java.time.ZoneId

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "moodify_preferences")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val IS_PRIVATE_SESSION = booleanPreferencesKey("is_private_session")
        val MAX_ALLOWED_SENSITIVITY = stringPreferencesKey("max_allowed_sensitivity")
        val EXPLORATION_FACTOR = floatPreferencesKey("exploration_factor")
        val PROACTIVE_ENABLED = booleanPreferencesKey("proactive_enabled")
        val PROACTIVE_MODE = stringPreferencesKey("proactive_mode")
        val PROACTIVE_USER_MAX = intPreferencesKey("proactive_user_max")
        val PROACTIVE_PINGS_TODAY = intPreferencesKey("proactive_pings_today")
        val PROACTIVE_DATE = stringPreferencesKey("proactive_date")
        val ACTIVE_CATEGORY_FILTER = stringPreferencesKey("active_category_filter")
    }

    val privacySettingsFlow: Flow<PrivacySettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            val isPrivate = prefs[Keys.IS_PRIVATE_SESSION] ?: false
            val sensString = prefs[Keys.MAX_ALLOWED_SENSITIVITY] ?: SensitivityLevel.PERSONAL.name
            val sensitivity = try {
                SensitivityLevel.valueOf(sensString)
            } catch (e: Exception) {
                SensitivityLevel.PERSONAL
            }
            PrivacySettings(
                isPrivateSession = isPrivate,
                maxAllowedSensitivity = sensitivity
            )
        }

    val proactiveSettingsFlow: Flow<ProactiveSetting> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            val enabled = prefs[Keys.PROACTIVE_ENABLED] ?: true
            val modeStr = prefs[Keys.PROACTIVE_MODE] ?: ProactiveMode.BALANCED.name
            val mode = try { ProactiveMode.valueOf(modeStr) } catch (e: Exception) { ProactiveMode.BALANCED }
            val userMax = prefs[Keys.PROACTIVE_USER_MAX] ?: 3
            ProactiveSetting(
                enabled = enabled,
                mode = mode,
                userConfiguredMax = userMax
            )
        }

    val explorationFactorFlow: Flow<Float> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.EXPLORATION_FACTOR] ?: 0.35f }

    val activeCategoryFilterFlow: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs -> prefs[Keys.ACTIVE_CATEGORY_FILTER] ?: "ALL" }

    suspend fun setPrivateSession(isPrivate: Boolean) {
        context.dataStore.edit { it[Keys.IS_PRIVATE_SESSION] = isPrivate }
    }

    suspend fun setMaxAllowedSensitivity(level: SensitivityLevel) {
        context.dataStore.edit { it[Keys.MAX_ALLOWED_SENSITIVITY] = level.name }
    }

    suspend fun setExplorationFactor(factor: Float) {
        context.dataStore.edit { it[Keys.EXPLORATION_FACTOR] = factor }
    }

    suspend fun setActiveCategoryFilter(filter: String) {
        context.dataStore.edit { it[Keys.ACTIVE_CATEGORY_FILTER] = filter }
    }

    suspend fun updateProactiveSettings(setting: ProactiveSetting) {
        context.dataStore.edit {
            it[Keys.PROACTIVE_ENABLED] = setting.enabled
            it[Keys.PROACTIVE_MODE] = setting.mode.name
            it[Keys.PROACTIVE_USER_MAX] = setting.userConfiguredMax
        }
    }

    /**
     * Proactivity local-calendar-date authority check and record.
     * Uses device local calendar date (not UTC).
     */
    suspend fun canSendProactivePing(setting: ProactiveSetting): Boolean {
        if (!setting.enabled || setting.effectiveDailyMax <= 0) return false
        val todayLocal = LocalDate.now(ZoneId.systemDefault()).toString()
        var allowed = false
        context.dataStore.edit { prefs ->
            val storedDate = prefs[Keys.PROACTIVE_DATE] ?: ""
            val count = if (storedDate == todayLocal) prefs[Keys.PROACTIVE_PINGS_TODAY] ?: 0 else 0
            if (count < setting.effectiveDailyMax) {
                prefs[Keys.PROACTIVE_DATE] = todayLocal
                prefs[Keys.PROACTIVE_PINGS_TODAY] = count + 1
                allowed = true
            }
        }
        return allowed
    }

    suspend fun getPingsSentToday(): Int {
        val todayLocal = LocalDate.now(ZoneId.systemDefault()).toString()
        var count = 0
        context.dataStore.edit { prefs ->
            val storedDate = prefs[Keys.PROACTIVE_DATE] ?: ""
            count = if (storedDate == todayLocal) prefs[Keys.PROACTIVE_PINGS_TODAY] ?: 0 else 0
        }
        return count
    }

    suspend fun resetPreferences() {
        context.dataStore.edit { it.clear() }
    }
}
