package live.yurii.sudoku.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token_prefs")

@Singleton
class TokenPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val TOKEN = stringPreferencesKey("jwt_token")
        val USERNAME = stringPreferencesKey("username")
        val PLAYER_ID = stringPreferencesKey("player_id")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.TOKEN] = token
        }
    }

    suspend fun saveUserInfo(username: String, playerId: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USERNAME] = username
            preferences[Keys.PLAYER_ID] = playerId
        }
    }

    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.TOKEN]
        }
    }

    fun getUsername(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.USERNAME]
        }
    }

    fun getPlayerId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[Keys.PLAYER_ID]
        }
    }

    suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
