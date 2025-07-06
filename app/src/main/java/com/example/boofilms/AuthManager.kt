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
                saveUserData(
                    userId.toString(),
                    username,
                    "",
                    null
                )
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
            authPrefs.edit()
                .putString("current_user", username)
                .putString("current_user_id", userId.toString())
                .apply()
            true
        } else {
            false
        }
    }

    override fun logout() {
        authPrefs.edit()
            .remove("current_user")
            .remove("current_user_id")
            .apply()
    }

    override fun isLoggedIn(): Boolean {
        return authPrefs.getString("current_user_id", null) != null
    }

    override fun getCurrentUser(): String? {
        return authPrefs.getString("current_user", null)
    }

    override fun getCurrentUserId(): String {
        return authPrefs.getString("current_user_id", "") ?: ""
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