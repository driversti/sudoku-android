package live.yurii.sudoku.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import live.yurii.sudoku.domain.model.Theme
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val THEME = stringPreferencesKey("theme")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
    }

    val preferences: Flow<UserSettingsData> =
        context.userPreferencesDataStore.data.map { preferences ->
            UserSettingsData(
                theme = Theme.fromString(preferences[Keys.THEME] ?: Theme.LIGHT.name),
                soundEnabled = preferences[Keys.SOUND_ENABLED] ?: true,
                hapticEnabled = preferences[Keys.HAPTIC_ENABLED] ?: true
            )
        }

    suspend fun setTheme(theme: Theme) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.THEME] = theme.name
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.SOUND_ENABLED] = enabled
        }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.HAPTIC_ENABLED] = enabled
        }
    }
}

data class UserSettingsData(
    val theme: Theme = Theme.LIGHT,
    val soundEnabled: Boolean = true,
    val hapticEnabled: Boolean = true
)
