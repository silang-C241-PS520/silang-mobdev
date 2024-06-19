package com.example.silang_mobdev.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {

    fun getUser(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                email = preferences[EMAIL_KEY] ?: "",
                token = preferences[TOKEN_KEY] ?: "",
                isLogin = preferences[IS_LOGIN_KEY] ?: false,
                tokenExpirationTime = preferences[TOKEN_EXPIRATION_KEY] ?: 0L
            )
        }
    }

    suspend fun saveSession(user: UserModel) {
        val expirationTime = System.currentTimeMillis() + TOKEN_VALIDITY_DURATION
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = true
            preferences[TOKEN_EXPIRATION_KEY] = expirationTime // Save the token expiration time
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            val currentTime = System.currentTimeMillis()
            val expirationTime = preferences[TOKEN_EXPIRATION_KEY] ?: 0L
            val isExpired = currentTime > expirationTime

            UserModel(
                preferences[EMAIL_KEY] ?: "",
                preferences[TOKEN_KEY]?.takeIf { !isExpired } ?: "",
                preferences[IS_LOGIN_KEY] ?: false,
                expirationTime
            ).also {
                if (isExpired) {
                    logout()
                }
            }
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val TOKEN_EXPIRATION_KEY = longPreferencesKey("token_expiration")
        private const val TOKEN_VALIDITY_DURATION = 3600000L // Token validity duration in milliseconds (e.g., 1 minute)

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
