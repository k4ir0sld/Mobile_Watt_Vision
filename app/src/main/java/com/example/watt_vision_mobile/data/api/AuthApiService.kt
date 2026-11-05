package com.example.watt_vision_mobile.data.api

import com.example.watt_vision_mobile.model.ApiResponse
import com.example.watt_vision_mobile.model.LoginRequest
import com.example.watt_vision_mobile.model.LoginResponse
import com.example.watt_vision_mobile.model.UsuarioDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/auth/me")
    suspend fun getMe(): Response<ApiResponse<UsuarioDto>>
}
