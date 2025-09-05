package ag.android.moviewatchlist

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session_pref")

object SessionKey {
    val SESSION_ID = stringPreferencesKey("session_id")
}

class SessionManager(private val context: Context) {
    private val dataStore = context.sessionDataStore

    suspend fun saveSessionId(sessionId: String) {
        dataStore.edit { prefs ->
            prefs[SessionKey.SESSION_ID] = sessionId
        }
    }

    val sessionIdFlow: Flow<String?> = dataStore.data.map {
        prefs -> prefs[SessionKey.SESSION_ID]
    }
}