package com.hocel.chirrup.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.hocel.chirrup.utils.ChatModels
import com.hocel.chirrup.utils.Constants.TEMPERATURE
import com.hocel.chirrup.utils.Constants.CHAT_MODEL
import com.hocel.chirrup.utils.Constants.DATA_NAME
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(DATA_NAME)

@ViewModelScoped
class DataStoreRepository @Inject constructor(@ApplicationContext private val context: Context) {

    private object PreferenceKeys {
        val model = stringPreferencesKey(CHAT_MODEL)
        val temperature = floatPreferencesKey(TEMPERATURE)
    }

    private val dataStore: DataStore<Preferences> = context.dataStore

    suspend fun saveChatGptData(
        model: String,
        temperature: Float,
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.model] = model
            preferences[PreferenceKeys.temperature] = temperature
        }
    }

    val readChatGptData: Flow<ChatGptData> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val model = preferences[PreferenceKeys.model] ?: ChatModels.GPT35Turbo.model
            val temperature = preferences[PreferenceKeys.temperature] ?: 1f
            ChatGptData(
                model,
                temperature,
            )
        }
}

data class ChatGptData(
    val model: String,
    val temperature: Float,
)