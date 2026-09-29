package com.mertsgi.moodify.model

enum class ProactiveMode(val safetyCap: Int) {
    QUIET(safetyCap = 0),
    BALANCED(safetyCap = 3),
    COMPANION(safetyCap = 5)
}

data class ProactiveSetting(
    val enabled: Boolean = true,
    val mode: ProactiveMode = ProactiveMode.BALANCED,
    val userConfiguredMax: Int = 3,
    val quietHoursStart: String = "22:00",
    val quietHoursEnd: String = "08:00",
    val respectLowBatteryState: Boolean = true
) {
    val effectiveDailyMax: Int
        get() = if (!enabled) 0 else minOf(userConfiguredMax, mode.safetyCap)
}

data class ProactiveRuntimeState(
    val lastPingLocalDate: String, // Device LOCAL calendar date (YYYY-MM-DD)
    val pingsSentToday: Int = 0
)
