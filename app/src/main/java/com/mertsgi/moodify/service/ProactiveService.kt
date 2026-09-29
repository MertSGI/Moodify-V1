package com.mertsgi.moodify.service

import com.mertsgi.moodify.model.ContextSnapshot
import com.mertsgi.moodify.model.ProactiveMode
import com.mertsgi.moodify.model.ProactiveSetting
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

object ProactiveService {

    /**
     * Determines whether a proactive prompt is permitted right now.
     * Evaluates:
     * 1. Enabled flag
     * 2. Effective daily cap: min(userConfiguredMax, mode.safetyCap)
     * 3. Current count for device LOCAL CALENDAR DATE
     * 4. Quiet hours boundary
     * 5. Low battery state suppression (if respectLowBatteryState is true)
     */
    fun shouldTriggerIntervention(
        setting: ProactiveSetting,
        pingsSentToday: Int,
        context: ContextSnapshot,
        currentTime: LocalTime = LocalTime.now(ZoneId.systemDefault())
    ): Boolean {
        if (!setting.enabled) return false

        val effectiveMax = setting.effectiveDailyMax
        if (effectiveMax <= 0 || pingsSentToday >= effectiveMax) {
            return false
        }

        // Check low battery suppression
        if (setting.respectLowBatteryState && context.primaryState == "LOW_BATTERY" && context.dimensions.energy < 0.2f) {
            return false
        }

        // Check quiet hours
        if (isQuietHours(currentTime, setting.quietHoursStart, setting.quietHoursEnd)) {
            return false
        }

        return true
    }

    fun isQuietHours(
        now: LocalTime,
        quietStartStr: String,
        quietEndStr: String
    ): Boolean {
        return try {
            val start = LocalTime.parse(quietStartStr)
            val end = LocalTime.parse(quietEndStr)
            if (start.isBefore(end)) {
                now.isAfter(start) && now.isBefore(end)
            } else {
                // Crosses midnight (e.g. 22:00 to 08:00)
                now.isAfter(start) || now.isBefore(end)
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Authority for daily boundary: Android device local calendar date.
     */
    fun currentLocalDate(): String {
        return LocalDate.now(ZoneId.systemDefault()).toString()
    }
}
