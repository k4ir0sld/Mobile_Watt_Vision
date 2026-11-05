package com.example.watt_vision_mobile.model

import com.google.gson.annotations.SerializedName

data class MedicaoDto(
    @SerializedName(value = "id_medicao", alternate = ["id"])
    val id_medicao: Int? = null,
    
    val id_pzem: Int? = null,
    val id_ponto_medicao: Int? = null,
    val data_hora: String? = null,
    
    @SerializedName(value = "tensao", alternate = ["v", "voltage"])
    val tensaoRaw: Any? = null,
    
    @SerializedName(value = "corrente", alternate = ["c", "current"])
    val correnteRaw: Any? = null,
    
    @SerializedName(value = "potencia_ativa", alternate = ["potencia", "power", "p"])
    val potenciaAtivaRaw: Any? = null,
    
    @SerializedName(value = "energia_acumulada", alternate = ["energia", "consumo", "e"])
    val energiaAcumuladaRaw: Any? = null,
    
    @SerializedName(value = "frequencia")
    val frequenciaRaw: Any? = null,
    
    @SerializedName(value = "fator_potencia")
    val fatorPotenciaRaw: Any? = null
) {
    private fun parseToDouble(value: Any?): Double? {
        return when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
    }

    val tensao: Double?
        get() = parseToDouble(tensaoRaw)

    val corrente: Double?
        get() = parseToDouble(correnteRaw)

    val potencia_ativa: Double?
        get() = parseToDouble(potenciaAtivaRaw)

    val energia_acumulada: Double?
        get() = parseToDouble(energiaAcumuladaRaw)

    val frequencia: Double?
        get() = parseToDouble(frequenciaRaw)

    val fator_potencia: Double?
        get() = parseToDouble(fatorPotenciaRaw)
}
