package com.example.watt_vision_mobile.model

data class LoginResponse(
    val sucesso: Boolean,
    val mensagem: String?,
    val dados: AuthData?
)

data class AuthData(
    val token: String?,
    val tipo: String?,
    val usuario: UsuarioDto?
)
