package com.example.boofilms

import android.content.Context
import android.net.Uri
import javax.inject.Inject

interface AuthManager {
    fun register(username: String, password: String): Boolean
    fun login(username: String, password: String): Boolean
    fun logout()
    fun isLoggedIn(): Boolean
    fun getCurrentUser(): String?
    fun getCurrentUserId(): String
    fun getUserData(userId: String): UserData?
    fun updateUserDescription(userId: String, description: String)
    fun updateUserAvatar(userId: String, avatarUri: String)

    data class UserData(
        val userId: String,
        val username: String,
        val description: String,
        val avatarUri: String?
    )
}

class SharedPrefsAuthManager(private val context: Context) : AuthManager {
    private val dbHelper = AuthDatabaseHelper(context)
    private val authPrefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val userDataPrefs = context.getSharedPreferences("user_data_prefs", Context.MODE_PRIVATE)

    override fun register(username: String, password: String): Boolean {
        return if (!dbHelper.isUsernameExists(username)) {
            val userId = dbHelper.addUser(username, password)
            if (userId > 0) {
                // Сохраняем данные пользователя
                saveUserData(
                    userId.toString(),
                    username,
                    "",
                    null
                )
                // Автоматически входим после регистрации
                authPrefs.edit().apply {
                    putString("current_user", username)
                    putString("current_user_id", userId.toString())
                    putBoolean("is_logged_in", true) // Добавляем флаг авторизации
                }.apply()
                true
            } else {
                false
            }
        } else {
            false
        }
    }

    override fun login(username: String, password: String): Boolean {
        return if (dbHelper.checkUser(username, password)) {
            val userId = dbHelper.getUserId(username)
            authPrefs.edit().apply {
                putString("current_user", username)
                putString("current_user_id", userId.toString())
                putBoolean("is_logged_in", true)
            }.apply()
            true
        } else {
            false
        }
    }

    override fun logout() {
        authPrefs.edit().apply {
            remove("current_user")
            remove("current_user_id")
            putBoolean("is_logged_in", false) // Явно указываем выход
        }.apply()
    }

    override fun isLoggedIn(): Boolean {
        return authPrefs.getBoolean("is_logged_in", false) &&
                authPrefs.getString("current_user_id", null) != null
    }

    override fun getCurrentUser(): String? {
        return if (isLoggedIn()) {
            authPrefs.getString("current_user", null) ?: "Гость"
        } else {
            "Гость"
        }
    }

    override fun getCurrentUserId(): String {
        return if (isLoggedIn()) {
            authPrefs.getString("current_user_id", "") ?: ""
        } else {
            ""
        }
    }

    override fun getUserData(userId: String): AuthManager.UserData? {
        return if (userDataPrefs.contains("user_${userId}_name")) {
            AuthManager.UserData(
                userId = userId,
                username = userDataPrefs.getString("user_${userId}_name", "") ?: "",
                description = userDataPrefs.getString("user_${userId}_desc", "") ?: "",
                avatarUri = userDataPrefs.getString("user_${userId}_avatar", null)
            )
        } else {
            null
        }
    }

    override fun updateUserDescription(userId: String, description: String) {
        userDataPrefs.edit()
            .putString("user_${userId}_desc", description)
            .apply()
    }

    override fun updateUserAvatar(userId: String, avatarUri: String) {
        userDataPrefs.edit()
            .putString("user_${userId}_avatar", avatarUri)
            .apply()
    }

    private fun saveUserData(
        userId: String,
        username: String,
        description: String,
        avatarUri: String?
    ) {
        userDataPrefs.edit()
            .putString("user_${userId}_name", username)
            .putString("user_${userId}_desc", description)
            .putString("user_${userId}_avatar", avatarUri)
            .apply()
    }
}

class AuthManagerImpl @Inject constructor(
    private val context: Context
) : AuthManager by SharedPrefsAuthManager(context)