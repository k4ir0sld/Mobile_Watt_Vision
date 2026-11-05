package com.example.watt_vision_mobile.data

import android.content.Context
import android.content.SharedPreferences

/**
 * TokenManager responsible for persisting and retrieving the JWT token.
 * NOTE: Uses standard Android SharedPreferences (unencrypted storage).
 */
class TokenManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("watt_vision_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"
    }
}
