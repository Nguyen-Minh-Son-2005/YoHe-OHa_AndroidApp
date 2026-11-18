package com.nguyenminhson.yoheoha.data.session

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("yohe_oha_session", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NICKNAME = "user_nickname"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUser(id: String, email: String, nickname: String) {
        prefs.edit()
            .putString(KEY_USER_ID, id)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NICKNAME, nickname)
            .apply()
    }

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserNickname(): String? = prefs.getString(KEY_USER_NICKNAME, "Bạn")
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun isLoggedIn(): Boolean {
        return !getAuthToken().isNullOrBlank()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }

    fun getAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val token = getAuthToken()
            val requestBuilder = original.newBuilder()
            if (!token.isNullOrBlank()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
            chain.proceed(requestBuilder.build())
        }
    }
}
