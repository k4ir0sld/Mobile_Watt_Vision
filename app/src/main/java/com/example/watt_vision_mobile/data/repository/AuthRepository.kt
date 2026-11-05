package com.example.watt_vision_mobile.data.repository

import android.content.Context
import com.example.watt_vision_mobile.data.TokenManager
import com.example.watt_vision_mobile.data.api.ApiClient
import com.example.watt_vision_mobile.data.api.AuthApiService
import com.example.watt_vision_mobile.model.LoginRequest
import com.example.watt_vision_mobile.model.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(context: Context) {
    private val authApiService = ApiClient.createService<AuthApiService>(context)
    private val tokenManager = TokenManager(context)

    suspend fun login(email: String, senha: String): Result<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApiService.login(LoginRequest(email, senha))
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    if (loginResponse.sucesso) {
                        loginResponse.dados?.token?.let { token ->
                            tokenManager.saveToken(token)
                        }
                        Result.success(loginResponse)
                    } else {
                        Result.failure(Exception(loginResponse.mensagem ?: "Erro no login"))
                    }
                } else {
                    Result.failure(Exception("Falha na autenticação: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun logout() {
        tokenManager.clearToken()
    }

    fun isLoggedIn(): Boolean {
        return !tokenManager.getToken().isNullOrBlank()
    }
}
