package com.gravity.ourmoments.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gravity.ourmoments.core.constant.AppConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val authTokenKey = stringPreferencesKey(AppConstants.KEY_AUTH_TOKEN)
    private val userIdKey = stringPreferencesKey(AppConstants.KEY_USER_ID)
    private val usernameKey = stringPreferencesKey(AppConstants.KEY_USERNAME)
    private val nicknameKey = stringPreferencesKey(AppConstants.KEY_NICKNAME)

    val authToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[authTokenKey]
    }

    val userId: Flow<String?> = dataStore.data.map { preferences ->
        preferences[userIdKey]
    }

    val username: Flow<String?> = dataStore.data.map { preferences ->
        preferences[usernameKey]
    }

    val nickname: Flow<String?> = dataStore.data.map { preferences ->
        preferences[nicknameKey]
    }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { preferences ->
            preferences[authTokenKey] = token
        }
    }

    suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[userIdKey] = userId
        }
    }

    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[usernameKey] = username
        }
    }

    suspend fun saveNickname(nickname: String) {
        dataStore.edit { preferences ->
            preferences[nicknameKey] = nickname
        }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}