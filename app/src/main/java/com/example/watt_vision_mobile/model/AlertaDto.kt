package com.example.watt_vision_mobile.model

data class AlertaDto(
    val id: Int?,
    val id_equipamento: Int?,
    val tipo: String?,
    val mensagem: String?,
    val severidade: String?,
    val data_hora: String?,
    val resolvido: Boolean?
)
