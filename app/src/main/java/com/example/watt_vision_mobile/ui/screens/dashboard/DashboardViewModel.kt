package com.example.watt_vision_mobile.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.watt_vision_mobile.data.repository.DashboardRepository
import com.example.watt_vision_mobile.model.MedicaoDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val dashboardRepository = DashboardRepository(application)

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null
    private var realSerieTemporalData: List<MedicaoDto> = emptyList()

    init {
        startLiveUpdates()
    }

    fun startLiveUpdates(intervalMillis: Long = 3000L) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            var isFirstFetch = true
            while (isActive) {
                fetchDashboardData(showLoading = isFirstFetch)
                isFirstFetch = false
                delay(intervalMillis)
            }
        }
    }

    fun loadDashboardData() {
        startLiveUpdates()
    }

    private suspend fun fetchDashboardData(showLoading: Boolean) {
        if (showLoading) {
            _uiState.update { it.copy(isLoading = true) }
        }

        try {
            var cards = emptyList<StatCardData>()
            var alarms = emptyList<AlarmData>()
            var equipamentosList = emptyList<ComposicaoItem>()

            val alertasResult = dashboardRepository.getAlertasAtivos()
            val equipamentosResult = dashboardRepository.getEquipamentos()
            val ultimaMedicaoResult = dashboardRepository.getUltimaMedicao()
            val resumoResult = dashboardRepository.getResumoMedicoes()

            val ultimaMedicao = ultimaMedicaoResult.getOrNull() ?: resumoResult.getOrNull()

            // Identifica o PZEM (id_pzem do último registro ou padrão 1)
            val idPzem = ultimaMedicao?.id_pzem ?: 1

            // O back-end exige horas entre 1 e 168 (máximo 7 dias / 168h)
            val horasQuery = when (_uiState.value.selectedChartPeriod) {
                "1h" -> 1
                "6h" -> 6
                "24h" -> 24
                "7d" -> 168
                "30d" -> 168
                else -> 24
            }

            val serieResult = dashboardRepository.getSerieTemporal(idPzem = idPzem, horas = horasQuery)
            if (serieResult.isSuccess) {
                realSerieTemporalData = serieResult.getOrNull() ?: emptyList()
            }

            val hasRealData = alertasResult.isSuccess || equipamentosResult.isSuccess || ultimaMedicao != null || realSerieTemporalData.isNotEmpty()

            if (hasRealData) {
                val apiAlertas = alertasResult.getOrNull() ?: emptyList()
                val apiEquipamentos = equipamentosResult.getOrNull() ?: emptyList()

                if (apiAlertas.isNotEmpty()) {
                    alarms = apiAlertas.map { alt ->
                        AlarmData(
                            title = alt.mensagem ?: alt.tipo ?: "Alerta",
                            equipment = "Equipamento ID: ${alt.id_equipamento ?: "N/D"}",
                            time = alt.data_hora ?: "Recentemente",
                            type = when (alt.severidade?.lowercase()) {
                                "critico", "critical" -> AlarmType.CRITICAL
                                "atencao", "warning" -> AlarmType.WARNING
                                else -> AlarmType.INFO
                            }
                        )
                    }
                }

                if (apiEquipamentos.isNotEmpty()) {
                    equipamentosList = apiEquipamentos.mapIndexed { index, eq ->
                        ComposicaoItem(
                            id = (eq.id ?: (index + 1)).toString(),
                            entidade = eq.nome ?: "Equipamento ${eq.id}",
                            valor = 0.0,
                            origem = eq.status ?: "Ativo"
                        )
                    }
                }

                val tensaoStr = ultimaMedicao?.tensao?.let { String.format(Locale.getDefault(), "%.1f V", it) } ?: "N/A"
                val correnteStr = ultimaMedicao?.corrente?.let { String.format(Locale.getDefault(), "%.2f A", it) } ?: "N/A"
                val potenciaStr = ultimaMedicao?.potencia_ativa?.let { String.format(Locale.getDefault(), "%.2f kW", it) } ?: "N/A"
                val consumoTotalStr = ultimaMedicao?.energia_acumulada?.let { String.format(Locale.getDefault(), "%.2f kWh", it) } ?: "N/A"

                cards = listOf(
                    StatCardData("Consumo Total", consumoTotalStr, "Energia acumulada", "Ao vivo", StatTone.BLUE, "cpu"),
                    StatCardData("Dispositivos Ativos", if (apiEquipamentos.isNotEmpty()) "${apiEquipamentos.size}" else "N/A", "Conectados", "Ao vivo", StatTone.GREEN, "check_circle"),
                    StatCardData("Potência Ativa", potenciaStr, "Potência instantânea", "Ao vivo", StatTone.YELLOW, "zap"),
                    StatCardData("Tensão Atual", tensaoStr, "Tensão da rede", "Ao vivo", StatTone.BLUE, "flash"),
                    StatCardData("Corrente Média", correnteStr, "Corrente instantânea", "Ao vivo", StatTone.GREEN, "activity"),
                    StatCardData("Frequência", ultimaMedicao?.frequencia?.let { String.format(Locale.getDefault(), "%.1f Hz", it) } ?: "60.0 Hz", "Frequência da rede", "Estável", StatTone.BLUE, "activity")
                )
            }

            if (cards.isEmpty()) {
                cards = listOf(
                    StatCardData("Consumo Total", "Sem dados", "Servidor offline", "N/A", StatTone.BLUE, "cpu"),
                    StatCardData("Dispositivos Ativos", "0", "Sem conexão", "N/A", StatTone.RED, "wifi_off"),
                    StatCardData("Potência Ativa", "0 kW", "Sem conexão", "N/A", StatTone.YELLOW, "zap"),
                    StatCardData("Tensão Atual", "0 V", "Sem conexão", "N/A", StatTone.BLUE, "flash"),
                    StatCardData("Corrente Média", "0 A", "Sem conexão", "N/A", StatTone.GREEN, "activity"),
                    StatCardData("Frequência", "0 Hz", "Sem conexão", "N/A", StatTone.BLUE, "activity")
                )
            }

            val tensaoVal = ultimaMedicao?.tensao?.let { String.format(Locale.getDefault(), "%.1f V", it) } ?: "N/A"
            val correnteVal = ultimaMedicao?.corrente?.let { String.format(Locale.getDefault(), "%.2f A", it) } ?: "N/A"
            val potenciaVal = ultimaMedicao?.potencia_ativa?.let { String.format(Locale.getDefault(), "%.2f kW", it) } ?: "N/A"
            val frequenciaVal = ultimaMedicao?.frequencia?.let { String.format(Locale.getDefault(), "%.1f Hz", it) } ?: "N/A"
            val fpVal = ultimaMedicao?.fator_potencia?.let { String.format(Locale.getDefault(), "%.2f", it) } ?: "N/A"
            val energiaVal = ultimaMedicao?.energia_acumulada?.let { String.format(Locale.getDefault(), "%.2f kWh", it) } ?: "N/A"

            val indicadores = listOf(
                EnergeticMetricData("Tensão Instantânea", tensaoVal, "Medição ao vivo", "flash"),
                EnergeticMetricData("Corrente Instantânea", correnteVal, "Medição ao vivo", "activity"),
                EnergeticMetricData("Potência Ativa", potenciaVal, "Medição ao vivo", "zap"),
                EnergeticMetricData("Energia Acumulada", energiaVal, "Sensor PZEM", "cpu"),
                EnergeticMetricData("Frequência", frequenciaVal, "Rede elétrica", "clock"),
                EnergeticMetricData("Fator de Potência", fpVal, "Eficiência energética", "trending_up")
            )

            val currentTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val pointsCount = realSerieTemporalData.size
            val statusText = if (ultimaMedicao != null || realSerieTemporalData.isNotEmpty()) {
                "Ao vivo às $currentTime (PZEM $idPzem: $pointsCount pts)"
            } else {
                "Sem resposta da API às $currentTime (Verifique 10.0.2.2:3000)"
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    summaryCards = cards,
                    alarms = alarms,
                    indicadores = indicadores,
                    composicaoItems = equipamentosList,
                    lastUpdatedText = statusText,
                    chartPoints = mapToChartPoints(it.selectedMetric)
                )
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(isLoading = false) }
        } finally {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onMetricSelected(metric: String) {
        _uiState.update {
            it.copy(
                selectedMetric = metric,
                chartPoints = mapToChartPoints(metric)
            )
        }
    }

    fun onChartPeriodSelected(period: String) {
        _uiState.update {
            it.copy(
                selectedChartPeriod = period,
                chartPoints = mapToChartPoints(it.selectedMetric)
            )
        }
        loadDashboardData()
    }

    fun setComposicaoModalVisible(visible: Boolean) {
        _uiState.update { it.copy(isComposicaoModalOpen = visible) }
    }

    fun onFilterChanged(key: String, value: String) {
        _uiState.update { state ->
            val updatedFilters = when (key) {
                "period" -> state.filters.copy(period = value)
                "location" -> state.filters.copy(location = value)
                "sector" -> state.filters.copy(sector = value)
                "equipment" -> state.filters.copy(equipment = value)
                else -> state.filters
            }
            state.copy(filters = updatedFilters)
        }
    }

    private fun mapToChartPoints(metric: String): List<MedicaoPoint> {
        if (realSerieTemporalData.isEmpty()) {
            return emptyList()
        }
        return realSerieTemporalData.mapNotNull { m ->
            val valFloat = when (metric.lowercase()) {
                "tensao" -> m.tensao?.toFloat()
                "corrente" -> m.corrente?.toFloat()
                "potencia" -> m.potencia_ativa?.toFloat()
                "consumo" -> m.energia_acumulada?.toFloat()
                else -> m.tensao?.toFloat() ?: m.potencia_ativa?.toFloat()
            }
            val rawTime = m.data_hora ?: ""
            val formattedTime = when {
                rawTime.contains("T") -> rawTime.substringAfter("T").take(5)
                rawTime.contains(" ") -> rawTime.substringAfter(" ").take(5)
                rawTime.length >= 5 -> rawTime.take(5)
                else -> "00:00"
            }
            if (valFloat != null) MedicaoPoint(formattedTime, valFloat) else null
        }
    }
}
