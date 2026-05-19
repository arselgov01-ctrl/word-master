package com.wordmaster.app.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode(val storageValue: String) {
    System("system"),
    Light("light"),
    Dark("dark");

    companion object {
        fun fromStorage(value: String?): ThemeMode = when (value) {
            Light.storageValue -> Light
            Dark.storageValue -> Dark
            else -> System
        }
    }
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.System,
    val answerCount: Int = DEFAULT_ANSWER_COUNT,
    val ttsSpeed: Float = DEFAULT_TTS_SPEED
) {
    companion object {
        const val DEFAULT_ANSWER_COUNT = 4
        const val DEFAULT_TTS_SPEED = 1.0f
        val ALLOWED_ANSWER_COUNTS = listOf(4, 6)
    }
}

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "wordmaster_settings")

class SettingsManager(context: Context) {

    private val dataStore = context.applicationContext.settingsDataStore

    val settings: Flow<AppSettings> = dataStore.data.map { prefs ->
        val rawCount = prefs[KEY_ANSWER_COUNT] ?: AppSettings.DEFAULT_ANSWER_COUNT
        val normalizedCount = if (rawCount in AppSettings.ALLOWED_ANSWER_COUNTS) {
            rawCount
        } else {
            AppSettings.DEFAULT_ANSWER_COUNT
        }
        val rawSpeed = prefs[KEY_TTS_SPEED] ?: AppSettings.DEFAULT_TTS_SPEED
        val normalizedSpeed = rawSpeed.coerceIn(MIN_TTS_SPEED, MAX_TTS_SPEED)
        AppSettings(
            themeMode = ThemeMode.fromStorage(prefs[KEY_THEME]),
            answerCount = normalizedCount,
            ttsSpeed = normalizedSpeed
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[KEY_THEME] = mode.storageValue
        }
    }

    suspend fun setAnswerCount(count: Int) {
        val safe = if (count in AppSettings.ALLOWED_ANSWER_COUNTS) count else AppSettings.DEFAULT_ANSWER_COUNT
        dataStore.edit { prefs ->
            prefs[KEY_ANSWER_COUNT] = safe
        }
    }

    suspend fun setTtsSpeed(speed: Float) {
        val safe = speed.coerceIn(MIN_TTS_SPEED, MAX_TTS_SPEED)
        dataStore.edit { prefs ->
            prefs[KEY_TTS_SPEED] = safe
        }
    }

    companion object {
        const val MIN_TTS_SPEED = 0.5f
        const val MAX_TTS_SPEED = 2.0f
        const val PRIVACY_POLICY_URL = "https://arselgov01-ctrl.github.io/word-master/"

        private val KEY_THEME = stringPreferencesKey("theme_mode")
        private val KEY_ANSWER_COUNT = intPreferencesKey("answer_count")
        private val KEY_TTS_SPEED = floatPreferencesKey("tts_speed")
    }
}
