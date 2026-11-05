package com.example.watt_vision_mobile.model

data class ApiResponse<T>(
    val sucesso: Boolean,
    val mensagem: String?,
    val dados: T?
)
