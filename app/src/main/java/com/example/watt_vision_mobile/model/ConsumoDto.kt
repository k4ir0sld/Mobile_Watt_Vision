package com.example.watt_vision_mobile.model

data class ConsumoDto(
    val id_pzem: Int?,
    val inicio: String?,
    val fim: String?,
    val quantidade_medicoes: Int?,
    val primeira_medicao: String?,
    val ultima_medicao: String?,
    val consumo_kwh: Double?,
    val tarifa: TarifaDto?,
    val custo_estimado: Double?,
    val moeda: String?
)
