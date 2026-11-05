package com.example.watt_vision_mobile.data.api

import com.example.watt_vision_mobile.model.AlertaDto
import com.example.watt_vision_mobile.model.ApiResponse
import com.example.watt_vision_mobile.model.ConsumoDto
import com.example.watt_vision_mobile.model.EquipamentoDto
import com.example.watt_vision_mobile.model.MedicaoDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WattVisionApiService {
    @GET("api/medicoes/resumo")
    suspend fun getResumoMedicoes(): Response<ApiResponse<MedicaoDto>>

    @GET("api/medicoes/ultima")
    suspend fun getUltimaMedicao(): Response<ApiResponse<MedicaoDto>>

    @GET("api/medicoes/serie-temporal")
    suspend fun getSerieTemporal(
        @Query("id_pzem") idPzem: Int,
        @Query("horas") horas: Int? = 24
    ): Response<ApiResponse<List<MedicaoDto>>>

    @GET("api/medicoes/consumo")
    suspend fun getConsumo(
        @Query("id_pzem") idPzem: Int,
        @Query("inicio") inicio: String,
        @Query("fim") fim: String
    ): Response<ApiResponse<ConsumoDto>>

    @GET("api/equipamentos")
    suspend fun getEquipamentos(): Response<ApiResponse<List<EquipamentoDto>>>

    @GET("api/alertas")
    suspend fun getAlertas(): Response<ApiResponse<List<AlertaDto>>>

    @GET("api/alertas/ativos")
    suspend fun getAlertasAtivos(): Response<ApiResponse<List<AlertaDto>>>
}
